# Đề cương đồ án: Website kết nối gia sư với phụ huynh

- Người thực hiện: Nguyễn Thị Khánh Linh; hình thức: làm cá nhân.
- Bản cơ sở: 19/10/2026; hạn hoàn thành dự kiến: 20/11/2026.
- Công nghệ lựa chọn: Java 21, Spring Boot, React, JavaScript và PostgreSQL 16.
- Đây là tài liệu làm việc; báo cáo nộp cuối cùng phải đối chiếu mẫu và yêu cầu của giảng viên.

## 1. Bài toán

Phụ huynh cần tìm gia sư phù hợp với môn học, khối lớp, hình thức học, khu vực và mức học phí. Gia sư cần giới thiệu năng lực và tiếp nhận nhu cầu có đủ thông tin để quyết định. Khi chỉ trao đổi qua nhiều kênh riêng lẻ, các bên khó theo dõi trạng thái kết nối và nội dung đã thống nhất.

Website tập trung hồ sơ gia sư, hỗ trợ tìm kiếm, gửi yêu cầu học, phản hồi yêu cầu và theo dõi lớp phát sinh. Quản trị viên kiểm tra hồ sơ trước khi công khai và theo dõi hoạt động hệ thống.

## 2. Mục tiêu và kết quả bàn giao

1. Hoàn thành luồng từ đăng ký gia sư, duyệt hồ sơ, tìm kiếm, gửi yêu cầu, chấp nhận tạo lớp đến hoàn thành và đánh giá.
2. Phân quyền và kiểm tra quyền sở hữu dữ liệu ở backend; không chỉ ẩn thao tác trên giao diện.
3. Dữ liệu trên PostgreSQL có khóa ngoại, ràng buộc duy nhất, kiểm tra giá trị và xử lý giao dịch cho nghiệp vụ quan trọng.
4. Có tài liệu yêu cầu, kiến trúc, dữ liệu, API, kế hoạch và kết quả kiểm thử liên kết với mã UC.
5. Có hướng dẫn chạy môi trường, dữ liệu minh họa không nhạy cảm và kịch bản demo tái hiện được.
6. Có lịch sử Git phản ánh thay đổi thực tế; báo cáo tiến độ một lần mỗi tuần.

Sản phẩm bàn giao gồm mã nguồn, migration, bộ kiểm thử, tài liệu Markdown, bản báo cáo theo mẫu trường và slide bảo vệ. Không coi các mục tiêu này là kết quả đã đạt; trạng thái thực tế nằm trong [theo dõi tiến độ](../PROJECT_PROGRESS.md).

## 3. Người sử dụng

| Vai trò | Phạm vi |
|---|---|
| Khách | Tìm kiếm, xem hồ sơ đã duyệt, đăng ký và đăng nhập |
| Phụ huynh | Gửi nhu cầu, quản lý yêu cầu và lớp của mình, đánh giá |
| Gia sư | Soạn hồ sơ, phản hồi yêu cầu, theo dõi và hủy lớp của mình |
| Quản trị viên | Duyệt hồ sơ, quản lý tài khoản, theo dõi kết nối |

Mỗi tài khoản có một vai trò. Học sinh chưa có tài khoản; tên gọi và nhu cầu học nằm trong yêu cầu do phụ huynh tạo. Không cho đăng ký tự chọn quyền quản trị viên.

## 4. Phạm vi bắt buộc

Phạm vi được cụ thể hóa thành [17 chức năng](dac-ta-yeu-cau.md): tài khoản; tìm kiếm và xem gia sư; soạn, gửi và duyệt hồ sơ; gửi, xem, hủy, chấp nhận hoặc từ chối yêu cầu; xem, hoàn thành và hủy lớp; đánh giá; quản trị tài khoản và theo dõi kết nối.

Một yêu cầu chỉ tạo một lớp khi được chấp nhận. Yêu cầu giữ nguyên nội dung thỏa thuận sau khi gửi. Mỗi lớp hoàn thành chỉ có một đánh giá từ phụ huynh liên quan. Cần xử lý cả dữ liệu sai, người dùng sai quyền và các thao tác đến đồng thời.

## 5. Phần mở rộng sau khi hoàn thành cốt lõi

