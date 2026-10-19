# Đặc tả yêu cầu phần mềm

Bản cơ sở 1.0 — 19/10/2026. Tài liệu kế thừa phạm vi và quy tắc đã thống nhất, giữ nguyên UC-01 đến UC-17. Đây là chuẩn để triển khai và kiểm thử, không phải tuyên bố tất cả chức năng đã tồn tại.

Đối chiếu [đề cương](de-cuong.md), [backlog](../05-quan-ly/danh-sach-cong-viec.md), [ca kiểm thử](../03-kiem-thu/ca-kiem-thu.md) và [tiến độ](../PROJECT_PROGRESS.md). Mã `UC-XX-ACYY` là một tiêu chí chấp nhận có thể kiểm chứng. Mã kỹ thuật giữ nguyên để đối chiếu code; phần diễn giải bằng tiếng Việt.

## 1. Quy ước và phạm vi

| Thuật ngữ | Nghĩa |
|---|---|
| Phụ huynh / `PARENT` | Người đại diện học sinh gửi nhu cầu và quản lý lớp |
| Gia sư / `TUTOR` | Người giới thiệu năng lực và nhận yêu cầu |
| Quản trị viên / `ADMIN` | Người duyệt hồ sơ và quản lý hoạt động |
| Yêu cầu học | Đề nghị bất biến về nội dung; có thể bị hủy hoặc từ chối |
| Lớp học | Quan hệ học tập hình thành khi yêu cầu được chấp nhận |
| Đã duyệt | Hồ sơ đáp ứng quy trình duyệt thông tin; không khẳng định bằng cấp được xác minh |
| Hoàn thành | Xác nhận của phụ huynh; không phải kiểm chứng độc lập việc dạy học |

Mỗi tài khoản có một vai trò. Học sinh chưa có tài khoản riêng. Bản cốt lõi không thu tiền, tổ chức video, chat thời gian thực hoặc tự kiểm tra trùng lịch. Không tự thêm các chức năng này khi triển khai một UC hiện có.

## 2. Quy tắc chung

| Mã | Quy tắc |
|---|---|
| QT-01 | Kiểm tra đăng nhập, vai trò, trạng thái tài khoản và quyền với từng tài nguyên ở backend. ID do client gửi không quyết định tác giả. |
| QT-02 | Tài khoản bị khóa không được thao tác có xác thực; lịch sử vẫn giữ nguyên, không tự hủy lớp. |
| QT-03 | Chỉ hồ sơ `APPROVED` của tài khoản `ACTIVE` xuất hiện công khai. Không công khai email, điện thoại, tên học sinh hoặc nhu cầu riêng. |
| QT-04 | Sửa hồ sơ đã duyệt đưa về `DRAFT`, ẩn khỏi tìm kiếm và yêu cầu duyệt lại. Đang chờ duyệt không sửa trực tiếp; phải rút về nháp. |
| QT-05 | Yêu cầu đã gửi không sửa nội dung. Lớp đọc thỏa thuận từ yêu cầu gốc; giá hồ sơ đổi không làm đổi học phí đã chấp nhận. |
| QT-06 | Một yêu cầu chỉ có một lớp; chấp nhận và tạo lớp là một giao dịch. Một lớp chỉ có một đánh giá. |
| QT-07 | Chuyển trạng thái phải kiểm tra trạng thái hiện tại hoặc phiên bản. Chỉ một trong các thao tác xung đột được thành công. |
| QT-08 | Tạm chống trùng yêu cầu `PENDING` theo phụ huynh, gia sư, tên học sinh chuẩn hóa và môn. Chuẩn hóa tên bằng trim, gom khoảng trắng, Unicode và chữ thường, giữ dấu. Tên không phải định danh; hai học sinh trùng tên là hạn chế đã biết. |
| QT-09 | Lưu thời điểm sự kiện theo UTC; ngày học được hiểu theo `Asia/Ho_Chi_Minh`. Học phí nguyên đồng/giờ. |
| QT-10 | Quyền quản trị không được cấp qua đăng ký công khai. Cách tạo quản trị viên ban đầu phải được đặc tả khi triển khai quản trị. |

| Đối tượng | Trạng thái và chuyển đổi |
|---|---|
| Tài khoản | `ACTIVE` ↔ `LOCKED` qua quản trị |
| Hồ sơ | `DRAFT` → `PENDING_REVIEW` → `APPROVED` hoặc `NEEDS_CHANGES`; cần bổ sung sửa/gửi lại; đã duyệt sửa về nháp; chờ duyệt có thể rút về nháp |
| Yêu cầu | `PENDING` → `ACCEPTED`, `REJECTED` hoặc `CANCELLED`; không mở lại |
| Lớp | `ACTIVE` → `COMPLETED` hoặc `CANCELLED`; không mở lại |

