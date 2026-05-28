package org.karar.dev.domain.vote.controller;
import org.karar.dev.domain.vote.service.VoteService;

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
import org.karar.dev.domain.vote.dto.VoteCountResponse;
import org.karar.dev.domain.vote.dto.VoteRequest;
import org.karar.dev.domain.vote.dto.VoteResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/votes")
@RequiredArgsConstructor
@Tag(name = "Vote Management", description = "Operations for voting on decisions")
public class VoteController {

        private final VoteService voteService;

        @Operation(summary = "List votes", description = "Retrieve votes with optional filtering. Examples:\n" +
                        "- /api/votes\n" +
                        "- /api/votes?decisionId={id}\n" +
                        "- /api/votes?userId={id}")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved votes", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
        })
        @GetMapping(produces = "application/vnd.karar.dev+json;v=1.0")
        public ResponseEntity<BaseResponse<List<VoteResponse>>> getVotes(
                        @Parameter(description = "Filter by decision ID") @RequestParam(required = false) UUID decisionId,
                        @Parameter(description = "Filter by user ID") @RequestParam(required = false) UUID userId) {
                BaseResponse<List<VoteResponse>> response;
                if (decisionId != null) {
                        response = voteService.getVotesByDecisionId(decisionId);
                } else if (userId != null) {
                        response = voteService.getVotesByUserId(userId);
                } else {
                        response = voteService.getAllVotes();
                }
                return ResponseEntity.status(response.getStatus()).body(response);
        }

        @Operation(summary = "Get vote by ID", description = "Retrieve a specific vote by its unique identifier")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Vote found successfully", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Vote not found", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
        })
        @GetMapping(value = "/{id}", produces = "application/vnd.karar.dev+json;v=1.0")
        public ResponseEntity<BaseResponse<VoteResponse>> getVoteById(
                        @Parameter(description = "UUID of the vote to retrieve", required = true) @PathVariable UUID id) {
                BaseResponse<VoteResponse> response = voteService.getVoteById(id);
                return ResponseEntity.status(response.getStatus()).body(response);
        }

        @GetMapping(value = "/decisions/{decisionId}/count", produces = "application/vnd.karar.dev+json;v=1.0")
        @Operation(summary = "Get vote count for a decision", description = "Get vote count and user's vote status for a decision")
        public ResponseEntity<BaseResponse<VoteCountResponse>> getVoteCountByDecisionId(
                        @Parameter(description = "Decision UUID", required = true) @PathVariable UUID decisionId,
                        @Parameter(description = "Current user ID for vote status", required = false) @RequestParam(required = false) UUID currentUserId) {
                BaseResponse<VoteCountResponse> response = voteService.getVoteCountByDecisionId(decisionId,
                                currentUserId);
                return ResponseEntity.ok(response);
        }

        @Operation(summary = "Cast a vote on a decision", description = "Create a new vote. A user can only vote once per decision.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Vote created successfully", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
                        @ApiResponse(responseCode = "404", description = "User or decision not found", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
                        @ApiResponse(responseCode = "409", description = "User has already voted on this decision", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
        })
        @PostMapping(produces = "application/vnd.karar.dev+json;v=1.0")
        public ResponseEntity<BaseResponse<VoteResponse>> createVote(
                        @Valid @RequestBody VoteRequest request) {
                BaseResponse<VoteResponse> response = voteService.createVote(request);
                return ResponseEntity.status(response.getStatus()).body(response);
        }

        @Operation(summary = "Delete a vote by ID", description = "Remove a vote from the system by its ID. This will decrease the decision's vote count.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Vote deleted successfully", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Vote not found", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
        })
        @DeleteMapping(value = "/{id}", produces = "application/vnd.karar.dev+json;v=1.0")
        @PreAuthorize("@securityService.isOwnerOfVote(authentication, #id)")
        public ResponseEntity<BaseResponse<Void>> deleteVote(
                        @Parameter(description = "UUID of the vote to delete", required = true) @PathVariable UUID id) {
                BaseResponse<Void> response = voteService.deleteVote(id);
                return ResponseEntity.status(response.getStatus()).body(response);
        }

        @Operation(summary = "Delete a vote by user and decision", description = "Remove a vote using user ID and decision ID. Useful for 'unvote' functionality.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Vote deleted successfully", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
                        @ApiResponse(responseCode = "404", description = "User, decision, or vote not found", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
        })
        @DeleteMapping(value = "/users/{userId}/decisions/{decisionId}", produces = "application/vnd.karar.dev+json;v=1.0")
        @PreAuthorize("@securityService.isOwnerOfVoteByUserAndDecision(authentication, #userId)")
        public ResponseEntity<BaseResponse<Void>> deleteVoteByUserAndDecision(
                        @Parameter(description = "UUID of the user", required = true) @PathVariable UUID userId,
                        @Parameter(description = "UUID of the decision", required = true) @PathVariable UUID decisionId) {
                BaseResponse<Void> response = voteService.deleteVoteByUserAndDecision(userId, decisionId);
                return ResponseEntity.status(response.getStatus()).body(response);
        }
}
