package org.karar.dev.domain.decisiontag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.karar.dev.common.audit.AuditAction;
import org.karar.dev.common.audit.Auditable;
import org.karar.dev.domain.base.BaseResponse;
import org.karar.dev.domain.tag.TagService;
import org.karar.dev.domain.tag.dto.TagResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DecisionTagService {

    private final DecisionTagRepository decisionTagRepository;
    private final TagService tagService;

    @Transactional(readOnly = true)
    public BaseResponse<List<TagResponse>> getTagsByDecisionId(UUID decisionId) {
        log.debug("Getting tags by decision id: {}", decisionId);
        List<DecisionTag> decisionTags = decisionTagRepository.findByDecisionId(decisionId);
        List<TagResponse> tagResponses = decisionTags.stream()
                .map(dt -> tagService.getTagById(dt.getTag().getId()).getData())
                .toList();
        log.debug("Tags retrieved successfully: {}", tagResponses);
        return BaseResponse.success(tagResponses, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public List<DecisionTag> findByTagId(UUID tagId) {
        log.debug("Finding tags by tag id: {}", tagId);
        return decisionTagRepository.findByTagId(tagId);
    }

    @Transactional
    public void deleteByDecisionId(UUID decisionId) {
        log.debug("Deleting tags by decision id: {}", decisionId);
        decisionTagRepository.deleteByDecisionId(decisionId);
    }

    @Transactional
    @Auditable(action = AuditAction.CREATE,entityName = "DecisionTag")
    public void save(DecisionTag decisionTag) {
        log.debug("Saving decision tag: {}", decisionTag);
        decisionTagRepository.save(decisionTag);
        log.debug("Decision tag saved successfully: {}", decisionTag);
    }
}
