package vn.giasuketnoi.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.CannotCreateTransactionException;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** DK-06/07: request bi tu choi truoc tang nghiep vu, khong the ghi database. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("demo")
class RegistrationInputTest {
    private static final String PASSWORD = "dont-leak-me!";
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @MockitoBean RegistrationService service;

    @ParameterizedTest(name = "{0}: {1}")
    @MethodSource("missingRequiredValues")
    void rejectsMissingNullEmptyAndBlankFields(String field, String state) throws Exception {
        ObjectNode body = validBody();
        switch (state) {
            case "missing" -> body.remove(field);
            case "null" -> body.putNull(field);
            case "empty" -> body.put(field, "");
            case "blank" -> body.put(field, "   ");
            default -> throw new IllegalArgumentException(state);
        }
        send(body).andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("fieldErrors." + field).exists())
                .andExpect(content().string(not(containsString(PASSWORD))));
        verifyNoInteractions(service);
    }

    static Stream<Arguments> missingRequiredValues() {
        return Stream.of("fullName", "email", "password", "role")
                .flatMap(field -> Stream.of("missing", "null", "empty", "blank")
                        .map(state -> Arguments.of(field, state)));
    }

    @ParameterizedTest
    @MethodSource("invalidFieldValues")
    void rejectsInvalidBoundariesAndRolesWithoutCallingService(String field, String value) throws Exception {
        ObjectNode body = validBody();
        body.put(field, value);
        send(body).andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("fieldErrors." + field).exists());
        verifyNoInteractions(service);
    }

    static Stream<Arguments> invalidFieldValues() {
        return Stream.of(
                Arguments.of("fullName", "A"),
                Arguments.of("fullName", "a".repeat(121)),
                Arguments.of("email", "invalid-email"),
                Arguments.of("email", "a".repeat(250) + "@x.vn"),
                Arguments.of("role", "ADMIN"),
                Arguments.of("role", "OTHER"));
    }

    @ParameterizedTest(name = "{0} khong duoc nhan {1}")
    @MethodSource("nonStringValues")
    void rejectsNumbersBooleansObjectsAndArraysInsteadOfCoercingToStrings(String field, Object value) throws Exception {
        ObjectNode body = validBody();
        body.set(field, mapper.valueToTree(value));
        send(body).andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("fieldErrors").isMap())
                .andExpect(content().string(not(containsString(PASSWORD))));
        verifyNoInteractions(service);
    }

    static Stream<Arguments> nonStringValues() {
        return Stream.of("fullName", "email", "password", "role")
                .flatMap(field -> Stream.<Object>of(1234567890L, 10.5, true, Map.of("value", "wrong"), List.of("wrong"))
                        .map(value -> Arguments.of(field, value)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"{broken", "{\"fullName\":", "[]", "null", ""})
    void rejectsMalformedMissingAndNonObjectBodies(String body) throws Exception {
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value("INVALID_REQUEST"));
        verifyNoInteractions(service);
    }

    @Test void rejectsAnotherJsonValueAfterRequestObject() throws Exception {
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(validBody()) + " true"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value("INVALID_REQUEST"));
        verifyNoInteractions(service);
    }

    @Test void connectionFailureUsesCommonErrorContractWithoutLeakingCause() throws Exception {
        when(service.register(any())).thenThrow(new CannotCreateTransactionException(
                "internal-transaction-detail", new SQLException("jdbc:postgresql://private-db password=test-secret")));
        send(validBody()).andExpect(status().isInternalServerError())
                .andExpect(jsonPath("code").value("STORAGE_ERROR"))
                .andExpect(jsonPath("message").value("Không thể lưu tài khoản lúc này. Vui lòng thử lại sau."))
                .andExpect(jsonPath("fieldErrors").isMap())
                .andExpect(jsonPath("trace").doesNotExist())
                .andExpect(content().string(not(containsString("private-db"))))
                .andExpect(content().string(not(containsString("test-secret"))))
                .andExpect(content().string(not(containsString("internal-transaction-detail"))))
                .andExpect(content().string(not(containsString(PASSWORD))));
    }

    private ObjectNode validBody() {
        return mapper.valueToTree(Map.of("fullName", "Linh Nguyen", "email", "linh@example.com",
                "password", PASSWORD, "role", "PARENT"));
    }

    private ResultActions send(ObjectNode body) throws Exception {
        return mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(body)));
    }
}
