package com.studysphere;

import com.studysphere.document.TextChunker;
import com.studysphere.model.DocumentChunk;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Text Chunker Tests (CSE1007 Algorithms & Collections)")
public class TextChunkerTest {

    @Test
    @DisplayName("Short text produces a single chunk")
    void testShortTextSingleChunk() {
        String text = "Java is a class-based, object-oriented programming language designed for platform portability.";
        TextChunker chunker = new TextChunker(600, 100);
        List<DocumentChunk> chunks = chunker.chunkText(1, text);

        assertNotNull(chunks);
        assertEquals(1, chunks.size());
        assertEquals(1, chunks.get(0).getChunkIndex());
        assertTrue(chunks.get(0).getChunkText().contains("object-oriented"));
    }

    @Test
    @DisplayName("Long text produces multiple overlapping chunks with correct indices")
    void testLongTextMultipleChunks() {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 300; i++) {
            sb.append("Section ").append(i).append(": This paragraph details academic concept number ")
              .append(i).append(" with extensive technical definitions and architectural discussion. ");
        }
        String text = sb.toString();

        TextChunker chunker = new TextChunker(100, 20);
        List<DocumentChunk> chunks = chunker.chunkText(1, text);

        assertNotNull(chunks);
        assertTrue(chunks.size() > 1, "Expected multiple chunks for large text");
        for (int i = 0; i < chunks.size(); i++) {
            assertEquals(i + 1, chunks.get(i).getChunkIndex());
            assertFalse(chunks.get(i).getChunkText().isBlank());
        }
    }
}
