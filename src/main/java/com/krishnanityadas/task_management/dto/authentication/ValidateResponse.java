package com.krishnanityadas.task_management.dto.authentication;

public record ValidateResponse(
    Boolean isUserAuthenticated
) {
    public static ValidateResponse from(Boolean isUserAuthenticated){
        return new ValidateResponse(isUserAuthenticated);
    }
}
