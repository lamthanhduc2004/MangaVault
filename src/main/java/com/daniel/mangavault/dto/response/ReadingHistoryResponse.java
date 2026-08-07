package com.daniel.mangavault.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReadingHistoryResponse {
    private String storyId;
    private String title;
    private String slug;
    private String coverUrl;
    private String chapterId;
    private Integer chapterNumber;
    private String chapterTitle;
    private LocalDateTime readAt;
}
