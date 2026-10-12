package com.studysphere.concurrency;

import com.studysphere.model.ProcessingStatus;

/**
 * Functional interface for reporting asynchronous processing progress to UI and services.
 */
@FunctionalInterface
public interface ProgressCallback {
    void onProgress(int progressPercent, String stageDescription, ProcessingStatus status);
}
