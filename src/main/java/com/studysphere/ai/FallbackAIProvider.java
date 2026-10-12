package com.studysphere.ai;

import com.studysphere.exception.AIServiceException;
import com.studysphere.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Deterministic local NLP and heuristic-based AI Provider.
 * Allows StudySphere to run completely standalone without requiring external API keys.
 * Demonstrates Abstraction, Algorithms, and the Java Collections Framework.
 */
public class FallbackAIProvider implements AIProvider {
    private static final Logger logger = LoggerFactory.getLogger(FallbackAIProvider.class);

    private static final Set<String> STOP_WORDS = Set.of(
            "a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are", "aren't",
            "as", "at", "be", "because", "been", "before", "being", "below", "between", "both", "but", "by", "can",
            "can't", "cannot", "could", "couldn't", "did", "didn't", "do", "does", "doesn't", "doing", "don't",
            "down", "during", "each", "few", "for", "from", "further", "had", "hadn't", "has", "hasn't", "have",
            "haven't", "having", "he", "he'd", "he'll", "he's", "her", "here", "here's", "hers", "herself", "him",
            "himself", "his", "how", "how's", "i", "i'd", "i'll", "i'm", "i've", "if", "in", "into", "is", "isn't",
            "it", "it's", "its", "itself", "let's", "me", "more", "most", "mustn't", "my", "myself", "no", "nor",
            "not", "of", "off", "on", "once", "only", "or", "other", "ought", "our", "ours", "ourselves", "out",
            "over", "own", "same", "shan't", "she", "she'd", "she'll", "she's", "should", "shouldn't", "so", "some",
            "such", "than", "that", "that's", "the", "their", "theirs", "them", "themselves", "then", "there",
            "there's", "these", "they", "they'd", "they'll", "they're", "they've", "this", "those", "through", "to",
            "too", "under", "until", "up", "very", "was", "wasn't", "we", "we'd", "we'll", "we're", "we've", "were",
            "weren't", "what", "what's", "when", "when's", "where", "where's", "which", "while", "who", "who's",
            "whom", "why", "why's", "with", "won't", "would", "wouldn't", "you", "you'd", "you'll", "you're", "you've",
            "your", "yours", "yourself", "yourselves"
    );

    @Override
    public String getProviderName() {
        return "Deterministic Academic NLP Engine (Local Fallback)";
    }

    @Override
    public AIResponse generateStructuredNotes(String documentTitle, String content) throws AIServiceException {
        if (content == null || content.trim().isEmpty()) {
            throw new AIServiceException("Cannot generate notes from empty content.");
        }

        AIResponse response = new AIResponse();
        response.setDocumentTitle(documentTitle);
        response.setFallback(true);
        response.setProviderName(getProviderName());

        // 1. Generate Executive Document Summary
        String executiveSummary = generateConciseSummary(content, 3);
        response.setExecutiveSummary(executiveSummary);

        // 2. Identify Academic Topics
        List<TopicBlock> rawTopics = segmentIntoTopics(content);
        if (rawTopics.isEmpty()) {
            rawTopics.add(new TopicBlock(documentTitle, content));
        }

        int orderIndex = 1;
        for (TopicBlock block : rawTopics) {
            Topic topic = new Topic();
            topic.setName(block.title);
            topic.setOrderIndex(orderIndex++);
            topic.setOverview(generateConciseSummary(block.content, 2));

            // Importance rating based on length, term density, and structural cues
            double importance = calculateTopicImportance(block.title, block.content);
            topic.setImportanceScore(importance);
            topic.setPriorityLevel(PriorityLevel.fromScore(importance));

            // Summary
            Summary sum = new Summary();
            sum.setSummaryType("TOPIC");
            sum.setSummaryText(generateConciseSummary(block.content, 3));
            topic.setSummary(sum);

            // Subtopics
            List<SubTopic> subTopics = extractSubtopics(block.content);
            topic.setSubtopics(subTopics);

            // Important Points
            List<ImportantPoint> points = extractImportantPoints(block.content);
            topic.setImportantPoints(points);

            // Definitions
            List<Definition> definitions = extractDefinitions(block.content);
            topic.setDefinitions(definitions);

            // Formulas
            List<Formula> formulas = extractFormulas(block.content);
            topic.setFormulas(formulas);

            // Keywords
            List<Keyword> keywords = extractKeywords(block.content);
            topic.setKeywords(keywords);

            response.addTopic(topic);
        }

        return response;
    }

