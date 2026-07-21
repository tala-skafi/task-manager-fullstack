package com.example.taskmanager.task;

import com.example.taskmanager.activity.ActivityActions;
import com.example.taskmanager.activity.ActivityService;
import com.example.taskmanager.common.Role;
import com.example.taskmanager.common.TaskStatus;
import com.example.taskmanager.common.exception.ResourceNotFoundException;
import com.example.taskmanager.notification.NotificationService;
import com.example.taskmanager.notification.NotificationTypes;
import com.example.taskmanager.task.dto.CreateTaskRequest;
import com.example.taskmanager.task.dto.TaskResponse;
import com.example.taskmanager.task.dto.UpdateTaskRequest;
import com.example.taskmanager.user.User;
import com.example.taskmanager.user.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final ActivityService activityService;
    private final NotificationService notificationService;

    public TaskService(TaskRepository taskRepository,
                       UserService userService,
                       ActivityService activityService,
                       NotificationService notificationService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
        this.activityService = activityService;
        this.notificationService = notificationService;
    }

    /** Admins see every task; regular users see only the tasks assigned to them. */
    @Transactional(readOnly = true)
    public List<TaskResponse> listFor(String username) {        User currentUser = userService.findByUsername(username);
        List<Task> tasks = isAdmin(currentUser)
                ? taskRepository.findAllByOrderByCreatedAtDesc()
                : taskRepository.findByAssignedUserIdOrderByCreatedAtDesc(currentUser.getId());
        return tasks.stream().map(TaskResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse get(Long id, String username) {
        return TaskResponse.from(getAccessibleTask(id, username));
    }

    @Transactional
    public TaskResponse create(CreateTaskRequest request) {
        Task task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status() != null ? request.status() : TaskStatus.PENDING);
        task.setDueDate(request.dueDate());
        task.setAssignedUser(resolveAssignee(request.assignedUserId()));
        Task saved = taskRepository.save(task);
        activityService.record(ActivityActions.TASK_CREATED, "Created task '" + saved.getTitle() + "'");
        if (saved.getAssignedUser() != null) {
            notificationService.notifyTask(saved, NotificationTypes.TASK_ASSIGNED,
                    "Task '" + saved.getTitle() + "' was assigned to " + saved.getAssignedUser().getName());
        }
        return TaskResponse.from(saved);
    }

    @Transactional
    public TaskResponse update(Long id, UpdateTaskRequest request) {
        Task task = findById(id);
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status());
        task.setDueDate(request.dueDate());
        task.setAssignedUser(resolveAssignee(request.assignedUserId()));
        Task saved = taskRepository.save(task);
        activityService.record(ActivityActions.TASK_UPDATED, "Updated task '" + saved.getTitle() + "'");
        return TaskResponse.from(saved);
    }

    @Transactional
    public TaskResponse updateStatus(Long id, TaskStatus status, String username) {
        Task task = getAccessibleTask(id, username);
        task.setStatus(status);
        Task saved = taskRepository.save(task);
        activityService.record(ActivityActions.TASK_STATUS_UPDATED,
                "Task '" + saved.getTitle() + "' status changed to " + status);
        notificationService.notifyTask(saved, NotificationTypes.TASK_STATUS,
                "Task '" + saved.getTitle() + "' status changed to " + status);
        return TaskResponse.from(saved);
    }

    @Transactional
    public void delete(Long id) {
        Task task = findById(id);
        taskRepository.delete(task);
        activityService.record(ActivityActions.TASK_DELETED, "Deleted task '" + task.getTitle() + "'");
    }

    /** Loads a task only if the current user may see it (admin or its assignee), else 403. */
    @Transactional(readOnly = true)
    public Task getAccessibleTask(Long id, String username) {
        Task task = findById(id);
        User currentUser = userService.findByUsername(username);
        if (!isAdmin(currentUser) && !isAssignedTo(task, currentUser)) {
            throw new AccessDeniedException("You do not have access to this task");
        }
        return task;
    }

    private Task findById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id " + id));
    }

    private User resolveAssignee(Long assignedUserId) {
        return assignedUserId != null ? userService.findById(assignedUserId) : null;
    }

    private boolean isAdmin(User user) {
        return user.getRole() == Role.ADMIN;
    }

    private boolean isAssignedTo(Task task, User user) {
        return task.getAssignedUser() != null
                && task.getAssignedUser().getId().equals(user.getId());
    }
}
