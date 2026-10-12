package com.studysphere.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entity representing an identified academic Topic.
 * Implements {@link Comparable} for ordering in PriorityQueues based on importance and urgency.
 */
public class Topic implements Serializable, Comparable<Topic> {
    private static final long serialVersionUID = 1L;

    private int id;
    private int documentId;
    private String name;
    private String overview;
    private double importanceScore; // 0.0 to 1.0
    private PriorityLevel priorityLevel;
    private int orderIndex;
    private LocalDateTime createdAt;

    // Collections representing child hierarchy
    private List<SubTopic> subtopics = new ArrayList<>();
    private List<ImportantPoint> importantPoints = new ArrayList<>();
    private List<Definition> definitions = new ArrayList<>();
    private List<Formula> formulas = new ArrayList<>();
    private List<Keyword> keywords = new ArrayList<>();
    private Summary summary;
    private RevisionRecord revisionRecord;

    public Topic() {
        this.importanceScore = 0.5;
        this.priorityLevel = PriorityLevel.MEDIUM;
        this.createdAt = LocalDateTime.now();
    }

    public Topic(int documentId, String name, String overview, double importanceScore, int orderIndex) {
        this();
        this.documentId = documentId;
        this.name = name;
        this.overview = overview;
        this.importanceScore = importanceScore;
        this.priorityLevel = PriorityLevel.fromScore(importanceScore);
        this.orderIndex = orderIndex;
    }

    public Topic(int id, int documentId, String name, String overview, double importanceScore,
                 PriorityLevel priorityLevel, int orderIndex, LocalDateTime createdAt) {
        this.id = id;
        this.documentId = documentId;
        this.name = name;
        this.overview = overview;
        this.importanceScore = importanceScore;
        this.priorityLevel = priorityLevel;
        this.orderIndex = orderIndex;
        this.createdAt = createdAt;
    }

    /**
     * Natural ordering for PriorityQueue: topics with higher importance appear first.
     */
    @Override
    public int compareTo(Topic other) {
        if (other == null) return 1;
        // Higher importance first
        return Double.compare(other.importanceScore, this.importanceScore);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public double getImportanceScore() {
        return importanceScore;
    }

    public void setImportanceScore(double importanceScore) {
        this.importanceScore = importanceScore;
        this.priorityLevel = PriorityLevel.fromScore(importanceScore);
    }

    public PriorityLevel getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(PriorityLevel priorityLevel) {
        this.priorityLevel = priorityLevel;
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

    public List<SubTopic> getSubtopics() {
        return subtopics;
    }

    public void setSubtopics(List<SubTopic> subtopics) {
        this.subtopics = subtopics;
    }

    public List<ImportantPoint> getImportantPoints() {
        return importantPoints;
    }

    public void setImportantPoints(List<ImportantPoint> importantPoints) {
        this.importantPoints = importantPoints;
    }

    public List<Definition> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(List<Definition> definitions) {
        this.definitions = definitions;
    }

    public List<Formula> getFormulas() {
        return formulas;
    }

    public void setFormulas(List<Formula> formulas) {
        this.formulas = formulas;
    }

    public List<Keyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<Keyword> keywords) {
        this.keywords = keywords;
    }

    public Summary getSummary() {
        return summary;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
    }

    public RevisionRecord getRevisionRecord() {
        return revisionRecord;
    }

    public void setRevisionRecord(RevisionRecord revisionRecord) {
        this.revisionRecord = revisionRecord;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Topic topic = (Topic) o;
        return id == topic.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name + " (" + priorityLevel + ")";
    }
}
