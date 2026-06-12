package com.techsikho.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static Connection conn = null;

    public static Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                String userHome = System.getProperty("user.home");
                String dbPath = userHome + "/TechSikho/techsikho.db";
                new java.io.File(userHome + "/TechSikho").mkdirs();
                String url = "jdbc:sqlite:" + dbPath;
                Class.forName("org.sqlite.JDBC");
                conn = DriverManager.getConnection(url);
                conn.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS users (" +
                    "user_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE NOT NULL," +
                    "email TEXT UNIQUE NOT NULL," +
                    "password_hash TEXT NOT NULL," +
                    "full_name TEXT," +
                    "role TEXT DEFAULT 'student'," +
                    "total_xp INTEGER DEFAULT 0," +
                    "current_level INTEGER DEFAULT 1," +
                    "streak_count INTEGER DEFAULT 0," +
                    "last_login TEXT," +
                    "created_at TEXT DEFAULT CURRENT_TIMESTAMP)"
                );
                System.out.println("DB Connected!");
            }
        } catch (Exception e) {
            System.err.println("DB Error: " + e.getMessage());
        }
        return conn;
    }

    public static void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Close Error: " + e.getMessage());
        }
    }
}