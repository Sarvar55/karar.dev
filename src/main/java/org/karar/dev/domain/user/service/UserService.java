package org.karar.dev.domain.user.service;
import org.karar.dev.domain.user.entity.User;
import org.karar.dev.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.karar.dev.common.exception.notfound.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public boolean existsById(UUID id) {
        log.debug("Checking if user exists by id: {}", id);
        return userRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public User getById(UUID id) {
        log.debug("Getting user by id: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }
}

