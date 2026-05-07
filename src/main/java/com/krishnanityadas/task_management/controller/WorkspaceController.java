package com.krishnanityadas.task_management.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.krishnanityadas.task_management.dto.authentication.SignUpResponse;
import com.krishnanityadas.task_management.dto.workspace.request.WorkspaceRequest;
import com.krishnanityadas.task_management.dto.workspace.response.WorkspaceResponse;
import com.krishnanityadas.task_management.model.User;
import com.krishnanityadas.task_management.model.Workspace;
import com.krishnanityadas.task_management.service.CurrentUserService;
import com.krishnanityadas.task_management.service.WorkspaceService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@CrossOrigin("https://task-management-60k8d7l35-lavish-singlas-projects.vercel.app") 
public class WorkspaceController {

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private WorkspaceService workspaceService;


    @Operation(summary = "Create a new workspace")
    @PostMapping("/workspace")
    public ResponseEntity<WorkspaceResponse> createWorkspace(
            @Valid @RequestBody WorkspaceRequest workspace) {
        User currentUser = currentUserService.getCurrentUser();
        WorkspaceResponse createdWorkspace =
                workspaceService.createWorkspace(workspace, currentUser);
        return new ResponseEntity<>(createdWorkspace, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all workspaces")
    @GetMapping("/workspaces")
    public ResponseEntity<List<WorkspaceResponse>> getWorkspaces() {
        User currentUser = currentUserService.getCurrentUser();
        List<WorkspaceResponse> workspaces = workspaceService.getWorkspaces(currentUser);
        return new ResponseEntity<>(workspaces, HttpStatus.OK);
    }

    @Operation(summary = "Add a user to a workspace")
    @PostMapping("/workspace/{workspaceId}/add/users")
    public ResponseEntity<WorkspaceResponse> addUserToWorkspace(@PathVariable Integer workspaceId,
            @RequestBody List<SignUpResponse> users ) {
        User currentUser = currentUserService.getCurrentUser();
        WorkspaceResponse workspace =
                workspaceService.addUserToWorkspace(workspaceId, users, currentUser);
        return new ResponseEntity<>(workspace, HttpStatus.OK);
    }

    @Operation(summary = "Delete a workspace")
    @DeleteMapping("/workspace/{workspaceId}")
    public ResponseEntity<WorkspaceResponse> deleteWorkspace(@PathVariable Integer workspaceId) {
        User currentUser = currentUserService.getCurrentUser();
        WorkspaceResponse deletedWorkspace = workspaceService.deleteWorkspace(workspaceId, currentUser);
        return new ResponseEntity<>(deletedWorkspace, HttpStatus.OK);
    }

    @GetMapping("/workspace/{workspaceId}")
    public ResponseEntity<WorkspaceResponse> getWorkspace(@PathVariable Integer workspaceId) {
        User currentUser = currentUserService.getCurrentUser();
        Workspace workspace = workspaceService.getWorkspace(workspaceId, currentUser);
        return new ResponseEntity<>(WorkspaceResponse.from(workspace), HttpStatus.OK);
    }

}
