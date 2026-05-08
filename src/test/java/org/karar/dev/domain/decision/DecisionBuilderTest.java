package org.karar.dev.domain.decision;

import org.karar.dev.common.enums.RegretLevel;
import org.karar.dev.domain.decisiontag.DecisionTag;
import org.karar.dev.domain.tag.Tag;
import org.karar.dev.domain.user.regular.RegularUser;
import org.karar.dev.domain.user.regular.RegularUserBuilder;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DecisionBuilderTest {
    private UUID id = UUID.randomUUID();
    private String title = "Test Decision";
    private String why = "Because...";
    private String alternative = "Another way";
    private RegretLevel regretLevel = RegretLevel.LOW;
    private RegularUser user = RegularUserBuilder.user().build();
    private int voteCount = 0;
    private Set<DecisionTag> tags = new HashSet<>();

    public static DecisionBuilderTest decision() {
        return new DecisionBuilderTest();
    }

    public DecisionBuilderTest withTag(Tag tag) {
        DecisionTag decisionTag = new DecisionTag();
        decisionTag.setTag(tag);
        this.tags.add(decisionTag);
        return this;
    }

    public DecisionBuilderTest withId(UUID id) {
        this.id = id;
        return this;
    }

    public DecisionBuilderTest withTitle(String title) {
        this.title = title;
        return this;
    }

    public DecisionBuilderTest withWhy(String why) {
        this.why = why;
        return this;
    }

    public DecisionBuilderTest withAlternative(String alternative) {
        this.alternative = alternative;
        return this;
    }

    public DecisionBuilderTest withRegretLevel(RegretLevel regretLevel) {
        this.regretLevel = regretLevel;
        return this;
    }

    public DecisionBuilderTest withUser(RegularUser user) {
        this.user = user;
        return this;
    }

    public DecisionBuilderTest withVoteCount(int voteCount) {
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
        decision.setTags(tags);
        return decision;
    }

}
