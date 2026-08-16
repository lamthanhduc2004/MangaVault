package com.daniel.mangavault.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/** Moderation queue row (F20). */
@Getter
@Builder
public class ReportedCommentResponse {
    private String id;
    private String content;
    private String storyId;
    private String storyTitle;
    private String authorId;
    private String authorUsername;
    private int reportCount;
    private boolean hidden;
    private LocalDateTime createdAt;
}
