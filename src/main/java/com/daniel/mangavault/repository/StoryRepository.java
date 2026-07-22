package com.daniel.mangavault.repository;

import com.daniel.mangavault.entity.Story;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoryRepository extends JpaRepository<Story, String> {
    Page<Story> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, String id);
}
