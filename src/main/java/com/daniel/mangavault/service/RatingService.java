package com.daniel.mangavault.service;

import com.daniel.mangavault.dto.response.RatingSummaryResponse;
import com.daniel.mangavault.entity.Rating;
import com.daniel.mangavault.entity.Story;
import com.daniel.mangavault.entity.User;
import com.daniel.mangavault.exception.AppException;
import com.daniel.mangavault.exception.ErrorCode;
import com.daniel.mangavault.repository.RatingRepository;
import com.daniel.mangavault.repository.StoryRepository;
import com.daniel.mangavault.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Star ratings (F15). Each user holds at most one score per story. */
@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingRepository ratingRepository;
    private final StoryRepository storyRepository;
    private final CurrentUserProvider currentUserProvider;

    /** Upsert — re-rating replaces the previous score instead of adding a new one. */
    @Transactional
    public RatingSummaryResponse rateStory(String storyId, int score) {
        User user = currentUserProvider.requireUser();
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));

        Rating rating = ratingRepository.findByUserIdAndStoryId(user.getId(), storyId)
                .orElseGet(() -> Rating.builder().user(user).story(story).build());
        rating.setScore(score);
        ratingRepository.save(rating);

        return recomputeAggregate(storyId, score);
    }

    @Transactional
    public RatingSummaryResponse deleteRating(String storyId) {
        String userId = currentUserProvider.requireUserId();
        Rating rating = ratingRepository.findByUserIdAndStoryId(userId, storyId)
                .orElseThrow(() -> new AppException(ErrorCode.RATING_NOT_FOUND));

        ratingRepository.delete(rating);
        ratingRepository.flush(); // aggregate below must not see the deleted row

        return recomputeAggregate(storyId, null);
    }

    public RatingSummaryResponse getSummary(String storyId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));

        Integer myScore = null;
        try {
            myScore = ratingRepository.findByUserIdAndStoryId(currentUserProvider.requireUserId(), storyId)
                    .map(Rating::getScore)
                    .orElse(null);
        } catch (AppException ignored) {
            // Anonymous visitors still get the public average, just no personal score.
        }

        return RatingSummaryResponse.builder()
                .average(story.getRatingAvg())
                .count(story.getRatingCount())
                .myScore(myScore)
                .build();
    }

    /**
     * Refreshes the denormalized columns on the story. Written with a bulk update so
     * @UpdateTimestamp does not fire — rating a story is not a content update.
     */
    private RatingSummaryResponse recomputeAggregate(String storyId, Integer myScore) {
        Object[] row = ratingRepository.aggregateByStoryId(storyId);
        // Hibernate returns the projection nested one level deep for multi-column results.
        Object[] values = (row.length == 1 && row[0] instanceof Object[] nested) ? nested : row;

        double average = ((Number) values[0]).doubleValue();
        int count = ((Number) values[1]).intValue();
        double rounded = Math.round(average * 10.0) / 10.0;

        storyRepository.updateRatingAggregate(storyId, rounded, count);

        return RatingSummaryResponse.builder()
                .average(rounded)
                .count(count)
                .myScore(myScore)
                .build();
    }
}