| Ưu tiên | Mở rộng | Điều kiện bắt đầu |
|---|---|---|
| 1 | Lịch từng buổi và phát hiện trùng lịch | Luồng chính và kiểm thử chấp nhận đã đạt; còn thời gian dự phòng |
| 2 | Gợi ý gia sư theo điểm phù hợp và giải thích | Có dữ liệu tiêu chí rõ ràng; đặc tả và cách đánh giá được chốt |
| Sau đó | Theo dõi tiến độ học, tin tìm gia sư và ứng tuyển | Được bổ sung vào phạm vi và kế hoạch chính thức |

Thanh toán, gọi video, chat thời gian thực, tự xác minh bằng cấp và tự xác nhận buổi học thực tế không thuộc bản cốt lõi. Đăng ký hiện chưa gồm xác minh email, quên mật khẩu hoặc tự đăng nhập sau đăng ký. Lịch mong muốn ban đầu là mô tả; chưa có kiểm tra trùng lịch tự động.

## 6. Giải pháp và cách tổ chức cá nhân

Một repository chứa frontend, backend, tài liệu và hạ tầng phát triển. Backend là một ứng dụng Spring Boot chia theo nghiệp vụ; frontend React gọi API. PostgreSQL lưu dữ liệu và Flyway quản lý phiên bản schema. Chưa cần chia microservice.

Làm từng chức năng xuyên suốt: đọc yêu cầu → chốt dữ liệu/API → triển khai backend và frontend → kiểm thử → cập nhật tài liệu → commit và tự review. Mỗi nhánh tập trung một phần có thể kiểm chứng. Một người làm không cần giả lập nhiều thành viên hoặc bắt buộc một người duyệt thứ hai.

Tên thư mục và tên tài liệu dùng tiếng Việt không dấu; nội dung giải thích dùng tiếng Việt có dấu. Các tên quy ước kỹ thuật như `README.md`, `PROJECT_PROGRESS.md`, `pom.xml`, trường JSON và mã vai trò được giữ để tương thích công cụ hoặc đúng yêu cầu.

## 7. Mốc thực hiện

| Mốc | Mục tiêu báo cáo |
|---|---|
| 19–24/10 | Chuẩn hóa tài liệu, ERD, PostgreSQL, môi trường phát triển, UC-02 |
| 25–31/10 | UC-03/04, UC-06/07; phân quyền và hồ sơ được duyệt |
| 01–07/11 | UC-05, UC-01, UC-08/09/10/11/12; kết nối tạo lớp |
| 08–14/11 | UC-13/14/15/16/17, kiểm thử tích hợp và luồng demo |
| 15–19/11 | Sửa lỗi, hoàn thiện báo cáo, slide và tập bảo vệ |

Đây là mục tiêu lập kế hoạch, không bảo đảm mọi chức năng mở rộng sẽ hoàn thành. Khi chậm tiến độ, giữ cốt lõi và giảm phần mở rộng; không bỏ phân quyền hoặc kiểm thử giao dịch để chạy theo số lượng chức năng.

## 8. Rủi ro và cách xử lý

| Rủi ro | Cách xử lý |
|---|---|
| Làm một mình, thời gian ngắn | Một việc đang làm tại một thời điểm; backlog có phụ thuộc, chia phần nhỏ |
| Thay đổi nghiệp vụ muộn | Ghi quyết định trong đặc tả và cập nhật tiêu chí chấp nhận trước code |
| Chạy được ở máy này nhưng lỗi máy khác | Có migration, cấu hình mẫu, hướng dẫn môi trường và CI |
| Nhấp lặp hoặc thao tác đồng thời | Giao dịch, ràng buộc DB và kiểm thử đồng thời cho hành động quan trọng |
| AI tự thêm nghiệp vụ hoặc báo hoàn thành quá sớm | Gắn thay đổi với UC/tiêu chí; chỉ ghi đạt khi có kết quả kiểm chứng |

## 9. Tiêu chí hoàn tất đồ án

Luồng chính chạy qua các vai trò với PostgreSQL thật; các trường hợp sai quyền và dữ liệu sai bị chặn; kiểm thử quan trọng đạt; một máy mới có thể làm theo hướng dẫn; tài liệu mô tả đúng phần đã làm và tách rõ phần dự kiến; báo cáo/slide theo yêu cầu trường. Các mục chưa đạt phải được ghi trung thực trong tiến độ và hạn chế.
