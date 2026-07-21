package com.example.taskmanager.activity.dto;

import com.example.taskmanager.activity.ActivityLog;

import java.time.LocalDateTime;

public record ActivityLogResponse(
        Long id,
        String action,
        String details,
        String userName,
        LocalDateTime createdAt
) {
    public static ActivityLogResponse from(ActivityLog log) {
        // The user can be null if the account was later deleted (FK set null).
        String userName = log.getUser() != null ? log.getUser().getName() : "System";
        return new ActivityLogResponse(
                log.getId(),
                log.getAction(),
                log.getDetails(),
                userName,
                log.getCreatedAt());
    }
}
