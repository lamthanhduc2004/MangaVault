package com.daniel.mangavault.dto.response;

import lombok.*;

import java.time.LocalDateTime;

/** Chapter info for list views — never contains the chapter content. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterSummaryResponse {
    private String id;
    private Integer chapterNumber;
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
