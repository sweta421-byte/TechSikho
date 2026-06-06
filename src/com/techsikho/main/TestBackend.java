package com.techsikho.main;

import com.techsikho.services.AuthService;
import com.techsikho.services.XPService;
import com.techsikho.services.StreakService;
import com.techsikho.dao.LeaderboardDAO;
import com.techsikho.models.User;
import java.util.List;

public class TestBackend {
    public static void main(String[] args) {
        
        System.out.println("=============================");
        System.out.println("   TechSikho Backend Test   ");
        System.out.println("=============================\n");

        // TEST 1 - Register
        System.out.println("TEST 1: Register User");
        boolean registered = AuthService.register(
            "rahul123", "rahul@email.com", "pass123", "Rahul Sharma"
        );
        System.out.println("Result: " + (registered ? "SUCCESS" : "FAILED"));

        // TEST 2 - Login
        System.out.println("\nTEST 2: Login User");
        User user = AuthService.login("rahul123", "pass123");
        System.out.println("Result: " + (user != null ? "SUCCESS | User: " + user.getUsername() : "FAILED"));

        if (user != null) {
            // TEST 3 - XP
            System.out.println("\nTEST 3: XP System");
            int newXP = XPService.addXP(user.getUserId(), user.getTotalXp(), 150);
            System.out.println("New XP: " + newXP);
            System.out.println("Level: " + XPService.calculateLevel(newXP));
            System.out.println("XP for next level: " + XPService.xpForNextLevel(newXP));

            // TEST 4 - Quiz XP
            System.out.println("\nTEST 4: Quiz XP Calculation");
            int quizXP = XPService.calculateQuizXP(8, 10, "medium");
            System.out.println("Quiz XP (8/10 medium): " + quizXP);

            // TEST 5 - Streak
            System.out.println("\nTEST 5: Streak Update");
            int streak = StreakService.updateStreak(user.getUserId());
            System.out.println("Current Streak: " + streak + " days");
            System.out.println("Streak Bonus XP: " + StreakService.streakBonusXP(streak));
            System.out.println("Milestone: " + StreakService.getStreakMilestone(streak));

            // TEST 6 - Leaderboard
            System.out.println("\nTEST 6: Leaderboard");
            List<String[]> top = LeaderboardDAO.getTopUsers();
            System.out.println("Top Users:");
            for (String[] row : top) {
                System.out.println("Rank " + row[0] + " | " + row[1] + 
                                 " | XP: " + row[3] + " | Level: " + row[4]);
            }

            // TEST 7 - User Rank
            System.out.println("\nTEST 7: User Rank");
            int rank = LeaderboardDAO.getUserRank(user.getUserId());
            System.out.println(user.getUsername() + " ka rank: #" + rank);
        }

        System.out.println("\n=============================");
        System.out.println("     All Tests Complete!     ");
        System.out.println("=============================");
    }
}