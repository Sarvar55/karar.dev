package org.karar.dev.common.security.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.karar.dev.common.security.service.token.config.JWTConstants;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JWTService {

    private static final String USER_ID = "userId";
    private static final String ROLE = "role";

    private final JWTConstants jwtConstants;
    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtConstants.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String email, UUID userId, String role) {
        log.debug("Generating token for user: {}", email);
        return Jwts.builder()
                .setSubject(email)
                .addClaims(prepareClaims(email, userId, role))
                .setIssuer(jwtConstants.getIssuer())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(prepareExpirationDate())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Extracts the user ID stored in the JWT claims.
     */
    public UUID getUserIdFromToken(String token) {
        String userId = getClaims(token).get("userId", String.class);
        return UUID.fromString(userId);
    }

    /**
     * Extracts the role stored in the JWT claims.
     */
    public String getRoleFromToken(String token) {
        return getClaims(token).get("role", String.class);
    }

    private Claims getClaims(String token) {
        log.debug("Parsing JWT claims");
        return Jwts.parserBuilder()
                .setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    private Map<String, Object> prepareClaims(String email, UUID userId, String role) {
        return Map.of(
                USER_ID, userId.toString(),
                ROLE, role);
    }

    private Date prepareExpirationDate() {
        return Date.from(Instant.now().plusMillis(jwtConstants.getAccessTokenExpirationTime()));
    }
}
