package vn.giasuketnoi.auth;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class RegistrationValidationTest {
    private static final ValidatorFactory FACTORY = Validation.buildDefaultValidatorFactory();
    private final Validator validator = FACTORY.getValidator();

    @AfterAll static void closeValidator() { FACTORY.close(); }

    @Test void normalizesIdentityButNeverChangesPassword() {
        var request = new RegistrationRequest("  Nguyễn Thị Linh  ", "  Linh@Example.com  ", " password123 ", "PARENT");
        assertThat(request.fullName()).isEqualTo("Nguyễn Thị Linh");
        assertThat(request.email()).isEqualTo("linh@example.com");
        assertThat(request.password()).isEqualTo(" password123 ");
        assertThat(validator.validate(request)).isEmpty();
        assertThat(request.toString()).doesNotContain("password123");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"short", "          ", "123456789"})
    void rejectsMissingOrShortPassword(String password) {
        assertThat(validator.validate(new RegistrationRequest("Linh Nguyen", "linh@example.com", password, "TUTOR")))
                .anyMatch(error -> error.getPropertyPath().toString().equals("password"));
    }

    @Test void enforcesUtf8BytesInsteadOfJavaLength() {
        assertThat(new PasswordValidator().isValid("á".repeat(36), null)).isTrue();
        assertThat(new PasswordValidator().isValid("á".repeat(37), null)).isFalse();
        assertThat(new PasswordValidator().isValid("a".repeat(72), null)).isTrue();
        assertThat(new PasswordValidator().isValid("a".repeat(73), null)).isFalse();
        assertThat(new PasswordValidator().isValid("😀".repeat(9), null)).isFalse();
        assertThat(new PasswordValidator().isValid("😀".repeat(10), null)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "admin", "tutor", "OTHER", ""})
    void rejectsUnsupportedRoles(String role) {
        assertThat(validator.validate(new RegistrationRequest("Linh Nguyen", "linh@example.com", "password123", role)))
                .anyMatch(error -> error.getPropertyPath().toString().equals("role"));
    }

    @Test void rejectsNameAndEmailOutsideContract() {
        var errors = validator.validate(new RegistrationRequest(" A ", "invalid", "password123", "PARENT"));
        assertThat(errors).extracting(error -> error.getPropertyPath().toString()).contains("fullName", "email");
        assertThat(validator.validate(new RegistrationRequest("a".repeat(121), "a".repeat(250) + "@x.vn", "password123", "PARENT")))
                .extracting(error -> error.getPropertyPath().toString()).contains("fullName", "email");
    }
}
