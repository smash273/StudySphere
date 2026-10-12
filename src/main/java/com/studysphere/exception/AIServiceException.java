package com.studysphere.exception;

/**
 * Thrown when an error occurs during AI model invocation, parsing, or network communication.
 */
public class AIServiceException extends StudySphereException {
    private static final long serialVersionUID = 1L;

    public AIServiceException(String message) {
        super("AI_SERVICE_ERROR", message, "The AI analysis service encountered an error.");
    }

    public AIServiceException(String message, String userFriendlyMessage) {
        super("AI_SERVICE_ERROR", message, userFriendlyMessage);
    }

    public AIServiceException(String message, Throwable cause) {
        super("AI_SERVICE_ERROR", message, "The AI analysis service encountered an error.", cause);
    }
}
