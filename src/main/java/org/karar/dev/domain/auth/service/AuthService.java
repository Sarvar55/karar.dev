package org.karar.dev.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.karar.dev.common.exception.ExceptionMessages;
import org.karar.dev.common.exception.base.ValidationException;
import org.karar.dev.common.exception.conflict.EmailAlreadyExistsException;
import org.karar.dev.common.security.service.token.TokenManager;
import org.karar.dev.common.security.service.token.base.JwtClaims;
import org.karar.dev.common.security.user.SecurityUser;
import org.karar.dev.domain.auth.dto.*;
import org.karar.dev.domain.auth.event.EmailVerificationEvent;
import org.karar.dev.domain.auth.event.EmailVerificationProducer;
import org.karar.dev.domain.auth.service.VerificationTokenService;
import org.karar.dev.domain.user.entity.User;
import org.karar.dev.domain.user.repository.UserRepository;
import org.karar.dev.domain.user.company.entity.CompanyUser;
import org.karar.dev.domain.user.regular.entity.RegularUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenManager tokenManager;
    private final VerificationTokenService verificationTokenService;
    private final EmailVerificationProducer emailVerificationProducer;

    @Value("${app.verification.base-url:http://localhost:8080}")
    private String verificationBaseUrl;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        log.debug("Registering user: {}", request.email());
        checkEmailUniqueness(request.email());

        User user = createUser(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEmailVerified(false);

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getEmail());

        String token = verificationTokenService.createToken(savedUser.getEmail());
        String verificationUrl = verificationBaseUrl + "/api/auth/verify?token=" + token;

        emailVerificationProducer.send(
                new EmailVerificationEvent(savedUser.getEmail(), token, verificationUrl));

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                "Registration successful. Please check your email to verify your account.");
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();

        User user = userRepository.findByEmail(securityUser.getUsername())
                .orElseThrow(() -> new BadCredentialsException(
                        ExceptionMessages.INVALID_CREDENTIALS.getMessage()));

        log.info("User logged in successfully: {}", user.getEmail());
        return buildAuthResponse(user);
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();

        if (!tokenManager.isValidRefreshToken(refreshToken)) {
            throw new BadCredentialsException(
                    ExceptionMessages.INVALID_REFRESH_TOKEN.getMessage());
        }

        String email = tokenManager.extractRefreshUsername(refreshToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException(
                        ExceptionMessages.INVALID_CREDENTIALS.getMessage()));

        log.info("Token refreshed successfully for user: {}", user.getEmail());
        return buildAuthResponse(user);
    }

    @Transactional
    public String verifyEmail(String token) {
        String email = verificationTokenService.verifyToken(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        ExceptionMessages.VERIFICATION_TOKEN_NOT_FOUND.getMessage()));

        if (user.isEmailVerified()) {
            return ExceptionMessages.EMAIL_ALREADY_VERIFIED.getMessage();
        }

        user.setEmailVerified(true);
        userRepository.save(user);
        log.info("Email verified successfully for user: {}", email);

        return "Email verified successfully";
    }

    @Transactional
    public String resendVerification(ResendVerificationRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException(
                        ExceptionMessages.INVALID_CREDENTIALS.getMessage()));

        if (user.isEmailVerified()) {
            return ExceptionMessages.EMAIL_ALREADY_VERIFIED.getMessage();
        }

        String token = verificationTokenService.createToken(user.getEmail());
        String verificationUrl = verificationBaseUrl + "/api/auth/verify?token=" + token;

        emailVerificationProducer.send(
                new EmailVerificationEvent(user.getEmail(), token, verificationUrl));

        log.info("Verification email resent to: {}", user.getEmail());
        return "Verification email resent";
    }

    private User createUser(RegisterRequest request) {
        if (request.isCompanyRegistration()) {
            return new CompanyUser(request.email(), request.password(), request.companyName());
        }

        if (request.username() == null || request.username().isBlank()) {
            throw new ValidationException(
                    Map.of("username", ExceptionMessages.USERNAME_REQUIRED.getMessage()));
        }
        return new RegularUser(request.email(), request.password(), request.username());
    }

    private void checkEmailUniqueness(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }
    }

    private AuthResponse buildAuthResponse(User user) {
        Map<String, Object> claims = Map.of(JwtClaims.USERID, user.getId());
        String accessToken = tokenManager.generateAccessToken(user.getEmail(), claims);
        String refreshToken = tokenManager.generateRefreshToken(user.getEmail(), claims);
        return new AuthResponse(user.getId(), user.getEmail(), user.getRole(), accessToken, refreshToken);
    }
}

