package com.example.taskmanager.comment;

import com.example.taskmanager.comment.dto.CommentResponse;
import com.example.taskmanager.comment.dto.CreateCommentRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Comments are nested under a task: /api/tasks/{taskId}/comments.
 */
@RestController
@RequestMapping("/api/tasks/{taskId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public List<CommentResponse> list(@PathVariable Long taskId, Authentication authentication) {
        return commentService.listForTask(taskId, authentication.getName());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse add(@PathVariable Long taskId,
                               @Valid @RequestBody CreateCommentRequest request,
                               Authentication authentication) {
        return commentService.addComment(taskId, request.content(), authentication.getName());
    }
}
