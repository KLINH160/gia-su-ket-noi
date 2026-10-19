package vn.giasuketnoi.auth;

import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import vn.giasuketnoi.common.ApiException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class RegistrationServiceTest {
    private final AccountRepository repository = mock(AccountRepository.class);
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(4);
    private final RegistrationService service = new PostgresRegistrationService(repository, encoder);

    @Test void hashesPasswordAndCreatesDraftForTutor() {
        var request = new RegistrationRequest("Linh Nguyen", "linh@example.com", " password123 ", "TUTOR");
        when(repository.insertAccount(any(), anyString())).thenReturn(42L);
        var result = service.register(request);
        var hash = ArgumentCaptor.forClass(String.class);
        verify(repository).insertAccount(eq(request), hash.capture());
        assertThat(hash.getValue()).isNotEqualTo(request.password());
        assertThat(encoder.matches(request.password(), hash.getValue())).isTrue();
        assertThat(encoder.matches(request.password().strip(), hash.getValue())).isFalse();
        verify(repository).insertDraftTutorProfile(42L);
        assertThat(result.id()).isEqualTo(42L);
        assertThat(result.status()).isEqualTo("ACTIVE");
    }

    @Test void parentDoesNotReceiveTutorProfile() {
        when(repository.insertAccount(any(), anyString())).thenReturn(1L);
        service.register(new RegistrationRequest("Linh Nguyen", "linh@example.com", "password123", "PARENT"));
        verify(repository, never()).insertDraftTutorProfile(anyLong());
    }

    @Test void mapsOnlyEmailUniqueConstraintToConflict() {
        when(repository.insertAccount(any(), anyString())).thenThrow(new DataIntegrityViolationException("duplicate",
                new SQLException("violates uq_users_email_case_insensitive", "23505")));
        assertThatThrownBy(() -> service.register(new RegistrationRequest("Linh Nguyen", "linh@example.com", "password123", "PARENT")))
                .isInstanceOfSatisfying(ApiException.class, exception -> {
                    assertThat(exception.status().value()).isEqualTo(409);
                    assertThat(exception.code()).isEqualTo("EMAIL_ALREADY_EXISTS");
                });
    }

    @Test void doesNotMisreportOtherDatabaseErrorsAsDuplicateEmail() {
        when(repository.insertAccount(any(), anyString())).thenThrow(new DataIntegrityViolationException("other constraint"));
        assertThatThrownBy(() -> service.register(new RegistrationRequest("Linh Nguyen", "linh@example.com", "password123", "PARENT")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
