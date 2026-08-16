# TÓM TẮT CỰC NGẮN - MANGAVAULT

## Mở đầu

> MangaVault là website đọc truyện dùng React, Spring Boot và MySQL. So với lần báo cáo trước, em đã bổ sung tìm kiếm theo thể loại, theo dõi, lịch sử đọc, bình luận, JWT Authentication, BCrypt, xử lý Exception và tối ưu N+1 Query.

## Demo 5 phút

1. **Đăng nhập:** BCrypt kiểm tra password hash; backend phát JWT; Axios gắn Bearer Token.
2. **Tìm kiếm thể loại:** frontend gửi keyword/genreSlug; backend join Story-Genre, dùng `distinct` để tránh trùng.
3. **Theo dõi:** USER theo dõi truyện; unique `user_id + story_id` ngăn dữ liệu trùng.
4. **Lịch sử:** đọc chương rồi mở lịch sử; hệ thống lưu chương gần nhất theo user-story để đọc tiếp.
5. **Bình luận:** thêm/sửa bình luận; backend kiểm tra user có phải chủ sở hữu.
6. **N+1 Query:** dùng `EntityGraph`, lazy loading và batch fetch 20.
7. **Exception:** `AppException + ErrorCode + GlobalExceptionHandler`; 401/403 xử lý trong Spring Security.

## Cấu trúc project

```text
React → Axios/JWT → Spring Security → Controller → Service → Repository → MySQL
```

- Controller: nhận request, trả response.
- Service: nghiệp vụ, transaction, ownership.
- Repository: truy vấn database.
- DTO: contract API, không trả Entity trực tiếp.

## Các công nghệ chính

- Frontend: React 19, Vite, React Router, Axios.
- Backend: Java 21, Spring Boot 3.5.
- Security: JWT HS256, Spring Security, BCrypt.
- Database: MySQL, JPA/Hibernate.
- Test: JUnit, MockMvc, H2; hiện có 18 test đạt.

## Trả lời nhanh

- **JWT là gì?** Token có chữ ký, chứa user ID và role; backend stateless.
- **BCrypt là gì?** Hàm băm một chiều có salt; không giải mã password.
- **401/403?** 401 chưa xác thực; 403 đã đăng nhập nhưng không đủ quyền.
- **DTO/Entity?** Entity ánh xạ database; DTO là dữ liệu API được phép gửi/nhận.
- **N+1?** Một query danh sách cộng N query quan hệ; xử lý bằng EntityGraph và batch fetch.
- **Exception?** Service ném AppException; handler trả mã lỗi và HTTP status thống nhất.
- **Theo dõi trùng?** Unique constraint user-story.
- **Lịch sử lưu thế nào?** Upsert một ReadingProgress gần nhất cho mỗi user-story.
- **Quyền sửa bình luận?** Service so sánh user trong JWT với chủ Comment.

## Điều học được

> Trên lớp em chủ yếu làm happy path và chạy localhost. Sau thực tập, em hiểu một chức năng phải được xử lý xuyên suốt từ giao diện, API, authentication, authorization, validation, database constraint, hiệu năng query, exception, test đến triển khai. Em chuyển từ tư duy “code chạy được” sang “hệ thống có thể bảo mật, kiểm thử và bảo trì”.

## Kết thúc

> Backend hiện có 18 integration test đạt và frontend build production thành công. Sản phẩm hoàn thành phạm vi đề tài nhưng vẫn còn hướng phát triển như Flyway, refresh token, upload ảnh, rate limiting và test frontend.
