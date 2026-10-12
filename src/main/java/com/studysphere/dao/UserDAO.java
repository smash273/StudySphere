package com.studysphere.dao;

import com.studysphere.exception.DatabaseException;
import com.studysphere.model.Student;
import com.studysphere.model.User;
import com.studysphere.model.UserRole;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for User and Student entities.
 * Demonstrates JDBC PreparedStatement usage, transaction handling, and OOP inheritance persistence.
 */
public class UserDAO extends BaseDAO implements Repository<User> {

    @Override
    public Optional<User> findById(int id) throws DatabaseException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find user by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Optional<User> findByUsername(String username) throws DatabaseException {
        String sql = "SELECT * FROM users WHERE LOWER(username) = LOWER(?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find user by username: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Optional<User> findByEmail(String email) throws DatabaseException {
        String sql = "SELECT * FROM users WHERE LOWER(email) = LOWER(?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find user by email: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Optional<Student> findStudentByUserId(int userId) throws DatabaseException {
        String sql = "SELECT u.*, s.student_id, s.enrollment_no, s.major, s.semester, s.overall_score " +
                     "FROM users u INNER JOIN students s ON u.id = s.user_id WHERE u.id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User base = mapResultSetToUser(rs);
                    Student s = new Student(base.getId(), base.getUsername(), base.getEmail(),
                            base.getPasswordHash(), base.getFullName(), base.getRole(),
                            base.getCreatedAt(), base.getUpdatedAt(),
                            rs.getString("student_id"), rs.getString("enrollment_no"),
                            rs.getString("major"), rs.getInt("semester"), rs.getDouble("overall_score"));
                    return Optional.of(s);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find student by user ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() throws DatabaseException {
        String sql = "SELECT * FROM users ORDER BY id ASC";
        List<User> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to list users: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public User save(User user) throws DatabaseException {
        String sql = "INSERT INTO users (username, email, password_hash, full_name, role) " +
                     "VALUES (?, ?, ?, ?, ?) RETURNING id, created_at, updated_at";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getFullName());
            stmt.setString(5, user.getRole().name());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user.setId(rs.getInt("id"));
                    user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    user.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                }
            }

            // If user is a Student instance, persist student profile in students table
            if (user instanceof Student student) {
                saveStudentProfile(conn, student);
            }
            return user;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to insert user: " + e.getMessage(), e);
        }
    }

    private void saveStudentProfile(Connection conn, Student s) throws SQLException {
        String sql = "INSERT INTO students (user_id, student_id, enrollment_no, major, semester, overall_score) " +
                     "VALUES (?, ?, ?, ?, ?, ?) ON CONFLICT (user_id) DO UPDATE SET " +
                     "student_id = EXCLUDED.student_id, enrollment_no = EXCLUDED.enrollment_no, " +
                     "major = EXCLUDED.major, semester = EXCLUDED.semester, overall_score = EXCLUDED.overall_score";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, s.getId());
            stmt.setString(2, s.getStudentId());
            stmt.setString(3, s.getEnrollmentNo());
            stmt.setString(4, s.getMajor());
            stmt.setInt(5, s.getSemester());
            stmt.setDouble(6, s.getOverallScore());
            stmt.executeUpdate();
        }
    }

    @Override
    public boolean update(User user) throws DatabaseException {
        String sql = "UPDATE users SET full_name = ?, email = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        return executeUpdate(sql, user.getFullName(), user.getEmail(), user.getId()) > 0;
    }

    @Override
    public boolean deleteById(int id) throws DatabaseException {
        String sql = "DELETE FROM users WHERE id = ?";
        return executeUpdate(sql, id) > 0;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setFullName(rs.getString("full_name"));
        u.setRole(UserRole.valueOf(rs.getString("role")));
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) u.setCreatedAt(created.toLocalDateTime());
        Timestamp updated = rs.getTimestamp("updated_at");
        if (updated != null) u.setUpdatedAt(updated.toLocalDateTime());
        return u;
    }
}
