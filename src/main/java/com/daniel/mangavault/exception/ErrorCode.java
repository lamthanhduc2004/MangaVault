package com.daniel.mangavault.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    STORY_NOT_FOUND(4041, "Story not found", HttpStatus.NOT_FOUND),
    CHAPTER_NOT_FOUND(4042, "Chapter not found", HttpStatus.NOT_FOUND),
    SLUG_ALREADY_EXISTS(4090, "Slug already exists", HttpStatus.CONFLICT),
    CHAPTER_NUMBER_ALREADY_EXISTS(4091, "Chapter number already exists in this story", HttpStatus.CONFLICT);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
