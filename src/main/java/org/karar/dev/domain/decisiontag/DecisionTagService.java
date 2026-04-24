package org.karar.dev.domain.decisiontag;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DecisionTagService {

    private final DecisionTagRepository decisionTagRepository;

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
