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

    private String[] allowedOrigins;

    private String[] allowedMethods;

    private String[] allowedHeaders;

    private boolean allowCredentials;
}
