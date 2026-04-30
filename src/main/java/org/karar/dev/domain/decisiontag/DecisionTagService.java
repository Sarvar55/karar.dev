package org.karar.dev.domain.decisiontag;

import lombok.RequiredArgsConstructor;
import org.karar.dev.domain.base.BaseResponse;
import org.karar.dev.domain.tag.Tag;
import org.karar.dev.domain.tag.TagService;
import org.karar.dev.domain.tag.dto.TagResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DecisionTagService {

    private final DecisionTagRepository decisionTagRepository;
    private final TagService tagService;

    @Transactional(readOnly = true)
    public BaseResponse<List<TagResponse>> getTagsByDecisionId(UUID decisionId) {
        List<DecisionTag> decisionTags = decisionTagRepository.findByDecisionId(decisionId);
        List<TagResponse> tagResponses = decisionTags.stream()
                .map(dt -> tagService.getTagById(dt.getTag().getId()).getData())
                .toList();
        return BaseResponse.success(tagResponses, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public List<DecisionTag> findByTagId(UUID tagId) {
        return decisionTagRepository.findByTagId(tagId);
    }

    @Transactional
    public void deleteByDecisionId(UUID decisionId) {
        decisionTagRepository.deleteByDecisionId(decisionId);
    }

    @Transactional
    public void save(DecisionTag decisionTag) {
        decisionTagRepository.save(decisionTag);
    }
}
