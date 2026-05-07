package com.krishnanityadas.task_management.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.krishnanityadas.task_management.dto.task.request.TaskRequest;
import com.krishnanityadas.task_management.dto.task.response.TaskResponse;
import com.krishnanityadas.task_management.enums.Status;
import com.krishnanityadas.task_management.exception.ResourceNotFoundException;
import com.krishnanityadas.task_management.exception.UserNotFoundException;
import com.krishnanityadas.task_management.model.Task;
import com.krishnanityadas.task_management.model.User;
import com.krishnanityadas.task_management.model.Workspace;
import com.krishnanityadas.task_management.repo.TaskRepo;
import com.krishnanityadas.task_management.repo.UserRepo;
import com.krishnanityadas.task_management.repo.WorkspaceRepo;

@Service
public class TaskService {

    @Autowired
    private TaskRepo taskRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private WorkspaceRepo workspaceRepo;


    public TaskResponse createTask(TaskRequest taskRequest, Integer workspaceId, User currentUser) {

        User assignedUser = userRepo.findById(taskRequest.assignedTo())
                .orElseThrow(() -> new UserNotFoundException("Assigned user does not exist"));
        if (assignedUser.getId() == currentUser.getId()) {
            throw new RuntimeException("User cannot assign the task to itself");
        }


        Workspace workspace = workspaceRepo.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace does not exist"));

        if (!assignedUser.getWorkspaces().contains(workspace)) {
            throw new RuntimeException("Assigned user does not belong to the workspace");
        }

        Task task = new Task();
        task.setTitle(taskRequest.title());
        task.setDescription(taskRequest.description());
        task.setAssignedTo(assignedUser);
        task.setCreatedBy(currentUser);
        if (taskRequest.dueDate() != null) {
            task.setDueDate(taskRequest.dueDate());
        }
        if (taskRequest.status() != null) {
            task.setStatus(taskRequest.status());
        } 
        task.setWorkspace(workspace);
        Task createdTask = taskRepo.save(task);

        return TaskResponse.from(createdTask);
    }

    public List<TaskResponse> getAllTasks(Integer workspaceId, User currentUser) {
        try {
            Workspace workspace = workspaceRepo.findById(workspaceId).orElseThrow(() -> new ResourceNotFoundException("Workspace does not exist"));
            if (!workspace.getUsers().stream()
                    .anyMatch(user -> user.getId() == currentUser.getId())) {
                throw new RuntimeException("You don't have access to the workspace");
            }
                

            List<Task> tasks = taskRepo.findAllByWorkspaceId(workspaceId);
            return TaskResponse.fromList(tasks);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get all tasks: " + e.getMessage());
        }
    }

    public TaskResponse updateTask(TaskRequest updatedTask, Integer workspaceId, Integer taskId) {
        Task originalTask = taskRepo.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task does not exist"));
        if (originalTask.getWorkspace().getId() != workspaceId) {
            throw new ResourceNotFoundException("Task is not present in the specified workspace.");
        }
        User assignedUser = userRepo.findById(updatedTask.assignedTo())
                .orElseThrow(() -> new UserNotFoundException("Assinged user does not exist"));
        originalTask.setTitle(updatedTask.title());
        originalTask.setDescription(updatedTask.description());
        if (updatedTask.dueDate() != null) {
            originalTask.setDueDate(updatedTask.dueDate());
        }
        originalTask.setAssignedTo(assignedUser);
        taskRepo.save(originalTask);
        return TaskResponse.from(originalTask);
    }

    public TaskResponse deleteTask(Integer workspaceId, Integer taskId) {
        Workspace workspace = workspaceRepo.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace does not exist"));
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task does not exist"));
        if (task.getWorkspace().getId() != workspace.getId()) {
            throw new ResourceNotFoundException("Task is not present in the specified workspace.");
        }
        taskRepo.delete(task);
        return TaskResponse.from(task);
    }

    public TaskResponse updateStaus(Status newStatus, Integer workspaceId, Integer taskId) {
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task does not exist"));
        if (task.getWorkspace().getId() != workspaceId) {
            throw new ResourceNotFoundException("Task is not present in the specified workspace.");
        }
        task.setStatus(newStatus);
        Task updatedTask = taskRepo.save(task);
        return TaskResponse.from(updatedTask);
    }
}
