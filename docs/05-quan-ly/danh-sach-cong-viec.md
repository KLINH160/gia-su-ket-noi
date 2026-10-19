# Danh sách công việc và thứ tự phát triển

Cập nhật bản cơ sở: 19/10/2026. Người thực hiện: Nguyễn Thị Khánh Linh. [Đặc tả](../01-yeu-cau/dac-ta-yeu-cau.md) quyết định nghiệp vụ; [tiến độ](../PROJECT_PROGRESS.md) ghi bằng chứng hoàn thành. Bảng này là backlog làm việc, không có nghĩa các Issue/Project đã được tạo trên GitHub.

## 1. Cách dùng cho người làm một mình

- Chỉ giữ một nhóm chức năng chính ở trạng thái “Đang làm”; mỗi nhóm có thể chia việc tài liệu/backend/frontend/kiểm thử.
- Trạng thái: Chưa làm → Đang làm → Chờ kiểm tra → Hoàn thành; dùng Bị chặn nếu thiếu điều kiện bên ngoài, ghi rõ nguyên nhân.
- Hoàn thành khi code, kiểm thử phù hợp, tài liệu và tự review đều xong; có commit/PR/báo cáo để kiểm chứng.
- Khi chuyển sang GitHub Issues/Projects, dùng Issue làm nơi theo dõi trạng thái công việc hằng ngày; bảng này giữ thứ tự, phạm vi và liên kết Issue, không duy trì thêm một bảng Excel độc lập.
- Phần mở rộng chỉ bắt đầu sau khi các lỗi cốt lõi quan trọng đã xử lý và còn thời gian trước mốc báo cáo cuối.

## 2. Nền tảng và tài liệu

| Mã | Công việc | Hoàn thành khi | Phụ thuộc | Trạng thái cơ sở |
|---|---|---|---|---|
| CV-01 | Chuẩn hóa tài liệu tiếng Việt, phân tích 17 UC | Đủ luồng/ngoại lệ/AC, chỉ rõ quyết định còn mở, có mục lục và đường dẫn đúng | Đọc repository | Hoàn thành |
| CV-02 | Kiến trúc và ERD PostgreSQL 16 | Sơ đồ, quan hệ, kiểu dữ liệu, ràng buộc và xử lý giao dịch thống nhất SRS | CV-01 | Hoàn thành |
| CV-03 | Migration và cấu hình môi trường | DB PostgreSQL mới migrate được; hướng dẫn chạy và cấu hình mẫu đúng | CV-02 | Hoàn thành |
| CV-04 | Backend/React/CI và kiểm tra nền tảng | Build đạt; backend gọi PostgreSQL; frontend gọi API; ghi kết quả thật | CV-03 | Chờ kiểm tra CI/PR |
| CV-05 | Quy trình Git cá nhân | Nhánh có mục tiêu, commit thật, tự review, PR/CI và theo dõi tuần | CV-01 | Chờ kiểm tra CI/PR |

Các trạng thái “Đang làm” ở bảng nền tảng phản ánh các phần song song của cùng đợt chuẩn hóa, không phải năm tính năng sản phẩm đang mở rộng đồng thời. Trạng thái chi tiết sau khi kiểm chứng được cập nhật vào tài liệu tiến độ.

## 3. Thứ tự chức năng cốt lõi

