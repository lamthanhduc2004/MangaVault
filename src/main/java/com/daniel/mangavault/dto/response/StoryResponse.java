package com.daniel.mangavault.dto.response;

import com.daniel.mangavault.enums.StoryStatus;
import com.daniel.mangavault.enums.Visibility;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoryResponse {
    private String id;
    private String title;
    private String slug;
    private String author;
    private String description;
    private String coverUrl;
    private StoryStatus status;
    private Visibility visibility;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
