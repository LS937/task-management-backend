package com.krishnanityadas.task_management.dto.authentication;

import java.util.Optional;
import com.krishnanityadas.task_management.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Username cannot be blank.") @Size(max = 50,
                message = "Username cannot exceed 50 characters.") String userName,
        @NotBlank(message = "Email cannot be blank.") @Email(message = "Invalid email") String email,
        @NotBlank(message = "Password cannot be blank.") @Size(max = 254,
                message = "Email cannot exceed 254 characters.") String password,
        Optional<Role> userRole

) {

}
