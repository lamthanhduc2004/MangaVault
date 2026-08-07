package com.daniel.mangavault.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequest {
    @NotBlank(message = "content must not be blank")
    @Size(max = 1000, message = "content must not exceed 1000 characters")
    private String content;
}
