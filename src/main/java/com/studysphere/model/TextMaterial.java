package com.studysphere.model;

/**
 * Concrete StudyMaterial subclass for Plain Text (TXT) files.
 * Demonstrates Polymorphism and Specialized Behavior.
 */
public class TextMaterial extends StudyMaterial {
    private static final long serialVersionUID = 1L;

    private int lineCount;
    private String encoding;

    public TextMaterial() {
        super();
        this.encoding = "UTF-8";
    }

    public TextMaterial(int userId, String title, String originalFilename, long fileSizeBytes, String storagePath,
                        int lineCount, String encoding) {
        super(userId, title, originalFilename, fileSizeBytes, storagePath);
        this.lineCount = lineCount;
        this.encoding = encoding != null ? encoding : "UTF-8";
    }

    @Override
    public String getMaterialType() {
        return "TXT";
    }

    @Override
    public int estimateProcessingTimeSeconds() {
        return Math.max(1, lineCount / 100);
    }

    public int getLineCount() {
        return lineCount;
    }

    public void setLineCount(int lineCount) {
        this.lineCount = lineCount;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}
