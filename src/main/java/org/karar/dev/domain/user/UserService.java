package org.karar.dev.domain.user;

import lombok.RequiredArgsConstructor;
import org.karar.dev.common.exception.ResourceNotFoundException;
import org.karar.dev.domain.user.dto.UserCreateRequest;
import org.karar.dev.domain.user.dto.UserResponse;
import org.karar.dev.domain.user.dto.UserUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return mapToResponse(user);
    }

    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        User user = new User();
        user.setUsername(request.username());
        user.setPassword(request.password()); // In a real app, password should be encoded
        user.setEmail(request.email());
        user.setAnonymous(request.anonymous());

        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    @Transactional
    public UserResponse updateUser(UUID id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setAnonymous(request.anonymous());

        User updatedUser = userRepository.save(user);
        return mapToResponse(updatedUser);
    }

    @Transactional
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userRepository.deleteById(id);
    }


    public boolean isUserExists(String email) {
        if(userRepository.existsByEmail(email).isPresent()){
            throw new
        }
    }

    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.isAnonymous()
        );
    }
}
