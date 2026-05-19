package org.karar.dev.common.security.service.token;

import org.karar.dev.common.security.service.token.base.TokenType;
import org.karar.dev.common.security.service.token.strategy.TokenStrategy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TokenStrategyFactory {

    private final Map<TokenType, TokenStrategy> strategies;

    public TokenStrategyFactory(List<TokenStrategy> list) {
        this.strategies = list.stream()
                .collect(Collectors.toMap(TokenStrategy::type, strategy -> strategy));
    }

    public TokenStrategy getStrategy(TokenType type) {
        return strategies.get(type);
    }
}
