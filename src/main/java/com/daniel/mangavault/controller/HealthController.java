package com.daniel.mangavault.controller;

import com.daniel.mangavault.dto.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @GetMapping("/api/health")
    public ApiResponse<String> health(){
        return ApiResponse.<String>builder()
                .code(1000)
                .result("Manga Vault API is running")
                .build();

    }
}
