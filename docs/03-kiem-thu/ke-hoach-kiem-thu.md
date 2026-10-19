# Kế hoạch kiểm thử

Bản cơ sở: 19/10/2026. Phạm vi lấy từ [đặc tả UC-01 đến UC-17](../01-yeu-cau/dac-ta-yeu-cau.md). [Danh sách ca kiểm thử](ca-kiem-thu.md) liên kết từng tiêu chí; kết quả thực tế và mốc hoàn thành ghi trong [tiến độ](../PROJECT_PROGRESS.md).

## 1. Mục tiêu

Kiểm chứng hệ thống thực hiện đúng luồng kết nối, bảo vệ quyền truy cập, giữ dữ liệu nhất quán khi có lỗi hoặc thao tác đồng thời, và người dùng hiểu được kết quả trên giao diện. Có kết quả lặp lại được để báo cáo và demo.

Kiểm thử được tiến hành cùng mỗi chức năng. Không cần đợi đủ 17 UC mới kiểm thử, cũng không đánh dấu chức năng đạt chỉ vì build thành công.

## 2. Các mức kiểm thử

| Mức | Nội dung | Công cụ/cách thực hiện |
|---|---|---|
| Đơn vị | Chuẩn hóa, kiểm tra dữ liệu, xử lý nhánh dịch vụ | JUnit; mock phụ thuộc khi cần |
| Tích hợp backend | HTTP, validation, giao dịch, repository, migration và ràng buộc | Spring Boot Test và PostgreSQL 16 độc lập cho kiểm thử |
| Giao diện | Form, lỗi trường, nút đang gửi, thành công, lỗi mạng | Kiểm thử thành phần React hoặc thao tác trình duyệt có bước tái hiện |
| Xuyên suốt | Chuyển vai trò, toàn bộ luồng từ gia sư đăng ký đến đánh giá | API và giao diện chạy cùng PostgreSQL |
| Xác minh triển khai | DB rỗng áp dụng migration, backend khởi động, frontend build/gọi API | Quy trình CI và hướng dẫn chạy |

Cơ sở dữ liệu giả trong bộ nhớ không thay thế kiểm thử PostgreSQL cho index có điều kiện, giao dịch, trùng email hoặc khác biệt kiểu dữ liệu. Nếu môi trường chưa có PostgreSQL/Docker thì ghi “Chưa chạy — thiếu môi trường”, không đổi thành “Đạt”.

## 3. Phạm vi theo giai đoạn

1. **Nền tảng:** migration sạch, cấu hình mẫu, API trạng thái, build frontend/backend.
2. **UC-02:** Parent/Tutor, chuẩn hóa email, BCrypt, giới hạn UTF-8, lỗi dữ liệu, vai trò không hợp lệ, email trùng đồng thời, rollback khi tạo hồ sơ thất bại, giao diện đăng ký.
3. **UC-03/04/06/07:** xác thực, quyền sở hữu, hồ sơ và lịch sử duyệt, rút hồ sơ cạnh tranh quyết định duyệt.
4. **UC-05/01/08/09/10/11/12:** tìm kiếm công khai, quyền xem yêu cầu, chống yêu cầu trùng, giao dịch tạo lớp, chấp nhận/hủy/từ chối đồng thời.
5. **UC-13/14/15/16/17:** kết thúc lớp, đánh giá duy nhất, khóa tài khoản, quản trị và kiểm thử toàn luồng.

Lịch buổi học, gợi ý gia sư, thanh toán, video và chat chưa nằm trong kế hoạch kiểm thử cốt lõi vì chưa thuộc phạm vi triển khai này.

## 4. Dữ liệu và môi trường

- Cấu hình Java 21, React/JavaScript, PostgreSQL 16; dùng phiên bản dependency đã khóa trong dự án.
- PostgreSQL kiểm thử phải tách DB phát triển: dùng container dùng một lần hoặc database có tên riêng. Chỉ dọn dữ liệu trong DB kiểm thử đã xác minh.
- Dùng người dùng giả: phụ huynh A/B, gia sư A/B, quản trị; thêm tài khoản khóa và hồ sơ nháp/chờ/đã duyệt/cần bổ sung.
- Email kiểm thử dùng miền dành cho ví dụ, chẳng hạn `phuhuynh@example.com`; không gửi email thật.
- Không tái sử dụng mật khẩu cá nhân. Payload, screenshot và log minh chứng phải loại mật khẩu/token và thông tin riêng.
- Mỗi ca tự chuẩn bị dữ liệu hoặc có fixture được mô tả; không phụ thuộc thứ tự chạy ngẫu nhiên của ca khác.
- Ngày học gần biên dùng đồng hồ kiểm soát được và múi giờ `Asia/Ho_Chi_Minh`; không để test lỗi khi sang ngày mới.

