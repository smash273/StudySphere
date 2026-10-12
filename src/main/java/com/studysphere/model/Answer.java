package com.studysphere.model;

import java.io.Serializable;

/**
 * Model representing a student's answer submission to a question.
 */
public class Answer implements Serializable {
    private static final long serialVersionUID = 1L;

    private int questionId;
    private String selectedAnswer;
    private boolean isCorrect;
    private String explanation;

    public Answer() {}

    public Answer(int questionId, String selectedAnswer) {
        this.questionId = questionId;
        this.selectedAnswer = selectedAnswer;
    }

    public Answer(int questionId, String selectedAnswer, boolean isCorrect, String explanation) {
        this.questionId = questionId;
        this.selectedAnswer = selectedAnswer;
        this.isCorrect = isCorrect;
        this.explanation = explanation;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getSelectedAnswer() {
        return selectedAnswer;
    }

    public void setSelectedAnswer(String selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
