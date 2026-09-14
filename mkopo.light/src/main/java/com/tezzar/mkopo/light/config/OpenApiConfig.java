package com.tezzar.mkopo.light.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
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
                        .title("Mkopo Light - Lending Service API")
                        .description("Core lending engine covering loan product configuration, tenure management and fee structures")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Tezzar")
                                )
                        .license(new License()
                                .name("Apache 2.0")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:9081")
                                .description("Local Development Server")
                ));
    }
}
