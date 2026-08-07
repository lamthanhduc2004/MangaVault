package com.daniel.mangavault.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileUpdateRequest {
    @Size(max = 100, message = "displayName must not exceed 100 characters")
    private String displayName;

    /** Avatar is referenced by URL — the project does not host uploaded files. */
    @Size(max = 500, message = "avatarUrl must not exceed 500 characters")
    private String avatarUrl;
}
