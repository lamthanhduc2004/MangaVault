package com.daniel.mangavault.entity;

import com.daniel.mangavault.enums.StoryStatus;
import com.daniel.mangavault.enums.Visibility;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "stories", indexes = {
        @Index(name = "idx_stories_visibility_updated", columnList = "visibility, updated_at"),
        @Index(name = "idx_stories_view_count", columnList = "view_count"),
        @Index(name = "idx_stories_rating_avg", columnList = "rating_avg"),
        @Index(name = "idx_stories_author", columnList = "author")
})
public class Story {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private String slug;

    private String author;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 500)
    private String coverUrl;

    @Enumerated(EnumType.STRING)
    private StoryStatus status;

    @Enumerated(EnumType.STRING)
    private Visibility visibility;

    // Denormalized counters. Sorting the story list by views/rating is a hard
    // requirement, and Pageable cannot order by an aggregate computed in a join,
    // so these are maintained on write instead (see StoryRepository bulk updates).
    // A DEFAULT is mandatory: ddl-auto=update never backfills existing rows.
    @Builder.Default
    @Column(name = "view_count", columnDefinition = "BIGINT NOT NULL DEFAULT 0")
    private long viewCount = 0L;

    @Builder.Default
    @Column(name = "rating_avg", columnDefinition = "DOUBLE NOT NULL DEFAULT 0")
    private double ratingAvg = 0.0;

    @Builder.Default
    @Column(name = "rating_count", columnDefinition = "INT NOT NULL DEFAULT 0")
    private int ratingCount = 0;

    // Set, not List: a bag-typed @ManyToMany breaks as soon as a query fetch-joins it.
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "story_genres",
            joinColumns = @JoinColumn(name = "story_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new LinkedHashSet<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
