package com.daniel.mangavault;

import com.daniel.mangavault.entity.Chapter;
import com.daniel.mangavault.entity.Comment;
import com.daniel.mangavault.entity.Genre;
import com.daniel.mangavault.entity.Story;
import com.daniel.mangavault.entity.User;
import com.daniel.mangavault.enums.Role;
import com.daniel.mangavault.enums.StoryStatus;
import com.daniel.mangavault.enums.Visibility;
import com.daniel.mangavault.repository.ChapterRepository;
import com.daniel.mangavault.repository.CommentRepository;
import com.daniel.mangavault.repository.GenreRepository;
import com.daniel.mangavault.repository.StoryRepository;
import com.daniel.mangavault.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Covers the access rules and counters that are easy to break by accident. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StoryApiIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired StoryRepository storyRepository;
    @Autowired ChapterRepository chapterRepository;
    @Autowired CommentRepository commentRepository;
    @Autowired GenreRepository genreRepository;
    @Autowired UserRepository userRepository;
    @Autowired PasswordEncoder passwordEncoder;

    private Story publicStory;
    private Story hiddenStory;
    private Chapter publishedChapter;
    private Chapter draftChapter;
    private User reader;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        chapterRepository.deleteAll();
        storyRepository.deleteAll();
        genreRepository.deleteAll();

        Genre fantasy = genreRepository.save(Genre.builder()
                .name("Kỳ ảo").slug("ky-ao").description("Thế giới phép thuật").build());

        publicStory = storyRepository.save(Story.builder()
                .title("Truyện công khai").slug("truyen-cong-khai").author("Tác giả A")
                .status(StoryStatus.ONGOING).visibility(Visibility.PUBLIC)
                .genres(new LinkedHashSet<>(java.util.List.of(fantasy))).build());

        hiddenStory = storyRepository.save(Story.builder()
                .title("Truyện riêng tư").slug("truyen-rieng-tu").author("Tác giả B")
                .status(StoryStatus.ONGOING).visibility(Visibility.PRIVATE).build());

        publishedChapter = chapterRepository.save(Chapter.builder()
                .story(publicStory).chapterNumber(1).title("Chương 1")
                .content("Nội dung chương 1").published(true).build());

        draftChapter = chapterRepository.save(Chapter.builder()
                .story(publicStory).chapterNumber(2).title("Chương nháp")
                .content("Chưa công khai").published(false).build());

        reader = userRepository.findByUsername("reader-fixture").orElseGet(() ->
                userRepository.save(User.builder()
                        .username("reader-fixture")
                        .email("reader@example.com")
                        .password(passwordEncoder.encode("secret123"))
                        .role(Role.USER)
                        .build()));
    }

    @Test
    @DisplayName("Danh sách công khai không trả truyện PRIVATE")
    void publicListExcludesPrivateStories() throws Exception {
        mockMvc.perform(get("/api/stories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result.totalElements").value(1))
                .andExpect(jsonPath("$.result.items[0].slug").value("truyen-cong-khai"));
    }

    @Test
    @DisplayName("Truyện PRIVATE trả 404 chứ không phải 403 — không lộ sự tồn tại")
    void privateStoryDetailIsNotFound() throws Exception {
        mockMvc.perform(get("/api/stories/" + hiddenStory.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(4041));
    }

    @Test
    @DisplayName("Tìm kiếm khớp cả tên tác giả")
    void searchMatchesAuthorName() throws Exception {
        mockMvc.perform(get("/api/stories").param("keyword", "Tác giả A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.totalElements").value(1));
    }

    @Test
    @DisplayName("Tìm kiếm khớp tên thể loại")
    void searchMatchesGenreName() throws Exception {
        mockMvc.perform(get("/api/stories").param("keyword", "Kỳ ảo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.totalElements").value(1));
    }

    @Test
    @DisplayName("Đăng nhập chấp nhận email thay cho username")
    void loginAcceptsEmail() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"reader@example.com","password":"secret123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.username").value("reader-fixture"))
                .andExpect(jsonPath("$.result.token").isNotEmpty());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin ẩn bình luận thì API công khai không còn trả bình luận đó")
    void hiddenCommentIsExcludedFromPublicDiscussion() throws Exception {
        Comment comment = commentRepository.save(Comment.builder()
                .story(publicStory).user(reader).content("Nội dung cần ẩn").build());

        mockMvc.perform(patch("/api/admin/comments/" + comment.getId() + "/visibility")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"hidden\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.hidden").value(true));

        mockMvc.perform(get("/api/stories/" + publicStory.getId() + "/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.totalElements").value(0));
    }

    @Test
    @DisplayName("Mục lục chương bỏ qua chương chưa công khai")
    void chapterListHidesUnpublishedChapters() throws Exception {
        mockMvc.perform(get("/api/stories/" + publicStory.getId() + "/chapters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.length()").value(1))
                .andExpect(jsonPath("$.result[0].chapterNumber").value(1));
    }

    @Test
    @DisplayName("Chương chưa công khai không đọc được qua API công khai")
    void draftChapterIsNotReadable() throws Exception {
        mockMvc.perform(get("/api/chapters/" + draftChapter.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(4042));
    }

    @Test
    @DisplayName("Đọc chương tăng lượt xem nhưng KHÔNG đổi updatedAt của truyện")
    void readingIncrementsViewsWithoutTouchingUpdatedAt() throws Exception {
        LocalDateTime before = storyRepository.findById(publicStory.getId()).orElseThrow().getUpdatedAt();

        mockMvc.perform(get("/api/chapters/" + publishedChapter.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content").value("Nội dung chương 1"));

        Story reloaded = storyRepository.findById(publicStory.getId()).orElseThrow();
        assertThat(reloaded.getViewCount()).isEqualTo(1L);
        // A read must not push the story to the top of "recently updated".
        assertThat(reloaded.getUpdatedAt()).isEqualTo(before);
    }

    @Test
    @DisplayName("API admin từ chối khách chưa đăng nhập")
    void adminEndpointsRejectAnonymous() throws Exception {
        mockMvc.perform(get("/api/admin/stories"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(4010));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("API admin từ chối tài khoản USER")
    void adminEndpointsRejectPlainUser() throws Exception {
        mockMvc.perform(get("/api/admin/stories"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(4030));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Danh sách admin bao gồm cả truyện đang ẩn")
    void adminListIncludesHiddenStories() throws Exception {
        mockMvc.perform(get("/api/admin/stories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.totalElements").value(2));
    }

    @Test
    @DisplayName("Tham số enum sai trả 400 chứ không phải 500")
    void invalidEnumParameterIsBadRequest() throws Exception {
        mockMvc.perform(get("/api/stories").param("status", "KHONG_TON_TAI"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(4000));
    }
}
