package org.karar.dev.domain.tag.controller;
import org.karar.dev.domain.tag.service.TagService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.karar.dev.common.dto.BaseResponse;
import org.karar.dev.domain.tag.dto.TagRequest;
import org.karar.dev.domain.tag.dto.TagResponse;
import org.karar.dev.domain.tag.dto.TagUpdateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
@Tag(name = "Tag Management", description = "CRUD operations for tags")
public class TagController {

    private final TagService tagService;

    @Operation(
            summary = "Get all tags",
            description = "Retrieve a list of all tags in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved all tags",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))
            )
    })
    @GetMapping(produces = "application/vnd.karar.dev+json;v=1.0")
    public ResponseEntity<BaseResponse<List<TagResponse>>> getAllTags() {
        BaseResponse<List<TagResponse>> response = tagService.getAllTags();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(
            summary = "Get tag by ID",
            description = "Retrieve a specific tag by its unique identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tag found successfully",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Tag not found",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))
            )
    })
    @GetMapping(value = "/{id}", produces = "application/vnd.karar.dev+json;v=1.0")
    public ResponseEntity<BaseResponse<TagResponse>> getTagById(
            @Parameter(description = "UUID of the tag to retrieve", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {
        BaseResponse<TagResponse> response = tagService.getTagById(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(
            summary = "Get tag by name",
            description = "Retrieve a specific tag by its name"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tag found successfully",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Tag not found",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))
            )
    })
    @GetMapping(value = "/name/{name}", produces = "application/vnd.karar.dev+json;v=1.0")
    public ResponseEntity<BaseResponse<TagResponse>> getTagByName(
            @Parameter(description = "Name of the tag", required = true, example = "programming")
            @PathVariable String name) {
        BaseResponse<TagResponse> response = tagService.getTagByName(name);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(
            summary = "Create a new tag",
            description = "Create a new tag with a unique name"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Tag created successfully",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Tag with this name already exists",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))
            )
    })
    @PostMapping(produces = "application/vnd.karar.dev+json;v=1.0")
    public ResponseEntity<BaseResponse<TagResponse>> createTag(
            @Valid @RequestBody TagRequest request) {
        BaseResponse<TagResponse> response = tagService.createTag(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(
            summary = "Update an existing tag",
            description = "Update the name of an existing tag"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tag updated successfully",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Tag not found",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Tag with this name already exists",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))
            )
    })
    @PutMapping(value = "/{id}", produces = "application/vnd.karar.dev+json;v=1.0")
    public ResponseEntity<BaseResponse<TagResponse>> updateTag(
            @Parameter(description = "UUID of the tag to update", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id,
            @Valid @RequestBody TagUpdateRequest request) {
        BaseResponse<TagResponse> response = tagService.updateTag(id, request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(
            summary = "Delete a tag",
            description = "Remove a tag from the system by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Tag deleted successfully",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Tag not found",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))
            )
    })
    @DeleteMapping(value = "/{id}", produces = "application/vnd.karar.dev+json;v=1.0")
    public ResponseEntity<BaseResponse<Void>> deleteTag(
            @Parameter(description = "UUID of the tag to delete", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {
        BaseResponse<Void> response = tagService.deleteTag(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}

