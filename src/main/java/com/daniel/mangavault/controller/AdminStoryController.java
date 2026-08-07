package com.daniel.mangavault.controller;

import com.daniel.mangavault.dto.request.ChapterCreationRequest;
import com.daniel.mangavault.dto.request.StoryCreationRequest;
import com.daniel.mangavault.dto.request.StoryUpdateRequest;
import com.daniel.mangavault.dto.response.ApiResponse;
import com.daniel.mangavault.dto.response.ChapterSummaryResponse;
import com.daniel.mangavault.dto.response.PageResponse;
import com.daniel.mangavault.dto.response.StoryResponse;
import com.daniel.mangavault.enums.StoryStatus;
import com.daniel.mangavault.enums.Visibility;
import com.daniel.mangavault.service.ChapterService;
import com.daniel.mangavault.service.StoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// URLs live under /api/admin/** from day one so wiring Spring Security later
// (ADMIN role on /api/admin/**) will not require moving endpoints.
@RestController
@RequestMapping("/api/admin/stories")
@RequiredArgsConstructor
public class AdminStoryController {
    private final StoryService storyService;
    private final ChapterService chapterService;

    // The public catalogue hides PRIVATE stories, so admin needs its own listing
    // or hidden stories would become unmanageable from the UI.
    @GetMapping
    public ApiResponse<PageResponse<StoryResponse>> getStories(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) StoryStatus status,
            @RequestParam(required = false) Visibility visibility,
            @RequestParam(required = false) String genre,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<StoryResponse>>builder()
                .code(1000)
                .result(storyService.getStoriesForAdmin(keyword, status, visibility, genre, sort, page, size))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<StoryResponse> getStory(@PathVariable String id) {
        return ApiResponse.<StoryResponse>builder()
                .code(1000)
                .result(storyService.getStoryByIdForAdmin(id))
                .build();
    }

    @GetMapping("/{id}/chapters")
    public ApiResponse<List<ChapterSummaryResponse>> getChapters(@PathVariable String id) {
        return ApiResponse.<List<ChapterSummaryResponse>>builder()
                .code(1000)
                .result(chapterService.getChaptersOfStoryForAdmin(id))
                .build();
    }

    @PostMapping
    public ApiResponse<StoryResponse> createStory(@Valid @RequestBody StoryCreationRequest request) {
        return ApiResponse.<StoryResponse>builder()
                .code(1000)
                .result(storyService.createStory(request))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<StoryResponse> updateStory(@PathVariable String id,
                                                  @Valid @RequestBody StoryUpdateRequest request) {
        return ApiResponse.<StoryResponse>builder()
                .code(1000)
                .result(storyService.updateStory(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteStory(@PathVariable String id) {
        storyService.deleteStory(id);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Story deleted")
                .build();
    }

    @PostMapping("/{id}/chapters")
    public ApiResponse<ChapterSummaryResponse> createChapter(@PathVariable String id,
                                                             @Valid @RequestBody ChapterCreationRequest request) {
        return ApiResponse.<ChapterSummaryResponse>builder()
                .code(1000)
                .result(chapterService.createChapter(id, request))
                .build();
    }
}
