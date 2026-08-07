package com.daniel.mangavault.controller;

import com.daniel.mangavault.dto.request.ChapterUpdateRequest;
import com.daniel.mangavault.dto.response.ApiResponse;
import com.daniel.mangavault.dto.response.ChapterResponse;
import com.daniel.mangavault.dto.response.ChapterSummaryResponse;
import com.daniel.mangavault.service.ChapterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/chapters")
@RequiredArgsConstructor
public class AdminChapterController {
    private final ChapterService chapterService;

    @PutMapping("/{id}")
    public ApiResponse<ChapterSummaryResponse> updateChapter(@PathVariable String id,
                                                             @Valid @RequestBody ChapterUpdateRequest request) {
        return ApiResponse.<ChapterSummaryResponse>builder()
                .code(1000)
                .result(chapterService.updateChapter(id, request))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ChapterResponse> getChapter(@PathVariable String id) {
        return ApiResponse.<ChapterResponse>builder()
                .code(1000)
                .result(chapterService.getChapterByIdForAdmin(id))
                .build();
    }

    @PatchMapping("/{id}/publish")
    public ApiResponse<ChapterSummaryResponse> setPublished(@PathVariable String id,
                                                            @RequestBody PublishRequest request) {
        return ApiResponse.<ChapterSummaryResponse>builder()
                .code(1000)
                .result(chapterService.setPublished(id, request.isPublished()))
                .build();
    }

    /** Tiny inline body so toggling visibility does not need a full chapter payload. */
    @lombok.Getter
    @lombok.Setter
    public static class PublishRequest {
        private boolean published;
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteChapter(@PathVariable String id) {
        chapterService.deleteChapter(id);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Chapter deleted")
                .build();
    }
}
