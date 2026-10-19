# Bộ tài liệu đồ án Gia Sư Kết Nối

Người thực hiện: Nguyễn Thị Khánh Linh. Ngày cập nhật nền tảng: 19/10/2026. Hạn hoàn thành dự kiến: 20/11/2026.

Tên thư mục/tệp tài liệu dùng tiếng Việt không dấu. Nội dung viết tiếng Việt có dấu. Giữ `README.md` theo quy ước repository và `PROJECT_PROGRESS.md` theo yêu cầu người thực hiện. Các tên kỹ thuật như API, React, PostgreSQL và mã UC được giữ nguyên để tra cứu.

| Tài liệu | Nơi đọc | Dùng khi nào |
|---|---|---|
| Đề cương | [de-cuong.md](01-yeu-cau/de-cuong.md) | Trao đổi phạm vi với giảng viên |
| Đặc tả yêu cầu | [dac-ta-yeu-cau.md](01-yeu-cau/dac-ta-yeu-cau.md) | Trước khi viết mỗi chức năng |
| Kiến trúc | [kien-truc.md](02-thiet-ke/kien-truc.md) | Hiểu các thành phần và luồng dữ liệu |
| Thiết kế dữ liệu | [co-so-du-lieu.md](02-thiet-ke/co-so-du-lieu.md) | Viết migration, entity và truy vấn |
| Thiết kế API | [thiet-ke-api.md](02-thiet-ke/thiet-ke-api.md) | Nối React và Java |
| Quyết định thiết kế | [quyet-dinh-thiet-ke.md](02-thiet-ke/quyet-dinh-thiet-ke.md) | Biết vì sao chọn giải pháp và giới hạn |
| Kế hoạch kiểm thử | [ke-hoach-kiem-thu.md](03-kiem-thu/ke-hoach-kiem-thu.md) | Chọn cách kiểm tra theo rủi ro |
| Ca kiểm thử | [ca-kiem-thu.md](03-kiem-thu/ca-kiem-thu.md) | Đối chiếu điều kiện chấp nhận |
| Hướng dẫn chạy | [huong-dan-chay.md](04-trien-khai/huong-dan-chay.md) | Chạy demo hoặc PostgreSQL thật |
| Danh sách công việc | [danh-sach-cong-viec.md](05-quan-ly/danh-sach-cong-viec.md) | Chọn việc tiếp theo |
| Quy trình Git | [quy-trinh-git.md](05-quan-ly/quy-trinh-git.md) | Issue, nhánh, commit, PR và tự rà soát |
| Báo cáo tuần | [bao-cao-tuan.md](05-quan-ly/bao-cao-tuan.md) | Báo cáo giảng viên mỗi tuần |
| Tiến độ thực tế | [PROJECT_PROGRESS.md](PROJECT_PROGRESS.md) | Xem việc đã làm và bằng chứng |

Tài liệu chỉ mô tả hệ thống đã triển khai khi có bằng chứng tương ứng ở tiến độ. Các UC chưa hoàn thành vẫn cần đặc tả trước, nhưng không được báo là đã có chức năng.

Mỗi thay đổi nghiệp vụ phải sửa điều kiện chấp nhận trước hoặc cùng commit với code, đồng thời bổ sung kiểm thử. Nếu phát hiện tài liệu mâu thuẫn, ghi quyết định ở tài liệu thiết kế và cập nhật các liên kết thay vì tự suy đoán trong code.

Tài liệu cũ nằm ở `luu-tru/2026-10-18/` để giữ lịch sử; không dùng phần MySQL trong đó làm hướng dẫn hiện tại. Báo cáo Word/PDF theo mẫu trường sẽ xuất riêng khi có mẫu, không duy trì nhiều bản “final” song song.
