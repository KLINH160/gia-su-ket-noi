# Danh sách ca kiểm thử và truy vết

Bản thiết kế kiểm thử ngày 19/10/2026. Cột “Chưa chạy” không có nghĩa code bị lỗi; nó cho biết chưa ghi bằng chứng chạy ca đó. Người triển khai chỉ đổi sang “Đạt” khi đã thực hiện đúng các bước/mong đợi, ghi lệnh/báo cáo tương ứng trong [tiến độ](../PROJECT_PROGRESS.md). Mọi tài khoản dưới đây là dữ liệu giả.

Tham chiếu: [đặc tả yêu cầu](../01-yeu-cau/dac-ta-yeu-cau.md), [kế hoạch kiểm thử](ke-hoach-kiem-thu.md). Dải `AC01–AC03` trong một dòng bao gồm các tiêu chí tương ứng của UC ghi cùng dòng. Một ca có thể có nhiều bộ dữ liệu; cần ghi rõ bộ nào đã chạy.

## 1. Nền tảng

| Mã | Điều kiện và thao tác | Mong đợi | Trạng thái |
|---|---|---|---|
| MT-01 | Khởi tạo DB PostgreSQL 16 rỗng dành cho kiểm thử; chạy migration | Tạo đủ bảng/index/ràng buộc và dữ liệu danh mục; không lỗi cú pháp MySQL | Đạt; xem bằng chứng trong tiến độ |
| MT-02 | Khởi động lại trên DB đã migrate thành công | Không tạo bảng hoặc danh mục trùng; lịch sử migration hợp lệ | Đạt; xem bằng chứng trong tiến độ |
| MT-03 | Backend chạy với cấu hình PostgreSQL; gọi API trạng thái; frontend gọi API | Backend và frontend kết nối được theo cấu hình; không hardcode bí mật | Đạt; xem bằng chứng trong tiến độ |
| MT-04 | Cài dependency từ khóa phiên bản; build backend/frontend | Build thành công; log nêu lệnh và môi trường | Đạt; xem bằng chứng trong tiến độ |

## 2. UC-02 — Đăng ký tài khoản

