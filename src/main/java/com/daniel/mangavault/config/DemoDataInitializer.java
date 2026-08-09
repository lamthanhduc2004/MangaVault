package com.daniel.mangavault.config;

import com.daniel.mangavault.entity.Chapter;
import com.daniel.mangavault.entity.Genre;
import com.daniel.mangavault.entity.Story;
import com.daniel.mangavault.enums.StoryStatus;
import com.daniel.mangavault.enums.Visibility;
import com.daniel.mangavault.repository.ChapterRepository;
import com.daniel.mangavault.repository.GenreRepository;
import com.daniel.mangavault.repository.StoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Seeds a small catalogue the first time the application starts against an empty
 * database, so a freshly provisioned deployment is demonstrable without manual SQL.
 * Runs only when the stories table is empty — it never touches existing data.
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "app.demo.seed", havingValue = "true", matchIfMissing = true)
public class DemoDataInitializer {

    private record SeedChapter(int number, String title, String content) {}

    private record SeedStory(String title, String slug, String author, String description,
                             StoryStatus status, List<SeedChapter> chapters) {}

    @Bean
    CommandLineRunner seedDemoData(StoryRepository storyRepository, ChapterRepository chapterRepository,
                                   GenreRepository genreRepository, PlatformTransactionManager txManager) {
        return args -> {
            if (storyRepository.count() == 0) {
                log.info("Empty catalogue detected — seeding demo stories.");

                for (SeedStory seed : catalogue()) {
                    Story story = storyRepository.save(Story.builder()
                            .title(seed.title())
                            .slug(seed.slug())
                            .author(seed.author())
                            .description(seed.description())
                            .status(seed.status())
                            .visibility(Visibility.PUBLIC)
                            .build());

                    for (SeedChapter chapter : seed.chapters()) {
                        chapterRepository.save(Chapter.builder()
                                .story(story)
                                .chapterNumber(chapter.number())
                                .title(chapter.title())
                                .content(chapter.content())
                                .published(true)
                                .build());
                    }
                }

                log.info("Seeded {} demo stories.", catalogue().size());
            }

            // Genres were added in a later feature pass. Kept as a separate,
            // independently-guarded step (rather than folded into the block above)
            // so it still runs — and backfills the existing demo stories — against a
            // database that was seeded before genres existed, without re-touching
            // story/chapter content.
            //
            // Run inside one explicit transaction (TransactionTemplate, not
            // @Transactional): a CommandLineRunner executes before any web request,
            // so there is no Open-Session-In-View to keep entities attached between
            // separate repository calls. Fetching a Story in one call and mutating
            // its lazy `genres` collection in a later, separate call operates on a
            // detached entity — the change is silently lost on save() instead of
            // being flushed. A single transaction keeps every entity managed for
            // the whole block, so plain field mutation is enough; Hibernate's
            // dirty-checking flushes it on commit.
            new TransactionTemplate(txManager).executeWithoutResult(status ->
                    seedGenres(storyRepository, genreRepository));
        };
    }

    /** name -> slug for the demo catalogue's genres. */
    private static final Map<String, String> GENRES = Map.of(
            "Tiên hiệp", "tien-hiep",
            "Huyền huyễn", "huyen-huyen",
            "Kiếm hiệp", "kiem-hiep",
            "Đô thị", "do-thi",
            "Trọng sinh", "trong-sinh",
            "Dị giới", "di-gioi"
    );

    /** slug -> genre slugs assigned to that demo story. */
    private static final Map<String, List<String>> STORY_GENRES = Map.of(
            "dau-pha-thuong-khung", List.of("tien-hiep", "huyen-huyen"),
            "toan-chuc-cao-thu", List.of("do-thi"),
            "pham-nhan-tu-tien", List.of("tien-hiep"),
            "than-dao-dan-ton", List.of("tien-hiep", "trong-sinh"),
            "vu-dong-can-khon", List.of("huyen-huyen", "di-gioi"),
            "tien-nghich", List.of("tien-hiep", "kiem-hiep")
    );

    private void seedGenres(StoryRepository storyRepository, GenreRepository genreRepository) {
        if (genreRepository.count() == 0) {
            log.info("No genres found — seeding the demo genre catalogue.");
            GENRES.forEach((name, slug) -> genreRepository.save(Genre.builder().name(name).slug(slug).build()));
        }

        Map<String, Genre> genresBySlug = genreRepository.findAll().stream()
                .collect(java.util.stream.Collectors.toMap(Genre::getSlug, g -> g));

        int assigned = 0;
        for (Story story : storyRepository.findAll()) {
            List<String> genreSlugs = STORY_GENRES.get(story.getSlug());
            if (genreSlugs == null || !story.getGenres().isEmpty()) {
                continue; // not a known demo story, or already assigned — don't clobber admin edits
            }

            Set<Genre> genres = new LinkedHashSet<>();
            for (String genreSlug : genreSlugs) {
                Genre genre = genresBySlug.get(genreSlug);
                if (genre != null) {
                    genres.add(genre);
                }
            }
            story.setGenres(genres);
            assigned++;
        }
        // No explicit save() needed: `story` is a managed entity inside the
        // enclosing transaction, so Hibernate flushes the mutation on commit.

        if (assigned > 0) {
            log.info("Backfilled genres onto {} demo stories.", assigned);
        }
    }

