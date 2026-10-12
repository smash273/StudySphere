package com.studysphere.model;

/**
 * Concrete StudyMaterial subclass for Portable Document Format (PDF) files.
 * Demonstrates Polymorphism and Specialized Behavior.
 */
public class PDFMaterial extends StudyMaterial {
    private static final long serialVersionUID = 1L;

    private int pageCount;
    private boolean isEncrypted;

    public PDFMaterial() {
        super();
    }

    public PDFMaterial(int userId, String title, String originalFilename, long fileSizeBytes, String storagePath,
                       int pageCount, boolean isEncrypted) {
        super(userId, title, originalFilename, fileSizeBytes, storagePath);
        this.pageCount = pageCount;
        this.isEncrypted = isEncrypted;
    }

    @Override
    public String getMaterialType() {
        return "PDF";
    }

    @Override
    public int estimateProcessingTimeSeconds() {
        // PDF parsing and page-by-page text layout analysis
        return Math.max(2, pageCount * 1);
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }

    public void setEncrypted(boolean encrypted) {
        isEncrypted = encrypted;
    }
}