## 3. Danh mục 17 chức năng

| Mã | Chức năng | Tác nhân |
|---|---|---|
| UC-01 | Gửi yêu cầu học | Phụ huynh |
| UC-02 | Đăng ký tài khoản | Khách |
| UC-03 | Đăng nhập | Khách |
| UC-04 | Quản lý tài khoản cá nhân | Mọi vai trò đã đăng nhập |
| UC-05 | Tìm và xem gia sư | Khách và người đã đăng nhập |
| UC-06 | Soạn hồ sơ và gửi duyệt | Gia sư |
| UC-07 | Duyệt hồ sơ | Quản trị viên |
| UC-08 | Xem yêu cầu học | Phụ huynh, gia sư |
| UC-09 | Hủy yêu cầu đang chờ | Phụ huynh |
| UC-10 | Chấp nhận yêu cầu | Gia sư |
| UC-11 | Từ chối yêu cầu | Gia sư |
| UC-12 | Xem lớp học | Phụ huynh, gia sư |
| UC-13 | Hoàn thành lớp | Phụ huynh |
| UC-14 | Hủy lớp | Phụ huynh, gia sư |
| UC-15 | Đánh giá gia sư | Phụ huynh |
| UC-16 | Quản lý tài khoản | Quản trị viên |
| UC-17 | Theo dõi kết nối | Quản trị viên |

## 4. Đặc tả từng chức năng

### UC-01 — Gửi yêu cầu học

**Tác nhân:** phụ huynh. **Kích hoạt:** bấm gửi yêu cầu trên hồ sơ gia sư.

**Tiền điều kiện:** phụ huynh đăng nhập và hoạt động; gia sư hoạt động, hồ sơ được duyệt; môn/khối lớp và hình thức phù hợp hồ sơ.

**Luồng chính:** (1) mở biểu mẫu; (2) nhập nhu cầu; (3) frontend kiểm tra và gửi; (4) backend xác định phụ huynh từ phiên đăng nhập, kiểm tra quyền, dữ liệu, trạng thái và trùng; (5) lưu `PENDING`; (6) trả xác nhận và đường xem yêu cầu.

**Dữ liệu:** tên học sinh trim 1–60 ký tự; khối lớp 1–12 thuộc môn/khối gia sư nhận dạy; mục tiêu bắt buộc tối đa 500; hình thức `ONLINE`/`OFFLINE`; khu vực bắt buộc nếu trực tiếp, tối đa 120; lịch bắt buộc tối đa 200; học phí 10.000–5.000.000 đồng/giờ, chia hết 1.000; ngày bắt đầu không quá khứ; ghi chú tùy chọn tối đa 500. Giới hạn học phí là quy tắc sản phẩm, không phải khảo sát thị trường.

**Ngoại lệ:** chưa đăng nhập chuyển đến đăng nhập và trở lại hồ sơ; sai quyền/trạng thái từ chối; thiếu/sai dữ liệu báo cạnh trường; trùng hướng đến yêu cầu hiện có; lỗi mạng giữ dữ liệu để thử lại. Khóa nút khi gửi không thay thế chống trùng ở backend.

**Hậu điều kiện:** có một yêu cầu hợp lệ gắn đúng hai bên; chưa tạo lớp; thất bại không có bản ghi dở dang.

- **UC-01-AC01:** dữ liệu hợp lệ tạo `PENDING`, giữ nguyên nội dung gửi và không tạo lớp.
- **UC-01-AC02:** dữ liệu thiếu, sai giới hạn, ngày quá khứ hoặc môn/khối/hình thức không hỗ trợ bị từ chối.
- **UC-01-AC03:** chỉ phụ huynh hoạt động gửi được tới gia sư hoạt động và đã duyệt; không giả mạo `parentId`.
- **UC-01-AC04:** hai lần gửi trùng kể cả đồng thời không sinh hai yêu cầu `PENDING`.

### UC-02 — Đăng ký tài khoản

**Tác nhân:** khách. **Kích hoạt:** gửi biểu mẫu đăng ký.

**Tiền điều kiện:** người dùng chưa có email đó trong hệ thống; dịch vụ và PostgreSQL sẵn sàng.

