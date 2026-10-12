package com.studysphere.dao;

import com.studysphere.exception.DatabaseException;
import com.studysphere.model.Question;
import com.studysphere.model.QuizAttempt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Quiz Questions and Quiz Attempts.
 */
public class QuizDAO extends BaseDAO {

    public List<Question> findQuestionsByTopicId(int topicId) throws DatabaseException {
        String sql = "SELECT * FROM quiz_questions WHERE topic_id = ? ORDER BY id ASC";
        List<Question> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, topicId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Question q = new Question();
                    q.setId(rs.getInt("id"));
                    q.setTopicId(rs.getInt("topic_id"));
                    q.setQuestionText(rs.getString("question_text"));
                    q.setQuestionType(rs.getString("question_type"));
                    q.setOptionA(rs.getString("option_a"));
                    q.setOptionB(rs.getString("option_b"));
                    q.setOptionC(rs.getString("option_c"));
                    q.setOptionD(rs.getString("option_d"));
                    q.setCorrectAnswer(rs.getString("correct_answer"));
                    q.setExplanation(rs.getString("explanation"));
                    Timestamp c = rs.getTimestamp("created_at");
                    if (c != null) q.setCreatedAt(c.toLocalDateTime());
                    list.add(q);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find quiz questions: " + e.getMessage(), e);
        }
        return list;
    }

    public Question saveQuestion(Question q) throws DatabaseException {
        String sql = "INSERT INTO quiz_questions " +
                     "(topic_id, question_text, question_type, option_a, option_b, option_c, option_d, correct_answer, explanation) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id, created_at";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, q.getTopicId());
            stmt.setString(2, q.getQuestionText());
            stmt.setString(3, q.getQuestionType());
            stmt.setString(4, q.getOptionA());
            stmt.setString(5, q.getOptionB());
            stmt.setString(6, q.getOptionC());
            stmt.setString(7, q.getOptionD());
            stmt.setString(8, q.getCorrectAnswer());
            stmt.setString(9, q.getExplanation());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    q.setId(rs.getInt("id"));
                    q.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                }
            }
            return q;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save quiz question: " + e.getMessage(), e);
        }
    }

    public QuizAttempt saveQuizAttempt(QuizAttempt attempt) throws DatabaseException {
        String sql = "INSERT INTO quiz_attempts (user_id, topic_id, score, total_questions, correct_count) " +
                     "VALUES (?, ?, ?, ?, ?) RETURNING id, attempted_at";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, attempt.getUserId());
            stmt.setInt(2, attempt.getTopicId());
            stmt.setDouble(3, attempt.getScore());
            stmt.setInt(4, attempt.getTotalQuestions());
            stmt.setInt(5, attempt.getCorrectCount());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    attempt.setId(rs.getInt("id"));
                    attempt.setAttemptedAt(rs.getTimestamp("attempted_at").toLocalDateTime());
                }
            }
            return attempt;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save quiz attempt: " + e.getMessage(), e);
        }
    }

    public List<QuizAttempt> findAttemptsByUserId(int userId) throws DatabaseException {
        String sql = "SELECT qa.*, t.name AS topic_name FROM quiz_attempts qa " +
                     "INNER JOIN topics t ON qa.topic_id = t.id " +
                     "WHERE qa.user_id = ? ORDER BY qa.attempted_at DESC";
        List<QuizAttempt> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    QuizAttempt a = new QuizAttempt();
                    a.setId(rs.getInt("id"));
                    a.setUserId(rs.getInt("user_id"));
                    a.setTopicId(rs.getInt("topic_id"));
                    a.setScore(rs.getDouble("score"));
                    a.setTotalQuestions(rs.getInt("total_questions"));
                    a.setCorrectCount(rs.getInt("correct_count"));
                    Timestamp at = rs.getTimestamp("attempted_at");
                    if (at != null) a.setAttemptedAt(at.toLocalDateTime());
                    a.setTopicName(rs.getString("topic_name"));
                    list.add(a);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to load quiz attempts: " + e.getMessage(), e);
        }
        return list;
    }
}
