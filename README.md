# Manga Vault

`Manga Vault` là project backend dùng để học Java Backend với Spring Boot.

Ý tưởng của project là xây một hệ thống nhỏ để lưu các bộ manga cá nhân: tên truyện, mô tả, thể loại, chapter, trạng thái truyện, và sau này có thể thêm đăng nhập, phân quyền, favorite, lịch sử đọc.

Project này không nhằm mục tiêu làm một website manga lớn ngay từ đầu. Mục tiêu chính là học từng phần của Spring Boot một cách chắc chắn, giống như đang xây một dự án thật.

## Cách Học Trong Project Này

Mỗi task sẽ đi theo thứ tự nhỏ:

```text
Hiểu mục tiêu
-> Code một phần nhỏ
-> Chạy thử
-> Sửa lỗi nếu có
-> Rút ra bài học
```

Khi học backend, điều quan trọng không chỉ là viết code chạy được, mà còn phải hiểu:

- Request từ client đi qua những class nào.
- Controller, Service, Repository có trách nhiệm gì.
- DTO và Entity khác nhau ở đâu.
- Khi lỗi xảy ra thì lỗi nằm ở code, cấu hình, database, hay môi trường chạy.
- Làm sao biết một feature đã đúng.

## Task 1: Project Skeleton Và Health Check

### Mục Tiêu

Trước khi xây các tính năng lớn như CRUD manga, database, đăng nhập hay JWT, ta cần kiểm tra một việc rất cơ bản:

Backend có chạy được không?

Vì vậy ta tạo một API rất đơn giản gọi là `health check`.

API này không xử lý nghiệp vụ phức tạp. Nó chỉ trả về một response để xác nhận rằng ứng dụng Spring Boot đã start thành công và tầng web/controller đang hoạt động.

### API Cần Có

```http
GET /api/health
```

Response mong đợi:

```json
{
  "code": 1000,
  "result": "Manga Vault API is running"
}
```

### Luồng Chạy

Khi mở trình duyệt hoặc dùng Postman gọi:

```text
http://localhost:8080/api/health
```

Request sẽ đi theo luồng:

```text
Client / Browser
-> Spring Boot nhận request
-> Tìm controller có đường dẫn /api/health
-> Gọi method health()
-> Method trả về dữ liệu Java
-> Spring chuyển dữ liệu Java thành JSON
-> Client nhận response
```

Ở task này, luồng chỉ đi tới `Controller`.

Chưa cần:

- Service
- Repository
- Database
- Entity
- DTO
- Security

Lý do là vì `health check` chỉ dùng để kiểm tra ứng dụng có sống hay không.

### Controller Là Gì?

Trong Spring Boot, `Controller` là nơi nhận HTTP request từ bên ngoài.

Ví dụ:

```text
GET /api/health
```

Spring sẽ tìm trong project xem method nào được gắn với đường dẫn này. Nếu tìm thấy, Spring gọi method đó và lấy kết quả trả về cho client.

Annotation thường dùng:

```java
@RestController
```

Ý nghĩa:

- Class này là một REST controller.
- Dữ liệu trả về từ method sẽ được convert thành JSON.

Annotation:

```java
@GetMapping("/api/health")
```

Ý nghĩa:

- Method này xử lý request HTTP `GET`.
- Đường dẫn của API là `/api/health`.

### Vì Sao Response Dùng JSON?

Backend thường giao tiếp với frontend/mobile app bằng JSON.

Ví dụ response:

```json
{
  "code": 1000,
  "result": "Manga Vault API is running"
}
```

Trong đó:

- `code`: mã quy ước của hệ thống. Ở đây `1000` nghĩa là thành công.
- `result`: dữ liệu thật mà API muốn trả về.

Ở Task 1, ta có thể dùng `Map<String, Object>` để trả JSON nhanh.

Sang Task 2, ta sẽ tạo class `ApiResponse<T>` để response có cấu trúc rõ ràng và tái sử dụng được.

