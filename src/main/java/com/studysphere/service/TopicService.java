package com.studysphere.service;

import com.studysphere.dao.TopicDAO;
import com.studysphere.exception.DatabaseException;
import com.studysphere.model.Topic;

import java.util.List;
import java.util.Optional;

/**
 * Service managing Topic hierarchical trees, searching, and topic-specific queries.
 */
public class TopicService {
    private final TopicDAO topicDAO;

    public TopicService() {
        this.topicDAO = new TopicDAO();
    }

    public TopicService(TopicDAO topicDAO) {
        this.topicDAO = topicDAO;
    }

    public Optional<Topic> getTopicById(int id) throws DatabaseException {
        return topicDAO.findById(id);
    }

    public List<Topic> getTopicsByDocumentId(int documentId) throws DatabaseException {
        return topicDAO.findByDocumentId(documentId);
    }

    public List<Topic> getTopicsByUserId(int userId) throws DatabaseException {
        return topicDAO.findByUserId(userId);
    }

    public Topic getTopicDetails(int id) throws DatabaseException {
        return topicDAO.findById(id).orElse(null);
    }

    public List<Topic> searchTopics(String query, int userId) throws DatabaseException {
        if (query == null || query.trim().isEmpty()) {
            return getTopicsByUserId(userId);
        }
        return topicDAO.searchTopics(query.trim(), userId);
    }
}

