package org.karar.dev.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String API_TITLE = "Karar Dev API";
    private static final String API_VERSION = "1.0";
    private static final String API_DESCRIPTION = "API documentation for Karar Dev Application";

    private static final String CONTACT_NAME = "Karar Dev Team";
    private static final String CONTACT_EMAIL = "contact@karar.dev";

    private static final String LICENSE_NAME = "Apache 2.0";
    private static final String LICENSE_URL = "https://springdoc.org";

    private static final String SECURITY_SCHEME_NAME = "Bearer Authentication";
    private static final String SECURITY_SCHEME = "bearer";
    private static final String BEARER_FORMAT = "JWT";
    private static final String SECURITY_DESCRIPTION = "Enter JWT token. Example: eyJhbGciOiJIUzI1NiJ9...";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .addSecurityItem(securityRequirement())
                .components(apiComponents());
    }

    private Info apiInfo() {
        return new Info()
                .title(API_TITLE)
                .version(API_VERSION)
                .description(API_DESCRIPTION)
                .contact(apiContact())
                .license(apiLicense());
    }

    private Contact apiContact() {
        return new Contact()
                .name(CONTACT_NAME)
                .email(CONTACT_EMAIL);
    }

    private License apiLicense() {
        return new License()
                .name(LICENSE_NAME)
                .url(LICENSE_URL);
    }

    private SecurityRequirement securityRequirement() {
        return new SecurityRequirement()
                .addList(SECURITY_SCHEME_NAME);
    }

    private Components apiComponents() {
        return new Components()
                .addSecuritySchemes(SECURITY_SCHEME_NAME, jwtBearerSecurityScheme());
    }

    private SecurityScheme jwtBearerSecurityScheme() {
        return new SecurityScheme()
                .name(SECURITY_SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme(SECURITY_SCHEME)
                .bearerFormat(BEARER_FORMAT)
                .description(SECURITY_DESCRIPTION);
    }
}
