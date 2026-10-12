package com.studysphere.dao;

import com.studysphere.config.DatabaseConnectionManager;
import com.studysphere.exception.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Abstract Base Data Access Object (DAO) providing core JDBC functionality,
 * resource safety via try-with-resources, and transaction support.
 */
public abstract class BaseDAO {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Obtains an active connection from the pool.
     */
    protected Connection getConnection() throws DatabaseException {
        try {
            return DatabaseConnectionManager.getInstance().getConnection();
        } catch (SQLException e) {
            logger.error("Failed to acquire database connection: {}", e.getMessage());
            throw new DatabaseException("Could not connect to database: " + e.getMessage(), e);
        }
    }

    /**
     * Executes a modification statement (INSERT, UPDATE, DELETE).
     */
    protected int executeUpdate(String sql, Object... params) throws DatabaseException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParameters(stmt, params);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Database update failed for SQL [{}]: {}", sql, e.getMessage());
            throw new DatabaseException("Database modification error: " + e.getMessage(), e);
        }
    }

    /**
     * Binds parameters to a PreparedStatement using type inspection.
     */
    protected void setParameters(PreparedStatement stmt, Object... params) throws SQLException {
        if (params == null) return;
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            int index = i + 1;
            if (param == null) {
                stmt.setNull(index, java.sql.Types.NULL);
            } else if (param instanceof String) {
                stmt.setString(index, (String) param);
            } else if (param instanceof Integer) {
                stmt.setInt(index, (Integer) param);
            } else if (param instanceof Long) {
                stmt.setLong(index, (Long) param);
            } else if (param instanceof Double) {
                stmt.setDouble(index, (Double) param);
            } else if (param instanceof Boolean) {
                stmt.setBoolean(index, (Boolean) param);
            } else if (param instanceof java.sql.Timestamp) {
                stmt.setTimestamp(index, (java.sql.Timestamp) param);
            } else if (param instanceof java.time.LocalDateTime) {
                stmt.setTimestamp(index, java.sql.Timestamp.valueOf((java.time.LocalDateTime) param));
            } else if (param instanceof Enum<?>) {
                stmt.setString(index, ((Enum<?>) param).name());
            } else {
                stmt.setObject(index, param);
            }
        }
    }
}
