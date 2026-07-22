# MangaVault — Website Đọc Truyện Trực Tuyến

Đồ án thực tập tốt nghiệp: **website đọc truyện trực tuyến sử dụng React và Java Spring Boot**.

- Tài liệu yêu cầu chính thức: [`docs/HƯỚNG ĐI THỰC TẬP.md`](docs/HƯỚNG%20ĐI%20THỰC%20TẬP.md)
- Nhật ký học tập theo từng task/giai đoạn: [`docs/LEARNING_LOG.md`](docs/LEARNING_LOG.md)
- Hướng dẫn cho AI coding assistant: [`CLAUDE.md`](CLAUDE.md)

## Kiến Trúc

```text
React (Vite) — frontend/          Spring Boot 3.5 / Java 21 — repo root
┌─────────────────────┐          ┌──────────────────────────────────┐
│ pages / components  │  axios   │ controller → service → repository │   JPA    ┌───────┐
│ services (API calls)│ ───────► │ dto request/response · exception  │ ───────► │ MySQL │
└─────────────────────┘          └──────────────────────────────────┘          └───────┘
```

- **Backend**: package `com.daniel.mangavault`, kiến trúc phân lớp, DTO tách khỏi entity, lỗi xử lý tập trung qua `GlobalExceptionHandler` + `ErrorCode` enum.
- **Frontend**: React 19 + react-router 7, mọi API call tập trung tại `frontend/src/services/`.
- **Database**: MySQL 8.4 (docker-compose), Hibernate `ddl-auto=update` cho giai đoạn phát triển.

## Tính Năng Hiện Có

| Nhóm | Tính năng |
|---|---|
| Khách (GUEST) | Trang chủ truyện mới cập nhật, danh sách truyện (phân trang, lọc theo trạng thái), tìm kiếm theo tên, chi tiết truyện, mục lục chương, đọc chương (điều hướng trước/sau, tùy chọn cỡ chữ + nền tối) |
| Thành viên (USER) | Đăng ký, đăng nhập JWT |
| Quản trị (ADMIN, `/admin`) | CRUD truyện, CRUD chương (form thêm/sửa inline, tự gợi ý số chương kế tiếp) — bảo vệ bằng Spring Security + JWT, tài khoản admin khởi tạo tự động lúc start |

## API

Response dùng chung envelope `ApiResponse<T>` `{ code, result, message }` — `1000` là thành công; danh sách phân trang bọc trong `PageResponse<T>`.

```text
# Public
GET    /api/health
GET    /api/stories?keyword=&status=&sort=&page=&size=
                                              danh sách + tìm kiếm + lọc trạng thái (sort: latest|updated)
GET    /api/stories/{id}                      chi tiết truyện
GET    /api/stories/{id}/chapters             mục lục chương (không có nội dung)
GET    /api/chapters/{id}                     nội dung chương + prev/next id
POST   /api/auth/register                     đăng ký (luôn tạo role USER)
POST   /api/auth/login                        đăng nhập, trả JWT

# Admin (yêu cầu Authorization: Bearer <token> với role ADMIN)
POST   /api/admin/stories
PUT    /api/admin/stories/{id}
DELETE /api/admin/stories/{id}                xóa kèm toàn bộ chương
POST   /api/admin/stories/{id}/chapters
PUT    /api/admin/chapters/{id}
DELETE /api/admin/chapters/{id}
```

Mã lỗi: `4041`/`4042` không tồn tại (404) · `4090` slug, `4091` số chương, `4092` username, `4093` email bị trùng (409) · `4011` sai tài khoản/mật khẩu, `4010` thiếu/sai token (401) · `4030` không đủ quyền (403) · `4000` dữ liệu không hợp lệ (400).

## Chạy Project

Yêu cầu: Java 21, Node.js 20+, Docker (cho MySQL).

```powershell
# 1. Database
docker compose up -d

# 2. Backend (cổng 8080)
$env:DB_PASSWORD = "manga_pass"
.\mvnw.cmd spring-boot:run

# 3. Frontend (cổng 5173)
cd frontend
npm install
npm run dev
```

Kiểm tra backend sống: `GET http://localhost:8080/api/health`. Trên Linux/macOS thay `.\mvnw.cmd` bằng `./mvnw`.

Tài khoản quản trị mặc định (tự tạo lần chạy đầu, đổi qua env `ADMIN_USERNAME`/`ADMIN_PASSWORD`): `admin` / `admin123`.

```powershell
# Test
.\mvnw.cmd test
```

## Tiến Độ

| Giai đoạn | Nội dung | Trạng thái |
|---|---|---|
| 1 | Domain Story/Chapter, API đọc công khai, frontend reader | ✅ Hoàn thành |
| 2 | Admin CRUD truyện + chương, `ErrorCode` enum, slug unique | ✅ Hoàn thành |
| 3 | Đăng ký/đăng nhập JWT, Spring Security, phân quyền GUEST/USER/ADMIN | ✅ Hoàn thành |
| 4 | Trang chủ, lọc trạng thái, tùy chọn đọc, hoàn thiện tài liệu | ✅ Hoàn thành |
