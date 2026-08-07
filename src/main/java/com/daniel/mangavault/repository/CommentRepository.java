package com.daniel.mangavault.repository;

import com.daniel.mangavault.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, String> {

    /** Author is always rendered with the comment. */
    @EntityGraph(attributePaths = "user")
    Page<Comment> findByStoryIdOrderByCreatedAtDesc(String storyId, Pageable pageable);

    /** Moderation queue: only reported comments, most-reported first. */
    @EntityGraph(attributePaths = {"user", "story"})
    Page<Comment> findByReportCountGreaterThanOrderByReportCountDesc(int threshold, Pageable pageable);

    void deleteByStoryId(String storyId);
}
