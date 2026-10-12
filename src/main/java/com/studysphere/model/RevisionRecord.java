package com.studysphere.model;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Entity tracking a student's revision progress for a specific academic topic.
 * Implements revision score heuristic algorithm.
 */
public class RevisionRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int userId;
    private int topicId;
    private int revisionCount;
    private LocalDateTime lastReviewed;
    private double importanceWeight; // 0.0 to 1.0
    private double recencyFactor;    // Decay over days elapsed
    private double revisionScore;    // 0.0 to 100.0
    private RevisionStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Optional transient reference
    private String topicName;
    private String documentTitle;

    public RevisionRecord() {
        this.revisionCount = 0;
        this.importanceWeight = 1.0;
        this.recencyFactor = 1.0;
        this.revisionScore = 0.0;
        this.status = RevisionStatus.NEEDS_REVISION;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public RevisionRecord(int userId, int topicId, double importanceWeight) {
        this();
        this.userId = userId;
        this.topicId = topicId;
        this.importanceWeight = importanceWeight;
    }

    /**
     * Recomputes revisionScore using the heuristic:
     * revisionScore = min(100.0, (revisionCount * 25.0 * recencyFactor * (0.8 + 0.2 * importanceWeight)))
     */
    public void recalculateScore() {
        if (lastReviewed == null || revisionCount == 0) {
            this.recencyFactor = 0.1;
            this.revisionScore = 0.0;
            this.status = RevisionStatus.NEEDS_REVISION;
            return;
        }

        long daysElapsed = Duration.between(lastReviewed, LocalDateTime.now()).toDays();
        // Recency exponential decay heuristic: 1.0 on day 0, decaying by ~15% every 2 days
        this.recencyFactor = Math.max(0.15, Math.exp(-0.08 * daysElapsed));

        double baseScore = (revisionCount * 22.0) * recencyFactor * (0.85 + 0.15 * importanceWeight);
        this.revisionScore = Math.min(100.0, Math.max(0.0, Math.round(baseScore * 10.0) / 10.0));
        this.status = RevisionStatus.calculateStatus(this.revisionScore);
    }

    /**
     * Records a new revision session by incrementing count and updating timestamp.
     */
    public void recordRevision() {
        this.revisionCount++;
        this.lastReviewed = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        recalculateScore();
    }

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

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public int getRevisionCount() {
        return revisionCount;
    }

    public void setRevisionCount(int revisionCount) {
        this.revisionCount = revisionCount;
    }

    public LocalDateTime getLastReviewed() {
        return lastReviewed;
    }

    public void setLastReviewed(LocalDateTime lastReviewed) {
        this.lastReviewed = lastReviewed;
    }

    public double getImportanceWeight() {
        return importanceWeight;
    }

    public void setImportanceWeight(double importanceWeight) {
        this.importanceWeight = importanceWeight;
    }

    public double getRecencyFactor() {
        return recencyFactor;
    }

    public void setRecencyFactor(double recencyFactor) {
        this.recencyFactor = recencyFactor;
    }

    public double getRevisionScore() {
        return revisionScore;
    }

    public void setRevisionScore(double revisionScore) {
        this.revisionScore = revisionScore;
    }

    public RevisionStatus getStatus() {
        return status;
    }

    public void setStatus(RevisionStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }
}
