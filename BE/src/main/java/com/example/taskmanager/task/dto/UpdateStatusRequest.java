package com.example.taskmanager.task.dto;

import com.example.taskmanager.common.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateStatusRequest(
        @NotNull(message = "Status is required")
        TaskStatus status
) {
}
