package org.karar.dev.common.security.service.token.strategy;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.karar.dev.common.security.service.token.base.JwtClaims;
import org.karar.dev.common.security.service.token.base.TokenType;
import org.karar.dev.common.security.service.token.config.JWTConstants;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public abstract class AbstractJwtTokenStrategy extends AbstractTokenStrategy {

    protected final JWTConstants props;
    protected SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    protected AbstractJwtTokenStrategy(JWTConstants props) {
        this.props = props;
    }

    protected Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    protected TokenType extractType(Claims claims) {
        return TokenType.valueOf(
                claims.get(JwtClaims.TYPE, String.class)
        );
    }

    protected boolean validateToken(String token, TokenType expectedType) {
        try {
            Claims claims = parse(token);
            TokenType actualType = extractType(claims);

            return actualType == expectedType;
        } catch (Exception e) {
            return false;
        }
    }

    protected String createToken(String username, TokenType type, Long exp) {
        return createToken(username, type, exp, Map.of());
    }

    protected String createToken(String username, TokenType type, Long exp, Map<String, Object> claims) {
        return Jwts.builder()
                .setSubject(username)
                .claim(JwtClaims.TYPE, type.name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + exp))
                .addClaims(claims)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}

