package com.daniel.mangavault.service;

import com.daniel.mangavault.dto.request.ChapterCreationRequest;
import com.daniel.mangavault.dto.request.ChapterUpdateRequest;
import com.daniel.mangavault.dto.response.ChapterResponse;
import com.daniel.mangavault.dto.response.ChapterSummaryResponse;
import com.daniel.mangavault.entity.Chapter;
import com.daniel.mangavault.entity.Story;
import com.daniel.mangavault.enums.Visibility;
import com.daniel.mangavault.exception.AppException;
import com.daniel.mangavault.exception.ErrorCode;
import com.daniel.mangavault.repository.ChapterRepository;
import com.daniel.mangavault.repository.ReadingProgressRepository;
import com.daniel.mangavault.repository.StoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChapterService {
    private final ChapterRepository chapterRepository;
    private final StoryRepository storyRepository;
    private final ReadingProgressRepository readingProgressRepository;

    /** Reader table of contents: published chapters of a public story only. */
    public List<ChapterSummaryResponse> getChaptersOfStory(String storyId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));
        if (story.getVisibility() != Visibility.PUBLIC) {
            throw new AppException(ErrorCode.STORY_NOT_FOUND);
        }
        return chapterRepository.findByStoryIdAndPublishedTrueOrderByChapterNumberAsc(storyId)
                .stream()
                .map(this::mapToSummary)
                .toList();
    }

    /** Admin table of contents: includes hidden chapters. */
    public List<ChapterSummaryResponse> getChaptersOfStoryForAdmin(String storyId) {
        if (!storyRepository.existsById(storyId)) {
            throw new AppException(ErrorCode.STORY_NOT_FOUND);
        }
        return chapterRepository.findByStoryIdOrderByChapterNumberAsc(storyId)
                .stream()
                .map(this::mapToSummary)
                .toList();
    }

    /**
     * Admin editor view: returns content regardless of publish state or story
     * visibility, and deliberately does not count a view.
     */
    public ChapterResponse getChapterByIdForAdmin(String id) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));

        return ChapterResponse.builder()
                .id(chapter.getId())
                .storyId(chapter.getStory().getId())
                .storyTitle(chapter.getStory().getTitle())
                .chapterNumber(chapter.getChapterNumber())
                .title(chapter.getTitle())
                .content(chapter.getContent())
                .createdAt(chapter.getCreatedAt())
                .updatedAt(chapter.getUpdatedAt())
                .build();
    }

    @Transactional
    public ChapterResponse getChapterById(String id) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));

        Story story = chapter.getStory();
        // Hidden chapter or hidden story reads as "not found" to the public API.
        if (!chapter.isPublished() || story.getVisibility() != Visibility.PUBLIC) {
            throw new AppException(ErrorCode.CHAPTER_NOT_FOUND);
        }

        String storyId = story.getId();

        // Nearest-neighbor lookup so navigation survives gaps in chapter numbering
        // (a deleted or unpublished middle chapter).
        String prevId = chapterRepository
                .findFirstByStoryIdAndPublishedTrueAndChapterNumberLessThanOrderByChapterNumberDesc(
                        storyId, chapter.getChapterNumber())
                .map(Chapter::getId)
                .orElse(null);
        String nextId = chapterRepository
                .findFirstByStoryIdAndPublishedTrueAndChapterNumberGreaterThanOrderByChapterNumberAsc(
                        storyId, chapter.getChapterNumber())
                .map(Chapter::getId)
                .orElse(null);

        // Reading a chapter counts as a view. Bulk update keeps the story's
        // updatedAt untouched so reads do not reorder "recently updated".
        storyRepository.incrementViewCount(storyId);

        return ChapterResponse.builder()
                .id(chapter.getId())
                .storyId(storyId)
                .storyTitle(story.getTitle())
                .chapterNumber(chapter.getChapterNumber())
                .title(chapter.getTitle())
                .content(chapter.getContent())
                .prevChapterId(prevId)
                .nextChapterId(nextId)
                .createdAt(chapter.getCreatedAt())
                .updatedAt(chapter.getUpdatedAt())
                .build();
    }

    @Transactional // chapter insert + story touch must succeed or fail together
    public ChapterSummaryResponse createChapter(String storyId, ChapterCreationRequest request) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));

        if (chapterRepository.existsByStoryIdAndChapterNumber(storyId, request.getChapterNumber())) {
            throw new AppException(ErrorCode.CHAPTER_NUMBER_ALREADY_EXISTS);
        }

        Chapter chapter = Chapter.builder()
                .story(story)
                .chapterNumber(request.getChapterNumber())
                .title(request.getTitle())
                .content(request.getContent())
                .published(true)
                .build();

        ChapterSummaryResponse response = mapToSummary(chapterRepository.save(chapter));

        // A new chapter counts as a story update so the story surfaces in
        // the "recently updated" home page sort. Marking the entity dirty
        // lets @UpdateTimestamp regenerate updatedAt on flush.
        story.setUpdatedAt(LocalDateTime.now());
        storyRepository.save(story);

        return response;
    }

    public ChapterSummaryResponse updateChapter(String id, ChapterUpdateRequest request) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));

        String storyId = chapter.getStory().getId();
        if (chapterRepository.existsByStoryIdAndChapterNumberAndIdNot(storyId, request.getChapterNumber(), id)) {
            throw new AppException(ErrorCode.CHAPTER_NUMBER_ALREADY_EXISTS);
        }

        chapter.setChapterNumber(request.getChapterNumber());
        chapter.setTitle(request.getTitle());
        chapter.setContent(request.getContent());

        return mapToSummary(chapterRepository.save(chapter));
    }

    public ChapterSummaryResponse setPublished(String id, boolean published) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));

        chapter.setPublished(published);

        return mapToSummary(chapterRepository.save(chapter));
    }

    @Transactional
    public void deleteChapter(String id) {
        if (!chapterRepository.existsById(id)) {
            throw new AppException(ErrorCode.CHAPTER_NOT_FOUND);
        }
        // Reading bookmarks point at this chapter and would break the FK.
        readingProgressRepository.deleteByChapterId(id);
        chapterRepository.deleteById(id);
    }

    private ChapterSummaryResponse mapToSummary(Chapter chapter) {
        return ChapterSummaryResponse.builder()
                .id(chapter.getId())
                .chapterNumber(chapter.getChapterNumber())
                .title(chapter.getTitle())
                .published(chapter.isPublished())
                .createdAt(chapter.getCreatedAt())
                .updatedAt(chapter.getUpdatedAt())
                .build();
    }
}
