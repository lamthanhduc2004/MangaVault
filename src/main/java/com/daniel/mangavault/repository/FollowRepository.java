package com.daniel.mangavault.repository;

import com.daniel.mangavault.entity.Follow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, String> {

    /** Story is always rendered alongside the follow, so join it up front. */
    @EntityGraph(attributePaths = "story")
    Page<Follow> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    Optional<Follow> findByUserIdAndStoryId(String userId, String storyId);

    boolean existsByUserIdAndStoryId(String userId, String storyId);

    long countByStoryId(String storyId);

    @Modifying
    @Query("delete from Follow f where f.story.id = :storyId")
    void deleteByStoryId(@Param("storyId") String storyId);

    /** Most-followed stories for the admin dashboard, resolved in one grouped query. */
    @Query("select f.story.id from Follow f group by f.story.id order by count(f) desc")
    List<String> findTopFollowedStoryIds(Pageable pageable);

    @Query("select f.story.id, count(f) from Follow f where f.story.id in :storyIds group by f.story.id")
    List<Object[]> countByStoryIds(@Param("storyIds") List<String> storyIds);
}
