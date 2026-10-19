# Kế hoạch và mẫu báo cáo tuần

Người thực hiện: Nguyễn Thị Khánh Linh. Đề tài: Website kết nối gia sư với phụ huynh. Thời hạn dự kiến: 20/11/2026. Ngày báo cáo cụ thể trong tuần theo lịch giảng viên; các khoảng dưới đây là mốc làm việc, chưa phải cuộc hẹn đã đặt.

## 1. Kế hoạch theo tuần

| Tuần | Khoảng thời gian | Mục tiêu | Minh chứng dự kiến |
|---|---|---|---|
| 1 | 19–24/10 | Chuẩn hóa tài liệu và 17 UC; kiến trúc/ERD; PostgreSQL; môi trường; UC-02 | Tài liệu, sơ đồ, migration, form đăng ký, kết quả kiểm thử và commit |
| 2 | 25–31/10 | UC-03/04/06/07: xác thực, tài khoản, hồ sơ và duyệt | Demo gia sư tạo/gửi hồ sơ, quản trị duyệt; kiểm thử quyền và trạng thái |
| 3 | 01–07/11 | UC-05/01/08/09/10/11/12: tìm, yêu cầu, tạo lớp | Demo yêu cầu được chấp nhận thành lớp; kiểm thử trùng và giao dịch |
| 4 | 08–14/11 | UC-13/14/15/16/17; kiểm thử toàn luồng | Demo kết thúc/đánh giá/quản trị; danh sách lỗi đã sửa/còn lại |
| 5 | 15–19/11 | Ổn định, báo cáo, slide, dữ liệu demo, tập bảo vệ | Bản chạy theo hướng dẫn, báo cáo theo mẫu trường và kịch bản demo |

Các chức năng mở rộng được cân nhắc sau khi cốt lõi đạt và có thời gian dự phòng; không làm chậm giai đoạn hoàn thiện báo cáo để thêm chức năng ngoài kế hoạch.

## 2. Mẫu dùng mỗi tuần

```markdown
Tuần: ...
Thời gian: ...
Mục tiêu đã đặt: ...

Đã hoàn thành:
- Công việc/UC, thay đổi cụ thể, bằng chứng (file/commit/PR/demo): ...

Kiểm thử đã chạy:
- Môi trường, lệnh hoặc kịch bản, số đạt/lỗi/bỏ qua và báo cáo: ...

Chưa hoàn thành:
- Việc còn lại, nguyên nhân và tác động tới mốc tiếp theo: ...

Quyết định thiết kế trong tuần:
- Quyết định, lý do, tài liệu được cập nhật: ...

Khó khăn/cần trao đổi với giảng viên:
- Vấn đề cụ thể và phương án đề xuất: ...

Kế hoạch tuần tới:
- Công việc theo thứ tự phụ thuộc và kết quả kiểm chứng dự kiến: ...
```

## 3. Bản nháp chuẩn bị cho tuần 1

**Mục tiêu:** thống nhất tài liệu bằng tiếng Việt, giữ số UC-01 đến UC-17, chuyển database sang PostgreSQL 16 và triển khai UC-02 sau khi nền tảng chạy được.

**Quyết định đã xác định trong phạm vi:** một người làm, một repository; React/JavaScript + Java/Spring Boot + PostgreSQL; ba vai trò; tách yêu cầu học và lớp; bản đầu chưa tự đăng nhập sau đăng ký; gia sư mới có hồ sơ nháp; không cho đăng ký quyền quản trị.

**Kết quả thực tế:** lấy từ [theo dõi tiến độ](../PROJECT_PROGRESS.md) và báo cáo kiểm thử tại thời điểm báo cáo. Không dùng danh sách mục tiêu trong đoạn trên để nói tất cả đã hoàn thành.

**Demo dự kiến:** mở giao diện đăng ký → tạo phụ huynh/gia sư → xem phản hồi thành công và bản ghi PostgreSQL → thử email trùng, vai trò sai và mật khẩu không hợp lệ. Chỉ demo những bước đã chạy kiểm tra.

**Kế hoạch tiếp:** chốt cơ chế xác thực, triển khai UC-03; sau đó UC-04, UC-06 và UC-07 theo backlog.

## 4. Cách trình bày khi báo cáo

Nêu sản phẩm chạy được trước, sau đó giải thích hai hoặc ba quyết định quan trọng. Ví dụ: “Em tạo tài khoản gia sư và hồ sơ nháp trong cùng giao dịch để không có tài khoản gia sư thiếu hồ sơ. Email được chuẩn hóa và có ràng buộc duy nhất ở PostgreSQL để ngăn đăng ký trùng, kể cả hai yêu cầu đồng thời.”

Chỉ dùng ví dụ ở trên như kết quả đã làm khi test tương ứng thực sự đạt. Nếu kiểm thử PostgreSQL bị chặn, nói rõ phần code có sẵn và điều kiện còn thiếu; không thay kết quả thực tế bằng ảnh mockup.

## 5. Kết quả thực hiện ngày 19/10/2026

- Hoàn thành bản cơ sở 17 UC/58 điều kiện chấp nhận, ERD PostgreSQL, API, backlog và bộ tài liệu tiếng Việt.
- Chạy React + Spring Boot + PostgreSQL; UC-02 tạo phụ huynh/gia sư thật, BCrypt, chống email trùng đồng thời, rollback nếu hồ sơ gia sư lỗi.
- 70 kiểm thử đơn vị/API, 14 kiểm thử PostgreSQL và 14 kiểm thử frontend đạt; build đạt. Chi tiết và giới hạn nằm trong PROJECT_PROGRESS.md.
- Đã đăng ký hai vai trò trên trình duyệt bằng dữ liệu giả và lưu ảnh thành công. Không dùng ảnh mockup làm bằng chứng chức năng.
- Khắc phục migration cũ còn trong target bằng clean build; dùng thư mục socket Java riêng cho môi trường Windows hiện tại.
- Tiếp theo: chốt PR/CI, rồi UC-03 đăng nhập/đăng xuất/phân quyền. Những UC còn lại chưa có code nghiệp vụ.
