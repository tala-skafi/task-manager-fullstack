package com.example.taskmanager.auth;

public record LoginResponse(
        String token,
        Long userId,
        String name,
        String username,
        String role
) {
}
