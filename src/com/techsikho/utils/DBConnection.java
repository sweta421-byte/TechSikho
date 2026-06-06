package com.techsikho.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL      = "jdbc:mysql://localhost:3306/techsikho_db";
    private static final String USER     = "root";
    private static final String PASSWORD = "root1421"; // tumhara password

    private static Connection conn = null;

    public static Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ DB Connected!");
            }
        } catch (Exception e) {
            System.err.println("❌ DB Error: " + e.getMessage());
        }
        return conn;
    }

    public static void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("✅ DB Connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Close Error: " + e.getMessage());
        }
    }
}

