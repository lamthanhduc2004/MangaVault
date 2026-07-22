package com.daniel.mangavault.repository;

import com.daniel.mangavault.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChapterRepository extends JpaRepository<Chapter, String> {
    List<Chapter> findByStoryIdOrderByChapterNumberAsc(String storyId);

    Optional<Chapter> findFirstByStoryIdAndChapterNumberLessThanOrderByChapterNumberDesc(String storyId, Integer chapterNumber);

    Optional<Chapter> findFirstByStoryIdAndChapterNumberGreaterThanOrderByChapterNumberAsc(String storyId, Integer chapterNumber);

    boolean existsByStoryIdAndChapterNumber(String storyId, Integer chapterNumber);

    boolean existsByStoryIdAndChapterNumberAndIdNot(String storyId, Integer chapterNumber, String id);

    void deleteByStoryId(String storyId);
}