    private static List<SeedStory> catalogue() {
        return List.of(
                new SeedStory("Đấu Phá Thương Khung", "dau-pha-thuong-khung", "Thiên Tằm Thổ Đậu",
                        "Tiêu Viêm, thiên tài tu luyện sa sút, bắt đầu hành trình lấy lại vinh quang cùng linh hồn Dược lão trong chiếc nhẫn cổ.",
                        StoryStatus.COMPLETED, List.of(
                        new SeedChapter(1, "Thiên tài sa sút", """
                                Tiêu Viêm đứng trước đài thí luyện, ánh mắt mọi người đổ dồn về phía hắn.
                                Ba năm trước, hắn là thiên tài trẻ tuổi nhất của Tiêu gia. Ba năm sau, hắn chỉ còn là kẻ bị chê cười.
                                "Đấu chi khí, tam đoạn." Giọng trưởng lão vang lên khô khốc. Đám đông cười ồ.
                                Tiêu Viêm nắm chặt tay. Hắn biết, trong nhẫn giới kia, một linh hồn già nua vừa thức tỉnh."""),
                        new SeedChapter(2, "Dược lão", """
                                Đêm khuya, Tiêu Viêm ngồi trong phòng, chiếc nhẫn cổ trên tay chợt phát sáng.
                                "Tiểu tử, ngươi muốn mạnh lên không?" Một bóng hình già nua hiện ra từ chiếc nhẫn.
                                Dược Trần — Dược lão — luyện dược sư đỉnh cấp của đại lục, linh hồn trú ngụ trong nhẫn.
                                Từ đêm đó, vận mệnh của Tiêu Viêm thay đổi."""),
                        new SeedChapter(3, "Thoái hôn", """
                                Nạp Lan Yên Nhiên đến Tiêu gia, mang theo hôn ước ba năm trước.
                                "Tiêu Viêm, ngươi không xứng với ta nữa." Nàng nói, giọng lạnh nhạt.
                                Tiêu Viêm ngẩng đầu, ánh mắt bình tĩnh đến lạ: "Ba năm sau, ta sẽ đến Vân Lam Tông."
                                Lời hẹn ba năm, chấn động cả thành Ô Thản."""),
                        new SeedChapter(4, "Hẹn ước Vân Lam Tông", """
                                Ba năm sau, Tiêu Viêm rời thành Ô Thản, hướng về Vân Lam Tông.
                                Lời hẹn năm xưa, hôm nay đến lúc thực hiện.
                                Trên lưng hắn là thanh Huyền Trọng Xích, trong tay là ngọn Dị Hỏa cháy rực."""))),

                new SeedStory("Toàn Chức Cao Thủ", "toan-chuc-cao-thu", "Hồ Điệp Lam",
                        "Diệp Tu, cao thủ Vinh Quang bị ép rời đội, làm lại từ đầu tại một quán net nhỏ với tài khoản vô danh.",
                        StoryStatus.ONGOING, List.of(
                        new SeedChapter(1, "Bị trục xuất", """
                                Diệp Tu nhìn bản hợp đồng giải ước trên bàn, khẽ cười.
                                Mười năm gắn bó với Gia Thế, đổi lại là một câu "đội cần hình tượng mới".
                                Hắn đặt tài khoản Nhất Diệp Chi Thu xuống, đứng dậy rời khỏi câu lạc bộ.
                                Đêm đó, quán net Hưng Hân có thêm một quản lý ca đêm."""),
                        new SeedChapter(2, "Quân Mạc Tiếu", """
                                Máy chủ thứ mười của Vinh Quang mở, Diệp Tu tạo nhân vật mới.
                                Tán Nhân, vũ khí thiên bút — Quân Mạc Tiếu chính thức xuất hiện.
                                Những kỷ lục dungeon lần lượt bị phá. Cả máy chủ chấn động vì một tân thủ bí ẩn."""),
                        new SeedChapter(3, "Đêm trắng bên bàn phím", """
                                Trần Quả nhìn màn hình, không tin nổi những gì mình vừa thấy.
                                "Anh... anh là ai vậy?" Cô lắp bắp hỏi người quản lý ca đêm.
                                Diệp Tu nhấp một ngụm nước, thản nhiên: "Một người chơi game thôi.\""""))),

                new SeedStory("Phàm Nhân Tu Tiên", "pham-nhan-tu-tien", "Vong Ngữ",
                        "Hàn Lập, một thiếu niên bình thường nơi thôn quê, từng bước bước vào thế giới tu tiên tàn khốc bằng sự cẩn trọng và kiên nhẫn.",
                        StoryStatus.COMPLETED, List.of(
                        new SeedChapter(1, "Thôn nhỏ Thanh Ngưu", """
                                Hàn Lập sinh ra ở thôn Thanh Ngưu, nơi cả đời người ta chỉ biết đến ruộng đồng.
                                Năm mười ba tuổi, hắn theo chú lên trấn, lần đầu nghe hai chữ "tu tiên".
                                Trong lòng đứa trẻ ấy, một hạt giống lặng lẽ nảy mầm."""),
                        new SeedChapter(2, "Thất Huyền Môn", """
                                Vượt qua kỳ khảo hạch, Hàn Lập trở thành đệ tử ngoại môn Thất Huyền Môn.
                                Hắn không phải người có tư chất tốt nhất, nhưng là người cẩn thận nhất.
                                "Sống sót đã, rồi mới nói đến chuyện tu tiên." Hắn tự nhủ."""),
                        new SeedChapter(3, "Bình nhỏ thần bí", """
                                Chiếc bình xanh nhỏ bé ấy có thể thúc đẩy dược thảo sinh trưởng.
                                Hàn Lập giấu kín bí mật này, không nói với bất kỳ ai.
                                Trong giới tu tiên, kẻ khoe khoang thường chết sớm."""))),

                new SeedStory("Thần Đạo Đan Tôn", "than-dao-dan-ton", "Cô Đơn Địa Phi",
                        "Một đan sư bị hãm hại trùng sinh về quá khứ, mang theo ký ức cả đời để viết lại vận mệnh của mình và gia tộc.",
                        StoryStatus.ONGOING, List.of(
                        new SeedChapter(1, "Trùng sinh", """
                                Lý Nhàn Ngư mở mắt, thấy trần nhà quen thuộc của mười năm trước.
                                Hắn đã chết một lần, chết dưới tay chính người mình tin tưởng nhất.
                                Lần này, hắn sẽ không đi lại con đường cũ."""),
                        new SeedChapter(2, "Lò đan đầu tiên", """
                                Ngọn lửa bùng lên trong lò đan cũ kỹ, mùi dược thảo lan khắp gian phòng nhỏ.
                                Kỹ thuật của một đan sư đỉnh cấp, đặt trong thân thể một thiếu niên mười sáu tuổi.
                                Viên đan đầu tiên thành hình, tỏa ánh sáng dịu nhẹ."""))),

                new SeedStory("Vũ Động Càn Khôn", "vu-dong-can-khon", "Thiên Tằm Thổ Đậu",
                        "Lâm Động mang trong mình Tổ Phù bí ẩn, từ một chi phái suy tàn vươn lên giữa đại lục đầy cường giả.",
                        StoryStatus.HIATUS, List.of(
                        new SeedChapter(1, "Thạch phù", """
                                Trong hang động tối tăm, Lâm Động nhặt được một khối đá kỳ lạ.
                                Nó lạnh như băng, và khi chạm vào, trong đầu hắn vang lên tiếng gầm của một con thú cổ xưa.
                                Đó là Tổ Phù đầu tiên."""),
                        new SeedChapter(2, "Chi phái suy tàn", """
                                Lâm gia chia làm hai, chi phái của Lâm Động ngày một lụn bại.
                                "Muốn người khác tôn trọng, trước hết phải đủ mạnh." Cha hắn nói.
                                Lâm Động ghi nhớ câu đó suốt đời."""))),

                new SeedStory("Tiên Nghịch", "tien-nghich", "Nhĩ Căn",
                        "Vương Lâm, một thiếu niên tư chất tầm thường, dùng ý chí sắt đá nghịch thiên cải mệnh giữa con đường tu tiên khắc nghiệt.",
                        StoryStatus.COMPLETED, List.of(
                        new SeedChapter(1, "Trắc nghiệm linh căn", """
                                Vương Lâm đứng cuối hàng, nhìn những đứa trẻ khác lần lượt bước lên trắc linh thạch.
                                Đến lượt hắn, viên đá chỉ sáng lên một chút rồi tắt lịm.
                                "Ngũ linh căn." Vị trưởng lão lắc đầu."""),
                        new SeedChapter(2, "Nghịch thiên", """
                                Người ta nói ngũ linh căn thì cả đời không thể trúc cơ.
                                Vương Lâm không tin. Hắn tu luyện gấp mười lần người khác.
                                "Ta tên Vương Lâm. Ta muốn nghịch thiên.\"""")))
        );
    }
}
