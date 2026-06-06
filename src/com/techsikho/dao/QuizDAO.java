package com.techsikho.dao;

import com.techsikho.models.Question;
import com.techsikho.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizDAO {

    // Ek level ke saare questions lo
    public static List<Question> getQuestionsByLevel(int levelId) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE level_id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, levelId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Question q = new Question();
                q.setQuestionId(rs.getInt("question_id"));
                q.setLevelId(rs.getInt("level_id"));
                q.setQuestionText(rs.getString("question_text"));
                q.setOptionA(rs.getString("option_a"));
                q.setOptionB(rs.getString("option_b"));
                q.setOptionC(rs.getString("option_c"));
                q.setOptionD(rs.getString("option_d"));
                q.setCorrectAns(rs.getString("correct_ans"));
                q.setExplanation(rs.getString("explanation"));
                q.setDifficulty(rs.getString("difficulty"));
                questions.add(q);
            }
        } catch (SQLException e) {
            System.out.println("Quiz Fetch Error: " + e.getMessage());
        }
        return questions;
    }

    // Quiz attempt save karo
    public static boolean saveQuizAttempt(int userId, int levelId, int score, int totalQuestions) {
        String sql = "INSERT INTO quiz_attempts (user_id, level_id, score, total_questions, attempted_at) " +
                     "VALUES (?, ?, ?, ?, NOW())";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, levelId);
            ps.setInt(3, score);
            ps.setInt(4, totalQuestions);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Quiz Save Error: " + e.getMessage());
            return false;
        }
    }

    // User ka best score ek level mein
    public static int getBestScore(int userId, int levelId) {
        String sql = "SELECT MAX(score) FROM quiz_attempts WHERE user_id = ? AND level_id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, levelId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("Best Score Error: " + e.getMessage());
        }
        return 0;
    }

    // Total quiz attempts
    public static int getTotalAttempts(int userId) {
        String sql = "SELECT COUNT(*) FROM quiz_attempts WHERE user_id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("Attempts Error: " + e.getMessage());
        }
        return 0;
    }
}