    @Override
    public String answerGroundedQuestion(String question, String storedNotesContext) throws AIServiceException {
        if (question == null || question.trim().isEmpty()) {
            return "Please provide a valid question.";
        }
        if (storedNotesContext == null || storedNotesContext.trim().isEmpty()) {
            return "I couldn't find enough information in your uploaded material.";
        }

        Set<String> qWords = extractSignificantWords(question);
        if (qWords.isEmpty()) {
            return "I couldn't find enough information in your uploaded material.";
        }

        String[] sentences = storedNotesContext.split("(?<=[.!?])\\s+");
        List<ScoredSentence> matches = new ArrayList<>();

        for (String sentence : sentences) {
            String sClean = sentence.trim();
            if (sClean.length() < 15) continue;
            int score = 0;
            String lower = sClean.toLowerCase();
            for (String qw : qWords) {
                if (lower.contains(qw)) {
                    score += 2;
                }
            }
            if (score > 0) {
                matches.add(new ScoredSentence(sClean, score));
            }
        }

        if (matches.isEmpty() || matches.stream().mapToInt(m -> m.score).max().orElse(0) < 2) {
            return "I couldn't find enough information in your uploaded material.";
        }

        matches.sort((a, b) -> Integer.compare(b.score, a.score));

        StringBuilder answer = new StringBuilder();
        answer.append("Based strictly on your uploaded study material:\n\n");
        int count = Math.min(3, matches.size());
        for (int i = 0; i < count; i++) {
            answer.append("• ").append(matches.get(i).sentence).append("\n");
        }
        return answer.toString().trim();
    }

    @Override
    public List<Question> generateQuiz(String topicName, String topicContent, int questionCount) throws AIServiceException {
        List<Question> questions = new ArrayList<>();
        List<Definition> defs = extractDefinitions(topicContent);
        List<ImportantPoint> pts = extractImportantPoints(topicContent);

        // 1. Generate questions from definitions
        for (Definition def : defs) {
            if (questions.size() >= questionCount) break;
            Question q = new Question();
            q.setQuestionType("MCQ");
            q.setQuestionText("In the context of " + topicName + ", what is defined as: \"" + def.getDefinitionText() + "\"?");
            q.setOptionA(def.getTerm());
            q.setOptionB("Alternative Protocol");
            q.setOptionC("System Interrupt");
            q.setOptionD("Static Allocation");
            q.setCorrectAnswer(def.getTerm());
            q.setExplanation("According to your notes: " + def.getTerm() + " is " + def.getDefinitionText());
            questions.add(q);
        }

        // 2. Generate True/False or MCQ from Important Points
        for (ImportantPoint pt : pts) {
            if (questions.size() >= questionCount) break;
            Question q = new Question();
            q.setQuestionType("TRUE_FALSE");
            q.setQuestionText("True or False: " + pt.getPointText());
            q.setOptionA("True");
            q.setOptionB("False");
            q.setCorrectAnswer("True");
            q.setExplanation("Direct academic takeaway from your study material: " + pt.getPointText());
            questions.add(q);
        }

        // Fallback default question if notes were very short
        if (questions.isEmpty()) {
            Question q = new Question();
            q.setQuestionType("TRUE_FALSE");
            q.setQuestionText("Is " + topicName + " an essential concept discussed in the uploaded academic document?");
            q.setOptionA("True");
            q.setOptionB("False");
            q.setCorrectAnswer("True");
            q.setExplanation("Topic verified in document index.");
            questions.add(q);
        }

        return questions;
    }

    // --- Helper NLP methods ---

    private static class TopicBlock {
        String title;
        String content;
        TopicBlock(String title, String content) {
            this.title = title;
            this.content = content;
        }
    }

