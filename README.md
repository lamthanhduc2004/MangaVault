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
| Người đọc | Danh sách truyện (phân trang), tìm kiếm theo tên, chi tiết truyện, mục lục chương, đọc chương với điều hướng chương trước/sau |
| Quản trị (`/admin`) | CRUD truyện, CRUD chương (form thêm/sửa inline, tự gợi ý số chương kế tiếp) — *chưa có đăng nhập, sẽ bảo vệ ở giai đoạn auth* |

## API

Response dùng chung envelope `ApiResponse<T>` `{ code, result, message }` — `1000` là thành công; danh sách phân trang bọc trong `PageResponse<T>`.

```text
# Public
GET    /api/health
GET    /api/stories?keyword=&page=&size=      danh sách + tìm kiếm (sort mới nhất)
GET    /api/stories/{id}                      chi tiết truyện
GET    /api/stories/{id}/chapters             mục lục chương (không có nội dung)
GET    /api/chapters/{id}                     nội dung chương + prev/next id

# Admin (sẽ yêu cầu role ADMIN ở giai đoạn auth)
POST   /api/admin/stories
PUT    /api/admin/stories/{id}
DELETE /api/admin/stories/{id}                xóa kèm toàn bộ chương
POST   /api/admin/stories/{id}/chapters
PUT    /api/admin/chapters/{id}
DELETE /api/admin/chapters/{id}
```

Mã lỗi: `4041` truyện không tồn tại, `4042` chương không tồn tại (HTTP 404) · `4090` trùng slug, `4091` trùng số chương (HTTP 409) · `4000` dữ liệu không hợp lệ (HTTP 400).

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

```powershell
# Test
.\mvnw.cmd test
```

## Tiến Độ

| Giai đoạn | Nội dung | Trạng thái |
|---|---|---|
| 1 | Domain Story/Chapter, API đọc công khai, frontend reader | ✅ Hoàn thành |
| 2 | Admin CRUD truyện + chương, `ErrorCode` enum, slug unique | ✅ Hoàn thành |
| 3 | Đăng ký/đăng nhập JWT, Spring Security, phân quyền GUEST/USER/ADMIN | ⏳ Tiếp theo |
| 4 | Trang chủ, lọc trạng thái, tùy chọn đọc, hoàn thiện tài liệu | ⏳ Kế hoạch |
