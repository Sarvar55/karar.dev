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
    protected String doGenerate(String username, Map<String, Object> claims) {
        return createToken(username, type(), props.getAccessTokenExpirationTime(), claims);
    }

    @Override
    protected boolean doValidate(String token) {
        return validateToken(token, type());
    }

    @Override
    protected String doExtractUsername(String token) {
        return parse(token).getSubject();
    }
}

