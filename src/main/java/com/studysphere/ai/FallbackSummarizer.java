package com.studysphere.ai;

import com.studysphere.model.Summary;

/**
 * Summarizer implementation leveraging local deterministic NLP algorithms.
 * Demonstrates Polymorphism.
 */
public class FallbackSummarizer implements Summarizer {
    private final FallbackAIProvider fallbackProvider;

    public FallbackSummarizer() {
        this.fallbackProvider = new FallbackAIProvider();
    }

    @Override
    public Summary summarize(String text) {
        try {
            var response = fallbackProvider.generateStructuredNotes("Document", text);
            return new Summary(0, null, "TOPIC", response.getExecutiveSummary());
        } catch (Exception e) {
            return new Summary(0, null, "TOPIC", text.substring(0, Math.min(text.length(), 200)) + "...");
        }
    }
}
