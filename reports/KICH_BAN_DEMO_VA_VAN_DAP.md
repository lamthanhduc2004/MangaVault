# KỊCH BẢN DEMO VÀ VẤN ĐÁP MANGAVAULT

## 1. Mục tiêu

- Tổng thời gian: 10 phút.
- Trình bày kết hợp demo: đúng 5 phút.
- Hỏi đáp với giảng viên: khoảng 5 phút.
- Thông điệp cần làm rõ: đây là sản phẩm end-to-end có giao diện, REST API, bảo mật, dữ liệu, kiểm thử và triển khai; không chỉ là một bài CRUD.

Nên demo local để tránh cold start của Render. Link production chỉ dùng làm bằng chứng sản phẩm đã được triển khai.

## 2. Chuẩn bị trước buổi demo

### 2.1. Chạy project

Cửa sổ PowerShell 1 - MySQL:

```powershell
cd C:\PROJECTS\THUCTAP
$env:DB_PASSWORD = "manga_pass"
docker compose up -d
docker compose ps
```

Chờ container `mangavault-mysql` ở trạng thái healthy.

Cửa sổ PowerShell 2 - backend:

```powershell
cd C:\PROJECTS\THUCTAP
$env:DB_PASSWORD = "manga_pass"
$env:JWT_SECRET = "mangavault-demo-secret-key-2026-change-me"
$env:ADMIN_USERNAME = "admin"
$env:ADMIN_PASSWORD = "admin123"
.\mvnw.cmd spring-boot:run
```

Kiểm tra backend:

```powershell
Invoke-RestMethod http://localhost:8080/api/health
```

Cửa sổ PowerShell 3 - frontend:

```powershell
cd C:\PROJECTS\THUCTAP\frontend
npm install
$env:VITE_API_BASE_URL = "http://localhost:8080/api"
npm run dev
```

Mở `http://localhost:5173`.

### 2.2. Dữ liệu và tài khoản chuẩn bị sẵn

- Kiểm tra trước tài khoản ADMIN thực sự đăng nhập được. Database cũ có thể không còn dùng mật khẩu bootstrap `admin123`.
- Tạo trước một USER: `sinhvien_demo` / `Demo@123456`.
- Chọn trước một truyện PUBLIC có ít nhất ba chương đã publish.
- Tạo riêng một truyện `Truyện demo báo cáo` với một thể loại và một chương. Đây là dữ liệu dùng để sửa/xóa trong demo.
- Viết sẵn một bình luận ngắn bằng USER và báo cáo bình luận đó nếu muốn trình diễn moderation.

### 2.3. Chuẩn bị ba phiên trình duyệt

1. Cửa sổ GUEST chưa đăng nhập, mở trang chủ.
2. Cửa sổ USER đã đăng nhập, mở chi tiết truyện được chọn.
3. Cửa sổ ADMIN đã đăng nhập, mở `/admin/dashboard`.

Có thể dùng Chrome thường, Chrome ẩn danh và Edge để tránh dùng chung `localStorage`.

## 3. Kịch bản trình bày và demo đúng 5 phút

### 0:00-0:35 - Giới thiệu đề tài

Không thao tác trên màn hình. Nhìn giảng viên và nói:

> Em là Lâm Thành Đức, mã sinh viên B22DCCN227. Đề tài của em là MangaVault, website đọc truyện chữ trực tuyến dành cho ba nhóm khách, thành viên và quản trị viên. Mục tiêu của project là xây dựng một luồng hoàn chỉnh từ React, REST API, xác thực JWT đến MySQL, kiểm thử và triển khai, thay vì chỉ dừng ở giao diện CRUD.

### 0:35-1:05 - Trình bày kiến trúc

Mở nhanh sơ đồ kiến trúc trong báo cáo hoặc giữ trang chủ trên màn hình và nói:

> Frontend sử dụng React 19, React Router và Axios. Backend sử dụng Java 21, Spring Boot 3.5, Spring Security và JPA/Hibernate; database hiện tại là MySQL 8. Request đi từ component qua frontend service, vào Spring Security, controller, service, repository rồi database. Controller giữ mỏng, nghiệp vụ và transaction nằm ở service, còn API trả DTO chứ không trả trực tiếp Entity.

### 1:05-1:55 - Demo GUEST

Thao tác:

