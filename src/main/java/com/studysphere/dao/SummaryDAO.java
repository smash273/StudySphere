package com.studysphere.dao;

import com.studysphere.exception.DatabaseException;
import com.studysphere.model.Summary;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Summary entity.
 */
public class SummaryDAO extends BaseDAO implements Repository<Summary> {

    @Override
    public Optional<Summary> findById(int id) throws DatabaseException {
        String sql = "SELECT * FROM summaries WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find summary by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Optional<Summary> findByTopicId(int topicId) throws DatabaseException {
        String sql = "SELECT * FROM summaries WHERE topic_id = ? ORDER BY id DESC LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, topicId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find summary for topic: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public List<Summary> findByDocumentId(int docId) throws DatabaseException {
        String sql = "SELECT * FROM summaries WHERE document_id = ? ORDER BY id ASC";
        List<Summary> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, docId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to list summaries for document: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Summary> findAll() throws DatabaseException {
        String sql = "SELECT * FROM summaries ORDER BY id ASC";
        List<Summary> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to list summaries: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public Summary save(Summary summary) throws DatabaseException {
        String sql = "INSERT INTO summaries (document_id, topic_id, summary_type, summary_text) " +
                     "VALUES (?, ?, ?, ?) RETURNING id, created_at";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, summary.getDocumentId());
            if (summary.getTopicId() != null) {
                stmt.setInt(2, summary.getTopicId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setString(3, summary.getSummaryType());
            stmt.setString(4, summary.getSummaryText());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    summary.setId(rs.getInt("id"));
                    summary.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                }
            }
            return summary;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save summary: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Summary summary) throws DatabaseException {
        String sql = "UPDATE summaries SET summary_type = ?, summary_text = ? WHERE id = ?";
        return executeUpdate(sql, summary.getSummaryType(), summary.getSummaryText(), summary.getId()) > 0;
    }

    @Override
    public boolean deleteById(int id) throws DatabaseException {
        String sql = "DELETE FROM summaries WHERE id = ?";
        return executeUpdate(sql, id) > 0;
    }

    private Summary mapResultSet(ResultSet rs) throws SQLException {
        Summary s = new Summary();
        s.setId(rs.getInt("id"));
        s.setDocumentId(rs.getInt("document_id"));
        s.setTopicId((Integer) rs.getObject("topic_id"));
        s.setSummaryType(rs.getString("summary_type"));
        s.setSummaryText(rs.getString("summary_text"));
        Timestamp c = rs.getTimestamp("created_at");
        if (c != null) s.setCreatedAt(c.toLocalDateTime());
        return s;
    }
}
