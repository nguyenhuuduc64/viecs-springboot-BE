package com.config;


import com.entity.Role;
import com.entity.User;
import com.repository.RoleRepository;
import com.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class ApplicationInitConfig {
    private final RoleRepository roleRepository;

    @Bean
    public ApplicationRunner applicationRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()){
                Role adminRole = roleRepository.findById("admin")
                        .orElseGet(() -> roleRepository.save(
                                Role.builder()
                                        .name("admin")
                                        .description("admin")
                                        .build()
                        ));



                User user = User.builder()
                        .username("admin")
                        .email("admin@gmail.com")
                        .password(passwordEncoder.encode("admin"))
                        .roles(adminRole)
                        .build();

                userRepository.save(user);

            }

        };
    }


}
