package org.karar.dev.common.security.service.token.strategy;

import org.karar.dev.common.security.service.token.base.TokenType;
import org.karar.dev.common.security.service.token.config.JWTConstants;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AccessTokenStrategy extends AbstractJwtTokenStrategy {

    protected AccessTokenStrategy(JWTConstants props) {
        super(props);
    }

    @Override
    public TokenType type() {
        return TokenType.ACCESS;
    }

    @Override
    public String generate(String username) {
        return createToken(username, type(), props.getAccessTokenExpirationTime());
    }

    @Override
    public String generate(String username, Map<String, Object> claims) {
        return createToken(username, type(), props.getAccessTokenExpirationTime(), claims);
    }

    @Override
    public boolean validate(String token) {
        return validateToken(token, type());
    }

    @Override
    public String extractUsername(String token) {
        return parse(token).getSubject();
    }
}
