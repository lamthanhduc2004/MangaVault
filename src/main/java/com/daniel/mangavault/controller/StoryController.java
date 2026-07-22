package com.daniel.mangavault.controller;

import com.daniel.mangavault.dto.response.ApiResponse;
import com.daniel.mangavault.dto.response.ChapterSummaryResponse;
import com.daniel.mangavault.dto.response.PageResponse;
import com.daniel.mangavault.dto.response.StoryResponse;
import com.daniel.mangavault.service.ChapterService;
import com.daniel.mangavault.service.StoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stories")
@RequiredArgsConstructor
public class StoryController {
    private final StoryService storyService;
    private final ChapterService chapterService;

    @GetMapping
    public ApiResponse<PageResponse<StoryResponse>> getStories(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ApiResponse.<PageResponse<StoryResponse>>builder()
                .code(1000)
                .result(storyService.getStories(keyword, page, size))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<StoryResponse> getStoryById(@PathVariable String id){
        return ApiResponse.<StoryResponse>builder()
                .code(1000)
                .result(storyService.getStoryById(id))
                .build();
    }

    @GetMapping("/{id}/chapters")
    public ApiResponse<List<ChapterSummaryResponse>> getChaptersOfStory(@PathVariable String id){
        return ApiResponse.<List<ChapterSummaryResponse>>builder()
                .code(1000)
                .result(chapterService.getChaptersOfStory(id))
                .build();
    }
}
