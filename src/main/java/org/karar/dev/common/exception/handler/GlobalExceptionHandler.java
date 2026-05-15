package org.karar.dev.common.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.karar.dev.common.exception.base.BaseException;
import org.karar.dev.common.exception.resolver.ExceptionMessageResolver;
import org.karar.dev.domain.base.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<?>> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error(
                "Unexpected error occurred. Path: {}, Method: {}",
                request.getRequestURI(),
                request.getMethod(),
                ex
        );
        BaseResponse<?> response = BaseResponse.error(
                "InternalServerError",
                exceptionMessageResolver.resolve(ex),
                HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
