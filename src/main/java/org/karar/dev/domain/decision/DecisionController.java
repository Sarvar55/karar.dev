package org.karar.dev.domain.decision;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.karar.dev.common.enums.RegretLevel;
import org.karar.dev.common.exception.dto.PageResponse;
import org.karar.dev.domain.base.BaseResponse;
import org.karar.dev.domain.comment.dto.CommentResponse;
import org.karar.dev.domain.decision.dto.DecisionRequest;
import org.karar.dev.domain.decision.dto.DecisionResponse;
import org.karar.dev.domain.decision.dto.DecisionUpdateRequest;
import org.karar.dev.domain.decisiontag.DecisionTagService;
import org.karar.dev.domain.tag.dto.TagResponse;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/decisions")
@RequiredArgsConstructor
@Tag(name = "Decision Management", description = "RESTful API for Decision resources")
public class DecisionController {

        private final DecisionService decisionService;
        private final DecisionCommentService decisionCommentService;
        private final DecisionTagService decisionTagService;

        @GetMapping(value = "/{decisionId}/comments", produces = "application/vnd.karar.dev+json;v=1.0")
        @Operation(summary = "List comments for a decision", description = "Retrieve paginated comments for a specific decision")
        public ResponseEntity<BaseResponse<PageResponse<CommentResponse>>> getDecisionComments(
                        @Parameter(description = "Decision UUID", required = true) @PathVariable UUID decisionId,
                        @ParameterObject @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
                return ResponseEntity.ok(decisionCommentService.getCommentsByDecisionId(decisionId, pageable));
        }

        @GetMapping(value = "/{decisionId}/tags", produces = "application/vnd.karar.dev+json;v=1.0")
        @Operation(summary = "List tags for a decision", description = "Retrieve all tags associated with a specific decision")
        public ResponseEntity<BaseResponse<List<TagResponse>>> getDecisionTags(
                        @Parameter(description = "Decision UUID", required = true) @PathVariable UUID decisionId) {
                return ResponseEntity.ok(decisionTagService.getTagsByDecisionId(decisionId));
        }

        @Operation(summary = "List decisions", description = "Retrieve paginated decisions with optional filtering. Examples:\n"
                        +
                        "- /api/decisions?page=0&size=10&sort=createdAt,desc\n" +
                        "- /api/decisions?userId={id}\n" +
                        "- /api/decisions?regretLevel=HIGH\n" +
                        "- /api/decisions?tagId={id}")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved decisions", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
        })
        @GetMapping(produces = "application/vnd.karar.dev+json;v=1.0")
        public ResponseEntity<BaseResponse<PageResponse<DecisionResponse>>> getDecisions(
                        @Parameter(description = "Filter by user ID") @RequestParam(required = false) UUID userId,
                        @Parameter(description = "Filter by regret level") @RequestParam(required = false) RegretLevel regretLevel,
                        @Parameter(description = "Filter by tag ID") @RequestParam(required = false) UUID tagId,
                        @ParameterObject @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
                BaseResponse<PageResponse<DecisionResponse>> response;
                if (userId != null) {
                        response = decisionService.getDecisionsByUserId(userId, pageable);
                } else if (regretLevel != null) {
                        response = decisionService.getDecisionsByRegretLevel(regretLevel, pageable);
                } else if (tagId != null) {
                        response = decisionService.getDecisionsByTagId(tagId, pageable);
                } else {
                        response = decisionService.getAllDecisions(pageable);
                }
                return ResponseEntity.status(response.getStatus()).body(response);
        }

        @Operation(summary = "Get decision by ID", description = "Retrieve a specific decision by its unique identifier")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Decision found successfully", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Decision not found", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
        })
        @GetMapping(value = "/{id}", produces = "application/vnd.karar.dev+json;v=1.0")
        public ResponseEntity<BaseResponse<DecisionResponse>> getDecisionById(
                        @Parameter(description = "UUID of the decision to retrieve", required = true) @PathVariable UUID id) {
                BaseResponse<DecisionResponse> response = decisionService.getDecisionById(id);
                return ResponseEntity.status(response.getStatus()).body(response);
        }

        @Operation(summary = "Create a new decision", description = "Create a new decision with title, reasoning, and regret level")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Decision created successfully", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
                        @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
                        @ApiResponse(responseCode = "409", description = "Decision with this title already exists for this user", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
        })
        @PostMapping(produces = "application/vnd.karar.dev+json;v=1.0")
        public ResponseEntity<BaseResponse<DecisionResponse>> createDecision(
                        @Valid @RequestBody DecisionRequest request) {
                BaseResponse<DecisionResponse> response = decisionService.createDecision(request);
                return ResponseEntity.status(response.getStatus()).body(response);
        }

        @Operation(summary = "Update an existing decision", description = "Update the details of an existing decision by its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Decision updated successfully", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Decision not found", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
                        @ApiResponse(responseCode = "409", description = "Decision with this title already exists for this user", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
        })
        @PutMapping(value = "/{id}", produces = "application/vnd.karar.dev+json;v=1.0")
        @PreAuthorize("@securityService.isAdminOrOwnerOfDecision(authentication, #id)")
        public ResponseEntity<BaseResponse<DecisionResponse>> updateDecision(
                        @Parameter(description = "UUID of the decision to update", required = true, example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID id,
                        @Valid @RequestBody DecisionUpdateRequest request) {
                BaseResponse<DecisionResponse> response = decisionService.updateDecision(id, request);
                return ResponseEntity.status(response.getStatus()).body(response);
        }

        @Operation(summary = "Delete a decision", description = "Remove a decision from the system by its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Decision deleted successfully", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Decision not found", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
        })
        @DeleteMapping(value = "/{id}", produces = "application/vnd.karar.dev+json;v=1.0")
        @PreAuthorize("@securityService.isAdminOrOwnerOfDecision(authentication, #id)")
        public ResponseEntity<BaseResponse<Void>> deleteDecision(
                        @Parameter(description = "UUID of the decision to delete", required = true, example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID id) {
                BaseResponse<Void> response = decisionService.deleteDecision(id);
                return ResponseEntity.status(response.getStatus()).body(response);
        }
}
