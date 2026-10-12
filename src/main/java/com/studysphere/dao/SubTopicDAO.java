package com.studysphere.dao;

import com.studysphere.exception.DatabaseException;
import com.studysphere.model.SubTopic;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for SubTopic entities.
 */
public class SubTopicDAO extends BaseDAO implements Repository<SubTopic> {

    @Override
    public Optional<SubTopic> findById(int id) throws DatabaseException {
        String sql = "SELECT * FROM subtopics WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find subtopic by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public List<SubTopic> findByTopicId(int topicId) throws DatabaseException {
        String sql = "SELECT * FROM subtopics WHERE topic_id = ? ORDER BY order_index ASC";
        List<SubTopic> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, topicId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find subtopics by topic ID: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<SubTopic> findAll() throws DatabaseException {
        String sql = "SELECT * FROM subtopics ORDER BY id ASC";
        List<SubTopic> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to list all subtopics: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public SubTopic save(SubTopic subTopic) throws DatabaseException {
        String sql = "INSERT INTO subtopics (topic_id, name, content, order_index) VALUES (?, ?, ?, ?) RETURNING id, created_at";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, subTopic.getTopicId());
            stmt.setString(2, subTopic.getName());
            stmt.setString(3, subTopic.getContent());
            stmt.setInt(4, subTopic.getOrderIndex());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    subTopic.setId(rs.getInt("id"));
                    subTopic.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                }
            }
            return subTopic;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save subtopic: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(SubTopic subTopic) throws DatabaseException {
        String sql = "UPDATE subtopics SET name = ?, content = ?, order_index = ? WHERE id = ?";
        return executeUpdate(sql, subTopic.getName(), subTopic.getContent(), subTopic.getOrderIndex(), subTopic.getId()) > 0;
    }

    @Override
    public boolean deleteById(int id) throws DatabaseException {
        String sql = "DELETE FROM subtopics WHERE id = ?";
        return executeUpdate(sql, id) > 0;
    }

    private SubTopic mapResultSet(ResultSet rs) throws SQLException {
        SubTopic st = new SubTopic();
        st.setId(rs.getInt("id"));
        st.setTopicId(rs.getInt("topic_id"));
        st.setName(rs.getString("name"));
        st.setContent(rs.getString("content"));
        st.setOrderIndex(rs.getInt("order_index"));
        Timestamp c = rs.getTimestamp("created_at");
        if (c != null) st.setCreatedAt(c.toLocalDateTime());
        return st;
    }
}
