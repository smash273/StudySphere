package com.studysphere.service;

import com.studysphere.ai.AIService;
import com.studysphere.dao.QuizDAO;
import com.studysphere.dao.TopicDAO;
import com.studysphere.exception.AIServiceException;
import com.studysphere.exception.DatabaseException;
import com.studysphere.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Service managing Quiz generation, answer evaluation, scoring, and history analytics.
 */
public class QuizService {
    private static final Logger logger = LoggerFactory.getLogger(QuizService.class);

    private final QuizDAO quizDAO;
    private final TopicDAO topicDAO;
    private final AIService aiService;
    private final RevisionService revisionService;

    public QuizService() {
        this.quizDAO = new QuizDAO();
        this.topicDAO = new TopicDAO();
        this.aiService = new AIService();
        this.revisionService = new RevisionService();
    }

    public Quiz getOrGenerateQuiz(int topicId, int questionCount) throws DatabaseException, AIServiceException {
        Optional<Topic> topicOpt = topicDAO.findById(topicId);
        if (topicOpt.isEmpty()) {
            throw new DatabaseException("Topic not found with ID: " + topicId);
        }
        Topic topic = topicOpt.get();

        // Check if existing questions exist in database
        List<Question> existing = quizDAO.findQuestionsByTopicId(topicId);
        if (!existing.isEmpty()) {
            return new Quiz(topicId, topic.getName(), existing);
        }

        // Build context from topic summary, points, and definitions
        StringBuilder context = new StringBuilder();
        if (topic.getSummary() != null) {
            context.append(topic.getSummary().getSummaryText()).append("\n");
        }
        for (var pt : topic.getImportantPoints()) {
            context.append("• ").append(pt.getPointText()).append("\n");
        }
        for (var def : topic.getDefinitions()) {
            context.append(def.getTerm()).append(" is defined as: ").append(def.getDefinitionText()).append("\n");
        }

        // Generate via AI
        List<Question> generated = aiService.generateQuiz(topic.getName(), context.toString(), questionCount);
        for (Question q : generated) {
            q.setTopicId(topicId);
            quizDAO.saveQuestion(q);
        }

        return new Quiz(topicId, topic.getName(), generated);
    }

    public QuizAttempt submitQuiz(int userId, int topicId, List<Answer> answers) throws DatabaseException {
        List<Question> questions = quizDAO.findQuestionsByTopicId(topicId);
        int correctCount = 0;
        int total = questions.size();

        for (Question q : questions) {
            for (Answer a : answers) {
                if (a.getQuestionId() == q.getId()) {
                    boolean correct = q.getCorrectAnswer().trim().equalsIgnoreCase(a.getSelectedAnswer().trim());
                    a.setCorrect(correct);
                    a.setExplanation(q.getExplanation());
                    if (correct) correctCount++;
                    break;
                }
            }
        }

        double scorePercent = total > 0 ? (correctCount * 100.0 / total) : 0.0;
        QuizAttempt attempt = new QuizAttempt(userId, topicId, scorePercent, total, correctCount);
        quizDAO.saveQuizAttempt(attempt);

        // If student performed well on the quiz, update their revision record
        if (scorePercent >= 60.0) {
            revisionService.markAsRevised(userId, topicId, "Passed quiz with " + (int) scorePercent + "%");
        }

        return attempt;
    }

    public List<QuizAttempt> getRecentAttempts(int userId) throws DatabaseException {
        return quizDAO.findAttemptsByUserId(userId);
    }
}
