package org.karar.dev.common.security.service.token;

import java.util.Map;

import org.karar.dev.common.security.service.token.facade.AccessTokenManager;
import org.karar.dev.common.security.service.token.facade.RefreshTokenManager;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenManager {

    private final AccessTokenManager accessTokenManager;
    private final RefreshTokenManager refreshTokenManager;

    public String generateAccessToken(String subject, Map<String, Object> claims) {
        return accessTokenManager.generate(subject, claims);
    }

    public String generateRefreshToken(String subject, Map<String, Object> claims) {
        return refreshTokenManager.generate(subject, claims);
    }

    public String generateAccessToken(String subject) {
        return accessTokenManager.generate(subject);
    }

    public String generateRefreshToken(String subject) {
        return refreshTokenManager.generate(subject);
    }

    public boolean isValidAccessToken(String token) {
        return accessTokenManager.validate(token);
    }

    public boolean isValidRefreshToken(String token) {
        return refreshTokenManager.validate(token);
    }

}