### Checklist Hoàn Thành Task 1

Task 1 được xem là hoàn thành khi:

- App Spring Boot chạy được.
- Gọi được `GET /api/health`.
- Response trả về HTTP 200.
- Response có field `code`.
- Response có field `result`.
- Nội dung `result` là `Manga Vault API is running`.

### Bài Học Cần Nhớ Sau Task 1

Sau task này, cần hiểu được:

- Spring Boot app có thể chạy một web server nhúng là Tomcat.
- Mặc định app chạy ở port `8080`.
- `@RestController` dùng để tạo REST API.
- `@GetMapping` dùng để map request GET vào một method Java.
- Spring Boot tự convert dữ liệu Java thành JSON.
- Health check là endpoint nhỏ nhưng rất quan trọng trong dự án thật.

## Task Tiếp Theo

## Task 2: Tạo ApiResponse Chung Cho API

### Mục Tiêu

Ở Task 1, API `/api/health` có thể trả response bằng `Map`.

Cách đó chạy được, nhưng nếu dự án có nhiều API, mỗi controller có thể tự trả response theo một kiểu khác nhau. Điều này làm cho backend thiếu thống nhất và frontend khó xử lý.

Vì vậy ở Task 2, ta tạo một class response chung:

```java
ApiResponse<T>
```

Mục tiêu của class này là giúp các API trong hệ thống trả về cùng một cấu trúc JSON.

### Response Format

Format response hiện tại:

```json
{
  "code": 1000,
  "result": "Manga Vault API is running"
}
```

Ý nghĩa:

- `code`: mã kết quả do hệ thống tự quy ước.
- `result`: dữ liệu thật mà API trả về.

### Generic `<T>` Là Gì?

Trong `ApiResponse<T>`, chữ `T` đại diện cho kiểu dữ liệu linh hoạt.

Ví dụ:

```java
ApiResponse<String>
```

Nghĩa là `result` có kiểu `String`.

Sau này có thể có:

```java
ApiResponse<MangaResponse>
```

Nghĩa là `result` có kiểu `MangaResponse`.

Hoặc:

```java
ApiResponse<List<MangaResponse>>
```

Nghĩa là `result` là danh sách manga.

Nhờ generic, ta chỉ cần một class `ApiResponse`, nhưng dùng được cho nhiều kiểu dữ liệu khác nhau.

### Vì Sao Không Nên Dùng `Map` Lâu Dài?

`Map` tiện cho demo nhanh, nhưng không rõ nghĩa trong dự án thật.

Ví dụ:

```java
Map<String, Object>
```

không nói rõ API sẽ có field nào, field đó có kiểu gì, và cấu trúc response có ổn định hay không.

Trong khi đó:

```java
ApiResponse<String>
```

cho biết rõ:

- Đây là response chuẩn của hệ thống.
- `code` là mã kết quả.
- `result` trong API này là `String`.

### Luồng Chạy Sau Task 2

```text
Client / Browser
-> HealthController
-> Tạo ApiResponse<String>
-> Spring Boot convert object Java thành JSON
-> Client nhận response
```

Controller không cần tự ráp JSON thủ công. Controller chỉ cần trả về object Java, còn Spring Boot sẽ xử lý phần chuyển object sang JSON.

### Bài Học Cần Nhớ Sau Task 2

Sau task này, cần hiểu được:

- DTO response giúp chuẩn hóa dữ liệu API trả về.
- `ApiResponse<T>` giúp dùng chung một format response cho nhiều API.
- Generic `<T>` giúp `result` linh hoạt nhưng vẫn có kiểm tra kiểu dữ liệu.
- Controller nên trả object rõ nghĩa thay vì dùng `Map` cho response lâu dài.
- Builder pattern giúp tạo object dễ đọc hơn khi object có nhiều field.

## Task Tiếp Theo

