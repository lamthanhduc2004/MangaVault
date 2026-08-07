package com.daniel.mangavault.repository;

import com.daniel.mangavault.entity.ReadingProgress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ReadingProgressRepository extends JpaRepository<ReadingProgress, String> {

    @EntityGraph(attributePaths = "story")
    Page<ReadingProgress> findByUserIdOrderByUpdatedAtDesc(String userId, Pageable pageable);

    Optional<ReadingProgress> findByUserIdAndStoryId(String userId, String storyId);

    /** Batch lookup so the following list does not query progress story by story. */
    @Query("select p from ReadingProgress p where p.user.id = :userId and p.story.id in :storyIds")
    List<ReadingProgress> findByUserIdAndStoryIds(@Param("userId") String userId,
                                                  @Param("storyIds") Collection<String> storyIds);

    void deleteByUserIdAndStoryId(String userId, String storyId);

    void deleteByStoryId(String storyId);

    void deleteByChapterId(String chapterId);
}
