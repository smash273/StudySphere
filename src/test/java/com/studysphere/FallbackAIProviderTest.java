package com.studysphere;

import com.studysphere.ai.FallbackAIProvider;
import com.studysphere.model.AIResponse;
import com.studysphere.model.Question;
import com.studysphere.model.Topic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Fallback AI Engine Tests (CSE1007 Deterministic NLP & Abstraction)")
public class FallbackAIProviderTest {

    private final FallbackAIProvider provider = new FallbackAIProvider();

    @Test
    @DisplayName("generateStructuredNotes extracts topics, summaries, and definitions")
    void testStructuredNotesExtraction() throws Exception {
        String academicDoc = """
                # Process Management in Modern Operating Systems
                A process is defined as an instance of a computer program that is being executed by one or many threads.
                Each process contains program counter, stack, and data sections.
                
                ## Process States
                A process moves through New, Ready, Running, Waiting, and Terminated states during its execution lifecycle.
                Context switching allows the OS to share single CPU core among multiple processes.
                
                Formula for CPU scheduling response time is T_r = T_first - T_arrive.
                """;

        AIResponse response = provider.generateStructuredNotes("OS Notes", academicDoc);

        assertNotNull(response);
        assertFalse(response.getTopics().isEmpty(), "Topics list should not be empty");

        Topic firstTopic = response.getTopics().get(0);
        assertNotNull(firstTopic.getName());
        assertNotNull(firstTopic.getSummary());
        assertFalse(firstTopic.getImportantPoints().isEmpty(), "Important points should be extracted");
    }

    @Test
    @DisplayName("generateQuiz creates valid multiple-choice revision questions")
    void testQuizGeneration() throws Exception {
        String topicText = "A deadlock occurs when a set of processes are blocked because each is holding a resource and waiting for another.";
        List<Question> questions = provider.generateQuiz("Deadlocks", topicText, 3);

        assertNotNull(questions);
        assertFalse(questions.isEmpty());
        for (Question q : questions) {
            assertNotNull(q.getQuestionText());
            assertNotNull(q.getCorrectAnswer());
        }
    }

    @Test
    @DisplayName("answerGroundedQuestion grounds answer strictly on document text")
    void testGroundedQA() throws Exception {
        String sourceContext = "ACID properties in database transactions ensure Atomicity, Consistency, Isolation, and Durability.";
        String answer = provider.answerGroundedQuestion("What does ACID stand for?", sourceContext);

        assertNotNull(answer);
        assertTrue(answer.contains("Atomicity") || answer.contains("ACID"), "Grounded answer must mention source concepts");
    }
}
