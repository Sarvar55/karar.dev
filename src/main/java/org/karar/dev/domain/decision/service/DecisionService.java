package org.karar.dev.domain.decision.service;
import org.karar.dev.domain.decision.entity.Decision;
import org.karar.dev.domain.decision.repository.DecisionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.karar.dev.common.audit.AuditAction;
import org.karar.dev.common.audit.Auditable;
import org.karar.dev.domain.decision.entity.RegretLevel;
import org.karar.dev.common.exception.conflict.ConflictException;
import org.karar.dev.common.dto.PageResponse;
import org.karar.dev.common.exception.notfound.ResourceNotFoundException;
import org.karar.dev.common.security.service.SecurityService;
import org.karar.dev.common.dto.BaseResponse;
import org.karar.dev.domain.decision.entity.DecisionTagId;
import org.karar.dev.domain.decision.dto.DecisionRequest;
import org.karar.dev.domain.decision.dto.DecisionResponse;
import org.karar.dev.domain.decision.dto.DecisionUpdateRequest;
import org.karar.dev.domain.decision.entity.DecisionTag;
import org.karar.dev.domain.decision.service.DecisionTagService;
import org.karar.dev.domain.tag.entity.Tag;
import org.karar.dev.domain.tag.service.TagService;
import org.karar.dev.domain.user.regular.entity.RegularUser;
import org.karar.dev.domain.user.regular.service.RegularUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DecisionService {

    private final DecisionRepository decisionRepository;
    private final RegularUserService regularUserService;
    private final TagService tagService;
    private final DecisionTagService decisionTagService;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public BaseResponse<PageResponse<DecisionResponse>> getAllDecisions(Pageable pageable) {
        log.debug("Getting all decisions");
        Page<Decision> decisions = decisionRepository.findAll(pageable);
        log.debug("All decisions retrieved successfully");
        return BaseResponse.success(mapToPageResponse(decisions.map(this::mapToResponse)));
    }

    @Transactional(readOnly = true)
    public BaseResponse<DecisionResponse> getDecisionById(UUID id) {
        log.debug("Getting decision by id: {}", id);
        Decision decision = findDecisionOrThrow(id);
        return BaseResponse.success(mapToResponse(decision));
    }

    @Transactional(readOnly = true)
    public BaseResponse<PageResponse<DecisionResponse>> getDecisionsByUserId(UUID userId, Pageable pageable) {
        if (!regularUserService.existsById(userId)) {
            log.warn("User not found: {}", userId);
            throw new ResourceNotFoundException("User", "id", userId);
        }
        log.debug("Getting decisions by user id: {}", userId);
        Page<Decision> decisions = decisionRepository.findByUserId(userId, pageable);
        log.debug("Decisions by user id retrieved successfully");
        return BaseResponse.success(mapToPageResponse(decisions.map(this::mapToResponse)));
    }

    @Transactional(readOnly = true)
    public BaseResponse<PageResponse<DecisionResponse>> getDecisionsByRegretLevel(RegretLevel regretLevel,
            Pageable pageable) {
        log.debug("Getting decisions by regret level: {}", regretLevel);
        Page<Decision> decisions = decisionRepository.findByRegretLevel(regretLevel, pageable);
        log.debug("Decisions by regret level retrieved successfully");
        return BaseResponse.success(mapToPageResponse(decisions.map(this::mapToResponse)));
    }

    @Transactional(readOnly = true)
    public BaseResponse<PageResponse<DecisionResponse>> getDecisionsByTagId(UUID tagId, Pageable pageable) {
        if (!tagService.existsById(tagId)) {
            log.warn("Tag not found: {}", tagId);
            throw new ResourceNotFoundException("Tag", "id", tagId);
        }
        log.debug("Getting decisions by tag id: {}", tagId);
        Page<Decision> decisions = decisionRepository.findByTagId(tagId, pageable);
        log.debug("Decisions by tag id retrieved successfully");
        return BaseResponse.success(mapToPageResponse(decisions.map(this::mapToResponse)));
    }

    @Transactional
    @Auditable(action = AuditAction.CREATE, entityName = "Decision")
    public BaseResponse<DecisionResponse> createDecision(DecisionRequest request) {
        UUID currentUserId = securityService.getCurrentUserId();
        log.debug("Creating decision for authenticated user: {}", currentUserId);

        RegularUser user = regularUserService.getById(currentUserId);

        if (decisionRepository.existsByTitleAndUserId(request.title(), currentUserId)) {
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

        if (request.tagIds() != null && !request.tagIds().isEmpty()) {
            log.debug("Associating tags with decision: {}", savedDecision.getId());
            associateTagsWithDecision(savedDecision, request.tagIds());
        }

        log.info("Decision created successfully: {}", savedDecision.getId());
        return BaseResponse.success(mapToResponse(savedDecision), HttpStatus.CREATED);
    }

    @Transactional
    @Auditable(action = AuditAction.UPDATE, entityName = "Decision")
    public BaseResponse<DecisionResponse> updateDecision(UUID id, DecisionUpdateRequest request) {
        Decision decision = findDecisionOrThrow(id);
        log.debug("Updating decision: {}", id);
        if (!decision.getTitle().equals(request.title()) &&
                decisionRepository.existsByTitleAndUserId(request.title(), decision.getUser().getId())) {
            log.warn("Decision with this title already exists for user: {}", decision.getUser().getId());
            throw new ConflictException("Decision with this title already exists for this user");
        }

        decision.setTitle(request.title());
        decision.setWhy(request.why());
        decision.setAlternative(request.alternative());
        decision.setRegretLevel(request.regretLevel());

        if (request.tagIds() != null && !request.tagIds().isEmpty()) {
            Set<UUID> existingTagIds = decision.getTags().stream()
                    .map(dt -> dt.getTag().getId())
                    .collect(Collectors.toSet());/// 1 2

            Set<UUID> newTagIds = request.tagIds();// 3

            decision.getTags().removeIf(dt -> !newTagIds.contains(dt.getTag().getId()));

            for (UUID tagId : newTagIds) {
                if (!existingTagIds.contains(tagId)) {
                    Tag tag = tagService.getById(tagId);
                    DecisionTag dt = new DecisionTag();
                    dt.setId(new DecisionTagId(decision.getId(), tag.getId()));
                    dt.setDecision(decision);
                    dt.setTag(tag);
                    decision.getTags().add(dt);
                }
            }
        }
        log.info("Decision updated successfully: {}", id);
        Decision updatedDecision = decisionRepository.save(decision);
        return BaseResponse.success(mapToResponse(updatedDecision));
    }

    @Transactional
    @Auditable(action = AuditAction.DELETE, entityName = "Decision")
    public BaseResponse<Void> deleteDecision(UUID id) {
        if (!decisionRepository.existsById(id)) {
            log.warn("Decision not found: {}", id);
            throw new ResourceNotFoundException("Decision", "id", id);
        }
        log.info("Decision deleted successfully: {}", id);
        decisionRepository.deleteById(id);
        return BaseResponse.success(null, HttpStatus.NO_CONTENT);
    }

    @Transactional
    public void incrementVoteCount(UUID id) {
        if (!decisionRepository.existsById(id)) {
            log.warn("Decision not found: {}", id);
            throw new ResourceNotFoundException("Decision", "id", id);
        }
        log.info("Decision vote count incremented successfully: {}", id);
        decisionRepository.incrementVoteCount(id);
    }

    @Transactional
    public void decrementVoteCount(UUID id) {
        if (!decisionRepository.existsById(id)) {
            log.warn("Decision not found: {}", id);
            throw new ResourceNotFoundException("Decision", "id", id);
        }
        log.info("Decision vote count decremented successfully: {}", id);
        decisionRepository.decrementVoteCount(id);
    }

    @Transactional
    public void save(Decision decision) {
        log.debug("Saving decision: {}", decision);
        decisionRepository.save(decision);
    }

    private void associateTagsWithDecision(Decision decision, Set<UUID> tagIds) {

        for (UUID tagId : tagIds) {

            Tag tag = tagService.getById(tagId);

            DecisionTag dt = new DecisionTag();
            dt.setId(new DecisionTagId(decision.getId(), tag.getId()));
            dt.setDecision(decision);
            dt.setTag(tag);

            decision.getTags().add(dt);
        }

    }

    private void updateDecisionTags(Decision decision, Set<UUID> newTagIds) {
        // Remove existing tag associations
        decision.getTags().clear();
        // decisionTagService.deleteByDecisionId(decision.getId());

        // Add new tag associations
        if (!newTagIds.isEmpty()) {
            associateTagsWithDecision(decision, newTagIds);
        }
    }

    public boolean existsById(UUID id) {
        return decisionRepository.existsById(id);
    }

    public Decision getById(UUID id) {
        return findDecisionOrThrow(id);
    }

    private Decision findDecisionOrThrow(UUID id) {
        return decisionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Decision", "id", id));
    }

    private <T> PageResponse<T> mapToPageResponse(Page<T> page) {
        return new PageResponse<>(page);
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
                decision.getUpdatedAt());
    }
}
