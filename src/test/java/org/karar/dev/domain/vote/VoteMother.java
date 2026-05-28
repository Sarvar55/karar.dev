package org.karar.dev.domain.vote;
import org.karar.dev.domain.vote.entity.Vote;

import org.karar.dev.domain.decision.entity.Decision;
import org.karar.dev.domain.decision.DecisionBuilderTest;
import org.karar.dev.domain.user.regular.entity.RegularUser;
import org.karar.dev.domain.user.regular.RegularUserBuilder;
import org.karar.dev.domain.vote.dto.VoteRequest;

import java.util.UUID;

public class VoteMother {

    private static final RegularUser user;
    private static final Decision decision;
    private static final UUID userId = UUID.randomUUID();
    private static final UUID decisionId = UUID.randomUUID();

    static {
        user = RegularUserBuilder.user().withId(userId).build();
        decision = DecisionBuilderTest.decision().withId(decisionId).withUser(user).build();
    }


    public static Vote aDefaultVote() {
        return VoteBuilderTest.builder().user(user)
                .decision(decision).build();
    }

    public static VoteRequest aDefaultRequest() {
        return VoteRequest.builder()
                .decisionId(decisionId)
                .build();
    }
}
