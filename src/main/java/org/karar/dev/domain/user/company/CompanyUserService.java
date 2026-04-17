package org.karar.dev.domain.user.company;

import lombok.RequiredArgsConstructor;
import org.karar.dev.common.exception.notFound.ResourceNotFoundException;
import org.karar.dev.domain.user.UserRepository;
import org.karar.dev.domain.user.company.dto.CompanyUserResponse;
import org.karar.dev.domain.user.company.dto.CompanyUserUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyUserService {

    private final CompanyUserRepository companyUserRepository;
    private final UserRepository userRepository;

    public List<CompanyUserResponse> getAll() {
        return companyUserRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CompanyUserResponse getById(UUID id) {
        CompanyUser company = findOrThrow(id);
        return mapToResponse(company);
    }

    @Transactional
    public CompanyUserResponse update(UUID id, CompanyUserUpdateRequest request) {
        CompanyUser company = findOrThrow(id);

        company.setEmail(request.email());
        company.setCompanyName(request.companyName());

        CompanyUser saved = companyUserRepository.save(company);
        return mapToResponse(saved);
    }

    @Transactional
    public void delete(UUID id) {
        if (!companyUserRepository.existsById(id)) {
            throw new ResourceNotFoundException("Company", "id", id);
        }
        companyUserRepository.deleteById(id);
    }

    private CompanyUser findOrThrow(UUID id) {
        return companyUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));
    }

    private CompanyUserResponse mapToResponse(CompanyUser company) {
        return new CompanyUserResponse(company.getId(), company.getEmail(), company.getCompanyName());
    }
}
