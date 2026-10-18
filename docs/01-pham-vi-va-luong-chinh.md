# Website kết nối gia sư với phụ huynh

Bản phân tích 0.1 — ngày 18/10/2026. Đây là phạm vi đề xuất ban đầu, cần đối chiếu yêu cầu của giảng viên.

Tài liệu tiếp nối: [Use case](02-use-case.md) và [Thiết kế dữ liệu](03-thiet-ke-co-so-du-lieu.md). Hai tài liệu này làm rõ quy trình sửa hồ sơ đã duyệt, giao dịch tạo lớp và cách lưu nội dung thỏa thuận; dùng các quyết định chi tiết đó khi triển khai.

## 1. Mục tiêu và công nghệ dự kiến

Hỗ trợ phụ huynh tìm gia sư, gửi nhu cầu học và quản lý kết nối; hỗ trợ gia sư giới thiệu năng lực và phản hồi yêu cầu. Người thực hiện: một sinh viên. Hạn dự kiến: 20/11/2026.

Frontend: React và JavaScript. Backend: Java Spring Boot. Cơ sở dữ liệu quan hệ sẽ chốt khi thiết kế ERD. Chưa cần chọn phiên bản thư viện ở giai đoạn phân tích.

## 2. Vai trò

| Vai trò | Quyền chính |
|---|---|
| Khách | Xem hồ sơ công khai đã duyệt, tìm kiếm |
| Phụ huynh | Gửi và quản lý yêu cầu của mình; xem lớp, hoàn thành và đánh giá |
| Gia sư | Quản lý hồ sơ của mình; phản hồi yêu cầu gửi đến mình; xem lớp của mình |
| Quản trị viên | Duyệt hồ sơ, quản lý tài khoản, theo dõi kết nối |

Thông tin học sinh thuộc yêu cầu học, chưa tạo tài khoản học sinh. Không cho người đăng ký tự chọn quyền quản trị viên.

## 3. Phạm vi

Bắt buộc: tài khoản và phân quyền; hồ sơ gia sư và duyệt; tìm kiếm; gửi, nhận, hủy, chấp nhận hoặc từ chối yêu cầu; tạo lớp khi chấp nhận; hoàn thành lớp và đánh giá; quản trị cơ bản.

Ưu tiên mở rộng sau luồng chính: lịch từng buổi và phát hiện trùng lịch; gợi ý gia sư dựa trên tiêu chí có giải thích. Các khả năng khác: theo dõi tiến độ, đăng tin tìm gia sư và ứng tuyển. Chưa cam kết hoàn thành các chức năng mở rộng trước hạn.

Thanh toán và gọi video không thuộc phiên bản đầu. Lịch ở biểu mẫu ban đầu là nội dung mong muốn, chưa hỗ trợ kiểm tra trùng lịch tự động. Khi làm chức năng lịch sẽ bổ sung các bản ghi thời gian có cấu trúc.

## 4. Luồng chính

1. Gia sư đăng ký, nhập hồ sơ và gửi duyệt.
2. Quản trị viên duyệt hoặc yêu cầu bổ sung kèm lý do.
3. Phụ huynh tìm và xem hồ sơ đã duyệt.
4. Phụ huynh đăng nhập, điền và gửi yêu cầu học.
5. Gia sư chấp nhận hoặc từ chối kèm lý do.
6. Nếu chấp nhận, hệ thống tạo đúng một lớp học từ yêu cầu, lưu học phí và nội dung đã thống nhất.
7. Phụ huynh xác nhận hoàn thành lớp và đánh giá.

## 5. Trạng thái và quy tắc

Hồ sơ: Nháp → Chờ duyệt → Đã duyệt hoặc Cần bổ sung. Hồ sơ cần bổ sung được sửa và gửi duyệt lại. Quy trình sửa hồ sơ đã duyệt cần đặc tả riêng trước triển khai, tránh hiển thị nội dung chưa duyệt.

| Đối tượng | Chuyển trạng thái | Người thực hiện |
|---|---|---|
| Yêu cầu | Chờ phản hồi → Đã chấp nhận | Gia sư nhận yêu cầu |
| Yêu cầu | Chờ phản hồi → Đã từ chối | Gia sư nhận yêu cầu, có lý do |
| Yêu cầu | Chờ phản hồi → Đã hủy | Phụ huynh tạo yêu cầu |
| Lớp | Đang học → Đã hoàn thành | Phụ huynh của lớp |
| Lớp | Đang học → Đã hủy | Một trong hai bên, bắt buộc ghi lý do |

