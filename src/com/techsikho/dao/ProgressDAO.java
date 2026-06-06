package com.techsikho.dao;

import com.techsikho.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProgressDAO {

    public static void saveProgress(int userId, int langId, int score, int xpEarned) {
        String sql = "INSERT INTO user_progress (user_id, level_id, is_completed, xp_earned, completion_date) " +
                     "SELECT ?, l.level_id, 1, ?, NOW() FROM levels l WHERE l.lang_id = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, xpEarned);
            ps.setInt(3, langId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Save progress error: " + e.getMessage());
        }
    }

    public static int getCompletedLevels(int userId) {
        return 0;
    }

    public static boolean hasCompleted(int userId, int levelId) {
        return false;
    }

    public static int getBestScore(int userId, int levelId) {
        return 0;
    }

    public static List<String[]> exportProgressData(int userId) {
        List<String[]> rows = new ArrayList<>();
        String sql = "SELECT up.completion_date, l.lang_name, up.xp_earned " +
                     "FROM user_progress up " +
                     "JOIN levels lev ON up.level_id = lev.level_id " +
                     "JOIN languages l ON lev.lang_id = l.lang_id " +
                     "WHERE up.user_id = ? AND up.is_completed = 1 " +
                     "ORDER BY up.completion_date ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String completionDate = rs.getString("completion_date");
                    String language = rs.getString("lang_name");
                    String xp = String.valueOf(rs.getInt("xp_earned"));
                    rows.add(new String[]{completionDate, language, xp});
                }
            }
        } catch (SQLException e) {
            System.err.println("Export progress data error: " + e.getMessage());
        }
        return rows;
    }
}