1. Ở trang chủ, chỉ nhanh các nhóm truyện.
2. Mở danh sách truyện.
3. Tìm theo một từ khóa đã chuẩn bị; chọn thể loại hoặc trạng thái.
4. Mở chi tiết truyện rồi mở một chương.
5. Bấm chương kế tiếp hoặc thay đổi cỡ chữ.

Lời nói:

> Khách có thể tìm theo tên, tác giả hoặc thể loại; kết hợp lọc, sắp xếp và phân trang. API danh sách chỉ trả dữ liệu tóm tắt, không tải nội dung chương. Khi đọc, backend chỉ cho truy cập Story PUBLIC và Chapter đã publish. Điều hướng tìm chương gần nhất nhỏ hơn hoặc lớn hơn nên không bị sai nếu số chương không liên tục.

### 1:55-2:50 - Demo USER

Chuyển sang cửa sổ USER và thao tác:

1. Theo dõi truyện.
2. Đánh giá sao.
3. Thêm một bình luận ngắn.
4. Mở lịch sử đọc hoặc danh sách đang theo dõi.

Lời nói:

> Sau đăng nhập, backend phát JWT và Axios gắn token vào header Authorization. Thành viên có thể theo dõi, lưu tiến độ, đọc tiếp, bình luận, báo cáo và đánh giá. Database đặt unique constraint để một user không theo dõi hoặc đánh giá trùng một truyện. Người dùng chỉ được sửa hoặc xóa bình luận của chính mình; backend kiểm tra quyền sở hữu, không dựa vào việc frontend ẩn nút.

### 2:50-4:20 - Demo ADMIN

Chuyển sang cửa sổ ADMIN và thao tác:

1. Mở dashboard, chỉ các thống kê chính.
2. Mở quản trị truyện, tìm `Truyện demo báo cáo`.
3. Mở danh sách chương và chỉ trạng thái publish cùng nút đổi thứ tự.
4. Quay lại danh sách truyện và xóa đúng `Truyện demo báo cáo`.
5. Nếu còn thời gian, mở quản lý bình luận và chỉ bộ lọc reported/hidden.

Lời nói:

> Toàn bộ API `/api/admin/**` yêu cầu role ADMIN ở Spring Security. Admin quản lý truyện, chương, thể loại, tài khoản, bình luận và thống kê. Reorder chương dùng transaction và một số thứ tự tạm để không vi phạm unique constraint. Khi xóa truyện, service xóa dữ liệu phụ thuộc theo đúng thứ tự và xử lý cả bảng liên kết thể loại cũ lẫn mới trước khi xóa Story, nhờ đó không bị lỗi khóa ngoại.

Nếu không muốn xóa trong buổi demo, chỉ mở hộp xác nhận rồi hủy và nói quy trình. Tuy nhiên bản hiện tại đã có integration test cho xóa truyện cùng dữ liệu phụ thuộc.

### 4:20-5:00 - Kết quả và bài học

Dừng thao tác, kết luận:

> Backend hiện có 18 integration test đều đạt và frontend build production thành công với 135 module. Khác biệt lớn nhất so với bài tập web trên trường là một chức năng phải được xem xuyên suốt từ UI, API contract, security, transaction, dữ liệu cũ, kiểm thử đến triển khai. Project vẫn còn các giới hạn như chưa có Flyway, refresh token, upload ảnh, rate limiting và test frontend. Em xin kết thúc phần trình bày và sẵn sàng trả lời câu hỏi của thầy cô.

## 4. Phương án rút gọn khi bị chậm thời gian

Nếu đến phút thứ ba mà chưa xong phần USER:

- Bỏ thao tác bình luận và lịch sử.
- Chuyển thẳng sang dashboard ADMIN.
- Chỉ mở truyện demo, chỉ nút quản lý chương và xóa truyện demo.
- Giữ nguyên 40 giây kết luận cuối vì đây là phần thể hiện kiến thức thực tập.

## 5. Kịch bản vấn đáp 5 phút

Mỗi câu trả lời trong 20-35 giây: trả lời thẳng ý chính, lấy một ví dụ trong MangaVault rồi dừng.

### Câu 1. Vì sao chọn React và Spring Boot?

> React phù hợp với SPA có nhiều trạng thái như tìm kiếm, bộ lọc, đăng nhập và trình đọc. Spring Boot cung cấp cấu trúc REST API, validation, Security và JPA rõ ràng. Tách frontend-backend giúp hai phần build và triển khai độc lập, đồng thời API có thể phục vụ thêm mobile client nếu phát triển sau này.