Không cho hủy yêu cầu đã chấp nhận; khi đó xử lý trên lớp học. Không sửa trực tiếp yêu cầu đã gửi; hủy yêu cầu đang chờ và tạo lại. Trạng thái kết thúc không được mở lại ở phiên bản đầu.

Chấp nhận yêu cầu và tạo lớp phải trong cùng giao dịch database; một yêu cầu chỉ sinh một lớp. Nếu phụ huynh hủy cùng lúc gia sư chấp nhận, chỉ một thao tác được thành công; thao tác còn lại nhận thông báo trạng thái đã thay đổi.

Chặn yêu cầu chờ bị trùng theo phụ huynh, gia sư, tên gọi học sinh đã chuẩn hóa và môn học. Đây là quy tắc tạm cho MVP; tên học sinh chưa phải mã định danh. Khi bổ sung hồ sơ học sinh, chuyển sang dùng student_id.

Chỉ phụ huynh của lớp hoàn thành được đánh giá; tối đa một đánh giá mỗi lớp. Không cho đánh giá lớp hủy. Hồ sơ đã duyệt không đồng nghĩa với bằng cấp đã được xác minh. Hoàn thành lớp là xác nhận của phụ huynh.

## 6. Đặc tả UC-01 — Gửi yêu cầu học

- Tác nhân: phụ huynh.
- Tiền điều kiện: tài khoản hoạt động, đã đăng nhập đúng vai trò; gia sư hoạt động và có hồ sơ được duyệt.
- Kích hoạt: bấm Gửi yêu cầu học trên hồ sơ gia sư.
- Luồng thành công: mở biểu mẫu → nhập nhu cầu → kiểm tra → gửi → backend kiểm tra quyền, trạng thái gia sư và dữ liệu → lưu yêu cầu Chờ phản hồi → hiển thị xác nhận.
- Hậu điều kiện: yêu cầu gắn đúng phụ huynh và gia sư; chưa có lớp học.
- Ngoại lệ: chưa đăng nhập thì đăng nhập và trở lại hồ sơ; sai vai trò thì không cho gửi; dữ liệu thiếu hoặc sai thì hiển thị lỗi cạnh trường; hồ sơ ngừng hoạt động thì từ chối; yêu cầu trùng thì hướng đến yêu cầu đang chờ; lỗi mạng thì giữ dữ liệu và cho thử lại.
- Chống gửi lặp: vô hiệu nút trong khi gửi; backend vẫn phải kiểm tra trùng và xử lý yêu cầu lặp an toàn.

| Trường | Ràng buộc đề xuất |
|---|---|
| Tên gọi học sinh | Bắt buộc, bỏ khoảng trắng hai đầu, 1–60 ký tự |
| Khối lớp | Bắt buộc, thuộc các lớp gia sư nhận dạy |
| Môn | Thuộc môn gia sư nhận dạy; không tin giá trị do trình duyệt tự gửi |
| Mục tiêu | Bắt buộc, tối đa 500 ký tự |
| Hình thức | Thuộc hình thức gia sư hỗ trợ |
| Khu vực | Bắt buộc khi trực tiếp, tối đa 120 ký tự; chưa cần địa chỉ nhà chi tiết |
| Lịch mong muốn | Bắt buộc, tối đa 200 ký tự |
| Học phí đề xuất | Từ 10.000 đến 5.000.000 đồng/giờ, bước 1.000; giới hạn sản phẩm đề xuất, không phải khảo sát thị trường |
| Ngày bắt đầu | Không trước ngày hiện tại theo Asia/Ho_Chi_Minh |
| Ghi chú | Không bắt buộc, tối đa 500 ký tự |

## 7. Màn hình

