package com.studysphere.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity representing a chunked segment of an academic document for parallel processing.
 */
public class DocumentChunk implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int documentId;
    private int chunkIndex;
    private int startChar;
    private int endChar;
    private String chunkText;
    private int tokenCount;
    private LocalDateTime createdAt;

    public DocumentChunk() {
        this.createdAt = LocalDateTime.now();
    }

    public DocumentChunk(int documentId, int chunkIndex, int startChar, int endChar, String chunkText, int tokenCount) {
        this();
        this.documentId = documentId;
        this.chunkIndex = chunkIndex;
        this.startChar = startChar;
        this.endChar = endChar;
        this.chunkText = chunkText;
        this.tokenCount = tokenCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDocumentId() {
        return documentId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }

    public int getChunkIndex() {
        return chunkIndex;
    }

    public void setChunkIndex(int chunkIndex) {
        this.chunkIndex = chunkIndex;
    }

    public int getStartChar() {
        return startChar;
    }

    public void setStartChar(int startChar) {
        this.startChar = startChar;
    }

    public int getEndChar() {
        return endChar;
    }

    public void setEndChar(int endChar) {
        this.endChar = endChar;
    }

    public String getChunkText() {
        return chunkText;
    }

    public void setChunkText(String chunkText) {
        this.chunkText = chunkText;
    }

    public int getTokenCount() {
        return tokenCount;
    }

    public void setTokenCount(int tokenCount) {
        this.tokenCount = tokenCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
