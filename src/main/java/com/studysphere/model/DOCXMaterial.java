package com.studysphere.model;

/**
 * Concrete StudyMaterial subclass for Microsoft Word (DOCX) files.
 * Demonstrates Polymorphism and Specialized Behavior.
 */
public class DOCXMaterial extends StudyMaterial {
    private static final long serialVersionUID = 1L;

    private int paragraphCount;
    private int tableCount;

    public DOCXMaterial() {
        super();
    }

    public DOCXMaterial(int userId, String title, String originalFilename, long fileSizeBytes, String storagePath,
                        int paragraphCount, int tableCount) {
        super(userId, title, originalFilename, fileSizeBytes, storagePath);
        this.paragraphCount = paragraphCount;
        this.tableCount = tableCount;
    }

    @Override
    public String getMaterialType() {
        return "DOCX";
    }

    @Override
    public int estimateProcessingTimeSeconds() {
        return Math.max(2, (paragraphCount / 20) + (tableCount * 2));
    }

    public int getParagraphCount() {
        return paragraphCount;
    }

    public void setParagraphCount(int paragraphCount) {
        this.paragraphCount = paragraphCount;
    }

    public int getTableCount() {
        return tableCount;
    }

    public void setTableCount(int tableCount) {
        this.tableCount = tableCount;
    }
}
