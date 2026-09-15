package com.tezzar.notifications.config;

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
                        .title("Notification Service API")
                        .description("Handles all notifications — email, SMS and push via Firebase")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Tezzar")
                                .email("support@tezzar.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:9082")
                                .description("Local Development Server")
                ));
    }
}