**Luồng chính:** (1) nhập họ tên, email, mật khẩu, xác nhận mật khẩu và chọn phụ huynh/gia sư; (2) frontend kiểm tra xác nhận; (3) gửi `POST /api/auth/register` với `fullName`, `email`, `password`, `role`; (4) backend chuẩn hóa, kiểm tra, băm mật khẩu bằng BCrypt; (5) lưu tài khoản `ACTIVE`; nếu là gia sư tạo thêm hồ sơ `DRAFT` trong cùng giao dịch; (6) trả `201` và thông tin tài khoản công khai; (7) giao diện thông báo thành công. Không tự tạo phiên đăng nhập.

**Quyết định triển khai bản cơ sở:** họ tên trim, dài 2–120; email trim, chuyển chữ thường, đúng định dạng, tối đa 254; mật khẩu tối thiểu 10 ký tự và tối đa 72 byte UTF-8 theo BCrypt, không trim; role chỉ nhận `PARENT`/`TUTOR`. Biểu mẫu không yêu cầu điện thoại; `confirmPassword` chỉ kiểm tra trên frontend. Chưa có xác minh email, do đó không gắn nhãn email đã xác minh.

**Kết quả API:** `201 {id, fullName, email, role, status: ACTIVE}`; dữ liệu sai/JSON lỗi `400`; email trùng `409`. Lỗi nghiệp vụ có dạng `{code, message, fieldErrors: {field: message}}`. Không trả hash hoặc mật khẩu. Lỗi hạ tầng không được ngụy trang thành đăng ký thành công.

**Ngoại lệ:** email đã tồn tại hoặc hai đăng ký đồng thời tranh cùng email chỉ cho một lần thành công; không cho `ADMIN` hoặc vai trò lạ; mật khẩu dài quá số byte báo lỗi trước khi băm; tạo hồ sơ lỗi rollback cả tài khoản. Lỗi mạng giữ các thông tin không nhạy cảm và cho người dùng thử lại; không ghi mật khẩu vào log, URL, localStorage hoặc sessionStorage.

**Hậu điều kiện:** tài khoản và hồ sơ nháp gia sư (nếu có) được lưu đầy đủ; chưa đăng nhập, chưa có hồ sơ công khai.

- **UC-02-AC01:** đăng ký phụ huynh hợp lệ trả `201`, lưu đúng vai trò `PARENT`, trạng thái `ACTIVE`, không tạo hồ sơ gia sư.
- **UC-02-AC02:** đăng ký gia sư hợp lệ trả `201`, lưu đúng một tài khoản và một hồ sơ `DRAFT`; lỗi tạo hồ sơ không để lại tài khoản.
- **UC-02-AC03:** họ tên/email được trim, email chữ thường; trùng email khác hoa/thường hoặc khoảng trắng trả `409`, kể cả cạnh tranh đồng thời chỉ một tài khoản được tạo.
- **UC-02-AC04:** họ tên/email sai, vai trò thiếu/lạ/`ADMIN`, mật khẩu dưới 10 ký tự hoặc quá 72 byte UTF-8 trả `400` với lỗi trường phù hợp; chuỗi mật khẩu không bị trim.
- **UC-02-AC05:** DB chỉ chứa hash BCrypt xác minh được; response và log không chứa mật khẩu/hash; response không tạo phiên hoặc token đăng nhập.
- **UC-02-AC06:** frontend kiểm tra xác nhận mật khẩu, hiển thị lỗi đúng trường, ngăn bấm lặp khi đang gửi, báo thành công hoặc lỗi mạng rõ ràng và không lưu mật khẩu vào bộ nhớ trình duyệt lâu dài.

### UC-03 — Đăng nhập

**Tác nhân:** khách. **Kích hoạt:** gửi email và mật khẩu.

**Tiền điều kiện:** tài khoản đã tồn tại. **Luồng chính:** (1) nhận email/mật khẩu; (2) chuẩn hóa email như đăng ký; (3) kiểm tra hash và tài khoản hoạt động; (4) tạo ngữ cảnh xác thực; (5) mở khu vực theo vai trò.

**Ngoại lệ:** thông tin sai trả lỗi chung không tiết lộ email có tồn tại; tài khoản khóa không được cấp phiên; dữ liệu thiếu báo lỗi; hạ tầng lỗi không đăng nhập thành công.

**Hậu điều kiện:** yêu cầu tiếp theo có thể xác định danh tính và vai trò; thất bại không tạo phiên. Cơ chế phiên/cookie hay token, thời hạn và giới hạn tần suất cần được chốt trong kiến trúc trước khi code UC-03; không giả định UC-02 đã xử lý đăng nhập.

- **UC-03-AC01:** mật khẩu đúng và tài khoản hoạt động xác thực được, lấy đúng vai trò từ backend.
- **UC-03-AC02:** sai thông tin hoặc bị khóa không tạo ngữ cảnh xác thực.
- **UC-03-AC03:** không nhận vai trò/ID do client tự khai làm căn cứ cấp quyền; lỗi đăng nhập không trả hash.

