package com.daniel.mangavault.repository;

import com.daniel.mangavault.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ChapterRepository extends JpaRepository<Chapter, String> {

    /** Admin view: every chapter, including unpublished ones. */
    List<Chapter> findByStoryIdOrderByChapterNumberAsc(String storyId);

    /** Reader view: published chapters only. */
    List<Chapter> findByStoryIdAndPublishedTrueOrderByChapterNumberAsc(String storyId);

    // Nearest-neighbor navigation skips gaps in numbering and hidden chapters.
    Optional<Chapter> findFirstByStoryIdAndPublishedTrueAndChapterNumberLessThanOrderByChapterNumberDesc(
            String storyId, Integer chapterNumber);

    Optional<Chapter> findFirstByStoryIdAndPublishedTrueAndChapterNumberGreaterThanOrderByChapterNumberAsc(
            String storyId, Integer chapterNumber);

    Optional<Chapter> findFirstByStoryIdAndPublishedTrueOrderByChapterNumberAsc(String storyId);

    long countByStoryIdAndPublishedTrue(String storyId);

    boolean existsByStoryIdAndChapterNumber(String storyId, Integer chapterNumber);

    boolean existsByStoryIdAndChapterNumberAndIdNot(String storyId, Integer chapterNumber, String id);

    /**
     * Latest published chapter of each story, gathered in one grouped query so the
     * following list does not issue a query per followed story.
     */
    @Query("""
            select c.story.id, max(c.chapterNumber) from Chapter c
            where c.story.id in :storyIds and c.published = true
            group by c.story.id
            """)
    List<Object[]> findLatestChapterNumberByStoryIds(@Param("storyIds") Collection<String> storyIds);

    Optional<Chapter> findByStoryIdAndChapterNumber(String storyId, Integer chapterNumber);

    long countByStoryId(String storyId);

    void deleteByStoryId(String storyId);
}