    private List<TopicBlock> segmentIntoTopics(String content) {
        List<TopicBlock> blocks = new ArrayList<>();
        // Match headings like "# Heading", "1. Title", "Chapter 1: Title", "Topic: Title", or double-line breaks
        Pattern pattern = Pattern.compile("(?m)^(?>(?:[0-9]+\\.|#+|Topic:|Chapter:)\\s+)(.+)$");
        Matcher matcher = pattern.matcher(content);

        List<Integer> splitIndices = new ArrayList<>();
        List<String> titles = new ArrayList<>();

        while (matcher.find()) {
            splitIndices.add(matcher.start());
            titles.add(matcher.group(1).trim());
        }

        if (splitIndices.isEmpty()) {
            // Segment by large paragraphs if no formal headings found
            String[] paragraphs = content.split("\\n\\s*\\n");
            int topicNum = 1;
            StringBuilder currentBlock = new StringBuilder();
            String currentTitle = "Topic 1: Foundations";

            for (String p : paragraphs) {
                if (p.trim().length() < 20) continue;
                if (currentBlock.length() > 1500) {
                    blocks.add(new TopicBlock(currentTitle, currentBlock.toString().trim()));
                    currentBlock = new StringBuilder();
                    topicNum++;
                    currentTitle = "Topic " + topicNum + ": Advanced Principles";
                }
                currentBlock.append(p).append("\n\n");
            }
            if (currentBlock.length() > 0) {
                blocks.add(new TopicBlock(currentTitle, currentBlock.toString().trim()));
            }
            return blocks;
        }

        for (int i = 0; i < splitIndices.size(); i++) {
            int start = splitIndices.get(i);
            int end = (i + 1 < splitIndices.size()) ? splitIndices.get(i + 1) : content.length();
            String blockContent = content.substring(start, end).trim();
            blocks.add(new TopicBlock(titles.get(i), blockContent));
        }

        return blocks;
    }

    private List<SubTopic> extractSubtopics(String content) {
        List<SubTopic> subTopics = new ArrayList<>();
        String[] lines = content.split("\\n");
        int order = 1;
        Pattern subPattern = Pattern.compile("^\\s*(?:[-*•]|[0-9]+\\.[0-9]+|[a-d]\\))\\s+(.+)$");

        for (String line : lines) {
            Matcher m = subPattern.matcher(line);
            if (m.find()) {
                String subName = m.group(1).trim();
                if (subName.length() >= 5 && subName.length() <= 80) {
                    subTopics.add(new SubTopic(0, subName, line.trim(), order++));
                    if (subTopics.size() >= 5) break;
                }
            }
        }

        if (subTopics.isEmpty()) {
            subTopics.add(new SubTopic(0, "Core Principles", "Foundational theoretical underpinnings discussed in the material.", 1));
            subTopics.add(new SubTopic(0, "Application & Constraints", "Practical algorithmic rules and operational constraints.", 2));
        }
        return subTopics;
    }

    private List<ImportantPoint> extractImportantPoints(String content) {
        List<ImportantPoint> points = new ArrayList<>();
        String[] lines = content.split("\\n");

        for (String line : lines) {
            String l = line.trim();
            if (l.startsWith("•") || l.startsWith("-") || l.startsWith("*") || l.matches("^[0-9]+\\..+")) {
                String clean = l.replaceFirst("^[-*•0-9.]+\\s*", "").trim();
                if (clean.length() >= 20) {
                    points.add(new ImportantPoint(0, null, clean, 1.0));
                    if (points.size() >= 6) break;
                }
            }
        }

        if (points.isEmpty()) {
            // Extract strong sentences containing keywords like important, must, ensures, requires, critical
            String[] sentences = content.split("(?<=[.!?])\\s+");
            for (String s : sentences) {
                String lower = s.toLowerCase();
                if (lower.contains("must") || lower.contains("important") || lower.contains("critical")
                        || lower.contains("ensures") || lower.contains("essential") || lower.contains("guarantees")) {
                    points.add(new ImportantPoint(0, null, s.trim(), 0.95));
                    if (points.size() >= 5) break;
                }
            }
            if (points.isEmpty()) {
                // Secondary fallback: take informative sentences
                for (String s : sentences) {
                    String clean = s.trim();
                    if (clean.length() >= 25 && !clean.startsWith("#")) {
                        points.add(new ImportantPoint(0, null, clean, 0.85));
                        if (points.size() >= 3) break;
                    }
                }
            }
        }
        return points;
    }

