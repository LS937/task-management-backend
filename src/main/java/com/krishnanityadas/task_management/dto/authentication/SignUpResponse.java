package com.krishnanityadas.task_management.dto.authentication;

import java.util.List;
import com.krishnanityadas.task_management.enums.Role;
import com.krishnanityadas.task_management.model.User;

public record SignUpResponse(String userName, String email, Role userRole, Integer userId) {
    public static SignUpResponse from(User user) {
        return new SignUpResponse(user.getUserName(), user.getEmail(), user.getUserRole(), user.getId());
    }

    public static List<SignUpResponse> fromList(List<User> users) {
        return users.stream().map(SignUpResponse::from).toList();
    }
}
