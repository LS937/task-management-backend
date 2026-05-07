package com.krishnanityadas.task_management.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final Logger logger = LoggerFactory.getLogger(RestAuthenticationEntryPoint.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        String reasonFromFilter = (String) request.getAttribute(SecurityErrorAttributes.AUTH_FAILURE_REASON);
        String reason = (reasonFromFilter != null && !reasonFromFilter.isBlank())
                ? reasonFromFilter
                : "Authentication is required. Provide a valid, non-expired 'token' cookie.";

        if (authException != null && authException.getMessage() != null && !authException.getMessage().isBlank()) {
            reason = reason + " Security detail: " + authException.getMessage();
        }

        logger.warn("Unauthorized request rejected. method={}, path={}, reason={}", request.getMethod(),
                request.getRequestURI(), reason);
        SecurityErrorResponseWriter.write(objectMapper, request, response, HttpStatus.UNAUTHORIZED, reason);
    }
}
