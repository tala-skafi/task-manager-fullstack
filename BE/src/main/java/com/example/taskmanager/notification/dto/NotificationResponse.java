package com.example.taskmanager.notification.dto;

import com.example.taskmanager.notification.Notification;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        String type,
        String message,
        Long relatedTaskId,
        boolean read,
        LocalDateTime createdAt
) {
    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getType(),
                notification.getMessage(),
                notification.getRelatedTaskId(),
                notification.isRead(),
                notification.getCreatedAt());
    }
}
