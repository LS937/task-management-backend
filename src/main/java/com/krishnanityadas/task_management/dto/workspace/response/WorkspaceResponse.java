package com.krishnanityadas.task_management.dto.workspace.response;

import java.util.List;
import com.krishnanityadas.task_management.dto.authentication.SignUpResponse;
import com.krishnanityadas.task_management.model.Workspace;

public record WorkspaceResponse(String name, List<SignUpResponse> users, Integer workspaceId) {

    public static WorkspaceResponse getDeletedWorkspace(String name, Integer workspaceId) {
        return new WorkspaceResponse(name, List.of(), workspaceId);
    }

    public static WorkspaceResponse from(Workspace workspace) {
        List<SignUpResponse> signupResponse = SignUpResponse.fromList(workspace.getUsers());
        return new WorkspaceResponse(workspace.getName(), signupResponse, workspace.getId());
    }

    public static List<WorkspaceResponse> fromList(List<Workspace> workspaces) {
        return workspaces.stream().map(WorkspaceResponse::from).toList();
    }
}
