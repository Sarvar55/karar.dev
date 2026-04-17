package org.karar.dev.domain.auth;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.karar.dev.common.enums.Role;
import org.karar.dev.common.exception.base.ValidationException;
import org.karar.dev.common.exception.conflict.EmailAlreadyExistsException;
import org.karar.dev.domain.auth.dto.AuthResponse;
import org.karar.dev.domain.auth.dto.RegisterRequest;
import org.karar.dev.domain.user.User;
import org.karar.dev.domain.user.UserRepository;
import org.karar.dev.domain.user.company.CompanyUser;
import org.karar.dev.domain.user.regular.RegularUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final Map<Role, Function<RegisterRequest, User>> registrationStrategies = new HashMap<>();

    @PostConstruct
    public void init() {
        registrationStrategies.put(Role.USER, this::createRegularUser);
        registrationStrategies.put(Role.COMPANY, this::createCompanyUser);
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        checkEmailUniqueness(request.email());

        Function<RegisterRequest, User> strategy = registrationStrategies.get(request.role());
        if (strategy == null) {
            throw new RuntimeException("Unsupported role: " + request.role());
        }

        User user = strategy.apply(request);
        User savedUser = userRepository.save(user);

        return mapToAuthResponse(savedUser);
    }

    private User createRegularUser(RegisterRequest request) {
        validateField(request.username(), "username", "Username is required for regular users");
        return new RegularUser(request.email(), request.password(), request.username());
    }

    private User createCompanyUser(RegisterRequest request) {
        validateField(request.companyName(), "companyName", "Company name is required for companies");
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

    private AuthResponse mapToAuthResponse(User user) {
        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                UUID.randomUUID().toString(), // Mock accessToken
                UUID.randomUUID().toString()  // Mock refreshToken
        );
    }
}
