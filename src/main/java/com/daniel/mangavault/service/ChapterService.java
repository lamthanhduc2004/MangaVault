package com.daniel.mangavault.service;

import com.daniel.mangavault.dto.request.ChapterCreationRequest;
import com.daniel.mangavault.dto.request.ChapterUpdateRequest;
import com.daniel.mangavault.dto.response.ChapterResponse;
import com.daniel.mangavault.dto.response.ChapterSummaryResponse;
import com.daniel.mangavault.entity.Chapter;
import com.daniel.mangavault.entity.Story;
import com.daniel.mangavault.exception.AppException;
import com.daniel.mangavault.exception.ErrorCode;
import com.daniel.mangavault.repository.ChapterRepository;
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

    public List<ChapterSummaryResponse> getChaptersOfStory(String storyId) {
        if (!storyRepository.existsById(storyId)) {
            throw new AppException(ErrorCode.STORY_NOT_FOUND);
        }
        return chapterRepository.findByStoryIdOrderByChapterNumberAsc(storyId)
                .stream()
                .map(this::mapToSummary)
                .toList();
    }

    public ChapterResponse getChapterById(String id) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));

        // Nearest-neighbor lookup so navigation survives gaps in chapter numbering
        // (e.g. a deleted middle chapter).
        String storyId = chapter.getStory().getId();
        String prevId = chapterRepository
                .findFirstByStoryIdAndChapterNumberLessThanOrderByChapterNumberDesc(storyId, chapter.getChapterNumber())
                .map(Chapter::getId)
                .orElse(null);
        String nextId = chapterRepository
                .findFirstByStoryIdAndChapterNumberGreaterThanOrderByChapterNumberAsc(storyId, chapter.getChapterNumber())
                .map(Chapter::getId)
                .orElse(null);

        return ChapterResponse.builder()
                .id(chapter.getId())
                .storyId(storyId)
                .storyTitle(chapter.getStory().getTitle())
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

    public void deleteChapter(String id) {
        if (!chapterRepository.existsById(id)) {
            throw new AppException(ErrorCode.CHAPTER_NOT_FOUND);
        }
        chapterRepository.deleteById(id);
    }

    private ChapterSummaryResponse mapToSummary(Chapter chapter) {
        return ChapterSummaryResponse.builder()
                .id(chapter.getId())
                .chapterNumber(chapter.getChapterNumber())
                .title(chapter.getTitle())
                .createdAt(chapter.getCreatedAt())
                .updatedAt(chapter.getUpdatedAt())
                .build();
    }
}
