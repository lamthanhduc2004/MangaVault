package com.daniel.mangavault.controller;

import com.daniel.mangavault.dto.request.LoginRequest;
import com.daniel.mangavault.dto.request.RegisterRequest;
import com.daniel.mangavault.dto.response.ApiResponse;
import com.daniel.mangavault.dto.response.AuthResponse;
import com.daniel.mangavault.dto.response.UserResponse;
import com.daniel.mangavault.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .result(authService.register(request))
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.<AuthResponse>builder()
                .code(1000)
                .result(authService.login(request))
                .build();
    }
}
