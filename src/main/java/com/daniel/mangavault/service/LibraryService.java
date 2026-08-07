package com.daniel.mangavault.service;

import com.daniel.mangavault.dto.response.FollowedStoryResponse;
import com.daniel.mangavault.dto.response.PageResponse;
import com.daniel.mangavault.dto.response.ReadingHistoryResponse;
import com.daniel.mangavault.entity.Chapter;
import com.daniel.mangavault.entity.Follow;
import com.daniel.mangavault.entity.ReadingProgress;
import com.daniel.mangavault.entity.Story;
import com.daniel.mangavault.entity.User;
import com.daniel.mangavault.exception.AppException;
import com.daniel.mangavault.exception.ErrorCode;
import com.daniel.mangavault.repository.ChapterRepository;
import com.daniel.mangavault.repository.FollowRepository;
import com.daniel.mangavault.repository.ReadingProgressRepository;
import com.daniel.mangavault.repository.StoryRepository;
import com.daniel.mangavault.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Personal library: followed stories (F11), reading history (F12) and resume (F13). */
@Service
@RequiredArgsConstructor
public class LibraryService {
    private final FollowRepository followRepository;
    private final ReadingProgressRepository readingProgressRepository;
    private final StoryRepository storyRepository;
    private final ChapterRepository chapterRepository;
    private final CurrentUserProvider currentUserProvider;

    // --- Follows -------------------------------------------------------------

    public void followStory(String storyId) {
        User user = currentUserProvider.requireUser();
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));

        if (followRepository.existsByUserIdAndStoryId(user.getId(), storyId)) {
            throw new AppException(ErrorCode.ALREADY_FOLLOWED);
        }

        followRepository.save(Follow.builder().user(user).story(story).build());
    }

    public void unfollowStory(String storyId) {
        String userId = currentUserProvider.requireUserId();
        Follow follow = followRepository.findByUserIdAndStoryId(userId, storyId)
                .orElseThrow(() -> new AppException(ErrorCode.FOLLOW_NOT_FOUND));

        followRepository.delete(follow);
    }

    public boolean isFollowing(String storyId) {
        return followRepository.existsByUserIdAndStoryId(currentUserProvider.requireUserId(), storyId);
    }

    /**
     * Followed stories with their newest chapter and the reader's position.
     * Both extras are resolved with one batched query each rather than per row.
     */
    public PageResponse<FollowedStoryResponse> getFollowedStories(int page, int size) {
        String userId = currentUserProvider.requireUserId();
        Page<Follow> follows = followRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size));

        List<String> storyIds = follows.getContent().stream()
                .map(f -> f.getStory().getId())
                .toList();

        Map<String, Integer> latestChapterNumbers = latestChapterNumbers(storyIds);
        Map<String, ReadingProgress> progressByStory = storyIds.isEmpty()
                ? Map.of()
                : readingProgressRepository.findByUserIdAndStoryIds(userId, storyIds).stream()
                .collect(Collectors.toMap(p -> p.getStory().getId(), Function.identity()));

        return PageResponse.from(follows, follow -> {
            Story story = follow.getStory();
            ReadingProgress progress = progressByStory.get(story.getId());
            return FollowedStoryResponse.builder()
                    .storyId(story.getId())
                    .title(story.getTitle())
                    .slug(story.getSlug())
                    .coverUrl(story.getCoverUrl())
                    .author(story.getAuthor())
                    .status(story.getStatus())
                    .latestChapterNumber(latestChapterNumbers.get(story.getId()))
                    .lastReadChapterNumber(progress == null ? null : progress.getChapterNumber())
                    .lastReadChapterId(progress == null ? null : progress.getChapter().getId())
                    .storyUpdatedAt(story.getUpdatedAt())
                    .followedAt(follow.getCreatedAt())
                    .build();
        });
    }

    private Map<String, Integer> latestChapterNumbers(List<String> storyIds) {
        if (storyIds.isEmpty()) {
            return Map.of();
        }
        Map<String, Integer> result = new HashMap<>();
        for (Object[] row : chapterRepository.findLatestChapterNumberByStoryIds(storyIds)) {
            result.put((String) row[0], (Integer) row[1]);
        }
        return result;
    }

    // --- Reading progress ----------------------------------------------------

    /** Upsert: one row per (user, story), overwritten as the reader advances. */
    @Transactional
    public void saveProgress(String chapterId) {
        User user = currentUserProvider.requireUser();
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));

        String storyId = chapter.getStory().getId();
        ReadingProgress progress = readingProgressRepository
                .findByUserIdAndStoryId(user.getId(), storyId)
                .orElseGet(() -> ReadingProgress.builder()
                        .user(user)
                        .story(chapter.getStory())
                        .build());

        progress.setChapter(chapter);
        progress.setChapterNumber(chapter.getChapterNumber());

        readingProgressRepository.save(progress);
    }

    public PageResponse<ReadingHistoryResponse> getHistory(int page, int size) {
        String userId = currentUserProvider.requireUserId();
        Page<ReadingProgress> history = readingProgressRepository
                .findByUserIdOrderByUpdatedAtDesc(userId, PageRequest.of(page, size));

        return PageResponse.from(history, this::mapToHistory);
    }

    /** Resume position for one story, or null when the reader has not started it. */
    public ReadingHistoryResponse getProgressForStory(String storyId) {
        return readingProgressRepository
                .findByUserIdAndStoryId(currentUserProvider.requireUserId(), storyId)
                .map(this::mapToHistory)
                .orElse(null);
    }

    @Transactional
    public void deleteProgress(String storyId) {
        readingProgressRepository.deleteByUserIdAndStoryId(currentUserProvider.requireUserId(), storyId);
    }

    private ReadingHistoryResponse mapToHistory(ReadingProgress progress) {
        Story story = progress.getStory();
        Chapter chapter = progress.getChapter();
        return ReadingHistoryResponse.builder()
                .storyId(story.getId())
                .title(story.getTitle())
                .slug(story.getSlug())
                .coverUrl(story.getCoverUrl())
                .chapterId(chapter.getId())
                .chapterNumber(progress.getChapterNumber())
                .chapterTitle(chapter.getTitle())
                .readAt(progress.getUpdatedAt())
                .build();
    }

    /** Convenience for callers that only need to know whether a resume point exists. */
    public Optional<ReadingHistoryResponse> findProgress(String storyId) {
        return Optional.ofNullable(getProgressForStory(storyId));
    }
}
