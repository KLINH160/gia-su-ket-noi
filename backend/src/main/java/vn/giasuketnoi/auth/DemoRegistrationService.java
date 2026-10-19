package vn.giasuketnoi.auth;

import java.util.Map;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.giasuketnoi.common.ApiException;

@Service
@Profile("demo")
public class DemoRegistrationService implements RegistrationService {
    @Override
    public RegistrationResponse register(RegistrationRequest request) {
        throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "DATABASE_NOT_CONFIGURED",
                "Chế độ xem thử chưa kết nối PostgreSQL. Hãy chạy backend với profile postgres để đăng ký.", Map.of());
    }
}
