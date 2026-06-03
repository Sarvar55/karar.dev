package org.karar.dev.domain.vote.service;
import org.karar.dev.domain.vote.entity.Vote;
import org.karar.dev.domain.vote.repository.VoteRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.karar.dev.common.audit.AuditAction;
import org.karar.dev.common.audit.Auditable;
import org.karar.dev.common.exception.conflict.ConflictException;
import org.karar.dev.common.exception.notfound.ResourceNotFoundException;
import org.karar.dev.common.security.service.SecurityService;
import org.karar.dev.common.dto.BaseResponse;
import org.karar.dev.domain.decision.entity.Decision;
import org.karar.dev.domain.decision.service.DecisionService;
import org.karar.dev.domain.user.regular.entity.RegularUser;
import org.karar.dev.domain.user.regular.service.RegularUserService;
import org.karar.dev.domain.vote.dto.VoteCountResponse;
import org.karar.dev.domain.vote.dto.VoteRequest;
import org.karar.dev.domain.vote.dto.VoteResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoteService {

    private final VoteRepository voteRepository;
    private final RegularUserService regularUserService;
    private final DecisionService decisionService;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public BaseResponse<List<VoteResponse>> getAllVotes() {
        log.debug("Getting all votes");
        List<Vote> votes = voteRepository.findAll();
        List<VoteResponse> responseList = votes.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        log.debug("All votes retrieved successfully");
        return BaseResponse.success(responseList);
    }

    @Transactional(readOnly = true)
    public BaseResponse<VoteResponse> getVoteById(UUID id) {
        log.debug("Getting vote by id: {}", id);
        Vote vote = findVoteOrThrow(id);
        log.debug("Vote retrieved successfully: {}", id);
        return BaseResponse.success(mapToResponse(vote));
    }

    @Transactional(readOnly = true)
    public BaseResponse<VoteCountResponse> getVoteCountByDecisionId(UUID decisionId, UUID currentUserId) {
        log.debug("Getting vote count for decision by id: {}", decisionId);
        if (!decisionService.existsById(decisionId)) {
            log.warn("Decision not found: {}", decisionId);
            throw new ResourceNotFoundException("Decision", "id", decisionId);
        }
        long count = voteRepository.countByDecisionId(decisionId);
        boolean hasVoted = currentUserId != null &&
                voteRepository.existsByUserIdAndDecisionId(currentUserId, decisionId);
        log.debug("Vote count retrieved successfully: {}", count);
        return BaseResponse.success(new VoteCountResponse(decisionId, count, hasVoted));
    }

    @Transactional(readOnly = true)
    public BaseResponse<List<VoteResponse>> getVotesByDecisionId(UUID decisionId) {
        if (!decisionService.existsById(decisionId)) {
            log.warn("Decision not found: {}", decisionId);
            throw new ResourceNotFoundException("Decision", "id", decisionId);
        }
        List<Vote> votes = voteRepository.findByDecisionId(decisionId);
        List<VoteResponse> responseList = votes.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        log.debug("Votes retrieved successfully: {}", decisionId);
        return BaseResponse.success(responseList);
    }

    @Transactional(readOnly = true)
    public BaseResponse<List<VoteResponse>> getVotesByUserId(UUID userId) {
        log.debug("Getting votes for user by id: {}", userId);
        if (!regularUserService.existsById(userId)) {
            log.warn("User not found: {}", userId);
            throw new ResourceNotFoundException("User", "id", userId);
        }
        List<Vote> votes = voteRepository.findByUserId(userId);
        List<VoteResponse> responseList = votes.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        log.debug("Votes retrieved successfully: {}", userId);
        return BaseResponse.success(responseList);
    }

    @Transactional(readOnly = true)
    public BaseResponse<Boolean> hasUserVoted(UUID userId, UUID decisionId) {
        log.debug("Checking if user has voted: {}", userId);
        if (!regularUserService.existsById(userId)) {
            log.warn("User not found: {}", userId);
            throw new ResourceNotFoundException("User", "id", userId);
        }
        if (!decisionService.existsById(decisionId)) {
            log.warn("Decision not found: {}", decisionId);
            throw new ResourceNotFoundException("Decision", "id", decisionId);
        }
        log.debug("User has voted successfully: {}", userId);
        boolean hasVoted = voteRepository.existsByUserIdAndDecisionId(userId, decisionId);
        return BaseResponse.success(hasVoted);
    }

    @Transactional
    @Auditable(action = AuditAction.CREATE, entityName = "Vote")
    public BaseResponse<VoteResponse> createVote(VoteRequest request) {
        UUID currentUserId = securityService.getCurrentUserId();
        log.debug("Creating vote for authenticated user: {}", currentUserId);

        RegularUser user = regularUserService.getById(currentUserId);
        Decision decision = decisionService.getById(request.decisionId());

        if (voteRepository.existsByUserIdAndDecisionId(currentUserId, request.decisionId())) {
            log.warn("User {} has already voted on decision {}", currentUserId, request.decisionId());
            throw new ConflictException("User has already voted on this decision");
        }

        Vote vote = Vote.builder()
                .user(user)
                .decision(decision)
                .build();

        Vote savedVote = voteRepository.saveAndFlush(vote);

        decisionService.incrementVoteCount(request.decisionId());

        log.info("Vote created successfully: {}", savedVote.getId());
        return BaseResponse.success(mapToResponse(savedVote), HttpStatus.CREATED);
    }

    @Transactional
    @Auditable(action = AuditAction.DELETE, entityName = "Vote")
    public BaseResponse<Void> deleteVote(UUID id) {
        log.debug("Deleting vote: {}", id);
        Vote vote = findVoteOrThrow(id);

        if (vote.getDecision() != null) {
            decisionService.decrementVoteCount(vote.getDecision().getId());
        }

        voteRepository.deleteById(id);
        log.debug("Vote deleted successfully: {}", id);
        return BaseResponse.success(null, HttpStatus.NO_CONTENT);
    }

    @Transactional
    @Auditable(action = AuditAction.DELETE, entityName = "Vote")
    public BaseResponse<Void> deleteVoteByUserAndDecision(UUID userId, UUID decisionId) {
        log.debug("Deleting vote by user and decision: {}, {}", userId, decisionId);
        if (!regularUserService.existsById(userId)) {
            log.warn("User not found: {}", userId);
            throw new ResourceNotFoundException("User", "id", userId);
        }
        if (!decisionService.existsById(decisionId)) {
            log.warn("Decision not found: {}", decisionId);
            throw new ResourceNotFoundException("Decision", "id", decisionId);
        }

        Vote vote = voteRepository.findByUserIdAndDecisionId(userId, decisionId)
                .orElseThrow(() -> new ResourceNotFoundException("Vote", "userId and decisionId",
                        "userId=" + userId + ", decisionId=" + decisionId));

        decisionService.decrementVoteCount(decisionId);

        voteRepository.delete(vote);
        return BaseResponse.success(null, HttpStatus.NO_CONTENT);
    }

    private Vote findVoteOrThrow(UUID id) {
        return voteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vote", "id", id));
    }

    private VoteResponse mapToResponse(Vote vote) {
        return new VoteResponse(
                vote.getId(),
                vote.getUser() != null ? vote.getUser().getId() : null,
                vote.getUser() != null ? vote.getUser().getUsername() : null,
                vote.getDecision() != null ? vote.getDecision().getId() : null,
                vote.getDecision() != null ? vote.getDecision().getTitle() : null,
                vote.getCreatedAt(),
                vote.getUpdatedAt());
    }
}

