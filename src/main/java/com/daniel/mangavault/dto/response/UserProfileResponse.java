package com.daniel.mangavault.dto.response;

import com.daniel.mangavault.enums.Role;
import com.daniel.mangavault.enums.UserStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/** The signed-in user's own profile. Never contains the password hash. */
@Getter
@Builder
public class UserProfileResponse {
    private String id;
    private String username;
    private String email;
    private String displayName;
    private String avatarUrl;
    private Role role;
    private UserStatus status;
    private LocalDateTime createdAt;
}
