package com.studysphere.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Quiz domain model containing a collection of Questions for an academic topic.
 */
public class Quiz implements Serializable {
    private static final long serialVersionUID = 1L;

    private int topicId;
    private String topicName;
    private List<Question> questions = new ArrayList<>();

    public Quiz() {}

    public Quiz(int topicId, String topicName) {
        this.topicId = topicId;
        this.topicName = topicName;
    }

    public Quiz(int topicId, String topicName, List<Question> questions) {
        this.topicId = topicId;
        this.topicName = topicName;
        this.questions = questions != null ? questions : new ArrayList<>();
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void addQuestion(Question question) {
        if (question != null) {
            this.questions.add(question);
        }
    }

    public int size() {
        return questions.size();
    }
}
