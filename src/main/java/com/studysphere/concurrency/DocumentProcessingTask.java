package com.studysphere.concurrency;

import com.studysphere.ai.AIService;
import com.studysphere.document.DocumentParser;
import com.studysphere.document.DocumentParserFactory;
import com.studysphere.document.TextChunker;
import com.studysphere.exception.DocumentProcessingException;
import com.studysphere.model.AIResponse;
import com.studysphere.model.Document;
import com.studysphere.model.DocumentChunk;
import com.studysphere.model.ProcessingStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Callable task for processing an academic document asynchronously in background thread.
 * Demonstrates Concurrency with Callable<T>, Future<T>, and Progress Callbacks.
 */
public class DocumentProcessingTask implements Callable<AIResponse> {
    private static final Logger logger = LoggerFactory.getLogger(DocumentProcessingTask.class);

    private final Document document;
    private final File file;
    private final AIService aiService;
    private final ProgressCallback callback;
    private final String jobId;

    public DocumentProcessingTask(Document document, File file, AIService aiService,
                                  ProgressCallback callback, String jobId) {
        this.document = document;
        this.file = file;
        this.aiService = aiService;
        this.callback = callback;
        this.jobId = jobId;
    }

    @Override
    public AIResponse call() throws Exception {
        try {
            // Stage 1: Parsing
            report(15, "Extracting text using " + document.getFileType() + " parser...", ProcessingStatus.EXTRACTING);
            DocumentParser parser = DocumentParserFactory.getParser(file.getName());
            String extractedText = parser.extractText(file);
            if (extractedText == null || extractedText.trim().isEmpty()) {
                throw new DocumentProcessingException("No readable academic text was found in this document.");
            }

            // Stage 2: Chunking
            report(35, "Chunking document into semantic segments...", ProcessingStatus.CHUNKING);
            TextChunker chunker = new TextChunker();
            List<DocumentChunk> chunks = chunker.chunkText(document.getId(), extractedText);
            document.setTotalChunks(chunks.size());

            // Stage 3: AI Analysis & Extraction
            report(55, "Analyzing topics, subtopics, and concepts via " + aiService.getActiveProviderName() + "...", ProcessingStatus.ANALYZING);
            AIResponse response = aiService.processDocument(document.getTitle(), extractedText);

            // Stage 4: Synthesis
            report(80, "Generating summaries, key definitions, formulas & keywords...", ProcessingStatus.GENERATING);
            document.setTotalTopics(response.getTopics().size());

            // Stage 5: Ready to persist
            report(95, "Finalizing academic notes structure...", ProcessingStatus.SAVING);

            return response;
        } catch (Exception e) {
            logger.error("Error during document processing: {}", e.getMessage(), e);
            report(0, "Failed: " + e.getMessage(), ProcessingStatus.FAILED);
            throw e;
        }
    }

    private void report(int percent, String stage, ProcessingStatus status) {
        if (jobId != null) {
            ProcessingJobQueue.updateJob(jobId, status, percent, stage);
        }
        if (callback != null) {
            callback.onProgress(percent, stage, status);
        }
    }
}
