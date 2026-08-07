package com.daniel.mangavault.service;

import com.daniel.mangavault.dto.response.StatsResponse;
import com.daniel.mangavault.dto.response.StoryResponse;
import com.daniel.mangavault.entity.Story;
import com.daniel.mangavault.repository.ChapterRepository;
import com.daniel.mangavault.repository.CommentRepository;
import com.daniel.mangavault.repository.FollowRepository;
import com.daniel.mangavault.repository.StoryRepository;
import com.daniel.mangavault.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/** Dashboard aggregates (F21). Read-only. */
@Service
@RequiredArgsConstructor
public class StatsService {
    private final UserRepository userRepository;
    private final StoryRepository storyRepository;
    private final ChapterRepository chapterRepository;
    private final CommentRepository commentRepository;
    private final FollowRepository followRepository;

    private static final int TOP_SIZE = 5;

    public StatsResponse getStats() {
        List<String> topFollowedIds = followRepository.findTopFollowedStoryIds(PageRequest.of(0, TOP_SIZE));

        return StatsResponse.builder()
                .totalUsers(userRepository.count())
                .totalStories(storyRepository.count())
                .totalChapters(chapterRepository.count())
                .totalComments(commentRepository.count())
                .storiesUpdatedLast7Days(storyRepository.countByUpdatedAtAfter(LocalDateTime.now().minusDays(7)))
                .topViewedStories(storyRepository
                        .findAll(PageRequest.of(0, TOP_SIZE, Sort.by(Sort.Direction.DESC, "viewCount")))
                        .map(StatsService::mapToBrief)
                        .getContent())
                .topFollowedStories(loadInIdOrder(topFollowedIds))
                .build();
    }

    /**
     * findAllById loses the ranking, so the rows are reordered to match the id list
     * that came back from the grouped follow count.
     */
    private List<StoryResponse> loadInIdOrder(List<String> orderedIds) {
        if (orderedIds.isEmpty()) {
            return List.of();
        }
        Map<String, Story> byId = storyRepository.findAllById(orderedIds).stream()
                .collect(java.util.stream.Collectors.toMap(Story::getId, Function.identity()));

        return orderedIds.stream()
                .map(byId::get)
                .filter(java.util.Objects::nonNull)
                .map(StatsService::mapToBrief)
                .sorted(Comparator.comparingInt(s -> orderedIds.indexOf(s.getId())))
                .toList();
    }

    /** Slim projection — the dashboard only needs enough to render a link. */
    private static StoryResponse mapToBrief(Story story) {
        return StoryResponse.builder()
                .id(story.getId())
                .title(story.getTitle())
                .slug(story.getSlug())
                .coverUrl(story.getCoverUrl())
                .author(story.getAuthor())
                .status(story.getStatus())
                .viewCount(story.getViewCount())
                .ratingAvg(story.getRatingAvg())
                .ratingCount(story.getRatingCount())
                .build();
    }
}
