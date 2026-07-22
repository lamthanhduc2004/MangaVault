package com.daniel.mangavault.dto.response;

import lombok.*;

import java.time.LocalDateTime;

/** Full chapter for the reader view, with prev/next navigation ids. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterResponse {
    private String id;
    private String storyId;
    private String storyTitle;
    private Integer chapterNumber;
    private String title;
    private String content;
    private String prevChapterId;
    private String nextChapterId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
