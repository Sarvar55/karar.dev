package org.karar.dev.domain.extensions;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.karar.dev.domain.comment.Comment;
import org.karar.dev.domain.comment.CommentFixture;
import org.karar.dev.domain.decision.Decision;
import org.karar.dev.domain.user.regular.RegularUser;

public class CommentParameterResolver implements ParameterResolver {
    @Override
    public boolean supportsParameter(ParameterContext pc, ExtensionContext ec) {
        Class<?> type = pc.getParameter().getType();
        return type == Comment.class ||
                type == RegularUser.class ||
                type == Decision.class;
    }

    @Override
    public Object resolveParameter(ParameterContext pc, ExtensionContext ec) {
        Class<?> type = pc.getParameter().getType();

        if (type == RegularUser.class) return CommentFixture.user();
        if (type == Decision.class) return CommentFixture.decision();
        if (type == Comment.class) {
            return CommentFixture.comment(
                    CommentFixture.user(),
                    CommentFixture.decision()
            );
        }
        return null;
    }
}