## Task 3.1: Chuẩn Bị Database Layer Với JPA Và MySQL

### Mục Tiêu

Từ Task 1 và Task 2, project mới chỉ có tầng web:

```text
Client
-> Controller
```

Để bắt đầu xây CRUD cho manga, project cần thêm tầng database.

Từ giai đoạn này, luồng backend sẽ dần chuyển thành:

```text
Client
-> Controller
-> Service
-> Repository
-> Database
```

Task 3.1 tập trung vào việc chuẩn bị nền tảng để Spring Boot có thể kết nối MySQL thông qua JPA.

### Các Thành Phần Được Thêm

#### Spring Data JPA

Spring Data JPA giúp thao tác với database thông qua repository thay vì phải tự viết nhiều câu SQL thủ công.

Ví dụ sau này ta có thể tạo:

```java
MangaRepository
```

và kế thừa từ:

```java
JpaRepository<Manga, String>
```

Khi đó Spring Data JPA sẽ hỗ trợ sẵn các thao tác như lưu, tìm theo id, tìm tất cả, xóa.

#### MySQL Driver

MySQL Driver là thư viện giúp Java application kết nối được tới MySQL.

Spring Boot biết cách làm việc với database, nhưng vẫn cần driver cụ thể để nói chuyện với loại database đang dùng.

Trong project này, database là MySQL, nên cần dependency:

```text
mysql-connector-j
```

#### Validation

Validation dùng để kiểm tra dữ liệu đầu vào từ client.

Ví dụ sau này khi tạo manga:

- `title` không được rỗng.
- `slug` không được rỗng.
- `status` phải hợp lệ.

Task này mới thêm dependency, chưa dùng validation ngay.

### Cấu Hình Database

Spring Boot đọc thông tin kết nối database từ file:

```text
application.properties
```

Các cấu hình quan trọng:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/manga_vault
spring.datasource.username=root
spring.datasource.password=your_password
```

Ý nghĩa:

- `spring.datasource.url`: địa chỉ database.
- `spring.datasource.username`: username để đăng nhập MySQL.
- `spring.datasource.password`: password để đăng nhập MySQL.

### Hibernate `ddl-auto`

Cấu hình:

```properties
spring.jpa.hibernate.ddl-auto=update
```

Ý nghĩa:

Hibernate sẽ dựa vào các entity trong code để cập nhật cấu trúc bảng trong database.

Trong giai đoạn học, `update` giúp nhìn thấy nhanh entity biến thành table như thế nào.

Trong dự án thật hoặc production, thường không để Hibernate tự sửa schema như vậy. Thay vào đó sẽ dùng công cụ migration như Flyway hoặc Liquibase.

### Hiển Thị SQL

Cấu hình:

```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

Ý nghĩa:

Khi JPA chạy query, SQL sẽ được in ra terminal.

Điều này rất hữu ích khi học, vì ta không chỉ thấy Java code chạy, mà còn thấy Hibernate tạo câu SQL như thế nào.

### Bài Học Cần Nhớ Sau Task 3.1

Sau task này, cần hiểu được:

- Thêm JPA nghĩa là project bắt đầu có tầng database.
- Spring Boot sẽ tự cấu hình nhiều thứ nếu thấy dependency phù hợp trong `pom.xml`.
- MySQL Driver là cầu nối giữa Java application và MySQL database.
- `application.properties` là nơi cấu hình kết nối database.
- `ddl-auto=update` tiện cho học tập, nhưng cần cẩn thận trong dự án thật.
- `show-sql=true` giúp quan sát SQL do Hibernate sinh ra.

## Task Tiếp Theo

## Task 3.2: Tạo Enum Và Entity Manga

### Mục Tiêu

Sau khi project kết nối được với MySQL, bước tiếp theo là mô hình hóa domain chính của hệ thống:

```text
Manga
```

Trong backend, một entity là class Java đại diện cho một bảng trong database.

