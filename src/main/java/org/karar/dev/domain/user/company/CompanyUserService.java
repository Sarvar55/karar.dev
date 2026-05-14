package org.karar.dev.domain.user.company;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.karar.dev.common.exception.dto.PageResponse;
import org.karar.dev.common.exception.notFound.ResourceNotFoundException;
import org.karar.dev.domain.base.BaseResponse;
import org.karar.dev.domain.user.UserService;
import org.karar.dev.domain.user.company.dto.CompanyUserResponse;
import org.karar.dev.domain.user.company.dto.CompanyUserUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyUserService {

    private final CompanyUserRepository companyUserRepository;
    private final UserService userService;

    public BaseResponse<PageResponse<CompanyUserResponse>> getAll(org.springframework.data.domain.Pageable pageable) {
        log.debug("Getting all company users");
        org.springframework.data.domain.Page<CompanyUserResponse> responses = companyUserRepository.findAll(pageable)
                .map(this::mapToResponse);
        log.debug("All company users retrieved successfully");
        return BaseResponse.success(new PageResponse<>(responses));
    }

    public BaseResponse<CompanyUserResponse> getCompanyById(UUID id) {
        log.debug("Getting company by id: {}", id);
        CompanyUser company = findOrThrow(id);
        log.debug("Company retrieved successfully: {}", id);
        return BaseResponse.success(mapToResponse(company));
    }

    @Transactional
    public BaseResponse<CompanyUserResponse> update(UUID id, CompanyUserUpdateRequest request) {
        log.debug("Updating company: {}", id);
        CompanyUser company = findOrThrow(id);

        company.setEmail(request.email());
        company.setCompanyName(request.companyName());

        CompanyUser saved = companyUserRepository.save(company);
        log.info("Company updated successfully: {}", id);
        return BaseResponse.success(mapToResponse(saved));
    }

    @Transactional
    public BaseResponse<Void> delete(UUID id) {
        if (!companyUserRepository.existsById(id)) {
            log.warn("Company not found: {}", id);
            throw new ResourceNotFoundException("Company", "id", id);
        }
        log.info("Company deleted successfully: {}", id);
        companyUserRepository.deleteById(id);
        return BaseResponse.success(null, HttpStatus.NO_CONTENT);
    }

    private CompanyUser findOrThrow(UUID id) {
        return companyUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));
    }

    private CompanyUserResponse mapToResponse(CompanyUser company) {
        return new CompanyUserResponse(company.getId(), company.getEmail(), company.getCompanyName());
    }
}
