package com.studysphere.ai;

import com.studysphere.model.Summary;

/**
 * Summarizer implementation leveraging remote LLM API.
 * Demonstrates Polymorphism.
 */
public class LLMSummarizer implements Summarizer {
    private final LLMAPIProvider provider;

    public LLMSummarizer() {
        this.provider = new LLMAPIProvider();
    }

    @Override
    public Summary summarize(String text) {
        try {
            var response = provider.generateStructuredNotes("Document", text);
            return new Summary(0, null, "TOPIC", response.getExecutiveSummary());
        } catch (Exception e) {
            return new FallbackSummarizer().summarize(text);
        }
    }
}
