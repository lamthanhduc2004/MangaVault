package com.daniel.mangavault.controller;

import com.daniel.mangavault.dto.response.ApiResponse;
import com.daniel.mangavault.dto.response.GenreResponse;
import com.daniel.mangavault.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    /** Not paginated: the genre list is small and used to populate filters. */
    @GetMapping
    public ApiResponse<List<GenreResponse>> getGenres() {
        return ApiResponse.<List<GenreResponse>>builder()
                .code(1000)
                .result(genreService.getGenres())
                .build();
    }
}
