package com.example.taskmanager.stats;

/**
 * Aggregated counts for the admin dashboard overview.
 */
public record StatsResponse(
        long totalUsers,
        long totalTasks,
        long pendingTasks,
        long inProgressTasks,
        long completedTasks
) {
}
