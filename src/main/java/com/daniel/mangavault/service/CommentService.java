package com.daniel.mangavault.service;

import com.daniel.mangavault.dto.request.CommentReportRequest;
import com.daniel.mangavault.dto.request.CommentRequest;
import com.daniel.mangavault.dto.response.CommentResponse;
import com.daniel.mangavault.dto.response.PageResponse;
import com.daniel.mangavault.dto.response.ReportedCommentResponse;
import com.daniel.mangavault.entity.Comment;
import com.daniel.mangavault.entity.CommentReport;
import com.daniel.mangavault.entity.Story;
import com.daniel.mangavault.entity.User;
import com.daniel.mangavault.exception.AppException;
import com.daniel.mangavault.exception.ErrorCode;
import com.daniel.mangavault.repository.CommentReportRepository;
import com.daniel.mangavault.repository.CommentRepository;
import com.daniel.mangavault.repository.StoryRepository;
import com.daniel.mangavault.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Story comments (F14) and the moderation queue (F20). */
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentReportRepository commentReportRepository;
    private final StoryRepository storyRepository;
    private final CurrentUserProvider currentUserProvider;

    public PageResponse<CommentResponse> getComments(String storyId, int page, int size) {
        if (!storyRepository.existsById(storyId)) {
            throw new AppException(ErrorCode.STORY_NOT_FOUND);
        }

        // Anonymous visitors can read comments; they simply own none of them.
        String currentUserId = null;
        try {
            currentUserId = currentUserProvider.requireUserId();
        } catch (AppException ignored) {
            // not signed in
        }

        Page<Comment> comments = commentRepository
                .findByStoryIdOrderByCreatedAtDesc(storyId, PageRequest.of(page, size));

        final String viewerId = currentUserId;
        return PageResponse.from(comments, comment -> mapToResponse(comment, viewerId));
    }

    public CommentResponse createComment(String storyId, CommentRequest request) {
        User user = currentUserProvider.requireUser();
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));

        Comment comment = commentRepository.save(Comment.builder()
                .story(story)
                .user(user)
                .content(request.getContent().trim())
                .build());

        return mapToResponse(comment, user.getId());
    }

    public CommentResponse updateComment(String commentId, CommentRequest request) {
        User user = currentUserProvider.requireUser();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));

        // Editing is owner-only; admins moderate by deleting, not rewriting.
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.NOT_RESOURCE_OWNER);
        }

        comment.setContent(request.getContent().trim());

        return mapToResponse(commentRepository.save(comment), user.getId());
    }

    @Transactional
    public void deleteComment(String commentId) {
        String userId = currentUserProvider.requireUserId();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));

        boolean owner = comment.getUser().getId().equals(userId);
        if (!owner && !currentUserProvider.isAdmin()) {
            throw new AppException(ErrorCode.NOT_RESOURCE_OWNER);
        }

        commentReportRepository.deleteByCommentId(commentId);
        commentRepository.delete(comment);
    }

    @Transactional
    public void reportComment(String commentId, CommentReportRequest request) {
        User user = currentUserProvider.requireUser();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));

        if (commentReportRepository.existsByCommentIdAndUserId(commentId, user.getId())) {
            throw new AppException(ErrorCode.COMMENT_ALREADY_REPORTED);
        }

        commentReportRepository.save(CommentReport.builder()
                .comment(comment)
                .user(user)
                .reason(request == null ? null : request.getReason())
                .build());

        // Counter is denormalized so the moderation queue can sort without a join.
        comment.setReportCount(comment.getReportCount() + 1);
        commentRepository.save(comment);
    }

    // --- Admin ---------------------------------------------------------------

    public PageResponse<ReportedCommentResponse> getReportedComments(int page, int size) {
        Page<Comment> comments = commentRepository
                .findByReportCountGreaterThanOrderByReportCountDesc(0, PageRequest.of(page, size));

        return PageResponse.from(comments, comment -> ReportedCommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .storyId(comment.getStory().getId())
                .storyTitle(comment.getStory().getTitle())
                .authorId(comment.getUser().getId())
                .authorUsername(comment.getUser().getUsername())
                .reportCount(comment.getReportCount())
                .createdAt(comment.getCreatedAt())
                .build());
    }

    /** Clears the reports without deleting the comment — "this one is fine". */
    @Transactional
    public void dismissReports(String commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));

        commentReportRepository.deleteByCommentId(commentId);
        comment.setReportCount(0);
        commentRepository.save(comment);
    }

    private CommentResponse mapToResponse(Comment comment, String viewerId) {
        User author = comment.getUser();
        String displayName = author.getDisplayName() != null && !author.getDisplayName().isBlank()
                ? author.getDisplayName()
                : author.getUsername();

        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .authorId(author.getId())
                .authorName(displayName)
                .authorAvatarUrl(author.getAvatarUrl())
                .mine(viewerId != null && viewerId.equals(author.getId()))
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
