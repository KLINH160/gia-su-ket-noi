package vn.giasuketnoi.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.DriverManager;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Chay bang -Ppostgres-it verify; database that, schema rieng cho moi lan chay. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("postgres")
class RegistrationPostgresIT {
    private static final String SCHEMA = "it_registration_" + UUID.randomUUID().toString().replace("-", "");
    private static String databaseUrl;
    private static String databaseUser;
    private static String databasePassword;
    private static boolean schemaCreated;
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;
    @Autowired ObjectMapper mapper;
    @Autowired PasswordEncoder passwordEncoder;

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry properties) throws Exception {
        databaseUrl = requiredEnvironment("TEST_DB_URL");
        databaseUser = requiredEnvironment("TEST_DB_USERNAME");
        databasePassword = requiredEnvironment("TEST_DB_PASSWORD");
        if (!databaseUrl.startsWith("jdbc:postgresql:")) throw new IllegalStateException("TEST_DB_URL phai dung PostgreSQL.");
        try (var connection = DriverManager.getConnection(databaseUrl, databaseUser, databasePassword);
             var statement = connection.createStatement()) {
            statement.execute("CREATE SCHEMA " + SCHEMA);
            schemaCreated = true;
        }
        properties.add("spring.datasource.url", () -> databaseUrl);
        properties.add("spring.datasource.username", () -> databaseUser);
        properties.add("spring.datasource.password", () -> databasePassword);
        properties.add("spring.datasource.hikari.schema", () -> SCHEMA);
        properties.add("spring.flyway.default-schema", () -> SCHEMA);
        properties.add("spring.flyway.schemas", () -> SCHEMA);
    }

    private static String requiredEnvironment(String key) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) throw new IllegalStateException(
                "Thieu " + key + ". Profile postgres-it bat buoc ket noi PostgreSQL that; khong bo qua kiem thu.");
        return value;
    }

    @AfterAll static void cleanOwnSchema() throws Exception {
        // Chi xoa schema co ten ngau nhien do chinh lan kiem thu nay tao.
        if (schemaCreated && SCHEMA.matches("it_registration_[a-f0-9]{32}")) {
            try (var connection = DriverManager.getConnection(databaseUrl, databaseUser, databasePassword);
                 var statement = connection.createStatement()) {
                statement.execute("DROP SCHEMA " + SCHEMA + " CASCADE");
            }
        }
    }

    @Test void appliesBothMigrationsOnPostgresAndSeedsSubjects() {
        assertThat(jdbc.queryForObject("SELECT current_schema()", String.class)).isEqualTo(SCHEMA);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM flyway_schema_history WHERE success AND version IS NOT NULL", Integer.class)).isEqualTo(2);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM subjects", Integer.class)).isEqualTo(7);
        assertThat(jdbc.queryForList("SELECT name FROM subjects", String.class)).contains("Toán", "Ngữ văn", "Tiếng Anh");
        assertThat(jdbc.queryForObject("SELECT count(*) FROM information_schema.tables WHERE table_schema=? AND table_type='BASE TABLE'", Integer.class, SCHEMA)).isEqualTo(9);
    }

    @ParameterizedTest @ValueSource(strings = {"PARENT", "TUTOR"})
    void registersRolesAndStoresOnlyHash(String role) throws Exception {
        String email = uniqueEmail();
        String password = " password123 ";
        JsonNode response = mapper.readTree(register("  Nguyễn Thị Linh  ", " " + email.toUpperCase() + " ", password, role)
                .andExpect(status().isCreated()).andExpect(jsonPath("fullName").value("Nguyễn Thị Linh"))
                .andExpect(jsonPath("email").value(email)).andExpect(jsonPath("role").value(role))
                .andExpect(jsonPath("status").value("ACTIVE"))
                .andExpect(jsonPath("password").doesNotExist()).andExpect(jsonPath("passwordHash").doesNotExist())
                .andExpect(jsonPath("token").doesNotExist()).andReturn().getResponse().getContentAsString());
        long id = response.get("id").asLong();
        String stored = jdbc.queryForObject("SELECT password_hash FROM users WHERE id=?", String.class, id);
        assertThat(stored).startsWith("$2a$12$").isNotEqualTo(password);
        assertThat(passwordEncoder.matches(password, stored)).isTrue();
        assertThat(passwordEncoder.matches(password.strip(), stored)).isFalse();
        assertThat(jdbc.queryForObject("SELECT count(*) FROM tutor_profiles WHERE user_id=?", Integer.class, id))
                .isEqualTo(role.equals("TUTOR") ? 1 : 0);
        if (role.equals("TUTOR")) assertThat(jdbc.queryForObject("SELECT status FROM tutor_profiles WHERE user_id=?", String.class, id)).isEqualTo("DRAFT");
    }

    @Test void duplicateNormalizedEmailReturnsConflictAndKeepsOneAccount() throws Exception {
        String email = uniqueEmail();
        register("Linh Nguyen", email, "password123", "PARENT").andExpect(status().isCreated());
        register("Linh Nguyen", " " + email.toUpperCase() + " ", "password123", "TUTOR")
                .andExpect(status().isConflict()).andExpect(jsonPath("code").value("EMAIL_ALREADY_EXISTS"))
                .andExpect(jsonPath("fieldErrors.email").exists());
        assertThat(jdbc.queryForObject("SELECT count(*) FROM users WHERE email=?", Integer.class, email)).isEqualTo(1);
    }

    @ParameterizedTest @ValueSource(strings = {"ADMIN", "INVALID", "parent"})
    void rejectsOtherRolesWithoutPersistence(String role) throws Exception {
        String email = uniqueEmail();
        register("Linh Nguyen", email, "password123", role).andExpect(status().isBadRequest())
                .andExpect(jsonPath("fieldErrors.role").exists());
        assertThat(jdbc.queryForObject("SELECT count(*) FROM users WHERE email=?", Integer.class, email)).isZero();
    }

    @Test void invalidInputsReturnVietnameseFieldErrors() throws Exception {
        register(" ", "invalid", "á".repeat(37), "PARENT").andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("fieldErrors.fullName").exists())
                .andExpect(jsonPath("fieldErrors.email").exists())
                .andExpect(jsonPath("fieldErrors.password").exists());
    }

    @Test void concurrentSameEmailCreatesExactlyOneTutorAndOneProfile() throws Exception {
        String email = uniqueEmail();
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        try (var executor = Executors.newFixedThreadPool(2)) {
            var call = (java.util.concurrent.Callable<Integer>) () -> {
                ready.countDown();
                if (!start.await(10, TimeUnit.SECONDS)) throw new IllegalStateException("Test start timeout");
                return register("Linh Nguyen", email, "password123", "TUTOR").andReturn().getResponse().getStatus();
            };
            var first = executor.submit(call);
            var second = executor.submit(call);
            assertThat(ready.await(10, TimeUnit.SECONDS)).isTrue();
            start.countDown();
            assertThat(java.util.List.of(first.get(30, TimeUnit.SECONDS), second.get(30, TimeUnit.SECONDS)))
                    .containsExactlyInAnyOrder(201, 409);
        }
        assertThat(jdbc.queryForObject("SELECT count(*) FROM users WHERE email=?", Integer.class, email)).isEqualTo(1);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM tutor_profiles p JOIN users u ON p.user_id=u.id WHERE u.email=?", Integer.class, email)).isEqualTo(1);
    }

    @Test void tutorProfileFailureRollsBackNewUser() throws Exception {
        String email = uniqueEmail();
        // Loi duoc tiem chi trong schema test va go bo ngay sau kiem tra.
        jdbc.execute("CREATE FUNCTION fail_test_profile() RETURNS trigger LANGUAGE plpgsql AS $$ BEGIN RAISE EXCEPTION 'test profile failure'; END; $$");
        jdbc.execute("CREATE TRIGGER fail_test_profile BEFORE INSERT ON tutor_profiles FOR EACH ROW EXECUTE FUNCTION fail_test_profile()");
        try {
            register("Linh Nguyen", email, "password123", "TUTOR").andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("code").value("STORAGE_ERROR"));
            assertThat(jdbc.queryForObject("SELECT count(*) FROM users WHERE email=?", Integer.class, email)).isZero();
        } finally {
            jdbc.execute("DROP TRIGGER fail_test_profile ON tutor_profiles");
            jdbc.execute("DROP FUNCTION fail_test_profile()");
        }
    }

    @Test void databaseEnforcesCaseInsensitiveEmailEvenOutsideApi() {
        String email = uniqueEmail();
        insertUser(email, "PARENT");
        assertThatThrownBy(() -> insertUser(email.toUpperCase(), "PARENT"))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test void pendingRequestIndexAllowsHistoryButRejectsTwoPendingRows() {
        long parent = insertUser(uniqueEmail(), "PARENT");
        long tutor = insertUser(uniqueEmail(), "TUTOR");
        jdbc.update("INSERT INTO tutor_profiles(user_id) VALUES (?)", tutor);
        long subject = jdbc.queryForObject("SELECT min(id) FROM subjects", Long.class);
        insertRequest(parent, tutor, subject);
        assertThatThrownBy(() -> insertRequest(parent, tutor, subject)).isInstanceOf(DataIntegrityViolationException.class);
        jdbc.update("UPDATE tutoring_requests SET status='CANCELLED',resolved_at=now() WHERE parent_id=?", parent);
        insertRequest(parent, tutor, subject);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM tutoring_requests WHERE parent_id=?", Integer.class, parent)).isEqualTo(2);
    }

    @Test void databaseRejectsInvalidRoleAndRatingAndOrphanProfile() {
        assertThatThrownBy(() -> insertUser(uniqueEmail(), "OTHER")).isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> jdbc.update("INSERT INTO tutor_profiles(user_id) VALUES (-1)"))
                .isInstanceOf(DataIntegrityViolationException.class);
        long parent = insertUser(uniqueEmail(), "PARENT");
        long tutor = insertUser(uniqueEmail(), "TUTOR");
        jdbc.update("INSERT INTO tutor_profiles(user_id) VALUES (?)", tutor);
        long subject = jdbc.queryForObject("SELECT min(id) FROM subjects", Long.class);
        long request = insertRequest(parent, tutor, subject);
        jdbc.update("UPDATE tutoring_requests SET status='ACCEPTED',resolved_at=now() WHERE id=?", request);
        long classId = jdbc.queryForObject("INSERT INTO tutoring_classes(request_id) VALUES (?) RETURNING id", Long.class, request);
        assertThatThrownBy(() -> jdbc.update("INSERT INTO tutoring_classes(request_id) VALUES (?)", request))
                .isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> jdbc.update("INSERT INTO reviews(class_id,rating) VALUES (?,6)", classId))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test void updatedAtTriggerAndJsonbAreNativePostgresTypes() {
        long user = insertUser(uniqueEmail(), "PARENT");
        jdbc.update("UPDATE users SET full_name='Ten da sua' WHERE id=?", user);
        assertThat(jdbc.queryForObject("SELECT updated_at > created_at FROM users WHERE id=?", Boolean.class, user)).isTrue();
        assertThat(jdbc.queryForObject("SELECT data_type FROM information_schema.columns WHERE table_schema=? AND table_name='profile_reviews' AND column_name='profile_snapshot'", String.class, SCHEMA)).isEqualTo("jsonb");
        assertThat(jdbc.queryForObject("SELECT data_type FROM information_schema.columns WHERE table_schema=? AND table_name='users' AND column_name='created_at'", String.class, SCHEMA)).isEqualTo("timestamp with time zone");
    }

    private String uniqueEmail() { return "test-" + UUID.randomUUID() + "@example.com"; }

    private ResultActions register(String fullName, String email, String password, String role) throws Exception {
        return mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of("fullName", fullName, "email", email, "password", password, "role", role))));
    }

    private long insertUser(String email, String role) {
        return jdbc.queryForObject("INSERT INTO users(email,password_hash,full_name,role) VALUES (?,'test-fixture','Nguoi kiem thu',?) RETURNING id", Long.class, email, role);
    }

    private long insertRequest(long parent, long tutor, long subject) {
        return jdbc.queryForObject("""
                INSERT INTO tutoring_requests(parent_id,tutor_id,subject_id,student_name,student_name_normalized,
                grade_level,learning_goal,learning_mode,desired_schedule,proposed_hourly_rate,desired_start_date)
                VALUES (?,?,?,'Minh','minh',9,'On tap','ONLINE','Thu ba 19h',150000,CURRENT_DATE) RETURNING id
                """, Long.class, parent, tutor, subject);
    }
}
