package org.karar.dev.domain.vote;

import org.karar.dev.domain.decision.entity.Decision;
import org.karar.dev.domain.user.regular.entity.RegularUser;
import org.karar.dev.domain.vote.entity.Vote;

public class VoteBuilderTest {

    private final RegularUser user;
    private final Decision decision;

    private VoteBuilderTest(RegularUser user, Decision decision) {
        this.user = user;
        this.decision = decision;
    }

    public static UserStep builder() {
        return new Builder();
    }

    // STEP 1
    public interface UserStep {
        DecisionStep user(RegularUser user);
    }

    // STEP 2
    public interface DecisionStep {
        BuildStep decision(Decision decision);
    }

    // STEP 3
    public interface BuildStep {
        Vote build();
    }

    private static class Builder implements UserStep, DecisionStep, BuildStep {

        private RegularUser user;
        private Decision decision;

        @Override
        public DecisionStep user(RegularUser user) {
            this.user = user;
            return this;
        }

        @Override
        public BuildStep decision(Decision decision) {
            this.decision = decision;
            return this;
        }

        @Override
        public Vote build() {
            return new Vote(user, decision);
        }
    }
}