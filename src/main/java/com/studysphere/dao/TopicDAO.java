package com.studysphere.dao;

import com.studysphere.exception.DatabaseException;
import com.studysphere.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Topic entity and its associated subtopics, points, definitions, formulas.
 */
public class TopicDAO extends BaseDAO implements Repository<Topic> {

    @Override
    public Optional<Topic> findById(int id) throws DatabaseException {
        String sql = "SELECT * FROM topics WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Topic topic = mapResultSetToTopic(rs);
                    loadTopicDetails(conn, topic);
                    return Optional.of(topic);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find topic by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public List<Topic> findByDocumentId(int docId) throws DatabaseException {
        String sql = "SELECT * FROM topics WHERE document_id = ? ORDER BY order_index ASC, importance_score DESC";
        List<Topic> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, docId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Topic t = mapResultSetToTopic(rs);
                    loadTopicDetails(conn, t);
                    list.add(t);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find topics by document ID: " + e.getMessage(), e);
        }
        return list;
    }

    public List<Topic> findByUserId(int userId) throws DatabaseException {
        String sql = "SELECT t.* FROM topics t INNER JOIN documents d ON t.document_id = d.id " +
                     "WHERE d.user_id = ? ORDER BY t.importance_score DESC";
        List<Topic> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Topic t = mapResultSetToTopic(rs);
                    loadTopicDetails(conn, t);
                    list.add(t);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find topics for user: " + e.getMessage(), e);
        }
        return list;
    }

    public List<Topic> searchTopics(String query, int userId) throws DatabaseException {
        String sql = "SELECT DISTINCT t.* FROM topics t " +
                     "INNER JOIN documents d ON t.document_id = d.id " +
                     "LEFT JOIN definitions def ON t.id = def.topic_id " +
                     "LEFT JOIN important_points ip ON t.id = ip.topic_id " +
                     "LEFT JOIN keywords k ON t.id = k.topic_id " +
                     "WHERE d.user_id = ? AND (" +
                     "LOWER(t.name) LIKE ? OR LOWER(t.overview) LIKE ? " +
                     "OR LOWER(def.term) LIKE ? OR LOWER(def.definition_text) LIKE ? " +
                     "OR LOWER(ip.point_text) LIKE ? OR LOWER(k.keyword) LIKE ?) " +
                     "ORDER BY t.importance_score DESC";
        List<Topic> list = new ArrayList<>();
        String wild = "%" + query.toLowerCase() + "%";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, wild);
            stmt.setString(3, wild);
            stmt.setString(4, wild);
            stmt.setString(5, wild);
            stmt.setString(6, wild);
            stmt.setString(7, wild);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Topic t = mapResultSetToTopic(rs);
                    loadTopicDetails(conn, t);
                    list.add(t);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Search failed: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Topic> findAll() throws DatabaseException {
        String sql = "SELECT * FROM topics ORDER BY id ASC";
        List<Topic> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToTopic(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to list all topics: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public Topic save(Topic topic) throws DatabaseException {
        String sql = "INSERT INTO topics (document_id, name, overview, importance_score, priority_level, order_index) " +
                     "VALUES (?, ?, ?, ?, ?, ?) RETURNING id, created_at";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, topic.getDocumentId());
            stmt.setString(2, topic.getName());
            stmt.setString(3, topic.getOverview());
            stmt.setDouble(4, topic.getImportanceScore());
            stmt.setString(5, topic.getPriorityLevel().name());
            stmt.setInt(6, topic.getOrderIndex());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    topic.setId(rs.getInt("id"));
                    topic.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                }
            }

            // Persist child collections
            saveChildren(conn, topic);
            return topic;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save topic: " + e.getMessage(), e);
        }
    }

    private void saveChildren(Connection conn, Topic topic) throws SQLException {
        // Save subtopics
        if (topic.getSubtopics() != null) {
            String subSql = "INSERT INTO subtopics (topic_id, name, content, order_index) VALUES (?, ?, ?, ?) RETURNING id";
            for (SubTopic st : topic.getSubtopics()) {
                try (PreparedStatement stmt = conn.prepareStatement(subSql)) {
                    stmt.setInt(1, topic.getId());
                    stmt.setString(2, st.getName());
                    stmt.setString(3, st.getContent());
                    stmt.setInt(4, st.getOrderIndex());
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) st.setId(rs.getInt("id"));
                    }
                }
            }
        }

        // Save summary
        if (topic.getSummary() != null) {
            String sumSql = "INSERT INTO summaries (document_id, topic_id, summary_type, summary_text) VALUES (?, ?, ?, ?) RETURNING id";
            try (PreparedStatement stmt = conn.prepareStatement(sumSql)) {
                stmt.setInt(1, topic.getDocumentId());
                stmt.setInt(2, topic.getId());
                stmt.setString(3, topic.getSummary().getSummaryType());
                stmt.setString(4, topic.getSummary().getSummaryText());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) topic.getSummary().setId(rs.getInt("id"));
                }
            }
        }

        // Save important points
        if (topic.getImportantPoints() != null) {
            String ptSql = "INSERT INTO important_points (topic_id, point_text, weight) VALUES (?, ?, ?) RETURNING id";
            for (ImportantPoint pt : topic.getImportantPoints()) {
                try (PreparedStatement stmt = conn.prepareStatement(ptSql)) {
                    stmt.setInt(1, topic.getId());
                    stmt.setString(2, pt.getPointText());
                    stmt.setDouble(3, pt.getWeight());
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) pt.setId(rs.getInt("id"));
                    }
                }
            }
        }

        // Save definitions
        if (topic.getDefinitions() != null) {
            String defSql = "INSERT INTO definitions (topic_id, term, definition_text) VALUES (?, ?, ?) RETURNING id";
            for (Definition def : topic.getDefinitions()) {
                try (PreparedStatement stmt = conn.prepareStatement(defSql)) {
                    stmt.setInt(1, topic.getId());
                    stmt.setString(2, def.getTerm());
                    stmt.setString(3, def.getDefinitionText());
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) def.setId(rs.getInt("id"));
                    }
                }
            }
        }

        // Save formulas
        if (topic.getFormulas() != null) {
            String formSql = "INSERT INTO formulas (topic_id, expression, explanation, variables_json) VALUES (?, ?, ?, ?) RETURNING id";
            for (Formula f : topic.getFormulas()) {
                try (PreparedStatement stmt = conn.prepareStatement(formSql)) {
                    stmt.setInt(1, topic.getId());
                    stmt.setString(2, f.getExpression());
                    stmt.setString(3, f.getExplanation());
                    stmt.setString(4, f.getVariablesJson());
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) f.setId(rs.getInt("id"));
                    }
                }
            }
        }

        // Save keywords
        if (topic.getKeywords() != null) {
            String keySql = "INSERT INTO keywords (topic_id, keyword, relevance_score, occurrence_count) VALUES (?, ?, ?, ?) RETURNING id";
            for (Keyword k : topic.getKeywords()) {
                try (PreparedStatement stmt = conn.prepareStatement(keySql)) {
                    stmt.setInt(1, topic.getId());
                    stmt.setString(2, k.getKeyword());
                    stmt.setDouble(3, k.getRelevanceScore());
                    stmt.setInt(4, k.getOccurrenceCount());
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) k.setId(rs.getInt("id"));
                    }
                }
            }
        }
    }

    private void loadTopicDetails(Connection conn, Topic t) throws SQLException {
        // Load subtopics
        String subSql = "SELECT * FROM subtopics WHERE topic_id = ? ORDER BY order_index ASC";
        try (PreparedStatement stmt = conn.prepareStatement(subSql)) {
            stmt.setInt(1, t.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    t.getSubtopics().add(new SubTopic(rs.getInt("id"), rs.getInt("topic_id"),
                            rs.getString("name"), rs.getString("content"), rs.getInt("order_index"),
                            rs.getTimestamp("created_at").toLocalDateTime()));
                }
            }
        }

        // Load summary
        String sumSql = "SELECT * FROM summaries WHERE topic_id = ? ORDER BY id DESC LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(sumSql)) {
            stmt.setInt(1, t.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    t.setSummary(new Summary(rs.getInt("id"), rs.getInt("document_id"), rs.getInt("topic_id"),
                            rs.getString("summary_type"), rs.getString("summary_text"),
                            rs.getTimestamp("created_at").toLocalDateTime()));
                }
            }
        }

        // Load points
        String ptSql = "SELECT * FROM important_points WHERE topic_id = ? ORDER BY id ASC";
        try (PreparedStatement stmt = conn.prepareStatement(ptSql)) {
            stmt.setInt(1, t.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    t.getImportantPoints().add(new ImportantPoint(rs.getInt("id"), rs.getInt("topic_id"),
                            (Integer) rs.getObject("subtopic_id"), rs.getString("point_text"),
                            rs.getDouble("weight"), rs.getTimestamp("created_at").toLocalDateTime()));
                }
            }
        }

        // Load definitions
        String defSql = "SELECT * FROM definitions WHERE topic_id = ? ORDER BY id ASC";
        try (PreparedStatement stmt = conn.prepareStatement(defSql)) {
            stmt.setInt(1, t.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    t.getDefinitions().add(new Definition(rs.getInt("id"), rs.getInt("topic_id"),
                            rs.getString("term"), rs.getString("definition_text"),
                            rs.getTimestamp("created_at").toLocalDateTime()));
                }
            }
        }

        // Load formulas
        String formSql = "SELECT * FROM formulas WHERE topic_id = ? ORDER BY id ASC";
        try (PreparedStatement stmt = conn.prepareStatement(formSql)) {
            stmt.setInt(1, t.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    t.getFormulas().add(new Formula(rs.getInt("id"), rs.getInt("topic_id"),
                            rs.getString("expression"), rs.getString("explanation"),
                            rs.getString("variables_json"), rs.getTimestamp("created_at").toLocalDateTime()));
                }
            }
        }

        // Load keywords
        String keySql = "SELECT * FROM keywords WHERE topic_id = ? ORDER BY occurrence_count DESC";
        try (PreparedStatement stmt = conn.prepareStatement(keySql)) {
            stmt.setInt(1, t.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    t.getKeywords().add(new Keyword(rs.getInt("id"), rs.getInt("topic_id"),
                            rs.getString("keyword"), rs.getDouble("relevance_score"),
                            rs.getInt("occurrence_count")));
                }
            }
        }
    }

    @Override
    public boolean update(Topic topic) throws DatabaseException {
        String sql = "UPDATE topics SET name = ?, overview = ?, importance_score = ?, priority_level = ? WHERE id = ?";
        return executeUpdate(sql, topic.getName(), topic.getOverview(), topic.getImportanceScore(),
                topic.getPriorityLevel().name(), topic.getId()) > 0;
    }

    @Override
    public boolean deleteById(int id) throws DatabaseException {
        String sql = "DELETE FROM topics WHERE id = ?";
        return executeUpdate(sql, id) > 0;
    }

    private Topic mapResultSetToTopic(ResultSet rs) throws SQLException {
        Topic t = new Topic();
        t.setId(rs.getInt("id"));
        t.setDocumentId(rs.getInt("document_id"));
        t.setName(rs.getString("name"));
        t.setOverview(rs.getString("overview"));
        t.setImportanceScore(rs.getDouble("importance_score"));
        t.setPriorityLevel(PriorityLevel.valueOf(rs.getString("priority_level")));
        t.setOrderIndex(rs.getInt("order_index"));
        Timestamp c = rs.getTimestamp("created_at");
        if (c != null) t.setCreatedAt(c.toLocalDateTime());
        return t;
    }
}