## 5. Cách kiểm thử đồng thời

Các ca đồng thời phải tạo hai yêu cầu thực sự chạy song song với hai giao dịch/kết nối, đồng bộ thời điểm bắt đầu, đợi cả hai kết quả và truy vấn trạng thái cuối. Hai lần gọi lần lượt chỉ kiểm tra gửi lặp, chưa chứng minh an toàn đồng thời.

| Tranh chấp | Bất biến cần chứng minh |
|---|---|
| Hai đăng ký cùng email chuẩn hóa | Đúng một tài khoản; với gia sư đúng một hồ sơ; một phản hồi thành công và một lỗi trùng |
| Hai yêu cầu học trùng | Tối đa một `PENDING` theo khóa chống trùng |
| Hai lần chấp nhận | Đúng một lớp, cùng request_id |
| Chấp nhận và hủy/từ chối | Một trạng thái cuối; có lớp khi và chỉ khi chấp nhận thắng |
| Rút hồ sơ và duyệt | Chỉ một chuyển đổi từ trạng thái chờ thành công |
| Hoàn thành và hủy lớp | Đúng một trạng thái kết thúc cùng thông tin người/thời điểm tương ứng |
| Hai đánh giá cùng lớp | Đúng một đánh giá |

## 6. Bằng chứng và cách ghi kết quả

Mỗi lần chạy cần ghi ngày, môi trường, lệnh/các bước, commit hoặc nhánh, số ca đạt/lỗi/bỏ qua và nơi xem báo cáo. Không đưa số ca “dự kiến” vào cột “đã đạt”. Với kiểm thử trình duyệt, ghi các bước, dữ liệu giả, kết quả quan sát; ảnh chỉ là bằng chứng bổ sung.

Trạng thái sử dụng: **Chưa chạy**, **Đạt**, **Lỗi**, **Bị chặn**. “Đã viết test” và “Đã chạy test” là hai việc riêng. Các dòng vẫn “Chưa chạy” trong danh sách nghĩa là chưa có kết quả kiểm chứng được ghi tại đó.

Ghi lỗi theo mẫu: mã ca → điều kiện/dữ liệu → bước tái hiện → mong đợi → thực tế → mức ảnh hưởng → tham chiếu commit/Issue → kết quả sau sửa.

## 7. Điều kiện hoàn tất một chức năng

1. Tiêu chí chấp nhận của UC đã được triển khai và có ca kiểm chứng.
2. Luồng thành công, dữ liệu sai và sai quyền được kiểm tra; với UC công khai phải kiểm tra chống nâng quyền.
3. Giao dịch/ràng buộc DB có kiểm thử PostgreSQL tương ứng nếu liên quan.
4. Giao diện hiển thị được tải/lỗi/thành công; API không lộ dữ liệu nhạy cảm.
5. Kiểm thử liên quan và build đạt; lỗi quan trọng chưa giải quyết phải được nêu rõ.
6. Tài liệu API, tiến độ và backlog phản ánh đúng kết quả; thay đổi được review trước merge.

## 8. Kiểm thử trước demo cuối kỳ

Chạy luồng mới trên dữ liệu chuẩn bị sạch: gia sư đăng ký → soạn/gửi hồ sơ → quản trị duyệt → phụ huynh tìm → gửi yêu cầu → gia sư chấp nhận → xem lớp → phụ huynh hoàn thành → đánh giá. Sau đó thử ít nhất ba trường hợp lỗi: đăng ký trùng email; xem yêu cầu người khác; đánh giá lớp chưa hoàn thành. Đối chiếu DB và giao diện để tránh kết quả chỉ đúng ở màn hình.

Không đưa dữ liệu/mật khẩu thật vào bản demo. Phần chưa có như email xác minh, thanh toán hoặc lịch từng buổi phải được ghi ở hạn chế/hướng phát triển.
