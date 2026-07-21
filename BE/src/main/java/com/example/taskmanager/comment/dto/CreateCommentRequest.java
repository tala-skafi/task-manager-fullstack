package com.example.taskmanager.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCommentRequest(
        @NotBlank(message = "Comment cannot be empty")
        @Size(max = 2000, message = "Comment must be at most 2000 characters")
        String content
) {
}