| Mã | Tiêu chí | Điều kiện và thao tác | Mong đợi | Trạng thái |
|---|---|---|---|---|
| DK-01 | UC-02-AC01 | Email mới; gửi `fullName`, email, mật khẩu hợp lệ, role `PARENT` | HTTP 201; một tài khoản ACTIVE đúng thông tin, không có tutor_profile; response chỉ trường hợp đồng | Đạt; xem bằng chứng trong tiến độ |
| DK-02 | UC-02-AC02 | Email mới; gửi role `TUTOR` | HTTP 201; một tài khoản TUTOR ACTIVE và đúng một hồ sơ DRAFT liên kết, chưa xuất hiện công khai | Đạt; xem bằng chứng trong tiến độ |
| DK-03 | UC-02-AC02 | Trong môi trường kiểm thử, gây lỗi lưu hồ sơ sau bước lưu tài khoản gia sư | Toàn bộ giao dịch rollback; không còn tài khoản hoặc hồ sơ dở dang | Đạt; xem bằng chứng trong tiến độ |
| DK-04 | UC-02-AC03 | Gửi tên `  Nguyễn An  `, email `  AN@example.com  `; sau đó gửi `an@example.com` | Lần đầu lưu tên đã trim/email chữ thường; lần sau 409, không tăng số tài khoản | Đạt; xem bằng chứng trong tiến độ |
| DK-05 | UC-02-AC03 | Hai request đồng thời cùng email chuẩn hóa, dùng hai giao dịch | Một 201, một 409; DB đúng một tài khoản; nếu TUTOR đúng một hồ sơ | Đạt; xem bằng chứng trong tiến độ |
| DK-06 | UC-02-AC04 | Tên thiếu/rỗng/toàn khoảng trắng/1 ký tự/121 ký tự; email thiếu/sai định dạng/quá 254 | HTTP 400, `fieldErrors` đúng trường; không ghi DB | Đạt; xem bằng chứng trong tiến độ |
| DK-07 | UC-02-AC04 | Role thiếu, `ADMIN`, giá trị lạ; JSON sai hoặc trường không đúng kiểu | HTTP 400, không cấp quyền và không lưu tài khoản | Đạt; xem bằng chứng trong tiến độ |
| DK-08 | UC-02-AC04/05 | Mật khẩu 9 ký tự; 10 ký tự; 72 byte ASCII; 73 byte ASCII | Từ chối 9 ký tự/73 byte; chấp nhận biên hợp lệ, băm đúng | Đạt validation; hash biên riêng chưa kiểm thử, hash chuỗi có khoảng trắng đạt ở DK-10 |
| DK-09 | UC-02-AC04/05 | Mật khẩu Unicode: `á` lặp 36 lần (72 byte) và 37 lần (74 byte); mật khẩu hợp lệ có khoảng trắng hai đầu | Chấp nhận 72 byte, từ chối 74 byte; BCrypt xác minh đúng chuỗi có khoảng trắng nguyên trạng | Đạt validation; hash biên riêng chưa kiểm thử, hash chuỗi có khoảng trắng đạt ở DK-10 |
| DK-10 | UC-02-AC05 | Sau đăng ký truy vấn password_hash và kiểm tra BCrypt; đọc response/header/log | Hash không bằng mật khẩu, BCrypt.matches thành công; response/log không có hash/mật khẩu; không cấp token/phiên đăng nhập | Đạt; xem bằng chứng trong tiến độ |
| DK-11 | UC-02-AC06 | Trên form nhập xác nhận mật khẩu khác mật khẩu | Hiện lỗi cạnh trường; không gửi request đăng ký | Đạt; xem bằng chứng trong tiến độ |
| DK-12 | UC-02-AC06 | Điền form hợp lệ; giữ request đang tải và bấm gửi lần nữa; hoàn tất | Nút đang gửi bị khóa, không gửi lặp; thông báo thành công đúng vai trò; xóa mật khẩu khỏi form khi hoàn tất | Đạt; xem bằng chứng trong tiến độ |
| DK-13 | UC-02-AC06 | API trả lỗi 400/409; sau đó mô phỏng lỗi mạng | Lỗi trường/email trùng/mạng dễ hiểu; có thể sửa/thử lại; không lưu mật khẩu vào localStorage/sessionStorage/URL | Đạt; xem bằng chứng trong tiến độ |

Mật khẩu trong DK-08/09 chỉ là dữ liệu test để xác minh giới hạn kỹ thuật, không phải mật khẩu khuyến nghị cho người sử dụng.

## 3. Các UC còn lại

