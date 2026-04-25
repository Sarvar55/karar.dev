package org.karar.dev.domain.decision;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.karar.dev.common.enums.RegretLevel;
import org.karar.dev.common.exception.conflict.ConflictException;
import org.karar.dev.common.exception.dto.PageResponse;
import org.karar.dev.common.exception.notFound.ResourceNotFoundException;
import org.karar.dev.domain.base.BaseResponse;
import org.karar.dev.domain.base.DecisionTagId;
import org.karar.dev.domain.decision.dto.DecisionRequest;
import org.karar.dev.domain.decision.dto.DecisionResponse;
import org.karar.dev.domain.decision.dto.DecisionUpdateRequest;
import org.karar.dev.domain.decisiontag.DecisionTag;
import org.karar.dev.domain.decisiontag.DecisionTagService;
import org.karar.dev.domain.tag.Tag;
import org.karar.dev.domain.tag.TagService;
import org.karar.dev.domain.user.regular.RegularUser;
import org.karar.dev.domain.user.regular.RegularUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DecisionService {

    private final DecisionRepository decisionRepository;
    private final RegularUserService regularUserService;
    private final TagService tagService;
    private final DecisionTagService decisionTagService;

    @Transactional(readOnly = true)
    public BaseResponse<PageResponse<DecisionResponse>> getAllDecisions(Pageable pageable) {
        Page<Decision> decisions = decisionRepository.findAll(pageable);
        return BaseResponse.success(mapToPageResponse(decisions.map(this::mapToResponse)));
    }

    @Transactional(readOnly = true)
    public BaseResponse<DecisionResponse> getDecisionById(UUID id) {
        Decision decision = findDecisionOrThrow(id);
        return BaseResponse.success(mapToResponse(decision));
    }

    @Transactional(readOnly = true)
    public BaseResponse<PageResponse<DecisionResponse>> getDecisionsByUserId(UUID userId, Pageable pageable) {
        if (!regularUserService.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        Page<Decision> decisions = decisionRepository.findByUserId(userId, pageable);
        return BaseResponse.success(mapToPageResponse(decisions.map(this::mapToResponse)));
    }

    @Transactional(readOnly = true)
    public BaseResponse<PageResponse<DecisionResponse>> getDecisionsByRegretLevel(RegretLevel regretLevel, Pageable pageable) {
        Page<Decision> decisions = decisionRepository.findByRegretLevel(regretLevel, pageable);
        return BaseResponse.success(mapToPageResponse(decisions.map(this::mapToResponse)));
    }

    @Transactional(readOnly = true)
    public BaseResponse<PageResponse<DecisionResponse>> getDecisionsByTagId(UUID tagId, Pageable pageable) {
        if (!tagService.existsById(tagId)) {
            throw new ResourceNotFoundException("Tag", "id", tagId);
        }
        Page<Decision> decisions = decisionRepository.findByTagId(tagId, pageable);
        return BaseResponse.success(mapToPageResponse(decisions.map(this::mapToResponse)));
    }

    @Transactional
    public BaseResponse<DecisionResponse> createDecision(DecisionRequest request) {
        RegularUser user = regularUserService.getById(request.userId());

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

        // Handle tag associations
        if (request.tagIds() != null && !request.tagIds().isEmpty()) {
            associateTagsWithDecision(savedDecision, request.tagIds());
        }

        // Reload to get tags
        savedDecision = decisionRepository.getDecisionsWithTags(savedDecision.getId()).orElse(savedDecision);

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

        Decision updatedDecision = decisionRepository.save(decision);
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

    @Transactional
    public void incrementVoteCount(UUID id) {
        if (!decisionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Decision", "id", id);
        }
        decisionRepository.incrementVoteCount(id);
    }

    @Transactional
    public void decrementVoteCount(UUID id) {
        if (!decisionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Decision", "id", id);
        }
        decisionRepository.decrementVoteCount(id);
    }

    @Transactional
    public void save(Decision decision) {
        decisionRepository.save(decision);
    }

    private void associateTagsWithDecision(Decision decision, Set<UUID> tagIds) {

        for (UUID tagId : tagIds) {

            Tag tag = tagService.getById(tagId);

            DecisionTag dt = new DecisionTag();
            dt.setDecision(decision);
            dt.setTag(tag);

            decision.getTags().add(dt);
        }

    }

    private void updateDecisionTags(Decision decision, Set<UUID> newTagIds) {
        // Remove existing tag associations
        decision.getTags().clear();
        //decisionTagService.deleteByDecisionId(decision.getId());

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
                decision.getUpdatedAt()
        );
    }
}
