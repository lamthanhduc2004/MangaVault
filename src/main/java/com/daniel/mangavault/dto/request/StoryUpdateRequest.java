package com.daniel.mangavault.dto.request;

import com.daniel.mangavault.enums.StoryStatus;
import com.daniel.mangavault.enums.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class StoryUpdateRequest {
    @NotBlank(message = "title must not be blank")
    @Size(max = 255, message = "title must not exceed 255 characters")
    private String title;

    @NotBlank(message = "slug must not be blank")
    @Size(max = 255, message = "slug must not exceed 255 characters")
    private String slug;

    @Size(max = 255, message = "author must not exceed 255 characters")
    private String author;

    @Size(max = 10000, message = "description must not exceed 10000 characters")
    private String description;

    @Size(max = 500, message = "coverUrl must not exceed 500 characters")
    private String coverUrl;

    @NotNull(message = "status is required")
    private StoryStatus status;

    @NotNull(message = "visibility is required")
    private Visibility visibility;

    /** Null leaves genres untouched; an empty set clears them. */
    private Set<String> genreIds;
}
