package com.daniel.mangavault;

import com.daniel.mangavault.entity.Chapter;
import com.daniel.mangavault.entity.Comment;
import com.daniel.mangavault.entity.CommentReport;
import com.daniel.mangavault.entity.Follow;
import com.daniel.mangavault.entity.Genre;
import com.daniel.mangavault.entity.Rating;
import com.daniel.mangavault.entity.ReadingProgress;
import com.daniel.mangavault.entity.Story;
import com.daniel.mangavault.entity.User;
import com.daniel.mangavault.enums.Role;
import com.daniel.mangavault.enums.StoryStatus;
import com.daniel.mangavault.enums.Visibility;
import com.daniel.mangavault.repository.ChapterRepository;
import com.daniel.mangavault.repository.CommentReportRepository;
import com.daniel.mangavault.repository.CommentRepository;
import com.daniel.mangavault.repository.FollowRepository;
import com.daniel.mangavault.repository.GenreRepository;
import com.daniel.mangavault.repository.RatingRepository;
import com.daniel.mangavault.repository.ReadingProgressRepository;
import com.daniel.mangavault.repository.StoryRepository;
import com.daniel.mangavault.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.LinkedHashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StoryDeletionIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired StoryRepository storyRepository;
    @Autowired ChapterRepository chapterRepository;
    @Autowired FollowRepository followRepository;
    @Autowired ReadingProgressRepository readingProgressRepository;
    @Autowired RatingRepository ratingRepository;
    @Autowired CommentRepository commentRepository;
    @Autowired CommentReportRepository commentReportRepository;
    @Autowired GenreRepository genreRepository;
    @Autowired UserRepository userRepository;

    @BeforeEach
    void cleanRelatedData() {
        commentReportRepository.deleteAll();
        commentRepository.deleteAll();
        ratingRepository.deleteAll();
        readingProgressRepository.deleteAll();
        followRepository.deleteAll();
        chapterRepository.deleteAll();
        storyRepository.deleteAll();
        genreRepository.deleteAll();
        userRepository.findByUsername("delete-fixture").ifPresent(userRepository::delete);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin xóa truyện cùng toàn bộ dữ liệu phụ thuộc")
    void deleteStoryRemovesEveryDependency() throws Exception {
        User reader = userRepository.save(User.builder()
                .username("delete-fixture")
                .email("delete-fixture@example.com")
                .password("not-used-in-this-test")
                .role(Role.USER)
                .build());
        Genre genre = genreRepository.save(Genre.builder()
                .name("Thể loại xóa")
                .slug("the-loai-xoa")
                .build());
        Story story = storyRepository.save(Story.builder()
                .title("Truyện cần xóa")
                .slug("truyen-can-xoa")
                .status(StoryStatus.ONGOING)
                .visibility(Visibility.PUBLIC)
                .genres(new LinkedHashSet<>(List.of(genre)))
                .build());
        Chapter chapter = chapterRepository.save(Chapter.builder()
                .story(story)
                .chapterNumber(1)
                .title("Chương 1")
                .content("Nội dung")
                .published(true)
                .build());
        followRepository.save(Follow.builder().user(reader).story(story).build());
        ratingRepository.save(Rating.builder().user(reader).story(story).score(5).build());
        readingProgressRepository.save(ReadingProgress.builder()
                .user(reader)
                .story(story)
                .chapter(chapter)
                .chapterNumber(1)
                .build());
        Comment comment = commentRepository.save(Comment.builder()
                .user(reader)
                .story(story)
                .content("Bình luận")
                .build());
        commentReportRepository.save(CommentReport.builder()
                .user(reader)
                .comment(comment)
                .reason("Báo cáo thử")
                .build());

        mockMvc.perform(delete("/api/admin/stories/" + story.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000));

        assertThat(storyRepository.existsById(story.getId())).isFalse();
        assertThat(chapterRepository.count()).isZero();
        assertThat(followRepository.count()).isZero();
        assertThat(readingProgressRepository.count()).isZero();
        assertThat(ratingRepository.count()).isZero();
        assertThat(commentRepository.count()).isZero();
        assertThat(commentReportRepository.count()).isZero();
        assertThat(genreRepository.existsById(genre.getId())).isTrue();
    }
}
