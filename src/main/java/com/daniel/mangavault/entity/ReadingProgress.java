package com.daniel.mangavault.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * The most recently read chapter of a story, per user.
 * <p>
 * One row per (user, story) rather than an append-only log: the requirement is
 * "continue where I left off" and "reading history", both of which only need the
 * latest position. This single table therefore covers F12 and F13, and supplies
 * the "last read chapter" column of the following list (F11).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "reading_progress",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "story_id"}),
        indexes = @Index(name = "idx_progress_user_updated", columnList = "user_id, updated_at")
)
public class ReadingProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;

    /** Denormalized so history lists can render without loading each chapter. */
    @Column(name = "chapter_number", nullable = false)
    private Integer chapterNumber;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
