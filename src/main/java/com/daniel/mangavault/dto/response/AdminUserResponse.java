package com.daniel.mangavault.dto.response;

import com.daniel.mangavault.enums.Role;
import com.daniel.mangavault.enums.UserStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/** User row in the admin console (F19). Never exposes the password hash. */
@Getter
@Builder
public class AdminUserResponse {
    private String id;
    private String username;
    private String email;
    private String displayName;
    private Role role;
    private UserStatus status;
    private LocalDateTime createdAt;
}
