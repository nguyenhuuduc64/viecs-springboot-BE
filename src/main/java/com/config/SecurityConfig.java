package com.config; // hoặc package phù hợp với dự án của bạn

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    @Lazy
    private final CustomJwtDecoder customJwtDecoder;
    private final String[] PUBLIC_ENDPOINT = {

            "/auth/token",
            "/auth/introspect",
            "/auth/log-in",
            "/auth/log-out",
            "/auth/refresh",
            "/users",
            "/auth/log-in/google",
            "/ai/analyze-tech",

    };
    private final String[] PUBLIC_GET_ENDPOINT = {
            "/cv-components",
    };
    private final String[] PUBLIC_POST_ENDPOINT = {
            "/cv-components",
    };


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        //cho pheps tất cả các end point /users đều truy cập được
        httpSecurity
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("http://localhost:5173", "https://openedx.id.vn"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                .authorizeHttpRequests(request ->
                    request
                            //tat ca endpoint nam trong public voi method POST thi public
                            .requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINT).permitAll()
                            .requestMatchers(HttpMethod.GET, PUBLIC_ENDPOINT).permitAll()
                            .requestMatchers(HttpMethod.GET, PUBLIC_GET_ENDPOINT).permitAll()
                            .requestMatchers(HttpMethod.POST, PUBLIC_POST_ENDPOINT).permitAll()

                            //.requestMatchers(HttpMethod.GET, "/users/**").hasAnyAuthority("SCOPE_ADMIN")
                            .anyRequest().authenticated()
                    )
                .oauth2ResourceServer(request ->
                    request
                            .jwt(jwtConfigurer -> jwtConfigurer.decoder(customJwtDecoder))
                    );
                //xu ly logout
                /*.logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                        }
                )*/

        //tắt cross origin
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        return httpSecurity.build();
    }


}
