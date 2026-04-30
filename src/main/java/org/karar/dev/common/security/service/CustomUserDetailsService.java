package org.karar.dev.common.security.service;

import lombok.RequiredArgsConstructor;
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
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getUser(username);
        return new SecurityUser(user);
    }

    private User getUser(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException
                        (ExceptionMessages.RESOURCE_NOT_FOUND.format("User", "username", username)));
    }
}
