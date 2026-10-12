package com.studysphere.exception;

/**
 * Root checked exception for all domain-specific errors in StudySphere.
 * Demonstrates Custom Exception Handling and Centralized Error Messaging.
 */
public class StudySphereException extends Exception {
    private static final long serialVersionUID = 1L;

    private final String errorCode;
    private final String userFriendlyMessage;

    public StudySphereException(String message) {
        super(message);
        this.errorCode = "STUDYSPHERE_ERROR";
        this.userFriendlyMessage = message;
    }

    public StudySphereException(String errorCode, String message, String userFriendlyMessage) {
        super(message);
        this.errorCode = errorCode;
        this.userFriendlyMessage = userFriendlyMessage;
    }

    public StudySphereException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.userFriendlyMessage = message;
    }

    public StudySphereException(String errorCode, String message, String userFriendlyMessage, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.userFriendlyMessage = userFriendlyMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getUserFriendlyMessage() {
        return userFriendlyMessage;
    }
}
