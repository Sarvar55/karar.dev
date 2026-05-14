package org.karar.dev.domain.decision;

import org.karar.dev.common.enums.RegretLevel;
import org.karar.dev.domain.decision.dto.DecisionRequest;

import java.util.Set;
import java.util.UUID;

public class DecisionRequestBuilder {

    public static Builder aRequest() {
        return new Builder();
    }
    public static class Builder {

        private String title = "Default Title";
        private String why = "Default Why";
        private String alternative = "Default Alt";
        private RegretLevel regretLevel = RegretLevel.LOW;
        private Set<UUID> tagIds = Set.of(UUID.randomUUID());

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withTags(Set<UUID> tagIds) {
            this.tagIds = tagIds;
            return this;
        }

        public Builder withoutTags() {
            this.tagIds = Set.of();
            return this;
        }

        public DecisionRequest build() {
            return new DecisionRequest(
                    title,
                    why,
                    alternative,
                    regretLevel,
                    tagIds
            );
        }
    }
}
