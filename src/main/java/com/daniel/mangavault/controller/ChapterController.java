package com.daniel.mangavault.controller;

import com.daniel.mangavault.dto.response.ApiResponse;
import com.daniel.mangavault.dto.response.ChapterResponse;
import com.daniel.mangavault.service.ChapterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chapters")
@RequiredArgsConstructor
public class ChapterController {
    private final ChapterService chapterService;

    @GetMapping("/{id}")
    public ApiResponse<ChapterResponse> getChapterById(@PathVariable String id){
        return ApiResponse.<ChapterResponse>builder()
                .code(1000)
                .result(chapterService.getChapterById(id))
                .build();
    }
}
