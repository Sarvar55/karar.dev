package org.karar.dev.domain.vote;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.karar.dev.domain.base.BaseResponse;
import org.karar.dev.domain.vote.dto.VoteCountResponse;
import org.karar.dev.domain.vote.dto.VoteRequest;
import org.karar.dev.domain.vote.dto.VoteResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/votes")
@RequiredArgsConstructor
@Tag(name = "Vote Management", description = "Operations for voting on decisions")
public class VoteController {

    private final VoteService voteService;

    @Operation(
            summary = "Get all votes",
            description = "Retrieve a list of all votes in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved all votes",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<BaseResponse<List<VoteResponse>>> getAllVotes() {
        BaseResponse<List<VoteResponse>> response = voteService.getAllVotes();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(
            summary = "Get vote by ID",
            description = "Retrieve a specific vote by its unique identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Vote found successfully",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Vote not found",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<VoteResponse>> getVoteById(
            @Parameter(description = "UUID of the vote to retrieve", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {
        BaseResponse<VoteResponse> response = voteService.getVoteById(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(
            summary = "Get votes by decision ID",
            description = "Retrieve all votes cast on a specific decision"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved decision's votes",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Decision not found",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))
            )
    })
    @GetMapping("/decision/{decisionId}")
    public ResponseEntity<BaseResponse<List<VoteResponse>>> getVotesByDecisionId(
            @Parameter(description = "UUID of the decision", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID decisionId) {
        BaseResponse<List<VoteResponse>> response = voteService.getVotesByDecisionId(decisionId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(
            summary = "Get votes by user ID",
            description = "Retrieve all votes cast by a specific user"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved user's votes",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))
            )
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<BaseResponse<List<VoteResponse>>> getVotesByUserId(
            @Parameter(description = "UUID of the user", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID userId) {
        BaseResponse<List<VoteResponse>> response = voteService.getVotesByUserId(userId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(
            summary = "Get vote count for a decision",
            description = "Retrieve the total vote count and check if current user has voted"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved vote count",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Decision not found",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))
            )
    })
    @GetMapping("/decision/{decisionId}/count")
    public ResponseEntity<BaseResponse<VoteCountResponse>> getVoteCountByDecisionId(
            @Parameter(description = "UUID of the decision", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID decisionId,
            @Parameter(description = "UUID of the current user (optional)", example = "550e8400-e29b-41d4-a716-446655440001")
            @RequestParam(required = false) UUID currentUserId) {
        BaseResponse<VoteCountResponse> response = voteService.getVoteCountByDecisionId(decisionId, currentUserId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(
            summary = "Check if user has voted on a decision",
            description = "Verify whether a specific user has already voted on a specific decision"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully checked vote status",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User or decision not found",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))
            )
    })
    @GetMapping("/check")
    public ResponseEntity<BaseResponse<Boolean>> hasUserVoted(
            @Parameter(description = "UUID of the user", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @RequestParam UUID userId,
            @Parameter(description = "UUID of the decision", required = true, example = "550e8400-e29b-41d4-a716-446655440001")
            @RequestParam UUID decisionId) {
        BaseResponse<Boolean> response = voteService.hasUserVoted(userId, decisionId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(
            summary = "Cast a vote on a decision",
            description = "Create a new vote. A user can only vote once per decision."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Vote created successfully",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User or decision not found",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "User has already voted on this decision",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<BaseResponse<VoteResponse>> createVote(
            @Valid @RequestBody VoteRequest request) {
        BaseResponse<VoteResponse> response = voteService.createVote(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(
            summary = "Delete a vote by ID",
            description = "Remove a vote from the system by its ID. This will decrease the decision's vote count."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Vote deleted successfully",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Vote not found",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteVote(
            @Parameter(description = "UUID of the vote to delete", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {
        BaseResponse<Void> response = voteService.deleteVote(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(
            summary = "Delete a vote by user and decision",
            description = "Remove a vote using user ID and decision ID. Useful for 'unvote' functionality."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Vote deleted successfully",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User, decision, or vote not found",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))
            )
    })
    @DeleteMapping
    public ResponseEntity<BaseResponse<Void>> deleteVoteByUserAndDecision(
            @Parameter(description = "UUID of the user", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @RequestParam UUID userId,
            @Parameter(description = "UUID of the decision", required = true, example = "550e8400-e29b-41d4-a716-446655440001")
            @RequestParam UUID decisionId) {
        BaseResponse<Void> response = voteService.deleteVoteByUserAndDecision(userId, decisionId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
