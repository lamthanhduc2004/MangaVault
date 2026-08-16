# CÁC ĐOẠN CODE TIÊU BIỂU CHO CHỨC NĂNG MỚI

Các đoạn dưới đây được rút gọn từ code thực tế để trình bày khi giảng viên hỏi. Không cần đọc toàn bộ code; chỉ cần giải thích mục đích và luồng xử lý.

## 1. Tìm kiếm truyện theo thể loại

File: `StoryRepository.java`

```java
@Query(value = """
        select distinct s from Story s left join s.genres g
        where s.visibility = com.daniel.mangavault.enums.Visibility.PUBLIC
          and (:keyword = ''
               or lower(s.title) like lower(concat('%', :keyword, '%'))
               or lower(s.author) like lower(concat('%', :keyword, '%'))
               or lower(g.name) like lower(concat('%', :keyword, '%')))
          and (:status is null or s.status = :status)
          and (:genreSlug is null or g.slug = :genreSlug)
        """,
        countQuery = """
        select count(distinct s) from Story s left join s.genres g
        where s.visibility = com.daniel.mangavault.enums.Visibility.PUBLIC
          and (...)
        """)
Page<Story> searchPublic(String keyword, StoryStatus status,
                         String genreSlug, Pageable pageable);
```

Giải thích:

- `left join s.genres g` nối Story với Genre.
- Keyword có thể khớp tên truyện, tác giả hoặc tên thể loại.
- `genreSlug` dùng khi người dùng chọn trực tiếp một thể loại.
- `distinct` tránh một Story xuất hiện nhiều lần khi có nhiều Genre.
- `count(distinct s)` giúp tổng số phần tử phân trang chính xác.

Câu nói ngắn:

> Frontend chỉ gửi điều kiện tìm kiếm; việc lọc thực hiện tại database. Vì Story-Genre là quan hệ nhiều-nhiều nên em dùng distinct và countQuery riêng để tránh trùng kết quả.

## 2. Theo dõi truyện

File: `LibraryService.java`

```java
public void followStory(String storyId) {
    User user = currentUserProvider.requireUser();
    Story story = storyRepository.findById(storyId)
            .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));

    if (followRepository.existsByUserIdAndStoryId(user.getId(), storyId)) {
        throw new AppException(ErrorCode.ALREADY_FOLLOWED);
    }

    followRepository.save(
            Follow.builder().user(user).story(story).build()
    );
}
```

Giải thích:

- User được lấy từ JWT, không nhận `userId` do frontend gửi.
- Kiểm tra Story có tồn tại.
- Kiểm tra đã theo dõi hay chưa.
- Database còn có unique constraint `user_id + story_id` để bảo vệ khi có request đồng thời.

Câu nói ngắn:

> Service lấy user hiện tại từ token và chỉ nhận Story ID từ client. Hệ thống kiểm tra ở service và có unique constraint ở database để tránh theo dõi trùng.

## 3. Lưu lịch sử và tiến độ đọc bằng upsert

File: `LibraryService.java`

```java
@Transactional
public void saveProgress(String chapterId) {
    User user = currentUserProvider.requireUser();
    Chapter chapter = chapterRepository.findById(chapterId)
            .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));

    String storyId = chapter.getStory().getId();
    ReadingProgress progress = readingProgressRepository
            .findByUserIdAndStoryId(user.getId(), storyId)
            .orElseGet(() -> ReadingProgress.builder()
                    .user(user)
                    .story(chapter.getStory())
                    .build());

    progress.setChapter(chapter);
    progress.setChapterNumber(chapter.getChapterNumber());
    readingProgressRepository.save(progress);
}
```

Giải thích:

- Tìm tiến độ theo cặp user-story.
- Nếu chưa có thì tạo mới; nếu đã có thì cập nhật chương gần nhất.
- Đây là thao tác upsert ở tầng nghiệp vụ.
- Mỗi user chỉ có một `ReadingProgress` cho một Story.
- `@Transactional` giữ thao tác đọc và cập nhật nhất quán.

Câu nói ngắn:

> Lịch sử không tạo một dòng cho mọi lần mở chương. Hệ thống chỉ cập nhật vị trí gần nhất của mỗi cặp user-story để hỗ trợ chức năng đọc tiếp.

## 4. Kiểm tra quyền sở hữu bình luận

File: `CommentService.java`

```java
public CommentResponse updateComment(String commentId, CommentRequest request) {
    User user = currentUserProvider.requireUser();
    Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));

    if (!comment.getUser().getId().equals(user.getId())) {
        throw new AppException(ErrorCode.NOT_RESOURCE_OWNER);
    }

    comment.setContent(request.getContent().trim());
    return mapToResponse(commentRepository.save(comment), user.getId());
}
```

Giải thích:

- JWT xác định user hiện tại.
- Service tải Comment và so sánh chủ sở hữu.
- Nếu không phải chủ sở hữu thì trả 403.
- Việc React ẩn nút sửa của người khác chỉ phục vụ UX; backend vẫn kiểm tra lại.

Câu nói ngắn:

> Spring Security kiểm tra người dùng đã đăng nhập, còn service kiểm tra người dùng có quyền trên đúng bản ghi Comment đó hay không.

## 5. Đăng nhập bằng BCrypt và phát JWT

File: `AuthService.java`

```java
public AuthResponse login(LoginRequest request) {
    String identifier = request.getUsername().trim();
    User user = userRepository
            .findByUsernameIgnoreCaseOrEmailIgnoreCase(identifier, identifier)
            .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        throw new AppException(ErrorCode.INVALID_CREDENTIALS);
    }

    if (user.getStatus() == UserStatus.BANNED) {
        throw new AppException(ErrorCode.ACCOUNT_LOCKED);
    }

    return AuthResponse.builder()
            .token(generateToken(user))
            .username(user.getUsername())
            .role(user.getRole())
            .build();
}
```

