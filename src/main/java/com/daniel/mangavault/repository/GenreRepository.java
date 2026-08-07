package com.daniel.mangavault.repository;

import com.daniel.mangavault.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GenreRepository extends JpaRepository<Genre, String> {

    List<Genre> findAllByOrderByNameAsc();

    boolean existsByName(String name);

    boolean existsBySlug(String slug);

    boolean existsByNameAndIdNot(String name, String id);

    boolean existsBySlugAndIdNot(String slug, String id);

    /** Guards deletion: a genre still assigned to stories must not disappear. */
    @Query("select count(s) from Story s join s.genres g where g.id = :genreId")
    long countStoriesUsingGenre(@Param("genreId") String genreId);
}
