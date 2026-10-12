package com.studysphere.model;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Abstract class representing academic study material, demonstrating Abstraction and Template Method.
 */
public abstract class StudyMaterial implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int userId;
    private String title;
    private String originalFilename;
    private long fileSizeBytes;
    private String storagePath;
    private LocalDateTime uploadedAt;

    public StudyMaterial() {
        this.uploadedAt = LocalDateTime.now();
    }

    public StudyMaterial(int userId, String title, String originalFilename, long fileSizeBytes, String storagePath) {
        this();
        this.userId = userId;
        this.title = title;
        this.originalFilename = originalFilename;
        this.fileSizeBytes = fileSizeBytes;
        this.storagePath = storagePath;
    }

    /**
     * Polymorphic method to return specific material format type.
     * @return PDF, DOCX, or TXT
     */
    public abstract String getMaterialType();

    /**
     * Polymorphic estimation of processing complexity based on format characteristics.
     * @return estimated processing duration in seconds
     */
    public abstract int estimateProcessingTimeSeconds();

    /**
     * Template method formatting file size into human-readable notation (KB/MB).
     */
    public String getFormattedSize() {
        if (fileSizeBytes < 1024) {
            return fileSizeBytes + " B";
        } else if (fileSizeBytes < 1024 * 1024) {
            return String.format("%.2f KB", fileSizeBytes / 1024.0);
        } else {
            return String.format("%.2f MB", fileSizeBytes / (1024.0 * 1024.0));
        }
    }

    public boolean existsOnDisk() {
        if (storagePath == null) return false;
        File f = new File(storagePath);
        return f.exists() && f.isFile();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public void setFileSizeBytes(long fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
