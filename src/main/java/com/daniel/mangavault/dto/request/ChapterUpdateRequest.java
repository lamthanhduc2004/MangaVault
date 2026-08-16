package com.daniel.mangavault.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChapterUpdateRequest {
    @NotNull(message = "chapterNumber is required")
    @Min(value = 1, message = "chapterNumber must be at least 1")
    private Integer chapterNumber;

    @NotBlank(message = "title must not be blank")
    @Size(max = 255, message = "title must not exceed 255 characters")
    private String title;

    @NotBlank(message = "content must not be blank")
    @Size(max = 1000000, message = "content must not exceed 1000000 characters")
    private String content;
}
