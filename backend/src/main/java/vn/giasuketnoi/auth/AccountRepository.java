package vn.giasuketnoi.auth;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Profile("postgres")
public class AccountRepository {
    private final JdbcTemplate jdbc;

    public AccountRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public long insertAccount(RegistrationRequest request, String passwordHash) {
        Long id = jdbc.queryForObject("""
                INSERT INTO users (full_name,email,password_hash,role)
                VALUES (?,?,?,?) RETURNING id
                """, Long.class, request.fullName(), request.email(), passwordHash, request.role());
        if (id == null) throw new IllegalStateException("Database did not return an account id");
        return id;
    }

    public void insertDraftTutorProfile(long userId) {
        jdbc.update("INSERT INTO tutor_profiles(user_id) VALUES (?)", userId);
    }
}
