package org.karar.dev.common.security.service.token;

import org.karar.dev.common.security.service.token.base.TokenType;
import org.karar.dev.common.security.service.token.strategy.TokenStrategy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TokenService {

    private final Map<TokenType, TokenStrategy> strategies;

    public TokenService(List<TokenStrategy> list) {
        this.strategies = list.stream()
                .collect(Collectors.toMap(TokenStrategy::type, strategy -> strategy));
    }

    public String generate(TokenType type, String username) {
        return strategies.get(type).generate(username);
    }

    public String generate(TokenType type, String username, Map<String, Object> claims) {
        return strategies.get(type).generate(username, claims);
    }

    public boolean validate(TokenType type, String token) {
        return strategies.get(type).validate(token);
    }

    public String extractUsername(TokenType type, String token) {
        return strategies.get(type).extractUsername(token);
    }
}
