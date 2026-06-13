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
                java.sql.Statement st = conn.createStatement();

                st.execute(
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

                st.execute(
                    "CREATE TABLE IF NOT EXISTS user_progress (" +
                    "progress_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER," +
                    "activity_type TEXT," +
                    "activity_name TEXT," +
                    "score INTEGER DEFAULT 0," +
                    "xp_earned INTEGER DEFAULT 0," +
                    "is_completed INTEGER DEFAULT 1," +
                    "completed_at TEXT DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY(user_id) REFERENCES users(user_id))"
                );

                st.execute(
                    "CREATE TABLE IF NOT EXISTS levels (" +
                    "level_id INTEGER PRIMARY KEY," +
                    "level_number INTEGER," +
                    "xp_required INTEGER," +
                    "title TEXT)"
                );
                st.execute("INSERT OR IGNORE INTO levels VALUES (1,1,0,'Beginner')");
                st.execute("INSERT OR IGNORE INTO levels VALUES (2,2,100,'Learner')");
                st.execute("INSERT OR IGNORE INTO levels VALUES (3,3,300,'Coder')");
                st.execute("INSERT OR IGNORE INTO levels VALUES (4,4,600,'Developer')");
                st.execute("INSERT OR IGNORE INTO levels VALUES (5,5,1000,'Expert')");

                st.execute(
                    "CREATE TABLE IF NOT EXISTS languages (" +
                    "lang_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT UNIQUE NOT NULL," +
                    "icon TEXT," +
                    "description TEXT)"
                );
                st.execute("INSERT OR IGNORE INTO languages(name,icon,description) VALUES ('Java','☕','Object-oriented programming')");
                st.execute("INSERT OR IGNORE INTO languages(name,icon,description) VALUES ('Python','🐍','Beginner friendly language')");
                st.execute("INSERT OR IGNORE INTO languages(name,icon,description) VALUES ('C++','⚙️','Systems programming')");
                st.execute("INSERT OR IGNORE INTO languages(name,icon,description) VALUES ('JavaScript','🌐','Web development')");

                st.execute(
                    "CREATE TABLE IF NOT EXISTS weekly_champions (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER," +
                    "week_start TEXT," +
                    "xp_earned INTEGER DEFAULT 0," +
                    "FOREIGN KEY(user_id) REFERENCES users(user_id))"
                );

                st.close();
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