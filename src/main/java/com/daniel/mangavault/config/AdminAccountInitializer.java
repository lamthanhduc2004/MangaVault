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

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    private static final String DEFAULT_ADMIN_PASSWORD = "admin123";
    private static final String DEFAULT_JWT_SECRET = "manga-vault-dev-secret-key-change-me-0123456789";

    @org.springframework.context.annotation.Bean
    CommandLineRunner initAdminAccount(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // These defaults are published in the repository — anyone could sign an
            // ADMIN token or log in. Loud enough to notice in a deployment log.
            if (DEFAULT_JWT_SECRET.equals(jwtSecret)) {
                log.error("JWT_SECRET is still the public development default. Set a random secret (>= 32 chars).");
            }
            if (DEFAULT_ADMIN_PASSWORD.equals(adminPassword)) {
                log.error("ADMIN_PASSWORD is still the public development default. Set ADMIN_PASSWORD.");
            }

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
