package org.karar.dev.domain.auth;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.karar.dev.common.enums.Role;
import org.karar.dev.common.exception.ExceptionMessages;
import org.karar.dev.common.exception.base.ValidationException;
import org.karar.dev.common.exception.conflict.EmailAlreadyExistsException;
import org.karar.dev.common.security.service.JWTService;
import org.karar.dev.common.security.user.SecurityUser;
import org.karar.dev.domain.auth.dto.AuthResponse;
import org.karar.dev.domain.auth.dto.LoginRequest;
import org.karar.dev.domain.auth.dto.RegisterRequest;
import org.karar.dev.domain.base.BaseResponse;
import org.karar.dev.domain.user.User;
import org.karar.dev.domain.user.UserRepository;
import org.karar.dev.domain.user.company.CompanyUser;
import org.karar.dev.domain.user.regular.RegularUser;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    private final Map<Role, Function<RegisterRequest, User>> registrationStrategies = new HashMap<>();

    @PostConstruct
    public void init() {
        registrationStrategies.put(Role.USER, this::createRegularUser);
        registrationStrategies.put(Role.COMPANY, this::createCompanyUser);
    }

    @Transactional
    public BaseResponse<AuthResponse> register(RegisterRequest request) {
        checkEmailUniqueness(request.email());

        Function<RegisterRequest, User> strategy = registrationStrategies.get(request.role());
        if (strategy == null) {
            return BaseResponse.error(
                    "UnsupportedRole",
                    ExceptionMessages.UNSUPPORTED_ROLE.format(request.role()),
                    HttpStatus.BAD_REQUEST);
        }

        User user = strategy.apply(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEmailVerified(true); // TODO: Set to false when email verification is implemented
        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(
                savedUser.getEmail(),
                savedUser.getId(),
                savedUser.getRole().name());

        log.info("Generated JWT token for user: {}", savedUser.getEmail());
        AuthResponse authResponse = mapToAuthResponse(savedUser, token);
        return BaseResponse.success(authResponse, HttpStatus.CREATED);
    }

    public BaseResponse<AuthResponse> login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));

            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
            User user = userRepository.findByEmail(securityUser.getUsername())
                    .orElseThrow(() -> new BadCredentialsException(
                            ExceptionMessages.INVALID_CREDENTIALS.getMessage()));

            String token = jwtService.generateToken(
                    user.getEmail(),
                    user.getId(),
                    user.getRole().name());
            log.info("Generated JWT token for user: {}", user.getEmail());

            return BaseResponse.success(mapToAuthResponse(user, token));

        } catch (BadCredentialsException e) {
            return BaseResponse.error(
                    "InvalidCredentials",
                    ExceptionMessages.INVALID_CREDENTIALS.getMessage(),
                    HttpStatus.UNAUTHORIZED);
        } catch (LockedException e) {
            long remainingMinutes = getRemainingLockMinutes(request.email());
            return BaseResponse.error(
                    "AccountLocked",
                    ExceptionMessages.ACCOUNT_LOCKED.format(remainingMinutes),
                    HttpStatus.FORBIDDEN);
        } catch (DisabledException e) {
            return BaseResponse.error(
                    "AccountDisabled",
                    ExceptionMessages.ACCOUNT_DISABLED.getMessage(),
                    HttpStatus.FORBIDDEN);
        }
    }

    private User createRegularUser(RegisterRequest request) {
        validateField(request.username(), "username",
                ExceptionMessages.USERNAME_REQUIRED.getMessage());
        return new RegularUser(request.email(), request.password(), request.username());
    }

    private User createCompanyUser(RegisterRequest request) {
        validateField(request.companyName(), "companyName",
                ExceptionMessages.COMPANY_NAME_REQUIRED.getMessage());
        return new CompanyUser(request.email(), request.password(), request.companyName());
    }

    private void checkEmailUniqueness(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }
    }

    private void validateField(String value, String fieldName, String message) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(Map.of(fieldName, message));
        }
    }

    private AuthResponse mapToAuthResponse(User user, String accessToken) {
        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                accessToken,
                null // Refresh token — will be implemented later
        );
    }

    private long getRemainingLockMinutes(String email) {
        return userRepository.findByEmail(email)
                .filter(u -> u.getLockedUntil() != null)
                .map(u -> java.time.Duration.between(java.time.LocalDateTime.now(), u.getLockedUntil()).toMinutes())
                .map(m -> Math.max(m, 1))
                .orElse(60L);
    }
}
