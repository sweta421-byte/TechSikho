package com.techsikho.services;

import com.techsikho.dao.UserDAO;
import com.techsikho.models.User;

public class AuthService {

    // Register karo naya user
    public static boolean register(String username, String email, 
                                   String password, String fullName) {
        // Validation
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Error: Username empty hai!");
            return false;
        }
        if (password == null || password.length() < 6) {
            System.out.println("Error: Password kam se kam 6 characters ka hona chahiye!");
            return false;
        }
        if (!email.contains("@")) {
            System.out.println("Error: Email valid nahi hai!");
            return false;
        }

        // Username already exists?
        if (UserDAO.usernameExists(username)) {
            System.out.println("Error: Username already le liya gaya hai!");
            return false;
        }

        // User object banao aur save karo
        User user = new User(username, email, password, fullName);
        boolean success = UserDAO.registerUser(user);

        if (success) {
            System.out.println("Registration successful: " + username);
        }
        return success;
    }

    // Login karo
    public static User login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Error: Username daalo!");
            return null;
        }
        if (password == null || password.trim().isEmpty()) {
            System.out.println("Error: Password daalo!");
            return null;
        }

        User user = UserDAO.loginUser(username, password);

        if (user != null) {
            System.out.println("Login successful: " + user.getUsername() + 
                             " | Role: " + user.getRole());
        } else {
            System.out.println("Login failed: Wrong username or password!");
        }
        return user;
    }

    // Admin hai ya nahi check karo
    public static boolean isAdmin(User user) {
        return user != null && user.isAdmin();
    }
}