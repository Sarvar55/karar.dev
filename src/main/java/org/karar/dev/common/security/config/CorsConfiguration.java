package org.karar.dev.common.security.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.cors")
@Getter
@Setter
public class CorsConfiguration {
    /** Allowed origins for CORS */
    private String[] allowedOrigins;

    /** Allowed methods for CORS */
    private String[] allowedMethods;

    /** Allowed headers for CORS */
    private String[] allowedHeaders;

    /** Whether credentials are allowed */
    private boolean allowCredentials;
}