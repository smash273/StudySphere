package com.studysphere.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Model representing a structured response returned by the AI summarization & analysis layer.
 */
public class AIResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String documentTitle;
    private String executiveSummary;
    private List<Topic> topics = new ArrayList<>();
    private boolean isFallback;
    private String providerName;

    public AIResponse() {}

    public AIResponse(String documentTitle, String executiveSummary) {
        this.documentTitle = documentTitle;
        this.executiveSummary = executiveSummary;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public String getExecutiveSummary() {
        return executiveSummary;
    }

    public void setExecutiveSummary(String executiveSummary) {
        this.executiveSummary = executiveSummary;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public void addTopic(Topic topic) {
        if (topic != null) {
            this.topics.add(topic);
        }
    }

    public boolean isFallback() {
        return isFallback;
    }

    public void setFallback(boolean fallback) {
        isFallback = fallback;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }
}
