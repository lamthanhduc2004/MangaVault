package com.daniel.mangavault.service;

import com.daniel.mangavault.dto.response.AdminUserResponse;
import com.daniel.mangavault.dto.response.PageResponse;
import com.daniel.mangavault.entity.User;
import com.daniel.mangavault.enums.Role;
import com.daniel.mangavault.enums.UserStatus;
import com.daniel.mangavault.exception.AppException;
import com.daniel.mangavault.exception.ErrorCode;
import com.daniel.mangavault.repository.UserRepository;
import com.daniel.mangavault.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/** Account administration (F19). */
@Service
@RequiredArgsConstructor
public class AdminUserService {
    private final UserRepository userRepository;
    private final CurrentUserProvider currentUserProvider;

    public PageResponse<AdminUserResponse> getUsers(String keyword, Role role, UserStatus status,
                                                    int page, int size) {
        String kw = StringUtils.hasText(keyword) ? keyword.trim() : "";
        Page<User> users = userRepository.search(kw, role, status,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));

        return PageResponse.from(users, AdminUserService::mapToResponse);
    }

    public AdminUserResponse updateStatus(String userId, UserStatus status) {
        User user = requireOtherUser(userId);
        user.setStatus(status);
        return mapToResponse(userRepository.save(user));
    }

    public AdminUserResponse updateRole(String userId, Role role) {
        User user = requireOtherUser(userId);
        user.setRole(role);
        return mapToResponse(userRepository.save(user));
    }

    /**
     * Blocks self-modification: an admin who demotes or bans their own account
     * would lock themselves out with no way back in through the UI.
     */
    private User requireOtherUser(String userId) {
        if (userId.equals(currentUserProvider.requireUserId())) {
            throw new AppException(ErrorCode.CANNOT_MODIFY_SELF);
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private static AdminUserResponse mapToResponse(User user) {
        return AdminUserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
