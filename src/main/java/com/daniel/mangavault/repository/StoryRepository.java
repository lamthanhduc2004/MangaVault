package com.daniel.mangavault.repository;

import com.daniel.mangavault.entity.Story;
import com.daniel.mangavault.enums.StoryStatus;
import com.daniel.mangavault.enums.Visibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface StoryRepository extends JpaRepository<Story, String> {

    /**
     * Public catalogue search. An empty keyword matches everything and a null status
     * skips that filter, so this one query covers every combination the list page needs.
     * Only PUBLIC stories are ever returned — visibility is a real access boundary here,
     * not a UI hint.
     */
    // The genre join multiplies rows, so `distinct` is required — and with it an
    // explicit countQuery, or totalElements would count joined rows instead of stories.
    @Query(value = """
            select distinct s from Story s left join s.genres g
            where s.visibility = com.daniel.mangavault.enums.Visibility.PUBLIC
              and (:keyword = '' or lower(s.title) like lower(concat('%', :keyword, '%'))
                                 or lower(s.author) like lower(concat('%', :keyword, '%')))
              and (:status is null or s.status = :status)
              and (:genreSlug is null or g.slug = :genreSlug)
            """,
            countQuery = """
            select count(distinct s) from Story s left join s.genres g
            where s.visibility = com.daniel.mangavault.enums.Visibility.PUBLIC
              and (:keyword = '' or lower(s.title) like lower(concat('%', :keyword, '%'))
                                 or lower(s.author) like lower(concat('%', :keyword, '%')))
              and (:status is null or s.status = :status)
              and (:genreSlug is null or g.slug = :genreSlug)
            """)
    Page<Story> searchPublic(@Param("keyword") String keyword,
                             @Param("status") StoryStatus status,
                             @Param("genreSlug") String genreSlug,
                             Pageable pageable);

    /** Admin catalogue search — includes PRIVATE stories, with an optional visibility filter. */
    @Query(value = """
            select distinct s from Story s left join s.genres g
            where (:keyword = '' or lower(s.title) like lower(concat('%', :keyword, '%'))
                                 or lower(s.author) like lower(concat('%', :keyword, '%')))
              and (:status is null or s.status = :status)
              and (:visibility is null or s.visibility = :visibility)
              and (:genreSlug is null or g.slug = :genreSlug)
            """,
            countQuery = """
            select count(distinct s) from Story s left join s.genres g
            where (:keyword = '' or lower(s.title) like lower(concat('%', :keyword, '%'))
                                 or lower(s.author) like lower(concat('%', :keyword, '%')))
              and (:status is null or s.status = :status)
              and (:visibility is null or s.visibility = :visibility)
              and (:genreSlug is null or g.slug = :genreSlug)
            """)
    Page<Story> searchForAdmin(@Param("keyword") String keyword,
                               @Param("status") StoryStatus status,
                               @Param("visibility") Visibility visibility,
                               @Param("genreSlug") String genreSlug,
                               Pageable pageable);

    /**
     * Bulk update on purpose: calling save() would trigger @UpdateTimestamp and push
     * every story that someone merely read to the top of the "recently updated" list.
     */
    @Modifying
    @Query("update Story s set s.viewCount = s.viewCount + 1 where s.id = :id")
    void incrementViewCount(@Param("id") String id);

    @Modifying
    @Query("update Story s set s.ratingAvg = :avg, s.ratingCount = :count where s.id = :id")
    void updateRatingAggregate(@Param("id") String id, @Param("avg") double avg, @Param("count") int count);

    long countByUpdatedAtAfter(LocalDateTime since);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, String id);
}