| Mã | Tiêu chí | Điều kiện và thao tác | Mong đợi | Trạng thái |
|---|---|---|---|---|
| YC-01 | UC-01-AC01 | Phụ huynh A/gia sư đã duyệt hoạt động; gửi dữ liệu hợp lệ | Một PENDING đúng hai bên, nội dung giữ nguyên, không lớp | Chưa chạy |
| YC-02 | UC-01-AC02 | Dữ liệu biên: thiếu mục tiêu/lịch, quá độ dài, giá ngoài khoảng/không chia 1.000, ngày quá khứ, môn/khối sai, OFFLINE thiếu khu vực | Mỗi bộ sai bị từ chối đúng trường; không lưu yêu cầu | Chưa chạy |
| YC-03 | UC-01-AC03 | Khách/gia sư giả phụ huynh; phụ huynh khóa; gia sư khóa/chưa duyệt; tự gắn parentId người khác | Không tạo yêu cầu trái quyền hoặc trái trạng thái | Chưa chạy |
| YC-04 | UC-01-AC04 | Gửi hai yêu cầu cùng khóa chống trùng, tuần tự rồi đồng thời | Tối đa một PENDING; báo trùng dễ hiểu | Chưa chạy |
| DN-01 | UC-03-AC01 | Email/mật khẩu đúng, tài khoản ACTIVE thuộc mỗi vai trò | Xác thực thành công và quyền đúng vai trò lưu tại backend | Chưa chạy |
| DN-02 | UC-03-AC02/03 | Sai email/mật khẩu, tài khoản khóa; tự gửi role ADMIN | Không tạo phiên/token; lỗi không trả hash, không nâng quyền | Chưa chạy |
| CN-01 | UC-04-AC01 | Người dùng sửa thông tin của mình, sau đó sửa ID thành người khác | Chỉ thông tin của mình cập nhật; dữ liệu người khác giữ nguyên | Chưa chạy |
| CN-02 | UC-04-AC02 | Đổi mật khẩu với mật khẩu cũ đúng/sai và mật khẩu mới hợp lệ/không hợp lệ | Chỉ trường hợp hợp lệ lưu hash mới; kiểm tra xác thực mật khẩu mới/cũ | Chưa chạy |
| CN-03 | UC-04-AC03 | Gửi kèm role/status/email vào cập nhật cá nhân | Không thay quyền, trạng thái hoặc email | Chưa chạy |
| TK-01 | UC-05-AC01 | Có hồ sơ đủ 4 trạng thái và tài khoản khóa; tìm và mở bằng ID | Chỉ APPROVED thuộc ACTIVE xem công khai được | Chưa chạy |
| TK-02 | UC-05-AC02 | Gia sư dạy Toán lớp 9 và Lý lớp 12; lọc Toán lớp 12; thử giá/hình thức/khu vực, phân trang và rỗng | Không suy ra sai tổ hợp; kết quả và trang đúng bộ lọc | Chưa chạy |
| TK-03 | UC-05-AC03 | Xem response và giao diện hồ sơ có/chưa có đánh giá | Không lộ liên hệ/học sinh/yêu cầu; chưa có đánh giá không hiển thị 0 sao | Chưa chạy |
| HS-01 | UC-06-AC01 | Gia sư sửa/lưu nháp của mình, thử ID gia sư khác | Nháp chưa đủ được lưu; không sửa hồ sơ khác | Chưa chạy |
| HS-02 | UC-06-AC02 | Gửi hồ sơ thiếu môn/hình thức/khu vực; môn-khối trùng/giá sai; sau đó hồ sơ đủ | Sai không chuyển chờ; đủ chuyển PENDING_REVIEW | Chưa chạy |
| HS-03 | UC-06-AC03 | Sửa hồ sơ APPROVED; thử sửa trực tiếp PENDING_REVIEW | Có cảnh báo, APPROVED chuyển DRAFT và ẩn; PENDING không sửa trực tiếp | Chưa chạy |
| HS-04 | UC-06-AC04; UC-07-AC03 | Rút hồ sơ đồng thời quản trị duyệt; so thỏa thuận lớp cũ | Một chuyển đổi thắng, lịch sử nhất quán; yêu cầu/lớp cũ không đổi | Chưa chạy |
| DU-01 | UC-07-AC01 | Quản trị duyệt hồ sơ PENDING; thử bằng vai trò khác và trạng thái không chờ | Chỉ quản trị xử lý đúng hồ sơ đang chờ; APPROVED được công khai | Chưa chạy |
| DU-02 | UC-07-AC02 | Yêu cầu bổ sung với lý do rỗng rồi hợp lệ | Rỗng bị chặn; hợp lệ lưu NEEDS_CHANGES và lý do đọc được | Chưa chạy |
| DU-03 | UC-07-AC03 | Kiểm tra lịch sử có người/thời điểm/snapshot; gây lỗi lưu lịch sử | Lưu đầy đủ cùng quyết định; lỗi rollback cả hai | Chưa chạy |
| XY-01 | UC-08-AC01/02 | Hai phụ huynh/hai gia sư có yêu cầu; lọc, phân trang, mở chi tiết | Mỗi người chỉ thấy yêu cầu liên quan, trạng thái/lý do đúng | Chưa chạy |
| XY-02 | UC-08-AC03 | Đổi ID URL sang yêu cầu người khác, kiểm tra nút theo trạng thái | Không lộ dữ liệu; nút và kiểm tra backend đúng quyền/trạng thái | Chưa chạy |
| HY-01 | UC-09-AC01/02 | Chủ yêu cầu hủy PENDING; người khác hủy; hủy ACCEPTED | Chỉ chủ hủy PENDING thành công, lưu thời điểm; còn lại từ chối | Chưa chạy |
| HY-02 | UC-09-AC03; UC-10-AC03 | Phụ huynh hủy đồng thời gia sư chấp nhận | Nếu hủy thắng không lớp; nếu chấp nhận thắng đúng một lớp | Chưa chạy |
| CH-01 | UC-10-AC01/04 | Chấp nhận hợp lệ; trước đó đổi giá niêm yết; sau đó kiểm tra DB | Một lớp ACTIVE và yêu cầu ACCEPTED; giá/nội dung lấy yêu cầu gốc | Chưa chạy |
| CH-02 | UC-10-AC02 | Gia sư khác; tài khoản một bên khóa; hồ sơ chưa duyệt; yêu cầu REJECTED/CANCELLED | Không tạo lớp hoặc chuyển sai trạng thái | Chưa chạy |
| CH-03 | UC-10-AC03 | Gọi chấp nhận lặp tuần tự, sau đó chạy tranh chấp hai chấp nhận | Trả cùng lớp cho gia sư có quyền; không có hai lớp | Chưa chạy |
| CH-04 | UC-10-AC04 | Gây lỗi tạo lớp sau bước đổi trạng thái yêu cầu | Giao dịch rollback; yêu cầu vẫn PENDING, không có lớp | Chưa chạy |
| TC-01 | UC-11-AC01/02 | Từ chối bằng chủ gia sư với lý do hợp lệ/rỗng; thử người khác | Hợp lệ lưu REJECTED/lý do/thời điểm, không lớp; sai bị chặn | Chưa chạy |
| TC-02 | UC-11-AC03; UC-10-AC03 | Từ chối đồng thời chấp nhận hoặc phụ huynh hủy | Một trạng thái cuối, lớp chỉ tồn tại nếu chấp nhận thắng | Chưa chạy |
| XL-01 | UC-12-AC01/03 | Hai bên xem/lọc/phân trang lớp của mình; người ngoài sửa URL | Dữ liệu/trạng thái/kết thúc đúng; người ngoài không truy cập | Chưa chạy |
| XL-02 | UC-12-AC02 | Gia sư thay hồ sơ/giá sau khi lớp đã được tạo | Lớp vẫn đọc nội dung và giá yêu cầu gốc | Chưa chạy |
| HT-01 | UC-13-AC01/02 | Phụ huynh chủ lớp ACTIVE hoàn thành; gia sư/người khác thử; thử lớp đã kết thúc | Chỉ chủ phụ huynh thực hiện đúng trạng thái, lưu người/thời điểm | Chưa chạy |
| HT-02 | UC-13-AC03; UC-14-AC03 | Hoàn thành và hủy lớp đồng thời | Một trạng thái kết thúc, thông tin kết thúc đúng; không tự đánh giá | Chưa chạy |
| HL-01 | UC-14-AC01/02 | Mỗi bên hủy lớp ACTIVE với lý do; thử rỗng/người ngoài/lớp kết thúc | Hợp lệ lưu lý do/người/thời điểm; sai bị từ chối | Chưa chạy |
| HL-02 | UC-14-AC03 | Sau hủy, xem yêu cầu gốc, thử đánh giá và mở lại | Yêu cầu vẫn ACCEPTED; không đánh giá/mở lại lớp | Chưa chạy |
| DG-01 | UC-15-AC01/02 | Chủ phụ huynh đánh giá COMPLETED; thử người khác, ACTIVE/CANCELLED, 0/6/1.5 sao, nhận xét 1.001 ký tự | Chỉ dữ liệu đúng và chủ lớp hoàn thành được tạo đánh giá | Chưa chạy |
| DG-02 | UC-15-AC03 | Gửi đánh giá lặp và hai lần đồng thời; thử sửa/xóa | Tối đa một đánh giá; không có sửa/xóa trong bản đầu | Chưa chạy |
| DG-03 | UC-15-AC04 | Gia sư có các điểm mẫu 3 và 5; gia sư chưa có điểm; xem response công khai | Trung bình 4; trường hợp chưa có đúng thông báo; không lộ dữ liệu riêng | Chưa chạy |
| QT-01 | UC-16-AC01 | Quản trị xem/khóa/mở; thử bằng PARENT/TUTOR; kiểm tra response | Chỉ ADMIN truy cập quản trị; không trả hash/mật khẩu | Chưa chạy |
| QT-02 | UC-16-AC02 | Tài khoản đang có phiên bị khóa; gọi API xác thực và tìm gia sư | Từ chối hành động tiếp theo; gia sư khóa bị ẩn | Chưa chạy |
| QT-03 | UC-16-AC03 | Khóa rồi mở tài khoản đang có lớp/yêu cầu; đối chiếu lịch sử | Dữ liệu và vai trò không đổi, lớp không tự hủy | Chưa chạy |
| TD-01 | UC-17-AC01/02 | Quản trị lọc yêu cầu/lớp của nhiều người, mở liên kết và phân trang; thử vai trò khác | ADMIN xem đúng liên kết và lọc; vai trò khác bị chặn; yêu cầu từ chối không lớp | Chưa chạy |
| TD-02 | UC-17-AC03 | Đọc chi tiết theo dõi, so trạng thái trước/sau, xem response | Không sửa nghiệp vụ hoặc cấp thao tác thay người dùng; không lộ hash/mật khẩu | Chưa chạy |

