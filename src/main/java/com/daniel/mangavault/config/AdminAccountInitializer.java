package com.daniel.mangavault.config;

import com.daniel.mangavault.entity.User;
import com.daniel.mangavault.enums.Role;
import com.daniel.mangavault.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

// Creates the bootstrap ADMIN account on first startup so the admin UI is
// reachable without manual SQL. Runs only when no ADMIN user exists yet.
@Slf4j
@Configuration
@RequiredArgsConstructor
public class AdminAccountInitializer {

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.email}")
    private String adminEmail;

    @org.springframework.context.annotation.Bean
    CommandLineRunner initAdminAccount(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.existsByRole(Role.ADMIN)) {
                return;
            }
            userRepository.save(User.builder()
                    .username(adminUsername)
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .role(Role.ADMIN)
                    .build());
            log.warn("Bootstrap ADMIN account '{}' created — change its password outside local dev.", adminUsername);
        };
    }
}
