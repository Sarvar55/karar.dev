package org.karar.dev.common.security.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.karar.dev.domain.base.BaseResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final String APPLICATION_JSON_CONTENT_TYPE = "application/json";
    private static final int UNAUTHORIZED_STATUS = HttpServletResponse.SC_UNAUTHORIZED;
    private static final String UNAUTHORIZED_CODE = String.valueOf(UNAUTHORIZED_STATUS);

    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        response.setContentType(APPLICATION_JSON_CONTENT_TYPE);
        response.setStatus(UNAUTHORIZED_STATUS);

        writeErrorResponse(response, BaseResponse.error(authException));
    }

    private void writeErrorResponse(HttpServletResponse response, BaseResponse<?> errorData) throws IOException {
        response.getWriter().write(objectMapper.writeValueAsString(errorData));
    }
}
