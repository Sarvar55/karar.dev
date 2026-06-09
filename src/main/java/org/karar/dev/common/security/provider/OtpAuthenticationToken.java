package org.karar.dev.common.security.provider;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Authentication token for OTP-based login.
 * <p>
 * Before authentication: principal = email, credentials = OTP code.
 * After authentication: principal = SecurityUser, credentials = null.
 */
public class OtpAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    private Object credentials;

    /** Create an unauthenticated token (before provider validates it). */
    public OtpAuthenticationToken(String email, String otpCode) {
        super((Collection<? extends GrantedAuthority>) null);
        this.principal = email;
        this.credentials = otpCode;
        setAuthenticated(false);
    }

    /** Create an authenticated token (after provider validates it). */
    public OtpAuthenticationToken(Object principal,
            Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = null;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
