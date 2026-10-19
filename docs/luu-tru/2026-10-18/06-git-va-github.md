# Git và GitHub cho đồ án

## Thư mục làm việc

Dùng thư mục gốc hiện tại chứa frontend, backend, database, docs và .git. Đây là một repository cho cả dự án. Không tạo thêm .git riêng bên trong frontend/backend và không sao chép thư mục node_modules hoặc .m2 sang GitHub.

Git lưu từng mốc thay đổi ở máy. GitHub nhận những commit đó khi push. Tạo repository GitHub không tự đưa các tệp đang nằm trên máy lên mạng.

## Thông tin tác giả

Đã cấu hình tác giả Nguyễn Thị Khánh Linh bằng email do người dùng cung cấp, chỉ trong repository này. Tài khoản GitHub dự kiến: KLINH160. Khi cấu hình lại, dùng email gắn với tài khoản GitHub hoặc địa chỉ noreply chính xác trong GitHub Settings → Emails. Không đoán địa chỉ noreply từ username.

Chỉ cấu hình trong repository này, giữ nguyên các dự án khác:

```powershell
git config --local user.name "TEN_TAC_GIA"
git config --local user.email "EMAIL_COMMIT"
```

Hai giá trị trên là chỗ điền thông tin thật, không chạy nguyên ví dụ. Không cần gửi mật khẩu hoặc token trong chat. Đăng nhập GitHub bằng giao diện xác thực được cung cấp khi kết nối.

## Các mốc đầu tiên dự kiến

Các tệp đã được viết trước khi tạo commit. Có thể tách chúng thành các commit theo nội dung với ngày tạo commit thực tế:

| Commit dự kiến | Nội dung |
|---|---|
| docs: define tutoring requirements and design | Phạm vi, use case, ERD, sơ đồ |
| feat: bootstrap Spring Boot API and MySQL migrations | Java, API trạng thái, kiểm thử, SQL |
| feat: bootstrap React frontend | React, CSS, Vite, package-lock |
| chore: add project guide and GitHub checks | README, hướng dẫn API/Git, CI, kết quả kiểm tra |

Ba commit đầu đã được tạo: `f3f976c` (tài liệu), `4fa67fc` (backend/SQL), `9d7dedd` (frontend). Tài liệu này thuộc commit hướng dẫn/CI tiếp theo. Mỗi commit dùng thời điểm thực tế, không backdate để mô phỏng các ngày làm việc trước đó. Chạy `git log --oneline` để xem lịch sử đầy đủ sau khi lưu.

## Mỗi khi hoàn thành một phần

1. Kiểm tra chức năng và đọc thay đổi.
2. Chọn đúng tệp của phần vừa hoàn thành.
3. Tạo commit có thông điệp mô tả kết quả.
4. Push sau khi repository đích và đăng nhập đã được xác nhận.

```powershell
git status
git diff
git add DUONG_DAN_TEP
git diff --cached
git commit -m "feat: add parent registration"
git push
```

Chỉ dùng thông điệp đăng ký phụ huynh sau khi đã thực sự làm chức năng đó. Không dùng `git add .` theo thói quen nếu chưa đọc danh sách tệp. Không force push hoặc sửa commit đã chia sẻ khi không có lý do rõ ràng.

## Repository GitHub

Tên đề xuất: gia-su-ket-noi, để private trong giai đoạn phát triển. Nếu repository đã có dữ liệu, phải kiểm tra lịch sử trước khi ghép; không ghi đè. Nếu tạo mới, tạo repository rỗng để tránh có hai commit khởi tạo độc lập.

Khi đã có URL thật và commit đầu tiên, nối origin rồi push nhánh đang làm việc. Không chạy với URL mẫu. Tên nhánh chính sẽ được chốt khi kết nối repository; không giả định mọi repo đều dùng main.

## Kiểm tra tự động

Workflow `.github/workflows/ci.yml` dự kiến chạy frontend build và backend test trên push/pull request. Chỉ xác nhận CI thành công sau khi GitHub thực sự chạy workflow; file cấu hình tồn tại không có nghĩa CI đã chạy. CI chưa kiểm tra MySQL thật.

## Báo cáo hằng tuần

```powershell
git log --since="7 days ago" --date=short --pretty=format:"%h %ad %s"
```

Dùng danh sách commit làm bằng chứng các thay đổi đã ghi nhận; bổ sung ảnh demo và kết quả kiểm thử. Thời điểm commit có thể khác thời điểm bắt đầu viết code.

Tham khảo: [GitHub hướng dẫn email commit](https://docs.github.com/en/account-and-profile/how-tos/email-preferences/setting-your-commit-email-address).
