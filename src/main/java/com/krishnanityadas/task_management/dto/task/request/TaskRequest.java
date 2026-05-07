package com.krishnanityadas.task_management.dto.task.request;

import java.util.Date;
import java.util.Optional;

import com.krishnanityadas.task_management.enums.Status;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TaskRequest(
        @NotBlank(message = "Title cannot be blank") @Size(max = 50,
                message = "Title cannot exceed 50 characters") String title,

        @Size(max = 200, message = "Description cannot exceed 200 characters") String description,

        Status status,

        @Future(message = "Due date must be in the future") Date dueDate,

        @NotNull(message = "Assigned user is required") Integer assignedTo) {
}
