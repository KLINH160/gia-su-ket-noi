# Hướng dẫn chạy và kiểm tra

## Chuẩn bị

Java 21, Node 24 và Maven; backend có Maven Wrapper để dùng khi không có Maven cài sẵn. PostgreSQL 16; Docker là lựa chọn thay thế, không bắt buộc. Máy hiện có PostgreSQL 16.15 và Docker chưa mở daemon ở lần kiểm tra đầu.

Mở terminal tại thư mục gốc chứa frontend/backend. Tất cả ví dụ PowerShell chạy từ gốc trừ khi có `cd`.

## Cách A — PostgreSQL riêng cho dự án trên Windows

```powershell
.\cong-cu\postgres-cuc-bo.ps1
.\cong-cu\chay-backend.ps1
```

Script dùng binary PostgreSQL đã cài, tạo cụm dữ liệu riêng trong `.local/postgres`, cổng 55432, chỉ lắng nghe 127.0.0.1. Không dùng database hệ thống ở cổng 5432. Tài khoản phát triển và mật khẩu ngẫu nhiên lưu ở `.local/postgres/ket-noi.json`, bị Git bỏ qua. Đây là tài khoản quản trị cụm phát triển, không dùng cấu hình này cho môi trường công cộng.

Nếu PostgreSQL không nằm ở vị trí mặc định:

```powershell
.\cong-cu\postgres-cuc-bo.ps1 -PostgresBin 'D:\PostgreSQL\16\bin'
```

Script dùng alias đường dẫn Windows 8.3 để tránh lỗi PostgreSQL đọc thư mục có dấu; nếu máy không có alias, dùng Docker hoặc clone dự án vào đường dẫn không dấu. Không tự di chuyển dữ liệu PostgreSQL khi server đang chạy.

Terminal thứ hai:

```powershell
cd frontend
npm ci
npm run dev
```

Mở http://127.0.0.1:5173. Vite chuyển `/api` về http://127.0.0.1:8080. Trang đăng ký dùng API thật; hồ sơ gia sư mới ở trạng thái nháp, chưa được duyệt.

## Cách B — PostgreSQL bằng Docker

```powershell
Copy-Item .env.example .env
# Sua POSTGRES_PASSWORD trong .env thanh mat khau rieng truoc khi chay.
docker compose --env-file .env -f infra/docker-compose.yml up -d
```

Compose mặc định công bố cổng 5433 trên loopback. `.env` chỉ được Docker Compose đọc, Spring Boot không tự đọc file đó. Đặt cùng thông tin cho terminal backend:

```powershell
$env:SPRING_PROFILES_ACTIVE='postgres'
$env:DB_URL='jdbc:postgresql://127.0.0.1:5433/tutor_connect'
$env:DB_USERNAME='tutor_app'
$credential=Get-Credential -UserName $env:DB_USERNAME -Message 'Nhap mat khau da dat trong .env'
$env:DB_PASSWORD=$credential.GetNetworkCredential().Password
cd backend
.\mvnw.cmd spring-boot:run
```

Giữ nguyên `.env` và volume giữa các lần chạy. Đổi mật khẩu trong `.env` không tự đổi mật khẩu của database đã được khởi tạo; không dùng `down -v` để sửa lỗi vì sẽ xóa dữ liệu.

## Cách C — Chế độ demo không database

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Profile mặc định demo chỉ dùng kiểm tra ứng dụng. Đăng ký trả 503 để thông báo chưa cấu hình database; không hiển thị giả đã tạo tài khoản. Nếu terminal còn SPRING_PROFILES_ACTIVE=postgres từ lần trước, xóa biến đó hoặc đặt demo rõ ràng.

## Kiểm thử

```powershell
# Don vi va API demo
cd backend
.\mvnw.cmd test
```

```powershell
# PostgreSQL that: chay tu goc, dung cum rieng o cach A
.\cong-cu\chay-backend.ps1 -KiemThu
```

Với database riêng do bạn cấp, đặt TEST_DB_URL, TEST_DB_USERNAME, TEST_DB_PASSWORD rồi chạy Maven `-Ppostgres-it clean verify`. Tài khoản kiểm thử phải có quyền tạo schema. Không trỏ kiểm thử vào database sản xuất; bài kiểm thử chỉ dọn schema ngẫu nhiên mà nó tạo, nhưng vẫn tạo/xóa đối tượng thật.

Sau khi chuyển nhánh từ MySQL sang PostgreSQL, chạy `clean` để bỏ migration cũ trong thư mục build. Dừng ứng dụng nếu đang chạy trực tiếp file JAR trước khi build lại, vì Windows khóa file đang sử dụng. Script chạy backend đặt thư mục socket Java riêng trong `.local/java-tmp` để xử lý lỗi `Unable to establish loopback connection` gặp trên máy hiện tại; không thay đổi cấu hình Java toàn máy. Tham khảo [thuộc tính thư mục socket của Java](https://docs.oracle.com/en/java/javase/16/core/networking-properties.html).

```powershell
cd frontend
npm run test:run
npm run build
```

## Dừng và chạy lại

Dừng terminal frontend/backend bằng Ctrl+C. Dừng cụm PostgreSQL riêng:

```powershell
.\cong-cu\postgres-cuc-bo.ps1 -Action stop
```

Lệnh stop không xóa dữ liệu. Dùng `-Action status` để kiểm tra. Không commit `.local`, `.env`, node_modules hoặc target.

## Trước khi triển khai công cộng

Đây là hướng dẫn chạy phát triển, chưa phải bản triển khai Internet đã nghiệm thu. Cần hoàn thành UC-03/phân quyền, giới hạn tốc độ đăng ký, HTTPS/reverse proxy, tài khoản DB ít quyền, backup/restore và quản lý secret của nơi hosting. Build frontend không tự cấu hình proxy production; `vite preview` không thay thế reverse proxy backend.

Tham khảo: [Docker Compose và env-file](https://docs.docker.com/compose/how-tos/environment-variables/variable-interpolation/).
