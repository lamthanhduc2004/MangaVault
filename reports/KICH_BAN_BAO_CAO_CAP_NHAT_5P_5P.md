# KỊCH BẢN BÁO CÁO CẬP NHẬT MANGAVAULT

## 1. Nội dung thay đổi so với lần báo cáo trước

Ở lần báo cáo trước, project đã có các chức năng nền tảng như danh sách truyện, chi tiết truyện, đọc chương và quản trị truyện/chương. Sau lần báo cáo đó, project được bổ sung và hoàn thiện bảy nội dung chính:

1. Tìm kiếm truyện theo thể loại.
2. Theo dõi và bỏ theo dõi truyện.
3. Lưu lịch sử, tiến độ và tiếp tục đọc.
4. Bình luận; sửa/xóa bình luận của chính mình.
5. Xác thực và phân quyền bằng JWT.
6. Mã hóa mật khẩu bằng BCrypt.
7. Giảm N+1 Query bằng `EntityGraph`, batch fetch và truy vấn theo nhóm.

Thông điệp chính của buổi báo cáo:

> Sau lần báo cáo trước, MangaVault được mở rộng từ website chủ yếu phục vụ việc đọc và quản trị nội dung thành hệ thống có tài khoản, thư viện cá nhân, tương tác người dùng, bảo mật backend và tối ưu truy vấn dữ liệu.

## 2. Chuẩn bị trước khi demo

### Tài khoản và dữ liệu

- Một tài khoản USER đã kiểm tra đăng nhập thành công.
- USER đã theo dõi sẵn ít nhất một truyện để trang theo dõi không trống.
- Một truyện PUBLIC có ít nhất ba chương đã publish.
- Chuẩn bị một bình luận ngắn: `Truyện hay, giao diện đọc rõ ràng.`
- Ghi nhớ email và mật khẩu tài khoản USER để demo đăng nhập.

### Các tab nên mở sẵn

1. Trang đăng nhập.
2. Trang danh sách truyện.
3. Trang chi tiết truyện được chọn.
4. Source code tại `SecurityConfig`, `LibraryService` hoặc `FollowRepository` nếu giảng viên yêu cầu xem kỹ thuật.

Không demo bằng tài khoản ADMIN trong 5 phút chính vì nội dung lần này tập trung vào các chức năng mới của USER và bảo mật.

## 3. Kịch bản trình bày và demo đúng 5 phút

### 0:00-0:30 - Giới thiệu phạm vi cập nhật

Nhìn giảng viên và nói:

> Em là Lâm Thành Đức, mã sinh viên B22DCCN227. Ở lần báo cáo trước, MangaVault đã có luồng xem danh sách, xem chi tiết, đọc chương và quản trị nội dung. Sau lần đó, em tập trung bổ sung tìm kiếm theo thể loại, thư viện cá nhân gồm theo dõi và lịch sử, chức năng bình luận, xác thực JWT, mã hóa BCrypt và tối ưu N+1 Query. Em xin demo trực tiếp các phần cập nhật này.

### 0:30-1:10 - Demo đăng nhập, giải thích JWT và BCrypt

Thao tác:

1. Nhập email hoặc username của USER.
2. Nhập mật khẩu và đăng nhập.
3. Chỉ nhanh giao diện đã chuyển sang trạng thái đăng nhập.

Lời nói:

> Khi người dùng đăng nhập, frontend gửi username hoặc email cùng mật khẩu đến `/api/auth/login`. Backend tìm tài khoản, dùng BCrypt để so khớp mật khẩu với password hash trong database rồi phát JWT HS256 chứa định danh và role. Axios lưu và gắn token vào header `Authorization: Bearer` cho các request sau. Backend stateless, không lưu session. Nếu không có token thì API cá nhân trả 401; có token nhưng không đủ role thì trả 403.

Nếu giảng viên hỏi ngay về BCrypt:

> BCrypt là hàm băm một chiều có salt, nên hai mật khẩu giống nhau vẫn có thể tạo hash khác nhau. Hệ thống không giải mã mật khẩu; lúc đăng nhập chỉ dùng `matches` để so sánh mật khẩu nhập vào với hash đã lưu.

### 1:10-1:50 - Demo tìm kiếm theo thể loại

Thao tác:

1. Mở trang chủ hoặc danh sách truyện.
2. Chọn một chip thể loại hoặc nhập tên thể loại vào ô tìm kiếm.
3. Chỉ kết quả được lọc.

Lời nói:

