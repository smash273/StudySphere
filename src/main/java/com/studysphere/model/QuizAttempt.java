package com.studysphere.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity tracking a student's score and evaluation on a completed quiz.
 */
public class QuizAttempt implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int userId;
    private int topicId;
    private double score;
    private int totalQuestions;
    private int correctCount;
    private LocalDateTime attemptedAt;

    private String topicName;

    public QuizAttempt() {
        this.attemptedAt = LocalDateTime.now();
    }

    public QuizAttempt(int userId, int topicId, double score, int totalQuestions, int correctCount) {
        this();
        this.userId = userId;
        this.topicId = topicId;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.correctCount = correctCount;
    }

    public QuizAttempt(int id, int userId, int topicId, double score, int totalQuestions, int correctCount, LocalDateTime attemptedAt) {
        this.id = id;
        this.userId = userId;
        this.topicId = topicId;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.correctCount = correctCount;
        this.attemptedAt = attemptedAt;
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

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(int correctCount) {
        this.correctCount = correctCount;
    }

    public LocalDateTime getAttemptedAt() {
        return attemptedAt;
    }

    public void setAttemptedAt(LocalDateTime attemptedAt) {
        this.attemptedAt = attemptedAt;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
}
