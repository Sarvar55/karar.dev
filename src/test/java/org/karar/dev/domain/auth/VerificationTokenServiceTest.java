package org.karar.dev.domain.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.karar.dev.common.exception.ExceptionMessages;
import org.karar.dev.domain.annotation.UnitTest;
import org.karar.dev.domain.auth.service.VerificationTokenService;
import org.mockito.Mock;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@UnitTest
class VerificationTokenServiceTest {

    private static final long TOKEN_TTL_HOURS = 24L;
    private static final String TOKEN_PREFIX = "verify:token:";
    private static final String EMAIL_PREFIX = "verify:email:";

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private VerificationTokenService verificationTokenService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        verificationTokenService = new VerificationTokenService(redisTemplate, TOKEN_TTL_HOURS);
    }

    @Nested
    @DisplayName("createToken()")
    class CreateToken {

        @Test
        @DisplayName("Should create a new token and store both keys in Redis")
        void shouldCreateTokenAndStoreBothKeys() {
            String email = "test@karar.dev";

            // No existing token for this email
            when(valueOperations.get(EMAIL_PREFIX + email)).thenReturn(null);

            String token = verificationTokenService.createToken(email);

            assertThat(token).isNotNull().isNotBlank();

            // Verify token → email mapping stored
            verify(valueOperations).set(
                    eq(TOKEN_PREFIX + token),
                    eq(email),
                    eq(TOKEN_TTL_HOURS),
                    eq(TimeUnit.HOURS));

            // Verify email → token mapping stored
            verify(valueOperations).set(
                    eq(EMAIL_PREFIX + email),
                    eq(token),
                    eq(TOKEN_TTL_HOURS),
                    eq(TimeUnit.HOURS));
        }

        @Test
        @DisplayName("Should delete old token before creating a new one on resend")
        void shouldDeleteOldTokenBeforeCreatingNew() {
            String email = "resend@karar.dev";
            String oldToken = "old-uuid-token";

            // Existing token found
            when(valueOperations.get(EMAIL_PREFIX + email)).thenReturn(oldToken);

            String newToken = verificationTokenService.createToken(email);

            assertThat(newToken).isNotNull().isNotEqualTo(oldToken);

            // Verify old keys deleted
            verify(redisTemplate).delete(TOKEN_PREFIX + oldToken);
            verify(redisTemplate).delete(EMAIL_PREFIX + email);

            // Verify new keys stored
            verify(valueOperations).set(
                    eq(TOKEN_PREFIX + newToken),
                    eq(email),
                    eq(TOKEN_TTL_HOURS),
                    eq(TimeUnit.HOURS));

            verify(valueOperations).set(
                    eq(EMAIL_PREFIX + email),
                    eq(newToken),
                    eq(TOKEN_TTL_HOURS),
                    eq(TimeUnit.HOURS));
        }

        @Test
        @DisplayName("Should generate unique tokens for different calls")
        void shouldGenerateUniqueTokens() {
            String email = "unique@karar.dev";
            when(valueOperations.get(EMAIL_PREFIX + email)).thenReturn(null);

            String token1 = verificationTokenService.createToken(email);
            String token2 = verificationTokenService.createToken(email);

            assertThat(token1).isNotEqualTo(token2);
        }
    }

    @Nested
    @DisplayName("verifyToken()")
    class VerifyToken {

        @Test
        @DisplayName("Should verify token and return email")
        void shouldVerifyTokenAndReturnEmail() {
            String token = "valid-uuid-token";
            String email = "verified@karar.dev";

            when(valueOperations.get(TOKEN_PREFIX + token)).thenReturn(email);

            String result = verificationTokenService.verifyToken(token);

            assertThat(result).isEqualTo(email);

            // Verify both keys consumed (deleted)
            verify(redisTemplate).delete(TOKEN_PREFIX + token);
            verify(redisTemplate).delete(EMAIL_PREFIX + email);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when token not found or expired")
        void shouldThrowWhenTokenNotFoundOrExpired() {
            String invalidToken = "expired-or-invalid-token";

            when(valueOperations.get(TOKEN_PREFIX + invalidToken)).thenReturn(null);

            assertThatThrownBy(() -> verificationTokenService.verifyToken(invalidToken))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(ExceptionMessages.VERIFICATION_TOKEN_NOT_FOUND.getMessage());

            // Verify no keys deleted
            verify(redisTemplate, never()).delete(anyString());
        }

        @Test
        @DisplayName("Should consume token so it cannot be reused")
        void shouldConsumeTokenOnVerification() {
            String token = "one-time-token";
            String email = "oneuse@karar.dev";

            when(valueOperations.get(TOKEN_PREFIX + token)).thenReturn(email);

            verificationTokenService.verifyToken(token);

            // After verification, both keys should be deleted
            verify(redisTemplate).delete(TOKEN_PREFIX + token);
            verify(redisTemplate).delete(EMAIL_PREFIX + email);
        }
    }

    @Nested
    @DisplayName("deleteByEmail()")
    class DeleteByEmail {

        @Test
        @DisplayName("Should delete existing token for the given email")
        void shouldDeleteExistingTokenForEmail() {
            String email = "delete@karar.dev";
            String existingToken = "existing-uuid-token";

            when(valueOperations.get(EMAIL_PREFIX + email)).thenReturn(existingToken);

            verificationTokenService.deleteByEmail(email);

            verify(redisTemplate).delete(TOKEN_PREFIX + existingToken);
            verify(redisTemplate).delete(EMAIL_PREFIX + email);
        }

        @Test
        @DisplayName("Should do nothing when no token exists for the email")
        void shouldDoNothingWhenNoTokenExists() {
            String email = "notoken@karar.dev";

            when(valueOperations.get(EMAIL_PREFIX + email)).thenReturn(null);

            verificationTokenService.deleteByEmail(email);

            // No delete calls should be made
            verify(redisTemplate, never()).delete(anyString());
        }
    }
}
