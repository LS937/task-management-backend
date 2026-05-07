package com.krishnanityadas.task_management.dto.workspace.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WorkspaceRequest(@NotBlank(message = "Workspace name cannot be empty") @Size(max = 50,
                message = "Workspace name cannot exceed 50 characters.") String name) {

}
