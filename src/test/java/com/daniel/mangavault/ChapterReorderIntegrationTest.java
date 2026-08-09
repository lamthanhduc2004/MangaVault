package com.daniel.mangavault;

import com.daniel.mangavault.entity.Chapter;
import com.daniel.mangavault.entity.Story;
import com.daniel.mangavault.enums.StoryStatus;
import com.daniel.mangavault.enums.Visibility;
import com.daniel.mangavault.repository.ChapterRepository;
import com.daniel.mangavault.repository.StoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** F17: reordering must swap chapterNumber without tripping the unique constraint. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ChapterReorderIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired StoryRepository storyRepository;
    @Autowired ChapterRepository chapterRepository;

    private Chapter chapter1;
    private Chapter chapter2;
    private Chapter chapter3;

    @BeforeEach
    void setUp() {
        chapterRepository.deleteAll();
        storyRepository.deleteAll();

        Story story = storyRepository.save(Story.builder()
                .title("Truyện đổi thứ tự").slug("truyen-doi-thu-tu")
                .status(StoryStatus.ONGOING).visibility(Visibility.PUBLIC).build());

        chapter1 = chapterRepository.save(Chapter.builder()
                .story(story).chapterNumber(1).title("Chương 1").content("...").published(true).build());
        chapter2 = chapterRepository.save(Chapter.builder()
                .story(story).chapterNumber(2).title("Chương 2").content("...").published(true).build());
        chapter3 = chapterRepository.save(Chapter.builder()
                .story(story).chapterNumber(3).title("Chương 3").content("...").published(true).build());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Đổi chương 2 lên trên hoán đổi số với chương 1")
    void moveUpSwapsWithPreviousChapter() throws Exception {
        mockMvc.perform(patch("/api/admin/chapters/" + chapter2.getId() + "/move")
                        .contentType("application/json")
                        .content("{\"direction\":\"UP\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000));

        assertThat(chapterRepository.findById(chapter1.getId()).orElseThrow().getChapterNumber()).isEqualTo(2);
        assertThat(chapterRepository.findById(chapter2.getId()).orElseThrow().getChapterNumber()).isEqualTo(1);
        assertThat(chapterRepository.findById(chapter3.getId()).orElseThrow().getChapterNumber()).isEqualTo(3);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Đổi chương đầu tiên lên trên là no-op, không lỗi")
    void moveUpAtTopIsNoOp() throws Exception {
        mockMvc.perform(patch("/api/admin/chapters/" + chapter1.getId() + "/move")
                        .contentType("application/json")
                        .content("{\"direction\":\"UP\"}"))
                .andExpect(status().isOk());

        assertThat(chapterRepository.findById(chapter1.getId()).orElseThrow().getChapterNumber()).isEqualTo(1);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Đổi chương xuống dưới hoán đổi đúng, không trùng số chương")
    void moveDownSwapsAndKeepsNumbersUnique() throws Exception {
        mockMvc.perform(patch("/api/admin/chapters/" + chapter2.getId() + "/move")
                        .contentType("application/json")
                        .content("{\"direction\":\"DOWN\"}"))
                .andExpect(status().isOk());

        assertThat(chapterRepository.findById(chapter2.getId()).orElseThrow().getChapterNumber()).isEqualTo(3);
        assertThat(chapterRepository.findById(chapter3.getId()).orElseThrow().getChapterNumber()).isEqualTo(2);

        long distinctNumbers = chapterRepository.findByStoryIdOrderByChapterNumberAsc(chapter1.getStory().getId())
                .stream().map(Chapter::getChapterNumber).distinct().count();
        assertThat(distinctNumbers).isEqualTo(3);
    }
}
