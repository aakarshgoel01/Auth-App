package com.authentication.application.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Auth App buid by Aakarsh Goel",
                description = "Generic auth app that can be used with any application.",
                contact = @Contact(
                        name = "Aakarsh Goel",
                        url = "https://www.google.com/",
                        email = "goelaakarsh05@gmail.com"
                        ),
                version = "1.0",
                summary = "This is a very useful application for authorization "
        ),
        security = {
                @SecurityRequirement(
                        name = "bearerAuth"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class APIDocsConfig {
}
