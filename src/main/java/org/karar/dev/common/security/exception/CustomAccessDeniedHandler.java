package org.karar.dev.common.security.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.karar.dev.common.dto.BaseResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;


import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private static final String APPLICATION_JSON_CONTENT_TYPE = "application/json";
    private static final int FORBIDDEN_STATUS = HttpServletResponse.SC_FORBIDDEN;

    private final ObjectMapper objectMapper;

    public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        response.setStatus(FORBIDDEN_STATUS);
        response.setContentType(APPLICATION_JSON_CONTENT_TYPE);

        writeErrorResponse(response, BaseResponse.error(accessDeniedException));
    }

    private void writeErrorResponse(HttpServletResponse response, BaseResponse<?> errorData) throws IOException {
        response.getWriter().write(objectMapper.writeValueAsString(errorData));
    }
}
