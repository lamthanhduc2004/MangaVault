package com.daniel.mangavault.service;

import com.daniel.mangavault.dto.request.StoryCreationRequest;
import com.daniel.mangavault.dto.request.StoryUpdateRequest;
import com.daniel.mangavault.dto.response.PageResponse;
import com.daniel.mangavault.dto.response.StoryResponse;
import com.daniel.mangavault.entity.Genre;
import com.daniel.mangavault.entity.Story;
import com.daniel.mangavault.enums.StoryStatus;
import com.daniel.mangavault.enums.Visibility;
import com.daniel.mangavault.exception.AppException;
import com.daniel.mangavault.exception.ErrorCode;
import com.daniel.mangavault.repository.ChapterRepository;
import com.daniel.mangavault.repository.CommentReportRepository;
import com.daniel.mangavault.repository.CommentRepository;
import com.daniel.mangavault.repository.FollowRepository;
import com.daniel.mangavault.repository.GenreRepository;
import com.daniel.mangavault.repository.RatingRepository;
import com.daniel.mangavault.repository.ReadingProgressRepository;
import com.daniel.mangavault.repository.StoryRepository;
import com.daniel.mangavault.repository.StoryRelationCleanupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StoryService {
    private final StoryRepository storyRepository;
    private final StoryRelationCleanupRepository storyRelationCleanupRepository;
    private final ChapterRepository chapterRepository;
    private final FollowRepository followRepository;
    private final ReadingProgressRepository readingProgressRepository;
    private final RatingRepository ratingRepository;
    private final GenreRepository genreRepository;
    private final CommentRepository commentRepository;
    private final CommentReportRepository commentReportRepository;

    /**
     * Sort keys exposed to clients. Mapped explicitly instead of binding the raw
     * parameter to a column name so a client cannot sort by arbitrary internals.
     */
    private static Sort resolveSort(String sort) {
        return switch (sort == null ? "" : sort) {
            case "updated" -> Sort.by(Sort.Direction.DESC, "updatedAt");
            case "views" -> Sort.by(Sort.Direction.DESC, "viewCount");
            case "rating" -> Sort.by(Sort.Direction.DESC, "ratingAvg").and(Sort.by(Sort.Direction.DESC, "ratingCount"));
            case "title" -> Sort.by(Sort.Direction.ASC, "title");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
    }

    public StoryResponse createStory(StoryCreationRequest request) {
        if (storyRepository.existsBySlug(request.getSlug())) {
            throw new AppException(ErrorCode.SLUG_ALREADY_EXISTS);
        }

        Story story = Story.builder()
                .title(request.getTitle())
                .slug(request.getSlug())
                .author(request.getAuthor())
                .description(request.getDescription())
                .coverUrl(request.getCoverUrl())
                .status(request.getStatus())
                .visibility(request.getVisibility())
                .genres(resolveGenres(request.getGenreIds()))
                .build();

        Story savedStory = storyRepository.save(story);

        return mapToStoryResponse(savedStory);
    }

    /** Turns submitted genre ids into entities, rejecting any that do not exist. */
    private Set<Genre> resolveGenres(Set<String> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) {
            return new LinkedHashSet<>();
        }
        List<Genre> found = genreRepository.findAllById(genreIds);
        if (found.size() != genreIds.size()) {
            throw new AppException(ErrorCode.GENRE_NOT_FOUND);
        }
        return new LinkedHashSet<>(found);
    }

    /** Public catalogue — PRIVATE stories are never exposed here. */
    public PageResponse<StoryResponse> getStories(String keyword, StoryStatus status, String genreSlug,
                                                  String sort, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, resolveSort(sort));
        String kw = StringUtils.hasText(keyword) ? keyword.trim() : "";
        String genre = StringUtils.hasText(genreSlug) ? genreSlug.trim() : null;

        Page<Story> storyPage = storyRepository.searchPublic(kw, status, genre, pageable);

        return PageResponse.from(storyPage, this::mapToStoryResponse);
    }

    /** Admin catalogue — includes hidden stories so they remain manageable. */
    public PageResponse<StoryResponse> getStoriesForAdmin(String keyword, StoryStatus status, Visibility visibility,
                                                          String genreSlug, String sort, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, resolveSort(sort));
        String kw = StringUtils.hasText(keyword) ? keyword.trim() : "";
        String genre = StringUtils.hasText(genreSlug) ? genreSlug.trim() : null;

        Page<Story> storyPage = storyRepository.searchForAdmin(kw, status, visibility, genre, pageable);

        return PageResponse.from(storyPage, this::mapToStoryResponse);
    }

    public StoryResponse getStoryById(String id) {
        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));

        // A hidden story must be indistinguishable from a missing one.
        if (story.getVisibility() != Visibility.PUBLIC) {
            throw new AppException(ErrorCode.STORY_NOT_FOUND);
        }

        StoryResponse response = mapToStoryResponse(story);
        response.setChapterCount(chapterRepository.countByStoryIdAndPublishedTrue(id));
        return response;
    }

    /** Admin detail view: reachable regardless of visibility. */
    public StoryResponse getStoryByIdForAdmin(String id) {
        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));

        return mapToStoryResponse(story);
    }

    public StoryResponse updateStory(String id, StoryUpdateRequest request) {
        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));

        if (storyRepository.existsBySlugAndIdNot(request.getSlug(), id)) {
            throw new AppException(ErrorCode.SLUG_ALREADY_EXISTS);
        }

        story.setTitle(request.getTitle());
        story.setSlug(request.getSlug());
        story.setAuthor(request.getAuthor());
        story.setDescription(request.getDescription());
        story.setCoverUrl(request.getCoverUrl());
        story.setStatus(request.getStatus());
        story.setVisibility(request.getVisibility());
        // Null means "not submitted" — leave the existing assignment alone.
        if (request.getGenreIds() != null) {
            story.setGenres(resolveGenres(request.getGenreIds()));
        }

        return mapToStoryResponse(storyRepository.save(story));
    }

    @Transactional
    public void deleteStory(String id) {
        if (!storyRepository.existsById(id)) {
            throw new AppException(ErrorCode.STORY_NOT_FOUND);
        }
        // Children own the FK and Story declares no cascade, so dependants are
        // removed explicitly. Every new table referencing a story must be added here,
        // innermost first, or the delete fails on a constraint violation.
        commentReportRepository.deleteByStoryId(id);
        commentRepository.deleteByStoryId(id);
        ratingRepository.deleteByStoryId(id);
        readingProgressRepository.deleteByStoryId(id);
        followRepository.deleteByStoryId(id);
        chapterRepository.deleteByStoryId(id);
        // The many-to-many join table also owns a foreign key to stories. Delete
        // it explicitly so old MySQL schemas and lazy collection state cannot
        // leave a row that blocks the final story delete.
        storyRelationCleanupRepository.deleteGenreLinks(id);
        storyRepository.deleteById(id);
    }

    private StoryResponse mapToStoryResponse(Story story) {
        return StoryResponse.builder()
                .id(story.getId())
                .title(story.getTitle())
                .slug(story.getSlug())
                .author(story.getAuthor())
                .description(story.getDescription())
                .coverUrl(story.getCoverUrl())
                .status(story.getStatus())
                .visibility(story.getVisibility())
                .viewCount(story.getViewCount())
                .ratingAvg(story.getRatingAvg())
                .ratingCount(story.getRatingCount())
                .genres(story.getGenres().stream().map(GenreService::mapToResponse).toList())
                .createdAt(story.getCreatedAt())
                .updatedAt(story.getUpdatedAt())
                .build();
    }
}
