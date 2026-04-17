package org.karar.dev.domain.user.company;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.karar.dev.domain.user.company.dto.CompanyUserResponse;
import org.karar.dev.domain.user.company.dto.CompanyUserUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
@Tag(name = "Company Management", description = "CRUD operations for company users")
public class CompanyUserController {

    private final CompanyUserService companyUserService;

    @GetMapping
    @Operation(summary = "Get all company users")
    public List<CompanyUserResponse> getAll() {
        return companyUserService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get company user by ID")
    public CompanyUserResponse getById(@PathVariable UUID id) {
        return companyUserService.getById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update company user")
    public CompanyUserResponse update(@PathVariable UUID id, @Valid @RequestBody CompanyUserUpdateRequest request) {
        return companyUserService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete company user")
    public void delete(@PathVariable UUID id) {
        companyUserService.delete(id);
    }
}
