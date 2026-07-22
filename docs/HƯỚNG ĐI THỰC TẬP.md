## HỌC VIỆN CÔNG NGHỆ BƯU CHÍNH VIỄN THÔNG KHOA CÔNG NGHỆ THÔNG TIN 1

## Xây dựng website đọc truyện trực tuyến sử dụng React và Java THỰC TẬP TỐT NGHIỆP BÁO CÁO THỰC TẬP Springboot ĐỀ TÀI:

## Giảng viên hướng dẫn: TS. Vũ Trọng Sinh Sinh viên thực hiện: Lâm Thành Đức Mã sinh viên: B22DCCN227

## Hà Nội, 2026


## CHƯƠNG 1. TỔNG QUAN VÀ KHẢO SÁT HỆ THỐNG

## 1.1. Giới thiệu tổng quan đề tài

## 1.1.1. Tên đề tài

## Xây dựng website đọc truyện trực tuyến sử dụng React và Java Spring Boot.

## 1.1.2. Bối cảnh thực hiện đề tài

Sự phát triển của Internet và các thiết bị cá nhân như máy tính, điện thoại thông minh và máy tính bảng đã làm thay đổi thói quen tiếp cận nội dung của người dùng. Thay vì sử dụng hoàn toàn các loại sách và truyện in truyền thống, người đọc có xu hướng tìm kiếm, theo dõi và đọc nội dung trực tuyến vì tính thuận tiện, khả năng truy cập nhanh

và không bị giới hạn về thời gian, địa điểm.

Website đọc truyện trực tuyến là hệ thống cung cấp nội dung truyện dưới dạng số. Người dùng có thể tìm kiếm truyện, xem thông tin giới thiệu, lựa chọn chương và đọc nội dung trực tiếp trên trình duyệt. Ngoài chức năng đọc truyện cơ bản, các hệ thống hiện nay còn hỗ trợ nhiều tính năng như lưu truyện yêu thích, theo dõi lịch sử đọc,

bình luận, đánh giá, nhận thông báo khi có chương mới và đề xuất nội dung phù hợp.

Từ nhu cầu thực tế trên, đề tài hướng đến việc xây dựng một website đọc truyện trực tuyến có giao diện thân thiện, hoạt động tốt trên nhiều kích thước màn hình và cung

cấp đầy đủ các chức năng cơ bản cho người đọc cũng như người quản trị.

Hệ thống được xây dựng theo kiến trúc tách biệt giữa frontend và backend. Phần giao diện sử dụng React, phần xử lý nghiệp vụ và cung cấp API sử dụng Java Spring Boot. Dữ liệu về người dùng, truyện, chương truyện, thể loại, bình luận và lịch sử đọc được lưu trữ trong hệ quản trị cơ sở dữ liệu quan hệ.

## 1.1.3. Lý do lựa chọn đề tài

Đề tài được lựa chọn dựa trên các lý do sau:

- Nhu cầu đọc truyện trực tuyến ngày càng phổ biến.

- Hệ thống có nhiều nghiệp vụ phù hợp để áp dụng kiến thức phát triển website.

- Đề tài cho phép thực hành xây dựng giao diện người dùng bằng React.

- Phần backend có thể áp dụng Spring Boot để xây dựng REST API và xử lý nghiệp vụ.


- Có thể áp dụng các kiến thức về cơ sở dữ liệu, xác thực, phân quyền và bảo mật.

- Đề tài có khả năng mở rộng thêm nhiều chức năng trong tương lai.

- Sản phẩm có tính thực tiễn và có thể triển khai thành một website hoàn chỉnh.

## 1.2. Mục tiêu của đề tài

## 1.2.1. Mục tiêu tổng quát

Mục tiêu tổng quát của đề tài là xây dựng một website đọc truyện trực tuyến có khả năng quản lý nội dung truyện và phục vụ nhu cầu tìm kiếm, theo dõi, đọc truyện của người dùng.

Hệ thống cần có giao diện trực quan, dễ sử dụng, hỗ trợ hiển thị trên máy tính và thiết bị di động. Các thành phần frontend, backend và cơ sở dữ liệu phải được tổ chức rõ ràng, bảo đảm khả năng bảo trì và mở rộng.

## 1.2.2. Mục tiêu cụ thể

Sau khi hoàn thành, đề tài dự kiến đạt được các kết quả sau:

- Một website đọc truyện có thể hoạt động trên trình duyệt.

