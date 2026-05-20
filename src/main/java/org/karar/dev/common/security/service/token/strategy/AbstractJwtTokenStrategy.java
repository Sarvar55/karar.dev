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

/**
 * Abstract class for implementing JWT token strategies. It provides common functionalities
 * required for creating, parsing, and extracting information from JSON Web Tokens (JWT).
 * This class is built on top of the `TokenStrategy` interface.
 *
 * The strategy uses a secret-based signing key to sign tokens, ensuring their integrity
 * and authenticity. It relies on the `JWTConstants` configuration for properties such as
 * the secret key and expiration times.
 *
 * Subclasses are expected to provide their specific implementation for token-related
 * operations while reusing the common mechanisms provided by this abstract class.
 *
 * Key Features:
 * - Maintains a secret-based signing key for token integrity.
 * - Provides utility methods for token creation, parsing, and extracting claims.
 * - Uses `Claims` from the JWT library to handle token data.
 * - Implements token type determination based on the provided or parsed claims.
 */
public abstract class AbstractJwtTokenStrategy implements TokenStrategy {

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
