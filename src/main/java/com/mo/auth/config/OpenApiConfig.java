package com.mo.auth.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        // Define the name of our security scheme
        final String securitySchemeName = "Bearer Authentication";

        return new OpenAPI()
                // 1. Basic API Information
                .info(new Info()
                        .title("JWT Auth System API")
                        .version("1.0")
                        .description("Secure REST API for user registration, login, and profile management."))

                // 2. Add Security Requirement globally to all endpoints
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))

                // 3. Define the Security Scheme (JWT Bearer)
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
