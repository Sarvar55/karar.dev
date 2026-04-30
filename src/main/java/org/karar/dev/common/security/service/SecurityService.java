package org.karar.dev.common.security.service;

import lombok.RequiredArgsConstructor;
import org.karar.dev.common.exception.notFound.ResourceNotFoundException;
import org.karar.dev.domain.comment.Comment;
import org.karar.dev.domain.comment.CommentRepository;
import org.karar.dev.domain.decision.Decision;
import org.karar.dev.domain.decision.DecisionRepository;
import org.karar.dev.domain.user.User;
import org.karar.dev.domain.user.UserRepository;
import org.karar.dev.domain.vote.Vote;
import org.karar.dev.domain.vote.VoteRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Provides ownership-checking methods used by {@code @PreAuthorize} SpEL
 * expressions.
 * <p>
 * Convention: {@code authentication.getName()} returns the user's <b>email</b>
 * (see {@link org.karar.dev.common.security.user.SecurityUser#getUsername()}).
 */
@Component("securityService")
@RequiredArgsConstructor
public class SecurityService {

    private final DecisionRepository decisionRepository;
    private final CommentRepository commentRepository;
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;

    public boolean isOwnerOfDecision(Authentication authentication, UUID decisionId) {
        Decision decision = decisionRepository.findById(decisionId)
                .orElseThrow(() -> new ResourceNotFoundException("Decision", "id", decisionId));
        return isOwner(authentication, decision.getUser().getEmail());
    }

    public boolean isOwnerOfComment(Authentication authentication, UUID commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));
        return isOwner(authentication, comment.getUser().getEmail());
    }

    public boolean isOwnerOfVote(Authentication authentication, UUID voteId) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new ResourceNotFoundException("Vote", "id", voteId));
        return isOwner(authentication, vote.getUser().getEmail());
    }

    public boolean isOwnerOfVoteByUserAndDecision(Authentication authentication, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return isOwner(authentication, user.getEmail());
    }

    public boolean isSelf(Authentication authentication, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return isOwner(authentication, user.getEmail());
    }

    public boolean isAdminOrOwnerOfDecision(Authentication authentication, UUID decisionId) {
        if (hasRole(authentication, "ROLE_ADMIN"))
            return true;
        return isOwnerOfDecision(authentication, decisionId);
    }

    public boolean isAdminOrOwnerOfComment(Authentication authentication, UUID commentId) {
        if (hasRole(authentication, "ROLE_ADMIN"))
            return true;
        return isOwnerOfComment(authentication, commentId);
    }

    public boolean isAdminOrSelf(Authentication authentication, UUID userId) {
        if (hasRole(authentication, "ROLE_ADMIN"))
            return true;
        return isSelf(authentication, userId);
    }

    private boolean isOwner(Authentication authentication, String ownerEmail) {
        return authentication.getName().equals(ownerEmail);
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(role));
    }
}