| Thứ tự | Mã | Công việc | Phụ thuộc | Điều kiện hoàn thành | Trạng thái |
|---|---|---|---|---|---|
| 1 | UC-02 | Đăng ký phụ huynh/gia sư | CV-01–04 | 6 AC đạt; PostgreSQL; BCrypt; form; kiểm thử lỗi/trùng/giao dịch | Chờ kiểm tra CI/PR |
| 2 | UC-03 | Đăng nhập và nền xác thực | UC-02, chốt cơ chế phiên | 3 AC đạt; backend xác định đúng danh tính/vai trò; khóa có hiệu lực | Chưa làm |
| 3 | UC-04 | Tài khoản cá nhân | UC-03 | 3 AC đạt; thông tin cá nhân, đổi mật khẩu, không nâng quyền | Chưa làm |
| 4 | UC-06 | Hồ sơ gia sư và gửi duyệt | UC-02/03 | 4 AC đạt; nháp/gửi/rút/sửa hồ sơ đã duyệt theo chính sách | Chưa làm |
| 5 | UC-07 | Quản trị duyệt hồ sơ | UC-03/06, cơ chế tạo ADMIN | 3 AC đạt; lịch sử/snapshot và giao dịch; xử lý xung đột | Chưa làm |
| 6 | UC-05 | Tìm và xem gia sư | UC-06/07 | 3 AC đạt; lọc/phân trang; chỉ hồ sơ công khai; không lộ dữ liệu riêng | Chưa làm |
| 7 | UC-01 | Gửi yêu cầu học | UC-03/05 | 4 AC đạt; ràng buộc dữ liệu, quyền, chống trùng đồng thời | Chưa làm |
| 8 | UC-08 | Danh sách/chi tiết yêu cầu | UC-01/03 | 3 AC đạt; mỗi bên xem đúng yêu cầu của mình | Chưa làm |
| 9 | UC-09 | Hủy yêu cầu chờ | UC-08 | 3 AC đạt; đúng chủ/trạng thái, xử lý cạnh tranh | Chưa làm |
| 10 | UC-10 | Chấp nhận và tạo lớp | UC-08, schema lớp | 4 AC đạt; giao dịch, một lớp/yêu cầu, gửi lặp an toàn | Chưa làm |
| 11 | UC-11 | Từ chối yêu cầu | UC-08/10 | 3 AC đạt; lý do và trạng thái, cạnh tranh với chấp nhận/hủy | Chưa làm |
| 12 | UC-12 | Danh sách/chi tiết lớp | UC-10 | 3 AC đạt; đúng quyền, dữ liệu thỏa thuận bất biến | Chưa làm |
| 13 | UC-13 | Hoàn thành lớp | UC-12 | 3 AC đạt; đúng phụ huynh, người/thời điểm kết thúc | Chưa làm |
| 14 | UC-14 | Hủy lớp | UC-12/13 | 3 AC đạt; hai bên, lý do, không tranh chấp trạng thái | Chưa làm |
| 15 | UC-15 | Đánh giá gia sư | UC-13/14 | 4 AC đạt; một đánh giá/lớp, đúng quyền/trạng thái, điểm đúng | Chưa làm |
| 16 | UC-16 | Quản trị tài khoản | UC-03, chính sách quản trị | 3 AC đạt; khóa/mở có hiệu lực, bảo toàn lịch sử | Chưa làm |
| 17 | UC-17 | Theo dõi kết nối | UC-08/12/15, UC-03 | 3 AC đạt; tra cứu toàn hệ thống đúng quyền, không sửa nghiệp vụ | Chưa làm |

UC-16 được làm đầy đủ ở giai đoạn sau nhưng UC-03 trở đi phải kiểm tra tài khoản `LOCKED` ngay từ nền xác thực; có thể chuẩn bị trạng thái khóa trong dữ liệu kiểm thử trước khi có giao diện quản trị.

## 4. Công việc cuối kỳ

| Mã | Công việc | Điều kiện bắt đầu/hoàn thành |
|---|---|---|
| CV-06 | Kiểm thử toàn luồng và sửa lỗi | Cốt lõi đủ; ghi kết quả XT-01 và các ca lỗi/quyền/đồng thời |
| CV-07 | Hướng dẫn triển khai và dữ liệu demo | Chạy lại trên DB mới; không dùng mật khẩu hoặc thông tin thật |
| CV-08 | Báo cáo trường và slide | Nội dung đối chiếu hệ thống thực tế; có hình/sơ đồ/kết quả kiểm thử |
| CV-09 | Tập bảo vệ và chốt bàn giao | Demo ổn định; giải thích được quyết định thiết kế; danh sách hạn chế rõ |

## 5. Phần mở rộng có điều kiện

| Mã | Chức năng | Giá trị | Điều kiện |
|---|---|---|---|
| MR-01 | Lịch buổi học, phát hiện trùng lịch | Nghiệp vụ thời gian và xử lý đồng thời | Cốt lõi đạt, còn thời gian; đặc tả riêng và migration mới |
| MR-02 | Gợi ý gia sư có giải thích | Tính điểm phù hợp theo môn/khối/lịch/phí/khu vực | Có tiêu chí/dữ liệu, quy tắc trọng số và cách kiểm chứng |
| MR-03 | Theo dõi tiến độ học | Gia sư ghi nội dung/nhận xét từng buổi | Sau MR-01; chốt quyền xem dữ liệu học sinh |
| MR-04 | Đăng nhu cầu và ứng tuyển | Mở thêm chiều kết nối | Cần phân tích riêng, không đưa ngầm vào UC-01 |

Các MR chưa cam kết trước 20/11; không tính là lỗi của phiên bản cốt lõi nếu chưa thực hiện.

## 6. Mẫu công việc đưa lên GitHub

**Tiêu đề:** `[UC-02] Đăng ký tài khoản phụ huynh và gia sư`.

**Mục tiêu:** đăng ký đúng vai trò, lưu PostgreSQL và hồ sơ nháp gia sư; không tự đăng nhập.

**Tham chiếu:** `UC-02-AC01` đến `UC-02-AC06`, các ca `DK-01` đến `DK-13`.

**Danh sách hoàn thành:** backend/API; validation và lỗi; giao dịch/hash; frontend; kiểm thử; cập nhật tài liệu; tự review; CI. Chỉ đánh dấu từng mục khi có bằng chứng.

**Ngoài phạm vi:** đăng nhập, xác minh email, quên mật khẩu, quản trị.

**Bằng chứng:** điền commit, PR, lệnh/kết quả test khi thực hiện; không tạo link giả hoặc số Issue chưa tồn tại.
