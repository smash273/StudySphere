package com.studysphere.ai;

import com.studysphere.exception.AIServiceException;

/**
 * Interface defining grounded question answering against student's uploaded notes.
 */
public interface QuestionAnsweringService {
    String answerQuestion(String question, String context) throws AIServiceException;
}
