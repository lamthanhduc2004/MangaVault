package com.daniel.mangavault.controller;

import com.daniel.mangavault.dto.request.ReadingProgressRequest;
import com.daniel.mangavault.dto.response.ApiResponse;
import com.daniel.mangavault.dto.response.FollowedStoryResponse;
import com.daniel.mangavault.dto.response.PageResponse;
import com.daniel.mangavault.dto.response.ReadingHistoryResponse;
import com.daniel.mangavault.service.LibraryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/** Personal library endpoints — all require an authenticated user. */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LibraryController {
    private final LibraryService libraryService;

    @PostMapping("/stories/{id}/follow")
    public ApiResponse<Void> follow(@PathVariable String id) {
        libraryService.followStory(id);
        return ApiResponse.<Void>builder().code(1000).message("Đã theo dõi truyện").build();
    }

    @DeleteMapping("/stories/{id}/follow")
    public ApiResponse<Void> unfollow(@PathVariable String id) {
        libraryService.unfollowStory(id);
        return ApiResponse.<Void>builder().code(1000).message("Đã bỏ theo dõi").build();
    }

    @GetMapping("/stories/{id}/follow")
    public ApiResponse<Map<String, Boolean>> isFollowing(@PathVariable String id) {
        return ApiResponse.<Map<String, Boolean>>builder()
                .code(1000)
                .result(Map.of("following", libraryService.isFollowing(id)))
                .build();
    }

    @GetMapping("/me/follows")
    public ApiResponse<PageResponse<FollowedStoryResponse>> getFollows(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ApiResponse.<PageResponse<FollowedStoryResponse>>builder()
                .code(1000)
                .result(libraryService.getFollowedStories(page, size))
                .build();
    }

    @GetMapping("/me/history")
    public ApiResponse<PageResponse<ReadingHistoryResponse>> getHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ApiResponse.<PageResponse<ReadingHistoryResponse>>builder()
                .code(1000)
                .result(libraryService.getHistory(page, size))
                .build();
    }

    @PostMapping("/me/history")
    public ApiResponse<Void> saveProgress(@Valid @RequestBody ReadingProgressRequest request) {
        libraryService.saveProgress(request.getChapterId());
        return ApiResponse.<Void>builder().code(1000).build();
    }

    /** Returns null in `result` when the story has never been opened. */
    @GetMapping("/me/history/{storyId}")
    public ApiResponse<ReadingHistoryResponse> getProgress(@PathVariable String storyId) {
        return ApiResponse.<ReadingHistoryResponse>builder()
                .code(1000)
                .result(libraryService.getProgressForStory(storyId))
                .build();
    }

    @DeleteMapping("/me/history/{storyId}")
    public ApiResponse<Void> deleteProgress(@PathVariable String storyId) {
        libraryService.deleteProgress(storyId);
        return ApiResponse.<Void>builder().code(1000).message("Đã xóa khỏi lịch sử").build();
    }
}
