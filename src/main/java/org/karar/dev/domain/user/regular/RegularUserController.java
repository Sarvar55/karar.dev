package org.karar.dev.domain.user.regular;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.karar.dev.domain.user.regular.dto.RegularUserResponse;
import org.karar.dev.domain.user.regular.dto.RegularUserUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Regular User Management", description = "CRUD operations for regular users")
public class RegularUserController {

    private final RegularUserService regularUserService;

    @GetMapping
    @Operation(summary = "Get all regular users")
    public List<RegularUserResponse> getAll() {
        return regularUserService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get regular user by ID")
    public RegularUserResponse getById(@PathVariable UUID id) {
        return regularUserService.getById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update regular user")
    public RegularUserResponse update(@PathVariable UUID id, @Valid @RequestBody RegularUserUpdateRequest request) {
        return regularUserService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete regular user")
    public void delete(@PathVariable UUID id) {
        regularUserService.delete(id);
    }
}
