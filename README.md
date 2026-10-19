# Gia Sư Kết Nối

Đồ án tốt nghiệp của **Nguyễn Thị Khánh Linh**. Một người thực hiện, báo cáo hằng tuần, mục tiêu hoàn thành khoảng **20/11/2026**.

**React + JavaScript · Java 21 + Spring Boot · PostgreSQL 16 · Flyway**

Repository riêng tư: [KLINH160/gia-su-ket-noi](https://github.com/KLINH160/gia-su-ket-noi).

## Đọc từ đâu?

- [Bản đồ tài liệu](docs/00-huong-dan.md) và [đặc tả UC-01–UC-17](docs/01-yeu-cau/dac-ta-yeu-cau.md).
- [Tiến độ, bằng chứng kiểm tra](docs/PROJECT_PROGRESS.md) và [thứ tự công việc](docs/05-quan-ly/danh-sach-cong-viec.md).
- [Hướng dẫn chạy chi tiết](docs/04-trien-khai/huong-dan-chay.md) và [API](docs/02-thiet-ke/thiet-ke-api.md).

Tên tài liệu dùng tiếng Việt không dấu; nội dung tiếng Việt có dấu. Tên kỹ thuật trong source giữ quy ước Java/React.

## Phạm vi hiện tại

UC-02: đăng ký phụ huynh/gia sư, lưu PostgreSQL, băm BCrypt, kiểm tra dữ liệu, xử lý email trùng và tạo hồ sơ gia sư DRAFT. Có form React và kiểm thử. Kết quả xác minh được ghi trong tiến độ.

**Chưa có đăng nhập, phân quyền nghiệp vụ, tìm gia sư hoặc gửi yêu cầu thật.** Đăng ký không tự đăng nhập; chưa phải bản vận hành công cộng.

## Cấu trúc

```text
backend/                  Java, migration, kiểm thử, Maven Wrapper
frontend/                 React: api, components, features, pages
database/                 SQL khởi tạo database
docs/
  00-huong-dan.md
  PROJECT_PROGRESS.md
  01-yeu-cau/             Đề cương, đặc tả 17 UC
  02-thiet-ke/            Kiến trúc, ERD, API, quyết định
  03-kiem-thu/            Kế hoạch, ca, kết quả kiểm thử
  04-trien-khai/          Hướng dẫn chạy
  05-quan-ly/             Công việc, Git, báo cáo tuần
  tai-nguyen/so-do/       Nguồn sơ đồ
  luu-tru/                Tài liệu cũ, không còn là thiết kế hiện tại
infra/                    Docker Compose tùy chọn
cong-cu/                  Script phát triển trên Windows
.github/                  CI và mẫu Issue/PR tiếng Việt
.env.example              Biến mẫu, không chứa bí mật thật
NHAT_KY_THAY_DOI.md        Thay đổi theo phiên bản
```

## Chạy trên máy hiện tại

Terminal 1, từ gốc dự án, đã cài PostgreSQL 16:

```powershell
.\cong-cu\postgres-cuc-bo.ps1
.\cong-cu\chay-backend.ps1
```

Terminal 2:

```powershell
cd frontend
npm ci
npm run dev
```

Mở http://127.0.0.1:5173. PostgreSQL riêng ở 55432, backend ở 8080; không sửa database hệ thống. Mật khẩu ngẫu nhiên nằm trong `.local`, bị Git bỏ qua. Profile `demo` không có database nên đăng ký trả 503; profile `postgres` lưu thật.

Các cách Docker/PostgreSQL tự cấu hình: [hướng dẫn chạy](docs/04-trien-khai/huong-dan-chay.md).

## Kiểm tra

```powershell
# Tu goc du an, PostgreSQL rieng dang chay
.\cong-cu\chay-backend.ps1 -KiemThu
```

```powershell
cd frontend
npm run test:run
npm run build
```

Backend có `mvnw.cmd test` để kiểm thử không DB, `mvnw.cmd -Ppostgres-it verify` khi có TEST_DB_URL/USERNAME/PASSWORD. Tích hợp tạo schema riêng; không trỏ vào database sản xuất.

## Cách làm một mình

Chọn việc có điều kiện chấp nhận → nhánh `codex/...` → code/kiểm thử → cập nhật tài liệu → commit → PR tự rà soát → CI đạt rồi merge. Nhánh chính hiện là `master`; giữ lịch sử đã có. Không yêu cầu người thứ hai duyệt chỉ để mô phỏng làm nhóm.

[Quy trình Git](docs/05-quan-ly/quy-trinh-git.md). Commit ghi ngày thực tế; báo cáo tuần dẫn commit, ảnh và kết quả kiểm thử.
