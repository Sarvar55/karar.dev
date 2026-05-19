package org.karar.dev.common.security.service.token.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "app.jwt")
@Getter
@Setter
@Component
public class JWTConstants {
    private String secret;
    private String issuer;
    private long accessTokenExpirationTime;
    private long refreshTokenExpirationTime;
}
