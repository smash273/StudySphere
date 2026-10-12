package com.studysphere.exception;

/**
 * Thrown when an error occurs during database connectivity or SQL query execution.
 */
public class DatabaseException extends StudySphereException {
    private static final long serialVersionUID = 1L;

    public DatabaseException(String message) {
        super("DATABASE_ERROR", message, "A database error occurred. Please verify your connection.");
    }

    public DatabaseException(String message, Throwable cause) {
        super("DATABASE_ERROR", message, "A database error occurred. Please verify your connection.", cause);
    }
}
