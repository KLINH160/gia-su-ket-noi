# Kết quả kiểm tra bộ khung

- `npm install`: cài thành công, npm audit tại thời điểm cài báo 0 lỗ hổng. Có package-lock.json để tái lập phiên bản.
- `npm run build`: thành công, Vite 7.3.7, 28 modules. Cần chạy ngoài sandbox Codex do EPERM khi truy cập realpath trong OneDrive.
- `mvn -o -q "-Dmaven.repo.local=../.m2" test`: thành công; 1 test, 0 failures, 0 errors, 0 skipped.
- Test khởi động Spring Boot với profile demo không có database, gọi API bằng MockMvc và kiểm tra phản hồi thông báo database DISABLED.
- Vite dev server khởi động thành công tại 127.0.0.1:5173, GET / trả HTTP 200. Máy chủ kiểm tra được dừng sau khi xác minh; chạy `npm run dev` để mở lại.

Chưa kiểm tra migration trên MySQL thật, chưa kiểm tra toàn bộ UI bằng trình duyệt tự động và chưa chạy kiểm thử end-to-end React đến Java qua HTTP. Máy có MySQL 8.0.46; script được thiết kế cho MySQL 8.4 và cần xác nhận trên phiên bản thực tế trước khi dùng. Chưa thay đổi server MySQL hiện hữu.

Bản này chỉ xác nhận bộ khung biên dịch và API demo hoạt động trong kiểm thử; không phải xác nhận luồng nghiệp vụ đã hoàn thành.
