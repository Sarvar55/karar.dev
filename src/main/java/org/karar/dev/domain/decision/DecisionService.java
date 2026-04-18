package org.karar.dev.domain.decision;

import lombok.RequiredArgsConstructor;
import org.karar.dev.common.enums.RegretLevel;
import org.karar.dev.common.exception.conflict.ConflictException;
import org.karar.dev.common.exception.notFound.ResourceNotFoundException;
import org.karar.dev.domain.base.BaseResponse;
import org.karar.dev.domain.decision.dto.DecisionRequest;
import org.karar.dev.domain.decision.dto.DecisionResponse;
import org.karar.dev.domain.decision.dto.DecisionUpdateRequest;
import org.karar.dev.domain.user.regular.RegularUser;
import org.karar.dev.domain.user.regular.RegularUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DecisionService {

    private final DecisionRepository decisionRepository;
    private final RegularUserRepository regularUserRepository;

    @Transactional(readOnly = true)
    public BaseResponse<List<DecisionResponse>> getAllDecisions() {
        List<Decision> decisions = decisionRepository.findAll();
        List<DecisionResponse> responseList = decisions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return BaseResponse.success(responseList);
    }

    @Transactional(readOnly = true)
    public BaseResponse<DecisionResponse> getDecisionById(UUID id) {
        Decision decision = findDecisionOrThrow(id);
        return BaseResponse.success(mapToResponse(decision));
    }

    @Transactional(readOnly = true)
    public BaseResponse<List<DecisionResponse>> getDecisionsByUserId(UUID userId) {
        if (!regularUserRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        List<Decision> decisions = decisionRepository.findByUserId(userId);
        List<DecisionResponse> responseList = decisions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return BaseResponse.success(responseList);
    }

    @Transactional(readOnly = true)
    public BaseResponse<List<DecisionResponse>> getDecisionsByRegretLevel(RegretLevel regretLevel) {
        List<Decision> decisions = decisionRepository.findByRegretLevel(regretLevel);
        List<DecisionResponse> responseList = decisions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return BaseResponse.success(responseList);
    }

    @Transactional
    public BaseResponse<DecisionResponse> createDecision(DecisionRequest request) {
        RegularUser user = regularUserRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.userId()));

        if (decisionRepository.existsByTitleAndUserId(request.title(), request.userId())) {
            throw new ConflictException("Decision with this title already exists for this user");
        }

        Decision decision = new Decision();
        decision.setTitle(request.title());
        decision.setWhy(request.why());
        decision.setAlternative(request.alternative());
        decision.setRegretLevel(request.regretLevel());
        decision.setUser(user);
        decision.setVoteCount(0);

        Decision savedDecision = decisionRepository.saveAndFlush(decision);
        return BaseResponse.success(mapToResponse(savedDecision), HttpStatus.CREATED);
    }

    @Transactional
    public BaseResponse<DecisionResponse> updateDecision(UUID id, DecisionUpdateRequest request) {
        Decision decision = findDecisionOrThrow(id);

        if (!decision.getTitle().equals(request.title()) &&
                decisionRepository.existsByTitleAndUserId(request.title(), decision.getUser().getId())) {
            throw new ConflictException("Decision with this title already exists for this user");
        }

        decision.setTitle(request.title());
        decision.setWhy(request.why());
        decision.setAlternative(request.alternative());
        decision.setRegretLevel(request.regretLevel());

        Decision updatedDecision = decisionRepository.saveAndFlush(decision);
        return BaseResponse.success(mapToResponse(updatedDecision));
    }

    @Transactional
    public BaseResponse<Void> deleteDecision(UUID id) {
        if (!decisionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Decision", "id", id);
        }
        decisionRepository.deleteById(id);
        return BaseResponse.success(null, HttpStatus.NO_CONTENT);
    }

    private Decision findDecisionOrThrow(UUID id) {
        return decisionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Decision", "id", id));
    }       

    private DecisionResponse mapToResponse(Decision decision) {
        return new DecisionResponse(
                decision.getId(),
                decision.getTitle(),
                decision.getWhy(),
                decision.getAlternative(),
                decision.getRegretLevel(),
                decision.getVoteCount(),
                decision.getUser() != null ? decision.getUser().getId() : null,
                decision.getUser() != null ? decision.getUser().getUsername() : null,
                decision.getComments() != null ? decision.getComments().size() : 0,
                decision.getTags() != null && !decision.getTags().isEmpty()
                        ? decision.getTags().stream()
                        .map(tag -> tag.getTag() != null ? tag.getTag().getName() : "")
                        .filter(name -> name != null && !name.isEmpty())
                        .collect(Collectors.toSet())
                        : java.util.Collections.emptySet(),
                decision.getCreatedAt(),
                decision.getUpdatedAt()
        );
    }
}
