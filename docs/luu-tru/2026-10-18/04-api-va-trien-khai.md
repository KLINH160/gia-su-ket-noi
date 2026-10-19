# API dự kiến và bộ khung triển khai

Đã có: `GET /api/system/status` trả JSON `{application, status, mode, database}`. `UP` chỉ là liveness của API. `demo` trả database `DISABLED`, `mysql` trả `NOT_CHECKED`. Không có endpoint ghi dữ liệu thật ở bước này.

Các endpoint bên dưới mới là thiết kế, chưa được lập trình:

| Method và đường dẫn | Quyền | Mục đích |
|---|---|---|
| POST /api/auth/register | Khách | Chỉ PARENT hoặc TUTOR |
| POST /api/auth/login | Khách | Đăng nhập |
| POST /api/auth/logout | Đã đăng nhập | Kết thúc phiên |
| GET /api/me | Đã đăng nhập | Thông tin tài khoản hiện tại |
| GET /api/tutors | Công khai | Phân trang, lọc môn/khối lớp/giá/khu vực/hình thức |
| GET /api/tutors/{id} | Công khai | Chỉ hồ sơ đã duyệt và tài khoản hoạt động |
| GET /api/tutor/profile | TUTOR | Xem hồ sơ riêng kể cả chưa duyệt |
| PUT /api/tutor/profile | TUTOR | Sửa hồ sơ của mình theo quy tắc duyệt |
| POST /api/tutor/profile/submit | TUTOR | Gửi duyệt |
| POST /api/tutor/profile/withdraw | TUTOR | Rút hồ sơ đang chờ về nháp |
| POST /api/admin/tutors/{id}/decisions | ADMIN | Duyệt/yêu cầu bổ sung, lưu lịch sử |
| POST /api/requests | PARENT | Gửi yêu cầu, lấy parent_id từ tài khoản đăng nhập |
| GET /api/requests | PARENT/TUTOR | Chỉ các yêu cầu liên quan đến mình |
| GET /api/requests/{id} | Chủ thể liên quan | Chi tiết có kiểm tra quyền sở hữu |
| POST /api/requests/{id}/accept | TUTOR nhận yêu cầu | Chấp nhận và tạo lớp trong một giao dịch |
| POST /api/requests/{id}/reject | TUTOR nhận yêu cầu | Từ chối kèm lý do |
| POST /api/requests/{id}/cancel | PARENT tạo yêu cầu | Hủy yêu cầu PENDING |
| GET /api/classes | PARENT/TUTOR | Lớp của mình |
| GET /api/classes/{id} | Chủ thể liên quan | Chi tiết lớp |
| POST /api/classes/{id}/complete | PARENT của lớp | Hoàn thành |
| POST /api/classes/{id}/cancel | PARENT/TUTOR của lớp | Hủy kèm lý do |
| POST /api/classes/{id}/review | PARENT của lớp | Một đánh giá sau khi hoàn thành |

HTTP dự kiến: 201 khi tạo mới, 200 khi đọc/chuyển trạng thái, 400 dữ liệu sai, 401 chưa đăng nhập, 403 không đúng vai trò, 404 tài nguyên không tồn tại hoặc không được phép thấy, 409 trùng/xung đột trạng thái. Không trả entity trực tiếp; dùng DTO để tránh lộ password_hash và dữ liệu học sinh.

## Cập nhật ERD khi chọn MySQL

Chọn MySQL 8.4. Migration dùng collation `utf8mb4_0900_as_ci` để phân biệt dấu tiếng Việt nhưng không phân biệt hoa thường. Tên học sinh vẫn cần chuẩn hóa Unicode, trim và gom khoảng trắng ở Java.

Bổ sung cột kỹ thuật generated `pending_marker`: bằng 1 khi yêu cầu PENDING, NULL ở trạng thái khác. UNIQUE(parent_id,tutor_id,student_name_normalized,subject_id,pending_marker) chặn yêu cầu chờ trùng và cho phép nhiều yêu cầu lịch sử. Không đưa cột kỹ thuật này vào form hoặc DTO tạo yêu cầu.

`profile_snapshot` lưu JSON gồm hồ sơ và offering ở thời điểm duyệt. Các FK dùng hành vi RESTRICT mặc định, không xóa dây chuyền lịch sử. Các ràng buộc liên bảng và quyền vai trò vẫn cần service Java, CHECK không thay thế chúng.

## Thứ tự làm tiếp

1. Xác nhận migration trên MySQL cục bộ.
2. Entity User, đăng ký/đăng nhập và phân quyền bằng Spring Security; chốt cơ chế phiên trước khi tạo API ghi.
3. Hồ sơ gia sư, offering và quản trị duyệt.
4. Tìm kiếm và xem hồ sơ trên React.
5. Yêu cầu học, giao dịch tạo lớp và kiểm thử xung đột.
6. Hoàn thành/hủy lớp và đánh giá.