### UC-04 — Quản lý tài khoản cá nhân

**Tác nhân:** phụ huynh, gia sư, quản trị viên. **Tiền điều kiện:** đăng nhập, tài khoản hoạt động. **Kích hoạt:** mở tài khoản cá nhân.

**Luồng chính:** (1) xem thông tin của mình; (2) sửa họ tên, số điện thoại tùy chọn hoặc chọn đổi mật khẩu; (3) kiểm tra dữ liệu; đổi mật khẩu phải kiểm tra mật khẩu hiện tại và mật khẩu mới theo UC-02; (4) lưu và thông báo.

**Ngoại lệ:** sai mật khẩu hiện tại, dữ liệu không hợp lệ hoặc tài khoản bị khóa thì từ chối; không nhận thay đổi role/status/ID từ form. Đổi email và quên mật khẩu chưa thuộc bản đầu; không thêm ngầm vào UC này. Định dạng/giới hạn điện thoại và chính sách thu hồi phiên sau đổi mật khẩu cần chốt trước triển khai.

**Hậu điều kiện:** chỉ thông tin tài khoản hiện tại thay đổi; hồ sơ công khai gia sư có quy trình riêng tại UC-06.

- **UC-04-AC01:** xem/sửa thông tin của mình, không thể đổi thông tin người khác bằng cách sửa ID.
- **UC-04-AC02:** đổi mật khẩu cần mật khẩu cũ đúng, lưu hash mới và từ chối mật khẩu mới sai chính sách.
- **UC-04-AC03:** payload role/status/email không được dùng để thay đổi quyền, trạng thái hoặc email trong bản đầu.

### UC-05 — Tìm và xem gia sư

**Tác nhân:** khách và người đã đăng nhập. **Tiền điều kiện:** không cần đăng nhập. **Kích hoạt:** mở danh sách hoặc hồ sơ.

**Luồng chính:** (1) nhập bộ lọc môn, khối lớp, học phí, hình thức, khu vực; (2) tải danh sách phân trang; (3) chọn gia sư; (4) xem hồ sơ công khai, môn/khối/giá và đánh giá.

**Ngoại lệ:** không có kết quả hiển thị trạng thái rỗng; lọc sai giá trị báo lỗi; hồ sơ bị ẩn/khóa trước khi mở thì không trả dữ liệu riêng. Lọc môn và khối phải áp dụng trên cùng một môn nhận dạy để tránh tạo tổ hợp không có thật.

**Hậu điều kiện:** không thay đổi dữ liệu; khách có thể quyết định đăng nhập để gửi yêu cầu.

- **UC-05-AC01:** chỉ trả hồ sơ `APPROVED` thuộc tài khoản `ACTIVE`, kể cả truy cập trực tiếp bằng ID.
- **UC-05-AC02:** bộ lọc phù hợp dữ liệu môn/khối/giá của gia sư; danh sách có phân trang và trạng thái rỗng.
- **UC-05-AC03:** không lộ liên hệ, nhu cầu hoặc thông tin học sinh; chưa có đánh giá hiển thị “Chưa có đánh giá”.

### UC-06 — Soạn hồ sơ và gửi duyệt

**Tác nhân:** gia sư. **Tiền điều kiện:** đăng nhập đúng vai trò, tài khoản hoạt động. **Kích hoạt:** mở hồ sơ của tôi.

**Luồng chính:** (1) sửa nháp thông tin giới thiệu, học vấn, kinh nghiệm, hình thức/khu vực và thời gian tham khảo; (2) thêm môn/khối lớp/giá; (3) lưu nháp; (4) gửi duyệt; (5) kiểm tra đầy đủ và chuyển `PENDING_REVIEW`.

**Ngoại lệ:** nháp có thể thiếu thông tin nhưng gửi duyệt phải có ít nhất một môn nhận dạy, hình thức hợp lệ, khu vực nếu trực tiếp, kinh nghiệm không âm; không trùng môn/khối. Đang chờ duyệt không sửa, chỉ rút về nháp nếu chưa được xử lý. Bắt đầu sửa hồ sơ đã duyệt phải cảnh báo việc ẩn và chuyển `DRAFT`; không âm thầm công khai nội dung chưa duyệt.

**Hậu điều kiện:** nháp/đang chờ được lưu đúng; yêu cầu cũ và lớp đã tạo giữ nguyên. Hồ sơ đang sửa/chờ duyệt chưa được chấp nhận yêu cầu mới.

