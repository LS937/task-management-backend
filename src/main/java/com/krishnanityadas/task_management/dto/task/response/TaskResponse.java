package com.krishnanityadas.task_management.dto.task.response;

import java.util.Date;
import java.util.List;
import com.krishnanityadas.task_management.enums.Status;
import com.krishnanityadas.task_management.model.Task;

public record TaskResponse(String title, String description, Status status, Date dueDate, Integer taskId) {
        public static TaskResponse from(Task task) {
                return new TaskResponse(task.getTitle(), task.getDescription(), task.getStatus(),
                                task.getDueDate(), task.getId());
        }

        public static List<TaskResponse> fromList(List<Task> tasks) {
                return tasks.stream().map(TaskResponse::from).toList();
        }

}
