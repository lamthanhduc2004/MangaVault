# MangaVault — Context handoff (cho AI khác)

> Cập nhật: 2026-07-22. File này tóm tắt trạng thái project + toàn bộ quyết định đã chốt trong các phiên làm việc với Claude Code, để một AI khác (hoặc phiên mới) tiếp tục mà không cần đọc lại hội thoại.

## 1. Project là gì

- **MangaVault** = đồ án thực tập tốt nghiệp PTIT của sinh viên **Lâm Thành Đức** (B22DCCN227, GVHD TS. Vũ Trọng Sinh).
- Đề tài: **"Website đọc truyện trực tuyến sử dụng React và Java Spring Boot"**.
- Tài liệu yêu cầu chính thức (KHÔNG sửa): `docs/HƯỚNG ĐI THỰC TẬP.md`.
- Lịch sử: ban đầu là app học CRUD "manga" cá nhân → đã **pivot sang website đọc truyện chữ** (domain Story + Chapter). Việc rename Manga→Story đã hoàn tất.
- User giao tiếp bằng **tiếng Việt** — trả lời bằng tiếng Việt.

## 2. Tech stack & môi trường

| Thành phần | Chi tiết |
|---|---|
| Backend | Java 21, Spring Boot 3.5.14, Maven wrapper (`.\mvnw.cmd`), package root `com.daniel.mangavault`, cổng 8080 |
| Frontend | React 19 + Vite 7 + react-router-dom 7 + axios, thư mục `frontend/`, cổng 5173 |
| Database | MySQL 8.4 qua `docker-compose.yml` gốc repo — container `mangavault-mysql`, db `manga_vault`, user `manga_user`, pass `manga_pass` (env `DB_PASSWORD`), `ddl-auto=update` |
| JDK | Temurin 21.0.11 cài user-level tại `C:\Users\danielPC\Java\jdk-21.0.11+10` — **mỗi lệnh PowerShell mới phải set `$env:JAVA_HOME`** (session không persist) |
| OS | Windows 11, **không có quyền admin**, không winget/choco; PowerShell 5.1 |
| Git | Repo-local identity: `Lam Thanh Duc <lamthanhducit2004@gmail.com>`; branch `main`; commit milestone gần nhất: `bae02bd` "Migrate domain to Story/Chapter with reader, pagination, and React frontend" (45 files) |

Chạy backend (background):
```powershell
$env:JAVA_HOME = "C:\Users\danielPC\Java\jdk-21.0.11+10"
$env:DB_PASSWORD = "manga_pass"
.\mvnw.cmd spring-boot:run
```
Frontend: `cd frontend; npm run dev` (hoặc preview qua `.claude/launch.json`, name "frontend").

## 3. Kiến trúc & quy ước (bắt buộc tuân theo)

- Layered: `controller → service → repository → entity`. Controller mỏng, không gọi repository trực tiếp, không chứa business logic.
- Constructor injection (`@RequiredArgsConstructor`, Lombok). DTO tách `dto/request` / `dto/response`; entity không expose ra controller.
- Envelope `ApiResponse<T> {code, result, message}`:
  - `1000` = success
  - `4040` / HTTP 404 = `AppException` (hiện luôn map 404 — xem nợ kỹ thuật)
  - `4000` / HTTP 400 = validation lỗi (gộp field errors bằng `"; "`) + malformed JSON body
- Phân trang: `PageResponse<T> {items, page, size, totalElements, totalPages, last}` với static `from(Page<E>, Function<E,T>)`.
- Frontend: mọi API call tập trung tại `frontend/src/services/`.
- Exception xử lý tập trung ở `GlobalExceptionHandler` — không thêm try/catch trả response ở controller.
- Authorization phải enforce ở backend (khi có security); frontend chỉ là visibility.

## 4. Đã làm xong (commit `bae02bd`)

### Backend
- Entity: `Story` (UUID id, title, slug, author, description, status ONGOING/COMPLETED/HIATUS, visibility PRIVATE/PUBLIC, timestamps), `Chapter` (`@ManyToOne(LAZY)` story, chapterNumber, unique `(story_id, chapter_number)`, content LONGTEXT).
- Endpoints public (tất cả dưới `/api`):
  - `GET /api/health`
  - `GET /api/stories?keyword=&page=&size=` (search title, sort createdAt DESC, default size 12)
  - `GET /api/stories/{id}`
  - `GET /api/stories/{id}/chapters` (summaries — KHÔNG bao giờ trả content)
  - `GET /api/chapters/{id}` (full content + `prevChapterId`/`nextChapterId` theo **nearest-neighbor** — `findFirst...LessThan/GreaterThan` — sống sót khi số chương có khoảng trống)
- Endpoints admin (Giai đoạn 2 — dưới `/api/admin/**` để sau gắn Security theo path, CHƯA có auth):
  - `POST /api/admin/stories`, `PUT/DELETE /api/admin/stories/{id}` (DELETE xóa cả chương, @Transactional)
  - `POST /api/admin/stories/{id}/chapters`, `PUT/DELETE /api/admin/chapters/{id}`
