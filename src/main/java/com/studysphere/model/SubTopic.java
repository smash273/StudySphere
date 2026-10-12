package com.studysphere.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity representing an academic SubTopic within a parent Topic.
 */
public class SubTopic implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int topicId;
    private String name;
    private String content;
    private int orderIndex;
    private LocalDateTime createdAt;

    public SubTopic() {
        this.createdAt = LocalDateTime.now();
    }

    public SubTopic(int topicId, String name, String content, int orderIndex) {
        this();
        this.topicId = topicId;
        this.name = name;
        this.content = content;
        this.orderIndex = orderIndex;
    }

    public SubTopic(int id, int topicId, String name, String content, int orderIndex, LocalDateTime createdAt) {
        this.id = id;
        this.topicId = topicId;
        this.name = name;
        this.content = content;
        this.orderIndex = orderIndex;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return name;
    }
}