> Chức năng tìm kiếm đã được mở rộng để tìm theo tên thể loại, ngoài tên truyện và tác giả. Frontend gửi keyword hoặc `genreSlug` xuống API chứ không tự lọc dữ liệu đã tải. Backend join quan hệ nhiều-nhiều giữa Story và Genre. Do join có thể tạo nhiều dòng cho cùng một Story, JPQL dùng `distinct` và một `countQuery` riêng để phân trang vẫn chính xác.

### 1:50-2:35 - Demo theo dõi truyện

Thao tác:

1. Mở chi tiết một truyện.
2. Nhấn `Theo dõi`.
3. Mở trang `Đang theo dõi` để chứng minh truyện xuất hiện.
4. Nếu đủ thời gian, nhấn bỏ theo dõi rồi theo dõi lại.

Lời nói:

> Theo dõi là chức năng dành cho tài khoản đã đăng nhập. Backend lấy user hiện tại từ JWT, không nhận user ID do frontend tự gửi. Bảng Follow có unique constraint trên cặp `user_id` và `story_id`, nên một người không thể theo dõi trùng cùng một truyện. Trang danh sách theo dõi dùng phân trang và tải thông tin Story liên quan bằng EntityGraph để tránh truy vấn riêng cho từng phần tử.

### 2:35-3:25 - Demo lịch sử và tiếp tục đọc

Thao tác:

1. Mở một chương của truyện.
2. Chuyển sang chương tiếp theo.
3. Mở trang `Lịch sử đọc`.
4. Chỉ chương gần nhất và nút tiếp tục đọc.

Lời nói:

> Khi USER đọc chương, frontend gửi yêu cầu lưu tiến độ. Backend upsert theo cặp user-story nên mỗi truyện chỉ có một vị trí đọc gần nhất. Khi đọc chương mới, bản ghi cũ được cập nhật thay vì tạo lịch sử trùng. Trang lịch sử hiển thị chương gần nhất và cho phép tiếp tục đọc. Dữ liệu này được bảo vệ bằng JWT; người dùng chỉ xem được lịch sử của chính mình.

### 3:25-4:10 - Demo bình luận và quyền sở hữu

Thao tác:

1. Quay lại trang chi tiết truyện.
2. Nhập bình luận: `Truyện hay, giao diện đọc rõ ràng.`
3. Gửi bình luận.
4. Chỉ các nút sửa/xóa trên bình luận vừa tạo.
5. Có thể sửa một vài chữ để chứng minh cập nhật.

Lời nói:

> USER đã đăng nhập có thể thêm, sửa và xóa bình luận của chính mình. Backend lấy user từ JWT, tải Comment rồi so sánh chủ sở hữu trước khi cho cập nhật hoặc xóa. Việc frontend chỉ hiển thị nút trên bình luận của người hiện tại phục vụ trải nghiệm; kiểm tra quyền thật nằm trong service. Danh sách bình luận dùng phân trang và EntityGraph để lấy tác giả cùng dữ liệu bình luận, tránh N+1 Query.

### 4:10-4:32 - Giải thích N+1 Query

Giữ trang theo dõi hoặc bình luận trên màn hình và nói:

> N+1 xảy ra khi hệ thống chạy một query lấy danh sách N bản ghi, sau đó chạy thêm một query cho từng quan hệ, tổng cộng thành 1 cộng N query. Ví dụ lấy 20 Follow rồi truy vấn Story 20 lần. Project xử lý bằng `EntityGraph` ở Follow, ReadingProgress và Comment; cấu hình batch fetch mặc định 20; đồng thời dùng truy vấn theo nhóm cho dữ liệu bổ sung. Mục tiêu là giảm số lần truy cập database nhưng vẫn giữ quan hệ lazy, không chuyển toàn bộ sang eager loading.

Nếu cần chỉ code, mở một trong các dòng:

```java
@EntityGraph(attributePaths = "story")
```

hoặc:

```properties
spring.jpa.properties.hibernate.default_batch_fetch_size=20
```

### 4:32-4:50 - Trình bày xử lý Exception

Nói:

> Backend không tự xử lý lỗi rải rác trong từng controller. Lỗi nghiệp vụ được biểu diễn bằng `AppException` và `ErrorCode`, ví dụ không tìm thấy Story trả 404, đã theo dõi trả 409 và không phải chủ bình luận trả 403. `GlobalExceptionHandler` bắt exception rồi trả cùng cấu trúc `ApiResponse` gồm mã lỗi và thông điệp. Lỗi validation hoặc JSON sai trả 400. Riêng lỗi thiếu token và không đủ quyền xảy ra trong Spring Security trước controller, nên được xử lý bằng `AuthenticationEntryPoint` và `AccessDeniedHandler` để response vẫn thống nhất.

