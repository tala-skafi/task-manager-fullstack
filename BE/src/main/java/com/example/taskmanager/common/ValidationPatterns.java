package com.example.taskmanager.common;

/**
 * Shared validation rules referenced from bean-validation annotations.
 */
public final class ValidationPatterns {

    /** At least 8 characters, containing at least one letter and one digit. */
    public static final String PASSWORD = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$";

    public static final String PASSWORD_MESSAGE =
            "Password must be at least 8 characters and include a letter and a number";

    private ValidationPatterns() {
    }
}
