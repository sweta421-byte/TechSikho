package com.techsikho.dao;

import com.techsikho.models.User;
import com.techsikho.utils.DBConnection;

import java.sql.*;
import java.security.MessageDigest;

public class UserDAO {

    // Password ko MD5 hash karta hai
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return password;
        }
    }

    // Register new user
    public static boolean registerUser(User user) {
        String sql = "INSERT INTO users (username, email, password_hash, full_name, role) VALUES (?, ?, ?, ?, ?)";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, hashPassword(user.getPasswordHash()));
            ps.setString(4, user.getFullName());
            ps.setString(5, "student");
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Register Error: " + e.getMessage());
            return false;
        }
    }

    // Login user
    public static User loginUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password_hash = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, hashPassword(password));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setFullName(rs.getString("full_name"));
                user.setRole(rs.getString("role"));
                user.setTotalXp(rs.getInt("total_xp"));
                user.setCurrentLevel(rs.getInt("current_level"));
                user.setStreakCount(rs.getInt("streak_count"));
                return user;
            }
        } catch (SQLException e) {
            System.out.println("Login Error: " + e.getMessage());
        }
        return null;
    }

    // Username already exists check
    public static boolean usernameExists(String username) {
        String sql = "SELECT user_id FROM users WHERE username = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }

    // XP update karo
    public static boolean updateXP(int userId, int xp) {
        String sql = "UPDATE users SET total_xp = total_xp + ? WHERE user_id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, xp);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("XP Update Error: " + e.getMessage());
            return false;
        }
    }

    // Check and give daily bonus
    public static boolean checkAndUpdateDailyBonus(int userId) {
        String sql = "SELECT DATE(last_login) FROM users WHERE user_id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                java.sql.Date lastLoginDate = rs.getDate(1);
                java.sql.Date todayDate = new java.sql.Date(System.currentTimeMillis());
                if (lastLoginDate == null || !lastLoginDate.equals(todayDate)) {
                    String updateSql = "UPDATE users SET total_xp = total_xp + 5, last_login = NOW() WHERE user_id = ?";
                    PreparedStatement updatePs = conn.prepareStatement(updateSql);
                    updatePs.setInt(1, userId);
                    boolean result = updatePs.executeUpdate() > 0;
                    updatePs.close();
                    ps.close();
                    return result;
                }
            }
            ps.close();
        } catch (SQLException e) {
            System.out.println("Daily Bonus Error: " + e.getMessage());
        }
        return false;
    }

    public static boolean verifyPassword(int userId, String password) {
        String sql = "SELECT user_id FROM users WHERE user_id = ? AND password_hash = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, hashPassword(password));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Verify Password Error: " + e.getMessage());
            return false;
        }
    }

    public static boolean updatePassword(int userId, String newPasswordHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPasswordHash);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Update Password Error: " + e.getMessage());
            return false;
        }
    }

    public static boolean updateFullName(int userId, String fullName) {
        String sql = "UPDATE users SET full_name = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fullName);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Update Full Name Error: " + e.getMessage());
            return false;
        }
    }

    public static boolean resetPasswordByEmail(String email) {
        String tempPassword = "temp1234";
        String sql = "UPDATE users SET password_hash = ? WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hashPassword(tempPassword));
            ps.setString(2, email);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Reset Password Error: " + e.getMessage());
            return false;
        }
    }
}
