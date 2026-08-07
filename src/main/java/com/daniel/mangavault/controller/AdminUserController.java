package com.daniel.mangavault.controller;

import com.daniel.mangavault.dto.response.AdminUserResponse;
import com.daniel.mangavault.dto.response.ApiResponse;
import com.daniel.mangavault.dto.response.PageResponse;
import com.daniel.mangavault.enums.Role;
import com.daniel.mangavault.enums.UserStatus;
import com.daniel.mangavault.service.AdminUserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    private final AdminUserService adminUserService;

    @GetMapping
    public ApiResponse<PageResponse<AdminUserResponse>> getUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<AdminUserResponse>>builder()
                .code(1000)
                .result(adminUserService.getUsers(keyword, role, status, page, size))
                .build();
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<AdminUserResponse> updateStatus(@PathVariable String id,
                                                       @RequestBody StatusRequest request) {
        return ApiResponse.<AdminUserResponse>builder()
                .code(1000)
                .result(adminUserService.updateStatus(id, request.getStatus()))
                .build();
    }

    @PatchMapping("/{id}/role")
    public ApiResponse<AdminUserResponse> updateRole(@PathVariable String id,
                                                     @RequestBody RoleRequest request) {
        return ApiResponse.<AdminUserResponse>builder()
                .code(1000)
                .result(adminUserService.updateRole(id, request.getRole()))
                .build();
    }

    @Getter
    @Setter
    public static class StatusRequest {
        private UserStatus status;
    }

    @Getter
    @Setter
    public static class RoleRequest {
        private Role role;
    }
}
