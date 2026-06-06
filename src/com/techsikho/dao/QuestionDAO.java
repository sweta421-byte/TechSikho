package com.techsikho.dao;

import com.techsikho.models.Question;
import com.techsikho.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAO {

    public static List<Question> getQuestionsByLang(int langId) {
        List<Question> questions = new ArrayList<>();
        String sql = """
                SELECT q.* FROM questions q
                JOIN levels l ON q.level_id = l.level_id
                WHERE l.lang_id = ?
                ORDER BY RAND()
                LIMIT 10
                """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, langId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                questions.add(mapQuestion(rs));
            }
        } catch (SQLException e) {
            System.err.println("QuestionDAO error: " + e.getMessage());
        }
        return questions;
    }

    public static List<Question> getQuestionsByLevel(int levelId) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE level_id = ? ORDER BY RAND()";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, levelId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                questions.add(mapQuestion(rs));
            }
        } catch (SQLException e) {
            System.err.println("QuestionDAO error: " + e.getMessage());
        }
        return questions;
    }

    private static Question mapQuestion(ResultSet rs) throws SQLException {
        return new Question(
            rs.getInt("question_id"),
            rs.getInt("level_id"),
            rs.getString("question_text"),
            rs.getString("option_a"),
            rs.getString("option_b"),
            rs.getString("option_c"),
            rs.getString("option_d"),
            rs.getString("correct_ans"),
            rs.getString("explanation"),
            rs.getString("difficulty")
        );
    }
}