# TÓM TẮT KỸ THUẬT PROJECT MANGAVAULT

## 1. Giới thiệu ngắn gọn

MangaVault là website đọc truyện chữ trực tuyến được xây dựng theo kiến trúc client-server tách biệt. React đảm nhiệm giao diện người dùng; Spring Boot cung cấp REST API, xử lý nghiệp vụ và phân quyền; MySQL lưu trữ dữ liệu. Hệ thống phục vụ ba nhóm người dùng là GUEST, USER và ADMIN.

Điểm chính của project không chỉ nằm ở các màn hình CRUD. Sản phẩm xử lý xuyên suốt từ giao diện, API contract, xác thực JWT, phân quyền ở backend, ràng buộc dữ liệu, transaction, kiểm thử đến triển khai thực tế.

## 2. Công nghệ đang được sử dụng trong code

| Thành phần | Công nghệ thực tế | Vai trò |
|---|---|---|
| Frontend | React 19, Vite 7, React Router 7, Axios | SPA, routing, quản lý phiên và gọi API |
| Backend | Java 21, Spring Boot 3.5.14 | REST API, validation và business logic |
| Bảo mật | Spring Security, OAuth2 Resource Server, JWT HS256, BCrypt | Xác thực stateless và phân quyền |
| Persistence | Spring Data JPA, Hibernate | ORM và truy xuất dữ liệu |
| Database | MySQL 8 | Lưu dữ liệu chính |
| Kiểm thử | JUnit 5, MockMvc, Spring Security Test, H2 | Integration test độc lập với MySQL |
| Triển khai | Docker, Render, Vercel, Aiven MySQL | Đóng gói và vận hành sản phẩm |
| Công cụ | Git/GitHub, Maven, npm, Postman | Quản lý mã nguồn, build và kiểm tra API |

Lưu ý: tài liệu định hướng ban đầu có nhắc PostgreSQL, nhưng code và môi trường triển khai hiện tại sử dụng MySQL. Khi trình bày phải lấy code hiện tại làm căn cứ.

## 3. Kiến trúc và luồng xử lý

```text
Người dùng
    ↓
React page/component
    ↓
Frontend service + Axios
    ↓ HTTP/JSON, Authorization: Bearer <JWT>
Spring Security
    ↓
Controller
    ↓
Service + Transaction
    ↓
Repository + JPA/Hibernate
    ↓
MySQL
```

- Controller nhận request, kiểm tra dữ liệu đầu vào cơ bản và trả `ApiResponse<T>`.
- Service chứa nghiệp vụ, kiểm tra quyền sở hữu và điều phối transaction.
- Repository thực hiện JPQL, bulk update và truy vấn dữ liệu.
- Entity chỉ ánh xạ database; API sử dụng request/response DTO, không trả Entity trực tiếp.
- Frontend tập trung lời gọi API trong thư mục `services`, thay vì gọi Axios rải rác trong component.
- Route guard ở React hỗ trợ trải nghiệm, còn Spring Security mới là ranh giới phân quyền thực sự.

## 4. Phạm vi chức năng đã có trong code

### GUEST

- Xem trang chủ và các nhóm truyện nổi bật, mới cập nhật, nhiều lượt xem.
- Xem danh sách truyện có phân trang.
- Tìm kiếm theo tên, tác giả hoặc thể loại.
- Lọc theo thể loại, trạng thái; sắp xếp theo thời gian, lượt xem, điểm hoặc tên.
- Xem chi tiết truyện, danh sách chương đã xuất bản và đọc chương.
- Điều hướng chương trước/sau gần nhất, không giả định số chương liên tục.
- Xem bình luận và điểm đánh giá công khai.
- Đăng ký và đăng nhập.

### USER

- Xem, cập nhật hồ sơ và đổi mật khẩu.
- Theo dõi/bỏ theo dõi truyện.
- Lưu lịch sử, tiến độ đọc và tiếp tục từ chương gần nhất.
- Tạo, sửa, xóa bình luận của chính mình.
- Báo cáo bình luận.
- Tạo, cập nhật hoặc xóa đánh giá 1-5 sao.
- Tùy chỉnh cỡ chữ, giãn dòng và giao diện đọc.

### ADMIN

- CRUD truyện, trạng thái và chế độ PUBLIC/PRIVATE.
- CRUD chương, publish/unpublish và thay đổi thứ tự.
- CRUD thể loại; chặn xóa thể loại đang được sử dụng.
- Tìm kiếm người dùng, khóa/mở khóa và thay đổi role.
- Tìm kiếm, lọc, ẩn/khôi phục, xóa bình luận và xử lý báo cáo.
- Xem dashboard thống kê tổng số và các nội dung nổi bật.

## 5. Mô hình dữ liệu chính