## 4. Kiểm thử xuyên suốt và phi chức năng

| Mã | Phạm vi | Thao tác và mong đợi | Trạng thái |
|---|---|---|---|
| XT-01 | UC-02 → UC-06 → UC-07 → UC-05 → UC-01 → UC-10 → UC-12 → UC-13 → UC-15 | Chuyển vai trò đúng tài khoản; hoàn thành luồng từ đăng ký đến đánh giá và kiểm tra dữ liệu PostgreSQL | Chưa chạy |
| XT-02 | YC-01/02/08 | Kiểm tra quyền, response, log và nội dung Git; không có mật khẩu/hash/token bí mật; lỗi không lộ SQL/stacktrace | Chưa chạy |
| XT-03 | YC-04 | Form trên màn hình nhỏ, bàn phím Tab/Enter; đọc được nhãn/lỗi; trạng thái tải/rỗng/mạng/thành công rõ | Chưa chạy |
| XT-04 | YC-05/07 | Chạy theo hướng dẫn trên DB mới dành riêng cho test; xác nhận migration tái tạo được và không tác động DB phát triển | Chưa chạy |

## 5. Ghi kết quả thực tế

Mẫu: `Ngày | mã ca | nhánh/commit | môi trường | lệnh hoặc bước | kết quả | báo cáo/lỗi`. Bằng chứng kiểm thử tự động nên là tên lớp test và báo cáo công cụ; ca thủ công ghi thao tác quan sát. Với test bị skip do thiếu Docker/PostgreSQL, ghi “Bị chặn” cùng nguyên nhân, không cộng vào số đạt.
