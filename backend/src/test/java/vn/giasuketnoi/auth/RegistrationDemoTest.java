package vn.giasuketnoi.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("demo")
class RegistrationDemoTest {
    @Autowired MockMvc mvc;

    @Test void neverClaimsSuccessWithoutDatabase() throws Exception {
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content("""
                {"fullName":"Linh Nguyen","email":"linh@example.com","password":"password123","role":"PARENT"}
                """))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("code").value("DATABASE_NOT_CONFIGURED"))
                .andExpect(jsonPath("fieldErrors").isMap());
    }

    @Test void invalidInputHasFieldErrorsAndNoPasswordEcho() throws Exception {
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content("""
                {"fullName":" ","email":"invalid","password":"secret","role":"ADMIN"}
                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("fieldErrors.fullName").exists())
                .andExpect(jsonPath("fieldErrors.email").exists())
                .andExpect(jsonPath("fieldErrors.password").exists())
                .andExpect(jsonPath("fieldErrors.role").exists())
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("secret"))));
    }

    @Test void malformedJsonIsBadRequest() throws Exception {
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content("{broken"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("code").value("INVALID_REQUEST"));
    }
}
