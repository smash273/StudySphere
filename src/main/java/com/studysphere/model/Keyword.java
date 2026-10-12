package com.studysphere.model;

import java.io.Serializable;

/**
 * Entity representing an extracted keyword with frequency and relevance weight.
 */
public class Keyword implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int topicId;
    private String keyword;
    private double relevanceScore;
    private int occurrenceCount;

    public Keyword() {
        this.relevanceScore = 0.5;
        this.occurrenceCount = 1;
    }

    public Keyword(int topicId, String keyword, double relevanceScore, int occurrenceCount) {
        this.topicId = topicId;
        this.keyword = keyword;
        this.relevanceScore = relevanceScore;
        this.occurrenceCount = occurrenceCount;
    }

    public Keyword(int id, int topicId, String keyword, double relevanceScore, int occurrenceCount) {
        this.id = id;
        this.topicId = topicId;
        this.keyword = keyword;
        this.relevanceScore = relevanceScore;
        this.occurrenceCount = occurrenceCount;
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

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public double getRelevanceScore() {
        return relevanceScore;
    }

    public void setRelevanceScore(double relevanceScore) {
        this.relevanceScore = relevanceScore;
    }

    public int getOccurrenceCount() {
        return occurrenceCount;
    }

    public void setOccurrenceCount(int occurrenceCount) {
        this.occurrenceCount = occurrenceCount;
    }

    @Override
    public String toString() {
        return keyword + " (" + occurrenceCount + "x)";
    }
}
