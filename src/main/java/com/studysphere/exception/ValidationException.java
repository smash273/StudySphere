package com.studysphere.exception;

/**
 * Thrown when client or entity validation fails.
 */
public class ValidationException extends StudySphereException {
    private static final long serialVersionUID = 1L;

    public ValidationException(String message) {
        super("VALIDATION_ERROR", message, message);
    }
}