Với task này, entity `Manga` đại diện cho bảng:

```text
mangas
```

### Entity Là Gì?

Entity là object nghiệp vụ có thể được lưu xuống database.

Ví dụ trong code Java có class:

```java
Manga
```

Khi dùng JPA, class này có thể được ánh xạ thành table trong MySQL:

```text
mangas
```

Mỗi object `Manga` trong Java tương ứng với một row trong bảng `mangas`.

### Các Field Của Manga

Entity `Manga` hiện có các field chính:

- `id`: khóa chính của manga.
- `title`: tên truyện.
- `slug`: chuỗi dùng cho URL hoặc tìm kiếm thân thiện hơn.
- `description`: mô tả truyện.
- `coverUrl`: link ảnh bìa.
- `status`: trạng thái truyện.
- `visibility`: truyện đang riêng tư hay công khai.
- `createdAt`: thời điểm tạo.
- `updatedAt`: thời điểm cập nhật gần nhất.

### Enum Là Gì?

Enum dùng để biểu diễn một tập giá trị cố định.

Ví dụ trạng thái manga chỉ nên nằm trong một số giá trị nhất định:

```java
ONGOING
COMPLETED
HIATUS
```

Nếu dùng `String` tự do, ta có thể nhập sai như:

```text
On going
complete
done
```

Enum giúp code rõ ràng hơn và giảm lỗi nhập sai.

### MangaStatus

`MangaStatus` biểu diễn trạng thái của truyện:

- `ONGOING`: truyện đang ra.
- `COMPLETED`: truyện đã hoàn thành.
- `HIATUS`: truyện đang tạm ngưng.

### Visibility

`Visibility` biểu diễn phạm vi hiển thị:

- `PRIVATE`: chỉ chủ app/admin thấy.
- `PUBLIC`: có thể công khai cho người khác xem.

### Lưu Enum Trong Database

Với enum, nên dùng:

```java
@Enumerated(EnumType.STRING)
```

Ý nghĩa:

JPA sẽ lưu enum bằng tên của enum, ví dụ:

```text
ONGOING
COMPLETED
PRIVATE
PUBLIC
```

Cách này dễ đọc và an toàn hơn so với lưu enum bằng số thứ tự.

### Tự Động Lưu Thời Gian

Entity `Manga` dùng:

```java
@CreationTimestamp
```

để tự động set thời điểm tạo record.

Và:

```java
@UpdateTimestamp
```

để tự động cập nhật thời điểm record được sửa.

Nhờ vậy, ta không cần tự set `createdAt` và `updatedAt` thủ công trong service.

### Java Naming Và Database Naming

Trong Java, field thường viết theo camelCase:

```java
coverUrl
createdAt
updatedAt
```

Khi xuống database, Spring Boot/Hibernate thường map thành snake_case:

```text
cover_url
created_at
updated_at
```

Đây là convention phổ biến trong nhiều dự án backend.

### Bài Học Cần Nhớ Sau Task 3.2

Sau task này, cần hiểu được:

- Entity là class Java được ánh xạ với table trong database.
- `@Entity` đánh dấu class là entity của JPA.
- `@Table(name = "...")` dùng để đặt tên bảng.
- `@Id` đánh dấu khóa chính.
- `@GeneratedValue` cho phép tự sinh id.
- Enum giúp giới hạn giá trị hợp lệ trong domain.
- `@Enumerated(EnumType.STRING)` giúp lưu enum dễ đọc và an toàn hơn.
- `@CreationTimestamp` và `@UpdateTimestamp` giúp tự động quản lý thời gian tạo/sửa.

## Task Tiếp Theo

## Task 3.3: Tạo MangaRepository

### Mục Tiêu

Sau khi đã có entity `Manga`, project cần một cách để thao tác với bảng `mangas` trong database.

Repository là tầng chịu trách nhiệm làm việc với dữ liệu.

