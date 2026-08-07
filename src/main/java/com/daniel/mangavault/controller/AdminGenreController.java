package com.daniel.mangavault.controller;

import com.daniel.mangavault.dto.request.GenreRequest;
import com.daniel.mangavault.dto.response.ApiResponse;
import com.daniel.mangavault.dto.response.GenreResponse;
import com.daniel.mangavault.service.GenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/genres")
@RequiredArgsConstructor
public class AdminGenreController {
    private final GenreService genreService;

    @GetMapping
    public ApiResponse<List<GenreResponse>> getGenres() {
        return ApiResponse.<List<GenreResponse>>builder()
                .code(1000)
                .result(genreService.getGenres())
                .build();
    }

    @PostMapping
    public ApiResponse<GenreResponse> createGenre(@Valid @RequestBody GenreRequest request) {
        return ApiResponse.<GenreResponse>builder()
                .code(1000)
                .result(genreService.createGenre(request))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<GenreResponse> updateGenre(@PathVariable String id, @Valid @RequestBody GenreRequest request) {
        return ApiResponse.<GenreResponse>builder()
                .code(1000)
                .result(genreService.updateGenre(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteGenre(@PathVariable String id) {
        genreService.deleteGenre(id);
        return ApiResponse.<Void>builder().code(1000).message("Đã xóa thể loại").build();
    }
}