Project hiện có 9 entity nghiệp vụ:

| Entity | Ý nghĩa và ràng buộc quan trọng |
|---|---|
| `User` | Username và email duy nhất; password lưu dưới dạng BCrypt hash; có role và trạng thái |
| `Story` | Slug duy nhất; chứa title, author, description, `coverUrl`, status, visibility và counter |
| `Genre` | Name và slug duy nhất; quan hệ nhiều-nhiều với Story |
| `Chapter` | Thuộc một Story; cặp `story_id + chapter_number` duy nhất |
| `Follow` | Cặp `user_id + story_id` duy nhất |
| `ReadingProgress` | Một tiến độ gần nhất cho mỗi cặp user-story |
| `Rating` | Một đánh giá cho mỗi cặp user-story; điểm 1-5 |
| `Comment` | Thuộc User và Story; hỗ trợ trạng thái ẩn và số lượt báo cáo |
| `CommentReport` | Một user chỉ báo cáo một comment một lần |

Ảnh bìa hiện được lưu dưới dạng URL tối đa 500 ký tự. Project chưa có API nhận file, lưu object storage, resize hoặc CDN; vì vậy không nên nói chức năng upload ảnh đã hoàn thành.

## 6. Các điểm kỹ thuật đáng trình bày

### 6.1. Xác thực và phân quyền

Khi đăng nhập, backend tìm tài khoản bằng username hoặc email, kiểm tra BCrypt và trạng thái tài khoản rồi phát JWT HS256 chứa định danh và role. Frontend lưu token và Axios interceptor gắn token vào header. Spring Security bảo vệ `/api/admin/**` bằng role ADMIN và bảo vệ các API cá nhân bằng trạng thái authenticated.

Backend phân biệt:

- `401 Unauthorized`: chưa đăng nhập hoặc token không hợp lệ.
- `403 Forbidden`: đã đăng nhập nhưng không đủ quyền.
- `404 Not Found`: dùng cho dữ liệu PRIVATE hoặc chưa publish để tránh tiết lộ tài nguyên tồn tại.

Quyền sở hữu bình luận được kiểm tra trong service, không phụ thuộc việc frontend có hiển thị nút sửa/xóa hay không.

### 6.2. Tìm kiếm, phân trang và hiệu năng

`StoryRepository` dùng JPQL với các bộ lọc tùy chọn. Query public luôn ép `visibility = PUBLIC`. Do join thể loại có thể nhân bản bản ghi, query dùng `distinct` và `countQuery` riêng. Sort key từ client được ánh xạ qua danh sách cho phép, không đưa trực tiếp tên cột tùy ý vào truy vấn.

API danh sách trả DTO tóm tắt, không trả nội dung chương. Quan hệ dùng lazy loading; các danh sách cần dữ liệu liên quan sử dụng `EntityGraph`, batch fetch hoặc truy vấn theo nhóm để giảm N+1.

### 6.3. Counter không làm sai `updatedAt`

Lượt xem và dữ liệu tổng hợp đánh giá được cập nhật bằng bulk query. Nhờ đó, một lượt đọc hoặc đánh giá không khiến truyện bị hiểu nhầm là vừa cập nhật nội dung. Chỉ thao tác nội dung như thêm/sửa chương mới chủ động cập nhật thời gian của Story.

### 6.4. Đổi thứ tự chương

Cặp Story và chapter number có unique constraint. Khi hoán đổi hai chương, service không thể đổi trực tiếp vì sẽ xuất hiện trạng thái trung gian có hai chương cùng số. Giải pháp là đưa một chương sang số âm tạm, flush, cập nhật chương còn lại rồi gán số cuối cùng. Toàn bộ thao tác nằm trong transaction để lỗi ở bất kỳ bước nào cũng rollback.

### 6.5. Xóa truyện và dữ liệu cũ

Project không dựa vào cascade không rõ ràng. Khi xóa Story, service xóa lần lượt comment report, comment, rating, reading progress, follow, chapter và các bản ghi bảng liên kết thể loại trước khi xóa Story.

Database đã từng tồn tại cả tên bảng cũ `story_categories` và tên hiện tại `story_genres`. Repository dọn liên kết kiểm tra metadata và xử lý cả hai bảng nếu tồn tại. Đây là ví dụ thực tế cho thấy code mới phải tính tới dữ liệu và schema cũ, không chỉ chạy đúng trên database vừa tạo.

## 7. API tiêu biểu

