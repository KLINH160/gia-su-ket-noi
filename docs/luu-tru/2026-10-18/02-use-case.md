# Phân tích use case — bản 0.1

Tài liệu tiếp nối `01-pham-vi-va-luong-chinh.md`. Sơ đồ UML nguồn: `diagrams/use-case.puml`. Hình oval là chức năng; đường nối là quyền tham gia, không phải thứ tự thực hiện. Khách là người chưa đăng nhập. Phụ huynh và gia sư cũng sử dụng được chức năng tìm kiếm công khai.

## Danh mục chức năng

| Mã | Use case | Tác nhân | Kết quả |
|---|---|---|---|
| UC-01 | Gửi yêu cầu học | Phụ huynh | Tạo yêu cầu chờ phản hồi |
| UC-02 | Đăng ký | Khách | Tài khoản phụ huynh hoặc gia sư |
| UC-03 | Đăng nhập | Khách | Phiên đăng nhập hợp lệ |
| UC-04 | Quản lý tài khoản cá nhân | Phụ huynh, gia sư, quản trị viên | Cập nhật thông tin hoặc đổi mật khẩu |
| UC-05 | Tìm và xem gia sư | Khách, phụ huynh, gia sư | Xem hồ sơ công khai đã duyệt |
| UC-06 | Soạn hồ sơ và gửi duyệt | Gia sư | Hồ sơ chuyển sang chờ duyệt |
| UC-07 | Duyệt hồ sơ | Quản trị viên | Duyệt hoặc yêu cầu bổ sung, ghi lịch sử |
| UC-08 | Xem yêu cầu học | Phụ huynh, gia sư | Xem yêu cầu liên quan đến mình |
| UC-09 | Hủy yêu cầu đang chờ | Phụ huynh | Yêu cầu chuyển đã hủy |
| UC-10 | Chấp nhận yêu cầu | Gia sư | Yêu cầu được chấp nhận và tạo một lớp |
| UC-11 | Từ chối yêu cầu | Gia sư | Yêu cầu bị từ chối, có lý do |
| UC-12 | Xem lớp học | Phụ huynh, gia sư | Xem lớp của mình |
| UC-13 | Hoàn thành lớp | Phụ huynh | Lớp chuyển đã hoàn thành |
| UC-14 | Hủy lớp | Phụ huynh, gia sư | Lớp chuyển đã hủy, lưu lý do và người hủy |
| UC-15 | Đánh giá gia sư | Phụ huynh | Tạo đánh giá cho lớp hoàn thành |
| UC-16 | Quản lý tài khoản | Quản trị viên | Tra cứu, khóa hoặc mở khóa tài khoản |
| UC-17 | Theo dõi kết nối | Quản trị viên | Tra cứu yêu cầu và lớp để hỗ trợ quản lý |

Không vẽ quan hệ include từ mọi use case sang Đăng nhập: đăng nhập là tiền điều kiện của chức năng được bảo vệ, không phải bước luôn thực thi lại. UC-10 include Tạo lớp vì mỗi lần chấp nhận thành công bắt buộc phải tạo lớp. UC-15 không include Hoàn thành lớp: lớp đã hoàn thành là tiền điều kiện.

## UC-06 — Soạn hồ sơ và gửi duyệt

- Tiền điều kiện: gia sư đăng nhập, tài khoản hoạt động.
- Luồng chính: nhập giới thiệu, học vấn, kinh nghiệm, hình thức, khu vực, thời gian tham khảo; thêm các môn/khối lớp và giá tham khảo; lưu nháp; gửi duyệt; hệ thống kiểm tra và chuyển PENDING_REVIEW.
- Ngoại lệ: không có môn nhận dạy hoặc thông tin không hợp lệ thì giữ nháp và báo lỗi.
- Quyết định cho phiên bản đầu: khi gia sư sửa hồ sơ APPROVED, hệ thống chuyển hồ sơ về DRAFT và tạm ẩn khỏi tìm kiếm. Gia sư sửa xong phải gửi duyệt lại. Giao diện phải báo rõ trước khi bắt đầu sửa. Hồ sơ đang chờ duyệt không được sửa; có thể rút về nháp trước khi quản trị viên quyết định. Thao tác rút và duyệt phải kiểm tra trạng thái cạnh tranh.
- Yêu cầu đã gửi vẫn giữ nguyên nội dung. Khi chấp nhận, kiểm tra lại tài khoản và trạng thái APPROVED của gia sư; nếu đang sửa/chờ duyệt thì yêu cầu vẫn chờ, chưa được chấp nhận. Lớp đã tạo không bị thay đổi do sửa hồ sơ.

