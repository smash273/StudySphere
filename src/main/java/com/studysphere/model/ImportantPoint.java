package com.studysphere.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity representing an extracted key academic takeaway / important point.
 */
public class ImportantPoint implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int topicId;
    private Integer subtopicId;
    private String pointText;
    private double weight;
    private LocalDateTime createdAt;

    public ImportantPoint() {
        this.weight = 1.0;
        this.createdAt = LocalDateTime.now();
    }

    public ImportantPoint(int topicId, Integer subtopicId, String pointText, double weight) {
        this();
        this.topicId = topicId;
        this.subtopicId = subtopicId;
        this.pointText = pointText;
        this.weight = weight;
    }

    public ImportantPoint(int id, int topicId, Integer subtopicId, String pointText, double weight, LocalDateTime createdAt) {
        this.id = id;
        this.topicId = topicId;
        this.subtopicId = subtopicId;
        this.pointText = pointText;
        this.weight = weight;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public Integer getSubtopicId() {
        return subtopicId;
    }

    public void setSubtopicId(Integer subtopicId) {
        this.subtopicId = subtopicId;
    }

    public String getPointText() {
        return pointText;
    }

    public void setPointText(String pointText) {
        this.pointText = pointText;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return pointText;
    }
}
