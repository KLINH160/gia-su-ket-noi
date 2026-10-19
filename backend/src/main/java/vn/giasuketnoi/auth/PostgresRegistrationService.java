package vn.giasuketnoi.auth;

import java.sql.SQLException;
import java.util.Map;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.giasuketnoi.common.ApiException;

@Service
@Profile("postgres")
public class PostgresRegistrationService implements RegistrationService {
    private final AccountRepository repository;
    private final PasswordEncoder passwordEncoder;

    public PostgresRegistrationService(AccountRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public RegistrationResponse register(RegistrationRequest request) {
        long id;
        try {
            id = repository.insertAccount(request, passwordEncoder.encode(request.password()));
        } catch (DataIntegrityViolationException exception) {
            // Duy nhat email duoc bao ve o database, ke ca hai yeu cau dong thoi.
            if (isDuplicateEmail(exception)) {
                throw new ApiException(HttpStatus.CONFLICT, "EMAIL_ALREADY_EXISTS",
                        "Email này đã được đăng ký.", Map.of("email", "Email này đã được đăng ký."));
            }
            throw exception;
        }
        if ("TUTOR".equals(request.role())) repository.insertDraftTutorProfile(id);
        return new RegistrationResponse(id, request.fullName(), request.email(), request.role(), "ACTIVE");
    }

    private boolean isDuplicateEmail(Throwable exception) {
        for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
            if (cause instanceof SQLException sql && "23505".equals(sql.getSQLState())
                    && sql.getMessage() != null && sql.getMessage().contains("uq_users_email_case_insensitive")) return true;
        }
        return false;
    }
}