| Quyền | Endpoint | Mục đích |
|---|---|---|
| Public | `GET /api/stories` | Danh sách, tìm kiếm, lọc, sort và phân trang |
| Public | `GET /api/stories/{id}` | Chi tiết Story công khai |
| Public | `GET /api/stories/{id}/chapters` | Danh sách chương đã publish |
| Public | `GET /api/chapters/{id}` | Đọc chương và lấy prev/next |
| Public | `POST /api/auth/register` | Đăng ký USER |
| Public | `POST /api/auth/login` | Đăng nhập và nhận JWT |
| USER | `/api/me/**` | Hồ sơ, mật khẩu, lịch sử và thư viện cá nhân |
| USER | `/api/stories/{id}/follow` | Theo dõi/bỏ theo dõi |
| USER | `/api/stories/{id}/comments` | Tạo và xem bình luận |
| USER | `/api/stories/{id}/rating` | Đánh giá truyện |
| ADMIN | `/api/admin/stories/**` | Quản trị truyện và chương |
| ADMIN | `/api/admin/genres` | Quản trị thể loại |
| ADMIN | `/api/admin/users` | Quản trị tài khoản |
| ADMIN | `/api/admin/comments` | Kiểm duyệt bình luận |
| ADMIN | `/api/admin/stats` | Dashboard thống kê |

## 8. Kiểm thử và triển khai

Backend hiện có 18 integration test, gồm:

- Spring context khởi động.
- Visibility của Story, Chapter và Comment.
- Phân quyền anonymous, USER và ADMIN.
- Tìm kiếm theo tác giả và thể loại.
- Đăng nhập bằng email và nhận JWT.
- Điều hướng chương, tăng lượt xem không làm đổi `updatedAt`.
- Reorder chương và bảo toàn unique constraint.
- Xóa truyện cùng toàn bộ dữ liệu phụ thuộc.

Kết quả đã xác minh: `18 tests`, không có failure/error; frontend production build thành công với `135 modules transformed`.

Môi trường triển khai được mô tả trong code và README:

- Frontend: Vercel.
- Backend: Render bằng Dockerfile.
- Database: Aiven MySQL.
- Các giá trị `DB_URL`, `JWT_SECRET`, `ADMIN_PASSWORD`, `CORS_ALLOWED_ORIGINS` và `VITE_API_BASE_URL` lấy từ biến môi trường.

## 9. Hạn chế cần nói trung thực

- Chưa có Flyway/Liquibase; local vẫn dùng `ddl-auto=update`.
- JWT chưa có refresh token và cơ chế thu hồi token.
- Ảnh bìa và avatar vẫn dùng URL, chưa upload file/CDN.
- Chưa có rate limiting, chống brute-force và monitoring chuyên sâu.
- Chưa có test tự động frontend; 18 test không có nghĩa toàn bộ hệ thống đã được bao phủ.
- H2 giúp test nhanh nhưng không giống hoàn toàn MySQL; vẫn cần kiểm tra trên MySQL trước khi triển khai.
- Spring JPA `open-in-view` chưa được tắt.

## 10. Bản nói tóm tắt kỹ thuật trong khoảng 60 giây

> MangaVault là website đọc truyện chữ được xây dựng theo kiến trúc client-server tách biệt. Frontend sử dụng React 19 và Axios; backend sử dụng Java 21, Spring Boot 3.5, Spring Security, JWT và JPA/Hibernate; dữ liệu được lưu trên MySQL. Hệ thống có ba vai trò GUEST, USER và ADMIN, hỗ trợ từ tìm kiếm, đọc truyện đến theo dõi, lịch sử, bình luận, đánh giá và quản trị. Backend được tổ chức theo controller-service-repository, dùng DTO thay vì trả Entity, kiểm tra quyền ở Spring Security và service. Những phần kỹ thuật đáng chú ý là truy vấn phân trang có filter, điều hướng chương không liên tục, bulk update counter không làm sai updatedAt, reorder chương bằng transaction và xóa Story an toàn qua nhiều bảng, kể cả schema cũ. Hiện backend có 18 integration test đạt và frontend build production thành công. Hạn chế còn lại là chưa có migration Flyway, refresh token, upload ảnh, rate limiting và test frontend.

## 11. Điều học được từ thực tập thực tế

Khác biệt lớn nhất so với bài tập web trên trường là một chức năng không kết thúc khi giao diện hiển thị đúng. Một thay đổi nhỏ có thể ảnh hưởng đồng thời đến request DTO, API response, quyền truy cập, transaction, foreign key, dữ liệu cũ, test và cấu hình triển khai.

Project giúp hình thành các kỹ năng:

- Đọc lỗi theo chuỗi browser - network - security - controller - service - repository - database.
- Thiết kế API contract và ranh giới quyền trước khi viết giao diện.
- Bảo vệ dữ liệu bằng cả kiểm tra nghiệp vụ và constraint database.
- Coi transaction, kiểm thử, Git, tài liệu và deployment là một phần của sản phẩm.
- Phân biệt chức năng đã hoàn thành với kế hoạch tương lai; không trình bày quá khả năng thật của code.
