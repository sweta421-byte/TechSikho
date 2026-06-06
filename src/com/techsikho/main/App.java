package com.techsikho.main;

import com.techsikho.utils.DBConnection;
import java.sql.Connection;

public class App {
    public static void main(String[] args) {
        System.out.println("🚀 TechSikho Starting...");
        
        Connection conn = DBConnection.getConnection();
        
        if (conn != null) {
            System.out.println("🎉 Database Connection: SUCCESS!");
            System.out.println("✅ TechSikho is Ready to Build!");
        } else {
            System.out.println("❌ Database Connection: FAILED!");
        }
        
        DBConnection.closeConnection();
    }
}