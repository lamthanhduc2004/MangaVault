package com.daniel.mangavault.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentVisibilityRequest {
    @NotNull(message = "hidden is required")
    private Boolean hidden;
}