| Mã | Màn hình | Thành phần và thao tác |
|---|---|---|
| UI-01 | Danh sách gia sư | Bộ lọc, kết quả, học phí, xem hồ sơ; trạng thái không có kết quả |
| UI-02 | Chi tiết gia sư | Giới thiệu, học vấn, môn/lớp, khu vực, thời gian, đánh giá; gửi yêu cầu |
| UI-03 | Gửi yêu cầu | Biểu mẫu, kiểm tra dữ liệu, gửi, xác nhận |
| UI-04 | Yêu cầu của phụ huynh | Danh sách, chi tiết, trạng thái, hủy khi đang chờ |
| UI-05 | Yêu cầu của gia sư | Danh sách, chi tiết, chấp nhận/từ chối |
| UI-06 | Lớp học | Danh sách và chi tiết, hoàn thành/hủy, đánh giá |
| UI-07 | Hồ sơ gia sư của tôi | Sửa bản nháp, gửi duyệt, xem lý do bổ sung |
| UI-08 | Duyệt hồ sơ | Danh sách, chi tiết, duyệt/yêu cầu bổ sung |
| UI-09 | Tài khoản | Đăng ký, đăng nhập, thông tin cá nhân, đổi mật khẩu |
| UI-10 | Quản trị | Tài khoản và yêu cầu kết nối |

Phác thảo tương tác hiện thực hiện UI-01 đến UI-03 và xác nhận/hủy yêu cầu mẫu. Dữ liệu giả, giả lập đã đăng nhập là phụ huynh. Không có backend, lưu database, thông báo thật hoặc kiểm tra quyền thật. Các bộ lọc hiện có: môn, hình thức, ngân sách. Bộ lọc khối lớp và khu vực bổ sung khi triển khai.

## 8. Dữ liệu dự kiến cho ERD

Các nhóm dữ liệu: User, TutorProfile, Subject, TutorSubject (kèm khối lớp và giá tham khảo nếu cần), TutoringRequest, TutoringClass, Review. Bổ sung ProfileReview hoặc lịch sử duyệt để lưu người xử lý và lý do. Thông tin yêu cầu được lưu thành bản chụp nội dung thống nhất, không phụ thuộc học phí hiện tại của hồ sơ.

Khi mở rộng: StudentProfile, LessonSession và AvailabilitySlot. Đây mới là danh mục khái niệm, chưa phải ERD hoặc schema đã chốt.

## 9. Kiểm thử chấp nhận cần thực hiện khi lập trình

1. Khách chỉ tìm thấy hồ sơ đã duyệt, đang hoạt động.
2. Người không có quyền không thể gửi/xem yêu cầu của người khác dù sửa URL hoặc payload.
3. Dữ liệu thiếu, chuỗi toàn khoảng trắng, giá ngoài giới hạn hoặc ngày quá khứ bị từ chối ở frontend và backend.
4. Học trực tiếp yêu cầu khu vực; trực tuyến không bắt buộc khu vực.
5. Gửi hợp lệ sinh yêu cầu Chờ phản hồi, chưa tạo lớp.
6. Chấp nhận sinh đúng một lớp; nhấp lặp hoặc hai thao tác đồng thời không sinh lớp trùng.
7. Từ chối và hủy không sinh lớp.
8. Không đánh giá được lớp chưa hoàn thành, lớp người khác hoặc đánh giá lần hai.
9. Thông tin học sinh không lộ ở hồ sơ công khai.
10. Kiểm tra bố cục di động, điều hướng bàn phím, lỗi mạng và kết quả rỗng.

## 10. Kế hoạch báo cáo

| Mốc | Sản phẩm dự kiến |
|---|---|
| 18–24/10 | Phạm vi, đặc tả nghiệp vụ, phác thảo, use case và ERD bản đầu |
| 25–31/10 | Tài khoản, phân quyền, hồ sơ và duyệt hồ sơ |
| 01–07/11 | Tìm gia sư, gửi và phản hồi yêu cầu, tạo lớp |
| 08–14/11 | Hoàn thành/hủy lớp, đánh giá, quản trị, kiểm thử; cân nhắc mở rộng nếu phần cốt lõi ổn định |
| 15–19/11 | Sửa lỗi, báo cáo, dữ liệu demo, slide và tập bảo vệ |

Mỗi tuần ghi rõ: việc hoàn thành có bằng chứng, việc chưa xong, khó khăn và kế hoạch tuần tới. Mốc trên là kế hoạch, không phải kết quả đã đạt.