Ví dụ response lỗi:

```json
{
  "code": 4097,
  "message": "Story is already followed"
}
```

### 4:50-5:00 - Kết luận

> Các phần cập nhật đã hoàn thiện luồng từ đăng nhập an toàn đến tìm kiếm, theo dõi, lịch sử và bình luận. Backend hiện có 18 integration test đạt. Em học được rằng chức năng thực tế phải xử lý cả quyền, dữ liệu lỗi, hiệu năng và kiểm thử. Em xin kết thúc phần demo và sẵn sàng trả lời câu hỏi.

## 4. Kịch bản vấn đáp 5 phút

Mỗi câu trả lời nên kéo dài khoảng 20-35 giây. Trả lời trực tiếp, lấy ví dụ trong MangaVault rồi dừng.

### Câu 1. Cấu trúc tổng thể của project như thế nào?

> Project tách frontend và backend. Frontend React nằm trong thư mục `frontend`. Backend là Maven project Spring Boot, gồm controller, dto, entity, repository, service, security, config và exception. React gọi REST API qua Axios; backend xử lý và lưu dữ liệu vào MySQL bằng JPA/Hibernate.

### Câu 2. Luồng một request đi qua hệ thống thế nào?

> Người dùng thao tác trên React; component gọi frontend service; Axios gửi HTTP request và gắn JWT nếu có. Spring Security kiểm tra token và quyền. Controller nhận request, service xử lý nghiệp vụ, repository truy cập MySQL. Kết quả được map sang response DTO và trả JSON cho React.

### Câu 3. Controller, Service và Repository có trách nhiệm gì?

> Controller phụ trách HTTP, nhận request và trả response. Service chứa business logic, transaction và kiểm tra ownership. Repository thực hiện truy vấn database. Ví dụ CommentController nhận yêu cầu sửa, CommentService kiểm tra người dùng có phải chủ bình luận, còn CommentRepository lưu thay đổi.

### Câu 4. Vì sao không trả Entity trực tiếp?

> Entity phản ánh database, còn DTO là contract với frontend. DTO giúp chỉ trả đúng trường cần thiết, tránh lộ password hash, tránh vòng lặp serialize quan hệ và giúp thay đổi database mà ít ảnh hưởng API. Request DTO cũng là nơi đặt Bean Validation.

### Câu 5. JWT hoạt động như thế nào?

> Sau khi kiểm tra thông tin đăng nhập, backend ký JWT HS256 chứa user ID và role. Frontend gửi token trong header Bearer. Spring Security giải mã token và tạo authentication cho request. JWT giúp backend stateless nhưng phiên bản hiện tại chưa có refresh token hoặc revocation.

### Câu 6. BCrypt khác mã hóa thông thường thế nào?

> BCrypt là hàm băm một chiều, không phải mã hóa có thể giải mã lại. Nó tự tạo salt và có cost để làm chậm brute-force. Database chỉ lưu hash. Khi đăng nhập, backend dùng `passwordEncoder.matches` để kiểm tra mật khẩu nhập vào.

### Câu 7. Làm sao bảo đảm user không sửa bình luận người khác?

> Spring Security trước hết yêu cầu request có JWT. Sau đó service lấy user hiện tại từ token, tải Comment và so sánh `comment.user.id`. Nếu không phải chủ sở hữu thì từ chối. Frontend ẩn nút chỉ hỗ trợ UX, không thay thế kiểm tra backend.

### Câu 8. Theo dõi truyện tránh dữ liệu trùng thế nào?

> Backend lấy user hiện tại từ JWT và Story từ path ID. Database đặt unique constraint cho cặp user-story. Service có thể kiểm tra trước để trả lỗi dễ hiểu, nhưng constraint database vẫn là lớp bảo vệ cuối cùng khi có request đồng thời.

### Câu 9. Lịch sử đọc được lưu như thế nào?

> Hệ thống không tạo một dòng cho mọi lần mở chương. Nó lưu ReadingProgress duy nhất theo cặp user-story, gồm chapter gần nhất và thời gian cập nhật. Khi đọc chương khác, service upsert bản ghi đó. Nhờ vậy trang lịch sử có thể hiển thị vị trí gần nhất và chức năng đọc tiếp.

### Câu 10. N+1 Query là gì?

> N+1 là một query lấy danh sách và N query bổ sung để tải quan hệ của từng phần tử. Ví dụ một query lấy 20 Follow rồi 20 query lấy Story. Điều này tăng thời gian và tải database khi dữ liệu lớn.

### Câu 11. Project giải quyết N+1 như thế nào?

