package com.techsikho.models;

import java.util.Date;

public class User {
    
    private int userId;
    private String username;
    private String email;
    private String passwordHash;
    private String fullName;
    private int avatarId;
    private String role;
    private int totalXp;
    private int currentLevel;
    private int streakCount;
    private Date lastLogin;
    private Date createdAt;

    // Constructor - Empty
    public User() {}

    // Constructor - Registration ke liye
    public User(String username, String email, String passwordHash, String fullName) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.role = "student";
        this.totalXp = 0;
        this.currentLevel = 1;
        this.streakCount = 0;
        this.avatarId = 1;
    }

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public int getAvatarId() { return avatarId; }
    public void setAvatarId(int avatarId) { this.avatarId = avatarId; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public int getTotalXp() { return totalXp; }
    public void setTotalXp(int totalXp) { this.totalXp = totalXp; }

    public int getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(int currentLevel) { this.currentLevel = currentLevel; }

    public int getStreakCount() { return streakCount; }
    public void setStreakCount(int streakCount) { this.streakCount = streakCount; }

    public Date getLastLogin() { return lastLogin; }
    public void setLastLogin(Date lastLogin) { this.lastLogin = lastLogin; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public boolean isAdmin() { return "admin".equals(this.role); }

    @Override
    public String toString() {
        return "User{id=" + userId + ", username=" + username + 
               ", level=" + currentLevel + ", xp=" + totalXp + "}";
    }
}