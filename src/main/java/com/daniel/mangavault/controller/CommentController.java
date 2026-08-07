package com.daniel.mangavault.controller;

import com.daniel.mangavault.dto.request.CommentReportRequest;
import com.daniel.mangavault.dto.request.CommentRequest;
import com.daniel.mangavault.dto.response.ApiResponse;
import com.daniel.mangavault.dto.response.CommentResponse;
import com.daniel.mangavault.dto.response.PageResponse;
import com.daniel.mangavault.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    /** Public: anyone can read a story's discussion. */
    @GetMapping("/stories/{id}/comments")
    public ApiResponse<PageResponse<CommentResponse>> getComments(
            @PathVariable String id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<CommentResponse>>builder()
                .code(1000)
                .result(commentService.getComments(id, page, size))
                .build();
    }

    @PostMapping("/stories/{id}/comments")
    public ApiResponse<CommentResponse> createComment(@PathVariable String id,
                                                      @Valid @RequestBody CommentRequest request) {
        return ApiResponse.<CommentResponse>builder()
                .code(1000)
                .result(commentService.createComment(id, request))
                .build();
    }

    @PutMapping("/comments/{id}")
    public ApiResponse<CommentResponse> updateComment(@PathVariable String id,
                                                      @Valid @RequestBody CommentRequest request) {
        return ApiResponse.<CommentResponse>builder()
                .code(1000)
                .result(commentService.updateComment(id, request))
                .build();
    }

    @DeleteMapping("/comments/{id}")
    public ApiResponse<Void> deleteComment(@PathVariable String id) {
        commentService.deleteComment(id);
        return ApiResponse.<Void>builder().code(1000).message("Đã xóa bình luận").build();
    }

    @PostMapping("/comments/{id}/report")
    public ApiResponse<Void> reportComment(@PathVariable String id,
                                           @RequestBody(required = false) @Valid CommentReportRequest request) {
        commentService.reportComment(id, request);
        return ApiResponse.<Void>builder().code(1000).message("Đã gửi báo cáo").build();
    }
}
