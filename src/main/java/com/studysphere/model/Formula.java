package com.studysphere.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity representing an extracted academic formula or equation.
 */
public class Formula implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int topicId;
    private String expression;
    private String explanation;
    private String variablesJson; // JSON representation of variable definitions
    private LocalDateTime createdAt;

    public Formula() {
        this.createdAt = LocalDateTime.now();
    }

    public Formula(int topicId, String expression, String explanation, String variablesJson) {
        this();
        this.topicId = topicId;
        this.expression = expression;
        this.explanation = explanation;
        this.variablesJson = variablesJson;
    }

    public Formula(int id, int topicId, String expression, String explanation, String variablesJson, LocalDateTime createdAt) {
        this.id = id;
        this.topicId = topicId;
        this.expression = expression;
        this.explanation = explanation;
        this.variablesJson = variablesJson;
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

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getVariablesJson() {
        return variablesJson;
    }

    public void setVariablesJson(String variablesJson) {
        this.variablesJson = variablesJson;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return expression + " (" + explanation + ")";
    }
}
