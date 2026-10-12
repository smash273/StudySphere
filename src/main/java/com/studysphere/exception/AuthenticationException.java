package com.studysphere.exception;

/**
 * Thrown when user authentication fails due to invalid credentials, deactivated account, etc.
 */
public class AuthenticationException extends StudySphereException {
    private static final long serialVersionUID = 1L;

    public AuthenticationException(String message) {
        super("AUTH_FAILED", message, message);
    }
}
