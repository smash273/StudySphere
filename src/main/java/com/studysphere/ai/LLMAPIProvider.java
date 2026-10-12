package com.studysphere.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studysphere.config.DatabaseConfig;
import com.studysphere.exception.AIServiceException;
import com.studysphere.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Remote LLM API implementation of AIProvider (OpenAI / OpenAI-compatible / Gemini).
 * Sends structured JSON prompts and automatically validates responses.
 */
public class LLMAPIProvider implements AIProvider {
    private static final Logger logger = LoggerFactory.getLogger(LLMAPIProvider.class);

    private final String apiKey;
    private final String endpoint;
    private final String model;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final FallbackAIProvider fallbackProvider;

    public LLMAPIProvider() {
        this.apiKey = DatabaseConfig.getAiApiKey();
        this.endpoint = DatabaseConfig.getAiEndpoint();
        this.model = DatabaseConfig.getAiModel();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
        this.objectMapper = new ObjectMapper();
        this.fallbackProvider = new FallbackAIProvider();
    }

    @Override
    public String getProviderName() {
        return "Remote LLM API (" + model + ")";
    }

    @Override
    public AIResponse generateStructuredNotes(String documentTitle, String content) throws AIServiceException {
        if (apiKey == null || apiKey.isBlank()) {
            logger.info("AI API key not configured — falling back to deterministic local NLP engine.");
            return fallbackProvider.generateStructuredNotes(documentTitle, content);
        }

        String prompt = buildStructuredNotesPrompt(documentTitle, content);
        try {
            String jsonResponse = callLLMApi(prompt);
            return parseAndValidateStructuredResponse(documentTitle, jsonResponse);
        } catch (Exception e) {
            logger.warn("Remote LLM failed ({}), falling back to deterministic local engine", e.getMessage());
            return fallbackProvider.generateStructuredNotes(documentTitle, content);
        }
    }

    @Override
    public String answerGroundedQuestion(String question, String storedNotesContext) throws AIServiceException {
        if (apiKey == null || apiKey.isBlank()) {
            return fallbackProvider.answerGroundedQuestion(question, storedNotesContext);
        }

        String prompt = "You are StudySphere Academic Assistant. Answer the student's question STRICTLY based on the provided notes context.\n" +
                "If the answer cannot be determined directly from the context, respond with EXACTLY: \"I couldn't find enough information in your uploaded material.\"\n" +
                "Do NOT fabricate information or cite external sources.\n\n" +
                "NOTES CONTEXT:\n" + storedNotesContext + "\n\n" +
                "STUDENT QUESTION:\n" + question + "\n\n" +
                "ANSWER:";

        try {
            String response = callLLMApi(prompt);
            return response != null ? response.trim() : fallbackProvider.answerGroundedQuestion(question, storedNotesContext);
        } catch (Exception e) {
            logger.warn("Remote LLM Q&A failed, falling back to local search: {}", e.getMessage());
            return fallbackProvider.answerGroundedQuestion(question, storedNotesContext);
        }
    }

    @Override
    public List<Question> generateQuiz(String topicName, String topicContent, int questionCount) throws AIServiceException {
        if (apiKey == null || apiKey.isBlank()) {
            return fallbackProvider.generateQuiz(topicName, topicContent, questionCount);
        }

        String prompt = "Generate " + questionCount + " academic quiz questions strictly based on the following topic material: " + topicName + "\n\n" +
                "MATERIAL:\n" + topicContent + "\n\n" +
                "Return a strict JSON array of objects with keys: questionText, questionType (MCQ or TRUE_FALSE), optionA, optionB, optionC, optionD, correctAnswer, explanation.\n" +
                "No markdown code fences, only valid JSON.";

        try {
            String json = callLLMApi(prompt);
            return parseQuizJson(json, topicName);
        } catch (Exception e) {
            logger.warn("Remote LLM quiz generation failed, using fallback: {}", e.getMessage());
            return fallbackProvider.generateQuiz(topicName, topicContent, questionCount);
        }
    }

    private String callLLMApi(String prompt) throws Exception {
        String requestBody = objectMapper.writeValueAsString(new LLMRequest(model, prompt));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .timeout(Duration.ofSeconds(45))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new AIServiceException("LLM API returned status " + response.statusCode() + ": " + response.body());
        }

