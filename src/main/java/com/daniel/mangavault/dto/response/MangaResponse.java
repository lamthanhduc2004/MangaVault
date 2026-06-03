package com.daniel.mangavault.dto.response;

import com.daniel.mangavault.enums.MangaStatus;
import com.daniel.mangavault.enums.Visibility;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MangaResponse {
    private String id;
    private String title;
    private String slug;
    private String description;
    private String coverUrl;
    private MangaStatus status;
    private Visibility visibility;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}