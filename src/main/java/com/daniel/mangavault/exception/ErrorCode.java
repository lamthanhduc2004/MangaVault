package com.daniel.mangavault.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 400 — invalid input
    INVALID_CURRENT_PASSWORD(4001, "Current password is incorrect", HttpStatus.BAD_REQUEST),

    // 401 — authentication
    UNAUTHENTICATED(4010, "Authentication required", HttpStatus.UNAUTHORIZED),
    INVALID_CREDENTIALS(4011, "Invalid username or password", HttpStatus.UNAUTHORIZED),

    // 403 — authorization
    FORBIDDEN(4030, "Access denied", HttpStatus.FORBIDDEN),
    ACCOUNT_LOCKED(4031, "Account has been locked", HttpStatus.FORBIDDEN),
    NOT_RESOURCE_OWNER(4032, "You can only modify your own content", HttpStatus.FORBIDDEN),
    CANNOT_MODIFY_SELF(4033, "You cannot change your own role or status", HttpStatus.FORBIDDEN),

    // 404 — missing resources
    STORY_NOT_FOUND(4041, "Story not found", HttpStatus.NOT_FOUND),
    CHAPTER_NOT_FOUND(4042, "Chapter not found", HttpStatus.NOT_FOUND),
    GENRE_NOT_FOUND(4043, "Genre not found", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND(4044, "User not found", HttpStatus.NOT_FOUND),
    COMMENT_NOT_FOUND(4045, "Comment not found", HttpStatus.NOT_FOUND),
    RATING_NOT_FOUND(4046, "Rating not found", HttpStatus.NOT_FOUND),
    FOLLOW_NOT_FOUND(4047, "Story is not being followed", HttpStatus.NOT_FOUND),

    // 409 — conflicts
    SLUG_ALREADY_EXISTS(4090, "Slug already exists", HttpStatus.CONFLICT),
    CHAPTER_NUMBER_ALREADY_EXISTS(4091, "Chapter number already exists in this story", HttpStatus.CONFLICT),
    USERNAME_ALREADY_EXISTS(4092, "Username already exists", HttpStatus.CONFLICT),
    EMAIL_ALREADY_EXISTS(4093, "Email already exists", HttpStatus.CONFLICT),
    GENRE_NAME_ALREADY_EXISTS(4094, "Genre name already exists", HttpStatus.CONFLICT),
    GENRE_SLUG_ALREADY_EXISTS(4095, "Genre slug already exists", HttpStatus.CONFLICT),
    GENRE_IN_USE(4096, "Genre is assigned to stories and cannot be deleted", HttpStatus.CONFLICT),
    ALREADY_FOLLOWED(4097, "Story is already followed", HttpStatus.CONFLICT),
    COMMENT_ALREADY_REPORTED(4098, "You already reported this comment", HttpStatus.CONFLICT);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
