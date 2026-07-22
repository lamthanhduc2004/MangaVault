package com.daniel.mangavault.service;

import com.daniel.mangavault.dto.request.StoryCreationRequest;
import com.daniel.mangavault.dto.response.PageResponse;
import com.daniel.mangavault.dto.response.StoryResponse;
import com.daniel.mangavault.entity.Story;
import com.daniel.mangavault.exception.AppException;
import com.daniel.mangavault.repository.StoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class StoryService {
    private final StoryRepository storyRepository;

    public StoryResponse createStory(StoryCreationRequest request) {
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

    public PageResponse<StoryResponse> getStories(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Story> storyPage = StringUtils.hasText(keyword)
                ? storyRepository.findByTitleContainingIgnoreCase(keyword.trim(), pageable)
                : storyRepository.findAll(pageable);

        return PageResponse.from(storyPage, this::mapToStoryResponse);
    }

    public StoryResponse getStoryById(String id) {
        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new AppException("Story not found"));

        return mapToStoryResponse(story);
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