- Giao diện frontend được xây dựng bằng React.

- Backend được xây dựng bằng Java Spring Boot.

- Hệ thống cung cấp API để trao đổi dữ liệu giữa frontend và backend.

- Người dùng có thể đăng ký, đăng nhập, tìm kiếm và đọc truyện.

- Người quản trị có thể thêm, sửa, xóa và cập nhật nội dung truyện.

- Dữ liệu được lưu trữ trong hệ quản trị cơ sở dữ liệu.

- Mã nguồn được quản lý trên GitHub với cấu trúc thư mục rõ ràng.

- Hệ thống có thể tiếp tục mở rộng

## 1.3. Đối tượng và phạm vi nghiên cứu

## 1.3.1. Đối tượng nghiên cứu

Đối tượng nghiên cứu của đề tài bao gồm:

- Quy trình quản lý truyện và chương truyện.

- Quy trình tìm kiếm và đọc truyện của người dùng.

- Phương pháp xây dựng giao diện website bằng React.

- Phương pháp xây dựng REST API bằng Java Spring Boot.


- Cơ chế xác thực và phân quyền người dùng.

- Phương pháp tổ chức và lưu trữ dữ liệu truyện.

- Phương thức trao đổi dữ liệu giữa frontend và backend.

- Các yêu cầu về bảo mật, hiệu năng và trải nghiệm người dùng.

## 1.3.2. Đối tượng sử dụng hệ thống

Hệ thống xác định ba nhóm người sử dụng chính.

## a. Khách chưa đăng nhập

Khách chưa đăng nhập có thể:

- Truy cập trang chủ.

- Xem danh sách truyện.

- Tìm kiếm và lọc truyện.

- Xem thông tin chi tiết của truyện.

- Xem danh sách chương.

- Đọc nội dung truyện.

- Đăng ký hoặc đăng nhập tài khoản.

## b. Người dùng đã đăng nhập

Ngoài các chức năng của khách, người dùng đã đăng nhập có thể:

- Cập nhật thông tin cá nhân.

- Theo dõi hoặc lưu truyện yêu thích.

- Xem lịch sử đọc.

- Tiếp tục đọc từ chương gần nhất.

- Bình luận về truyện hoặc chương truyện.

- Đánh giá truyện.

- Quản lý danh sách truyện đang theo dõi.

## c. Người quản trị

Người quản trị có thể:

- Quản lý tài khoản người dùng.

- Quản lý thể loại truyện.

- Thêm, sửa, xóa và cập nhật truyện.

- Thêm, sửa, xóa và sắp xếp chương truyện.

- Quản lý bình luận.

- Ẩn các nội dung không phù hợp.


- Theo dõi số lượng truyện, chương và người dùng.

- Theo dõi một số số liệu hoạt động cơ bản của hệ thống.

## 1.3.3. Phạm vi nội dung

Trong giai đoạn phát triển, đề tài tập trung vào website đọc truyện chữ trực tuyến. Mỗi truyện bao gồm thông tin mô tả và danh sách các chương. Mỗi chương chứa tiêu đề và nội dung văn bản.

Các loại dữ liệu chính được quản lý gồm:

- Tài khoản người dùng.

- Vai trò và quyền hạn.

- Thông tin tác giả.

- Thông tin truyện.

- Ảnh bìa truyện.

- Thể loại truyện.

- Chương truyện.

- Nội dung chương.

- Truyện yêu thích.

- Lịch sử đọc.

- Bình luận.

- Đánh giá.

Trong phiên bản đầu tiên, hệ thống chưa tập trung vào các nội dung có dung lượng lớn như truyện tranh nhiều hình ảnh, sách nói hoặc video. Tuy nhiên, cấu trúc hệ thống có thể được mở rộng để hỗ trợ các loại nội dung này trong tương lai.

## 1.3.4. Phạm vi kỹ thuật

Hệ thống dự kiến sử dụng các công nghệ sau:

- Frontend: React.

Backend: Java Spring Boot.

API: RESTful API.

Bảo mật: Spring Security và JSON Web Token.

- Cơ sở dữ liệu: MySQL.

- Quản lý mã nguồn: Git và GitHub.

- Kiểm thử API: Postman.


React hỗ trợ xây dựng giao diện từ các component độc lập và có khả năng tái sử dụng. Cách tổ chức này phù hợp với các thành phần như thẻ truyện, danh sách truyện, thanh tìm kiếm, trang chi tiết và trình đọc chương.

