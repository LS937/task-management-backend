package com.krishnanityadas.task_management.dto.exception;

public record ErrorResponse(String message, int status) {

    public static ErrorResponse from(String message, int status) {
        return new ErrorResponse(message, status);
    }

}
