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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/** A comment on a story (F14). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "comments", indexes = {
        @Index(name = "idx_comments_story_created", columnList = "story_id, created_at"),
        @Index(name = "idx_comments_report_count", columnList = "report_count")
})
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Bounded in the column as well as the DTO — the requirement asks for a hard cap. */
    @Column(nullable = false, length = 1000)
    private String content;

    @Builder.Default
    @Column(name = "report_count", columnDefinition = "INT NOT NULL DEFAULT 0")
    private int reportCount = 0;

    /** Soft moderation keeps an audit trail and allows an admin to restore content. */
    @Builder.Default
    @Column(columnDefinition = "BOOLEAN NOT NULL DEFAULT FALSE")
    private boolean hidden = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