Spring Boot hỗ trợ xây dựng ứng dụng Spring độc lập, cung cấp cơ chế tự động cấu hình và các starter dependency, giúp giảm khối lượng cấu hình ban đầu khi xây dựng backend.

Spring Security cung cấp các chức năng xác thực, phân quyền và bảo vệ ứng dụng trước một số nhóm tấn công phổ biến. Framework này có thể được sử dụng để phân biệt quyền truy cập giữa khách, người dùng và quản trị viên.

## 1.4. Khảo sát các sản phẩm và giải pháp tương tự

## 1.4.1. Mục đích khảo sát

Việc khảo sát các sản phẩm tương tự nhằm:

- Tìm hiểu cách tổ chức nội dung của các website đọc truyện.

- Xác định các tính năng thường được người dùng sử dụng.

- Tham khảo cách bố trí giao diện và điều hướng.

- Đánh giá ưu điểm và hạn chế của từng sản phẩm.

- Xác định các chức năng phù hợp với phạm vi đề tài.

- Hạn chế xây dựng những chức năng không cần thiết trong giai đoạn đầu.

- Đề xuất hướng phát triển riêng cho hệ thống.

Các tiêu chí khảo sát chính gồm:

- Khả năng tìm kiếm và khám phá truyện.

- Cách phân loại truyện.

- Trải nghiệm đọc.

- Quản lý lịch sử đọc.

- Chức năng theo dõi truyện.

- Tương tác giữa người dùng.

- Công cụ quản lý nội dung.

- Khả năng hiển thị trên thiết bị di động.

- Mức độ kiểm soát nội dung.


## 1.4.2. Khảo sát Wattpad

Wattpad là nền tảng đọc và chia sẻ truyện trực tuyến, hướng đến việc kết nối cộng đồng người đọc và người sáng tác. Người dùng có thể đọc truyện, đăng nội dung do mình sáng tác, tương tác và bình luận trong quá trình đọc.

## a. Các chức năng tiêu biểu

- Đăng ký và đăng nhập tài khoản.

- Tìm kiếm truyện.

- Phân loại truyện theo nhiều thể loại.

- Đọc truyện theo từng phần hoặc từng chương.

- Lưu truyện vào thư viện cá nhân.

- Theo dõi tác giả.

- Bình luận trong quá trình đọc.

- Đăng tải truyện do người dùng sáng tác.

- Tương tác giữa người đọc và người viết.

- Đề xuất nội dung dựa trên hoạt động của người dùng.

## b. Ưu điểm

- Có tính cộng đồng cao.

- Cho phép người dùng vừa đọc vừa sáng tác nội dung.

- Hỗ trợ tương tác trực tiếp với tác giả.

- Nội dung được tổ chức theo thể loại tương đối rõ ràng.

- Giao diện đọc phù hợp trên cả website và thiết bị di động.

- Có khả năng giữ chân người dùng thông qua thư viện và danh sách theo dõi.

## c. Hạn chế

- Số lượng nội dung lớn làm cho việc kiểm soát chất lượng gặp khó khăn.

- Chất lượng nội dung không đồng đều do nhiều truyện được đăng bởi cộng đồng.

- Một số thao tác và tính năng có thể gây khó khăn cho người dùng mới.

- Cần có cơ chế kiểm duyệt và báo cáo nội dung chặt chẽ.

## d. Bài học áp dụng

Từ Wattpad, hệ thống của đề tài có thể tham khảo:

- Cách tổ chức truyện theo từng chương.

- Chức năng lưu truyện yêu thích.

- Chức năng bình luận.


- Theo dõi tiến độ đọc.

- Cách thiết kế trang đọc tập trung vào nội dung.

- Cơ chế tương tác giữa người đọc và nội dung.

Trong giai đoạn đầu, đề tài không triển khai đầy đủ mô hình mạng xã hội hoặc cho phép mọi người dùng tự do đăng truyện. Nội dung chính sẽ do quản trị viên quản lý

nhằm giới hạn phạm vi và bảo đảm chất lượng dữ liệu.

## 1.4.3. Khảo sát Royal Road

Royal Road là nền tảng tập trung vào web novel và truyện hư cấu được xuất bản theo từng chương. Nền tảng cung cấp chức năng tìm kiếm nâng cao, danh sách theo dõi,

