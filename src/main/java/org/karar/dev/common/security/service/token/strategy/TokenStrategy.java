package org.karar.dev.common.security.service.token.strategy;

import org.karar.dev.common.security.service.token.base.TokenType;

import java.util.Map;

public interface TokenStrategy {
    TokenType type();

    String generate(String username);

    String generate(String username, Map<String, Object> claims);

    boolean validate(String token);

    String extractUsername(String token);
}