### Câu 2. Luồng đăng nhập JWT hoạt động thế nào?

> Client gửi username hoặc email cùng mật khẩu. Backend tìm user, so khớp BCrypt, kiểm tra trạng thái rồi ký JWT HS256 chứa uid và role. Frontend lưu token và Axios gắn `Authorization: Bearer <token>`. Spring Security giải mã token trước khi request đi tới controller.

### Câu 3. 401 và 403 khác nhau thế nào?

> 401 là chưa xác thực hoặc token không hợp lệ. 403 là đã xác thực nhưng không đủ quyền, ví dụ USER gọi API ADMIN. Hai lỗi này xảy ra trong security filter chain nên project cấu hình entry point và access denied handler riêng để giữ response nhất quán.

### Câu 4. Vì sao dùng DTO thay vì trả Entity?

> Entity phản ánh cấu trúc database, còn DTO là contract với client. DTO giúp giới hạn trường được nhận và trả, tránh lộ password hash, tránh vòng lặp serialize quan hệ và giảm phụ thuộc giữa schema với frontend.

### Câu 5. Tại sao business logic nằm ở Service?

> Controller chỉ nhận request và trả response. Service tập trung nghiệp vụ, kiểm tra ownership và transaction nên dễ kiểm thử và tái sử dụng. Repository chỉ chịu trách nhiệm truy vấn dữ liệu. Ví dụ toàn bộ thứ tự xóa dữ liệu phụ thuộc của Story nằm trong `StoryService`, không nằm trong controller.

### Câu 6. Reorder chương tránh trùng số như thế nào?

> Database đặt unique constraint trên cặp story và chapter number. Nếu đổi trực tiếp 1 thành 2 sẽ xung đột với chương 2. Service đưa một chương sang số âm tạm, flush, cập nhật chương còn lại rồi gán số cuối cùng. Transaction bảo đảm hoặc thành công toàn bộ hoặc rollback.

### Câu 7. Xóa truyện có nhiều bảng liên quan như thế nào?

> Story không dùng cascade mơ hồ. Trong một transaction, service xóa report, comment, rating, reading progress, follow, chapter và liên kết thể loại rồi mới xóa Story. Database thực tế từng có cả `story_categories` và `story_genres`, nên lớp cleanup kiểm tra metadata và dọn cả hai nếu tồn tại. Đây là lỗi thực tế do tương thích schema cũ mà em đã gặp và xử lý.

### Câu 8. Hệ thống xử lý hiệu năng danh sách như thế nào?

> API danh sách có phân trang và chỉ trả dữ liệu tóm tắt, không trả chapter content. Quan hệ dùng lazy loading; một số repository dùng EntityGraph và batch fetch để giảm N+1. Rating trung bình và lượt xem được lưu/cập nhật bằng bulk query để hỗ trợ sắp xếp hiệu quả.

### Câu 9. Vì sao đọc chương không cập nhật `updatedAt`?

> `updatedAt` biểu thị nội dung truyện vừa thay đổi. Nếu mỗi lượt đọc làm đổi trường này thì danh sách mới cập nhật sẽ sai. Vì vậy lượt xem được tăng bằng bulk update riêng; chỉ thao tác nội dung như thêm hoặc sửa chương mới cập nhật thời gian Story.

### Câu 10. Đã kiểm thử những gì?

> Backend có 18 integration test cho context, visibility, phân quyền, tìm kiếm, đăng nhập bằng email, điều hướng chương, lượt xem, reorder và xóa truyện có dữ liệu phụ thuộc. Test dùng H2 để chạy nhanh và độc lập, nhưng H2 không thay thế hoàn toàn MySQL nên em vẫn kiểm tra trường hợp xóa truyện trên MySQL thực tế.

### Câu 11. Tại sao chưa dùng upload ảnh bìa?

> Phiên bản hiện tại chỉ lưu URL ảnh tối đa 500 ký tự. Upload file sẽ cần thêm kiểm tra định dạng và dung lượng, nơi lưu object storage, URL công khai, xóa file rác, resize và CDN. Đây là một hạng mục riêng có ảnh hưởng backend, hạ tầng và bảo mật nên em ghi đúng là hướng phát triển, không coi là chức năng đã hoàn thành.

### Câu 12. Khác biệt lớn nhất với học web ở trường là gì?

