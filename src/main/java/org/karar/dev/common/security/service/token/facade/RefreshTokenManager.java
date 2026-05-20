package org.karar.dev.common.security.service.token.facade;

import org.karar.dev.common.security.service.token.base.TokenType;
import org.karar.dev.common.security.service.token.factory.TokenStrategyFactory;
import org.karar.dev.common.security.service.token.strategy.TokenStrategy;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RefreshTokenManager {
    private final TokenStrategy tokenStrategy;

    public RefreshTokenManager(TokenStrategyFactory factory) {
        this.tokenStrategy = factory.getStrategy(TokenType.REFRESH);
    }

    public String generate(String username) {
        return tokenStrategy.generate(username);
    }

    public String generate(String username, Map<String, Object> claims) {
        return tokenStrategy.generate(username, claims);
    }

    public boolean validate(String token) {
        return tokenStrategy.validate(token);
    }

    public String extractUsername(String token) {
        return tokenStrategy.extractUsername(token);
    }
}
