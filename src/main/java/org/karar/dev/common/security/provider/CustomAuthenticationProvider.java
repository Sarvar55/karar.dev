package org.karar.dev.common.security.provider;

import lombok.RequiredArgsConstructor;
import org.karar.dev.common.exception.ExceptionMessages;
import org.karar.dev.common.security.service.CustomUserDetailsService;
import org.karar.dev.common.security.user.SecurityUser;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final CustomPreAuthenticationChecks preAuthenticationChecks;

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        String email = auth.getName();
        String rawPassword = auth.getCredentials().toString();
        SecurityUser user = (SecurityUser) userDetailsService.loadUserByUsername(email);

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BadCredentialsException(ExceptionMessages.INVALID_CREDENTIALS.getMessage());
        }
        preAuthenticationChecks.check(user);

        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> type) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(type);
    }
}