Trong kiến trúc phân lớp:

```text
Controller
-> Service
-> Repository
-> Database
```

Repository nằm gần database nhất.

### Repository Là Gì?

Repository là nơi chứa các thao tác đọc/ghi dữ liệu.

Ví dụ:

- Lưu một manga mới.
- Tìm manga theo id.
- Lấy danh sách manga.
- Xóa manga.
- Kiểm tra manga có tồn tại không.

Service sẽ gọi repository khi cần dữ liệu, nhưng service không nên tự viết logic truy cập database.

### Spring Data JPA Repository

Trong Spring Data JPA, repository thường là một interface kế thừa từ:

```java
JpaRepository<Entity, IdType>
```

Với `MangaRepository`:

```java
JpaRepository<Manga, String>
```

Ý nghĩa:

- `Manga`: entity mà repository quản lý.
- `String`: kiểu dữ liệu của khóa chính `id`.

Vì entity `Manga` có:

```java
private String id;
```

nên `MangaRepository` dùng `String` làm kiểu id.

### Vì Sao Interface Rỗng Vẫn Dùng Được?

`MangaRepository` không cần tự viết method cơ bản vì `JpaRepository` đã cung cấp sẵn nhiều method.

Ví dụ:

```java
save()
findById()
findAll()
deleteById()
existsById()
count()
```

Spring Data JPA sẽ tự tạo implementation cho repository khi application chạy.

Ta chỉ khai báo interface, còn Spring lo phần code phía sau.

### Repository Biết Query Bảng Nào Bằng Cách Nào?

Repository biết thông qua entity:

```java
JpaRepository<Manga, String>
```

`Manga` là entity có:

```java
@Entity
@Table(name = "mangas")
```

Vì vậy Spring hiểu:

```text
MangaRepository
-> quản lý entity Manga
-> entity Manga ánh xạ với bảng mangas
-> query xuống bảng mangas
```

### Bài Học Cần Nhớ Sau Task 3.3

Sau task này, cần hiểu được:

- Repository là tầng giao tiếp với database.
- Repository không xử lý nghiệp vụ chính.
- Service sẽ gọi repository để lấy hoặc lưu dữ liệu.
- `JpaRepository` cung cấp sẵn nhiều method CRUD cơ bản.
- Spring Data JPA tự tạo implementation cho repository interface.
- Khi app start, Spring sẽ scan và đăng ký repository thành bean.

## Task Tiếp Theo

## Task 3.4: Tạo DTO Request Và Response Cho Manga

### Mục Tiêu

Sau khi có entity `Manga`, ta cần chuẩn bị các class dùng cho API.

Entity là model nội bộ gắn với database. API không nên nhận trực tiếp entity từ client và cũng không nên trả trực tiếp entity ra ngoài.

Vì vậy ta tạo DTO:

```text
DTO = Data Transfer Object
```

DTO là object dùng để truyền dữ liệu giữa client và server.

### Vì Sao Không Dùng Entity Trực Tiếp?

Entity đại diện cho bảng database.

Nếu dùng entity trực tiếp trong controller, API sẽ bị phụ thuộc quá chặt vào database schema.

Một số vấn đề có thể xảy ra:

- Client có thể gửi các field không nên được phép gửi, như `id`, `createdAt`, `updatedAt`.
- Khi database thay đổi, API dễ bị ảnh hưởng theo.
- Khó kiểm soát dữ liệu đầu vào.
- Khó tách dữ liệu request và response.

DTO giúp API rõ ràng và an toàn hơn.

### MangaCreationRequest

`MangaCreationRequest` đại diện cho dữ liệu client gửi lên khi muốn tạo manga mới.

Các field gồm:

- `title`
- `slug`
- `description`
- `coverUrl`
- `status`
- `visibility`

Không có:

- `id`
- `createdAt`
- `updatedAt`

Lý do là các field này do backend hoặc database tự tạo, client không nên tự quyết định.