thông báo chương mới, đánh giá và các công cụ quản lý dành cho tác giả.

Royal Road còn cung cấp bảng điều khiển cho tác giả, cho phép quản lý truyện, chương, bản nháp và theo dõi một số số liệu như tổng số chương, tổng số từ, đánh giá và người theo dõi.

## a. Các chức năng tiêu biểu

- Tìm kiếm truyện theo nhiều điều kiện.

- Lọc theo thể loại, trạng thái và thẻ nội dung.

- Theo dõi truyện.

- Thông báo khi có chương mới.

- Hiển thị chương gần nhất người dùng đã đọc.

- Đánh giá và nhận xét truyện.

- Quản lý truyện và chương dành cho tác giả.

- Hiển thị trạng thái truyện như đang tiến hành, hoàn thành hoặc tạm dừng.

- Hiển thị thống kê cho tác giả.

## b. Ưu điểm

- Tập trung rõ ràng vào truyện chữ.

- Chức năng tìm kiếm và lọc tương đối chi tiết.

- Quản lý tiến độ đọc hiệu quả.

- Thông tin về trạng thái truyện được trình bày rõ ràng.

- Có công cụ hỗ trợ quản lý chương truyện.

- Phù hợp với mô hình phát hành nội dung theo từng chương.

## c. Hạn chế

- Giao diện chứa nhiều thông tin và có thể gây khó khăn cho người mới.

- Một số chức năng nâng cao không cần thiết đối với hệ thống quy mô nhỏ.


- Việc cho phép cộng đồng đăng nội dung yêu cầu cơ chế kiểm duyệt phức tạp.

- Chức năng thống kê chuyên sâu làm tăng phạm vi phát triển.

## d. Bài học áp dụng

Hệ thống có thể tham khảo các điểm sau:

- Phân biệt rõ trạng thái truyện.

- Ghi nhận chương gần nhất đã đọc.

- Hiển thị thời gian cập nhật chương mới.

- Cho phép tìm kiếm theo nhiều điều kiện.

- Thiết kế trang quản trị chương đơn giản và rõ ràng.

- Sắp xếp chương theo thứ tự.

- Cho phép người dùng tiếp tục đọc từ vị trí trước đó.

## 1.4.5. So sánh các sản phẩm được khảo sát

*Bảng 1.1. So sánh một số sản phẩm đọc truyện trực tuyến*

| Tiêu chí | Wattpad Royal Road |   | Hệ thống đề xuất |
| --- | --- | --- | --- |
| Loại nội dung | Truyện | Truyện chữ | Truyện chữ |
| chính | chữ |   |   |
| Đọc theo chương | Có | Có | Có |
| Tìm kiếm truyện | Có | Có | Có |
| Lọc theo thể loại | Có | Có | Có |
| Theo dõi truyện | Có | Có | Có |
| Lịch sử đọc | Có | Có | Có |
| Bình luận | Có | Có | Có |
| Đánh giá truyện | Có | Có | Có |
| Người dùng tự | Có | Có | Chưa triển khai trong |
| đăng nội dung |   |   | phiên bản đầu |
| Trang quản lý nội | Có | Có | Có, dành cho quản trị |
| dung |   |   | viên |
| Hỗ trợ thiết bị di | Có | Có | Có |
| động |   |   |   |


| Đề xuất cá nhân | Có | Có ở một số | Chưa triển khai trong |
| --- | --- | --- | --- |
| hóa |   | hình thức | phiên bản đầu |
| Kiểm duyệt nội | Có | Có | Quản trị viên thực hiện |
| dung |   |   |   |
| Thống kê nâng | Có | Có | Thống kê cơ bản |
| cao |   |   |   |

Qua quá trình khảo sát, có thể nhận thấy các website đọc truyện phổ biến đều tập trung vào ba nhóm chức năng:

- 1. Khám phá nội dung: tìm kiếm, phân loại, xếp hạng và hiển thị truyện mới.

- 2. Trải nghiệm đọc: hiển thị chương, điều hướng chương và ghi nhớ tiến độ.

- 3. Tương tác và quản lý: theo dõi truyện, bình luận, đánh giá và quản trị nội dung.

Hệ thống đề xuất sẽ ưu tiên triển khai các chức năng cốt lõi trên. Các chức năng phức tạp như mạng xã hội cho tác giả, kiếm tiền từ nội dung, đề xuất bằng trí tuệ nhân tạo hoặc đọc truyện ngoại tuyến chưa thuộc phạm vi phiên bản đầu tiên.

