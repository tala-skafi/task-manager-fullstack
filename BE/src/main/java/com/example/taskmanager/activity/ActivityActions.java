package com.example.taskmanager.activity;

/**
 * The set of actions recorded in the activity log. Kept as constants so the same
 * labels are used everywhere they are written.
 */
public final class ActivityActions {

    public static final String LOGIN = "LOGIN";
    public static final String LOGOUT = "LOGOUT";
    public static final String USER_CREATED = "USER_CREATED";
    public static final String USER_UPDATED = "USER_UPDATED";
    public static final String USER_DELETED = "USER_DELETED";
    public static final String TASK_CREATED = "TASK_CREATED";
    public static final String TASK_UPDATED = "TASK_UPDATED";
    public static final String TASK_STATUS_UPDATED = "TASK_STATUS_UPDATED";
    public static final String TASK_DELETED = "TASK_DELETED";

    private ActivityActions() {
    }
}
