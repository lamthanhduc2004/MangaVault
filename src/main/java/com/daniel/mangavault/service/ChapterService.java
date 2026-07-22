package com.daniel.mangavault.service;

import com.daniel.mangavault.dto.response.ChapterResponse;
import com.daniel.mangavault.dto.response.ChapterSummaryResponse;
import com.daniel.mangavault.entity.Chapter;
import com.daniel.mangavault.exception.AppException;
import com.daniel.mangavault.repository.ChapterRepository;
import com.daniel.mangavault.repository.StoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChapterService {
    private final ChapterRepository chapterRepository;
    private final StoryRepository storyRepository;

    public List<ChapterSummaryResponse> getChaptersOfStory(String storyId) {
        if (!storyRepository.existsById(storyId)) {
            throw new AppException("Story not found");
        }
        return chapterRepository.findByStoryIdOrderByChapterNumberAsc(storyId)
                .stream()
                .map(this::mapToSummary)
                .toList();
    }

    public ChapterResponse getChapterById(String id) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new AppException("Chapter not found"));

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
