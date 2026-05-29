package org.karar.dev.common.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.karar.dev.common.exception.ExceptionMessages;
import org.karar.dev.common.exception.base.BaseException;
import org.karar.dev.common.exception.resolver.ExceptionMessageResolver;
import org.karar.dev.domain.media.minio.exception.StorageException;
import org.karar.dev.common.dto.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private static final String DEFAULT_VALIDATION_MESSAGE = "Validation failed";
    private static final String DEFAULT_ERROR_MESSAGE = "An unexpected error occurred";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "InternalServerError";
    private static final String INVALID_CREDENTIALS_MESSAGE = "InvalidCredentials";
    private static final String ACCOUNT_DISABLED_MESSAGE = "AccountDisabled";
    private static final String ACCOUNT_LOCKED_MESSAGE = "AccountLocked";
    private final ExceptionMessageResolver exceptionMessageResolver;

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseResponse<?>> handleBaseException(BaseException ex) {
        BaseResponse<?> response = BaseResponse.error(ex, ex.getStatus());
        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<?>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> validationErrors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });

        BaseResponse<?> response = BaseResponse.validationError(DEFAULT_VALIDATION_MESSAGE, validationErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<BaseResponse<?>> handleBadCredentials(BadCredentialsException ex) {
        log.warn("Authentication failed: invalid credentials");
        BaseResponse<?> response = BaseResponse.error(
                INVALID_CREDENTIALS_MESSAGE,
                ExceptionMessages.INVALID_CREDENTIALS.getMessage(),
                HttpStatus.UNAUTHORIZED);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<BaseResponse<?>> handleLocked(LockedException ex) {
        log.warn("Authentication failed: account locked");
        BaseResponse<?> response = BaseResponse.error(
                ACCOUNT_LOCKED_MESSAGE,
                ex.getMessage(),
                HttpStatus.FORBIDDEN);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<BaseResponse<?>> handleDisabled(DisabledException ex) {
        log.warn("Authentication failed: account disabled");
        BaseResponse<?> response = BaseResponse.error(
                ACCOUNT_DISABLED_MESSAGE,
                exceptionMessageResolver.resolve(ex),
                HttpStatus.FORBIDDEN);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<BaseResponse<?>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        log.warn("Authentication failed: username not found");
        BaseResponse<?> response = BaseResponse.error(
                INVALID_CREDENTIALS_MESSAGE,
                exceptionMessageResolver.resolve(ex),
                HttpStatus.UNAUTHORIZED);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<BaseResponse<?>> handleStorageException(StorageException ex) {
        log.warn("Storage operation failed: {}", ex.getMessage());
        BaseResponse<?> response = BaseResponse.error(
                ExceptionMessages.STORAGE_ERROR.getMessage(),
                ex.getMessage(),
                ex.getStatus());
        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<?>> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error(
                "Unexpected error occurred. Path: {}, Method: {}",
                request.getRequestURI(),
                request.getMethod(),
                ex);
        BaseResponse<?> response = BaseResponse.error(
                INTERNAL_SERVER_ERROR_MESSAGE,
                exceptionMessageResolver.resolve(ex),
                HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
