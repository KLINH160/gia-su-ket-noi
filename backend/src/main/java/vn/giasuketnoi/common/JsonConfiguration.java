package vn.giasuketnoi.common;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.type.LogicalType;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonConfiguration {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer strictRequestTypes() {
        return builder -> builder
                .featuresToDisable(MapperFeature.ALLOW_COERCION_OF_SCALARS)
                .featuresToEnable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
                .postConfigurer(mapper -> {
                    // String can cau hinh rieng: khong tu bien so/boolean thanh mat khau hoac ho ten.
                    var text = mapper.coercionConfigFor(LogicalType.Textual);
                    text.setCoercion(CoercionInputShape.Integer, CoercionAction.Fail);
                    text.setCoercion(CoercionInputShape.Float, CoercionAction.Fail);
                    text.setCoercion(CoercionInputShape.Boolean, CoercionAction.Fail);
                });
    }
}