- `ErrorCode` enum: 4041 story not found, 4042 chapter not found (404); 4090 trùng slug, 4091 trùng số chương (409). `AppException(ErrorCode)` → handler đọc status + code từ enum.
- `slug` đã unique (DB constraint + `existsBySlug`/`existsBySlugAndIdNot` ở service).
- `WebConfig`: CORS `/api/**` cho `${app.cors.allowed-origins:http://localhost:5173}`.
- `GlobalExceptionHandler`: 3 handler (AppException, MethodArgumentNotValidException, HttpMessageNotReadableException).

### Frontend
- `src/services/api.js` (axios, baseURL `VITE_API_BASE_URL || http://localhost:8080/api`) + `storyService.js` (unwrap `.data.result`).
- Pages: `StoryListPage` (search + pagination), `StoryDetailPage` (danh sách chương + "Đọc từ đầu"), `ChapterReaderPage` (nút prev/next, content split theo `\n`, reader 720px line-height 1.9).
- Admin UI (Giai đoạn 2): `src/services/adminService.js` + `src/pages/admin/` — `AdminStoryListPage` (bảng + sửa/xóa), `AdminStoryFormPage` (dùng chung tạo/sửa qua `:id`), `AdminChapterListPage` (bảng chương + form thêm/sửa inline, tự gợi ý số chương kế tiếp). Routes: `/admin`, `/admin/stories/new`, `/admin/stories/:id/edit`, `/admin/stories/:id/chapters`. `StoryCreatePage` cũ đã xóa.
- Components: `StoryCard`, `SearchBar`, `Pagination`, `StatusBadge` (nhãn tiếng Việt).
- Routes: `/`, `/stories/new`, `/stories/:id`, `/chapters/:id`.

### Dữ liệu demo (DB local)
- 2 truyện: "Đấu Phá Thương Khung" (3 chương), "Toàn Chức Cao Thủ" (2 chương). Chương được chèn trực tiếp SQL vì **chưa có API tạo chương**.
- Đã verify end-to-end trên browser: list → detail → reader → next chapter.

## 5. Nợ kỹ thuật đã ghi nhận (cũng có trong CLAUDE.md)

1. ~~`slug` chưa unique~~ — ĐÃ XONG (Giai đoạn 2).
2. ~~`AppException` luôn map 404~~ — ĐÃ XONG: `ErrorCode` enum (Giai đoạn 2).
3. ~~Admin endpoints chưa bảo vệ~~ — ĐÃ XONG: Spring Security + JWT (Giai đoạn 3).
4. OSIV (open-in-view) đang bật — lazy `chapter.getStory()` trong `ChapterService` dựa vào nó.
5. Bảng `mangas` cũ còn trong DB local (vô hại, drop khi tiện).
6. README + Postman collection chưa cập nhật theo domain Story/Chapter (để Giai đoạn 4).

## 6. Kế hoạch đã chốt với user (thứ tự KHÔNG đổi)

User đã quyết qua AskUserQuestion: **Admin CRUD làm TRƯỚC, Auth làm SAU** (ngược khuyến nghị ban đầu — phải tôn trọng).

### Giai đoạn 2 — Admin CRUD — ✅ HOÀN THÀNH (2026-07-22)
- Backend + frontend admin CRUD đầy đủ, slug unique, ErrorCode enum. Đã verify API bằng curl (409/404 đúng mã) và UI trên browser (thêm/sửa chương hoạt động).

### Giai đoạn 3 — Auth — ✅ HOÀN THÀNH (2026-07-22)
- Entity `User` + enum `Role` (USER/ADMIN); `POST /api/auth/register|login`; JWT HS256 qua OAuth2 Resource Server (KHÔNG dùng jjwt — lưu ý: encoder cần `JwsHeader.with(MacAlgorithm.HS256)` tường minh).
- `security/SecurityConfig`: GET public, `/api/admin/**` = ROLE_ADMIN; 401 (4010) / 403 (4030) trả envelope qua entry point; CORS chuyển từ WebConfig (đã xóa) vào đây.
- Seed admin lúc start: `app.admin.*` (mặc định admin/admin123). ErrorCode mới: 4092/4093 trùng username/email, 4011 sai credentials.
- Frontend: `context/AuthContext.jsx`, interceptor token trong `services/api.js`, LoginPage/RegisterPage, `routes/RequireAdmin.jsx` bọc `/admin`, nav theo trạng thái đăng nhập.
- Đã verify: ma trận quyền qua curl (no token 401, USER 403, ADMIN 200, token rác 401) + UI flow trên browser.

### Giai đoạn 4 — Hoàn thiện demo bảo vệ
- Trang chủ (truyện mới cập nhật), lọc theo trạng thái, tùy chọn đọc (cỡ chữ / nền tối — F22), cập nhật README + Postman collection.

## 7. Bẫy môi trường đã gặp (tránh lặp lại)

- Seed tiếng Việt qua `curl -d` inline → hỏng encoding. Fix: viết JSON UTF-8 ra file rồi `curl --data-binary @file` với `Content-Type: application/json; charset=utf-8`.
- PowerShell không giữ env var / cwd giữa các lệnh → set `JAVA_HOME`, `DB_PASSWORD` và `Set-Location` trong mỗi lệnh khi cần.
- Không cài được phần mềm cần quyền admin — Docker Desktop user đã tự cài.
- Test duy nhất là `@SpringBootTest` context-load → cần DB chạy mới pass.
