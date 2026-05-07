package com.krishnanityadas.task_management.dto.authentication;

public record LoginResponse(
    String token
) {
    public static LoginResponse from(String token) {
        return new LoginResponse(token);
    }
}
