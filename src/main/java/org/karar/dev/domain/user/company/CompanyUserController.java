package org.karar.dev.domain.user.company;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.karar.dev.common.exception.dto.PageResponse;
import org.karar.dev.domain.base.BaseResponse;
import org.karar.dev.domain.user.company.dto.CompanyUserResponse;
import org.karar.dev.domain.user.company.dto.CompanyUserUpdateRequest;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
@Tag(name = "Company Management", description = "CRUD operations for company users")
public class CompanyUserController {

    private final CompanyUserService companyUserService;

    @GetMapping(produces = "application/vnd.karar.dev+json;v=1.0")
    @Operation(summary = "Get all company users")
    public ResponseEntity<BaseResponse<PageResponse<CompanyUserResponse>>> getAll(
            @ParameterObject @PageableDefault(value = 5, sort = "createdAt") Pageable pageable) {
        BaseResponse<PageResponse<CompanyUserResponse>> response = companyUserService.getAll(pageable);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping(value = "/{id}", produces = "application/vnd.karar.dev+json;v=1.0")
    @Operation(summary = "Get company user by ID")
    public ResponseEntity<BaseResponse<CompanyUserResponse>> getById(@PathVariable UUID id) {
        BaseResponse<CompanyUserResponse> response = companyUserService.getCompanyById(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping(value = "/{id}", produces = "application/vnd.karar.dev+json;v=1.0")
    @Operation(summary = "Update company user")
    @PreAuthorize("@securityService.isAdminOrSelf(authentication, #id)")
    public ResponseEntity<BaseResponse<CompanyUserResponse>> update(@PathVariable UUID id,
            @Valid @RequestBody CompanyUserUpdateRequest request) {
        BaseResponse<CompanyUserResponse> response = companyUserService.update(id, request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping(value = "/{id}", produces = "application/vnd.karar.dev+json;v=1.0")
    @Operation(summary = "Delete company user")
    @PreAuthorize("@securityService.isAdminOrSelf(authentication, #id)")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        BaseResponse<Void> response = companyUserService.delete(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