Phần tạo JWT:

```java
private String generateToken(User user) {
    Instant now = Instant.now();
    JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("manga-vault")
            .subject(user.getUsername())
            .issuedAt(now)
            .expiresAt(now.plusSeconds(expirationSeconds))
            .claim("uid", user.getId())
            .claim("role", user.getRole().name())
            .build();

    JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
    return jwtEncoder
            .encode(JwtEncoderParameters.from(header, claims))
            .getTokenValue();
}
```

Giải thích:

- Cho phép đăng nhập bằng username hoặc email.
- `passwordEncoder.matches` so sánh mật khẩu với BCrypt hash; không giải mã password.
- Tài khoản bị khóa không được cấp token.
- JWT có thời gian phát hành, hết hạn, user ID và role.
- Token được ký bằng HS256 để backend phát hiện token bị sửa.

Câu nói ngắn:

> BCrypt bảo vệ mật khẩu lưu trong database, còn JWT đại diện cho phiên xác thực ở các request sau. Hai công nghệ giải quyết hai vấn đề khác nhau.

## 6. Phân quyền endpoint bằng Spring Security

File: `SecurityConfig.java`

```java
http
    .csrf(csrf -> csrf.disable())
    .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/health", "/api/auth/**").permitAll()
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .requestMatchers("/api/me", "/api/me/**").authenticated()
            .requestMatchers("/api/stories/*/follow").authenticated()
            .requestMatchers(HttpMethod.POST,
                    "/api/stories/*/comments").authenticated()
            .requestMatchers(HttpMethod.PUT,
                    "/api/comments/**").authenticated()
            .requestMatchers(HttpMethod.GET,
                    "/api/stories/**", "/api/chapters/**", "/api/genres/**")
            .permitAll()
            .anyRequest().authenticated())
    .oauth2ResourceServer(oauth2 -> oauth2.jwt(...));
```

Giải thích:

- API đăng nhập, health và dữ liệu đọc công khai cho GUEST.
- Theo dõi, lịch sử và tạo/sửa bình luận yêu cầu JWT.
- `/api/admin/**` yêu cầu role ADMIN.
- `STATELESS` nghĩa là backend không lưu HTTP session.

## 7. Xử lý N+1 Query bằng EntityGraph

File: `FollowRepository.java`

```java
@EntityGraph(attributePaths = "story")
Page<Follow> findByUserIdOrderByCreatedAtDesc(
        String userId, Pageable pageable
);
```

File: `ReadingProgressRepository.java`

```java
@EntityGraph(attributePaths = "story")
Page<ReadingProgress> findByUserIdOrderByUpdatedAtDesc(
        String userId, Pageable pageable
);
```

Truy vấn theo nhóm thay vì từng Story:

```java
@Query("""
       select p from ReadingProgress p
       where p.user.id = :userId
         and p.story.id in :storyIds
       """)
List<ReadingProgress> findByUserIdAndStoryIds(
        String userId, Collection<String> storyIds
);
```

Cấu hình batch fetch:

```properties
spring.jpa.properties.hibernate.default_batch_fetch_size=20
```

Giải thích:

- Không có `EntityGraph`: một query lấy N Follow, sau đó có thể thêm N query lấy Story.
- Có `EntityGraph`: Story cần hiển thị được tải cùng truy vấn danh sách.
- Với dữ liệu bổ sung của nhiều Story, dùng `IN (:storyIds)` để lấy theo nhóm.
- Batch fetch là lớp hỗ trợ thêm cho các quan hệ lazy còn lại.
- Không đổi mọi quan hệ sang eager vì sẽ tải dữ liệu không cần thiết.

Câu nói ngắn:

> Em chỉ fetch trước quan hệ mà màn hình chắc chắn cần, còn các quan hệ khác vẫn để lazy. Cách này cân bằng giữa tránh N+1 và tránh tải thừa.

## 8. Xử lý Exception thống nhất

Service phát sinh lỗi nghiệp vụ:

```java
if (followRepository.existsByUserIdAndStoryId(user.getId(), storyId)) {
    throw new AppException(ErrorCode.ALREADY_FOLLOWED);
}
```

`ErrorCode` quy định mã, message và HTTP status:

```java
ALREADY_FOLLOWED(
        4097,
        "Story is already followed",
        HttpStatus.CONFLICT
),
NOT_RESOURCE_OWNER(
        4032,
        "You can only modify your own content",
        HttpStatus.FORBIDDEN
);
```

`GlobalExceptionHandler` chuyển thành response JSON:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Void>> handleAppException(
            AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.<Void>builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }
}
```

Giải thích:

- Service không tự tạo HTTP response.
- Controller không phải viết `try-catch` lặp lại.
- Frontend luôn nhận cấu trúc lỗi có `code` và `message`.
- Validation, JSON sai và parameter sai kiểu được trả 400.
- Lỗi 401/403 trong Security filter được xử lý riêng vì request chưa tới controller.

## 9. Ba đoạn code nên ưu tiên khi vấn đáp

Nếu không có thời gian mở nhiều file, chỉ cần trình bày ba đoạn:

1. `AuthService.login()` để giải thích JWT và BCrypt.
2. `LibraryService.saveProgress()` để giải thích lịch sử/upsert.
3. `FollowRepository @EntityGraph` để giải thích N+1 Query.

Ba đoạn này thể hiện được bảo mật, nghiệp vụ, dữ liệu và hiệu năng của project.
