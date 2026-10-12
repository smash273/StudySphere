package com.studysphere.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity representing an extracted academic term definition.
 */
public class Definition implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int topicId;
    private String term;
    private String definitionText;
    private LocalDateTime createdAt;

    public Definition() {
        this.createdAt = LocalDateTime.now();
    }

    public Definition(int topicId, String term, String definitionText) {
        this();
        this.topicId = topicId;
        this.term = term;
        this.definitionText = definitionText;
    }

    public Definition(int id, int topicId, String term, String definitionText, LocalDateTime createdAt) {
        this.id = id;
        this.topicId = topicId;
        this.term = term;
        this.definitionText = definitionText;
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

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getDefinitionText() {
        return definitionText;
    }

    public void setDefinitionText(String definitionText) {
        this.definitionText = definitionText;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return term + ": " + definitionText;
    }
}
