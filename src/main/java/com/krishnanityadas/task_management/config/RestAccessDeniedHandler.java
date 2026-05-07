package com.krishnanityadas.task_management.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {
    private static final Logger logger = LoggerFactory.getLogger(RestAccessDeniedHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        String reason = "Authenticated user does not have permission to access this resource.";

        if (accessDeniedException != null && accessDeniedException.getMessage() != null
                && !accessDeniedException.getMessage().isBlank()) {
            reason = reason + " Security detail: " + accessDeniedException.getMessage();
        }

        logger.warn("Forbidden request rejected. method={}, path={}, reason={}", request.getMethod(),
                request.getRequestURI(), reason);
        SecurityErrorResponseWriter.write(objectMapper, request, response, HttpStatus.FORBIDDEN, reason);
    }
}
