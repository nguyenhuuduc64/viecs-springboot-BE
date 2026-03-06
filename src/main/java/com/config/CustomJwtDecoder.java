package com.config;

import com.exception.AppException;
import com.exception.ErrorCode;
import com.nimbusds.jwt.SignedJWT;
import com.repository.InvalidatedTokenRepository;
import com.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Objects;
@Component
@RequiredArgsConstructor
public class CustomJwtDecoder implements JwtDecoder {
    @Value("${jwt.signerKey}")
    private String jwtSignerKey;

    // KHÔNG tiêm AuthenticationService nữa để ngắt vòng lặp
    // Tiêm trực tiếp Repository để check Blacklist
    private final InvalidatedTokenRepository invalidatedTokenRepository;

    private NimbusJwtDecoder nimbusJwtDecoder = null;

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            String jit = signedJWT.getJWTClaimsSet().getJWTID();

            if (invalidatedTokenRepository.existsById(jit)) {
                throw new JwtException("Token invalidated");
            }

            if (Objects.isNull(nimbusJwtDecoder)) {
                SecretKeySpec secretKeySpec = new SecretKeySpec(jwtSignerKey.getBytes(), "HS256");
                nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                        .macAlgorithm(MacAlgorithm.HS256)
                        .build();
            }

            return nimbusJwtDecoder.decode(token);
        } catch (Exception e) {
            // Luôn trả về JwtException ở đây để Spring Security xử lý Context
            throw new JwtException("Invalid token: " + e.getMessage());
        }
    }
}