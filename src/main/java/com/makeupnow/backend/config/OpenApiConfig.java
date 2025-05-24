package com.makeupnow.backend.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Makeupnow API")
                .version("1.0")
                .description("Documentation des API Makeupnow")
                .contact(new Contact()
                    .name("Stéphanie")
                    .email("contact@makeupnow.com")
                )
            );
    }
}
