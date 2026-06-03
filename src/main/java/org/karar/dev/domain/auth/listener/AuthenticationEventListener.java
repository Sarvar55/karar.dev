package org.karar.dev.domain.auth.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.karar.dev.domain.user.entity.User;
import org.karar.dev.domain.user.repository.UserRepository;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationEventListener {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 60;

    private final UserRepository userRepository;

    @EventListener
    @Transactional
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
        String email = extractEmail(event.getAuthentication().getPrincipal());
        if (email == null) return;

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            log.warn("Failed login attempt for non-existent email: {}", email);
            return;
        }

        User user = userOpt.get();
        int newAttempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(newAttempts);

        if (newAttempts >= MAX_FAILED_ATTEMPTS) {
            user.setAccountLocked(true);
            user.setLockedUntil(LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES));
            log.warn("Account locked for user '{}' after {} failed attempts. Locked until: {}",
                    email, newAttempts, user.getLockedUntil());
        } else {
            log.info("Failed login attempt {} of {} for user '{}'",
                    newAttempts, MAX_FAILED_ATTEMPTS, email);
        }

        userRepository.save(user);
    }

    @EventListener
    @Transactional
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        String email = extractEmail(event.getAuthentication().getPrincipal());
        if (email == null) return;

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return;

        User user = userOpt.get();

        if (user.getFailedLoginAttempts() > 0 || user.getLockedUntil() != null) {
            user.setFailedLoginAttempts(0);
            user.setAccountLocked(false);
            user.setLockedUntil(null);
            userRepository.save(user);
            log.info("Login successful. Reset failed attempts for user '{}'", email);
        }
    }

    private String extractEmail(Object principal) {
        if (principal instanceof String email) {
            return email;
        }
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
            return userDetails.getUsername();
        }
        return null;
    }
}

