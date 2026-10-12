package com.studysphere.service;

import com.studysphere.dao.DocumentDAO;
import com.studysphere.dao.TopicDAO;
import com.studysphere.exception.DatabaseException;
import com.studysphere.model.Document;
import com.studysphere.model.Topic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Full-text unified search service across documents, topics, definitions, and points.
 */
public class SearchService {
    private final TopicDAO topicDAO;
    private final DocumentDAO documentDAO;

    public SearchService() {
        this.topicDAO = new TopicDAO();
        this.documentDAO = new DocumentDAO();
    }

    public Map<String, Object> searchAll(String query, int userId) throws DatabaseException {
        Map<String, Object> results = new HashMap<>();
        if (query == null || query.trim().isEmpty()) {
            results.put("documents", new ArrayList<>());
            results.put("topics", new ArrayList<>());
            return results;
        }

        String q = query.trim().toLowerCase();

        // Search matching documents
        List<Document> userDocs = documentDAO.findByUserId(userId);
        List<Document> matchedDocs = new ArrayList<>();
        for (Document d : userDocs) {
            if (d.getTitle().toLowerCase().contains(q) || d.getOriginalFilename().toLowerCase().contains(q)) {
                matchedDocs.add(d);
            }
        }

        // Search matching topics
        List<Topic> matchedTopics = topicDAO.searchTopics(query, userId);

        results.put("documents", matchedDocs);
        results.put("topics", matchedTopics);
        return results;
    }

    public List<Topic> search(int userId, String query) throws DatabaseException {
        return topicDAO.searchTopics(query, userId);
    }
}

