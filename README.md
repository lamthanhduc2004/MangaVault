# MangaVault — Website Đọc Truyện Trực Tuyến

Đồ án thực tập: **website đọc truyện trực tuyến sử dụng React và Java Spring Boot**.

🔗 **Demo trực tuyến**: https://manga-vault-roan.vercel.app
(API backend: https://mangavault-hi7e.onrender.com/api)

> Backend chạy trên gói miễn phí của Render nên sẽ **ngủ sau ~15 phút không có truy cập** — lần mở đầu tiên có thể mất 30–60 giây để "thức dậy". Database (Aiven, gói Free) cũng có thể tự tắt khi không hoạt động lâu; nếu trang báo lỗi kết nối, đợi khoảng 1–2 phút rồi tải lại.

**Tài khoản dùng thử**
| Vai trò | Tài khoản | Mật khẩu |
|---|---|---|
| Quản trị (ADMIN) | `admin` | *(đặt qua biến môi trường `ADMIN_PASSWORD` khi deploy — xem CLAUDE.md)* |
| Thành viên (USER) | tự đăng ký tại trang `/register` | — |

## Kiến Trúc

```text
React (Vite) — frontend/          Spring Boot 3.5 / Java 21 — repo root
┌─────────────────────┐          ┌──────────────────────────────────┐
│ pages / components  │  axios   │ controller → service → repository │   JPA    ┌───────┐
│ services (API calls)│ ───────► │ dto request/response · exception  │ ───────► │ MySQL │
└─────────────────────┘          └──────────────────────────────────┘          └───────┘
```

- **Backend**: package `com.daniel.mangavault`, kiến trúc phân lớp (controller/service/repository/entity/DTO), lỗi xử lý tập trung qua `GlobalExceptionHandler` + `ErrorCode` enum, phân quyền qua Spring Security + JWT.
- **Frontend**: React 19 + react-router 7, mọi API call tập trung tại `frontend/src/services/`, theme sáng/tối toàn cục.
- **Database**: MySQL 8 (local: docker-compose · production: Aiven MySQL free tier).
- **Triển khai**: backend đóng gói bằng Docker (Render) · frontend build tĩnh (Vercel).

## Tính Năng

| Nhóm | Tính năng |
|---|---|
| Khách (GUEST) | Trang chủ (mới cập nhật / nổi bật / xem nhiều), danh sách truyện có phân trang + tìm theo tên hoặc tác giả + lọc trạng thái/thể loại + 5 kiểu sắp xếp, chi tiết truyện, mục lục chương, đọc chương (điều hướng trước/sau, đổi cỡ chữ, nền sáng/tối), xem bình luận và điểm đánh giá |
| Thành viên (USER) | Đăng ký, đăng nhập JWT, hồ sơ cá nhân (tên hiển thị, ảnh đại diện, đổi mật khẩu), theo dõi truyện, lịch sử đọc + "Đọc tiếp", viết/sửa/xóa bình luận + báo cáo bình luận vi phạm, đánh giá sao |
| Quản trị (ADMIN, `/admin`) | CRUD truyện + chương (ẩn/hiện, gán thể loại), CRUD thể loại (chặn xóa khi đang dùng), quản lý người dùng (khóa/mở khóa, phân quyền), duyệt bình luận bị báo cáo, bảng thống kê hệ thống |

Toàn bộ route ghi (`/api/admin/**`) và route theo người dùng (`/api/me/**`, follow, rating, comment) được Spring Security enforce ở backend — ẩn nút trên giao diện chỉ là UX phụ trợ, không phải ranh giới bảo mật.

## API

Response dùng chung envelope `ApiResponse<T>` `{ code, result, message }` — `1000` là thành công; danh sách phân trang bọc trong `PageResponse<T>`.

```text
# Công khai
GET    /api/health
GET    /api/stories?keyword=&status=&genre=&sort=&page=&size=
                                              danh sách (keyword khớp tên, tác giả hoặc thể loại; sort: latest|updated|views|rating|title)
GET    /api/stories/{id}                      chi tiết truyện (chỉ truyện PUBLIC)
GET    /api/stories/{id}/chapters             mục lục chương đã công khai
GET    /api/chapters/{id}                     nội dung chương + prev/next id (tăng lượt xem)
GET    /api/genres                            danh sách thể loại
GET    /api/stories/{id}/comments             bình luận của truyện
GET    /api/stories/{id}/rating               điểm đánh giá trung bình
POST   /api/auth/register                     đăng ký (luôn tạo role USER)
POST   /api/auth/login                        đăng nhập bằng username/email, trả JWT

# Cần đăng nhập
GET    /api/me · PUT /api/me · PUT /api/me/password
POST/DELETE/GET /api/stories/{id}/follow       theo dõi truyện
GET    /api/me/follows                        danh sách đang theo dõi
GET/POST/DELETE /api/me/history                lịch sử đọc + lưu tiến độ
PUT/DELETE /api/stories/{id}/rating            đánh giá sao
POST   /api/stories/{id}/comments · PUT/DELETE /api/comments/{id}
POST   /api/comments/{id}/report               báo cáo bình luận

# Quản trị (yêu cầu Authorization: Bearer <token> với role ADMIN)
GET/POST/PUT/DELETE /api/admin/stories[/{id}]  CRUD truyện (danh sách gồm cả truyện ẩn)
POST/PUT/PATCH/DELETE /api/admin/stories/{id}/chapters, /api/admin/chapters/{id}
                                              CRUD chương + PATCH .../publish (ẩn/hiện)
GET/POST/PUT/DELETE /api/admin/genres[/{id}]   CRUD thể loại
GET    /api/admin/users · PATCH .../status · PATCH .../role
GET    /api/admin/comments                    tìm/lọc toàn bộ bình luận
PATCH  /api/admin/comments/{id}/visibility    ẩn/khôi phục bình luận
GET    /api/admin/comments/reported · DELETE /api/admin/comments/{id}
GET    /api/admin/stats                       thống kê hệ thống
```

Mã lỗi: `40xx` không tồn tại (404) · `409x` xung đột — trùng slug/username/email/thể loại, đã theo dõi, đã báo cáo (409) · `4010`/`4011`/`4031` xác thực (401/403) · `4030`/`4032`/`4033` không đủ quyền (403) · `4000`/`4001` dữ liệu không hợp lệ (400). Chi tiết đầy đủ trong `CLAUDE.md`.

## Chạy Project (local)

**Yêu cầu**: Java 21, Node.js 20+, Docker (cho MySQL local).

```powershell
# 1. Database (MySQL 8 qua docker-compose)
docker compose up -d

# 2. Backend — cổng 8080
$env:DB_PASSWORD = "manga_pass"
.\mvnw.cmd spring-boot:run

# 3. Frontend — cổng 5173 (terminal khác)
cd frontend
npm install
npm run dev
```

Trên Linux/macOS thay `.\mvnw.cmd` bằng `./mvnw`. Kiểm tra backend sống: `GET http://localhost:8080/api/health`.

Database rỗng sẽ tự nạp ~6 truyện mẫu khi backend khởi động lần đầu (`app.demo.seed`, mặc định bật). Tài khoản quản trị mặc định (đổi qua env `ADMIN_USERNAME`/`ADMIN_PASSWORD`): `admin` / `admin123`.

```powershell
# Chạy toàn bộ test (không cần MySQL — dùng H2 in-memory)
.\mvnw.cmd test
```

Bộ Postman collection kiểm thử API: `postman/MangaVault.postman_collection.json` (đã trỏ sẵn cả 2 biến `baseUrl` local và production).

## Triển Khai (Deployment)

| Thành phần | Nền tảng | Ghi chú |
|---|---|---|
| Backend | [Render](https://render.com), Docker, gói Free | `Dockerfile` ở root; đọc cấu hình qua biến môi trường (xem bảng dưới) |
| Frontend | [Vercel](https://vercel.com), gói Free | Root Directory = `frontend/`; SPA fallback qua `frontend/vercel.json` |
| Database | [Aiven](https://aiven.io) MySQL, gói Free | Bắt buộc SSL (`sslMode=REQUIRED` trong `DB_URL`) |

Biến môi trường bắt buộc trên Render:

| Biến | Ví dụ | Ghi chú |
|---|---|---|
| `DB_URL` | `jdbc:mysql://host:port/db?sslMode=REQUIRED` | |
| `DB_USERNAME` / `DB_PASSWORD` | | |
| `JWT_SECRET` | chuỗi ngẫu nhiên ≥ 32 ký tự | **Không dùng giá trị mặc định trong code** — dev default tự log cảnh báo ERROR khi khởi động |
| `ADMIN_USERNAME` / `ADMIN_PASSWORD` | | Tài khoản admin tự tạo lúc khởi động nếu chưa có |
| `CORS_ALLOWED_ORIGINS` | `https://<domain-vercel>` | Không có dấu `/` ở cuối — so khớp tuyệt đối |

Biến môi trường trên Vercel: `VITE_API_BASE_URL=https://<domain-render>/api` (đổi biến này bắt buộc **Redeploy** — nếu dùng "Use existing Build Cache" thì giá trị mới sẽ không được nhúng vào bundle).

## Tiến Độ

Đủ 22/22 tính năng theo bảng chức năng của tài liệu yêu cầu (`docs/HƯỚNG ĐI THỰC TẬP.md`, mục 1.6), đã triển khai lên môi trường thật.

| Giai đoạn | Nội dung | Trạng thái |
|---|---|---|
| 1 | Domain Story/Chapter, API đọc công khai, frontend reader | ✅ |
| 2 | Admin CRUD truyện + chương, `ErrorCode` enum, slug unique | ✅ |
| 3 | Đăng ký/đăng nhập JWT, Spring Security, phân quyền GUEST/USER/ADMIN | ✅ |
| 4 | Trang chủ, lọc trạng thái, tùy chọn đọc | ✅ |
| 5 | Thể loại, hồ sơ cá nhân, theo dõi truyện, lịch sử đọc, bình luận + báo cáo, đánh giá sao, quản lý người dùng, thống kê | ✅ |
| — | Đóng gói Docker, deploy Render + Vercel + Aiven, responsive, test tự động (H2) | ✅ |

Xem `docs/LEARNING_LOG.md` cho nhật ký học tập theo từng giai đoạn và các bài học kỹ thuật.
