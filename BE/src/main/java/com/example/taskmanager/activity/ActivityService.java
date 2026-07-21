package com.example.taskmanager.activity;

import com.example.taskmanager.activity.dto.ActivityLogResponse;
import com.example.taskmanager.user.User;
import com.example.taskmanager.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ActivityService {

    private final ActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;

    public ActivityService(ActivityLogRepository activityLogRepository, UserRepository userRepository) {
        this.activityLogRepository = activityLogRepository;
        this.userRepository = userRepository;
    }

    /** Records an event performed by the current user (resolved from the security context). */
    public void record(String action, String details) {
        record(currentUser(), action, details);
    }

    /** Records an event for a specific user (used at login, before a token exists). */
    public void record(User user, String action, String details) {
        ActivityLog log = new ActivityLog();
        log.setUser(user);
        log.setAction(action);
        log.setDetails(details);
        activityLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<ActivityLogResponse> getAll() {
        return activityLogRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(ActivityLogResponse::from)
                .toList();
    }

    private User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            return null;
        }
        return userRepository.findByUsername(authentication.getName()).orElse(null);
    }
}
