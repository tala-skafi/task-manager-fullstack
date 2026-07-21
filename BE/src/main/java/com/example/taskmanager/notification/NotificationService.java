package com.example.taskmanager.notification;

import com.example.taskmanager.common.Role;
import com.example.taskmanager.common.exception.ResourceNotFoundException;
import com.example.taskmanager.notification.dto.NotificationResponse;
import com.example.taskmanager.task.Task;
import com.example.taskmanager.user.User;
import com.example.taskmanager.user.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository,
                               SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    /** Notifies the task's assignee and every admin (a task change concerns both). */
    public void notifyTask(Task task, String type, String message) {
        Map<Long, User> recipients = new LinkedHashMap<>();
        if (task.getAssignedUser() != null) {
            recipients.put(task.getAssignedUser().getId(), task.getAssignedUser());
        }
        admins().forEach(admin -> recipients.put(admin.getId(), admin));
        recipients.values().forEach(user -> dispatch(user, type, message, task.getId()));
    }

    /** Notifies every admin; regular users are not told about other users' changes. */
    public void notifyUser(String type, String message) {
        admins().forEach(admin -> dispatch(admin, type, message, null));
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> listRecent(String username) {
        User user = findUser(username);
        return notificationRepository.findTop20ByRecipientIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(NotificationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public long unreadCount(String username) {
        return notificationRepository.countByRecipientIdAndReadFalse(findUser(username).getId());
    }

    @Transactional
    public void markRead(Long id, String username) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id " + id));
        if (!notification.getRecipient().getUsername().equals(username)) {
            throw new AccessDeniedException("You cannot change this notification");
        }
        notification.setRead(true);
    }

    @Transactional
    public void markAllRead(String username) {
        notificationRepository.markAllReadForRecipient(findUser(username).getId());
    }

    /** Persists the notification and pushes it to the recipient's WebSocket queue. */
    private void dispatch(User recipient, String type, String message, Long relatedTaskId) {
        if (recipient.getUsername().equals(currentUsername())) {
            return; // don't notify people about their own actions
        }
        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setType(type);
        notification.setMessage(message);
        notification.setRelatedTaskId(relatedTaskId);
        Notification saved = notificationRepository.save(notification);
        messagingTemplate.convertAndSendToUser(
                recipient.getUsername(), "/queue/notifications", NotificationResponse.from(saved));
    }

    private List<User> admins() {
        return userRepository.search(null, Role.ADMIN, null);
    }

    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    private String currentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }
}
