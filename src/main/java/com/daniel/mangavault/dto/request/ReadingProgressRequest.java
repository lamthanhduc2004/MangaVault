package com.daniel.mangavault.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReadingProgressRequest {
    @NotBlank(message = "chapterId must not be blank")
    private String chapterId;
}
