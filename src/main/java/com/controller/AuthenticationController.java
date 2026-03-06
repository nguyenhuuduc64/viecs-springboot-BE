package com.controller;


import com.dto.request.*;
import com.dto.response.AuthenticationResponse;
import com.dto.response.IntrospectResponse;
import com.dto.response.RefreshResponse;
import com.nimbusds.jose.JOSEException;
import com.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService  authenticationService;

    @PostMapping("/log-in")
    ApiResponse <AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest){
        AuthenticationResponse result = authenticationService.authenticate(authenticationRequest);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(AuthenticationResponse.builder()
                        .isAuthenticated(result.isAuthenticated())
                        .token(result.getToken())
                        .build())
                .build();
    }

    @PostMapping("/log-in/google")
    public ApiResponse <AuthenticationResponse> authenticateGoogle(@RequestBody Map<String, String> request){
        var code = request.get("code");
        System.out.print("dang nhap bang goolr");
        AuthenticationResponse result = authenticationService.authenticateGoogle(code);
        System.out.println("ket qua tra ve o controller" + result);
        return ApiResponse.<AuthenticationResponse>builder()
                .code(200)
                .message("success")
                .result(result)
                .build();
    }

    @PostMapping("/log-out")
    ApiResponse<Object> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        return authenticationService.logout(request);
    }
    @PostMapping("/introspect")
    ApiResponse <IntrospectResponse> authenticate(@RequestBody IntrospectRequest introspectRequest){
        System.out.println(introspectRequest.toString());
        IntrospectResponse result = authenticationService.introspect(introspectRequest);

        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/refresh")
    ApiResponse <RefreshResponse> refresh(@RequestBody RefreshRequest request) throws ParseException, JOSEException {
        RefreshResponse result = authenticationService.refreshToken(request);

        return ApiResponse.<RefreshResponse>builder()
                .code(200)
                .message("Refresh token successfully")
                .result(result)
                .build();
    }
}
