package org.karar.dev.common.security.provider;

import org.karar.dev.common.exception.ExceptionMessages;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.stereotype.Component;

@Component
public class CustomPreAuthenticationChecks implements UserDetailsChecker {

    @Override
    public void check(UserDetails user) {

        if (!user.isAccountNonLocked()) {
            throw new LockedException(ExceptionMessages.ACCOUNT_LOCKED.format(60));
        }

        if (!user.isEnabled()) {
            throw new DisabledException(ExceptionMessages.ACCOUNT_DISABLED.getMessage());
        }

        if (!user.isAccountNonExpired()) {
            throw new AccountExpiredException("Account expired");
        }
    }
}

