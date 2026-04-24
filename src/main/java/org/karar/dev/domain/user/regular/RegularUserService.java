package org.karar.dev.domain.user.regular;

import lombok.RequiredArgsConstructor;
import org.karar.dev.common.exception.dto.PageResponse;
import org.karar.dev.common.exception.notFound.ResourceNotFoundException;
import org.karar.dev.domain.base.BaseResponse;
import org.karar.dev.domain.tag.Tag;
import org.karar.dev.domain.tag.dto.TagResponse;
import org.karar.dev.domain.user.UserRepository;
import org.karar.dev.domain.user.regular.dto.RegularUserResponse;
import org.karar.dev.domain.user.regular.dto.RegularUserUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegularUserService {

    private final RegularUserRepository regularUserRepository;

    public BaseResponse<PageResponse<RegularUserResponse>> getAll(Pageable pageable) {
        Page<RegularUserResponse> responses = regularUserRepository.findAll(pageable)
                .map(this::mapToResponse);
        return BaseResponse.success(new PageResponse<>(responses));
    }

    @Transactional
    public BaseResponse<RegularUserResponse> update(UUID id, RegularUserUpdateRequest request) {
        RegularUser user = findOrThrow(id);

        user.setEmail(request.email());
        user.setUsername(request.username());

        RegularUser saved = regularUserRepository.save(user);
        return BaseResponse.success(mapToResponse(saved));
    }

    @Transactional(readOnly = true)
    public BaseResponse<RegularUserResponse> getUserById(UUID id) {
        RegularUser user = findOrThrow(id);
        return BaseResponse.success(mapToResponse(user));
    }


    @Transactional
    public BaseResponse<Void> delete(UUID id) {
        if (!regularUserRepository.existsById(id)) {
            throw new ResourceNotFoundException("RegularUser", "id", id);
        }
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
        return regularUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RegularUser", "id", id));
    }

    private RegularUserResponse mapToResponse(RegularUser user) {
        return new RegularUserResponse(user.getId(), user.getEmail(), user.getUsername());
    }
}
