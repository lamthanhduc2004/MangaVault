package com.daniel.mangavault.repository;

import com.daniel.mangavault.entity.Story;
import com.daniel.mangavault.enums.StoryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoryRepository extends JpaRepository<Story, String> {
    // An empty keyword matches every row, so these two methods cover all
    // keyword/status filter combinations.
    Page<Story> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Story> findByTitleContainingIgnoreCaseAndStatus(String title, StoryStatus status, Pageable pageable);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, String id);
}
