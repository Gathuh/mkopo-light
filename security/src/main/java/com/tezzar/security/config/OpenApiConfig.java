package com.tezzar.security.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Security Service API")
                        .description("Handles user management and authentication")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Tezzar")
                                .email("support@tezzar.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:9083")
                                .description("Local Development Server")
                ));
    }
}
