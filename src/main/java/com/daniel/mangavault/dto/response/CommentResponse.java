package com.daniel.mangavault.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentResponse {
    private String id;
    private String content;
    private String authorId;
    private String authorName;
    private String authorAvatarUrl;
    /** True when the signed-in user wrote it — drives the edit/delete controls. */
    private boolean mine;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
