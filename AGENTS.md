# SYSTEM CONTEXT — ONLINE STORY READING WEBSITE

## 1. Project Overview

Xây dựng website đọc truyện chữ trực tuyến theo kiến trúc tách biệt frontend và backend.

Công nghệ chính:

* Frontend: React
* Backend: Java Spring Boot
* API: RESTful API, dữ liệu JSON
* Database: PostgreSQL
* Authentication: Spring Security + JWT
* Source control: Git + GitHub
* API testing: Postman

Hệ thống có ba nhóm người dùng:

* `GUEST`: xem, tìm kiếm và đọc truyện.
* `USER`: quản lý tài khoản, theo dõi truyện, lịch sử đọc, bình luận và đánh giá.
* `ADMIN`: quản lý người dùng, truyện, chương, thể loại, bình luận và thống kê.

## 2. Architecture

Hệ thống sử dụng mô hình client–server gồm ba thành phần:

```text
React Frontend
      |
      | HTTP / JSON / REST API
      v
Spring Boot Backend
      |
      | JPA / Hibernate
      v
PostgreSQL Database
```

Luồng xử lý chuẩn:

1. Người dùng thao tác trên React.
2. Frontend gọi REST API.
3. Controller tiếp nhận request.
4. Security kiểm tra JWT và quyền truy cập.
5. Service xử lý nghiệp vụ.
6. Repository truy vấn hoặc cập nhật database.
7. Backend trả response DTO dưới dạng JSON.
8. Frontend cập nhật giao diện.

Không đặt logic nghiệp vụ trong controller.

## 3. Frontend Structure

```text
frontend/
├── public/
├── src/
│   ├── assets/
│   ├── components/
│   ├── layouts/
│   ├── pages/
│   ├── services/
│   ├── hooks/
│   ├── contexts/
│   ├── routes/
│   ├── utils/
│   ├── App.jsx
│   └── main.jsx
└── package.json
```

Trách nhiệm:

* `components`: UI component tái sử dụng.
* `pages`: các màn hình ứng dụng.
* `layouts`: bố cục chung như user layout và admin layout.
* `services`: gọi REST API.
* `hooks`: custom React hooks.
* `contexts`: authentication và global state.
* `routes`: định nghĩa route và protected route.
* `utils`: hàm tiện ích, constants và formatter.

Các component chính:

```text
Header
Footer
SearchBar
StoryCard
StoryList
CategoryMenu
ChapterList
ChapterReader
CommentList
Pagination
```

Frontend không được tự quyết định quyền truy cập. Việc ẩn nút chỉ phục vụ giao diện; backend luôn phải kiểm tra quyền.

## 4. Backend Structure

```text
backend/
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/example/storywebsite/
    │   │       ├── config/
    │   │       ├── controller/
    │   │       ├── dto/
    │   │       ├── entity/
    │   │       ├── exception/
    │   │       ├── mapper/
    │   │       ├── repository/
    │   │       ├── security/
    │   │       └── service/
    │   └── resources/
    │       └── application.yml
    └── test/
```

Quy tắc phân tầng:

* `controller`: nhận request, validation cơ bản và trả response.
* `service`: chứa toàn bộ business logic.
* `repository`: truy xuất dữ liệu bằng Spring Data JPA.
* `entity`: ánh xạ bảng database.
* `dto`: request và response của API.
* `mapper`: chuyển đổi giữa Entity và DTO.
* `security`: JWT, authentication và authorization.
* `exception`: exception tùy chỉnh và global exception handler.
* `config`: cấu hình CORS, security, serialization và application beans.

Controller không trả trực tiếp JPA Entity.

## 5. Core Domain

Các entity/bảng dự kiến:

```text
users
roles
user_roles
authors
stories
categories
story_categories
chapters
favorites
reading_histories
comments
ratings
```

Quan hệ chính:

```text
User        N---N Role
Story       N---N Category
Author      1---N Story
Story       1---N Chapter
User        N---N Story        through Favorite
User        N---N Story        through ReadingHistory
User        1---N Comment
Story       1---N Comment
User        1---N Rating
Story       1---N Rating
```

Ràng buộc quan trọng:

* `username` duy nhất.
* `email` duy nhất.
* Một chapter phải thuộc một story tồn tại.
* `chapterNumber` hoặc thứ tự chương không được trùng trong cùng một story.
* Mỗi user chỉ có một rating cho mỗi story.
* Favorite không được trùng cặp `userId + storyId`.
* Các cập nhật nhiều bảng phải sử dụng transaction.
* Không xóa dữ liệu cha khi còn dữ liệu liên quan nếu chưa xác định rõ cascade policy.

## 6. Main Features

### Guest

* Xem trang chủ.
* Xem danh sách truyện.
* Tìm kiếm theo tên truyện, tác giả hoặc từ khóa.
* Lọc theo thể loại và trạng thái.
* Xem chi tiết truyện.
* Xem danh sách chương.
* Đọc nội dung chương.
* Đăng ký và đăng nhập.

### User

* Xem và cập nhật hồ sơ.
* Đổi mật khẩu.
* Theo dõi hoặc bỏ theo dõi truyện.
* Xem lịch sử đọc.
* Tiếp tục từ chương gần nhất.
* Bình luận.
* Sửa hoặc xóa bình luận của chính mình.
* Đánh giá truyện.

### Admin

* CRUD truyện.
* CRUD chương.
* CRUD thể loại.
* Quản lý tài khoản.
* Khóa hoặc mở khóa tài khoản.
* Quản lý role.
* Ẩn hoặc xóa bình luận vi phạm.
* Xem thống kê cơ bản.

