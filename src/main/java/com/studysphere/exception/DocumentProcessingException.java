package com.studysphere.exception;

/**
 * Thrown when an error occurs during text extraction, segment chunking, or document analysis.
 */
public class DocumentProcessingException extends StudySphereException {
    private static final long serialVersionUID = 1L;

    public DocumentProcessingException(String message) {
        super("PROCESSING_FAILED", message, "An error occurred while processing the academic document.");
    }

    public DocumentProcessingException(String message, String userFriendlyMessage) {
        super("PROCESSING_FAILED", message, userFriendlyMessage);
    }

    public DocumentProcessingException(String message, Throwable cause) {
        super("PROCESSING_FAILED", message, "An error occurred while processing the academic document.", cause);
    }
}