### MangaResponse

`MangaResponse` đại diện cho dữ liệu API trả về cho client sau khi xử lý.

Các field gồm:

- `id`
- `title`
- `slug`
- `description`
- `coverUrl`
- `status`
- `visibility`
- `createdAt`
- `updatedAt`

Response có thể chứa nhiều thông tin hơn request, vì sau khi lưu database, backend đã có thêm id và thời gian tạo/cập nhật.

### Request Và Response Khác Nhau Như Thế Nào?

Khi tạo manga, client gửi request:

```json
{
  "title": "One Piece",
  "slug": "one-piece",
  "description": "Pirate adventure manga",
  "coverUrl": "https://example.com/one-piece.jpg",
  "status": "ONGOING",
  "visibility": "PRIVATE"
}
```

Sau khi backend xử lý và lưu database, API có thể trả response:

```json
{
  "id": "...",
  "title": "One Piece",
  "slug": "one-piece",
  "description": "Pirate adventure manga",
  "coverUrl": "https://example.com/one-piece.jpg",
  "status": "ONGOING",
  "visibility": "PRIVATE",
  "createdAt": "...",
  "updatedAt": "..."
}
```

Request là dữ liệu client gửi vào.

Response là dữ liệu backend trả ra.

Hai loại này không nhất thiết phải giống nhau.

### Bài Học Cần Nhớ Sau Task 3.4

Sau task này, cần hiểu được:

- DTO dùng để truyền dữ liệu qua API.
- Entity dùng để ánh xạ database, không nên lộ trực tiếp ra API.
- Request DTO mô tả dữ liệu client được phép gửi lên.
- Response DTO mô tả dữ liệu backend trả về.
- Tách DTO khỏi entity giúp code dễ kiểm soát, dễ validate và dễ bảo trì hơn.

## Task Tiếp Theo

## Task 3.5: Tạo MangaService Để Xử Lý Create Manga

### Mục Tiêu

Sau khi có entity, repository và DTO, ta cần một nơi để xử lý nghiệp vụ tạo manga.

Nơi đó là service.

Trong kiến trúc phân lớp:

```text
Controller
-> Service
-> Repository
-> Database
```

Service đứng giữa controller và repository.

Controller nhận request từ client, còn repository làm việc với database. Service điều phối quá trình xử lý nghiệp vụ.

### Luồng Create Manga Trong Service

Flow tạo manga gồm:

```text
MangaCreationRequest
-> MangaService.createManga()
-> Manga entity
-> MangaRepository.save()
-> Database
-> Manga đã được lưu
-> MangaResponse
```

### Bước 1: Chuyển Request DTO Thành Entity

Client gửi dữ liệu tạo manga vào `MangaCreationRequest`.

Service chuyển dữ liệu đó thành entity `Manga` để có thể lưu xuống database.

Các field được map:

- `title`
- `slug`
- `description`
- `coverUrl`
- `status`
- `visibility`

Không tự set:

- `id`
- `createdAt`
- `updatedAt`

Lý do là các field này do Hibernate hoặc database tự xử lý.

### Bước 2: Lưu Entity Bằng Repository

Service gọi:

```java
mangaRepository.save(manga)
```

Đây là method có sẵn từ `JpaRepository`.

Khi gọi `save`, JPA/Hibernate sẽ lưu entity vào bảng `mangas`.

Sau khi lưu, entity trả về sẽ có thêm các giá trị như:

- `id`
- `createdAt`
- `updatedAt`

### Bước 3: Chuyển Entity Thành Response DTO

Sau khi lưu xong, service không trả thẳng entity ra ngoài.

Service chuyển entity thành `MangaResponse`.

Response có thể chứa các field mà request ban đầu chưa có, ví dụ:

- `id`
- `createdAt`
- `updatedAt`

### Dependency Injection Trong Service

