package com.daniel.mangavault.dto.request;

import com.daniel.mangavault.enums.MangaStatus;
import com.daniel.mangavault.enums.Visibility;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MangaCreationRequest {
    private String title;
    private String slug;
    private String description;
    private String coverUrl;
    private MangaStatus status;
    private Visibility visibility;                                    
}