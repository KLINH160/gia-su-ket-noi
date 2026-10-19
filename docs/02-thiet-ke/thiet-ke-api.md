# Thiết kế API

Phân biệt rõ **đã có** (`GET /api/system/status`), **triển khai trong đợt này** (UC-02), và **kế hoạch** (các endpoint còn lại). Bằng chứng cuối cùng nằm trong `PROJECT_PROGRESS.md`.

## Quy ước

- URL bắt đầu `/api`; JSON UTF-8; tên trường camelCase.
- Vai trò lưu PARENT/TUTOR/ADMIN, giao diện hiển thị Phụ huynh/Gia sư/Quản trị viên.
- HTTP 201 khi tạo mới, 200 khi đọc/chuyển trạng thái; 400 dữ liệu không hợp lệ; 401 chưa đăng nhập; 403 sai vai trò; 404 không tìm thấy/không được phép xem; 409 trùng hoặc xung đột trạng thái; 503 tính năng cần database nhưng profile demo đang chạy.
- Không nhận parentId/tác giả đánh giá tùy ý từ client cho các hành động của người dùng; lấy từ danh tính đã xác thực khi triển khai UC-03 trở đi.

## API trạng thái — đã có

`GET /api/system/status`, công khai, 200:

```json
{"application":"Gia Sư Kết Nối","status":"UP","mode":"postgres","database":"NOT_CHECKED"}
```

Profile demo trả mode `demo`, database `DISABLED`. `UP` là ứng dụng phản hồi, không phải health check PostgreSQL.

## UC-02 — Đăng ký tài khoản

`POST /api/auth/register`, công khai. Header `Content-Type: application/json`.

```json
{
  "fullName": "Nguyễn An",
  "email": "an@example.com",
  "password": "ViDuMatKhau123!",
  "role": "PARENT"
}
```

Mật khẩu ví dụ chỉ minh họa payload, không dùng cho tài khoản thật. Xác nhận mật khẩu được kiểm tra ở React, không gửi lên API.

| Trường | Kiểm tra |
|---|---|
| fullName | Bắt buộc, trim, 2–120 ký tự |
| email | Bắt buộc, trim, chuyển chữ thường, đúng dạng email, tối đa 254 ký tự |
| password | Tối thiểu 10 ký tự; tối đa 72 byte UTF-8, không tự trim |
| role | Chỉ PARENT hoặc TUTOR; không chấp nhận ADMIN |

Thành công 201:

```json
{"id":1,"fullName":"Nguyễn An","email":"an@example.com","role":"PARENT","status":"ACTIVE"}
```

Không có mật khẩu, passwordHash hoặc token trong response. Với TUTOR, backend đồng thời tạo hồ sơ DRAFT. Email đã tồn tại trả 409; nhiều request đồng thời cùng email không được tạo nhiều tài khoản.

Lỗi dữ liệu 400 theo cấu trúc:

```json
{"code":"VALIDATION_ERROR","message":"Dữ liệu không hợp lệ.","fieldErrors":{"email":"Email không hợp lệ."}}
```

Thông điệp cụ thể có thể chi tiết hơn theo validator; frontend bám `code`, HTTP status và tên trường, không dựa vào toàn bộ chuỗi tiếng Việt. Các mã lỗi thực tế được đối chiếu với handler backend trước khi chốt kết quả kiểm thử.

## Danh mục API kế hoạch

| UC | Endpoint | Request chính | Response dự kiến / lỗi |
|---|---|---|---|
| 03 | POST /auth/login | email, password | 200 tài khoản + phiên; 401 thông tin sai |
| 03 | POST /auth/logout | Không có | 204, hủy phiên |
| 04 | GET /me | Không có | 200 tài khoản; 401 |
| 04 | PATCH /me | fullName, phone | 200 thông tin mới; 400 |
| 04 | PUT /me/password | currentPassword, newPassword | 204; 400/401 |
| 05 | GET /tutors | subjectId, gradeLevel, maxRate, mode, area, page, size | 200 phân trang hồ sơ đã duyệt |
| 05 | GET /tutors/{id} | Không có | 200 hồ sơ công khai; 404 |
| 06 | GET/PUT /tutor/profile | Hồ sơ và offerings khi PUT | 200 hồ sơ riêng; 400/403/409 |
| 06 | POST /tutor/profile/submit | version | 200 PENDING_REVIEW; 400/409 |
| 06 | POST /tutor/profile/withdraw | version | 200 DRAFT; 409 |
| 07 | POST /admin/tutors/{id}/decisions | decision, reason, version | 200 hồ sơ; 400/403/409 |
| 01 | POST /requests | tutorId, subjectId, studentName, gradeLevel, learningGoal, learningMode, learningArea, desiredSchedule, proposedHourlyRate, desiredStartDate, note | 201 yêu cầu PENDING; 400/409 |
| 08 | GET /requests; GET /requests/{id} | status, page, size cho danh sách | 200 yêu cầu liên quan người gọi; 404 |
| 09 | POST /requests/{id}/cancel | version | 200 CANCELLED; 409 |
| 10 | POST /requests/{id}/accept | version | 200 yêu cầu và lớp; 409 |
| 11 | POST /requests/{id}/reject | reason, version | 200 REJECTED; 400/409 |
| 12 | GET /classes; GET /classes/{id} | status, page, size cho danh sách | 200 lớp của người gọi; 404 |
| 13 | POST /classes/{id}/complete | version | 200 COMPLETED; 403/409 |
| 14 | POST /classes/{id}/cancel | reason, version | 200 CANCELLED; 400/409 |
| 15 | POST /classes/{id}/review | rating, comment | 201 đánh giá; 400/409 |
| 16 | GET /admin/users; PATCH /admin/users/{id}/status | q, page; status khi PATCH | 200 danh sách/tài khoản; 403 |
| 17 | GET /admin/requests; GET /admin/classes | trạng thái, id các bên, phân trang | 200 danh sách quản trị; 403 |

Tất cả đường dẫn trong bảng có tiền tố `/api`. Bảng là hợp đồng dự thảo; mở rộng schema request/response chi tiết cùng lúc triển khai từng UC, không mô tả chúng là API hiện đang gọi được.

Ví dụ response dự kiến UC-10:

```json
{"requestId":12,"requestStatus":"ACCEPTED","classId":8,"classStatus":"ACTIVE"}
```

Nếu yêu cầu đã được chấp nhận bởi chính gia sư đó, service trả lại lớp đã tồn tại; không tạo lần hai. Nếu bị hủy hoặc từ chối, trả 409. Lớp và yêu cầu phải commit/rollback cùng nhau.
