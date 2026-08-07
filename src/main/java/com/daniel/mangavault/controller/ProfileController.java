package com.daniel.mangavault.controller;

import com.daniel.mangavault.dto.request.PasswordChangeRequest;
import com.daniel.mangavault.dto.request.ProfileUpdateRequest;
import com.daniel.mangavault.dto.response.ApiResponse;
import com.daniel.mangavault.dto.response.UserProfileResponse;
import com.daniel.mangavault.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping
    public ApiResponse<UserProfileResponse> getProfile() {
        return ApiResponse.<UserProfileResponse>builder()
                .code(1000)
                .result(profileService.getCurrentProfile())
                .build();
    }

    @PutMapping
    public ApiResponse<UserProfileResponse> updateProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        return ApiResponse.<UserProfileResponse>builder()
                .code(1000)
                .result(profileService.updateProfile(request))
                .build();
    }

    @PutMapping("/password")
    public ApiResponse<Void> changePassword(@Valid @RequestBody PasswordChangeRequest request) {
        profileService.changePassword(request);
        return ApiResponse.<Void>builder().code(1000).message("Đổi mật khẩu thành công").build();
    }
}
