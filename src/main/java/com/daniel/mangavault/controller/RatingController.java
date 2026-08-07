package com.daniel.mangavault.controller;

import com.daniel.mangavault.dto.request.RatingRequest;
import com.daniel.mangavault.dto.response.ApiResponse;
import com.daniel.mangavault.dto.response.RatingSummaryResponse;
import com.daniel.mangavault.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stories/{id}/rating")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;

    /** PUT rather than POST: re-rating overwrites, so the call is idempotent. */
    @PutMapping
    public ApiResponse<RatingSummaryResponse> rate(@PathVariable String id,
                                                   @Valid @RequestBody RatingRequest request) {
        return ApiResponse.<RatingSummaryResponse>builder()
                .code(1000)
                .result(ratingService.rateStory(id, request.getScore()))
                .build();
    }

    @DeleteMapping
    public ApiResponse<RatingSummaryResponse> removeRating(@PathVariable String id) {
        return ApiResponse.<RatingSummaryResponse>builder()
                .code(1000)
                .result(ratingService.deleteRating(id))
                .build();
    }

    @GetMapping
    public ApiResponse<RatingSummaryResponse> getSummary(@PathVariable String id) {
        return ApiResponse.<RatingSummaryResponse>builder()
                .code(1000)
                .result(ratingService.getSummary(id))
                .build();
    }
}