- **UC-06-AC01:** chỉ gia sư sửa được hồ sơ của mình, lưu nháp chưa đầy đủ được.
- **UC-06-AC02:** chỉ hồ sơ đầy đủ, có tổ hợp môn/khối không trùng và giá hợp lệ được gửi duyệt.
- **UC-06-AC03:** sửa hồ sơ đã duyệt đưa về nháp và ẩn công khai; không sửa trực tiếp hồ sơ đang chờ.
- **UC-06-AC04:** rút hồ sơ và quyết định duyệt cạnh tranh không cùng thành công; dữ liệu thỏa thuận cũ không đổi.

### UC-07 — Duyệt hồ sơ

**Tác nhân:** quản trị viên. **Tiền điều kiện:** tài khoản quản trị hoạt động; hồ sơ `PENDING_REVIEW`. **Kích hoạt:** chọn hồ sơ trong danh sách chờ.

**Luồng chính:** (1) xem hồ sơ và danh sách môn/khối/giá; (2) chọn duyệt hoặc yêu cầu bổ sung; (3) nhập lý do nếu bổ sung; (4) lưu trạng thái cùng bản ghi xét duyệt, người xử lý, thời điểm và bản chụp nội dung trong một giao dịch.

**Ngoại lệ:** thiếu lý do bổ sung thì từ chối; hồ sơ đã rút/đã xử lý báo xung đột, tải lại; lỗi lưu lịch sử rollback cả quyết định.

**Hậu điều kiện:** `APPROVED` hoặc `NEEDS_CHANGES`; lịch sử phản ánh chính xác nội dung đã xét.

- **UC-07-AC01:** chỉ quản trị xử lý được hồ sơ đang chờ; duyệt làm hồ sơ đủ điều kiện công khai.
- **UC-07-AC02:** yêu cầu bổ sung cần lý do, lưu lý do để gia sư xem/sửa/gửi lại.
- **UC-07-AC03:** quyết định và lịch sử gồm người/thời điểm/bản chụp được lưu cùng giao dịch; xung đột không ghi đè.

### UC-08 — Xem yêu cầu học

**Tác nhân:** phụ huynh, gia sư. **Tiền điều kiện:** đăng nhập và hoạt động. **Kích hoạt:** mở danh sách yêu cầu.

**Luồng chính:** (1) lọc theo trạng thái; (2) xem danh sách phân trang; (3) xem chi tiết nội dung và kết quả xử lý; (4) hiển thị các thao tác phù hợp vai trò/trạng thái.

**Ngoại lệ:** không có yêu cầu hiển thị rỗng; ID không thuộc người dùng bị từ chối mà không trả nội dung riêng; tài khoản khóa không truy cập.

**Hậu điều kiện:** không thay đổi trạng thái khi xem.

- **UC-08-AC01:** phụ huynh chỉ thấy yêu cầu mình gửi; gia sư chỉ thấy yêu cầu mình nhận.
- **UC-08-AC02:** chi tiết/lọc/phân trang trả đúng thông tin và lý do đã từ chối nếu có.
- **UC-08-AC03:** đổi URL/ID không đọc được yêu cầu người khác; nút thao tác phù hợp trạng thái.

### UC-09 — Hủy yêu cầu đang chờ

**Tác nhân:** phụ huynh tạo yêu cầu. **Tiền điều kiện:** đăng nhập, hoạt động, yêu cầu của mình `PENDING`. **Kích hoạt:** chọn hủy và xác nhận.

**Luồng chính:** (1) kiểm tra quyền sở hữu và trạng thái hiện tại; (2) chuyển `CANCELLED`, ghi thời điểm xử lý; (3) trả trạng thái mới.

**Ngoại lệ:** đã chấp nhận/từ chối/hủy thì không chuyển tiếp; hủy cạnh tranh với chấp nhận chỉ một bên thắng. Yêu cầu đã chấp nhận phải xử lý qua lớp, không hủy yêu cầu gốc.

**Hậu điều kiện:** yêu cầu đã hủy không sinh lớp; không mở lại.

- **UC-09-AC01:** phụ huynh sở hữu hủy yêu cầu đang chờ được, lưu `CANCELLED` và thời điểm.
- **UC-09-AC02:** sai chủ sở hữu hoặc trạng thái cuối bị từ chối; không hủy được yêu cầu đã chấp nhận.
- **UC-09-AC03:** chấp nhận/hủy đồng thời không để yêu cầu hủy có lớp vừa được tạo.

### UC-10 — Chấp nhận yêu cầu

**Tác nhân:** gia sư nhận yêu cầu. **Tiền điều kiện:** hai tài khoản hoạt động; gia sư đăng nhập và hồ sơ `APPROVED`; yêu cầu thuộc gia sư, đang `PENDING`.

