package com.studysphere.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity representing an academic Summary generated for a Topic or Document.
 */
public class Summary implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int documentId;
    private Integer topicId; // Nullable if document-level summary
    private String summaryType; // TOPIC, DOCUMENT, EXECUTIVE
    private String summaryText;
    private LocalDateTime createdAt;

    public Summary() {
        this.summaryType = "TOPIC";
        this.createdAt = LocalDateTime.now();
    }

    public Summary(int documentId, Integer topicId, String summaryType, String summaryText) {
        this();
        this.documentId = documentId;
        this.topicId = topicId;
        this.summaryType = summaryType != null ? summaryType : "TOPIC";
        this.summaryText = summaryText;
    }

    public Summary(int id, int documentId, Integer topicId, String summaryType, String summaryText, LocalDateTime createdAt) {
        this.id = id;
        this.documentId = documentId;
        this.topicId = topicId;
        this.summaryType = summaryType;
        this.summaryText = summaryText;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDocumentId() {
        return documentId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }

    public Integer getTopicId() {
        return topicId;
    }

    public void setTopicId(Integer topicId) {
        this.topicId = topicId;
    }

    public String getSummaryType() {
        return summaryType;
    }

    public void setSummaryType(String summaryType) {
        this.summaryType = summaryType;
    }

    public String getSummaryText() {
        return summaryText;
    }

    public void setSummaryText(String summaryText) {
        this.summaryText = summaryText;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Summary{" +
                "id=" + id +
                ", topicId=" + topicId +
                ", summaryType='" + summaryType + '\'' +
                '}';
    }
}