> Bài tập ở trường thường có đề bài ổn định, dữ liệu nhỏ và tập trung happy path. Trong dự án thực tế, một thay đổi nhỏ có thể ảnh hưởng UI, API, security, transaction, schema cũ, test và cloud. Em học được cách đọc lỗi theo từng tầng và coi deployment, Git, test cùng tài liệu là một phần của sản phẩm.

### Câu 13. Project đã hoàn thiện 100% chưa?

> Project đã hoàn thành phạm vi chức năng đề tài và có thể demo end-to-end, nhưng chưa phải production hoàn thiện tuyệt đối. Các phần còn thiếu gồm migration Flyway, refresh token/revocation, upload/CDN, rate limiting, monitoring, test frontend và CI. Em phân biệt rõ chức năng đã làm với kế hoạch phát triển tiếp theo.

### Câu 14. Nếu phát triển tiếp, ưu tiên việc gì?

> Em ưu tiên Flyway để quản lý schema, tắt open-in-view và bổ sung test cho auth, thư viện, bình luận, rating. Sau đó mới triển khai refresh token, upload ảnh, rate limiting, monitoring và CI. Đây là thứ tự ưu tiên độ ổn định trước khi mở rộng tính năng.

## 6. Checklist 15 phút trước khi trình bày

- [ ] Docker MySQL healthy.
- [ ] `GET /api/health` thành công.
- [ ] Frontend tải được trang chủ và ảnh bìa.
- [ ] Đăng nhập USER và ADMIN thành công.
- [ ] Có truyện PUBLIC với ít nhất ba chương publish.
- [ ] Có riêng `Truyện demo báo cáo` để sửa/xóa.
- [ ] Ba phiên trình duyệt đã mở đúng trang.
- [ ] Đóng tab chứa password, JWT secret hoặc thông tin riêng tư.
- [ ] Chạy `.\mvnw.cmd test`: 18 test đạt.
- [ ] Chạy `npm run build`: 135 module được transform.
- [ ] Mở sẵn PDF báo cáo và source code nếu giảng viên yêu cầu.
- [ ] Có ảnh chụp/video dự phòng các màn hình chính.

## 7. Xử lý sự cố khi demo

### Trang web không tải dữ liệu

```powershell
Invoke-RestMethod http://localhost:8080/api/health
```

Nếu health không phản hồi, kiểm tra terminal backend và MySQL. Nếu health tốt, kiểm tra `VITE_API_BASE_URL` rồi khởi động lại Vite vì biến môi trường được nhúng lúc build/start.

### Không kết nối được MySQL

```powershell
docker compose ps
docker compose logs mysql --tail 50
```

Kiểm tra `DB_PASSWORD` của backend phải khớp cấu hình Docker Compose.

### Admin không đăng nhập được

Database cũ có thể chứa admin với mật khẩu khác `admin123`. Không xóa volume ngay trước buổi báo cáo. Dùng tài khoản đã kiểm tra từ trước.

### Production bị cold start

Chuyển sang local và nói:

> Backend production sử dụng gói miễn phí nên có cold start. Em đã chuẩn bị môi trường local với cùng source code để bảo đảm demo ổn định.

### Xóa truyện vẫn báo lỗi sau khi vừa sửa code

Kiểm tra đã dừng và khởi động lại backend hay chưa. Java không tự nạp lại class nếu tiến trình cũ vẫn đang chạy. Không xóa dữ liệu chính để thử; chỉ dùng `Truyện demo báo cáo`.

## 8. Các cách diễn đạt cần tránh

- Không nói: “Sản phẩm hoàn thiện 100% và không còn lỗi.”
- Nên nói: “Sản phẩm hoàn thành phạm vi đề tài, có thể chạy end-to-end và đã xác định rõ các hạn chế còn lại.”
- Không nói: “Frontend chặn route nên hệ thống đã bảo mật.”
- Nên nói: “Spring Security và kiểm tra ownership ở backend là ranh giới bảo mật; frontend guard chỉ hỗ trợ UX.”
- Không nói: “18 test nghĩa là toàn bộ hệ thống đã được kiểm thử.”
- Nên nói: “18 test bao phủ các ranh giới quan trọng; frontend và một số luồng vẫn cần bổ sung test.”
- Không nói: “H2 giống hoàn toàn MySQL.”
- Nên nói: “H2 giúp test độc lập và nhanh, nhưng vẫn phải kiểm tra trên MySQL.”
- Không nói: “Project đã upload ảnh bìa.”
- Nên nói: “Project hiện lưu URL ảnh; upload file và CDN là hướng phát triển.”
