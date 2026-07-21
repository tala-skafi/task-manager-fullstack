package com.example.taskmanager.stats;

import com.example.taskmanager.common.TaskStatus;
import com.example.taskmanager.task.TaskRepository;
import com.example.taskmanager.user.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public StatsController(UserRepository userRepository, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public StatsResponse getStats() {
        return new StatsResponse(
                userRepository.count(),
                taskRepository.count(),
                taskRepository.countByStatus(TaskStatus.PENDING),
                taskRepository.countByStatus(TaskStatus.IN_PROGRESS),
                taskRepository.countByStatus(TaskStatus.COMPLETED));
    }
}
