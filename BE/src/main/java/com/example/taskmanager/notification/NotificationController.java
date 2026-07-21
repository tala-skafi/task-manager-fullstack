package com.example.taskmanager.notification;

import com.example.taskmanager.notification.dto.NotificationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** A user's own notifications; each endpoint acts on the authenticated user. */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public List<NotificationResponse> list(Authentication authentication) {
        return notificationService.listRecent(authentication.getName());
    }

    @GetMapping("/unread-count")
    public Map<String, Long> unreadCount(Authentication authentication) {
        return Map.of("count", notificationService.unreadCount(authentication.getName()));
    }

    @PatchMapping("/{id}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markRead(@PathVariable Long id, Authentication authentication) {
        notificationService.markRead(id, authentication.getName());
    }

    @PatchMapping("/read-all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAllRead(Authentication authentication) {
        notificationService.markAllRead(authentication.getName());
    }
}