> Project giữ quan hệ lazy để tránh tải thừa, nhưng dùng EntityGraph ở các danh sách biết chắc cần quan hệ như Follow-Story, ReadingProgress-Story và Comment-User. Hibernate batch fetch được đặt là 20, và một số dữ liệu bổ sung được lấy bằng query theo nhóm. Không chuyển tất cả quan hệ sang eager vì eager cũng có thể gây query lớn và tải dữ liệu không cần thiết.

### Câu 12. Vì sao tìm kiếm theo thể loại cần `distinct`?

> Story và Genre là quan hệ nhiều-nhiều. Khi join, một Story có nhiều Genre có thể xuất hiện ở nhiều dòng SQL. `distinct` loại Story trùng trong kết quả; `countQuery` dùng `count(distinct s)` để tổng số phần tử và số trang chính xác.

### Câu 13. 401 và 403 khác nhau thế nào?

> 401 là chưa xác thực hoặc token không hợp lệ. 403 là đã xác thực nhưng không đủ quyền, ví dụ USER gọi `/api/admin/**`. Hai lỗi này thường được xử lý trong Spring Security filter chain trước khi request tới controller.

### Câu 14. Vì sao chọn React và Spring Boot?

> React phù hợp với SPA có nhiều state như đăng nhập, tìm kiếm, theo dõi và lịch sử. Spring Boot cung cấp REST API, validation, Security và JPA theo cấu trúc rõ ràng. Việc tách hai phần giúp frontend và backend phát triển, build và triển khai độc lập.

### Câu 15. Vì sao dùng MySQL và JPA/Hibernate?

> Project có dữ liệu quan hệ và nhiều constraint nên MySQL phù hợp. JPA/Hibernate giảm code ánh xạ và hỗ trợ repository, transaction, pagination. Tuy nhiên em vẫn phải hiểu SQL, foreign key, lazy loading và N+1 vì ORM không tự tối ưu mọi trường hợp.

### Câu 16. Project xử lý Exception như thế nào?

> Service ném `AppException` kèm một `ErrorCode`. Mỗi ErrorCode quy định mã ứng dụng, thông điệp và HTTP status, ví dụ `STORY_NOT_FOUND` là 404 hoặc `ALREADY_FOLLOWED` là 409. `GlobalExceptionHandler` dùng `@RestControllerAdvice` để bắt lỗi và chuyển thành `ApiResponse` thống nhất. Nhờ đó controller không phải lặp lại try-catch và frontend có cùng một cách đọc lỗi.

### Câu 17. Tại sao lỗi 401/403 không chỉ xử lý trong `GlobalExceptionHandler`?

> Token thiếu, token sai hoặc không đủ role bị chặn trong Spring Security filter chain trước khi request tới controller. Vì vậy các lỗi này chưa đi vào phạm vi của `@RestControllerAdvice`. Project cấu hình `AuthenticationEntryPoint` cho 401 và `AccessDeniedHandler` cho 403, sau đó ghi response JSON cùng cấu trúc `ApiResponse`.

### Câu 18. Project đang xử lý những nhóm lỗi nào?

> Có lỗi validation và request sai trả 400; sai đăng nhập trả 401; không đủ quyền hoặc sai ownership trả 403; không tìm thấy tài nguyên trả 404; dữ liệu trùng hoặc xung đột trả 409. JSON không đọc được và query parameter sai kiểu cũng được chuyển thành 400 thay vì để thành lỗi 500.

### Câu 19. Exception handling hiện còn hạn chế gì?

> `GlobalExceptionHandler` hiện xử lý rõ lỗi nghiệp vụ, validation, JSON sai và parameter sai kiểu, nhưng chưa có handler tổng quát cho mọi exception hệ thống. Vì vậy lỗi ngoài dự kiến có thể rơi về response mặc định của Spring. Nếu hoàn thiện production, em sẽ bổ sung handler 500 dùng thông điệp chung cho client, ghi log chi tiết ở server và không trả stack trace hoặc thông tin database ra ngoài.

## 5. Những gì học được sau thực tập so với học web trên lớp

### 5.1. Một chức năng là một luồng end-to-end

Trên lớp, một chức năng thường được nhìn theo luồng form-controller-database. Trong project thực tế, chức năng theo dõi cần đồng thời:

1. Nút và state trên React.
2. Frontend service gọi API.
3. Axios gắn JWT.
4. Spring Security yêu cầu đăng nhập.
5. Controller nhận Story ID.
6. Service lấy user hiện tại và xử lý nghiệp vụ.
7. Repository truy cập dữ liệu.
8. Database đặt unique constraint user-story.
9. Test kiểm tra trường hợp đúng và dữ liệu trùng.