## 1.5. Giải pháp đề xuất

## 1.5.1. Mô tả tổng quan giải pháp

Giải pháp được đề xuất là xây dựng một website đọc truyện theo mô hình ứng dụng web client–server.

Frontend chịu trách nhiệm:

- Hiển thị giao diện.

- Tiếp nhận thao tác của người dùng.

- Kiểm tra dữ liệu đầu vào cơ bản.

- Gửi yêu cầu đến backend.

- Nhận và hiển thị dữ liệu trả về.

- Quản lý trạng thái giao diện.

Backend chịu trách nhiệm:

- Tiếp nhận yêu cầu từ frontend.

- Xác thực người dùng.

- Kiểm tra quyền truy cập.

- Xử lý các quy tắc nghiệp vụ.


- Truy vấn và cập nhật cơ sở dữ liệu.

- Trả kết quả dưới định dạng JSON.

- Xử lý lỗi và ghi nhận hoạt động cần thiết.

Cơ sở dữ liệu chịu trách nhiệm lưu trữ lâu dài các dữ liệu về người dùng, truyện, chương, thể loại, bình luận, đánh giá, danh sách theo dõi và lịch sử đọc.

## 1.5.2. Kiến trúc tổng quan

Hệ thống được chia thành ba thành phần chính:

## 1. Frontend React

- Chạy trên trình duyệt của người dùng.

- Gọi REST API để lấy hoặc cập nhật dữ liệu.

- Hiển thị giao diện tương ứng với trạng thái đăng nhập và quyền của tài khoản.

- Cung cấp các REST API.

- Xử lý nghiệp vụ.

- Xác thực và phân quyền.

- Làm việc với cơ sở dữ liệu thông qua tầng repository.

- Lưu trữ dữ liệu có cấu trúc.

- Quản lý quan hệ giữa người dùng, truyện, chương và thể loại.

- Bảo đảm tính nhất quán của dữ liệu.

Luồng xử lý tổng quát:

- 1. Người dùng thực hiện thao tác trên giao diện React.

- 2. Frontend gửi HTTP request đến REST API.

- 3. Spring Boot tiếp nhận và kiểm tra request.

- 4. Backend thực hiện xác thực, phân quyền và xử lý nghiệp vụ.

- 5. Backend truy vấn hoặc cập nhật cơ sở dữ liệu.

- 6. Kết quả được chuyển thành JSON và gửi về frontend.

- 7. React cập nhật giao diện theo dữ liệu nhận được.

## 2. Backend Spring Boot

## 3. Cơ sở dữ liệu


## 1.6. Xác định các tính năng chính

## 1.6.1. Nhóm chức năng dành cho khách

## a. Xem trang chủ

Trang chủ hiển thị tổng quan nội dung của website, bao gồm:

- Truyện mới cập nhật.

- Truyện nổi bật.

- Truyện có lượt xem cao.

- Danh sách thể loại.

- Thanh tìm kiếm.

- Các nội dung được đề xuất chung.

## b. Tìm kiếm truyện

Người dùng có thể tìm kiếm truyện theo:

- Tên truyện.

- Tên tác giả.

- Từ khóa liên quan.

Kết quả tìm kiếm hiển thị các thông tin cơ bản như ảnh bìa, tên truyện, tác giả, thể loại và trạng thái.

## c. Lọc và sắp xếp truyện

Người dùng có thể lọc truyện theo:

- Thể loại.

- Trạng thái.

- Truyện mới cập nhật.

- Truyện đã hoàn thành.

- Truyện đang tiến hành.

Kết quả có thể được sắp xếp theo:

- Thời gian cập nhật.

- Lượt xem.

- Điểm đánh giá.

- Tên truyện.

## d. Xem chi tiết truyện


Trang chi tiết truyện hiển thị:

- Tên truyện.

- Ảnh bìa.

- Tác giả.

- Mô tả.

- Danh sách thể loại.

- Trạng thái.

- Số chương.

- Lượt xem.

- Điểm đánh giá.

- Danh sách chương.

- Bình luận của người dùng.

## e. Đọc chương truyện

Trang đọc chương cung cấp:

- Tên truyện.

- Tên chương.

- Nội dung chương.

- Nút chuyển chương trước.

- Nút chuyển chương sau.