`MangaService` cần dùng `MangaRepository`.

Với Spring Boot, ta không tự tạo repository bằng `new`.

Thay vào đó, Spring inject repository vào service.

Một cách phổ biến là dùng:

```java
@RequiredArgsConstructor
```

với field:

```java
private final MangaRepository mangaRepository;
```

Lombok sẽ tạo constructor, và Spring dùng constructor đó để inject dependency.

### Bài Học Cần Nhớ Sau Task 3.5

Sau task này, cần hiểu được:

- Service là nơi xử lý và điều phối nghiệp vụ.
- Controller không nên tự lưu database.
- Repository không nên chứa logic nghiệp vụ.
- Service có thể chuyển request DTO thành entity.
- Service gọi repository để lưu dữ liệu.
- Service chuyển entity đã lưu thành response DTO.
- `@RequiredArgsConstructor` giúp viết constructor injection gọn hơn.

## Task 3.6: Tạo API POST /api/mangas

### Mục Tiêu

Sau khi service đã có method tạo manga, ta cần mở một HTTP API để client có thể gọi vào flow đó.

API cần có:

```http
POST /api/mangas
```

Client gửi JSON trong request body. Backend nhận dữ liệu, gọi service để lưu manga, rồi trả về response chuẩn.

### Luồng Đầy Đủ

Sau task này, luồng create manga trở thành:

```text
Client
-> MangaController
-> MangaService
-> MangaRepository
-> Database
-> MangaResponse
-> ApiResponse<MangaResponse>
```

Đây là flow CRUD đầu tiên đi qua đủ các tầng chính của backend.

### Vai Trò Của Controller

Controller là nơi nhận HTTP request.

Trong task này, `MangaController` nhận request:

```http
POST /api/mangas
```

Sau đó controller gọi:

```java
mangaService.createManga(request)
```

Controller không tự tạo entity và không tự gọi repository.

### `@RequestMapping` Và `@PostMapping`

Class controller có thể có:

```java
@RequestMapping("/api/mangas")
```

Điều này nghĩa là các endpoint trong controller đều bắt đầu bằng:

```text
/api/mangas
```

Method tạo manga dùng:

```java
@PostMapping
```

Kết hợp lại thành endpoint:

```http
POST /api/mangas
```

Trong REST API, hành động tạo mới thường được biểu diễn bằng HTTP method `POST`, nên URL không cần viết thêm `/createManga`.

### `@RequestBody`

Annotation:

```java
@RequestBody
```

nói với Spring rằng dữ liệu JSON trong body của request cần được convert thành object Java.

Ví dụ JSON:

```json
{
  "title": "One Piece",
  "slug": "one-piece",
  "description": "Pirate adventure manga",
  "coverUrl": "https://example.com/one-piece.jpg",
  "status": "ONGOING",
  "visibility": "PRIVATE"
}
```

sẽ được convert thành:

```java
MangaCreationRequest
```

### Response Chuẩn

Controller trả về:

```java
ApiResponse<MangaResponse>
```

Ý nghĩa:

- API vẫn giữ format chung của hệ thống.
- `result` chứa dữ liệu chi tiết của manga vừa tạo.

### Bài Học Cần Nhớ Sau Task 3.6

Sau task này, cần hiểu được:

- Controller là tầng nhận HTTP request.
- `@RequestBody` giúp map JSON body thành Java object.
- Controller nên gọi service thay vì xử lý nghiệp vụ trực tiếp.
- `POST /api/mangas` là REST style tốt hơn `/api/mangas/createManga`.
- `ApiResponse<MangaResponse>` giúp response giữ format thống nhất.
- Đây là flow đầu tiên đi đủ từ client đến database.

## Task Tiếp Theo

Task 3.7 sẽ là:

```text
Tạo API GET /api/mangas để lấy danh sách manga
```

Mục tiêu là học luồng đọc dữ liệu từ database và trả danh sách response cho client.
