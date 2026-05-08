package org.karar.dev.domain.comment;

import org.karar.dev.domain.comment.dto.CommentRequest;
import org.karar.dev.domain.comment.dto.CommentUpdateRequest;
import org.karar.dev.domain.decision.Decision;
import org.karar.dev.domain.user.regular.RegularUser;

import java.util.UUID;

public class CommentFixture {
    public static UUID randomId() {
        return UUID.randomUUID();
    }

    public static Comment comment(RegularUser user, Decision decision) {
        Comment c = new Comment();
        c.setId(UUID.randomUUID());
        c.setContent("test-content");
        c.setUser(user);
        c.setDecision(decision);
        return c;
    }

    public static RegularUser user() {
        RegularUser u = new RegularUser();
        u.setId(UUID.randomUUID());
        u.setUsername("test-user");
        return u;
    }

    public static RegularUser user(UUID id) {
        RegularUser u = new RegularUser();
        u.setId(id);
        u.setUsername("test-user");
        return u;
    }

    public static Decision decision() {
        Decision d = new Decision();
        d.setId(UUID.randomUUID());
        d.setTitle("test-decision");
        return d;
    }

    public static Decision decision(UUID id) {
        Decision d = new Decision();
        d.setId(id);
        d.setTitle("test-decision");
        return d;
    }

    public static CommentRequest createRequest(UUID decisionId, UUID userId) {
        Decision d = decision(decisionId);
        RegularUser u = user(userId);
        return new CommentRequest("content", u.getId(), d.getId());
    }

    public static CommentUpdateRequest createUpdateRequest(String content) {
        return new CommentUpdateRequest(content);
    }
}
