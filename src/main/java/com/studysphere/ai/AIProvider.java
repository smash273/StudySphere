package com.studysphere.ai;

import com.studysphere.exception.AIServiceException;
import com.studysphere.model.AIResponse;
import com.studysphere.model.Question;

import java.util.List;

/**
 * Replaceable AI Provider interface.
 * Demonstrates Abstraction and Strategy Pattern.
 * Implementations provide AI services either via remote LLM APIs or local deterministic algorithms.
 */
public interface AIProvider {

    /**
     * Analyzes raw academic document text and returns structured notes.
     * @param documentTitle document title
     * @param content extracted raw text
     * @return structured AIResponse containing topics, subtopics, summaries, points, definitions, formulas, keywords
     * @throws AIServiceException if AI analysis fails
     */
    AIResponse generateStructuredNotes(String documentTitle, String content) throws AIServiceException;

    /**
     * Answers a student's question strictly grounded in uploaded notes.
     * Must return "I couldn't find enough information in your uploaded material." if answer is not in context.
     * @param question student question
     * @param storedNotesContext relevant retrieved text from student's notes
     * @return grounded answer with citation
     * @throws AIServiceException if generation fails
     */
    String answerGroundedQuestion(String question, String storedNotesContext) throws AIServiceException;

    /**
     * Generates revision quiz questions from study notes.
     * @param topicName name of the topic
     * @param topicContent content of the topic
     * @param questionCount number of questions to generate
     * @return list of Question objects
     * @throws AIServiceException if quiz generation fails
     */
    List<Question> generateQuiz(String topicName, String topicContent, int questionCount) throws AIServiceException;

    /**
     * Returns the name of the AI provider (e.g., "OpenAI", "Gemini", "Fallback-NLP").
     */
    String getProviderName();
}
