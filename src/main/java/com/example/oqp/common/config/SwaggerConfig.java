package com.example.oqp.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    public OpenAPI openAPI(){
        return new OpenAPI()
                .components(new Components())
                .info(getInfo());
    }

    public Info getInfo(){
        return new Info()
                .title("Online Quiz Project API")
                .description("온라인 퀴즈 프로젝트 백엔드 API swagger")
                .version("1.0.0");
    }
}
