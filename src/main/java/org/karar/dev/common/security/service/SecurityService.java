package org.karar.dev.common.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import org.karar.dev.common.exception.notfound.ResourceNotFoundException;
import org.karar.dev.common.security.user.SecurityUser;
import org.karar.dev.domain.comment.entity.Comment;
import org.karar.dev.domain.comment.repository.CommentRepository;
import org.karar.dev.domain.decision.entity.Decision;
import org.karar.dev.domain.decision.repository.DecisionRepository;
import org.karar.dev.domain.user.entity.User;
import org.karar.dev.domain.user.repository.UserRepository;
import org.karar.dev.domain.vote.entity.Vote;
import org.karar.dev.domain.vote.repository.VoteRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Provides ownership-checking methods used by {@code @PreAuthorize} SpEL
 * expressions, and helper methods for extracting authenticated user info.
 * <p>
 * Convention: {@code authentication.getName()} returns the user's <b>email</b>
 * (see {@link org.karar.dev.common.security.user.SecurityUser#getUsername()}).
 */
@Component("securityService")
@RequiredArgsConstructor
@Slf4j
public class SecurityService {

    private final DecisionRepository decisionRepository;
    private final CommentRepository commentRepository;
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;

    // ========================
    // Authenticated User Helpers
    // ========================

    /**
     * Returns the UUID of the currently authenticated user.
     * Extracts it from the SecurityUser principal stored in SecurityContextHolder.
     *
     * @throws IllegalStateException if no authenticated user is found
     */
    public UUID getCurrentUserId() {
        SecurityUser securityUser = getCurrentSecurityUser();
        log.debug("Current authenticated user ID: {}", securityUser.getUserId());
        return securityUser.getUserId();
    }

    /**
     * Returns the email of the currently authenticated user.
     */
    public String getCurrentUserEmail() {
        SecurityUser securityUser = getCurrentSecurityUser();
        return securityUser.getUsername();
    }

    private SecurityUser getCurrentSecurityUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof SecurityUser)) {
            throw new IllegalStateException("No authenticated user found in SecurityContext");
        }
        return (SecurityUser) authentication.getPrincipal();
    }

    public boolean isOwnerOfDecision(Authentication authentication, UUID decisionId) {
        log.debug("Checking if user is owner of decision: {}", decisionId);
        Decision decision = decisionRepository.findById(decisionId)
                .orElseThrow(() -> new ResourceNotFoundException("Decision", "id", decisionId));
        log.debug("User is owner of decision: {}", isOwner(authentication, decision.getUser().getEmail()));
        return isOwner(authentication, decision.getUser().getEmail());
    }

    public boolean isOwnerOfComment(Authentication authentication, UUID commentId) {
        log.debug("Checking if user is owner of comment: {}", commentId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));
        log.debug("User is owner of comment: {}", isOwner(authentication, comment.getUser().getEmail()));
        return isOwner(authentication, comment.getUser().getEmail());
    }

    public boolean isOwnerOfVote(Authentication authentication, UUID voteId) {
        log.debug("Checking if user is owner of vote: {}", voteId);
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new ResourceNotFoundException("Vote", "id", voteId));
        log.debug("User is owner of vote: {}", isOwner(authentication, vote.getUser().getEmail()));
        return isOwner(authentication, vote.getUser().getEmail());
    }

    public boolean isOwnerOfVoteByUserAndDecision(Authentication authentication, UUID userId) {
        log.debug("Checking if user is owner of vote by user and decision: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        log.debug("User is owner of vote by user and decision: {}", isOwner(authentication, user.getEmail()));
        return isOwner(authentication, user.getEmail());
    }

    public boolean isSelf(Authentication authentication, UUID userId) {
        log.debug("Checking if user is self: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        log.debug("User is self: {}", isOwner(authentication, user.getEmail()));
        return isOwner(authentication, user.getEmail());
    }

    public boolean isAdminOrOwnerOfDecision(Authentication authentication, UUID decisionId) {
        log.debug("Checking if user is admin or owner of decision: {}", decisionId);
        if (hasRole(authentication, "ROLE_ADMIN")) {
            log.debug("User is admin");
            return true;
        }
        log.debug("User is admin or owner of decision: {}", isOwnerOfDecision(authentication, decisionId));
        return isOwnerOfDecision(authentication, decisionId);
    }

    public boolean isAdminOrOwnerOfComment(Authentication authentication, UUID commentId) {
        log.debug("Checking if user is admin or owner of comment: {}", commentId);
        if (hasRole(authentication, "ROLE_ADMIN")) {
            log.debug("User is admin");
            return true;
        }
        log.debug("User is admin or owner of comment: {}", isOwnerOfComment(authentication, commentId));
        return isOwnerOfComment(authentication, commentId);
    }

    public boolean isAdminOrSelf(Authentication authentication, UUID userId) {
        log.debug("Checking if user is admin or self: {}", userId);
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
