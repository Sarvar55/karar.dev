package org.karar.dev.domain.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.karar.dev.common.exception.base.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {

    private boolean success;
    private T data;
    private ErrorData error;
    private LocalDateTime timestamp;
    private HttpStatus status;

    @Data
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorData {
        String code;
        String message;
        Map<String, Object> details;
        Map<String, String> validationErrors;

        public static ErrorData of(String code, String message) {
            return ErrorData.builder().code(code).message(message).build();
        }

        public static ErrorData of(String code, String message, Map<String, Object> details) {
            return ErrorData.builder()
                    .code(code)
                    .message(message)
                    .details(details)
                    .build();
        }

        public static ErrorData of(BaseException ex) {
            return ErrorData.builder()
                    .code(ex.getClass().getSimpleName())
                    .message(ex.getMessage())
                    .validationErrors(ex.getValidationErrors())
                    .build();
        }

        public static ErrorData validationError(String message, Map<String, String> validationErrors) {
            return ErrorData.builder()
                    .code("ValidationError")
                    .message(message)
                    .validationErrors(validationErrors)
                    .build();
        }
    }

    public static <T> BaseResponse<T> success(T data) {
        return BaseResponse.<T>builder()
                .success(true)
                .data(data)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> BaseResponse<T> success(T data, HttpStatus status) {
        return BaseResponse.<T>builder()
                .success(true)
                .data(data)
                .status(status)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> BaseResponse<T> success() {
        return success(null);
    }

    public static <T> BaseResponse<T> error(BaseException ex, HttpStatus status) {
        return BaseResponse.<T>builder()
                .success(false)
                .error(ErrorData.of(ex))
                .status(status)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> BaseResponse<T> error(String code, String message, HttpStatus status) {
        return BaseResponse.<T>builder()
                .success(false)
                .error(ErrorData.of(code, message))
                .status(status)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> BaseResponse<T> error(AuthenticationException ex) {
        return BaseResponse.<T>builder()
                .success(false)
                .error(ErrorData.of(HttpStatus.UNAUTHORIZED.toString(), ex.getMessage()))
                .status(HttpStatus.UNAUTHORIZED)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> BaseResponse<T> error(AccessDeniedException ex) {
        return BaseResponse.<T>builder()
                .success(false)
                .error(ErrorData.of(HttpStatus.FORBIDDEN.toString(), ex.getMessage()))
                .status(HttpStatus.FORBIDDEN)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> BaseResponse<T> error(String code, String message, Map<String, Object> details, HttpStatus status) {
        return BaseResponse.<T>builder()
                .success(false)
                .error(ErrorData.of(code, message, details))
                .status(status)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> BaseResponse<T> validationError(String message, Map<String, String> validationErrors) {
        return BaseResponse.<T>builder()
                .success(false)
                .error(ErrorData.validationError(message, validationErrors))
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