- Nút quay lại danh sách chương.

- Tùy chọn thay đổi kích thước chữ.

- Tùy chọn thay đổi khoảng cách dòng.

- Chế độ nền sáng hoặc nền tối.

## 1.6.2. Nhóm chức năng tài khoản

## a. Đăng ký tài khoản

Người dùng nhập các thông tin:

- Tên đăng nhập.

- Email.

- Mật khẩu.

- Xác nhận mật khẩu.

Hệ thống kiểm tra:

- Tên đăng nhập không được trùng.

- Email không được trùng.


- Email đúng định dạng.

- Mật khẩu đạt độ dài tối thiểu.

- Mật khẩu xác nhận phải trùng khớp.

## b. Đăng nhập

Người dùng đăng nhập bằng tên đăng nhập hoặc email và mật khẩu.

Sau khi đăng nhập thành công, hệ thống cấp token xác thực cho frontend. Token được sử dụng trong các yêu cầu cần xác thực.

## c. Quản lý thông tin cá nhân

Người dùng có thể:

- Xem thông tin tài khoản.

- Cập nhật tên hiển thị.

- Thay đổi ảnh đại diện.

- Đổi mật khẩu.

- Xem ngày tạo tài khoản.

## d. Đăng xuất

Khi đăng xuất, frontend xóa thông tin xác thực đang lưu và chuyển người dùng về trạng thái chưa đăng nhập.

## 1.6.3. Nhóm chức năng hỗ trợ người đọc

## a. Theo dõi truyện

Người dùng có thể thêm truyện vào danh sách theo dõi hoặc yêu thích.

Danh sách theo dõi hiển thị:

- Tên truyện.

- Ảnh bìa.

- Chương mới nhất.

- Chương gần nhất đã đọc.

- Thời gian cập nhật.

## b. Lịch sử đọc

Khi người dùng mở một chương, hệ thống ghi nhận:

- Người dùng.


- Truyện.

- Chương đã đọc.

- Thời điểm đọc gần nhất.

Chức năng này giúp người dùng tiếp tục đọc từ chương gần nhất.

## c. Bình luận

Người dùng đã đăng nhập có thể:

- Gửi bình luận.

- Xem bình luận của người khác.

- Chỉnh sửa bình luận của mình.

- Xóa bình luận của mình.

- Báo cáo bình luận không phù hợp.

Quản trị viên có quyền ẩn hoặc xóa bình luận vi phạm quy định.

## d. Đánh giá truyện

Người dùng có thể đánh giá truyện theo thang điểm do hệ thống quy định. Hệ thống

tính điểm trung bình dựa trên các đánh giá hợp lệ.

Mỗi người dùng chỉ được tạo một đánh giá cho một truyện nhưng có thể cập nhật

đánh giá trước đó.

## 1.6.4. Nhóm chức năng quản trị

## a. Quản lý truyện

Quản trị viên có thể:

- Xem danh sách truyện.

- Thêm truyện mới.

- Cập nhật thông tin truyện.

- Thay đổi ảnh bìa.

- Thay đổi trạng thái truyện.

- Ẩn hoặc xóa truyện.

- Gán thể loại cho truyện.

## b. Quản lý chương

Quản trị viên có thể:


- Xem danh sách chương của từng truyện.

- Thêm chương mới.

- Chỉnh sửa tiêu đề và nội dung chương.

- Thay đổi thứ tự chương.

- Công khai hoặc ẩn chương.

- Xóa chương.

## c. Quản lý thể loại

Quản trị viên có thể:

- Thêm thể loại.

- Cập nhật tên và mô tả thể loại.

- Xóa thể loại chưa được sử dụng.

- Gán nhiều thể loại cho một truyện.

## d. Quản lý người dùng

Quản trị viên có thể:

- Xem danh sách tài khoản.

- Tìm kiếm người dùng.

- Xem trạng thái tài khoản.

- Khóa hoặc mở khóa tài khoản.

- Phân quyền tài khoản.

- Kiểm tra thời điểm tạo tài khoản.

## e. Quản lý bình luận

Quản trị viên có thể:

- Xem danh sách bình luận.

- Tìm kiếm bình luận.

- Xem các bình luận bị báo cáo.

- Ẩn hoặc xóa bình luận vi phạm.

- Khóa tài khoản có hành vi vi phạm nhiều lần.

## f. Thống kê cơ bản

