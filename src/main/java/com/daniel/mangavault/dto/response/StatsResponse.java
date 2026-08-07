package com.daniel.mangavault.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/** Dashboard figures (F21). */
@Getter
@Builder
public class StatsResponse {
    private long totalUsers;
    private long totalStories;
    private long totalChapters;
    private long totalComments;
    private long storiesUpdatedLast7Days;
    private List<StoryResponse> topViewedStories;
    private List<StoryResponse> topFollowedStories;
}