Cách nói:

> Em học được rằng một tính năng không chỉ là một nút trên giao diện. Nó là một luồng xuyên suốt API contract, authentication, business logic, database và kiểm thử.

### 5.2. Bảo mật không chỉ là làm màn hình đăng nhập

Trên lớp, đăng nhập đôi khi chỉ là kiểm tra tài khoản rồi lưu trạng thái. Trong project thực tế phải quan tâm:

- Mật khẩu không được lưu plaintext.
- JWT phải có chữ ký, thời hạn và role.
- Phải phân biệt 401 với 403.
- Backend phải kiểm tra ownership.
- Secret không được commit lên GitHub.
- Frontend ẩn nút không phải cơ chế bảo mật.

Cách nói:

> Qua JWT và BCrypt, em hiểu xác thực là xác định người dùng, còn phân quyền là xác định họ được làm gì. Cả hai phải được enforce ở backend.

### 5.3. Phải nghĩ tới tính toàn vẹn dữ liệu

Theo dõi và lịch sử đọc đều có nguy cơ tạo bản ghi trùng. Project sử dụng cả kiểm tra nghiệp vụ và unique constraint.

Cách nói:

> Em học được database constraint là lớp bảo vệ cuối cùng. Chỉ kiểm tra ở giao diện hoặc service vẫn có thể sai khi hai request chạy đồng thời.

### 5.4. Chạy đúng chưa chắc đã chạy hiệu quả

Một trang danh sách có thể hiển thị đúng nhưng âm thầm chạy hàng chục query. N+1 chỉ rõ khi quan sát log SQL hoặc khi dữ liệu lớn.

Cách nói:

> Trước đây em chủ yếu kiểm tra kết quả hiển thị. Sau thực tập, em chú ý thêm số lượng query, kích thước response, phân trang và dữ liệu thực sự cần tải.

### 5.5. Biết phân tích lỗi theo tầng

```text
React state
  → Axios request
  → HTTP status
  → Spring Security
  → Controller
  → Service
  → Repository
  → MySQL
```

Cách nói:

> Em học được cách xác định lỗi thuộc tầng nào trước khi sửa. Ví dụ 401 thường liên quan token hoặc Security, còn dữ liệu trùng và foreign key thường nằm ở service, repository hoặc database.

### 5.6. Test và triển khai là một phần của sản phẩm

Trên lớp, sản phẩm thường chỉ cần chạy localhost. Trong quá trình thực tập còn phải quản lý Git, biến môi trường, CORS, database production, build frontend/backend và integration test.

Cách kết luận:

> Sự thay đổi lớn nhất của em là chuyển từ tư duy “viết code để chạy được” sang “xây hệ thống có thể bảo mật, kiểm thử, triển khai và bảo trì”.

## 6. Câu trả lời tổng hợp cho câu hỏi “Sau thực tập em học được gì?”

> Sau thực tập, em hiểu rằng làm web thực tế không chỉ là tạo giao diện và CRUD. Với chức năng theo dõi, lịch sử và bình luận, em phải thiết kế API, gắn JWT, kiểm tra quyền sở hữu, đặt unique constraint và viết test. Khi tối ưu N+1, em nhận ra một trang hiển thị đúng vẫn có thể không hiệu quả nếu mỗi bản ghi tạo thêm một query. Việc sử dụng BCrypt cũng giúp em hiểu mật khẩu phải được băm một chiều chứ không lưu hoặc mã hóa để giải mã lại. So với học trên lớp, em quan tâm nhiều hơn đến trường hợp lỗi, bảo mật, dữ liệu trùng, hiệu năng, môi trường triển khai và khả năng bảo trì lâu dài.

## 7. Checklist trước khi báo cáo

- [ ] Backend và frontend đang chạy bằng code mới nhất.
- [ ] Đăng nhập USER bằng username hoặc email thành công.
- [ ] Có thể loại chắc chắn trả về kết quả tìm kiếm.
- [ ] Truyện demo chưa được USER theo dõi hoặc biết rõ trạng thái hiện tại.
- [ ] Có truyện với ít nhất ba chương để tạo lịch sử đọc.
- [ ] Bình luận demo có thể thêm, sửa và xóa.
- [ ] Trang theo dõi và lịch sử đã có dữ liệu để tránh màn hình trống.
- [ ] Tab source code `SecurityConfig`, `FollowRepository` và `CommentRepository` mở sẵn.
- [ ] Đã tập thử và hoàn thành dưới 5 phút.
- [ ] Có ảnh hoặc video dự phòng nếu môi trường demo gặp sự cố.