## UC-07 — Duyệt hồ sơ

- Tiền điều kiện: quản trị viên đăng nhập; hồ sơ đang PENDING_REVIEW.
- Luồng chính: xem hồ sơ và môn nhận dạy → chọn duyệt hoặc yêu cầu bổ sung → nhập lý do nếu yêu cầu bổ sung → lưu trạng thái và bản ghi xét duyệt trong một giao dịch.
- Ngoại lệ: hồ sơ đã rút hoặc đã được xử lý thì từ chối thao tác cũ và yêu cầu tải lại.
- Hậu điều kiện: APPROVED hoặc NEEDS_CHANGES. Lịch sử lưu người duyệt, thời điểm, quyết định và nội dung hồ sơ tại thời điểm duyệt.

## UC-10 — Chấp nhận yêu cầu và tạo lớp

- Tiền điều kiện: gia sư nhận yêu cầu đang đăng nhập và hoạt động; hồ sơ được duyệt; phụ huynh còn hoạt động; yêu cầu PENDING thuộc gia sư đó.
- Luồng chính: xem nội dung → bấm chấp nhận → kiểm tra lại quyền và trạng thái → chuyển ACCEPTED → tạo lớp ACTIVE tham chiếu yêu cầu → commit cả hai thay đổi → thông báo kết quả.
- Không tự đổi giá: học phí đã thống nhất chính là mức phụ huynh đề xuất mà gia sư đã chấp nhận.
- Ngoại lệ: yêu cầu đã hủy/từ chối thì không tạo lớp; yêu cầu đã chấp nhận thì trả về lớp đã tồn tại cho chính gia sư có quyền; lỗi tạo lớp thì rollback, yêu cầu vẫn PENDING.
- Backend sử dụng giao dịch, khóa hàng hoặc cập nhật có điều kiện theo version. Ràng buộc UNIQUE trên request_id của lớp là lớp bảo vệ bổ sung.

## UC-13 và UC-14 — Kết thúc lớp

- Hoàn thành: chỉ phụ huynh của lớp ACTIVE được chuyển COMPLETED.
- Hủy: phụ huynh hoặc gia sư của lớp ACTIVE được chuyển CANCELLED, bắt buộc lý do.
- Lưu thời điểm kết thúc, người thực hiện; lớp kết thúc không mở lại ở phiên bản đầu.
- Nếu hai thao tác đến đồng thời, chỉ một chuyển trạng thái thành công. Không dùng cập nhật ghi đè không kiểm tra trạng thái.

## UC-15 — Đánh giá gia sư

- Tiền điều kiện: phụ huynh của lớp đăng nhập, tài khoản hoạt động; lớp COMPLETED; chưa có đánh giá.
- Luồng chính: chọn 1–5 sao, nhập nhận xét tối đa 1.000 ký tự → gửi → kiểm tra quyền và trạng thái → lưu đánh giá.
- Hậu điều kiện: đánh giá liên kết đúng lớp. Điểm trung bình lấy từ các đánh giá hợp lệ của gia sư; chưa có đánh giá thì hiển thị “Chưa có đánh giá”, không hiển thị 0 sao.
- Ngoại lệ: đã đánh giá thì không tạo thêm; lớp hủy/chưa hoàn thành hoặc sai người dùng thì từ chối.
- Phiên bản đầu chưa có sửa/xóa đánh giá. Công khai nội dung đánh giá và tên hiển thị phụ huynh; không công khai dữ liệu học sinh hoặc nội dung yêu cầu.

## Quy tắc chung

Kiểm tra quyền ở backend cho từng tài nguyên, không chỉ ẩn nút trên React. Tài khoản bị khóa không được thực hiện thao tác có xác thực. Khóa tài khoản không xóa lịch sử, không tự hủy lớp. Quản trị viên theo dõi và hỗ trợ xử lý các lớp còn hoạt động theo quy trình quản trị; tự động chuyển giao hoặc hoàn tiền không thuộc phạm vi này.
