package com.service;

import com.application.TokenVerifier;
import com.client.OutboundIdentityClient;
import com.client.OutboundUserClient;
import com.dto.request.*;
import com.dto.response.*;
import com.entity.InvalidatedToken;
import com.entity.User;
import com.exception.AppException;
import com.exception.ErrorCode;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.repository.InvalidatedTokenRepository;
import com.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthenticationService {
    UserRepository userRepository;
    InvalidatedTokenRepository  invalidatedTokenRepository;
    private final OutboundUserClient outboundUserClient;
    private final OutboundIdentityClient outboundIdentityClient;
    @NonFinal
    @Value("${outbound.identity.client-id}")
    String CLIENT_ID;

    @NonFinal
    @Value("${outbound.identity.client-secret}")
    String CLIENT_SECRET;

    //@NonFinal
    //@Value("${outbound.identity.redirect-uri}")
    String REDIRECT_URI = "postmessage";

    //đánh dấu không inject vào contructor
    @NonFinal
    protected static final String SIGNER_KEY = "ZYWT4y/G+dF+z31xQSIMo1rrCaEiuBwpbCmnewCGO4E=";

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
        System.out.println("user email" + authenticationRequest.getEmail());
        var user = userRepository.findByEmail(authenticationRequest.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        boolean authenticated =  passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword());

        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String token = this.generateToken(user);
        String refreshToken = this.generateToken(user);

        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);   // Quan trọng: Chặn JavaScript truy cập
        cookie.setSecure(false);    // Để true nếu dùng HTTPS
        cookie.setPath("/");        // Áp dụng cho toàn bộ domain
        cookie.setMaxAge(7 * 24 * 60 * 60); // Sống trong 7 ngày

        assert response != null;
        response.addCookie(cookie);
        boolean isAuthenticated = true;
        return new AuthenticationResponse(token, isAuthenticated);
    }


    //ham xu ly login voi google
    public AuthenticationResponse authenticateGoogle(String code) {
        // Bước A: Đổi Code lấy AccessToken của Google
        ExchangeTokenResponse response = outboundIdentityClient.exchangeToken(ExchangeTokenRequest.builder()
                .code(code)
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .redirectUri(REDIRECT_URI)
                .grantType("authorization_code")
                .build());

        // Bước B: Dùng AccessToken đó để lấy thông tin User từ Google
        OutboundUserResponse userInfo = outboundUserClient.getUserInfo("json", response.getAccessToken());

        // Bước C: Tìm user trong DB, nếu không có thì đăng ký mới (Social Login)
        var user = userRepository.findByEmail(userInfo.getEmail())
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(userInfo.getEmail())
                        .username(userInfo.getEmail()) // Dùng email làm username
                        .password("") // Password trống vì login qua Google
                        .fullName(userInfo.getFamilyName())
                        .build()));
        System.out.print("thay user trong DB" + user);
        // Bước D: Tạo JWT của riêng hệ thống mình trả về cho Frontend
        String token = generateToken(user);
        System.out.println("token" + token);
        return AuthenticationResponse.builder()
                .token(token)
                .isAuthenticated(true)
                .build();
    }


    public ApiResponse<Object> logout(LogoutRequest request) throws ParseException, JOSEException {
        var signedJwt = verifyToken(request.getToken());

        String jti = signedJwt.getJWTClaimsSet().getJWTID();

        Date expiry = signedJwt.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jti)
                .expiryTime(expiry)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);
        return ApiResponse.builder()
                .code(200)
                .message("logout successfully")
                .build();
    }

    public IntrospectResponse introspect(IntrospectRequest request) {
        System.out.println(request.getToken());
        String token = request.getToken();
        try {
            SignedJWT signJwt = verifyToken(token);

            return IntrospectResponse.builder()
                    .valid(true)
                    .build();


        } catch (JOSEException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateToken (User user){
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256); //256 cho 32 bit va 512 cho 64 bit
        System.out.println("tao token");
        //các thành phần trong 1 jwt
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer(user.getUsername())
                .issueTime(new Date())
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .expirationTime(new Date(
                        new Date().getTime() + 3600 * 1000
                ))

                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        System.out.println("co payload " + payload);
        JWSObject jwsObject = new JWSObject(jwsHeader,payload);
        try{
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return  jwsObject.serialize();
        } catch (JOSEException e){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }
    public SignedJWT verifyToken(String token) throws JOSEException, ParseException {


            //xem token có do chúng ta cung cấp không
            JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

            //parse token để lấy dữ liệu
            SignedJWT signedJWT = SignedJWT.parse(token);

            Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

            boolean verified = signedJWT.verify(verifier);

            if (!verified | expiryTime.before(new Date())) throw new AppException(ErrorCode.TOKEN_EXPIRED);
            log.info(signedJWT.getJWTClaimsSet().getJWTID());
            if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            // kiem tra token co nam trong bang invalidatedToken hay khong
            return signedJWT;

    }


    public RefreshResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        var signJWT = verifyToken(request.getToken());
        var jit = signJWT.getJWTClaimsSet().getJWTID();
        var expireTime = signJWT.getJWTClaimsSet().getExpirationTime();
        //vo hieu hoa token cu
        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .expiryTime(expireTime)
                .id(jit)
                .build();
        invalidatedTokenRepository.save(invalidatedToken);

        //tao token moi
        //lay thong tin user
        var userName = signJWT.getJWTClaimsSet().getSubject();
        var user = userRepository.findByUsername(userName).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
        //tao token moi
        var token = generateToken(user);

        return RefreshResponse.builder()
                .token(token)
                .isAuthenticated(true)
                .build();
    }

    private String buildScope(User user){
        StringJoiner scopes =  new StringJoiner(" ");
            scopes.add((CharSequence) user.getRoles().getName());
        return scopes.toString();

    }


}
