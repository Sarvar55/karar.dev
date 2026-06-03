package org.karar.dev.common.security.service.token.strategy;

import java.util.Map;

public abstract class AbstractTokenStrategy implements TokenStrategy {

    @Override
    public final String generate(String username) {
        return generate(username, Map.of());
    }

    @Override
    public final String generate(String username, Map<String, Object> claims) {

        Map<String, Object> enrichedClaims = enrichClaims(username, claims);

        String token = doGenerate(username, enrichedClaims);

        postGenerate(username, token, enrichedClaims);

        return token;
    }

    @Override
    public final boolean validate(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        if (!isFormatValid(token)) {
            return false;
        }

        return doValidate(token);
    }

    @Override
    public final String extractUsername(String token) {
        if (!validate(token)) {
            throw new IllegalArgumentException("Invalid or expired token");
        }
        return doExtractUsername(token);
    }

    protected abstract String doGenerate(String username, Map<String, Object> claims);

    protected abstract boolean doValidate(String token);

    protected abstract String doExtractUsername(String token);

    protected Map<String, Object> enrichClaims(String username, Map<String, Object> claims) {
        return claims;
    }

    protected void postGenerate(String username, String token, Map<String, Object> claims) {

    }

    protected boolean isFormatValid(String token) {
        return true;
    }
}

