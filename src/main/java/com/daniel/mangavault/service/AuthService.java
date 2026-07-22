package com.daniel.mangavault.service;

import com.daniel.mangavault.dto.request.LoginRequest;
import com.daniel.mangavault.dto.request.RegisterRequest;
import com.daniel.mangavault.dto.response.AuthResponse;
import com.daniel.mangavault.dto.response.UserResponse;
import com.daniel.mangavault.entity.User;
import com.daniel.mangavault.enums.Role;
import com.daniel.mangavault.exception.AppException;
import com.daniel.mangavault.exception.ErrorCode;
import com.daniel.mangavault.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    @Value("${app.jwt.expiration-seconds}")
    private long expirationSeconds;

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER) // self-registration never grants ADMIN
                .build();

        User saved = userRepository.save(user);

        return UserResponse.builder()
                .id(saved.getId())
                .username(saved.getUsername())
                .email(saved.getEmail())
                .role(saved.getRole())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                // Same error for unknown username and wrong password —
                // do not reveal which one failed.
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        return AuthResponse.builder()
                .token(generateToken(user))
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

    private String generateToken(User user) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("manga-vault")
                .subject(user.getUsername())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expirationSeconds))
                .claim("uid", user.getId())
                .claim("role", user.getRole().name())
                .build();
        // Signing with a symmetric secret requires naming the MAC algorithm explicitly.
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
