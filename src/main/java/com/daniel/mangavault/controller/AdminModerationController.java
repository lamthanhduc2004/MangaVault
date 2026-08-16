package com.daniel.mangavault.controller;

import com.daniel.mangavault.dto.request.CommentVisibilityRequest;
import com.daniel.mangavault.dto.response.ApiResponse;
import com.daniel.mangavault.dto.response.PageResponse;
import com.daniel.mangavault.dto.response.ReportedCommentResponse;
import com.daniel.mangavault.dto.response.StatsResponse;
import com.daniel.mangavault.service.CommentService;
import com.daniel.mangavault.service.StatsService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/** Admin-only moderation queue (F20) and dashboard figures (F21). */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminModerationController {
    private final CommentService commentService;
    private final StatsService statsService;

    @GetMapping("/comments")
    public ApiResponse<PageResponse<ReportedCommentResponse>> getComments(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "false") boolean reportedOnly,
            @RequestParam(required = false) Boolean hidden,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<ReportedCommentResponse>>builder()
                .code(1000)
                .result(commentService.getCommentsForAdmin(keyword, reportedOnly, hidden, page, size))
                .build();
    }

    @GetMapping("/comments/reported")
    public ApiResponse<PageResponse<ReportedCommentResponse>> getReportedComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<ReportedCommentResponse>>builder()
                .code(1000)
                .result(commentService.getReportedComments(page, size))
                .build();
    }

    /** Keeps the comment but empties its report queue. */
    @PostMapping("/comments/{id}/dismiss-reports")
    public ApiResponse<Void> dismissReports(@PathVariable String id) {
        commentService.dismissReports(id);
        return ApiResponse.<Void>builder().code(1000).message("Đã bỏ qua báo cáo").build();
    }

    @PatchMapping("/comments/{id}/visibility")
    public ApiResponse<ReportedCommentResponse> setVisibility(
            @PathVariable String id, @Valid @RequestBody CommentVisibilityRequest request) {
        return ApiResponse.<ReportedCommentResponse>builder()
                .code(1000)
                .result(commentService.setHidden(id, request.getHidden()))
                .build();
    }

    @DeleteMapping("/comments/{id}")
    public ApiResponse<Void> deleteComment(@PathVariable String id) {
        commentService.deleteComment(id);
        return ApiResponse.<Void>builder().code(1000).message("Đã xóa bình luận").build();
    }

    @GetMapping("/stats")
    public ApiResponse<StatsResponse> getStats() {
        return ApiResponse.<StatsResponse>builder()
                .code(1000)
                .result(statsService.getStats())
                .build();
    }
}
