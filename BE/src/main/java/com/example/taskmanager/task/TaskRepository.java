package com.example.taskmanager.task;

import com.example.taskmanager.common.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByOrderByCreatedAtDesc();

    List<Task> findByAssignedUserIdOrderByCreatedAtDesc(Long userId);

    long countByStatus(TaskStatus status);
}
