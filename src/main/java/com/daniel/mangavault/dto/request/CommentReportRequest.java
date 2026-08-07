package com.daniel.mangavault.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentReportRequest {
    @Size(max = 255, message = "reason must not exceed 255 characters")
    private String reason;
}
