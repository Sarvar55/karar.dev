package org.karar.dev.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Centralised exception message constants.
 * <p>
 * Every user-facing error string lives here so that messages stay consistent
 * across the entire codebase and are easy to change in one place (e.g. for
 * i18n).
 * <p>
 * Usage:
 * 
 * <pre>
 * throw new ResourceNotFoundException(
 *         ExceptionMessages.RESOURCE_NOT_FOUND.format("User", "id", userId));
 * </pre>
 */
@Getter
@RequiredArgsConstructor
public enum ExceptionMessages {

    // ── Not-Found ────────────────────────────────────────────────────────
    RESOURCE_NOT_FOUND("%s not found with %s: '%s'"),

    // ── Conflict / Duplicate ─────────────────────────────────────────────
    EMAIL_ALREADY_EXISTS("Email already exists: '%s'"),
    DECISION_TITLE_DUPLICATE("Decision with this title already exists for this user"),
    TAG_NAME_DUPLICATE("Tag with name '%s' already exists"),
    VOTE_ALREADY_EXISTS("User has already voted on this decision"),

    // ── Validation ───────────────────────────────────────────────────────
    VALIDATION_FAILED("Validation failed"),
    USERNAME_REQUIRED("Username is required for regular users"),
    COMPANY_NAME_REQUIRED("Company name is required for companies"),

    // ── Auth / Role ──────────────────────────────────────────────────────
    UNSUPPORTED_ROLE("Unsupported role: %s"),
    INVALID_CREDENTIALS("Invalid email or password"),
    INVALID_REFRESH_TOKEN("Refresh token is invalid or expired"),
    ACCOUNT_LOCKED("Account is locked due to too many failed attempts. Try again after %s minutes"),
    ACCOUNT_DISABLED("Account is not verified"),

    // ── Generic ──────────────────────────────────────────────────────────
    INTERNAL_SERVER_ERROR("An unexpected error occurred");

    private final String message;

    /**
     * Returns the message with {@code args} interpolated via
     * {@link String#format(String, Object...)}.
     */
    public String format(Object... args) {
        return String.format(message, args);
    }
}
