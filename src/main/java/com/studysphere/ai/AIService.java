package com.studysphere.ai;

import com.studysphere.config.DatabaseConfig;
import com.studysphere.exception.AIServiceException;
import com.studysphere.model.AIResponse;
import com.studysphere.model.Question;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Service Layer coordinator for all AI operations.
 * Isolates business logic from AI provider implementations and supports dynamic provider switching.
 */
public class AIService implements TopicExtractor, ImportantPointExtractor, QuestionAnsweringService {
    private static final Logger logger = LoggerFactory.getLogger(AIService.class);

    private final AIProvider provider;

    public AIService() {
        String configured = DatabaseConfig.getAiProvider();
        if ("openai".equalsIgnoreCase(configured) || "gemini".equalsIgnoreCase(configured)) {
            logger.info("Initializing LLMAPIProvider for provider: {}", configured);
            this.provider = new LLMAPIProvider();
        } else {
            logger.info("Initializing FallbackAIProvider (Deterministic NLP)");
            this.provider = new FallbackAIProvider();
        }
    }

    public AIService(AIProvider provider) {
        this.provider = provider != null ? provider : new FallbackAIProvider();
    }

    public AIResponse processDocument(String title, String content) throws AIServiceException {
        return provider.generateStructuredNotes(title, content);
    }

    @Override
    public String answerQuestion(String question, String context) throws AIServiceException {
        return provider.answerGroundedQuestion(question, context);
    }

    public List<Question> generateQuiz(String topicName, String content, int count) throws AIServiceException {
        return provider.generateQuiz(topicName, content, count);
    }

    @Override
    public List<com.studysphere.model.Topic> extractTopics(String content) throws AIServiceException {
        AIResponse response = provider.generateStructuredNotes("Document", content);
        return response.getTopics();
    }

    @Override
    public List<com.studysphere.model.ImportantPoint> extractImportantPoints(String content) throws AIServiceException {
        AIResponse response = provider.generateStructuredNotes("Document", content);
        List<com.studysphere.model.ImportantPoint> points = new java.util.ArrayList<>();
        for (var t : response.getTopics()) {
            points.addAll(t.getImportantPoints());
        }
        return points;
    }

    public String getActiveProviderName() {
        return provider.getProviderName();
    }
}
