# Gia Sư Kết Nối

Đồ án website kết nối gia sư và phụ huynh. Bộ khung hiện có React + JavaScript, Java 21 + Spring Boot, migration MySQL 8.4 và API kiểm tra kết nối.

**Chưa có đăng nhập, phân quyền, hồ sơ gia sư hoặc xử lý yêu cầu thật.** API duy nhất hiện tại là kiểm tra trạng thái. Chạy cục bộ; chưa triển khai lên Internet.

## Cấu trúc

```text
frontend/     React, Vite, trang kiểm tra kết nối
backend/      Spring Boot, kiểm thử, Flyway migration
database/     Script tạo database rỗng
docs/         Phạm vi, use case, ERD và thiết kế API
.github/      Kiểm thử backend và build frontend trên GitHub Actions
```

## Chạy nhanh chưa cần MySQL

Yêu cầu: JDK 21, Maven, Node >=22.12. Máy phát triển hiện đã có Java 21 và Node 24. Dùng hai terminal trong thư mục dự án.

Terminal 1:

```powershell
cd backend
mvn spring-boot:run
```

Terminal 2:

```powershell
cd frontend
npm ci
npm run dev
```

Mở http://localhost:5173. Frontend gọi `/api/system/status` qua Vite proxy tới `127.0.0.1:8080`. Chế độ mặc định `demo` không kết nối database và không lưu dữ liệu. Nút kiểm tra lại thực sự gọi API.

Nếu máy dùng nhầm Java qua javapath, đặt JAVA_HOME cho terminal đang chạy Maven:

```powershell
$env:JAVA_HOME='C:\Program Files\Java\jdk-21'
```

## Bật MySQL

1. Chuẩn bị MySQL 8.4 và một tài khoản phát triển có quyền trên database `tutor_connect`.
2. Chạy `database/create-database.sql` trong MySQL Workbench bằng tài khoản quản trị database.
3. Trong terminal backend, đặt các biến môi trường rồi chạy:

```powershell
$env:SPRING_PROFILES_ACTIVE='mysql'
$env:DB_USERNAME='ten_tai_khoan_mysql'
$dbCredential=Get-Credential -UserName $env:DB_USERNAME -Message 'Nhập mật khẩu MySQL'
$env:DB_PASSWORD=$dbCredential.GetNetworkCredential().Password
mvn spring-boot:run
```

Địa chỉ mặc định là localhost:3306/tutor_connect. Có thể ghi đè bằng DB_URL. Spring Boot không tự đọc file `.env`; hướng dẫn này dùng biến môi trường PowerShell. Không đưa mật khẩu vào Git.

Flyway chạy V1 tạo 8 bảng nghiệp vụ và V2 thêm 7 môn học; Flyway có thêm bảng lịch sử kỹ thuật riêng. Hibernate dùng `validate`, không tự sửa cấu trúc bảng. Chưa có entity nghiệp vụ ở bước này, nên chưa có kiểm tra đối chiếu mapping entity với 8 bảng. Không sửa migration đã áp dụng; tạo migration mới khi thay đổi schema.

## Kiểm thử và build

```powershell
cd backend
mvn test
mvn package
```

```powershell
cd frontend
npm run build
```

Trong môi trường Codex, thư viện được tải vào `.m2` ở gốc dự án. Để dùng lại cache này, chạy Maven từ backend với `"-Dmaven.repo.local=../.m2"`. Có thể dùng cache Maven mặc định trên máy khi tự chạy.

Kiểm thử cấu hình Mockito dùng subclass mock maker để tránh cơ chế tự gắn Java agent bị lỗi trên JDK/Windows hiện tại. Cấu hình này không hỗ trợ mock phương thức static hoặc lớp final; các bài kiểm thử cần chúng phải cấu hình agent riêng.

Frontend build tạo `frontend/dist`. Vite proxy chỉ có ở chế độ phát triển; khi triển khai phải cấu hình reverse proxy `/api` đến Spring Boot, không coi `vite preview` là máy chủ đầy đủ cho hệ thống.

## Giới hạn và bước tiếp theo

Quy trình lưu lịch sử: [Git và GitHub cho đồ án](docs/06-git-va-github.md). Các commit nên tương ứng từng phần đã thực hiện và kiểm tra. Workflow CI chạy khi push/pull request; chưa có kết quả GitHub Actions cho đến khi repository được kết nối và đẩy lên.

- API trạng thái xác nhận backend hoạt động, không phải phép kiểm tra database đang khỏe.
- Schema đặt ràng buộc cột, khóa và chống trùng; quyền người dùng, bất biến nội dung yêu cầu, chuyển trạng thái và giao dịch tạo lớp phải được hiện thực ở service Java.
- Chưa cài hoặc tự sửa MySQL trên máy người dùng. Cần chạy migration trên MySQL thật để xác nhận tương thích.
- Bước triển khai tiếp theo: đăng ký, đăng nhập, phân quyền; rồi hồ sơ gia sư và duyệt hồ sơ.

Tham khảo cấu hình: [Spring Boot 3.5 requirements](https://docs.spring.io/spring-boot/3.5/system-requirements.html), [Vite guide](https://vite.dev/guide/).
