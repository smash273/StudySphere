package com.studysphere.exception;

/**
 * Thrown when an uploaded academic file is empty, corrupted, unreadable, or exceeds limits.
 */
public class InvalidFileException extends StudySphereException {
    private static final long serialVersionUID = 1L;

    public InvalidFileException(String message) {
        super("INVALID_FILE", message, message);
    }

    public InvalidFileException(String message, String userFriendlyMessage) {
        super("INVALID_FILE", message, userFriendlyMessage);
    }

    public InvalidFileException(String message, Throwable cause) {
        super("INVALID_FILE", message, message, cause);
    }
}