## 7. MVP Priority

Phiên bản đầu tiên chỉ ưu tiên:

1. Trang chủ.
2. Danh sách truyện.
3. Tìm kiếm truyện.
4. Chi tiết truyện.
5. Danh sách chương.
6. Đọc chương.
7. Đăng ký.
8. Đăng nhập và đăng xuất.
9. Admin CRUD truyện.
10. Admin CRUD chương.

Các chức năng favorite, reading history, comment, rating và statistics triển khai sau khi MVP ổn định.

## 8. API Conventions

Base path:

```text
/api
```

API công khai:

```http
GET  /api/stories
GET  /api/stories/{storyId}
GET  /api/stories/{storyId}/chapters
GET  /api/chapters/{chapterId}

POST /api/auth/register
POST /api/auth/login
```

API người dùng:

```http
GET    /api/users/me
PUT    /api/users/me

POST   /api/stories/{storyId}/favorites
DELETE /api/stories/{storyId}/favorites

GET    /api/users/me/favorites
GET    /api/users/me/reading-history

POST   /api/stories/{storyId}/comments
PUT    /api/comments/{commentId}
DELETE /api/comments/{commentId}
```

API quản trị:

```http
POST   /api/admin/stories
PUT    /api/admin/stories/{storyId}
DELETE /api/admin/stories/{storyId}

POST   /api/admin/stories/{storyId}/chapters
PUT    /api/admin/chapters/{chapterId}
DELETE /api/admin/chapters/{chapterId}
```

Quy ước:

* URL sử dụng danh từ số nhiều.
* Không chứa động từ như `/getStories`.
* Sử dụng đúng HTTP method.
* API danh sách phải hỗ trợ pagination.
* Không trả toàn bộ nội dung chương trong API danh sách truyện.
* Request và response sử dụng DTO.
* Lỗi được xử lý tập trung và trả về cấu trúc thống nhất.

Response lỗi đề xuất:

```json
{
  "timestamp": "2026-07-13T10:00:00Z",
  "status": 400,
  "error": "BAD_REQUEST",
  "message": "Invalid request data",
  "path": "/api/stories"
}
```

## 9. Security Rules

* Mật khẩu phải được hash bằng BCrypt.
* Không lưu hoặc log mật khẩu dạng plaintext.
* JWT được gửi qua:

```http
Authorization: Bearer <token>
```

* Backend luôn kiểm tra authentication và authorization.
* Chỉ `ADMIN` được truy cập `/api/admin/**`.
* User chỉ được sửa hoặc xóa dữ liệu thuộc quyền sở hữu của mình.
* Validate toàn bộ request từ client.
* Không trả password hash hoặc dữ liệu nhạy cảm trong response.
* Secret, database password và JWT key không được commit lên GitHub.
* Cấu hình nhạy cảm lấy từ environment variables.
* Cần giới hạn độ dài comment, title, description và chapter content.
* Cấu hình CORS chỉ cho phép frontend origin hợp lệ.

## 10. Performance Rules

* API danh sách sử dụng pagination.
* Tạo index cho các cột thường tìm kiếm:

  * `users.username`
  * `users.email`
  * `stories.title`
  * `stories.status`
  * `chapters.story_id`
  * `chapters.chapter_number`
* Tránh N+1 query.
* Không sử dụng eager loading không cần thiết.
* Không trả chapter content trong story list.
* Tối ưu kích thước ảnh bìa.
* Chỉ tải dữ liệu cần thiết cho từng màn hình.
* Dùng transaction cho nghiệp vụ cập nhật liên quan nhiều entity.

## 11. Coding Rules

Khi tạo hoặc sửa code:

* Giữ đúng kiến trúc phân tầng hiện tại.
* Không tạo dependency vòng.
* Không đưa business logic vào controller hoặc React component.
* Không gọi API trực tiếp trong nhiều component; tập trung trong `services`.
* Không trả Entity trực tiếp ra API.
* Sử dụng constructor injection.
* Đặt tên class, method và biến thể hiện đúng mục đích.
* Ưu tiên code dễ đọc hơn code quá trừu tượng.
* Không thêm thư viện mới nếu chưa thực sự cần.
* Không thay đổi contract API mà không cập nhật frontend và tài liệu.
* Mỗi thay đổi phải xem xét validation, authorization, exception handling và test.

## 12. Codex Working Instructions

Trước khi sửa code:

1. Đọc cấu trúc thư mục và các file liên quan.
2. Xác định luồng từ React component đến API, controller, service, repository và database.
3. Kiểm tra convention hiện tại trước khi tạo pattern mới.
4. Chỉ sửa các file cần thiết.
5. Không tự ý đổi framework, database hoặc kiến trúc tổng thể.

Sau khi sửa code:

1. Kiểm tra compile.
2. Chạy test liên quan.
3. Kiểm tra API contract.
4. Kiểm tra phân quyền.
5. Kiểm tra lỗi null, dữ liệu trùng và dữ liệu không tồn tại.
6. Tóm tắt:

   * File đã sửa.
   * Luồng xử lý mới.
   * API thay đổi.
   * Database migration nếu có.
   * Rủi ro hoặc việc chưa hoàn thành.

Khi yêu cầu chưa rõ, ưu tiên phương án đơn giản nhất phù hợp với kiến trúc hiện tại. Không tự mở rộng phạm vi ngoài yêu cầu.

