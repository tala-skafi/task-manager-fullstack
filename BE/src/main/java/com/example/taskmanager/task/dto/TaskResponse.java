package com.example.taskmanager.task.dto;

import com.example.taskmanager.common.TaskStatus;
import com.example.taskmanager.task.Task;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus status,
        LocalDate dueDate,
        Long assignedUserId,
        String assignedUserName,
        LocalDateTime createdAt
) {
    public static TaskResponse from(Task task) {
        var assignee = task.getAssignedUser();
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDueDate(),
                assignee != null ? assignee.getId() : null,
                assignee != null ? assignee.getName() : null,
                task.getCreatedAt());
    }
}
