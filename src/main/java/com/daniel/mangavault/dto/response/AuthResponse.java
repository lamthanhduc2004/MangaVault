package com.daniel.mangavault.dto.response;

import com.daniel.mangavault.enums.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {
    private String token;
    private String username;
    private Role role;
}
