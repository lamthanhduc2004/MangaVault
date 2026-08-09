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
import java.util.stream.Collectors;

/**
 * Seeds and backfills a demo catalogue so any deployment — fresh or already
 * populated — is demonstrable without manual SQL.
 * <p>
 * Every step here is idempotent and keyed by slug/name rather than "does the
 * table have zero rows": that coarse guard is how the genre-assignment step
 * silently never ran against the already-seeded production database earlier
 * (see git history). Checking each item individually means adding a new demo
 * story, genre, or cover image later just means adding it to the list below —
 * it backfills into any existing deployment on the next restart, and never
 * touches a row that already has real (possibly admin-edited) data.
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "app.demo.seed", havingValue = "true", matchIfMissing = true)
public class DemoDataInitializer {

    private record SeedChapter(int number, String title, String content) {}

    private record SeedStory(String title, String slug, String author, String description,
                             StoryStatus status, String coverSeed, List<String> genreSlugs,
                             List<SeedChapter> chapters) {}

    @Bean
    CommandLineRunner seedDemoData(StoryRepository storyRepository, ChapterRepository chapterRepository,
                                   GenreRepository genreRepository, PlatformTransactionManager txManager) {
        // Run inside one explicit transaction (TransactionTemplate, not
        // @Transactional): a CommandLineRunner executes before any web request, so
        // there is no Open-Session-In-View to keep entities attached between
        // separate repository calls made from within the same method. Fetching an
        // entity in one call and mutating a lazy association in a later, separate
        // call operates on a detached instance — the change is silently lost on
        // save() instead of being flushed. A single transaction keeps every entity
        // managed for the whole block, so plain field mutation is enough;
        // Hibernate's dirty-checking flushes it on commit.
        return args -> new TransactionTemplate(txManager).executeWithoutResult(status -> {
            seedGenres(genreRepository);
            seedStories(storyRepository, chapterRepository, genreRepository);
            backfillCovers(storyRepository);
        });
    }

    private static final Map<String, String> GENRES = Map.ofEntries(
            Map.entry("Tiên hiệp", "tien-hiep"),
            Map.entry("Huyền huyễn", "huyen-huyen"),
            Map.entry("Kiếm hiệp", "kiem-hiep"),
            Map.entry("Đô thị", "do-thi"),
            Map.entry("Trọng sinh", "trong-sinh"),
            Map.entry("Dị giới", "di-gioi"),
            Map.entry("Ngôn tình", "ngon-tinh"),
            Map.entry("Khoa huyễn", "khoa-huyen")
    );

    private void seedGenres(GenreRepository genreRepository) {
        int created = 0;
        for (Map.Entry<String, String> entry : GENRES.entrySet()) {
            if (!genreRepository.existsBySlug(entry.getValue())) {
                genreRepository.save(Genre.builder().name(entry.getKey()).slug(entry.getValue()).build());
                created++;
            }
        }
        if (created > 0) {
            log.info("Seeded {} demo genres.", created);
        }
    }

    private void seedStories(StoryRepository storyRepository, ChapterRepository chapterRepository,
                             GenreRepository genreRepository) {
        Map<String, Genre> genresBySlug = genreRepository.findAll().stream()
                .collect(Collectors.toMap(Genre::getSlug, g -> g));

        int created = 0;
        for (SeedStory seed : catalogue()) {
            if (storyRepository.existsBySlug(seed.slug())) {
                continue; // already present (fresh seed or a prior partial run) — leave it alone
            }

            Set<Genre> genres = new LinkedHashSet<>();
            for (String genreSlug : seed.genreSlugs()) {
                Genre genre = genresBySlug.get(genreSlug);
                if (genre != null) {
                    genres.add(genre);
                }
            }

            Story story = storyRepository.save(Story.builder()
                    .title(seed.title())
                    .slug(seed.slug())
                    .author(seed.author())
                    .description(seed.description())
                    .status(seed.status())
                    .visibility(Visibility.PUBLIC)
                    .coverUrl(coverUrlFor(seed.title(), seed.slug()))
                    .genres(genres)
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
            created++;
        }
        if (created > 0) {
            log.info("Seeded {} demo stories.", created);
        }
    }

    /**
     * Fills in a cover for any story that doesn't have one yet — including the
     * original demo stories seeded before cover art was part of the catalogue.
     */
    private void backfillCovers(StoryRepository storyRepository) {
        int filled = 0;
        for (Story story : storyRepository.findAll()) {
            if (story.getCoverUrl() == null || story.getCoverUrl().isBlank()) {
                story.setCoverUrl(coverUrlFor(story.getTitle(), story.getSlug()));
                filled++;
            }
        }
        if (filled > 0) {
            log.info("Backfilled cover images onto {} stories.", filled);
        }
    }

    // A small, high-contrast palette; each story gets one color deterministically
    // from its slug, so the same story always renders the same cover.
    private static final String[] COVER_COLORS = {
            "4f46e5", "0891b2", "b45309", "be185d", "15803d", "7c3aed", "b91c1c", "0f766e"
    };

    /**
     * Builds a title-card cover via placehold.co (free placeholder-image service):
     * a solid color background with the story title rendered as text. Earlier this
     * used Lorem Picsum's random stock photos, but a landscape/object photo picked
     * by hash has no relationship to the story at all and reads as simply wrong
     * next to the title. A title card is honestly a placeholder — not official
     * cover art — without pretending to depict anything.
     */
    private static String coverUrlFor(String title, String slug) {
        String color = COVER_COLORS[Math.floorMod(slug.hashCode(), COVER_COLORS.length)];
        String encodedTitle = java.net.URLEncoder.encode(title, java.nio.charset.StandardCharsets.UTF_8)
                .replace("+", "%20");
        return "https://placehold.co/480x680/" + color + "/ffffff/png?text=" + encodedTitle + "&font=roboto";
    }

    private static List<SeedStory> catalogue() {
        return List.of(
                new SeedStory("Đấu Phá Thương Khung", "dau-pha-thuong-khung", "Thiên Tằm Thổ Đậu",
                        "Tiêu Viêm, thiên tài tu luyện sa sút, bắt đầu hành trình lấy lại vinh quang cùng linh hồn Dược lão trong chiếc nhẫn cổ.",
                        StoryStatus.COMPLETED, "dau-pha-thuong-khung", List.of("tien-hiep", "huyen-huyen"), List.of(
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
                        StoryStatus.ONGOING, "toan-chuc-cao-thu", List.of("do-thi"), List.of(
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
                        StoryStatus.COMPLETED, "pham-nhan-tu-tien", List.of("tien-hiep"), List.of(
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
                        StoryStatus.ONGOING, "than-dao-dan-ton", List.of("tien-hiep", "trong-sinh"), List.of(
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
                        StoryStatus.HIATUS, "vu-dong-can-khon", List.of("huyen-huyen", "di-gioi"), List.of(
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
                        StoryStatus.COMPLETED, "tien-nghich", List.of("tien-hiep", "kiem-hiep"), List.of(
                        new SeedChapter(1, "Trắc nghiệm linh căn", """
                                Vương Lâm đứng cuối hàng, nhìn những đứa trẻ khác lần lượt bước lên trắc linh thạch.
                                Đến lượt hắn, viên đá chỉ sáng lên một chút rồi tắt lịm.
                                "Ngũ linh căn." Vị trưởng lão lắc đầu."""),
                        new SeedChapter(2, "Nghịch thiên", """
                                Người ta nói ngũ linh căn thì cả đời không thể trúc cơ.
                                Vương Lâm không tin. Hắn tu luyện gấp mười lần người khác.
                                "Ta tên Vương Lâm. Ta muốn nghịch thiên.\""""))),

                new SeedStory("Ánh Trăng Nơi Phố Cổ", "anh-trang-noi-pho-co", "Lam Tử Nhiên",
                        "Một kiến trúc sư trẻ trở về phố cổ Hội An để trùng tu căn nhà của bà ngoại, và tình cờ gặp lại mối tình đầu thời đại học.",
                        StoryStatus.ONGOING, "anh-trang-noi-pho-co", List.of("ngon-tinh", "do-thi"), List.of(
                        new SeedChapter(1, "Trở về", """
                                Minh Anh xuống xe trước con hẻm nhỏ lát đá, va li kéo lộc cộc trên nền gạch cũ.
                                Đã năm năm cô chưa quay lại phố cổ, kể từ ngày bà ngoại mất và cô rời đi học kiến trúc ở Sài Gòn.
                                Căn nhà gỗ ba gian giờ đây phủ bụi thời gian, mái ngói rêu xanh loang lổ dưới nắng chiều.
                                "Về rồi đấy à?" Một giọng nói quen thuộc vang lên sau lưng, khiến tim cô hẫng một nhịp."""),
                        new SeedChapter(2, "Người cũ", """
                                Cô quay lại. Đức Anh đứng đó, vẫn nụ cười ấy, chỉ là gương mặt đã chững chạc hơn nhiều so với tám năm trước.
                                "Anh... sao anh biết em về?" Minh Anh lắp bắp, tay vẫn còn nắm chặt quai va li.
                                "Chị Tư đầu ngõ gọi điện báo anh. Ở phố này, có gì mà không ai biết." Đức Anh cười, ánh mắt dịu dàng như ngày xưa.
                                Gió từ sông Hoài thổi vào, mang theo mùi ẩm của đá cổ và một chút bối rối không tên."""),
                        new SeedChapter(3, "Bản vẽ dở dang", """
                                Đêm đó, Minh Anh ngồi trong căn nhà cũ, trải bản vẽ trùng tu ra bàn gỗ.
                                Ký ức ùa về: những buổi chiều hai người ngồi vẽ ký họa bên bờ sông, những lời hứa hẹn chưa kịp nói hết đã phải xa nhau vì công việc.
                                Có tiếng gõ cửa nhẹ. Đức Anh đứng ngoài hiên, tay cầm một hộp đèn lồng nhỏ.
                                "Anh nghĩ em sẽ cần ánh sáng để làm việc." Anh nói, và đặt chiếc đèn lồng lên bậu cửa sổ, ánh vàng ấm áp lan ra khắp gian nhà."""))),

                new SeedStory("Đế Bá Tinh Hà", "de-ba-tinh-ha", "Vũ Lăng Cửu",
                        "Trong tương lai xa, nhân loại đã vươn ra các vì sao. Một phi công chiến đấu cơ bị lưu đày nơi biên giới thiên hà tình cờ khám phá tàn tích của một nền văn minh cổ đại.",
                        StoryStatus.ONGOING, "de-ba-tinh-ha", List.of("khoa-huyen", "huyen-huyen"), List.of(
                        new SeedChapter(1, "Vùng biên giới lạnh lẽo", """
                                Trạm Gác Biên Vực Bảy chỉ là một điểm sáng nhỏ nhoi giữa vùng không gian tối đen vô tận.
                                Lý Thần Phong, cựu đại úy Hạm Đội Ngân Hà, ngồi trong buồng lái chiến đấu cơ Ưng Đêm, nhìn màn hình radar trống rỗng đã ba tháng liền.
                                "Lại một ca trực vô vị." Hắn thở dài, gõ ngón tay lên bảng điều khiển đã cũ kỹ.
                                Đột nhiên, một tín hiệu lạ nhấp nháy ở rìa màn hình — thứ gì đó không nằm trong bất kỳ bản đồ nào hắn từng biết."""),
                        new SeedChapter(2, "Tàn tích cổ đại", """
                                Theo tín hiệu, Lý Thần Phong lái tàu vượt qua một trường tiểu hành tinh dày đặc.
                                Phía sau đó là một công trình khổng lồ, hình dạng như một con mắt bằng kim loại đen tuyền, đã ngủ yên hàng vạn năm.
                                Máy quét của tàu báo lỗi liên tục — vật liệu cấu thành công trình không tồn tại trong bảng tuần hoàn đã biết.
                                Khi tàu tiến lại gần, "con mắt" khổng lồ ấy chậm rãi mở ra, và một luồng ánh sáng xanh lam bao trùm lấy con tàu nhỏ bé."""),
                        new SeedChapter(3, "Tiếng vọng ngàn năm", """
                                Trong đầu Lý Thần Phong vang lên một giọng nói không phải tiếng người, không phải sóng vô tuyến, mà như thể vọng thẳng vào tâm trí.
                                "Kẻ lữ hành... ngươi đã tìm thấy Đế Bá Đài sau một vạn năm ngủ yên."
                                Hắn siết chặt cần lái, mồ hôi lạnh túa ra sau gáy. "Ngươi là ai? Đây là nơi nào?"
                                "Ta là ký ức cuối cùng của một đế chế đã tan biến giữa các vì sao. Và ngươi, sẽ là người kế thừa nó — nếu ngươi dám.\""""))),

                new SeedStory("Kiếm Khách Vô Danh", "kiem-khach-vo-danh", "Cổ Long Phong",
                        "Một kiếm khách giấu tên lang bạt giang hồ, mang trên vai món nợ máu ba đời, đi tìm kẻ đã hủy diệt môn phái của mình năm xưa.",
                        StoryStatus.COMPLETED, "kiem-khach-vo-danh", List.of("kiem-hiep"), List.of(
                        new SeedChapter(1, "Quán rượu bên đường", """
                                Mưa xuân lất phất trên con đường đất dẫn vào trấn nhỏ. Một bóng người khoác áo tơi rách, thanh kiếm gỗ cũ đeo sau lưng, bước vào quán rượu ven đường.
                                "Cho một vò rượu." Giọng nói khàn đặc, không ai nhìn rõ mặt dưới vành nón lá sụp thấp.
                                Chủ quán run rẩy rót rượu, mắt liếc nhìn thanh kiếm gỗ — nghe đồn kiếm khách áo tơi đã giết ba tên đại đạo ở trấn bên chỉ bằng thanh kiếm gỗ ấy.
                                Không ai biết tên hắn. Người ta chỉ gọi hắn là Vô Danh Kiếm Khách."""),
                        new SeedChapter(2, "Danh sách máu", """
                                Đêm đó, dưới ánh đèn dầu leo lét, Vô Danh lấy ra một mảnh lụa cũ đã ố vàng vì máu và thời gian.
                                Trên đó là năm cái tên, viết bằng nét chữ run rẩy của một người sắp chết — sư phụ hắn, trước khi trút hơi thở cuối cùng.
                                Bốn cái tên đã bị gạch. Chỉ còn lại một: Thiết Diện Vô Tình, đại đương gia Hắc Phong Trại.
                                Hắn cất mảnh lụa vào ngực áo, tay khẽ chạm vào chuôi kiếm gỗ. "Sắp rồi, sư phụ. Con sắp trả xong món nợ này.\""""),
                        new SeedChapter(3, "Hắc Phong Trại", """
                                Ba ngày sau, Vô Danh đứng trước cổng Hắc Phong Trại, nơi từng là thánh địa của môn phái hắn mười lăm năm về trước.
                                Giờ đây nó chỉ còn là sào huyệt của bọn thổ phỉ khét tiếng nhất vùng Giang Nam.
                                "Thiết Diện Vô Tình, ra đây!" Hắn hét lớn, giọng vang vọng khắp thung lũng.
                                Cánh cổng gỗ nặng nề mở ra, và trong bóng tối, một người đàn ông mặt sắt bước ra, tay cầm đại đao, cười lạnh: "Kiếm gỗ mà cũng dám tìm ta báo thù sao?\""""))),

                new SeedStory("Hệ Thống Tối Thượng", "he-thong-toi-thuong", "Ngã Thị Đại Sư",
                        "Một nhân viên văn phòng bình thường bất ngờ nhận được một hệ thống game bí ẩn ngay giữa thành phố hiện đại, và cuộc sống của anh thay đổi hoàn toàn từ đó.",
                        StoryStatus.ONGOING, "he-thong-toi-thuong", List.of("do-thi", "huyen-huyen"), List.of(
                        new SeedChapter(1, "Tiếng chuông lúc nửa đêm", """
                                Trần Phong tỉnh giấc lúc ba giờ sáng bởi một âm thanh chưa từng nghe — tiếng "keng" trong trẻo vang thẳng vào đầu.
                                [Ký Chủ đã thức tỉnh. Hệ Thống Tối Thượng chính thức kích hoạt.]
                                Dòng chữ xanh lam hiện ra lơ lửng trước mắt anh, không phải ảo giác, không phải giấc mơ.
                                Anh vỗ hai bên má thật mạnh. Đau. Rất đau. Vậy... đây là thật?"""),
                        new SeedChapter(2, "Nhiệm vụ đầu tiên", """
                                [Nhiệm vụ mới: Giúp đỡ một người xa lạ trong vòng 24 giờ. Phần thưởng: 100 điểm kinh nghiệm.]
                                Trần Phong nhìn dòng chữ, rồi nhìn quanh căn phòng trọ nhỏ bé quen thuộc của mình. Mọi thứ vẫn như cũ, chỉ có anh là khác.
                                Sáng hôm sau, đi làm ngang công viên, anh thấy một cụ già ngã xe đạp, hàng hóa đổ tung tóe khắp đường.
                                Không suy nghĩ nhiều, anh chạy đến đỡ cụ dậy. [Nhiệm vụ hoàn thành. Nhận 100 điểm kinh nghiệm.] — dòng chữ quen thuộc lại hiện lên, kèm theo một cảm giác ấm áp lan khắp cơ thể."""),
                        new SeedChapter(3, "Sức mạnh đầu tiên", """
                                Một tuần sau, Trần Phong đã quen dần với những dòng thông báo xanh lam xuất hiện bất chợt trong ngày.
                                [Đạt cấp độ 5. Mở khóa kỹ năng: Nhãn Lực Thấu Thị.]
                                Anh thử nhìn vào chiếc cốc trên bàn, và bất ngờ thấy được cả cấu trúc phân tử thủy tinh bên trong — một khả năng không thuộc về thế giới bình thường.
                                Trần Phong mỉm cười. Cuộc sống tẻ nhạt của một nhân viên văn phòng, có lẽ, sắp kết thúc từ đây.""")))
        );
    }
}
