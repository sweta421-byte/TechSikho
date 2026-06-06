package com.techsikho.services;

import com.techsikho.utils.DBConnection;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class StreakService {

    // Streak update karo jab user login kare
    public static int updateStreak(int userId) {
        String getSQL = "SELECT streak_count, last_login FROM users WHERE user_id = ?";
        String updateSQL = "UPDATE users SET streak_count = ?, last_login = NOW() WHERE user_id = ?";

        try {
            Connection conn = DBConnection.getConnection();

            // Pehle last login aur streak lo
            PreparedStatement getPS = conn.prepareStatement(getSQL);
            getPS.setInt(1, userId);
            ResultSet rs = getPS.executeQuery();

            if (rs.next()) {
                int currentStreak = rs.getInt("streak_count");
                Date lastLogin = rs.getDate("last_login");

                int newStreak = currentStreak;

                if (lastLogin == null) {
                    // Pehli baar login
                    newStreak = 1;
                } else {
                    LocalDate lastDate = lastLogin.toLocalDate();
                    LocalDate today = LocalDate.now();
                    long daysDiff = ChronoUnit.DAYS.between(lastDate, today);

                    if (daysDiff == 1) {
                        // Agle din login — streak badho!
                        newStreak = currentStreak + 1;
                        System.out.println("🔥 Streak badhi! " + newStreak + " days!");
                    } else if (daysDiff == 0) {
                        // Aaj pehle hi login kiya — streak same rahe
                        newStreak = currentStreak;
                    } else {
                        // Streak tooti!
                        newStreak = 1;
                        System.out.println("Streak reset! Kal zaroor aana!");
                    }
                }

                // Update karo
                PreparedStatement updatePS = conn.prepareStatement(updateSQL);
                updatePS.setInt(1, newStreak);
                updatePS.setInt(2, userId);
                updatePS.executeUpdate();

                return newStreak;
            }
        } catch (SQLException e) {
            System.out.println("Streak Error: " + e.getMessage());
        }
        return 0;
    }

    // Streak bonus XP calculate karo
    public static int streakBonusXP(int streakCount) {
        if (streakCount >= 30) return 50;
        else if (streakCount >= 14) return 30;
        else if (streakCount >= 7)  return 20;
        else if (streakCount >= 3)  return 10;
        else return 0;
    }

    // Streak milestone check karo
    public static String getStreakMilestone(int streakCount) {
        if (streakCount >= 30) return "🏆 30 Day Legend!";
        else if (streakCount >= 14) return "🥇 2 Week Warrior!";
        else if (streakCount >= 7)  return "🔥 Week Streaker!";
        else if (streakCount >= 3)  return "⚡ 3 Day Streak!";
        else return "";
    }
}