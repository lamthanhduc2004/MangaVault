package com.daniel.mangavault.dto.request;

import com.daniel.mangavault.enums.StoryStatus;
import com.daniel.mangavault.enums.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoryCreationRequest {
    @NotBlank(message = "title must not be blank")
    @Size(max = 255, message = "title must not exceed 255 characters")
    private String title;

    @NotBlank(message = "slug must not be blank")
    @Size(max = 255, message = "slug must not exceed 255 characters")
    private String slug;

    @Size(max = 255, message = "author must not exceed 255 characters")
    private String author;

    private String description;

    private String coverUrl;

    @NotNull(message = "status is required")
    private StoryStatus status;

    @NotNull(message = "visibility is required")
    private Visibility visibility;
}
