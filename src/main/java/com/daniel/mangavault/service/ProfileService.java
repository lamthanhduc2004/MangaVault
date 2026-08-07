package com.daniel.mangavault.service;

import com.daniel.mangavault.dto.request.PasswordChangeRequest;
import com.daniel.mangavault.dto.request.ProfileUpdateRequest;
import com.daniel.mangavault.dto.response.UserProfileResponse;
import com.daniel.mangavault.entity.User;
import com.daniel.mangavault.exception.AppException;
import com.daniel.mangavault.exception.ErrorCode;
import com.daniel.mangavault.repository.UserRepository;
import com.daniel.mangavault.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/** Self-service profile management (F10). */
@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserProvider currentUserProvider;

    public UserProfileResponse getCurrentProfile() {
        return mapToProfile(currentUserProvider.requireUser());
    }

    public UserProfileResponse updateProfile(ProfileUpdateRequest request) {
        User user = currentUserProvider.requireUser();

        // Blank input clears the field rather than storing an empty string.
        user.setDisplayName(StringUtils.hasText(request.getDisplayName()) ? request.getDisplayName().trim() : null);
        user.setAvatarUrl(StringUtils.hasText(request.getAvatarUrl()) ? request.getAvatarUrl().trim() : null);

        return mapToProfile(userRepository.save(user));
    }

    public void changePassword(PasswordChangeRequest request) {
        User user = currentUserProvider.requireUser();

        // Proving knowledge of the current password stops a hijacked session from
        // locking the real owner out.
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CURRENT_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private UserProfileResponse mapToProfile(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