**Luồng chính:** (1) xem nội dung và chọn chấp nhận; (2) kiểm tra lại quyền, trạng thái hai tài khoản, hồ sơ và yêu cầu; (3) chuyển `ACCEPTED`; (4) tạo lớp `ACTIVE` tham chiếu yêu cầu; (5) commit chung; (6) trả lớp đã tạo.

**Ngoại lệ:** gia sư đang sửa/chờ duyệt hoặc phụ huynh bị khóa thì giữ yêu cầu chờ; yêu cầu hủy/từ chối không tạo lớp; nếu đã chấp nhận trả lớp hiện có cho đúng gia sư có quyền; lỗi tạo lớp rollback trạng thái yêu cầu. Không tính lại học phí theo hồ sơ mới, không thay nội dung đề nghị.

**Hậu điều kiện:** đúng một lớp từ yêu cầu; mức phí là phí đề xuất đã chấp nhận. Khóa hàng/phiên bản và `UNIQUE(request_id)` bảo vệ đồng thời.

- **UC-10-AC01:** chấp nhận hợp lệ tạo đúng một lớp `ACTIVE`, yêu cầu `ACCEPTED`, thời điểm xử lý được lưu.
- **UC-10-AC02:** không được chấp nhận thay gia sư khác; tài khoản khóa/hồ sơ chưa duyệt/yêu cầu kết thúc không tạo lớp.
- **UC-10-AC03:** nhấp lặp trả lớp hiện có; hai chấp nhận đồng thời không tạo trùng; cạnh tranh hủy/từ chối chỉ một kết quả hợp lệ.
- **UC-10-AC04:** lỗi tạo lớp rollback yêu cầu; học phí và nội dung lấy từ yêu cầu gốc.

### UC-11 — Từ chối yêu cầu

**Tác nhân:** gia sư nhận yêu cầu. **Tiền điều kiện:** đăng nhập, hoạt động; yêu cầu của mình `PENDING`. **Kích hoạt:** chọn từ chối.

**Luồng chính:** (1) nhập lý do; (2) backend kiểm tra quyền, trạng thái và lý do không rỗng; (3) chuyển `REJECTED`, lưu lý do và thời điểm; (4) phụ huynh thấy kết quả.

**Ngoại lệ:** thiếu lý do, sai quyền hoặc trạng thái thay đổi thì từ chối thao tác. Giới hạn độ dài lý do phải khớp thiết kế dữ liệu/API khi triển khai.

**Hậu điều kiện:** không tạo lớp, không mở lại yêu cầu.

- **UC-11-AC01:** từ chối hợp lệ lưu `REJECTED`, lý do và thời điểm, không sinh lớp.
- **UC-11-AC02:** thiếu lý do hoặc sai chủ yêu cầu không cập nhật.
- **UC-11-AC03:** từ chối cạnh tranh với chấp nhận/hủy không ghi đè kết quả đã thắng.

### UC-12 — Xem lớp học

**Tác nhân:** phụ huynh và gia sư của lớp. **Tiền điều kiện:** đăng nhập, tài khoản hoạt động. **Kích hoạt:** mở lớp của tôi.

**Luồng chính:** (1) xem/lọc danh sách lớp; (2) chọn lớp; (3) xem trạng thái, nội dung thỏa thuận, học phí, đối tác và thông tin kết thúc/đánh giá nếu có.

**Ngoại lệ:** sai quyền hoặc lớp không tồn tại không trả dữ liệu riêng; không có lớp hiển thị rỗng. Chưa hiển thị điểm danh/lịch từng buổi như tính năng đã có.

**Hậu điều kiện:** thao tác xem không sửa thỏa thuận.

- **UC-12-AC01:** hai bên chỉ xem được các lớp liên quan mình; đổi ID không đọc được lớp người khác.
- **UC-12-AC02:** phí và nội dung lấy từ yêu cầu đã chấp nhận, vẫn đúng sau khi gia sư sửa hồ sơ.
- **UC-12-AC03:** có phân trang/lọc trạng thái, hiển thị lý do/người/thời điểm kết thúc phù hợp.

### UC-13 — Hoàn thành lớp

**Tác nhân:** phụ huynh của lớp. **Tiền điều kiện:** đăng nhập, hoạt động; lớp của mình `ACTIVE`. **Kích hoạt:** xác nhận hoàn thành.

**Luồng chính:** (1) xác nhận thao tác; (2) kiểm tra quyền và trạng thái; (3) chuyển `COMPLETED`, lưu người và thời điểm kết thúc; (4) cho phép đi đến đánh giá.

