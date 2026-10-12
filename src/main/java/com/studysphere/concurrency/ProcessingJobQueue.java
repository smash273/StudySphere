package com.studysphere.concurrency;

import com.studysphere.model.ProcessingJob;
import com.studysphere.model.ProcessingStatus;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Thread-safe queue and tracking registry for asynchronous document processing jobs.
 * Demonstrates Collections Framework (ConcurrentHashMap and ConcurrentLinkedQueue).
 */
public class ProcessingJobQueue {
    private static final Map<String, ProcessingJob> activeJobs = new ConcurrentHashMap<>();
    private static final ConcurrentLinkedQueue<ProcessingJob> recentCompletedJobs = new ConcurrentLinkedQueue<>();

    public static void registerJob(ProcessingJob job) {
        if (job != null && job.getId() != null) {
            activeJobs.put(job.getId(), job);
        }
    }

    public static Optional<ProcessingJob> getJob(String jobId) {
        if (jobId == null) return Optional.empty();
        return Optional.ofNullable(activeJobs.get(jobId));
    }

    public static void updateJob(String jobId, ProcessingStatus status, int progress, String stage) {
        ProcessingJob job = activeJobs.get(jobId);
        if (job != null) {
            job.updateProgress(status, progress, stage);
            if (status == ProcessingStatus.COMPLETED || status == ProcessingStatus.FAILED) {
                recentCompletedJobs.offer(job);
                // Keep recent queue bounded to last 50 jobs
                if (recentCompletedJobs.size() > 50) {
                    recentCompletedJobs.poll();
                }
            }
        }
    }

    public static Map<String, ProcessingJob> getActiveJobs() {
        return activeJobs;
    }
}
