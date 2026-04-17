package org.karar.dev.domain.user.regular;

import lombok.RequiredArgsConstructor;
import org.karar.dev.common.exception.notFound.ResourceNotFoundException;
import org.karar.dev.domain.user.UserRepository;
import org.karar.dev.domain.user.regular.dto.RegularUserResponse;
import org.karar.dev.domain.user.regular.dto.RegularUserUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegularUserService {

    private final RegularUserRepository regularUserRepository;

    public List<RegularUserResponse> getAll() {
        return regularUserRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public RegularUserResponse getById(UUID id) {
        RegularUser user = findOrThrow(id);
        return mapToResponse(user);
    }

    @Transactional
    public RegularUserResponse update(UUID id, RegularUserUpdateRequest request) {
        RegularUser user = findOrThrow(id);

        user.setEmail(request.email());
        user.setUsername(request.username());

        RegularUser saved = regularUserRepository.save(user);
        return mapToResponse(saved);
    }

    @Transactional
    public void delete(UUID id) {
        if (!regularUserRepository.existsById(id)) {
            throw new ResourceNotFoundException("RegularUser", "id", id);
        }
        regularUserRepository.deleteById(id);
    }

    private RegularUser findOrThrow(UUID id) {
        return regularUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RegularUser", "id", id));
    }

    private RegularUserResponse mapToResponse(RegularUser user) {
        return new RegularUserResponse(user.getId(), user.getEmail(), user.getUsername());
    }
}
