package org.karar.dev.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionMessages {

    RESOURCE_NOT_FOUND("%s not found with %s: '%s'"),

    EMAIL_ALREADY_EXISTS("Email already exists: '%s'"),
    DECISION_TITLE_DUPLICATE("Decision with this title already exists for this user"),
    TAG_NAME_DUPLICATE("Tag with name '%s' already exists"),
    VOTE_ALREADY_EXISTS("User has already voted on this decision"),

    VALIDATION_FAILED("Validation failed"),
    USERNAME_REQUIRED("Username is required for regular users"),
    COMPANY_NAME_REQUIRED("Company name is required for companies"),

    UNSUPPORTED_ROLE("Unsupported role: %s"),
    INVALID_CREDENTIALS("Invalid email or password"),
    INVALID_REFRESH_TOKEN("Refresh token is invalid or expired"),
    ACCOUNT_LOCKED("Account is locked due to too many failed attempts. Try again after %s minutes"),
    ACCOUNT_DISABLED("Account is not verified"),

    EMAIL_NOT_VERIFIED("Please verify your email before logging in"),
    VERIFICATION_TOKEN_EXPIRED("Verification token has expired, please request a new one"),
    VERIFICATION_TOKEN_NOT_FOUND("Invalid verification token"),
    EMAIL_ALREADY_VERIFIED("Email is already verified"),

    INVALID_OTP("Invalid or expired OTP code"),
    OTP_ALREADY_SENT("OTP was already sent, please wait before requesting a new one"),

    STORAGE_ERROR("Storage operation failed"),

    INTERNAL_SERVER_ERROR("An unexpected error occurred");

    private final String message;

    public String format(Object... args) {
        return String.format(message, args);
    }
}

