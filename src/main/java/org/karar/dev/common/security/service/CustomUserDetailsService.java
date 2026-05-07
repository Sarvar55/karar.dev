package org.karar.dev.common.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.karar.dev.common.exception.ExceptionMessages;
import org.karar.dev.common.security.user.SecurityUser;
import org.karar.dev.domain.user.User;
import org.karar.dev.domain.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getUser(username);
        log.info("User loaded successfully: id={}, username={}", user.getId(), user.getEmail());
        return new SecurityUser(user);
    }

    private User getUser(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        ExceptionMessages.RESOURCE_NOT_FOUND.format("User", "username", username)));
    }
}
