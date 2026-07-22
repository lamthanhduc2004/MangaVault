package com.daniel.mangavault.dto.response;

import com.daniel.mangavault.enums.Role;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponse {
    private String id;
    private String username;
    private String email;
    private Role role;
    private LocalDateTime createdAt;
}
