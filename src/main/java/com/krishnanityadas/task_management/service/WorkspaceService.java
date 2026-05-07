package com.krishnanityadas.task_management.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.krishnanityadas.task_management.dto.authentication.SignUpResponse;
import com.krishnanityadas.task_management.dto.workspace.request.WorkspaceRequest;
import com.krishnanityadas.task_management.dto.workspace.response.WorkspaceResponse;
import com.krishnanityadas.task_management.exception.ResourceNotFoundException;
import com.krishnanityadas.task_management.exception.UserNotFoundException;
import com.krishnanityadas.task_management.model.User;
import com.krishnanityadas.task_management.model.Workspace;
import com.krishnanityadas.task_management.repo.UserRepo;
import com.krishnanityadas.task_management.repo.WorkspaceRepo;

@Service
public class WorkspaceService {
    @Autowired
    private WorkspaceRepo workspaceRepo;

    @Autowired
    private UserRepo userRepo;

    public WorkspaceResponse createWorkspace(WorkspaceRequest workspaceRequest, User currentUser) {
        List<Workspace> workspaces = currentUser.getWorkspaces();
        for (Workspace iterator : workspaces) {
            if (iterator.getName().equals(workspaceRequest.name())) {
                System.out.println("User already have a workspace with same name.");
                throw new RuntimeException("User already have a workspace with same name.");
            }
        }

        Workspace workspace = new Workspace();
        workspace.setName(workspaceRequest.name());

        currentUser.getWorkspaces().add(workspace);
        userRepo.save(currentUser);

        // workspace.getUsers().add(currentUser);
        // workspaceRepo.save(workspace);
        workspace.getUsers().add(currentUser);

        return WorkspaceResponse.from(workspace);
    }

    public WorkspaceResponse addUserToWorkspace(Integer workspaceId, List<SignUpResponse> users,
            User currentUser) {
        Workspace workspace = workspaceRepo.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace not found"));

        for (SignUpResponse user : users) {
            User userToAdd = userRepo.findById(user.userId())
                    .orElseThrow(() -> new UserNotFoundException("User not found"));

            if (workspace.getUsers().contains(userToAdd)) {
                throw new RuntimeException("User already in workspace");
            }

            workspace.getUsers().add(userToAdd);
            userToAdd.getWorkspaces().add(workspace);
            userRepo.save(userToAdd);
            workspaceRepo.save(workspace);
        }
        return WorkspaceResponse.from(workspace);
    }

    public WorkspaceResponse deleteWorkspace(Integer workspaceId, User currentUser) {
        Workspace workspace = workspaceRepo.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace not found"));
        currentUser.getWorkspaces().remove(workspace);
        userRepo.save(currentUser);
        workspaceRepo.deleteById(workspaceId);
        return WorkspaceResponse.getDeletedWorkspace(workspace.getName(), workspace.getId());
    }

    public List<WorkspaceResponse> getWorkspaces(User currentUser) {
        return WorkspaceResponse.fromList(currentUser.getWorkspaces());
    }

    public Workspace getWorkspace(Integer workspaceId, User currentUser) {
        Workspace workspace = workspaceRepo.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace not found"));
        if (!workspace.getUsers().contains(currentUser)) {
            throw new RuntimeException("User already in workspace");
        }
        return workspace;
    }
}
