package com.studysphere.service;

import com.studysphere.ai.AIService;
import com.studysphere.dao.DocumentDAO;
import com.studysphere.dao.TopicDAO;
import com.studysphere.exception.AIServiceException;
import com.studysphere.exception.DatabaseException;
import com.studysphere.model.Document;
import com.studysphere.model.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Service for grounded student question answering against stored academic notes.
 * Enforces provenance citations and prevents hallucinated responses.
 */
public class AskNotesService {
    private static final Logger logger = LoggerFactory.getLogger(AskNotesService.class);

    private final TopicDAO topicDAO;
    private final DocumentDAO documentDAO;
    private final AIService aiService;

    public AskNotesService() {
        this.topicDAO = new TopicDAO();
        this.documentDAO = new DocumentDAO();
        this.aiService = new AIService();
    }

    public AskNotesService(TopicDAO topicDAO, DocumentDAO documentDAO, AIService aiService) {
        this.topicDAO = topicDAO;
        this.documentDAO = documentDAO;
        this.aiService = aiService;
    }

    public GroundedAnswerResult askQuestion(int userId, String question) throws DatabaseException, AIServiceException {
        if (question == null || question.trim().isEmpty()) {
            return new GroundedAnswerResult("Please enter a question.", Collections.emptyList());
        }

        // 1. Retrieve all topics for user
        List<Topic> userTopics = topicDAO.findByUserId(userId);
        if (userTopics.isEmpty()) {
            return new GroundedAnswerResult("You have not uploaded any study material yet. Please upload notes first.", Collections.emptyList());
        }

        // 2. Identify relevant topics based on keyword matching
        Set<String> qKeywords = extractKeywords(question);
        List<TopicScore> relevantTopics = new ArrayList<>();

        for (Topic t : userTopics) {
            int score = 0;
            String tNameLower = t.getName().toLowerCase();
            for (String kw : qKeywords) {
                if (tNameLower.contains(kw)) score += 5;
            }
            if (t.getOverview() != null) {
                String overLower = t.getOverview().toLowerCase();
                for (String kw : qKeywords) {
                    if (overLower.contains(kw)) score += 2;
                }
            }
            for (var def : t.getDefinitions()) {
                String termLower = def.getTerm().toLowerCase();
                for (String kw : qKeywords) {
                    if (termLower.contains(kw)) score += 4;
                }
            }
            for (var pt : t.getImportantPoints()) {
                String ptLower = pt.getPointText().toLowerCase();
                for (String kw : qKeywords) {
                    if (ptLower.contains(kw)) score += 2;
                }
            }
            for (var k : t.getKeywords()) {
                String kLower = k.getKeyword().toLowerCase();
                for (String kw : qKeywords) {
                    if (kLower.contains(kw)) score += 3;
                }
            }

            if (score > 0) {
                relevantTopics.add(new TopicScore(t, score));
            }
        }

        if (relevantTopics.isEmpty()) {
            return new GroundedAnswerResult("I couldn't find enough information in your uploaded material.", Collections.emptyList());
        }

        relevantTopics.sort((a, b) -> Integer.compare(b.score, a.score));

        // 3. Compile context & citations from top matching topics
        StringBuilder context = new StringBuilder();
        List<String> citations = new ArrayList<>();
        Map<Integer, Document> docCache = new HashMap<>();

        int topN = Math.min(3, relevantTopics.size());
        for (int i = 0; i < topN; i++) {
            Topic topic = relevantTopics.get(i).topic;
            Document doc = docCache.computeIfAbsent(topic.getDocumentId(), id -> {
                try {
                    return documentDAO.findById(id).orElse(null);
                } catch (Exception e) {
                    return null;
                }
            });

            String docTitle = doc != null ? doc.getTitle() : "Document";
            citations.add(docTitle + " → " + topic.getName());

            context.append("--- SOURCE: ").append(docTitle).append(" | TOPIC: ").append(topic.getName()).append(" ---\n");
            if (topic.getSummary() != null) {
                context.append("Summary: ").append(topic.getSummary().getSummaryText()).append("\n");
            }
            for (var def : topic.getDefinitions()) {
                context.append("Definition: ").append(def.getTerm()).append(" = ").append(def.getDefinitionText()).append("\n");
            }
            for (var pt : topic.getImportantPoints()) {
                context.append("Key Point: ").append(pt.getPointText()).append("\n");
            }
            for (var sub : topic.getSubtopics()) {
                context.append("Subtopic ").append(sub.getName()).append(": ").append(sub.getContent()).append("\n");
            }
            for (var f : topic.getFormulas()) {
                context.append("Formula: ").append(f.getExpression()).append(" (").append(f.getExplanation()).append(")\n");
            }
            context.append("\n");
        }

        // 4. Generate grounded answer
        String rawAnswer = aiService.answerQuestion(question, context.toString());
        return new GroundedAnswerResult(rawAnswer, citations);
    }

    private Set<String> extractKeywords(String text) {
        Set<String> set = new HashSet<>();
        String[] parts = text.toLowerCase().replaceAll("[^a-z0-9\\s]", " ").split("\\s+");
        for (String p : parts) {
            if (p.length() >= 3) set.add(p);
        }
        return set;
    }

    private static class TopicScore {
        Topic topic;
        int score;
        TopicScore(Topic topic, int score) {
            this.topic = topic;
            this.score = score;
        }
    }

    public static class GroundedAnswerResult {
        private final String answer;
        private final List<String> citations;

        public GroundedAnswerResult(String answer, List<String> citations) {
            this.answer = answer;
            this.citations = citations;
        }

        public String getAnswer() {
            return answer;
        }

        public List<String> getCitations() {
            return citations;
        }
    }
}
