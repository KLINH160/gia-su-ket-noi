# Quyết định thiết kế

| Mã | Ngày | Quyết định | Lý do / hệ quả |
|---|---|---|---|
| QD-01 | 19/10/2026 | PostgreSQL 16 thay MySQL | Theo yêu cầu người thực hiện; đổi driver, Flyway và SQL, kiểm thử trên PostgreSQL thật |
| QD-02 | 19/10/2026 | Một repository, một backend | Vừa sức một sinh viên; tách mô-đun bằng package, không tách dịch vụ |
| QD-03 | 19/10/2026 | Giữ migration MySQL dưới db/legacy/mysql | Giữ bằng chứng lịch sử; không chạy trong profile PostgreSQL; không sửa checksum của database cũ |
| QD-04 | 19/10/2026 | Yêu cầu và lớp là hai bảng | Yêu cầu bị từ chối không sinh lớp; một yêu cầu chỉ sinh một lớp |
| QD-05 | 19/10/2026 | Email duy nhất không phân biệt hoa thường | Chuẩn hóa ở Java và unique index lower(email) ở PostgreSQL |
| QD-06 | 19/10/2026 | Mật khẩu BCrypt, tối thiểu 10 ký tự, tối đa 72 byte UTF-8 | Giới hạn byte tránh cắt mật khẩu dài; không trim, không gửi hash ra API |
| QD-07 | 19/10/2026 | Tạo tài khoản TUTOR và hồ sơ DRAFT trong một giao dịch | Không có tài khoản gia sư tạo thành công nhưng thiếu hồ sơ |
| QD-08 | 19/10/2026 | Đăng ký không tự đăng nhập | UC-02 có thể kiểm thử độc lập; UC-03 sẽ quản lý phiên |
| QD-09 | 19/10/2026 | Tên tài liệu tiếng Việt không dấu | Dễ tìm, liên kết ổn định; nội dung vẫn viết tiếng Việt có dấu |
| QD-10 | 19/10/2026 | Nhánh tính năng + tự rà soát PR + CI | Phù hợp làm một mình; không giả có người review độc lập hoặc bắt buộc hai người duyệt |

Các mục QD-05–08 là chi tiết kỹ thuật được chốt để triển khai UC-02 trong yêu cầu hiện tại, không phải tính năng nâng cao mới. Những thay đổi về quyền người dùng hoặc kết quả nghiệp vụ phải cập nhật SRS và hỏi người thực hiện khi có nhiều cách hiểu ảnh hưởng phạm vi.

## Chưa quyết định

- Cơ chế phiên đăng nhập và thời hạn phiên: chốt ở UC-03; dự kiến session cookie HttpOnly cùng origin, cần CSRF cho các thao tác có xác thực.
- Xác minh email, quên mật khẩu, tải bằng cấp, thông báo email: chưa thuộc UC-02.
- Nhà cung cấp hosting, tên miền, HTTPS và backup ngoài máy: chốt khi chuẩn bị triển khai.
- Lịch từng buổi, kiểm tra trùng lịch và gợi ý gia sư: ưu tiên sau luồng cốt lõi, không hứa hoàn thành tất cả trước 20/11.

## Quy tắc sử dụng trợ lý AI

Trước khi viết chức năng: đọc UC, điều kiện chấp nhận, ERD và API tương ứng. Sau khi viết: kiểm thử, ghi đúng kết quả, đọc diff và cập nhật tiến độ. Không tự chuyển UC sang hoàn thành chỉ vì đã tạo file. Không tự thêm thanh toán, chat hoặc đổi quyền vai trò khi chưa được yêu cầu.
