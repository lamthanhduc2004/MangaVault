package com.daniel.mangavault.dto.response;

import com.daniel.mangavault.enums.StoryStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * A followed story with everything the list needs at a glance: the newest chapter,
 * how far the reader got, and when the story last changed.
 */
@Getter
@Builder
public class FollowedStoryResponse {
    private String storyId;
    private String title;
    private String slug;
    private String coverUrl;
    private String author;
    private StoryStatus status;
    private Integer latestChapterNumber;
    private Integer lastReadChapterNumber;
    private String lastReadChapterId;
    private LocalDateTime storyUpdatedAt;
    private LocalDateTime followedAt;
}