        JsonNode root = objectMapper.readTree(response.body());
        JsonNode choices = root.path("choices");
        if (choices.isArray() && !choices.isEmpty()) {
            return choices.get(0).path("message").path("content").asText();
        }
        return response.body();
    }

    private String buildStructuredNotesPrompt(String title, String content) {
        return "You are StudySphere's academic summarization engine. Analyze this study material and return a strict JSON payload.\n" +
                "Rules:\n" +
                "1. Stay strictly grounded in the uploaded material.\n" +
                "2. Preserve definitions, formulas, and technical terminology.\n" +
                "3. Output MUST adhere to this exact JSON schema:\n" +
                "{\n" +
                "  \"executiveSummary\": \"...\",\n" +
                "  \"topics\": [\n" +
                "    {\n" +
                "      \"name\": \"...\",\n" +
                "      \"overview\": \"...\",\n" +
                "      \"importanceScore\": 0.9,\n" +
                "      \"summary\": \"...\",\n" +
                "      \"subtopics\": [{\"name\": \"...\", \"content\": \"...\"}],\n" +
                "      \"importantPoints\": [\"...\"],\n" +
                "      \"definitions\": [{\"term\": \"...\", \"definitionText\": \"...\"}],\n" +
                "      \"formulas\": [{\"expression\": \"...\", \"explanation\": \"...\"}],\n" +
                "      \"keywords\": [\"...\"]\n" +
                "    }\n" +
                "  ]\n" +
                "}\n\n" +
                "DOCUMENT TITLE: " + title + "\n\n" +
                "CONTENT:\n" + content;
    }

    private AIResponse parseAndValidateStructuredResponse(String title, String rawJson) {
        try {
            String cleanJson = sanitizeJson(rawJson);
            JsonNode root = objectMapper.readTree(cleanJson);

            AIResponse response = new AIResponse();
            response.setDocumentTitle(title);
            response.setProviderName(getProviderName());
            response.setFallback(false);
            response.setExecutiveSummary(root.path("executiveSummary").asText("Executive summary."));

            JsonNode topicsNode = root.path("topics");
            if (topicsNode.isArray()) {
                int order = 1;
                for (JsonNode tNode : topicsNode) {
                    Topic t = new Topic();
                    t.setName(tNode.path("name").asText("Topic " + order));
                    t.setOverview(tNode.path("overview").asText(""));
                    double imp = tNode.path("importanceScore").asDouble(0.75);
                    t.setImportanceScore(imp);
                    t.setPriorityLevel(PriorityLevel.fromScore(imp));
                    t.setOrderIndex(order++);

                    // Summary
                    Summary s = new Summary();
                    s.setSummaryType("TOPIC");
                    s.setSummaryText(tNode.path("summary").asText("Topic summary"));
                    t.setSummary(s);

                    // Subtopics
                    JsonNode subs = tNode.path("subtopics");
                    if (subs.isArray()) {
                        int subOrder = 1;
                        for (JsonNode sn : subs) {
                            t.getSubtopics().add(new SubTopic(0, sn.path("name").asText("Subtopic"),
                                    sn.path("content").asText(""), subOrder++));
                        }
                    }

                    // Important Points
                    JsonNode pts = tNode.path("importantPoints");
                    if (pts.isArray()) {
                        for (JsonNode pn : pts) {
                            t.getImportantPoints().add(new ImportantPoint(0, null, pn.asText(), 1.0));
                        }
                    }

                    // Definitions
                    JsonNode defs = tNode.path("definitions");
                    if (defs.isArray()) {
                        for (JsonNode dn : defs) {
                            t.getDefinitions().add(new Definition(0, dn.path("term").asText(), dn.path("definitionText").asText()));
                        }
                    }

                    // Formulas
                    JsonNode forms = tNode.path("formulas");
                    if (forms.isArray()) {
                        for (JsonNode fn : forms) {
                            t.getFormulas().add(new Formula(0, fn.path("expression").asText(), fn.path("explanation").asText(), "{}"));
                        }
                    }

                    // Keywords
                    JsonNode keys = tNode.path("keywords");
                    if (keys.isArray()) {
                        for (JsonNode kn : keys) {
                            t.getKeywords().add(new Keyword(0, kn.asText(), 0.85, 1));
                        }
                    }

                    response.addTopic(t);
                }
            }
            return response;
        } catch (Exception e) {
            logger.warn("Failed to parse LLM JSON response: {}, returning fallback", e.getMessage());
            try {
                return fallbackProvider.generateStructuredNotes(title, rawJson);
            } catch (AIServiceException fallbackEx) {
                logger.error("Fallback provider also failed: {}", fallbackEx.getMessage());
                return new AIResponse();
            }
        }
    }

    private List<Question> parseQuizJson(String rawJson, String topicName) {
        List<Question> list = new ArrayList<>();
        try {
            String clean = sanitizeJson(rawJson);
            JsonNode array = objectMapper.readTree(clean);
            if (array.isArray()) {
                for (JsonNode qn : array) {
                    Question q = new Question();
                    q.setQuestionText(qn.path("questionText").asText());
                    q.setQuestionType(qn.path("questionType").asText("MCQ"));
                    q.setOptionA(qn.path("optionA").asText());
                    q.setOptionB(qn.path("optionB").asText());
                    q.setOptionC(qn.path("optionC").asText());
                    q.setOptionD(qn.path("optionD").asText());
                    q.setCorrectAnswer(qn.path("correctAnswer").asText());
                    q.setExplanation(qn.path("explanation").asText());
                    list.add(q);
                }
            }
        } catch (Exception e) {
            logger.warn("Could not parse quiz JSON: {}", e.getMessage());
        }
        return list;
    }

    private String sanitizeJson(String raw) {
        String clean = raw.trim();
        if (clean.startsWith("```json")) {
            clean = clean.substring(7);
        } else if (clean.startsWith("```")) {
            clean = clean.substring(3);
        }
        if (clean.endsWith("```")) {
            clean = clean.substring(0, clean.length() - 3);
        }
        return clean.trim();
    }

    private static class LLMRequest {
        public String model;
        public List<Message> messages;

        public LLMRequest(String model, String userContent) {
            this.model = model;
            this.messages = List.of(new Message("user", userContent));
        }

        public static class Message {
            public String role;
            public String content;

            public Message(String role, String content) {
                this.role = role;
                this.content = content;
            }
        }
    }
}
