package vn.giasuketnoi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("demo")
class SystemControllerTest {
    @Autowired MockMvc mvc;

    @Test
    void bootstrapWorksWithoutDatabaseAndReportsThatExplicitly() throws Exception {
        mvc.perform(get("/api/system/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("status").value("UP"))
                .andExpect(jsonPath("mode").value("demo"))
                .andExpect(jsonPath("database").value("DISABLED"));
    }
}
