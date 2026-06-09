package org.karar.dev.common.security.provider;

import lombok.RequiredArgsConstructor;
import org.karar.dev.common.exception.ExceptionMessages;
import org.karar.dev.common.security.service.CustomUserDetailsService;
import org.karar.dev.common.security.user.SecurityUser;
import org.karar.dev.domain.auth.service.OtpTokenService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * Authenticates users via a one-time password (OTP) stored in Redis.
 * <p>
 * Only handles {@link OtpAuthenticationToken} — the existing
 * {@link CustomAuthenticationProvider} continues to handle
 * username/password authentication.
 */
@Component
@RequiredArgsConstructor
public class OtpAuthenticationProvider implements AuthenticationProvider {

    private final OtpTokenService otpTokenService;
    private final CustomUserDetailsService userDetailsService;
    private final CustomPreAuthenticationChecks preAuthenticationChecks;

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        String email = auth.getName();
        String otpCode = auth.getCredentials().toString();

        // Validate OTP from Redis — throws if invalid or expired
        if (!otpTokenService.verifyOtp(email, otpCode)) {
            throw new BadCredentialsException(ExceptionMessages.INVALID_OTP.getMessage());
        }

        // Load user details and run pre-auth checks (locked, disabled, etc.)
        SecurityUser user = (SecurityUser) userDetailsService.loadUserByUsername(email);
        preAuthenticationChecks.check(user);

        return new OtpAuthenticationToken(user, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> type) {
        return OtpAuthenticationToken.class.isAssignableFrom(type);
    }
}
