package com.krishnanityadas.task_management.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.krishnanityadas.task_management.dto.task.request.TaskRequest;
import com.krishnanityadas.task_management.dto.task.response.TaskResponse;
import com.krishnanityadas.task_management.enums.Status;
import com.krishnanityadas.task_management.model.User;
import com.krishnanityadas.task_management.service.CurrentUserService;
import com.krishnanityadas.task_management.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@CrossOrigin("https://task-management-60k8d7l35-lavish-singlas-projects.vercel.app") 
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private CurrentUserService currentUserService;

    @Operation(summary = "Create a task in a workspace")
    @PostMapping("/workspace/{workspaceId}/task")
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest taskRequest,
            @PathVariable Integer workspaceId) {
        User currentUser = currentUserService.getCurrentUser();
        TaskResponse createdTask = taskService.createTask(taskRequest, workspaceId, currentUser);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    @Operation(summary = "Get tasks in a workspace")
    @GetMapping("/workspace/{workspaceId}/tasks")
    public ResponseEntity<List<TaskResponse>> getTasks(@PathVariable Integer workspaceId) {
        User currentUser = currentUserService.getCurrentUser();
        List<TaskResponse> tasks = taskService.getAllTasks(workspaceId, currentUser);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @Operation(summary = "Update a task")
    @PostMapping("/workspace/{workspaceId}/task/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(@Valid @RequestBody TaskRequest updatedTask,
            @PathVariable Integer workspaceId, @PathVariable Integer taskId) {

        TaskResponse task = taskService.updateTask(updatedTask, workspaceId, taskId);
        return new ResponseEntity<>(task, HttpStatus.OK);

    }

    @Operation(summary = "Delete a task")
    @PostMapping("/workspace/{workspaceId}/task/delete/{taskId}")
    public ResponseEntity<TaskResponse> deleteTask(@PathVariable Integer workspaceId,
            @PathVariable Integer taskId) {

        TaskResponse task = taskService.deleteTask(workspaceId, taskId);
        return new ResponseEntity<>(task, HttpStatus.OK);

    }

    @Operation(summary = "Update task status")
    @PostMapping("/workspace/{workspaceId}/updateStatus/{taskId}")
    public ResponseEntity<TaskResponse> updateStatus(@RequestParam Status newStatus,
            @PathVariable Integer workspaceId, @PathVariable Integer taskId) {
        TaskResponse task = taskService.updateStaus(newStatus, workspaceId, taskId);
        return new ResponseEntity<>(task, HttpStatus.OK);

    }
}
