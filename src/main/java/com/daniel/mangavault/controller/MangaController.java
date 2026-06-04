package com.daniel.mangavault.controller;

import com.daniel.mangavault.dto.request.MangaCreationRequest;
import com.daniel.mangavault.dto.response.ApiResponse;
import com.daniel.mangavault.dto.response.MangaResponse;
import com.daniel.mangavault.service.MangaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/mangas")
@RequiredArgsConstructor
public class MangaController {
    private final MangaService mangaService;

    @PostMapping
    public ApiResponse<MangaResponse> createManga(@RequestBody MangaCreationRequest request){
        return ApiResponse.<MangaResponse>builder()
                .code(1000)
                .result(mangaService.createManga(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<MangaResponse>> getAllMangas() {
        return ApiResponse.<List<MangaResponse>>builder()
                .code(1000)
                .result(mangaService.getAllMangas())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<MangaResponse> getMangaById(@PathVariable String id){
        return ApiResponse.<MangaResponse>builder()
                .code(1000)
                .result(mangaService.getMangaById(id))
                .build();
    }

}