**Ngoại lệ:** gia sư/người khác không thực hiện; lớp đã kết thúc không mở lại; hoàn thành cạnh tranh với hủy chỉ một bên thành công.

**Hậu điều kiện:** lớp hoàn thành đủ điều kiện nhận một đánh giá từ phụ huynh; không tự tạo đánh giá.

- **UC-13-AC01:** chỉ phụ huynh sở hữu hoàn thành lớp `ACTIVE`, lưu `ended_by` và thời điểm.
- **UC-13-AC02:** sai vai trò/quyền hoặc lớp đã kết thúc không cập nhật; không mở lại.
- **UC-13-AC03:** hoàn thành/hủy đồng thời giữ một trạng thái cuối duy nhất, không tạo đánh giá tự động.

### UC-14 — Hủy lớp

**Tác nhân:** phụ huynh hoặc gia sư của lớp. **Tiền điều kiện:** đăng nhập, hoạt động; lớp `ACTIVE`. **Kích hoạt:** chọn hủy lớp.

**Luồng chính:** (1) nhập lý do và xác nhận; (2) kiểm tra người dùng là một trong hai bên và trạng thái; (3) chuyển `CANCELLED`, lưu lý do, người và thời điểm hủy.

**Ngoại lệ:** lý do rỗng, người ngoài hoặc lớp đã kết thúc thì từ chối; xung đột báo tải lại. Không chuyển yêu cầu gốc từ `ACCEPTED` về hủy, không hoàn tiền tự động.

**Hậu điều kiện:** lớp không nhận đánh giá; lịch sử yêu cầu và lớp vẫn được giữ.

- **UC-14-AC01:** một trong hai bên hủy được lớp `ACTIVE` với lý do, lưu đầy đủ người/thời điểm.
- **UC-14-AC02:** thiếu lý do, sai người hoặc trạng thái cuối bị từ chối.
- **UC-14-AC03:** lớp hủy không đánh giá/mở lại được; yêu cầu gốc vẫn `ACCEPTED`; cạnh tranh không ghi đè.

### UC-15 — Đánh giá gia sư

**Tác nhân:** phụ huynh của lớp. **Tiền điều kiện:** đăng nhập, hoạt động; lớp `COMPLETED`, chưa có đánh giá. **Kích hoạt:** gửi đánh giá.

**Luồng chính:** (1) chọn số nguyên 1–5 sao, nhận xét tùy chọn tối đa 1.000 ký tự; (2) kiểm tra quyền và trạng thái; (3) lưu gắn lớp; (4) hiển thị trên hồ sơ và tính điểm trung bình từ đánh giá hợp lệ.

**Ngoại lệ:** lớp chưa hoàn thành/hủy, sai người, sao ngoài khoảng hoặc nhận xét quá dài thì từ chối; gửi lặp hoặc đồng thời không sinh hai đánh giá. Bản đầu không sửa/xóa đánh giá.

**Hậu điều kiện:** có một đánh giá; công khai nội dung và tên hiển thị phụ huynh, không công khai thông tin học sinh/yêu cầu.

- **UC-15-AC01:** phụ huynh của lớp hoàn thành tạo được một đánh giá hợp lệ.
- **UC-15-AC02:** sai người/trạng thái, sao không nguyên 1–5 hoặc quá 1.000 ký tự bị từ chối.
- **UC-15-AC03:** gửi lặp/đồng thời chỉ có một đánh giá trên lớp; không có sửa/xóa ở bản đầu.
- **UC-15-AC04:** điểm trung bình chính xác; chưa có đánh giá hiển thị thông báo tương ứng; dữ liệu riêng không lộ.

### UC-16 — Quản lý tài khoản

**Tác nhân:** quản trị viên. **Tiền điều kiện:** đăng nhập, quyền quản trị hoạt động. **Kích hoạt:** mở quản lý tài khoản.

**Luồng chính:** (1) tìm/lọc danh sách; (2) xem thông tin quản trị cần thiết; (3) chọn khóa hoặc mở khóa; (4) kiểm tra quyền và đối tượng; (5) lưu trạng thái, thông báo kết quả.

**Ngoại lệ:** không có quyền không được thực hiện; người bị khóa không tiếp tục thao tác có xác thực, kể cả còn thông tin phiên. Chính sách tự khóa quản trị viên và bảo vệ quản trị cuối cùng phải được chốt trước triển khai; chưa có chuyển vai trò/xóa tài khoản qua màn hình này.

**Hậu điều kiện:** giữ nguyên lịch sử, lớp đang học không tự hủy; hồ sơ gia sư bị khóa không công khai.

