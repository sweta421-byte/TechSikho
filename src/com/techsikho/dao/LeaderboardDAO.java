package com.techsikho.dao;

import com.techsikho.services.XPService;
import com.techsikho.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardDAO {

    public static List<String[]> getTopUsers() {
        List<String[]> topUsers = new ArrayList<>();
        String sql = "SELECT rnk, username, full_name, total_xp, streak_count " +
                     "FROM (" +
                     "  SELECT user_id, username, full_name, total_xp, streak_count, " +
                     "         RANK() OVER (ORDER BY total_xp DESC) AS rnk " +
                     "  FROM users " +
                     ") ranked_users " +
                     "ORDER BY rnk ASC, username ASC " +
                     "LIMIT 10";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String rank = String.valueOf(rs.getInt("rnk"));
                String username = rs.getString("username");
                String fullName = rs.getString("full_name");
                int xp = rs.getInt("total_xp");
                String level = String.valueOf(XPService.calculateLevel(xp));
                String streak = String.valueOf(rs.getInt("streak_count"));
                topUsers.add(new String[]{rank, username, fullName, String.valueOf(xp), level, streak});
            }
        } catch (SQLException e) {
            System.err.println("Leaderboard retrieval error: " + e.getMessage());
        }
        return topUsers;
    }

    public static int getUserRank(int userId) {
        String sql = "SELECT rnk FROM (" +
                     "  SELECT user_id, RANK() OVER (ORDER BY total_xp DESC) AS rnk " +
                     "  FROM users " +
                     ") ranked_users " +
                     "WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("rnk");
                }
            }
        } catch (SQLException e) {
            System.err.println("Rank retrieval error: " + e.getMessage());
        }
        return 0;
    }
}
