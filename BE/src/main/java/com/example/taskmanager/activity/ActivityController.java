package com.example.taskmanager.activity;

import com.example.taskmanager.activity.dto.ActivityLogResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Admin-only audit trail; restricted by the security configuration. */
@RestController
@RequestMapping("/api/activity-logs")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping
    public List<ActivityLogResponse> list() {
        return activityService.getAll();
    }
}