- **UC-16-AC01:** chỉ quản trị xem danh sách quản trị và thay trạng thái tài khoản; không trả mật khẩu/hash.
- **UC-16-AC02:** khóa có hiệu lực với yêu cầu xác thực tiếp theo; gia sư khóa không xuất hiện trong tìm kiếm.
- **UC-16-AC03:** khóa/mở khóa không xóa lịch sử hoặc tự hủy lớp; không tự đổi vai trò.

### UC-17 — Theo dõi kết nối

**Tác nhân:** quản trị viên. **Tiền điều kiện:** đăng nhập, quyền quản trị hoạt động. **Kích hoạt:** mở theo dõi kết nối.

**Luồng chính:** (1) lọc yêu cầu/lớp theo trạng thái và các bên; (2) xem danh sách phân trang; (3) mở chi tiết liên kết yêu cầu, lớp và kết quả đánh giá; (4) dùng thông tin để hỗ trợ quản lý.

**Ngoại lệ:** sai vai trò không truy cập; bản ghi không tồn tại báo phù hợp; danh sách rỗng có thông báo. UC này chỉ tra cứu, không tự cho quản trị chấp nhận/hủy/đánh giá thay các bên.

**Hậu điều kiện:** dữ liệu nghiệp vụ không đổi do xem.

- **UC-17-AC01:** chỉ quản trị truy cập danh sách theo dõi toàn hệ thống, có phân trang/lọc.
- **UC-17-AC02:** liên kết yêu cầu → lớp → đánh giá đúng, yêu cầu từ chối không có lớp.
- **UC-17-AC03:** thao tác xem không sửa trạng thái hoặc cấp quyền thao tác thay người dùng; không trả hash/mật khẩu.

## 5. Yêu cầu phi chức năng

| Mã | Yêu cầu có thể kiểm chứng |
|---|---|
| YC-01 | Chỉ lưu hash BCrypt; không commit mật khẩu thật/token hoặc log trường nhạy cảm. Kiểm tra DB, response, log và nội dung Git. |
| YC-02 | API bảo vệ phân biệt không xác thực, sai quyền và xung đột dữ liệu; kiểm tra trên backend bằng kiểm thử HTTP, không dựa vào React. |
| YC-03 | PostgreSQL có khóa ngoại, ràng buộc duy nhất email/yêu cầu-lớp/lớp-đánh giá và index yêu cầu chờ trùng; kiểm thử trên PostgreSQL thật. |
| YC-04 | Danh sách phân trang; giao diện có đang tải, rỗng, lỗi và thành công; biểu mẫu có nhãn, thao tác bàn phím và bố cục dùng được trên màn hình nhỏ. |
| YC-05 | Migration áp dụng được trên DB rỗng; không sửa migration đã dùng trên môi trường có dữ liệu cần giữ. Cấu hình bí mật qua biến môi trường. |
| YC-06 | Mỗi chức năng có mã UC, tiêu chí, ca kiểm thử và trạng thái thực tế; không ghi đạt chỉ vì đã viết code. |
| YC-07 | Kiểm thử tích hợp tái tạo dữ liệu độc lập, không kết nối hoặc xóa DB phát triển/người dùng thật. |
| YC-08 | Ghi lỗi có thông tin phục vụ chẩn đoán nhưng không gửi stacktrace/SQL hoặc dữ liệu nhạy cảm ra client. |

Chưa cam kết số người dùng đồng thời hoặc thời gian phản hồi khi chưa có dữ liệu đo. Nếu đặt mục tiêu hiệu năng sau này, ghi cấu hình máy, kích thước dữ liệu, tải và kết quả đo, không tự điền con số vào báo cáo.

## 6. Những quyết định phải chốt trước UC liên quan

| Nội dung | Thời điểm chốt |
|---|---|
| Cơ chế xác thực, thời hạn, đăng xuất, giới hạn đăng nhập | Trước UC-03 và áp dụng kiểm tra quyền |
| Định dạng điện thoại, thu hồi phiên khi đổi mật khẩu | Trước UC-04 |
| Giới hạn trường hồ sơ và lý do xử lý khớp DB/API | Trước UC-06/07/11/14 |
| Cấp tài khoản quản trị đầu tiên, tự khóa và quản trị cuối cùng | Trước UC-07/16 |
| Thông báo trong ứng dụng hoặc email | Chỉ khi được bổ sung; hiện kết quả được xem qua màn hình/API |

Các quyết định mới phải cập nhật tài liệu, API và ca kiểm thử trước hoặc cùng thay đổi code. Phần đã thống nhất ở bản cơ sở, đặc biệt UC-02, không cần tự tạo một vòng xin xác nhận khác cho chi tiết triển khai thông thường.
