package com.studysphere.dao;

import com.studysphere.exception.DatabaseException;
import com.studysphere.model.RevisionRecord;
import com.studysphere.model.RevisionStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for RevisionRecord entity and learning progress analytics.
 */
public class RevisionDAO extends BaseDAO implements Repository<RevisionRecord> {

    @Override
    public Optional<RevisionRecord> findById(int id) throws DatabaseException {
        String sql = "SELECT r.*, t.name AS topic_name, d.title AS doc_title " +
                     "FROM revision_records r " +
                     "INNER JOIN topics t ON r.topic_id = t.id " +
                     "INNER JOIN documents d ON t.document_id = d.id " +
                     "WHERE r.id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find revision record by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Optional<RevisionRecord> findByUserAndTopic(int userId, int topicId) throws DatabaseException {
        String sql = "SELECT r.*, t.name AS topic_name, d.title AS doc_title " +
                     "FROM revision_records r " +
                     "INNER JOIN topics t ON r.topic_id = t.id " +
                     "INNER JOIN documents d ON t.document_id = d.id " +
                     "WHERE r.user_id = ? AND r.topic_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, topicId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find revision record by user and topic: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public List<RevisionRecord> findAllByUser(int userId) throws DatabaseException {
        String sql = "SELECT r.*, t.name AS topic_name, d.title AS doc_title " +
                     "FROM revision_records r " +
                     "INNER JOIN topics t ON r.topic_id = t.id " +
                     "INNER JOIN documents d ON t.document_id = d.id " +
                     "WHERE r.user_id = ? ORDER BY r.revision_score ASC, r.importance_weight DESC";
        List<RevisionRecord> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to list revision records: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<RevisionRecord> findAll() throws DatabaseException {
        String sql = "SELECT r.*, t.name AS topic_name, d.title AS doc_title " +
                     "FROM revision_records r " +
                     "INNER JOIN topics t ON r.topic_id = t.id " +
                     "INNER JOIN documents d ON t.document_id = d.id " +
                     "ORDER BY r.id ASC";
        List<RevisionRecord> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to list all revision records: " + e.getMessage(), e);
        }
        return list;
    }

    public RevisionRecord saveOrUpdate(RevisionRecord record) throws DatabaseException {
        String sql = "INSERT INTO revision_records " +
                     "(user_id, topic_id, revision_count, last_reviewed, importance_weight, recency_factor, revision_score, status, notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON CONFLICT (user_id, topic_id) DO UPDATE SET " +
                     "revision_count = EXCLUDED.revision_count, last_reviewed = EXCLUDED.last_reviewed, " +
                     "importance_weight = EXCLUDED.importance_weight, recency_factor = EXCLUDED.recency_factor, " +
                     "revision_score = EXCLUDED.revision_score, status = EXCLUDED.status, notes = EXCLUDED.notes, " +
                     "updated_at = CURRENT_TIMESTAMP RETURNING id, created_at, updated_at";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, record.getUserId());
            stmt.setInt(2, record.getTopicId());
            stmt.setInt(3, record.getRevisionCount());
            if (record.getLastReviewed() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(record.getLastReviewed()));
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
            }
            stmt.setDouble(5, record.getImportanceWeight());
            stmt.setDouble(6, record.getRecencyFactor());
            stmt.setDouble(7, record.getRevisionScore());
            stmt.setString(8, record.getStatus().name());
            stmt.setString(9, record.getNotes());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    record.setId(rs.getInt("id"));
                    record.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    record.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                }
            }
            return record;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save or update revision record: " + e.getMessage(), e);
        }
    }

    @Override
    public RevisionRecord save(RevisionRecord entity) throws DatabaseException {
        return saveOrUpdate(entity);
    }

    @Override
    public boolean update(RevisionRecord record) throws DatabaseException {
        saveOrUpdate(record);
        return true;
    }

    @Override
    public boolean deleteById(int id) throws DatabaseException {
        String sql = "DELETE FROM revision_records WHERE id = ?";
        return executeUpdate(sql, id) > 0;
    }

    private RevisionRecord mapResultSet(ResultSet rs) throws SQLException {
        RevisionRecord r = new RevisionRecord();
        r.setId(rs.getInt("id"));
        r.setUserId(rs.getInt("user_id"));
        r.setTopicId(rs.getInt("topic_id"));
        r.setRevisionCount(rs.getInt("revision_count"));
        Timestamp lr = rs.getTimestamp("last_reviewed");
        if (lr != null) r.setLastReviewed(lr.toLocalDateTime());
        r.setImportanceWeight(rs.getDouble("importance_weight"));
        r.setRecencyFactor(rs.getDouble("recency_factor"));
        r.setRevisionScore(rs.getDouble("revision_score"));
        r.setStatus(RevisionStatus.valueOf(rs.getString("status")));
        r.setNotes(rs.getString("notes"));
        Timestamp c = rs.getTimestamp("created_at");
        if (c != null) r.setCreatedAt(c.toLocalDateTime());
        Timestamp u = rs.getTimestamp("updated_at");
        if (u != null) r.setUpdatedAt(u.toLocalDateTime());

        try {
            r.setTopicName(rs.getString("topic_name"));
            r.setDocumentTitle(rs.getString("doc_title"));
        } catch (SQLException ignored) {}

        return r;
    }
}
