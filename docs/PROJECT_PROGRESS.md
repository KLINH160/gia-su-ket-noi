# Tiến độ dự án

Cập nhật 19/10/2026. Người thực hiện: Nguyễn Thị Khánh Linh. Hạn dự kiến: 20/11/2026. Nhánh đợt này: `codex/postgres-project-foundation`; nhánh chính: `master`.

| Hạng mục | Trạng thái | Bằng chứng / giới hạn |
|---|---|---|
| Đọc repository, giữ lịch sử | Hoàn thành | Giữ bốn commit gốc; tài liệu cũ ở luu-tru |
| Phân tích UC-01–UC-17 | Hoàn thành bản cơ sở | 17 UC, 58 điều kiện chấp nhận trong đặc tả |
| Tổ chức tài liệu | Hoàn thành bản cơ sở | Tên đường dẫn tiếng Việt không dấu; mục lục 00-huong-dan.md |
| Kiến trúc, ERD, API | Hoàn thành bản cơ sở | Có nguồn sơ đồ; phân biệt API đã có và kế hoạch |
| Backlog và thứ tự phát triển | Hoàn thành bản cơ sở | 05-quan-ly/danh-sach-cong-viec.md; chưa tạo mọi GitHub Issue/Project |
| PostgreSQL 16 | Chạy được | Cụm riêng cổng 55432; 2 migration, 8 bảng nghiệp vụ, 7 môn học |
| UC-02 backend | Đã triển khai và kiểm thử | Đăng ký PARENT/TUTOR, BCrypt 12, validation, trùng email, giao dịch |
| UC-02 frontend | Đã triển khai và kiểm thử | Form thật; không tự đăng nhập; thông báo lỗi/thành công |
| UC còn lại | Chưa triển khai | Hiện mới có yêu cầu và thiết kế |
| GitHub đợt này | Chờ kiểm tra CI | Xem nhánh và PR trên repository; chưa merge vào master |

## Bằng chứng kiểm tra ngày 19/10/2026

- Java 21, PostgreSQL 16.15, Maven 3.9.16. `mvn -B -ntp -f backend/pom.xml -Dmaven.repo.local=.m2 -Ppostgres-it clean verify`: **70 kiểm thử đơn vị/API + 14 kiểm thử PostgreSQL đạt**, không lỗi, không bỏ qua.
- Frontend: `npm run test:run`: **14 kiểm thử đạt**; `npm run build`: đạt.
- PostgreSQL: Flyway tạo schema mới và dữ liệu môn học; khởi động lại báo schema phiên bản 2 đã cập nhật, không chạy trùng migration.
- Integration test kiểm tra hai vai trò, chỉ lưu BCrypt, không trim mật khẩu, email khác hoa/thường, hai đăng ký đồng thời, rollback khi lưu hồ sơ gia sư lỗi, khóa ngoại và các ràng buộc PostgreSQL. Test tạo và dọn schema ngẫu nhiên riêng, không xóa dữ liệu phát triển.
- Kiểm thử API bổ sung: thiếu/null/rỗng, JSON sai hoặc sai kiểu, không cho đăng ký ADMIN, không lộ thông tin database khi lỗi mở giao dịch.
- Đã đăng ký phụ huynh và gia sư từ React qua Vite tới backend/PostgreSQL thật bằng dữ liệu giả. Gia sư có một hồ sơ DRAFT. [Ảnh đăng ký thành công](tai-nguyen/anh-chup/dang-ky-thanh-cong.png).
- `GET /api/system/status` qua Vite trả 200, mode postgres. Trường `database=NOT_CHECKED` chỉ nói API này kiểm tra ứng dụng; bằng chứng lưu DB đến từ đăng ký và integration test.

Báo cáo Maven được tạo tại `backend/target/surefire-reports` và `backend/target/failsafe-reports` sau khi chạy. Các file build/log không commit. [Ca kiểm thử](03-kiem-thu/ca-kiem-thu.md) ghi mức bao phủ; ca của UC khác vẫn chưa chạy.

## Giới hạn và bước tiếp theo

UC-02 chưa bao gồm đăng nhập, xác minh email, quên mật khẩu hoặc triển khai công cộng. Docker Compose đã chuẩn bị nhưng chưa chạy trên máy này; môi trường đã nghiệm thu dùng PostgreSQL cục bộ. Chưa kiểm tra trên trình duyệt Safari hoặc thiết bị điện thoại thật.

1. Hoàn tất PR/CI đợt UC-02; giữ lịch sử commit thật, không sửa lùi ngày.
2. UC-03: chốt cơ chế phiên, đăng nhập/đăng xuất và phân quyền nền tảng.
3. UC-04 tài khoản → UC-06 hồ sơ gia sư → UC-07 duyệt → UC-05 tìm kiếm.
4. UC-01/08–11 yêu cầu → UC-12–15 lớp/đánh giá → UC-16–17 quản trị.

Lịch học nâng cao và gợi ý gia sư ưu tiên sau luồng cốt lõi. Trước mỗi UC đọc điều kiện chấp nhận; sau UC cập nhật kiểm thử/API/backlog và báo cáo tuần.
