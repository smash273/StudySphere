package com.studysphere.service;

import com.studysphere.ai.AIService;
import com.studysphere.concurrency.DocumentProcessingTask;
import com.studysphere.concurrency.ProcessingJobQueue;
import com.studysphere.concurrency.ProgressCallback;
import com.studysphere.concurrency.ThreadPoolManager;
import com.studysphere.config.DatabaseConfig;
import com.studysphere.dao.DocumentDAO;
import com.studysphere.dao.RevisionDAO;
import com.studysphere.dao.SummaryDAO;
import com.studysphere.dao.TopicDAO;
import com.studysphere.document.DocumentParserFactory;
import com.studysphere.exception.DatabaseException;
import com.studysphere.exception.DocumentProcessingException;
import com.studysphere.exception.InvalidFileException;
import com.studysphere.exception.UnsupportedFileException;
import com.studysphere.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Service orchestrating Document Uploads, Multithreaded Processing, and Database Persistence.
 */
public class DocumentService {
    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);

    private final DocumentDAO documentDAO;
    private final TopicDAO topicDAO;
    private final SummaryDAO summaryDAO;
    private final RevisionDAO revisionDAO;
    private final AIService aiService;

    public DocumentService() {
        this.documentDAO = new DocumentDAO();
        this.topicDAO = new TopicDAO();
        this.summaryDAO = new SummaryDAO();
        this.revisionDAO = new RevisionDAO();
        this.aiService = new AIService();
    }

    public List<Document> getDocumentsForUser(int userId) throws DatabaseException {
        return documentDAO.findByUserId(userId);
    }

    public Optional<Document> getDocumentById(int id) throws DatabaseException {
        return documentDAO.findById(id);
    }

    /**
     * Uploads and initiates asynchronous processing of an academic document.
     */
    public CompletableFuture<Document> uploadAndProcessAsync(int userId, String title, String originalFilename,
                                                             InputStream inputStream, long fileSizeBytes,
                                                             ProgressCallback callback)
            throws InvalidFileException, UnsupportedFileException, DatabaseException {

        validateUpload(originalFilename, fileSizeBytes);

        // Determine file extension
        String extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toUpperCase();

        // Ensure upload directory exists
        Path uploadDirPath = Paths.get(DatabaseConfig.getUploadDir());
        try {
            if (!Files.exists(uploadDirPath)) {
                Files.createDirectories(uploadDirPath);
            }
        } catch (IOException e) {
            throw new InvalidFileException("Could not create upload directory: " + e.getMessage(), e);
        }

        // Save file with unique name
        String uniqueFileName = UUID.randomUUID() + "_" + originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
        Path targetPath = uploadDirPath.resolve(uniqueFileName);
        try {
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new InvalidFileException("Failed to save uploaded file to storage: " + e.getMessage(), e);
        }

        // Create and persist Document record in QUEUED state
        Document doc = new Document(userId, title, originalFilename, extension, fileSizeBytes, targetPath.toAbsolutePath().toString());
        doc = documentDAO.save(doc);

        final int docId = doc.getId();
        final Document finalDoc = doc;
        final String jobId = UUID.randomUUID().toString();
        ProcessingJob job = new ProcessingJob(jobId, docId, userId);
        ProcessingJobQueue.registerJob(job);

        // Submit processing task to ThreadPoolManager
        DocumentProcessingTask task = new DocumentProcessingTask(finalDoc, targetPath.toFile(), aiService, callback, jobId);

        return ThreadPoolManager.getInstance().supplyAsync(() -> {
            try {
                AIResponse aiResponse = task.call();

                // Persist Executive Summary
                if (aiResponse.getExecutiveSummary() != null) {
                    Summary execSum = new Summary(docId, null, "DOCUMENT", aiResponse.getExecutiveSummary());
                    summaryDAO.save(execSum);
                }

                // Persist Topics and Children
                for (Topic t : aiResponse.getTopics()) {
                    t.setDocumentId(docId);
                    Topic savedTopic = topicDAO.save(t);

                    // Initialize a revision tracking record for the student
                    RevisionRecord rev = new RevisionRecord(userId, savedTopic.getId(), savedTopic.getImportanceScore());
                    revisionDAO.saveOrUpdate(rev);
                }

                // Update document status to COMPLETED
                documentDAO.updateStatus(docId, ProcessingStatus.COMPLETED, finalDoc.getTotalChunks(), aiResponse.getTopics().size());
                finalDoc.setStatus(ProcessingStatus.COMPLETED);
                finalDoc.setTotalTopics(aiResponse.getTopics().size());

                ProcessingJobQueue.updateJob(jobId, ProcessingStatus.COMPLETED, 100, "Processing completed successfully!");
                if (callback != null) {
                    callback.onProgress(100, "Completed", ProcessingStatus.COMPLETED);
                }
                return finalDoc;
            } catch (Exception e) {
                logger.error("Asynchronous processing task failed for doc ID {}: {}", docId, e.getMessage(), e);
                try {
                    documentDAO.updateStatus(docId, ProcessingStatus.FAILED, 0, 0);
                } catch (DatabaseException ignored) {}
                ProcessingJobQueue.updateJob(jobId, ProcessingStatus.FAILED, 0, "Failed: " + e.getMessage());
                throw new RuntimeException(e);
            }
        });
    }

    private void validateUpload(String filename, long sizeBytes) throws InvalidFileException, UnsupportedFileException {
        if (filename == null || filename.isBlank()) {
            throw new InvalidFileException("Filename cannot be empty.");
        }
        if (sizeBytes <= 0) {
            throw new InvalidFileException("Uploaded file is empty (0 bytes).");
        }
        if (sizeBytes > DatabaseConfig.getMaxUploadSizeBytes()) {
            throw new InvalidFileException("File size exceeds limit of " + (DatabaseConfig.getMaxUploadSizeBytes() / (1024 * 1024)) + " MB.");
        }
        if (!DocumentParserFactory.isSupported(filename)) {
            String ext = filename.contains(".") ? filename.substring(filename.lastIndexOf('.') + 1) : "unknown";
            throw new UnsupportedFileException(ext);
        }
    }

    public boolean deleteDocument(int id) throws DatabaseException {
        Optional<Document> docOpt = documentDAO.findById(id);
        if (docOpt.isPresent()) {
            Document doc = docOpt.get();
            try {
                Files.deleteIfExists(Paths.get(doc.getStoragePath()));
            } catch (IOException ignored) {}
            return documentDAO.deleteById(id);
        }
        return false;
    }
}
