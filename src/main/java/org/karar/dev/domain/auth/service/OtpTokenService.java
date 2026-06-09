package org.karar.dev.domain.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

/**
 * Generates, stores, and validates one-time passwords (OTP) in Redis.
 * <p>
 * Redis key pattern: {@code otp:<email>} → 6-digit code, TTL = configurable minutes.
 */
@Service
@Slf4j
public class OtpTokenService {

    private static final String OTP_PREFIX = "otp:";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final StringRedisTemplate redisTemplate;
    private final long otpTtlMinutes;
    private final int otpLength;

    public OtpTokenService(
            StringRedisTemplate redisTemplate,
            @Value("${app.otp.ttl-minutes:5}") long otpTtlMinutes,
            @Value("${app.otp.length:6}") int otpLength) {
        this.redisTemplate = redisTemplate;
        this.otpTtlMinutes = otpTtlMinutes;
        this.otpLength = otpLength;
    }

    /**
     * Generate a new OTP for the given email and store it in Redis.
     * Any previous OTP for this email is overwritten.
     *
     * @return the generated OTP code
     */
    public String createOtp(String email) {
        String code = generateCode();
        String key = OTP_PREFIX + email;

        redisTemplate.opsForValue().set(key, code, otpTtlMinutes, TimeUnit.MINUTES);
        log.debug("OTP created for email: {}", email);

        return code;
    }

    /**
     * Verify the OTP for the given email.
     * On success, the OTP is consumed (deleted from Redis).
     *
     * @return true if valid, false otherwise
     */
    public boolean verifyOtp(String email, String code) {
        String key = OTP_PREFIX + email;
        String storedCode = redisTemplate.opsForValue().get(key);

        if (storedCode == null || !storedCode.equals(code)) {
            log.debug("OTP verification failed for email: {}", email);
            return false;
        }

        // Consume the OTP — it can only be used once
        redisTemplate.delete(key);
        log.info("OTP verified and consumed for email: {}", email);
        return true;
    }

    private String generateCode() {
        int bound = (int) Math.pow(10, otpLength);
        int code = RANDOM.nextInt(bound);
        return String.format("%0" + otpLength + "d", code);
    }
}
