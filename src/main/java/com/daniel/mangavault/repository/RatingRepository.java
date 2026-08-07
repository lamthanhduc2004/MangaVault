package com.daniel.mangavault.repository;

import com.daniel.mangavault.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, String> {

    Optional<Rating> findByUserIdAndStoryId(String userId, String storyId);

    /** Recomputes the story aggregate in one pass after a vote changes. */
    @Query("select coalesce(avg(r.score), 0), count(r) from Rating r where r.story.id = :storyId")
    Object[] aggregateByStoryId(@Param("storyId") String storyId);

    void deleteByStoryId(String storyId);
}
