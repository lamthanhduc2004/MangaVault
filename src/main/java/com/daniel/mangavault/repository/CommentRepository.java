package com.daniel.mangavault.repository;

import com.daniel.mangavault.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, String> {

    /** Author is always rendered with the comment. */
    @EntityGraph(attributePaths = "user")
    Page<Comment> findByStoryIdAndHiddenFalseOrderByCreatedAtDesc(String storyId, Pageable pageable);

    /** Moderation queue: only reported comments, most-reported first. */
    @EntityGraph(attributePaths = {"user", "story"})
    Page<Comment> findByReportCountGreaterThanOrderByReportCountDesc(int threshold, Pageable pageable);

    /** Full admin catalogue with optional report/visibility filters and text search. */
    @EntityGraph(attributePaths = {"user", "story"})
    @Query("""
            select c from Comment c
            where (:keyword = '' or lower(c.content) like lower(concat('%', :keyword, '%'))
                                 or lower(c.user.username) like lower(concat('%', :keyword, '%'))
                                 or lower(c.story.title) like lower(concat('%', :keyword, '%')))
              and (:reportedOnly = false or c.reportCount > 0)
              and (:hidden is null or c.hidden = :hidden)
            order by c.createdAt desc
            """)
    Page<Comment> searchForAdmin(@Param("keyword") String keyword,
                                 @Param("reportedOnly") boolean reportedOnly,
                                 @Param("hidden") Boolean hidden,
                                 Pageable pageable);

    @Modifying
    @Query("delete from Comment c where c.story.id = :storyId")
    void deleteByStoryId(@Param("storyId") String storyId);
}
