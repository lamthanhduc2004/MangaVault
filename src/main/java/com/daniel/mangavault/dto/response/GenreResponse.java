package com.daniel.mangavault.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GenreResponse {
    private String id;
    private String name;
    private String slug;
}
