package com.studysphere.ai;

import com.studysphere.model.Summary;

/**
 * Interface defining the contract for text summarization.
 * Demonstrates Abstraction and Polymorphism in CSE1007.
 */
public interface Summarizer {
    /**
     * Generates an academic summary from input text.
     * @param text source material
     * @return structured Summary entity
     */
    Summary summarize(String text);
}
