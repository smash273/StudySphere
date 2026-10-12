package com.studysphere.dao;

import com.studysphere.exception.DatabaseException;
import com.studysphere.model.Document;
import com.studysphere.model.DocumentChunk;
import com.studysphere.model.ProcessingStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Document and DocumentChunk entities.
 */
public class DocumentDAO extends BaseDAO implements Repository<Document> {

    @Override
    public Optional<Document> findById(int id) throws DatabaseException {
        String sql = "SELECT * FROM documents WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDocument(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find document by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public List<Document> findByUserId(int userId) throws DatabaseException {
        String sql = "SELECT * FROM documents WHERE user_id = ? ORDER BY created_at DESC";
        List<Document> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToDocument(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to list documents for user: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Document> findAll() throws DatabaseException {
        String sql = "SELECT * FROM documents ORDER BY created_at DESC";
        List<Document> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToDocument(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to list all documents: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public Document save(Document doc) throws DatabaseException {
        String sql = "INSERT INTO documents (user_id, title, original_filename, file_type, file_size_bytes, storage_path, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id, created_at, updated_at";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, doc.getUserId());
            stmt.setString(2, doc.getTitle());
            stmt.setString(3, doc.getOriginalFilename());
            stmt.setString(4, doc.getFileType());
            stmt.setLong(5, doc.getFileSizeBytes());
            stmt.setString(6, doc.getStoragePath());
            stmt.setString(7, doc.getStatus().name());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    doc.setId(rs.getInt("id"));
                    doc.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    doc.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                }
            }
            return doc;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save document: " + e.getMessage(), e);
        }
    }

    public void updateStatus(int docId, ProcessingStatus status, int totalChunks, int totalTopics) throws DatabaseException {
        String sql = "UPDATE documents SET status = ?, total_chunks = ?, total_topics = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        executeUpdate(sql, status.name(), totalChunks, totalTopics, docId);
    }

    public void saveChunk(DocumentChunk chunk) throws DatabaseException {
        String sql = "INSERT INTO document_chunks (document_id, chunk_index, start_char, end_char, chunk_text, token_count) " +
                     "VALUES (?, ?, ?, ?, ?, ?) RETURNING id, created_at";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, chunk.getDocumentId());
            stmt.setInt(2, chunk.getChunkIndex());
            stmt.setInt(3, chunk.getStartChar());
            stmt.setInt(4, chunk.getEndChar());
            stmt.setString(5, chunk.getChunkText());
            stmt.setInt(6, chunk.getTokenCount());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    chunk.setId(rs.getInt("id"));
                    chunk.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save document chunk: " + e.getMessage(), e);
        }
    }

    public List<DocumentChunk> findChunksByDocumentId(int docId) throws DatabaseException {
        String sql = "SELECT * FROM document_chunks WHERE document_id = ? ORDER BY chunk_index ASC";
        List<DocumentChunk> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, docId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DocumentChunk c = new DocumentChunk();
                    c.setId(rs.getInt("id"));
                    c.setDocumentId(rs.getInt("document_id"));
                    c.setChunkIndex(rs.getInt("chunk_index"));
                    c.setStartChar(rs.getInt("start_char"));
                    c.setEndChar(rs.getInt("end_char"));
                    c.setChunkText(rs.getString("chunk_text"));
                    c.setTokenCount(rs.getInt("token_count"));
                    c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    list.add(c);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to load chunks: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public boolean update(Document doc) throws DatabaseException {
        String sql = "UPDATE documents SET title = ?, status = ?, total_chunks = ?, total_topics = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        return executeUpdate(sql, doc.getTitle(), doc.getStatus().name(), doc.getTotalChunks(), doc.getTotalTopics(), doc.getId()) > 0;
    }

    @Override
    public boolean deleteById(int id) throws DatabaseException {
        String sql = "DELETE FROM documents WHERE id = ?";
        return executeUpdate(sql, id) > 0;
    }

    private Document mapResultSetToDocument(ResultSet rs) throws SQLException {
        Document d = new Document();
        d.setId(rs.getInt("id"));
        d.setUserId(rs.getInt("user_id"));
        d.setTitle(rs.getString("title"));
        d.setOriginalFilename(rs.getString("original_filename"));
        d.setFileType(rs.getString("file_type"));
        d.setFileSizeBytes(rs.getLong("file_size_bytes"));
        d.setStoragePath(rs.getString("storage_path"));
        d.setStatus(ProcessingStatus.valueOf(rs.getString("status")));
        d.setTotalChunks(rs.getInt("total_chunks"));
        d.setTotalTopics(rs.getInt("total_topics"));
        Timestamp c = rs.getTimestamp("created_at");
        if (c != null) d.setCreatedAt(c.toLocalDateTime());
        Timestamp u = rs.getTimestamp("updated_at");
        if (u != null) d.setUpdatedAt(u.toLocalDateTime());
        return d;
    }
}
