package com.example.taskmanager.user.dto;

import com.example.taskmanager.common.Role;
import com.example.taskmanager.common.UserStatus;
import com.example.taskmanager.user.User;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String name,
        String username,
        String email,
        Role role,
        UserStatus status,
        LocalDateTime createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt());
    }
}
