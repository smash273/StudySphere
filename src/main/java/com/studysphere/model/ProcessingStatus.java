package com.studysphere.model;

/**
 * Status stages of document background processing.
 */
public enum ProcessingStatus {
    QUEUED("Queued for processing"),
    EXTRACTING("Extracting readable academic text"),
    CHUNKING("Segmenting document into semantic chunks"),
    ANALYZING("Identifying topics, subtopics, and key concepts"),
    GENERATING("Synthesizing summaries, points, formulas and definitions"),
    SAVING("Persisting structured academic notes to database"),
    COMPLETED("Document processing completed successfully"),
    FAILED("Processing encountered an error");

    private final String description;

    ProcessingStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
