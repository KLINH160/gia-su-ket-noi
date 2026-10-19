package vn.giasuketnoi.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Locale;

public record RegistrationRequest(
        @NotBlank(message = "Vui lòng nhập họ tên.")
        @Size(min = 2, max = 120, message = "Họ tên phải có từ 2 đến 120 ký tự.") String fullName,
        @NotBlank(message = "Vui lòng nhập email.")
        @Email(message = "Email không đúng định dạng.")
        @Size(max = 254, message = "Email không được quá 254 ký tự.") String email,
        @ValidPassword String password,
        @NotBlank(message = "Vui lòng chọn vai trò.")
        @Pattern(regexp = "PARENT|TUTOR", message = "Chỉ được đăng ký tài khoản phụ huynh hoặc gia sư.") String role) {

    public RegistrationRequest {
        fullName = fullName == null ? null : fullName.strip();
        email = email == null ? null : email.strip().toLowerCase(Locale.ROOT);
        // Mat khau duoc giu nguyen, ke ca khoang trang dau/cuoi.
    }

    @Override
    public String toString() {
        return "RegistrationRequest[password=REDACTED]";
    }
}
