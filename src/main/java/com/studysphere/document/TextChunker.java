package com.studysphere.document;

import com.studysphere.model.DocumentChunk;

import java.util.ArrayList;
import java.util.List;

/**
 * Text segment chunker that breaks large academic texts into manageable pieces
 * with configurable overlap for LLM context windows and parallel processing.
 */
public class TextChunker {
    private final int chunkSizeWords;
    private final int chunkOverlapWords;

    public TextChunker() {
        this(600, 100);
    }

    public TextChunker(int chunkSizeWords, int chunkOverlapWords) {
        this.chunkSizeWords = Math.max(100, chunkSizeWords);
        this.chunkOverlapWords = Math.min(this.chunkSizeWords / 2, Math.max(0, chunkOverlapWords));
    }

    /**
     * Splits text into DocumentChunk entities.
     * @param documentId ID of the parent document
     * @param fullText complete extracted text
     * @return List of DocumentChunk models
     */
    public List<DocumentChunk> chunkText(int documentId, String fullText) {
        List<DocumentChunk> chunks = new ArrayList<>();
        if (fullText == null || fullText.trim().isEmpty()) {
            return chunks;
        }

        // Split by whitespace while tracking approximate character offsets
        String[] words = fullText.split("\\s+");
        if (words.length <= chunkSizeWords) {
            int tokenCount = words.length;
            chunks.add(new DocumentChunk(documentId, 1, 0, fullText.length(), fullText.trim(), tokenCount));
            return chunks;
        }

        int step = chunkSizeWords - chunkOverlapWords;
        int chunkIndex = 1;

        for (int i = 0; i < words.length; i += step) {
            int end = Math.min(words.length, i + chunkSizeWords);
            StringBuilder sb = new StringBuilder();
            for (int w = i; w < end; w++) {
                sb.append(words[w]).append(" ");
            }
            String chunkText = sb.toString().trim();
            int approxStart = (int) (((double) i / words.length) * fullText.length());
            int approxEnd = (int) (((double) end / words.length) * fullText.length());
            int tokenCount = end - i;

            chunks.add(new DocumentChunk(documentId, chunkIndex++, approxStart, approxEnd, chunkText, tokenCount));
            if (end >= words.length) {
                break;
            }
        }

        return chunks;
    }

    public int getChunkSizeWords() {
        return chunkSizeWords;
    }

    public int getChunkOverlapWords() {
        return chunkOverlapWords;
    }
}
