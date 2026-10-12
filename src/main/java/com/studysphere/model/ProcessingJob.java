package com.studysphere.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity representing a multithreaded background processing job for document analysis.
 */
public class ProcessingJob implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id; // UUID string
    private int documentId;
    private int userId;
    private ProcessingStatus status;
    private int progressPercent; // 0 to 100
    private String currentStage;
    private String errorMessage;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    public ProcessingJob() {
        this.status = ProcessingStatus.QUEUED;
        this.progressPercent = 0;
        this.startedAt = LocalDateTime.now();
    }

    public ProcessingJob(String id, int documentId, int userId) {
        this();
        this.id = id;
        this.documentId = documentId;
        this.userId = userId;
        this.currentStage = ProcessingStatus.QUEUED.getDescription();
    }

    public void updateProgress(ProcessingStatus status, int progressPercent, String currentStage) {
        this.status = status;
        this.progressPercent = Math.min(100, Math.max(0, progressPercent));
        this.currentStage = currentStage;
        if (status == ProcessingStatus.COMPLETED || status == ProcessingStatus.FAILED) {
            this.completedAt = LocalDateTime.now();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getDocumentId() {
        return documentId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public ProcessingStatus getStatus() {
        return status;
    }

    public void setStatus(ProcessingStatus status) {
        this.status = status;
    }

    public int getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(int progressPercent) {
        this.progressPercent = progressPercent;
    }

    public String getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(String currentStage) {
        this.currentStage = currentStage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
