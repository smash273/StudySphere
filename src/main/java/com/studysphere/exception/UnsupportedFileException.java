package com.studysphere.exception;

/**
 * Thrown when an uploaded file format is not supported by StudySphere (must be PDF, DOCX, or TXT).
 */
public class UnsupportedFileException extends StudySphereException {
    private static final long serialVersionUID = 1L;

    public UnsupportedFileException(String extension) {
        super("UNSUPPORTED_FORMAT",
              "File format '." + extension + "' is not supported. Please upload PDF, DOCX, or TXT.",
              "Only PDF (.pdf), Word (.docx), and Plain Text (.txt) files are supported.");
    }
}
