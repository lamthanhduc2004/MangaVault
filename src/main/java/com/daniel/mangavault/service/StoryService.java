package com.daniel.mangavault.service;

import com.daniel.mangavault.dto.request.StoryCreationRequest;
import com.daniel.mangavault.dto.request.StoryUpdateRequest;
import com.daniel.mangavault.dto.response.PageResponse;
import com.daniel.mangavault.dto.response.StoryResponse;
import com.daniel.mangavault.entity.Story;
import com.daniel.mangavault.enums.StoryStatus;
import com.daniel.mangavault.exception.AppException;
import com.daniel.mangavault.exception.ErrorCode;
import com.daniel.mangavault.repository.ChapterRepository;
import com.daniel.mangavault.repository.StoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class StoryService {
    private final StoryRepository storyRepository;
    private final ChapterRepository chapterRepository;

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
                .build();

        Story savedStory = storyRepository.save(story);

        return mapToStoryResponse(savedStory);
    }

    public PageResponse<StoryResponse> getStories(String keyword, StoryStatus status, String sort, int page, int size) {
        // "updated" surfaces recently-updated stories (home page); default is newest first.
        Sort sortSpec = "updated".equals(sort)
                ? Sort.by(Sort.Direction.DESC, "updatedAt")
                : Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sortSpec);

        String kw = StringUtils.hasText(keyword) ? keyword.trim() : "";
        Page<Story> storyPage = status != null
                ? storyRepository.findByTitleContainingIgnoreCaseAndStatus(kw, status, pageable)
                : storyRepository.findByTitleContainingIgnoreCase(kw, pageable);

        return PageResponse.from(storyPage, this::mapToStoryResponse);
    }

    public StoryResponse getStoryById(String id) {
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

        return mapToStoryResponse(storyRepository.save(story));
    }

    @Transactional
    public void deleteStory(String id) {
        if (!storyRepository.existsById(id)) {
            throw new AppException(ErrorCode.STORY_NOT_FOUND);
        }
        // Remove chapters first: Chapter owns the FK, and Story has no cascade mapping.
        chapterRepository.deleteByStoryId(id);
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
                .createdAt(story.getCreatedAt())
                .updatedAt(story.getUpdatedAt())
                .build();
    }
}