    private List<Definition> extractDefinitions(String content) {
        List<Definition> definitions = new ArrayList<>();
        // Match patterns like "X is defined as Y", "X refers to Y", "X: Y"
        Pattern defPattern = Pattern.compile("(?i)\\b([A-Z][a-zA-Z\\s]{2,30})\\s+(?:is defined as|refers to|is a|means|is an)\\s+([^.\\n]{15,200})\\.");
        Matcher m = defPattern.matcher(content);

        while (m.find()) {
            String term = m.group(1).trim();
            String text = m.group(2).trim();
            if (!STOP_WORDS.contains(term.toLowerCase())) {
                definitions.add(new Definition(0, term, text));
                if (definitions.size() >= 5) break;
            }
        }
        return definitions;
    }

    private List<Formula> extractFormulas(String content) {
        List<Formula> formulas = new ArrayList<>();
        Pattern formPattern = Pattern.compile("(?m)([A-Za-z0-9_\\[\\]\\(\\)\\s]{2,30}\\s*=\\s*[^\\n.]{3,60})");
        Matcher m = formPattern.matcher(content);

        while (m.find()) {
            String expr = m.group(1).trim();
            if (expr.contains("+") || expr.contains("-") || expr.contains("*") || expr.contains("/") || expr.contains("O(")) {
                formulas.add(new Formula(0, expr, "Mathematical or algorithmic formula identified in study text.", "{}"));
                if (formulas.size() >= 3) break;
            }
        }
        return formulas;
    }

    private List<Keyword> extractKeywords(String content) {
        Map<String, Integer> freq = new HashMap<>();
        String[] words = content.toLowerCase().replaceAll("[^a-z0-9\\s]", " ").split("\\s+");

        for (String w : words) {
            if (w.length() >= 4 && !STOP_WORDS.contains(w)) {
                freq.put(w, freq.getOrDefault(w, 0) + 1);
            }
        }

        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(freq.entrySet());
        sorted.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

        List<Keyword> keywords = new ArrayList<>();
        int count = Math.min(8, sorted.size());
        int maxOccur = sorted.isEmpty() ? 1 : sorted.get(0).getValue();

        for (int i = 0; i < count; i++) {
            Map.Entry<String, Integer> entry = sorted.get(i);
            String capitalized = entry.getKey().substring(0, 1).toUpperCase() + entry.getKey().substring(1);
            double rel = Math.min(1.0, 0.5 + (0.5 * entry.getValue() / maxOccur));
            keywords.add(new Keyword(0, capitalized, Math.round(rel * 100.0) / 100.0, entry.getValue()));
        }
        return keywords;
    }

    private String generateConciseSummary(String text, int sentenceCount) {
        String[] sentences = text.split("(?<=[.!?])\\s+");
        StringBuilder sb = new StringBuilder();
        int added = 0;
        for (String s : sentences) {
            String clean = s.trim();
            if (clean.length() >= 30 && !clean.startsWith("#")) {
                sb.append(clean).append(" ");
                added++;
                if (added >= sentenceCount) break;
            }
        }
        return sb.toString().trim();
    }

    private double calculateTopicImportance(String title, String content) {
        double score = 0.65;
        String t = title.toLowerCase();
        if (t.contains("deadlock") || t.contains("normalization") || t.contains("concurrency")
                || t.contains("process") || t.contains("transaction") || t.contains("architecture")) {
            score += 0.25;
        }
        if (content.length() > 1000) score += 0.05;
        return Math.min(0.98, Math.max(0.40, score));
    }

    private Set<String> extractSignificantWords(String text) {
        Set<String> set = new HashSet<>();
        String[] words = text.toLowerCase().replaceAll("[^a-z0-9\\s]", " ").split("\\s+");
        for (String w : words) {
            if (w.length() >= 3 && !STOP_WORDS.contains(w)) {
                set.add(w);
            }
        }
        return set;
    }

    private static class ScoredSentence {
        String sentence;
        int score;
        ScoredSentence(String sentence, int score) {
            this.sentence = sentence;
            this.score = score;
        }
    }
}
