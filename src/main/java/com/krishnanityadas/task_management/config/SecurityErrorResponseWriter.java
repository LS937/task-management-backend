package com.krishnanityadas.task_management.config;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public final class SecurityErrorResponseWriter {
    private SecurityErrorResponseWriter() {
    }

    public static void write(ObjectMapper objectMapper, HttpServletRequest request, HttpServletResponse response,
            HttpStatus status, String reason) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", reason);
        body.put("path", request.getRequestURI());
        body.put("method", request.getMethod());

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
