package com.example.taskmanager.task.dto;

import com.example.taskmanager.common.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/** Status defaults to PENDING when omitted; assignedUserId may be null. */
public record CreateTaskRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 150, message = "Title must be at most 150 characters")
        String title,

        @Size(max = 2000, message = "Description must be at most 2000 characters")
        String description,

        TaskStatus status,

        LocalDate dueDate,

        Long assignedUserId
) {
}
