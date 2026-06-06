package com.techsikho.services;

import com.techsikho.dao.UserDAO;

public class XPService {

    // Level up ke liye kitna XP chahiye
    private static final int[] LEVEL_XP = {
        0,      // Level 1
        100,    // Level 2
        250,    // Level 3
        500,    // Level 4
        900,    // Level 5
        1400,   // Level 6
        2000,   // Level 7
        2700,   // Level 8
        3500,   // Level 9
        4500    // Level 10
    };

    // XP add karo aur level check karo
    public static int addXP(int userId, int currentXP, int xpToAdd) {
        boolean success = UserDAO.updateXP(userId, xpToAdd);
        if (success) {
            int newXP = currentXP + xpToAdd;
            System.out.println("XP Added: +" + xpToAdd + " | Total XP: " + newXP);
            return newXP;
        }
        return currentXP;
    }

    // Current XP se level calculate karo
    public static int calculateLevel(int totalXP) {
        int level = 1;
        for (int i = LEVEL_XP.length - 1; i >= 0; i--) {
            if (totalXP >= LEVEL_XP[i]) {
                level = i + 1;
                break;
            }
        }
        return level;
    }

    // Next level ke liye kitna XP aur chahiye
    public static int xpForNextLevel(int totalXP) {
        int currentLevel = calculateLevel(totalXP);
        if (currentLevel >= LEVEL_XP.length) {
            return 0; // Max level
        }
        return LEVEL_XP[currentLevel] - totalXP;
    }

    // Quiz score se XP calculate karo
    public static int calculateQuizXP(int score, int totalQuestions, String difficulty) {
        double percentage = (double) score / totalQuestions * 100;
        int baseXP = 0;

        switch (difficulty.toLowerCase()) {
            case "easy":   baseXP = 20; break;
            case "medium": baseXP = 40; break;
            case "hard":   baseXP = 70; break;
            default:       baseXP = 20;
        }

        if (percentage == 100) return baseXP * 2;      // Perfect score bonus
        else if (percentage >= 80) return baseXP;       // Good score
        else if (percentage >= 60) return baseXP / 2;  // Average
        else return baseXP / 4;                         // Low score
    }

    // Level up hua ya nahi check karo
    public static boolean didLevelUp(int oldXP, int newXP) {
        return calculateLevel(newXP) > calculateLevel(oldXP);
    }
}