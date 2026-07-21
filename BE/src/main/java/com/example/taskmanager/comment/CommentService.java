package com.example.taskmanager.comment;

import com.example.taskmanager.comment.dto.CommentResponse;
import com.example.taskmanager.task.Task;
import com.example.taskmanager.task.TaskService;
import com.example.taskmanager.user.User;
import com.example.taskmanager.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskService taskService;
    private final UserService userService;

    public CommentService(CommentRepository commentRepository,
                          TaskService taskService,
                          UserService userService) {
        this.commentRepository = commentRepository;
        this.taskService = taskService;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> listForTask(Long taskId, String username) {
        taskService.getAccessibleTask(taskId, username);
        return commentRepository.findByTaskIdOrderByCreatedAtAsc(taskId).stream()
                .map(CommentResponse::from)
                .toList();
    }

    @Transactional
    public CommentResponse addComment(Long taskId, String content, String username) {
        Task task = taskService.getAccessibleTask(taskId, username);
        User author = userService.findByUsername(username);

        Comment comment = new Comment();
        comment.setTask(task);
        comment.setUser(author);
        comment.setContent(content);

        return CommentResponse.from(commentRepository.save(comment));
    }
}