Trang quản trị có thể hiển thị:

- Tổng số người dùng.

- Tổng số truyện.

- Tổng số chương.


- Tổng số bình luận.

- Các truyện có lượt xem cao.

- Các truyện được theo dõi nhiều.

- Số lượng nội dung mới được cập nhật.

## 1.6.5. Danh sách yêu cầu chức năng

*Bảng 1.2. Danh sách yêu cầu chức năng của hệ thống*

| Mã | Tên chức năng | Đối tượng sử | Mức ưu |
| --- | --- | --- | --- |
|   |   | dụng | tiên |
| F01 Xem trang chủ |   | Tất cả người dùng Cao |   |
| F02 Xem danh sách truyện |   | Tất cả người dùng Cao |   |
| F03 Tìm kiếm truyện |   | Tất cả người dùng Cao |   |
| F04 Lọc truyện theo thể loại và trạng |   | Tất cả người dùng Cao |   |
| thái |   |   |   |
| F05 Xem thông tin chi tiết truyện |   | Tất cả người dùng Cao |   |
| F06 Xem danh sách chương |   | Tất cả người dùng Cao |   |
| F07 Đọc nội dung chương |   | Tất cả người dùng Cao |   |
| F08 Đăng ký tài khoản |   | Khách | Cao |
|   | F09 Đăng nhập và đăng xuất | Người dùng | Cao |
| F10 Quản lý hồ sơ cá nhân |   | Người dùng | Trung bình |
| F11 Theo dõi truyện |   | Người dùng | Cao |
| F12 Lưu lịch sử đọc |   | Người dùng | Cao |
| F13 Tiếp tục đọc chương gần nhất Người dùng |   |   | Trung bình |
| F14 Bình luận |   | Người dùng | Trung bình |
| F15 Đánh giá truyện |   | Người dùng | Trung bình |
| F16 Quản lý truyện |   | Quản trị viên | Cao |
| F17 Quản lý chương |   | Quản trị viên | Cao |


| F18 Quản lý thể loại | Quản trị viên | Cao |
| --- | --- | --- |
| F19 Quản lý người dùng | Quản trị viên | Trung bình |
| F20 Quản lý bình luận | Quản trị viên | Trung bình |
| F21 Xem thống kê hệ thống | Quản trị viên | Thấp |
| F22 Thay đổi giao diện đọc | Người đọc | Trung bình |

## 1.6.6. Các tính năng cốt lõi của phiên bản đầu tiên

Để bảo đảm tiến độ, phiên bản đầu tiên ưu tiên các tính năng sau:

- 1. Hiển thị trang chủ.

- 2. Hiển thị danh sách truyện.

- 3. Tìm kiếm truyện.

- 4. Xem chi tiết truyện.

- 5. Xem danh sách chương.

- 6. Đọc nội dung chương.

- 7. Đăng ký tài khoản.

- 8. Đăng nhập và đăng xuất.

- 9. Quản lý truyện cơ bản.

- 10. Quản lý chương cơ bản.

Các tính năng theo dõi truyện, lịch sử đọc, bình luận, đánh giá và thống kê sẽ được bổ sung sau khi các chức năng cốt lõi hoạt động ổn định.

## 1.7. Yêu cầu phi chức năng

Ngoài các chức năng nghiệp vụ, hệ thống cần đáp ứng một số yêu cầu phi chức năng.

## 1.7.1. Yêu cầu về giao diện

- Giao diện dễ hiểu và dễ thao tác.

- Các trang có bố cục thống nhất.

- Màu sắc và kích thước chữ phù hợp với việc đọc trong thời gian dài.

- Hỗ trợ responsive trên máy tính, máy tính bảng và điện thoại.

- Các nút chức năng có tên và biểu tượng rõ ràng.

- Giao diện đọc hạn chế thành phần gây mất tập trung.

## 1.7.2. Yêu cầu về hiệu năng


- Trang danh sách cần hỗ trợ phân trang.

- Chỉ tải dữ liệu cần thiết cho từng màn hình.

- Hình ảnh bìa cần được tối ưu kích thước.

- API danh sách không trả về toàn bộ nội dung chương.

- Các trường thường xuyên tìm kiếm cần được đánh index.

- Hệ thống cần hạn chế truy vấn cơ sở dữ liệu lặp lại không cần thiết.

## 1.7.3. Yêu cầu về bảo mật

- Mật khẩu không được lưu dưới dạng văn bản thuần.

