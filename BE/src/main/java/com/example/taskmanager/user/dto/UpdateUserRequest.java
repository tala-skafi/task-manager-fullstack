package com.example.taskmanager.user.dto;

import com.example.taskmanager.common.Role;
import com.example.taskmanager.common.UserStatus;
import com.example.taskmanager.common.ValidationPatterns;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/** Username is immutable; a blank password leaves the current one unchanged. */
public record UpdateUserRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotNull(message = "Role is required")
        Role role,

        @NotNull(message = "Status is required")
        UserStatus status,

        @Pattern(regexp = ValidationPatterns.PASSWORD, message = ValidationPatterns.PASSWORD_MESSAGE)
        String password
) {
}
