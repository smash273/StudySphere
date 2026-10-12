package com.studysphere.service;

import com.studysphere.dao.DocumentDAO;
import com.studysphere.dao.RevisionDAO;
import com.studysphere.dao.TopicDAO;
import com.studysphere.exception.DatabaseException;
import com.studysphere.model.RevisionRecord;
import com.studysphere.model.RevisionStatus;
import com.studysphere.model.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Service managing student revision tracking, mastery calculations,
 * and generating priority recommendations using Java Collections (PriorityQueue).
 */
public class RevisionService {
    private static final Logger logger = LoggerFactory.getLogger(RevisionService.class);

    private final RevisionDAO revisionDAO;
    private final TopicDAO topicDAO;
    private final DocumentDAO documentDAO;

    public RevisionService() {
        this.revisionDAO = new RevisionDAO();
        this.topicDAO = new TopicDAO();
        this.documentDAO = new DocumentDAO();
    }

    public RevisionService(RevisionDAO revisionDAO, TopicDAO topicDAO, DocumentDAO documentDAO) {
        this.revisionDAO = revisionDAO;
        this.topicDAO = topicDAO;
        this.documentDAO = documentDAO;
    }

    /**
     * Marks a topic as revised by student, updates frequency and recency, and recalculates score.
     */
    public RevisionRecord markAsRevised(int userId, int topicId, String notes) throws DatabaseException {
        Optional<RevisionRecord> recOpt = revisionDAO.findByUserAndTopic(userId, topicId);
        RevisionRecord record;
        if (recOpt.isPresent()) {
            record = recOpt.get();
        } else {
            Optional<Topic> topicOpt = topicDAO.findById(topicId);
            double importance = topicOpt.map(Topic::getImportanceScore).orElse(1.0);
            record = new RevisionRecord(userId, topicId, importance);
        }

        record.recordRevision();
        if (notes != null && !notes.isBlank()) {
            record.setNotes(notes);
        }
        revisionDAO.saveOrUpdate(record);
        logger.info("Marked topic {} as revised for user {}. New score: {}", topicId, userId, record.getRevisionScore());
        return record;
    }

    public List<RevisionRecord> getRevisionRecordsForUser(int userId) throws DatabaseException {
        List<RevisionRecord> records = revisionDAO.findAllByUser(userId);
        // Refresh scores with current time decay
        for (RevisionRecord r : records) {
            r.recalculateScore();
        }
        return records;
    }

    /**
     * Recommends topics needing urgent revision using a PriorityQueue ordered by revision urgency.
     * Demonstrates Collections Framework: PriorityQueue with custom Comparator.
     */
    public List<Topic> getRecommendedRevisionTopics(int userId, int limit) throws DatabaseException {
        List<Topic> allTopics = topicDAO.findByUserId(userId);
        if (allTopics.isEmpty()) return Collections.emptyList();

        List<RevisionRecord> records = revisionDAO.findAllByUser(userId);
        Map<Integer, RevisionRecord> recMap = new HashMap<>();
        for (RevisionRecord r : records) {
            r.recalculateScore();
            recMap.put(r.getTopicId(), r);
        }

        // PriorityQueue ordered such that lowest revision score and highest importance has highest priority
        PriorityQueue<Topic> priorityQueue = new PriorityQueue<>(11, (t1, t2) -> {
            RevisionRecord r1 = recMap.get(t1.getId());
            RevisionRecord r2 = recMap.get(t2.getId());
            double score1 = r1 != null ? r1.getRevisionScore() : 0.0;
            double score2 = r2 != null ? r2.getRevisionScore() : 0.0;

            // Prioritize topics with lower revision score
            int scoreCmp = Double.compare(score1, score2);
            if (scoreCmp != 0) return scoreCmp;

            // Tie-break with higher importance
            return Double.compare(t2.getImportanceScore(), t1.getImportanceScore());
        });

        for (Topic t : allTopics) {
            t.setRevisionRecord(recMap.get(t.getId()));
            priorityQueue.offer(t);
        }

        List<Topic> recommended = new ArrayList<>();
        int count = 0;
        while (!priorityQueue.isEmpty() && count < limit) {
            recommended.add(priorityQueue.poll());
            count++;
        }
        return recommended;
    }

    /**
     * Aggregates dashboard statistics for a student user.
     */
    public Map<String, Object> getDashboardStats(int userId) throws DatabaseException {
        Map<String, Object> stats = new HashMap<>();
        var docs = documentDAO.findByUserId(userId);
        var topics = topicDAO.findByUserId(userId);
        var records = revisionDAO.findAllByUser(userId);

        int totalDocs = docs.size();
        int totalTopics = topics.size();
        long revisedTopicsCount = records.stream().filter(r -> r.getRevisionCount() > 0).count();
        long masteredCount = records.stream().filter(r -> r.getStatus() == RevisionStatus.MASTERED).count();

        double completionPercent = totalTopics > 0 ? (revisedTopicsCount * 100.0 / totalTopics) : 0.0;

        stats.put("totalDocuments", totalDocs);
        stats.put("totalTopics", totalTopics);
        stats.put("revisedTopics", (int) revisedTopicsCount);
        stats.put("masteredTopics", (int) masteredCount);
        stats.put("completionPercent", Math.round(completionPercent * 10.0) / 10.0);

        return stats;
    }
}