- Các API quản trị phải yêu cầu xác thực.

- Người dùng chỉ được chỉnh sửa dữ liệu thuộc quyền sở hữu của mình.

- Backend phải kiểm tra quyền thay vì chỉ ẩn nút trên frontend.

- Dữ liệu đầu vào phải được kiểm tra trước khi xử lý.

- Không trả về mật khẩu hoặc thông tin bảo mật trong API response.

- Cần giới hạn độ dài của bình luận và dữ liệu văn bản.

- Các thông tin cấu hình nhạy cảm không được đưa trực tiếp lên GitHub.

## 1.7.4. Yêu cầu về tính toàn vẹn dữ liệu

- Tên đăng nhập và email phải duy nhất.

- Một chương phải thuộc về một truyện tồn tại.

- Thứ tự chương trong cùng một truyện không được trùng.

- Một đánh giá phải gắn với một người dùng và một truyện.

- Khi xóa dữ liệu cần kiểm tra các dữ liệu liên quan.

- Các thao tác cập nhật nhiều bảng cần sử dụng transaction khi cần thiết.

## 1.7.5. Yêu cầu về khả năng bảo trì

- Frontend được chia thành component, page, service và module phù hợp.

- Backend được chia thành controller, service, repository, entity và DTO.

- Không đặt toàn bộ logic nghiệp vụ trong controller.

- Tên class, hàm và biến phải thể hiện đúng mục đích.

- API có quy tắc đặt tên thống nhất.

- Mã nguồn cần có tài liệu hướng dẫn cài đặt.

- Các thay đổi quan trọng được quản lý bằng Git.


## 1.8. Định hướng công nghệ

## 1.8.1. React

React được sử dụng để xây dựng phần giao diện người dùng. Giao diện được chia thành các component có thể tái sử dụng như:

- Header

- Footer

- SearchBar

- StoryCard

- StoryList

- CategoryMenu

- ChapterList

- ChapterReader

- CommentList

- Pagination

Cách tổ chức này giúp giảm việc lặp lại mã nguồn và thuận tiện khi thay đổi giao diện.

Frontend dự kiến có các nhóm thư mục:

## 1.8.2. Java Spring Boot


Spring Boot được sử dụng để xây dựng backend và cung cấp REST API.

Backend dự kiến được tổ chức theo các tầng:

- Controller: tiếp nhận request và trả response.

- Service: xử lý nghiệp vụ.

- Repository: truy xuất dữ liệu.

- Entity: ánh xạ bảng trong cơ sở dữ liệu.

- DTO: truyền dữ liệu vào và ra khỏi API.

- Mapper: chuyển đổi giữa entity và DTO.

- Security: xác thực và phân quyền.

- Exception: xử lý lỗi tập trung.

- Config: chứa cấu hình hệ thống.

Cấu trúc thư mục dự kiến:

## 1.8.3. Cơ sở dữ liệu

Hệ thống có thể sử dụng PostgreSQL để lưu trữ dữ liệu. PostgreSQL là hệ quản trị cơ sở dữ liệu quan hệ mã nguồn mở, hỗ trợ SQL, ràng buộc dữ liệu và transaction, phù hợp với dữ liệu có quan hệ giữa người dùng, truyện, chương và thể loại.


## Các bảng dữ liệu dự kiến gồm:

- users

- roles

- user_roles

- authors

- stories

- categories

- story_categories

- chapters

- favorites

- reading_histories

- comments

- ratings

## 1.8.4. REST API

Frontend và backend giao tiếp thông qua REST API.

```
Một số API dự kiến:
GET /api/stories
GET /api/stories/{storyId}
GET /api/stories/{storyId}/chapters
GET /api/chapters/{chapterId}
POST /api/auth/register
POST /api/auth/login
GET /api/users/me
PUT /api/users/me
POST /api/stories/{storyId}/favorites
DELETE /api/stories/{storyId}/favorites
GET /api/users/me/favorites
GET /api/users/me/reading-history
POST /api/stories/{storyId}/comments
PUT /api/comments/{commentId}
DELETE /api/comments/{commentId}
```


POST /api/admin/stories PUT /api/admin/stories/{storyId} DELETE /api/admin/stories/{storyId}

POST /api/admin/stories/{storyId}/chapters PUT /api/admin/chapters/{chapterId}

DELETE /api/admin/chapters/{chapterId}
