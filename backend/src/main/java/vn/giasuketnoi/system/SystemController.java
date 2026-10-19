package vn.giasuketnoi.system;

import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system")
public class SystemController {
    private final Environment environment;

    public SystemController(Environment environment) {
        this.environment = environment;
    }

    @GetMapping("/status")
    public SystemStatus status() {
        // This is application liveness, not a database readiness probe.
        boolean demo = environment.acceptsProfiles(Profiles.of("demo"));
        return new SystemStatus("Gia Sư Kết Nối", "UP", demo ? "demo" : "postgres",
                demo ? "DISABLED" : "NOT_CHECKED");
    }

    public record SystemStatus(String application, String status, String mode, String database) {}
}
