package org.karar.dev.domain.decision;

import org.karar.dev.common.enums.RegretLevel;
import org.karar.dev.domain.user.regular.RegularUser;
import org.karar.dev.domain.user.regular.RegularUserBuilder;

import java.util.UUID;

public class DecisionBuilder {
    private UUID id = UUID.randomUUID();
    private String title = "Test Decision";
    private String why = "Because...";
    private String alternative = "Another way";
    private RegretLevel regretLevel = RegretLevel.LOW;
    private RegularUser user = RegularUserBuilder.user().build();
    private int voteCount = 0;

    public DecisionBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public DecisionBuilder withWhy(String why) {
        this.why = why;
        return this;
    }

    public DecisionBuilder withAlternative(String alternative) {
        this.alternative = alternative;
        return this;
    }

    public DecisionBuilder withRegretLevel(RegretLevel regretLevel) {
        this.regretLevel = regretLevel;
        return this;
    }

    public DecisionBuilder withUser(RegularUser user) {
        this.user = user;
        return this;
    }

    public DecisionBuilder withVoteCount(int voteCount) {
        this.voteCount = voteCount;
        return this;
    }

    public Decision build() {
        Decision decision = new Decision();
        decision.setId(id);
        decision.setTitle(title);
        decision.setWhy(why);
        decision.setAlternative(alternative);
        decision.setRegretLevel(regretLevel);
        decision.setUser(user);
        decision.setVoteCount(voteCount);
        return decision;
    }

}
