package com.daniel.mangavault.repository;

import com.daniel.mangavault.entity.CommentReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentReportRepository extends JpaRepository<CommentReport, String> {

    boolean existsByCommentIdAndUserId(String commentId, String userId);

    void deleteByCommentId(String commentId);

    /** Bulk delete by story, used when a story (and all its comments) is removed. */
    @Modifying
    @Query("delete from CommentReport r where r.comment.story.id = :storyId")
    void deleteByStoryId(@Param("storyId") String storyId);
}
