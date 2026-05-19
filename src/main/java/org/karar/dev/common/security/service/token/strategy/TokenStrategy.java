package org.karar.dev.common.security.service.token.strategy;

import org.karar.dev.common.security.service.token.base.TokenType;

import java.util.Map;

/**
 * Represents a strategy for handling tokens in a security framework.
 * This interface defines the essential operations required for managing
 * tokens, including token type identification, generation, validation,
 * and data extraction.
 */
public interface TokenStrategy {
    TokenType type();

    String generate(String username);

    String generate(String username, Map<String, Object> claims);

    boolean validate(String token);

    String extractUsername(String token);
}
