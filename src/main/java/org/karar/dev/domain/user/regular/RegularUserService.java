package org.karar.dev.domain.user.regular;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.karar.dev.common.exception.dto.PageResponse;
import org.karar.dev.common.exception.notFound.ResourceNotFoundException;
import org.karar.dev.domain.base.BaseResponse;
import org.karar.dev.domain.user.regular.dto.RegularUserResponse;
import org.karar.dev.domain.user.regular.dto.RegularUserUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegularUserService {

    private final RegularUserRepository regularUserRepository;

    public BaseResponse<PageResponse<RegularUserResponse>> getAll(Pageable pageable) {
        log.debug("Getting all regular users");
        Page<RegularUserResponse> responses = regularUserRepository.findAll(pageable)
                .map(this::mapToResponse);
        log.debug("All regular users retrieved successfully");
        return BaseResponse.success(new PageResponse<>(responses));
    }

    @Transactional
    public BaseResponse<RegularUserResponse> update(UUID id, RegularUserUpdateRequest request) {
        log.debug("Updating regular user: {}", id);
        RegularUser user = findOrThrow(id);

        user.setEmail(request.email());
        user.setUsername(request.username());

        RegularUser saved = regularUserRepository.save(user);
        log.info("Regular user updated successfully: {}", id);
        return BaseResponse.success(mapToResponse(saved));
    }

    @Transactional(readOnly = true)
    public BaseResponse<RegularUserResponse> getUserById(UUID id) {
        log.debug("Getting regular user by id: {}", id);
        RegularUser user = findOrThrow(id);
        log.debug("Regular user retrieved successfully: {}", id);
        return BaseResponse.success(mapToResponse(user));
    }

    @Transactional
    public BaseResponse<Void> delete(UUID id) {
        if (!regularUserRepository.existsById(id)) {
            log.warn("Regular user not found: {}", id);
            throw new ResourceNotFoundException("RegularUser", "id", id);
        }
        log.info("Regular user deleted successfully: {}", id);
        regularUserRepository.deleteById(id);
        return BaseResponse.success(null, org.springframework.http.HttpStatus.NO_CONTENT);
    }

    public boolean existsById(UUID id) {
        return regularUserRepository.existsById(id);
    }

    public RegularUser getById(UUID id) {
        return findOrThrow(id);
    }

    private RegularUser findOrThrow(UUID id) {
        log.debug("Finding regular user by id: {}", id);
        return regularUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RegularUser", "id", id));
    }

    private RegularUserResponse mapToResponse(RegularUser user) {
        log.debug("Mapping regular user to response: {}", user);
        return new RegularUserResponse(user.getId(), user.getEmail(), user.getUsername());
    }
}
