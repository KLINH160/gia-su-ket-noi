# Quy trình Git và GitHub khi làm đồ án một mình

Mục tiêu là lưu lịch sử dễ hiểu, có bằng chứng phát triển và một nhánh ổn định để demo. Không cần giả lập thành viên hay tạo nhiều tầng duyệt cho dự án cá nhân.

## 1. Nguyên tắc

- Mỗi thay đổi gắn với UC hoặc công việc trong backlog; ghi mục tiêu và điều kiện hoàn thành trước khi code.
- Dùng nhánh riêng theo nhóm việc, commit phần có ý nghĩa, tự xem lại diff và kiểm thử trước merge.
- Lịch sử phải phản ánh thay đổi thật. Không thay ngày commit để tạo cảm giác đã làm trước đó; không tạo commit rỗng chỉ để tăng số lượng.
- Tài liệu và code cùng repository, cập nhật cùng đợt khi nghiệp vụ hoặc API đổi.
- Không bắt buộc một người review thứ hai. Tự review bằng danh sách kiểm tra, CI và PR vẫn phù hợp đồ án cá nhân.

## 2. Nhánh và commit

Repository hiện dùng nhánh ổn định `master`; không đổi sang `main` chỉ để giống mẫu. Nhánh đang chuẩn hóa là `codex/postgres-project-foundation`. Các nhánh sau có thể đặt dễ đọc như:

```text
codex/dang-ky-tai-khoan
codex/dang-nhap-phan-quyen
codex/ho-so-gia-su
codex/yeu-cau-va-lop-hoc
```

Một commit nên chứa thay đổi liên quan có thể giải thích được. Ví dụ thông điệp:

```text
Tai lieu: chuan hoa dac ta 17 chuc nang
Du lieu: chuyen migration sang PostgreSQL
UC-02: them dang ky phu huynh va gia su
Kiem thu: bo sung truong hop trung email dong thoi
```

Có thể dùng tiếng Việt có dấu trong mô tả commit nếu công cụ hiển thị tốt. Không tách tùy tiện một thay đổi phụ thuộc thành nhiều commit làm nhánh không build được; không gộp sửa lỗi không liên quan vào commit của chức năng khác.

## 3. Một vòng làm việc

1. Đọc `PROJECT_PROGRESS.md`, backlog và UC liên quan; kiểm tra `git status` để tránh ghi đè việc đang làm.
2. Tạo/chọn Issue và nhánh phù hợp. Nếu đang có nhánh chứa cùng phần việc, tiếp tục nhánh đó thay vì mở nhánh trùng.
3. Triển khai một lát cắt đủ backend, frontend và kiểm thử; không tự thêm nghiệp vụ ngoài AC.
4. Chạy kiểm thử phù hợp và build; xem diff, loại file sinh ra/bí mật; cập nhật tài liệu và tiến độ.
5. Commit các file cụ thể đã xem lại; đẩy nhánh lên GitHub.
6. Mở PR, ghi thay đổi thực tế, cách kiểm thử và giới hạn; gắn Issue nếu đã tồn tại.
7. Tự review PR, kiểm tra CI; sửa lỗi nếu có. Chỉ merge khi phần việc đạt điều kiện và được phép merge theo phạm vi đang làm.
8. Sau merge, cập nhật tiến độ/Issue; chuẩn bị phần tiếp theo. Không force-push nhánh ổn định để chỉnh lịch sử báo cáo.

Các bước trên là quy trình sử dụng; tài liệu này không chứng minh đã tạo Issue/PR/Project hoặc đã merge. Chỉ ghi các URL đã được tạo và kiểm tra thực tế.

## 4. Mẫu mô tả PR

```markdown
UC và vấn đề: ...

Thay đổi:
- ...

Kiểm thử:
- Lệnh hoặc kịch bản đã chạy, kết quả thực tế: ...
- Phần chưa chạy và lý do: ...

Tài liệu/dữ liệu bị ảnh hưởng: ...
Giới hạn hoặc việc tiếp theo: ...
Issue liên quan: chỉ điền khi đã có Issue thực tế.
```

Mô tả kết quả cuối cùng của thay đổi để người đọc không cần biết toàn bộ cuộc trò chuyện. Không ghi “đạt” cho test bị bỏ qua hoặc chỉ dự định chạy.

## 5. Kiểm tra trước commit/PR

- File thay đổi đúng phạm vi; không vô tình đưa file cá nhân hoặc dữ liệu khác vào repository.
- Không có `.env`, mật khẩu, token, khóa API, dữ liệu thật hoặc bản dump database nhạy cảm.
- Không commit `node_modules/`, `target/`, thư mục cache dependency và log máy cá nhân.
- Commit `pom.xml`, file khóa dependency frontend, migration, cấu hình mẫu và mã nguồn.
- Kiểm tra `.env.example` chỉ có tên biến và giá trị minh họa; không đổi tên file bí mật thành “example” rồi commit.
- API và DB thống nhất; migration đã dùng trên DB có dữ liệu cần giữ phải được bổ sung bằng phiên bản mới.
- Xem `git diff` trước và `git diff --cached` sau khi stage; kiểm tra nội dung thay vì chỉ xem tên file.
- CI đạt đúng những kiểm tra được cấu hình; CI build đạt không tự chứng minh toàn bộ nghiệp vụ đạt.

## 6. Theo dõi bằng Issues và Projects

Đề xuất một bảng Project với bốn cột: **Chưa làm**, **Đang làm**, **Chờ kiểm tra**, **Hoàn thành**. Một Issue cho một UC hoặc phần nền tảng đủ nhỏ. Nếu UC lớn, chia các Issue con theo lát cắt kiểm chứng được và liên kết về UC gốc.

Không cần mở toàn bộ 17 Issue cùng lúc; có thể chuẩn bị backlog và tạo các việc của tuần gần nhất. Nếu chưa dùng Projects, file backlog hiện tại đủ để bắt đầu. Khi dùng GitHub làm nguồn trạng thái, file backlog giữ kế hoạch/phụ thuộc và đường dẫn, tránh nhập lại tiến độ vào nhiều nơi.

Với repo riêng tư, quyền mời giảng viên hoặc người khác là quyết định riêng. Không tự thêm cộng tác viên hoặc đổi repo sang công khai chỉ để báo cáo.

## 7. Báo cáo tuần từ lịch sử thật

Chọn các commit/PR đã hoàn thành trong tuần và minh chứng tương ứng. Giải thích ngắn: vấn đề nào được xử lý, chức năng chạy được tới đâu, kiểm thử gì, khó khăn gì và tuần tới làm gì. Không dùng số commit làm thước đo chất lượng duy nhất.

Mẫu báo cáo ở [bao-cao-tuan.md](bao-cao-tuan.md). Tên/email tác giả Git phải là người thực hiện đúng dự án; không giả danh người review hoặc tạo hoạt động GitHub không có thật.
