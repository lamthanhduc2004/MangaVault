package com.daniel.mangavault.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RatingSummaryResponse {
    private double average;
    private int count;
    /** The signed-in user's own score, or null when they have not rated yet. */
    private Integer myScore;
}
