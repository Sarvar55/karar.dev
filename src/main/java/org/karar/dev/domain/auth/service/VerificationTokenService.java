package org.karar.dev.domain.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.karar.dev.common.exception.ExceptionMessages;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class VerificationTokenService {

    private static final String TOKEN_PREFIX = "verify:token:";
    private static final String EMAIL_PREFIX = "verify:email:";

    private final StringRedisTemplate redisTemplate;
    private final long tokenTtlHours;

    public VerificationTokenService(
            StringRedisTemplate redisTemplate,
            @Value("${app.verification.token-ttl-hours:24}") long tokenTtlHours) {
        this.redisTemplate = redisTemplate;
        this.tokenTtlHours = tokenTtlHours;
    }

    public String createToken(String email) {

        deleteByEmail(email);

        String token = UUID.randomUUID().toString();

        redisTemplate.opsForValue().set(
                TOKEN_PREFIX + token, email, tokenTtlHours, TimeUnit.HOURS);

        redisTemplate.opsForValue().set(
                EMAIL_PREFIX + email, token, tokenTtlHours, TimeUnit.HOURS);

        log.debug("Verification token created for email: {}", email);
        return token;
    }

    public String verifyToken(String token) {
        String tokenKey = TOKEN_PREFIX + token;
        String email = redisTemplate.opsForValue().get(tokenKey);

        if (email == null) {
            throw new IllegalArgumentException(
                    ExceptionMessages.VERIFICATION_TOKEN_NOT_FOUND.getMessage());
        }

        redisTemplate.delete(tokenKey);
        redisTemplate.delete(EMAIL_PREFIX + email);

        log.info("Verification token consumed for email: {}", email);
        return email;
    }

    public void deleteByEmail(String email) {
        String emailKey = EMAIL_PREFIX + email;
        String existingToken = redisTemplate.opsForValue().get(emailKey);

        if (existingToken != null) {
            redisTemplate.delete(TOKEN_PREFIX + existingToken);
            redisTemplate.delete(emailKey);
            log.debug("Deleted old verification token for email: {}", email);
        }
    }
}

