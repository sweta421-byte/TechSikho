package com.techsikho.ui;

import com.techsikho.models.User;
import com.techsikho.models.Question;
import com.techsikho.dao.LeaderboardDAO;
import com.techsikho.dao.QuestionDAO;
import com.techsikho.dao.UserDAO;
import com.techsikho.dao.ProgressDAO;
import com.techsikho.services.XPService;
import com.techsikho.services.StreakService;
import com.techsikho.utils.DBConnection;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DashboardFrame extends JFrame {

    private User currentUser;
    private JPanel contentPanel;
    private JPanel sidebarPanel;
    private JPanel navButtonsPanel;
    private JPanel logoPanelRef;
    private JScrollPane sidebarScrollPane;
    private boolean isDarkMode = true;
    private static Set<String> purchasedItems = new HashSet<>();
    private static boolean streakFreezeActive = false;
    private static boolean doubleXPActive = false;
    private static int doubleXPQuizzesLeft = 0;
    private static int hintsRemaining = 0;
    
    private static final String[] GLOSSARY_TERMS = {
        "Java - Object-oriented programming language by Oracle",
        "Python - High-level interpreted programming language",
        "HTML - HyperText Markup Language for web pages",
        "CSS - Cascading Style Sheets: styles web pages",
        "Algorithm - Step by step procedure to solve a problem",
        "Debugging - Process of finding and fixing errors in code",
        "Framework - Pre-built code structure to build applications",
        "Library - Collection of reusable code functions",
        "Compiler - Converts source code to machine code",
        "Interpreter - Executes code line by line without compiling",
        "Variable - Named container for storing data",
        "Function - Reusable block of code that performs a task",
        "Array - Collection of elements of same type",
        "Loop - Repeats code block multiple times",
        "Recursion - Function that calls itself",
        "Polymorphism - One interface multiple implementations",
        "Encapsulation - Binding data and methods in a class",
        "Inheritance - Child class gets properties from parent",
        "Interface - Contract defining methods a class must implement",
        "Exception - Error that occurs during program execution",
        "Thread - Lightweight process for concurrent execution"
    };

    private static final int[] WHEEL_XP = {10, 25, 50, 75, 100, 150};
    private static final Color[] WHEEL_COLORS = {
        new Color(239, 68, 68),
        new Color(245, 158, 11),
        new Color(234, 179, 8),
        new Color(16, 185, 129),
        new Color(0x0ea5e9),
        new Color(0x2dd4bf)
    };

    public DashboardFrame(User user) {
        this.currentUser = user;
        setTitle("TechSikho Dashboard");
        setSize(1280, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1100, 650));
        setResizable(true);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(0x0d1520));
        sidebarPanel = createSidebar();
        add(sidebarPanel, BorderLayout.WEST);
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(0x0d1520));
        add(contentPanel, BorderLayout.CENTER);
        applyTheme(isDarkMode);
        showDashboard();
        showDailyReward();
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebarPanel = sidebar;
        sidebar.setBackground(new Color(0x0d1117));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(16, 12, 16, 12));

        // Logo panel
        JPanel logoPanel = new JPanel();
        logoPanelRef = logoPanel;
        logoPanel.setBackground(new Color(0x0d1117));
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));

        JButton themeToggle = new JButton("Light Mode"); themeToggle.setVisible(false);
        themeToggle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        themeToggle.setBackground(new Color(0x0ea5e9));
        themeToggle.setForeground(Color.WHITE);
        themeToggle.setFocusPainted(false);
        themeToggle.setBorderPainted(false);
        themeToggle.setAlignmentX(Component.CENTER_ALIGNMENT);
        themeToggle.setMaximumSize(new Dimension(170, 28));
        themeToggle.addActionListener(e -> {
            isDarkMode = !isDarkMode;
            themeToggle.setText(isDarkMode ? "Light Mode" : "Dark Mode");
            applyTheme(isDarkMode);
            showDashboard();
        });
        logoPanel.add(themeToggle);
        logoPanel.add(Box.createVerticalStrut(8));

        JPanel logo = new JPanel() {
            protected void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);
                java.awt.Graphics2D g2 = (java.awt.Graphics2D)g;
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                int cx=getWidth()/2, cy=0;
                // crown
                int[]crx={cx-12,cx-8,cx-4,cx,cx+4,cx+8,cx+12};
                int[]cry={cy+10,cy+4,cy+8,cy+2,cy+8,cy+4,cy+10};
                g2.setColor(new Color(0x0ea5e9));g2.fillPolygon(crx,cry,7);
                g2.setColor(new Color(0x2dd4bf));g2.setStroke(new java.awt.BasicStroke(0.8f));g2.drawPolygon(crx,cry,7);
                // head
                g2.setColor(new Color(0x0d1520));g2.fillRoundRect(cx-18,cy+10,36,26,8,8);
                g2.setColor(new Color(0x0ea5e9));g2.setStroke(new java.awt.BasicStroke(1.5f));g2.drawRoundRect(cx-18,cy+10,36,26,8,8);
                // eyes glow
                g2.setColor(new Color(0x2dd4bf));g2.fillRoundRect(cx-13,cy+16,10,7,3,3);g2.fillRoundRect(cx+3,cy+16,10,7,3,3);
                g2.setColor(new Color(0xffffff));g2.fillOval(cx-10,cy+18,4,3);g2.fillOval(cx+6,cy+18,4,3);
                // mouth
                g2.setColor(new Color(0x0ea5e9));g2.fillRoundRect(cx-8,cy+27,16,4,3,3);
                g2.setColor(new Color(0x2dd4bf));g2.fillRect(cx-5,cy+28,3,2);g2.fillRect(cx,cy+28,3,2);
                // neck
                g2.setColor(new Color(0x0d1520));g2.fillRect(cx-5,cy+36,10,6);
                g2.setColor(new Color(0x0ea5e9));g2.setStroke(new java.awt.BasicStroke(0.8f));g2.drawRect(cx-5,cy+36,10,6);
                // body
                g2.setColor(new Color(0x071520));g2.fillRoundRect(cx-22,cy+42,44,30,6,6);
                g2.setColor(new Color(0x0ea5e9));g2.setStroke(new java.awt.BasicStroke(1.2f));g2.drawRoundRect(cx-22,cy+42,44,30,6,6);
                // chest screen
                g2.setColor(new Color(0x040c14));g2.fillRoundRect(cx-14,cy+46,28,18,4,4);
                g2.setColor(new Color(0x2dd4bf));g2.setStroke(new java.awt.BasicStroke(0.8f));g2.drawRoundRect(cx-14,cy+46,28,18,4,4);
                // TS on chest
                g2.setFont(new Font("Segoe UI",Font.BOLD,11));
                g2.setColor(new Color(0x0ea5e9));g2.drawString("T",cx-10,cy+59);
                g2.setColor(new Color(0x2dd4bf));g2.drawString("S",cx,cy+59);
                // arms
                g2.setColor(new Color(0x0d1520));g2.fillRoundRect(cx-36,cy+42,13,22,4,4);g2.fillRoundRect(cx+23,cy+42,13,22,4,4);
                g2.setColor(new Color(0x0ea5e9));g2.setStroke(new java.awt.BasicStroke(0.8f));g2.drawRoundRect(cx-36,cy+42,13,22,4,4);g2.drawRoundRect(cx+23,cy+42,13,22,4,4);
                // hands - both symmetric
                g2.setColor(new Color(0x050d0d));
                g2.fillRoundRect(cx-36,cy+64,13,10,2,2);
                g2.fillRoundRect(cx+23,cy+64,13,10,2,2);
                g2.setColor(new Color(0x0ea5e9));g2.setStroke(new java.awt.BasicStroke(0.7f));
                g2.drawRoundRect(cx-36,cy+64,13,10,2,2);
                g2.drawRoundRect(cx+23,cy+64,13,10,2,2);
                g2.drawRoundRect(cx+23,cy+64,13,10,2,2);
                // legs
                g2.setColor(new Color(0x0d1520));g2.fillRoundRect(cx-16,cy+74,12,14,4,4);g2.fillRoundRect(cx+4,cy+74,12,14,4,4);
                g2.setColor(new Color(0x0ea5e9));g2.setStroke(new java.awt.BasicStroke(0.8f));g2.drawRoundRect(cx-16,cy+74,12,14,4,4);g2.drawRoundRect(cx+4,cy+74,12,14,4,4);
                // amber dots
                g2.setColor(new Color(0xf59e0b));g2.fillOval(cx-30,cy+5,5,5);g2.fillOval(cx+25,cy+5,5,5);
                g2.setColor(new Color(0x2dd4bf));g2.fillOval(cx-24,cy+2,3,3);g2.fillOval(cx+21,cy+2,3,3);
                // TECHSIKHO text
                g2.setFont(new Font("Segoe UI",Font.BOLD,11));
                g2.setColor(new Color(0x0ea5e9));
                java.awt.FontMetrics fm=g2.getFontMetrics();
                int tw=fm.stringWidth("TECHSIKHO");
                g2.drawString("TECH",cx-tw/2,cy+103);
                g2.setColor(new Color(0x2dd4bf));g2.drawString("SIKHO",cx-tw/2+fm.stringWidth("TECH"),cy+103);
            }
            public java.awt.Dimension getPreferredSize(){return new java.awt.Dimension(120,130);}
            public java.awt.Dimension getMinimumSize(){return new java.awt.Dimension(120,130);}
        };
        logo.setBackground(new java.awt.Color(0x0d1117));logo.setOpaque(true);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoPanel.add(logo);
        logoPanel.add(Box.createVerticalStrut(12));
        sidebar.add(logoPanel, BorderLayout.NORTH);

        JPanel navButtons = new JPanel();
        navButtonsPanel = navButtons;
        navButtons.setBackground(new Color(0x0d1117));
        navButtons.setLayout(new BoxLayout(navButtons, BoxLayout.Y_AXIS));
        navButtons.add(createNavBtn("Dashboard", () -> showDashboard()));
        navButtons.add(Box.createVerticalStrut(6));

        navButtons.add(createSectionLabel("LEARN"));
        navButtons.add(createNavBtn("Languages", () -> showLanguages()));
        navButtons.add(Box.createVerticalStrut(4));
        navButtons.add(createNavBtn("Lessons", () -> showLessons()));
        navButtons.add(Box.createVerticalStrut(4));
        navButtons.add(createNavBtn("Learning Path", () -> showLearningPath()));
        navButtons.add(Box.createVerticalStrut(4));
        navButtons.add(createNavBtn("Flash Cards", () -> showFlashCards()));
        navButtons.add(Box.createVerticalStrut(6));

        navButtons.add(createSectionLabel("PLAY"));
        navButtons.add(createNavBtn("Boss Battle", () -> showBossBattle()));
        navButtons.add(Box.createVerticalStrut(4));
        navButtons.add(createNavBtn("Mini Games", () -> showMiniGames()));
        navButtons.add(createNavBtn("Escape Hacker", () -> showEscapeHacker()));
        navButtons.add(createNavBtn("Guess Output", () -> showGuessOutput()));
        navButtons.add(createNavBtn("AI Prompt", () -> showAIPrompt()));
        navButtons.add(Box.createVerticalStrut(4));
        navButtons.add(createNavBtn("Typing Test", () -> showTypingTest()));
        navButtons.add(Box.createVerticalStrut(4));
        navButtons.add(createNavBtn("Glossary", () -> showGlossary()));
        navButtons.add(Box.createVerticalStrut(4));
        navButtons.add(createNavBtn("Code Breaker", () -> showCodeBreaker()));




        navButtons.add(Box.createVerticalStrut(4));
        navButtons.add(createNavBtn("Binary Blitz", () -> showBinaryBlitz()));
        navButtons.add(Box.createVerticalStrut(4));
        navButtons.add(createNavBtn("Debug Dhamaal", () -> showDebugDhamaal()));
        navButtons.add(Box.createVerticalStrut(4));
        navButtons.add(createNavBtn("Stack Overflow", () -> showStackOverflow()));
        navButtons.add(Box.createVerticalStrut(4));
        navButtons.add(createNavBtn("Deploy or Die", () -> showDeployOrDie()));



        navButtons.add(createSectionLabel("TRACK"));
        navButtons.add(createNavBtn("Progress", () -> showProgress()));
        navButtons.add(Box.createVerticalStrut(4));
        navButtons.add(createNavBtn("Leaderboard", () -> showLeaderboard()));
        navButtons.add(Box.createVerticalStrut(4));
        navButtons.add(createNavBtn("Analytics", () -> showAnalytics()));
        navButtons.add(Box.createVerticalStrut(4));
        navButtons.add(createNavBtn("Achievements", () -> showAchievements()));
        navButtons.add(Box.createVerticalStrut(6));

        navButtons.add(createSectionLabel("ME"));
        navButtons.add(createNavBtn("Profile", () -> showProfile()));
        navButtons.add(Box.createVerticalStrut(4));
        navButtons.add(createNavBtn("XP Shop", () -> showXPShop()));
        navButtons.add(Box.createVerticalStrut(4));
        navButtons.add(createNavBtn("Certificate", () -> showCertificate()));
        navButtons.add(Box.createVerticalStrut(4));
        navButtons.add(createNavBtn("XP History", () -> showXPHistory()));
        navButtons.add(Box.createVerticalStrut(4));

        navButtons.add(Box.createVerticalStrut(4));

        navButtons.add(Box.createVerticalStrut(4));
        navButtons.add(createNavBtn("Settings", () -> showSettings()));
        navButtons.add(Box.createVerticalStrut(4));


        if (currentUser.isAdmin()) {
            navButtons.add(Box.createVerticalStrut(6));
            navButtons.add(createSectionLabel("ADMIN"));
            navButtons.add(createNavBtn("Admin", () -> showAdmin()));
        }

        JScrollPane scrollPane = new JScrollPane(navButtons);
        sidebarScrollPane = scrollPane;
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(new Color(0x0d1117));
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(0x0d1117));
        sidebar.add(scrollPane, BorderLayout.CENTER);

        // Logout
        JButton logoutBtn = createNavBtn("Logout", () -> {
            int c = JOptionPane.showConfirmDialog(this,
                "Do you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });
        JPanel logoutPanel = new JPanel();
        logoutPanelRef = logoutPanel;
        logoutPanel.setBackground(new Color(0x0d1117));
        logoutPanel.setLayout(new BoxLayout(logoutPanel, BoxLayout.Y_AXIS));
        logoutPanel.add(Box.createVerticalStrut(8));
        logoutPanel.add(logoutBtn);
        sidebar.add(logoutPanel, BorderLayout.SOUTH);

        return sidebar;
    }

    private void playSound(String type) {
        new Thread(() -> {
            try {
                java.awt.Toolkit tk = java.awt.Toolkit.getDefaultToolkit();
                if (type.equals("correct")) {
                    tk.beep();
                    Thread.sleep(100);
                    tk.beep();
                } else if (type.equals("wrong")) {
                    tk.beep();
                } else if (type.equals("levelup")) {
                    tk.beep(); Thread.sleep(150);
                    tk.beep(); Thread.sleep(150);
                    tk.beep();
                } else {
                    tk.beep();
                }
            } catch (Exception ignored) {}
        }).start();
    }

    private static boolean dailyRewardShown = false;
    private void showDailyReward() { System.out.println("showDailyReward called, shown=" + dailyRewardShown);
        if (dailyRewardShown) return;
        dailyRewardShown = true;
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT last_login, streak_count FROM users WHERE user_id=?");
            ps.setInt(1, currentUser.getUserId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                java.sql.Date lastLogin = rs.getDate("last_login");
                java.sql.Date today = java.sql.Date.valueOf(java.time.LocalDate.now());
                System.out.println("lastLogin=" + lastLogin + " today=" + today);
                if (lastLogin != null && lastLogin.compareTo(today) >= 0) return;
                int streak = rs.getInt("streak_count");
                int day = (streak % 7) + 1;
                int[] rewards = {10,15,20,25,30,40,100};
                int xp = rewards[day-1];
                JDialog dialog = new JDialog(this, true);
                dialog.setUndecorated(true);
                dialog.setSize(440, 360);
                dialog.setLocationRelativeTo(this);
                JPanel panel = new JPanel();
                panel.setBackground(new Color(0x0d1520));
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                panel.setBorder(BorderFactory.createEmptyBorder(25,25,25,25));
                JLabel titleLbl = new JLabel("Daily Login Reward!");
                titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 24));
                titleLbl.setForeground(new Color(255,215,0));
                titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
                JLabel subLbl = new JLabel("Come back every day for bigger rewards!");
                subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                subLbl.setForeground(Color.WHITE);
                subLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
                JPanel dayGrid = new JPanel(new GridLayout(1,7,4,0));
                dayGrid.setBackground(new Color(0x0d1520));
                String[] dayXp = {"10","15","20","25","30","40","100"};
                for (int i=1; i<=7; i++) {
                    JPanel card = new JPanel();
                    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
                    if (i == day) card.setBackground(new Color(60,50,0));
                    else if (i < day) card.setBackground(new Color(0x0d1520));
                    else card.setBackground(new Color(0x0d1520));
                    card.setBorder(BorderFactory.createEmptyBorder(8,4,8,4));
                    JLabel dayL = new JLabel("Day "+i);
                    dayL.setFont(new Font("Segoe UI", Font.BOLD, 10));
                    dayL.setForeground(i==day ? new Color(255,215,0) : new Color(150,150,180));
                    dayL.setAlignmentX(Component.CENTER_ALIGNMENT);
                    JLabel xpL = new JLabel("+"+dayXp[i-1]);
                    xpL.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    xpL.setForeground(i==day ? new Color(255,215,0) : new Color(100,100,130));
                    xpL.setAlignmentX(Component.CENTER_ALIGNMENT);
                    card.add(dayL); card.add(xpL);
                    dayGrid.add(card);
                }
                JButton claimBtn = new JButton("Claim Reward! +" + xp + " XP");
                claimBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
                claimBtn.setBackground(new Color(0x0ea5e9));
                claimBtn.setForeground(Color.WHITE);
                claimBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
                claimBtn.setMaximumSize(new Dimension(220,44));
                claimBtn.addActionListener(e -> {
                    XPService.addXP(currentUser.getUserId(), currentUser.getTotalXp(), xp);
                    try (Connection c2 = DBConnection.getConnection()) {
                        PreparedStatement p2 = c2.prepareStatement("UPDATE users SET last_login=NOW() WHERE user_id=?");
                        p2.setInt(1, currentUser.getUserId());
                        p2.executeUpdate();
                    } catch (Exception ex) {}
                    claimBtn.setText("Claimed! +" + xp + " XP!");
                    claimBtn.setBackground(new Color(16,185,129));
                    claimBtn.setEnabled(false);
                    new Timer(1500, ev -> { ((Timer)ev.getSource()).stop(); dialog.dispose(); }).start();
                });
                panel.add(titleLbl);
                panel.add(Box.createVerticalStrut(8));
                panel.add(subLbl);
                panel.add(Box.createVerticalStrut(20));
                panel.add(dayGrid);
                panel.add(Box.createVerticalStrut(24));
                panel.add(claimBtn);
                dialog.add(panel);
                dialog.setVisible(true);
            }
        } catch (Exception ex) {}
    }

    private static java.util.List<String> notifications = new java.util.ArrayList<>();
    private static int unreadCount = 0;
        private JPanel logoutPanelRef;
    private boolean notificationsShown = false;
    private int previousLevel = -1;
    private boolean dailyChallengeAttempted = false;
    private boolean wheelSpunToday = false;
    private Timer pomodoroTimer = null;
    private int pomodoroSecondsLeft = 25 * 60;
    private int pomodoroTotalSeconds = 25 * 60;
    private boolean pomodoroIsBreak = false;
    private JPanel pomodoroPanel = null;
    private JLabel pomodoroModeLabel = null;
    private Timer bossQuestionTimer = null;
    private static Color avatarColor = new Color(0x0ea5e9);
    private static String selectedBadge = "Beginner";
    private static String selectedTitle = "";
    private static final String[] FUN_FACTS = {
        "Java was created by James Gosling in 1995",
        "Python is named after Monty Python, not the snake!",
        "The first computer bug was an actual bug found in 1947",
        "JavaScript was created in just 10 days",
        "Git was created by Linus Torvalds in 2005"
    };
    private static final String[] TYPING_SNIPPETS = {
        "public static void main(String[] args) {}",
        "for(int i=0; i<10; i++) { System.out.println(i); }",
        "if(condition) { doSomething(); } else { doOther(); }"
    };


    private void addNotification(String message) {
        java.time.LocalTime t = java.time.LocalTime.now();
        String time = String.format("%02d:%02d", t.getHour(), t.getMinute());
        notifications.add(0, "[" + time + "] " + message);
        unreadCount++;




    }





































































    private void showLearningPath() {
        contentPanel.removeAll();
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(0x0d1520));
        main.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("Learning Path");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        JLabel sub = new JLabel("  Your journey from Beginner to Legend");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(0x475569));
        JPanel hdr = new JPanel(); hdr.setOpaque(false);
        hdr.setLayout(new BoxLayout(hdr, BoxLayout.Y_AXIS));
        hdr.add(title); hdr.add(Box.createVerticalStrut(4)); hdr.add(sub);
        main.add(hdr, BorderLayout.NORTH);

        String[][] beginner     = {{"Java Basics","Beginner"},{"Python Basics","Beginner"},{"Web Basics","Beginner"},{"C++ Basics","Beginner"}};
        String[][] intermediate = {{"OOP Concepts","Intermediate"},{"Data Structures","Intermediate"},{"Algorithms","Intermediate"},{"SQL & Databases","Intermediate"}};
        String[][] advanced     = {{"Design Patterns","Advanced"},{"System Design","Advanced"},{"AI / ML Basics","Advanced"},{"Git & DevOps","Advanced"}};
        String[] headers  = {"BEGINNER", "INTERMEDIATE", "ADVANCED"};
        Color[]  hdrColors = {new Color(0x22c55e), new Color(0xf59e0b), new Color(0xef4444)};
        java.util.List<String[][]> cols = java.util.Arrays.asList(beginner, intermediate, advanced);
        int userLevel = currentUser.getCurrentLevel();

        JPanel roadmap = new JPanel(new GridLayout(1, 3, 20, 0));
        roadmap.setOpaque(false);
        roadmap.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        for (int c = 0; c < 3; c++) {
            JPanel col = new JPanel();
            col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
            col.setBackground(new Color(0x0d1520));
            col.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(hdrColors[c].getRed(), hdrColors[c].getGreen(), hdrColors[c].getBlue(), 80), 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

            JLabel colHdr = new JLabel(headers[c]);
            colHdr.setFont(new Font("Segoe UI", Font.BOLD, 13));
            colHdr.setForeground(hdrColors[c]);
            colHdr.setAlignmentX(Component.CENTER_ALIGNMENT);
            col.add(colHdr);
            col.add(Box.createVerticalStrut(16));

            String[][] nodes = cols.get(c);
            for (int i = 0; i < nodes.length; i++) {
                boolean completed = (c == 0 && userLevel > i+1) || (c == 1 && userLevel > 4+i) || (c == 2 && userLevel > 8+i);
                boolean current   = (c == 0 && userLevel == i+1) || (c == 1 && userLevel == 4+i+1) || (c == 2 && userLevel == 8+i+1);
                Color nodeBg     = completed ? new Color(0x052e16) : current ? new Color(0x0d1f3c) : new Color(0x0d1117);
                Color nodeAccent = completed ? new Color(0x22c55e) : current ? new Color(0x0ea5e9) : new Color(0x1e293b);
                String statusTxt = completed ? "âœ“ Completed" : current ? "â†’ In Progress" : "ðŸ”’ Locked";
                Color statusColor = completed ? new Color(0x22c55e) : current ? new Color(0xf59e0b) : new Color(0x334155);

                JPanel node = new JPanel();
                node.setLayout(new BoxLayout(node, BoxLayout.Y_AXIS));
                node.setBackground(nodeBg);
                node.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(nodeAccent, current ? 2 : 1),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)));
                node.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

                JLabel nodeName = new JLabel(nodes[i][0]);
                nodeName.setFont(new Font("Segoe UI", Font.BOLD, 13));
                nodeName.setForeground(completed ? new Color(0x22c55e) : current ? new Color(0x0ea5e9) : new Color(0x475569));
                nodeName.setAlignmentX(Component.CENTER_ALIGNMENT);

                JLabel statusLbl = new JLabel(statusTxt);
                statusLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                statusLbl.setForeground(statusColor);
                statusLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

                node.add(nodeName);
                node.add(Box.createVerticalStrut(4));
                node.add(statusLbl);
                node.setAlignmentX(Component.CENTER_ALIGNMENT);
                col.add(node);

                if (i < nodes.length - 1) {
                    JLabel arrow = new JLabel("â†“");
                    arrow.setForeground(new Color(0x1e293b));
                    arrow.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                    arrow.setAlignmentX(Component.CENTER_ALIGNMENT);
                    col.add(Box.createVerticalStrut(4));
                    col.add(arrow);
                    col.add(Box.createVerticalStrut(4));
                }
            }
            roadmap.add(col);
        }
        main.add(roadmap, BorderLayout.CENTER);
        contentPanel.add(main);
        contentPanel.revalidate();
        contentPanel.repaint();
    }



























































































    private JLabel createSectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(new Color(0x334155));
        lbl.setBorder(BorderFactory.createEmptyBorder(12, 4, 4, 4));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }
    private JButton createNavBtn(String text, Runnable action) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(0x0ea5e9, true));
                    g2.setColor(new Color(14, 165, 233, 30));
                    g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                    g2.setColor(new Color(14, 165, 233, 80));
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,8,8);
                } else {
                    g2.setColor(new Color(0x0d1520));
                    g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                }
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setBackground(new Color(0x0d1520));
        btn.setForeground(new Color(0xb8c4cc));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(180, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addActionListener(e -> action.run());
        return btn;
    }


    private void showDashboard() {
        if (!notificationsShown) {
            notificationsShown = true;
        }
        boolean bonusGiven = com.techsikho.dao.UserDAO.checkAndUpdateDailyBonus(currentUser.getUserId());
        if (bonusGiven) {
            currentUser.setTotalXp(currentUser.getTotalXp() + 5);
            JOptionPane.showMessageDialog(this, "Daily Login Bonus: +5 XP", "Bonus", JOptionPane.INFORMATION_MESSAGE);
        }

        contentPanel.removeAll();

        // Theme colors matching Matrix Rain
        Color bgMain   = new Color(0x0d1520);
        Color cardBg   = new Color(0x0d1520);
        Color teal     = new Color(0x2dd4bf);
        Color cyan     = new Color(0x0ea5e9);
        Color silver   = new Color(0xb8c4cc);
        Color dimText  = new Color(0x475569);

        int level = XPService.calculateLevel(currentUser.getTotalXp());
        currentUser.setCurrentLevel(level);
        if (previousLevel != -1 && level > previousLevel) showConfetti(level);
        previousLevel = level;
        processStreakFreeze();

        ProgressData dp = loadProgressData();
        int quizzesDone = dp.completedLevels;

        // XP progress
        int[] xpTable = {0,0,100,250,500,900,1400,2000,2700,3500,4500};
        int minXP = (level < xpTable.length) ? xpTable[level] : 4500;
        int maxXP = (level+1 < xpTable.length) ? xpTable[level+1] : 4500;
        int pct = (maxXP == minXP) ? 100 : (int)((double)(currentUser.getTotalXp()-minXP)/(maxXP-minXP)*100);
        pct = Math.max(0, Math.min(100, pct));

        JPanel main = new JPanel(new BorderLayout(0, 16));
        main.setBackground(bgMain);
        main.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        // â”€â”€ BANNER â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        JPanel banner = new JPanel(null) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x0d1520));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(0x2dd4bf, false));
                g2.setColor(new Color(45,212,191,60));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
            }
        };
        banner.setPreferredSize(new Dimension(0, 110));
        banner.setOpaque(false);

        JLabel welcomeLbl = new JLabel("Welcome back, " + currentUser.getUsername() + "!");
        welcomeLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcomeLbl.setForeground(Color.WHITE);
        welcomeLbl.setBounds(20, 14, 700, 30);
        banner.add(welcomeLbl);

        JLabel lvlLbl = new JLabel("Level " + level + "  |  " + currentUser.getTotalXp() + " XP");
        lvlLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lvlLbl.setForeground(teal);
        lvlLbl.setBounds(20, 46, 400, 22);
        banner.add(lvlLbl);

        // XP bar
        JProgressBar xpBar = new JProgressBar(0, 100);
        xpBar.setValue(pct);
        xpBar.setStringPainted(true);
        xpBar.setString(pct + "% to next level");
        xpBar.setForeground(cyan);
        xpBar.setBackground(new Color(0x1e293b));
        xpBar.setBorderPainted(false);
        xpBar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        xpBar.setBounds(20, 72, 500, 20);
        banner.add(xpBar);

        if (isWeeklyChampion()) {
            JLabel champ = new JLabel("\u2605 Weekly Champion!");
            champ.setFont(new Font("Segoe UI", Font.BOLD, 13));
            champ.setForeground(new Color(255, 215, 0));
            champ.setBounds(540, 72, 200, 20);
            banner.add(champ);
        }

        main.add(banner, BorderLayout.NORTH);

        // â”€â”€ CENTER: two columns â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        JPanel leftCol = new JPanel();
        leftCol.setLayout(new BoxLayout(leftCol, BoxLayout.Y_AXIS));
        leftCol.setOpaque(false);

        // Stat cards row
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 10, 0));
        statsRow.setOpaque(false);
        statsRow.setPreferredSize(new Dimension(0, 95));
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        statsRow.add(createPolishedStatCard("Total XP",     String.valueOf(currentUser.getTotalXp()), cyan,   cardBg));
        statsRow.add(createPolishedStatCard("Streak",       getStreakDisplayText(),                   teal,   cardBg));

        statsRow.add(createPolishedStatCard("Quiz",    String.valueOf(quizzesDone),             silver, cardBg));
        statsRow.add(createPolishedStatCard("Level",   String.valueOf(level),                   new Color(0x22c55e), cardBg));


        // Daily Missions card
        JPanel missionsCard = createPolishedCard("Daily Missions", cardBg, teal);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT COUNT(*) FROM user_progress WHERE user_id=? AND DATE(completion_date)=CURDATE()")) {
            ps.setInt(1, currentUser.getUserId());
            ResultSet rs = ps.executeQuery();
            int quizzesToday = rs.next() ? rs.getInt(1) : 0;
            missionsCard.add(createMissionRowPolished("Complete 2 activities today", "+25 XP", quizzesToday >= 2, cardBg));
            missionsCard.add(Box.createVerticalStrut(6));
            missionsCard.add(createMissionRowPolished("Win a Boss Battle", "+25 XP", false, cardBg));
            missionsCard.add(Box.createVerticalStrut(6));
            missionsCard.add(createMissionRowPolished("100% Typing Accuracy", "+25 XP", false, cardBg));
        } catch (Exception e) {
            missionsCard.add(createMissionRowPolished("Complete 2 activities today", "+25 XP", false, cardBg));
        }



        // Fun Fact card
        JPanel factCard = createPolishedCard("Fun Fact", cardBg, teal);
        JLabel factLbl = new JLabel("<html><div style='width:220px;font-style:italic;'>" + getRandomFunFact() + "</div></html>");
        factLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        factLbl.setForeground(silver);
        factLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton refreshFact = new JButton("Refresh");
        refreshFact.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        refreshFact.setBackground(new Color(0x1e293b));
        refreshFact.setForeground(teal);
        refreshFact.setBorderPainted(false);
        refreshFact.setFocusPainted(false);
        refreshFact.setAlignmentX(Component.LEFT_ALIGNMENT);
        refreshFact.setMaximumSize(new Dimension(80, 26));
        refreshFact.addActionListener(e ->
            factLbl.setText("<html><div style='width:220px;font-style:italic;'>" + getRandomFunFact() + "</div></html>"));
        factCard.add(factLbl);
        factCard.add(Box.createVerticalStrut(10));
        factCard.add(refreshFact);

        factCard.add(Box.createVerticalStrut(12));
        JSeparator factSep = new JSeparator(); factSep.setForeground(new Color(0x1e293b)); factSep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1)); factCard.add(factSep);
        factCard.add(Box.createVerticalStrut(8));
        JLabel tipTitle = new JLabel("Tip of the Day"); tipTitle.setFont(new Font("Segoe UI", Font.BOLD, 12)); tipTitle.setForeground(new Color(0x0ea5e9)); tipTitle.setAlignmentX(Component.LEFT_ALIGNMENT); factCard.add(tipTitle);
        factCard.add(Box.createVerticalStrut(4));
        String[] tips = {"Practice coding daily for 30 mins!", "Read error messages carefully.", "Break big problems into small ones.", "Comment your code well.", "Review yesterday's code today."};
        JLabel tipLbl = new JLabel("<html><div style='width:260px;'>" + tips[new java.util.Random().nextInt(tips.length)] + "</div></html>"); tipLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12)); tipLbl.setForeground(new Color(0xb8c4cc)); tipLbl.setAlignmentX(Component.LEFT_ALIGNMENT); factCard.add(tipLbl);
        leftCol.add(missionsCard);
        leftCol.add(Box.createVerticalStrut(12));
        leftCol.add(factCard);


        // â”€â”€ RIGHT COLUMN â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        JPanel rightCol = new JPanel();
        rightCol.setLayout(new BoxLayout(rightCol, BoxLayout.Y_AXIS));
        rightCol.setOpaque(false);

        // Quick Actions
        JPanel qaCard = createPolishedCard("Quick Actions", cardBg, teal);
        JPanel actionGrid = new JPanel(new GridLayout(2, 2, 8, 8));
        actionGrid.setOpaque(false);
        actionGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        actionGrid.add(createPolishedActionBtn("Take Quiz",     cyan,                   this::showLanguages));
        actionGrid.add(createPolishedActionBtn("Boss Battle",   new Color(0xef4444),    this::showBossBattle));
        actionGrid.add(createPolishedActionBtn("Word Scramble", teal,                   this::showWordScramble));
        actionGrid.add(createPolishedActionBtn("Rapid Fire",    new Color(0xf59e0b),    this::showRapidFire));
        qaCard.add(actionGrid);
        rightCol.add(qaCard);
        rightCol.add(Box.createVerticalStrut(12));

        // Recent Activity
        JPanel actCard = createPolishedCard("Recent Activity", cardBg, teal);
        if (dp.recentProgress.isEmpty()) {
            JLabel empty = new JLabel("No activity yet â€” start a quiz!");
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            empty.setForeground(dimText);
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            actCard.add(empty);
        } else {
            int show = Math.min(5, dp.recentProgress.size());
            for (int i = 0; i < show; i++) {
                String[] row = dp.recentProgress.get(i);
                String date = (row[0] != null && row[0].length() >= 10) ? row[0].substring(0,10) : row[0];
                JPanel actRow = new JPanel(new BorderLayout());
                actRow.setOpaque(false);
                actRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
                JLabel actLbl = new JLabel(date + "  \u2014  " + row[1] + " \u203a " + row[2]);
                actLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                actLbl.setForeground(silver);
                JLabel xpBadge = new JLabel("+" + row[3] + " XP");
                xpBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
                xpBadge.setForeground(teal);
                actRow.add(actLbl, BorderLayout.WEST);
                actRow.add(xpBadge, BorderLayout.EAST);
                actCard.add(actRow);
                if (i < show-1) actCard.add(Box.createVerticalStrut(4));
            }
        }
        rightCol.add(actCard);





        JPanel centerPanel = new JPanel(new BorderLayout(0, 12));
        centerPanel.setOpaque(false);
        JPanel columns = new JPanel(new GridLayout(1, 2, 16, 0));
        columns.setOpaque(false);
        columns.add(leftCol);
        columns.add(rightCol);
        centerPanel.add(statsRow, BorderLayout.NORTH);
        centerPanel.add(columns, BorderLayout.CENTER);
        main.add(centerPanel, BorderLayout.CENTER);

        contentPanel.add(main);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createPolishedCard(String title, Color cardBg, Color accentColor) {
        JPanel card = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cardBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 60));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLbl.setForeground(Color.WHITE);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 80));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        card.add(titleLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(sep);
        card.add(Box.createVerticalStrut(10));
        return card;
    }

    private JPanel createPolishedStatCard(String title, String value, Color accent, Color cardBg) {
        JPanel card = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                for (int gi=6; gi>=1; gi--) {
                    g2.setColor(new Color(accent.getRed(),accent.getGreen(),accent.getBlue(), gi*8));
                    g2.setStroke(new BasicStroke(gi*1.2f));
                    g2.drawRoundRect(gi,gi,getWidth()-gi*2-1,getHeight()-gi*2-1,10,10);
                }
                g2.setColor(cardBg);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.setColor(new Color(accent.getRed(),accent.getGreen(),accent.getBlue(),120));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1,1,getWidth()-2,getHeight()-2,10,10);
                g2.setColor(accent);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawLine(16,0,getWidth()-16,0);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        t.setForeground(new Color(0x64748b));
        t.setAlignmentX(Component.CENTER_ALIGNMENT);
        t.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.BOLD, 22));
        v.setForeground(accent);
        v.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(Box.createVerticalStrut(4));
        card.add(t);
        card.add(Box.createVerticalStrut(4));
        card.add(v);
        return card;
    }

    private JPanel createMissionRowPolished(String mission, String reward, boolean done, Color cardBg) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        JLabel mLbl = new JLabel((done ? "\u2713 " : "\u25cb ") + mission);
        mLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        mLbl.setForeground(done ? new Color(0x22c55e) : new Color(0xcbd5e1));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        right.setOpaque(false);
        JLabel rLbl = new JLabel(reward);
        rLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        rLbl.setForeground(done ? new Color(0x22c55e) : new Color(0x0ea5e9));
        JLabel sLbl = new JLabel(done ? "DONE" : "Pending");
        sLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        sLbl.setForeground(done ? new Color(0x22c55e) : new Color(0x475569));
        right.add(rLbl);
        right.add(sLbl);
        row.add(mLbl, BorderLayout.WEST);
        row.add(right, BorderLayout.EAST);
        return row;
    }

    private JButton createPolishedActionBtn(String text, Color accent, Runnable action) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    for (int gi=5; gi>=1; gi--) {
                        g2.setColor(new Color(accent.getRed(),accent.getGreen(),accent.getBlue(),gi*12));
                        g2.setStroke(new BasicStroke(gi*1.5f));
                        g2.drawRoundRect(gi,gi,getWidth()-gi*2-1,getHeight()-gi*2-1,14,14);
                    }
                }
                Color bg2 = getModel().isRollover()
                    ? new Color(accent.getRed(),accent.getGreen(),accent.getBlue(),210)
                    : new Color(accent.getRed(),accent.getGreen(),accent.getBlue(),130);
                g2.setColor(bg2);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),14,14);
                g2.setColor(new Color(accent.getRed(),accent.getGreen(),accent.getBlue(),180));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1,1,getWidth()-2,getHeight()-2,14,14);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,
                    (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> action.run());
        return btn;
    }

    private JPanel createDashboardStatCard(String title, String value, Color accent, Color cardBg) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(cardBg);
        card.setBackground(cardBg);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        t.setForeground(new Color(150, 150, 180));
        t.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.BOLD, 24));
        v.setForeground(accent);
        v.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(t);
        card.add(Box.createVerticalStrut(6));
        card.add(v);
        return card;
    }


    private JButton createQuickActionBtn(String text, Color bg, Runnable action) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> action.run());
        return btn;
    }


    private void processStreakFreeze() {
        if (!streakFreezeActive) return;
        if (streakMissedYesterday()) {
            streakFreezeActive = false;
        }
    }

    private boolean streakMissedYesterday() {
        String sql = "SELECT last_login FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, currentUser.getUserId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    java.sql.Date lastLogin = rs.getDate("last_login");
                    if (lastLogin == null) return false;
                    long daysDiff = ChronoUnit.DAYS.between(lastLogin.toLocalDate(), LocalDate.now());
                    return daysDiff > 1;
                }
            }
        } catch (SQLException e) {
            System.err.println("Streak check error: " + e.getMessage());
        }
        return false;
    }


    private String getStreakDisplayText() {
        int streak = currentUser.getStreakCount();
        if (streakFreezeActive) return streak + " days [F]";
        return streak + " days";
    }



    private int awardXP(int baseXp) {
        int xp = baseXp;
        if (doubleXPActive && doubleXPQuizzesLeft > 0) {
            xp = baseXp * 2;
            doubleXPQuizzesLeft--;
            if (doubleXPQuizzesLeft <= 0) doubleXPActive = false;
        }
        int newXp = XPService.addXP(currentUser.getUserId(), currentUser.getTotalXp(), xp);
        currentUser.setTotalXp(newXp);
        return xp;
    }

    private boolean purchaseShopItem(String itemName, int cost, Runnable onSuccess) {
        if (purchasedItems.contains(itemName)) return false;
        if (currentUser.getTotalXp() < cost) return false;
        int newXp = XPService.addXP(currentUser.getUserId(), currentUser.getTotalXp(), -cost);
        currentUser.setTotalXp(newXp);
        purchasedItems.add(itemName);
        onSuccess.run();
        return true;
    }

    private List<String[]> loadLanguageOptions() {
        List<String[]> langs = new ArrayList<>();
        String sql = "SELECT lang_id, lang_name FROM languages ORDER BY lang_name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                langs.add(new String[]{String.valueOf(rs.getInt("lang_id")), rs.getString("lang_name")});
            }
        } catch (SQLException e) {
            System.err.println("Language load error: " + e.getMessage());
        }
        if (langs.isEmpty()) {
            langs.add(new String[]{"1", "Java"});
            langs.add(new String[]{"2", "Python"});
            langs.add(new String[]{"3", "C++"});
            langs.add(new String[]{"4", "Web Dev"});
        }
        return langs;
    }

    private Integer getUserIdByUsername(String username) {
        String sql = "SELECT user_id FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("user_id");
            }
        } catch (SQLException e) {
            System.err.println("User lookup error: " + e.getMessage());
        }
        return null;
    }

    private int getFriendRecentQuizScore(int userId) {
        String sql = "SELECT xp_earned FROM user_progress WHERE user_id = ? ORDER BY completion_date DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("xp_earned");
            }
        } catch (SQLException e) {
            System.err.println("Friend score error: " + e.getMessage());
        }
        return 0;
    }

    private String getBadgeForLevel(int level) {
        if (level >= 5) return "Legend";
        if (level >= 4) return "Champion";
        if (level >= 3) return "Fighter";
        if (level >= 2) return "Explorer";
        return "Beginner";
    }

    private boolean isBadgeUnlocked(String badge, int level) {
        switch (badge) {
            case "Beginner": return level >= 1;
            case "Explorer": return level >= 2;
            case "Fighter": return level >= 3;
            case "Champion": return level >= 4;
            case "Legend": return level >= 5;
            default: return false;
        }
    }

    private JPanel createDailyMissionsCard() {
        JPanel card = new JPanel();
        card.setBackground(new Color(0x0d1520));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JLabel title = new JLabel("Daily Missions");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(8));

        // Mission 1: Complete 2 quizzes today
        int quizzesToday = 0;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT COUNT(*) FROM user_progress WHERE user_id=? AND DATE(completion_date)=CURDATE()")) {
            ps.setInt(1, currentUser.getUserId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) quizzesToday = rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Daily mission check error: " + e.getMessage());
        }
        boolean m1done = quizzesToday >= 2;
        card.add(createMissionRow("Complete 2 activities today", "+25 XP", m1done));
        card.add(Box.createVerticalStrut(6));
        card.add(createMissionRow("Win a Boss Battle", "+25 XP", false));
        card.add(Box.createVerticalStrut(6));
        card.add(createMissionRow("100% Typing Accuracy", "+25 XP", false));
        return card;
    }

    private JPanel createMissionRow(String mission, String reward, boolean done) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(new Color(0x0d1520));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JLabel missionLbl = new JLabel(mission);
        missionLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        missionLbl.setForeground(Color.WHITE);
        missionLbl.setBackground(new Color(0x0d1520));
        missionLbl.setOpaque(true);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        right.setBackground(new Color(0x0d1520));
        JLabel rewardLbl = new JLabel(reward);
        rewardLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        rewardLbl.setForeground(new Color(0x0ea5e9));
        JLabel statusLbl = new JLabel(done ? "DONE" : "Pending");
        statusLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        statusLbl.setForeground(done ? new Color(16, 185, 129) : new Color(100, 100, 130));
        right.add(rewardLbl);
        right.add(statusLbl);

        row.add(missionLbl, BorderLayout.WEST);
        row.add(right, BorderLayout.EAST);
        return row;
    }

    private void showLessons() {
        contentPanel.removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0x0d1520));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        JLabel title = new JLabel("Lessons");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        JLabel sub = new JLabel("  Learn concepts before taking quizzes");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(0x475569));
        JPanel hdr = new JPanel(); hdr.setOpaque(false);
        hdr.setLayout(new BoxLayout(hdr, BoxLayout.Y_AXIS));
        hdr.add(title); hdr.add(Box.createVerticalStrut(4)); hdr.add(sub);
        panel.add(hdr, BorderLayout.NORTH);
        JPanel grid = new JPanel(new GridLayout(2, 4, 16, 16));
        grid.setBackground(new Color(0x0d1520));
        grid.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        grid.add(createLessonLangCard("Java",         "OOP, Collections, Exceptions",  new Color(0xf59e0b)));
        grid.add(createLessonLangCard("Python",       "Basics, OOP, Functions",        new Color(0x22c55e)));
        grid.add(createLessonLangCard("C++",          "Pointers, STL, Memory",         new Color(0x0ea5e9)));
        grid.add(createLessonLangCard("Web Dev",      "HTML, CSS, JavaScript",         new Color(0xef4444)));
        grid.add(createLessonLangCard("DSA",          "Arrays, Trees, Graphs",         new Color(0x8b5cf6)));
        grid.add(createLessonLangCard("SQL",          "Queries, Joins, Aggregations",  new Color(0x06b6d4)));
        grid.add(createLessonLangCard("AI / ML",      "ML, Algorithms, Deep Learning", new Color(0xec4899)));
        grid.add(createLessonLangCard("Git & DevOps", "Git, CI/CD, Docker",            new Color(0xf97316)));
        panel.add(grid, BorderLayout.CENTER);
        contentPanel.add(panel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }




























































    private JPanel createLessonLangCard(String name, String topics, Color accent) {
        JPanel card = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x0d1520));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 60));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.setColor(accent);
                g2.setStroke(new BasicStroke(3f));
                g2.drawLine(0, 0, getWidth(), 0);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(18, 16, 18, 16));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        JLabel nameLbl = new JLabel(name);
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLbl.setForeground(accent);
        nameLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel topicsLbl = new JLabel(topics);
        topicsLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        topicsLbl.setForeground(new Color(0x64748b));
        topicsLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton open = new JButton("View Lessons â†’") {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? accent : new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(getModel().isRollover() ? Color.WHITE : accent);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2, (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        open.setContentAreaFilled(false);
        open.setBorderPainted(false);
        open.setFocusPainted(false);
        open.setAlignmentX(Component.LEFT_ALIGNMENT);
        open.setMaximumSize(new Dimension(140, 30));
        open.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        open.addActionListener(e -> showLessonsForLanguage(name));
        card.add(nameLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(topicsLbl);
        card.add(Box.createVerticalGlue());
        card.add(Box.createVerticalStrut(14));
        card.add(open);
        return card;
    }





















    private int getLangIdByName(String name) {
        int id = -1;
        String sql = "SELECT lang_id FROM languages WHERE lang_name = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) id = rs.getInt("lang_id");
            }
        } catch (SQLException ex) {
            System.err.println("getLangId error: " + ex.getMessage());
        }
        if (id == -1) {
            if (name.equalsIgnoreCase("Java")) id = 1;
            else if (name.equalsIgnoreCase("Python")) id = 2;
            else if (name.equalsIgnoreCase("C++")) id = 3;
            else id = 4;
        }
        return id;
    }

    private void showLessonsForLanguage(String langName) {
        contentPanel.removeAll();
        JPanel panel = new JPanel();
        panel.setBackground(new Color(0x0d1520));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel(langName + " Lessons");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(12));

        int langId = getLangIdByName(langName);

        String[][] lessons;
        if (langName.equalsIgnoreCase("Java")) {
            lessons = new String[][]{
                {"Introduction to Java", "Java was developed by James Gosling in 1995. It follows Write Once Run Anywhere (WORA) principle. Java programs compile to bytecode running on JVM. Key features: Platform independent, Secure, Robust, Object Oriented, Multithreaded. Java is used in Android apps, web backends, enterprise software."},
                {"Variables and Data Types", "Java has 8 primitive types: int (4 bytes), long (8 bytes), double (8 bytes), float (4 bytes), char (2 bytes), byte (1 byte), short (2 bytes), boolean. Declare variables: int age = 20; String name = Java; double price = 99.9; Java is strongly typed - variable type cannot change. String is a class not primitive."},
                {"OOP Concepts", "4 pillars of OOP: 1-Encapsulation: bind data with methods, use private fields with getters/setters. 2-Inheritance: child class extends parent using extends keyword, reuses code. 3-Polymorphism: one method many forms via overloading and overriding. 4-Abstraction: hide implementation using abstract classes and interfaces."}
            };
        } else if (langName.equalsIgnoreCase("Python")) {
            lessons = new String[][]{
                {"Introduction to Python", "Python created by Guido van Rossum in 1991. Interpreted and dynamically typed language. Clean readable syntax with no semicolons. Indentation defines code blocks. Used in Web Dev, Data Science, AI, ML, Automation. Python supports OOP, functional, and procedural programming paradigms."},
                {"Python Syntax", "Python syntax is simple. Variables: x = 10, name = Python. No type declaration needed. Lists: [1,2,3]. Dict: {key:value}. For loop: for i in range(10). Functions: def greet(name): return Hello+name. Conditionals: if x>5: print(x). Comments with # symbol. String formatting with f-strings."},
                {"Functions and Built-ins", "Functions defined with def keyword. Default args: def greet(name=World). Lambda: square = lambda x: x*x. Built-ins: len(), range(), print(), type(), int(), str(). *args for variable arguments. **kwargs for keyword arguments. Recursion supported. Python has rich standard library."}
            };
        } else if (langName.equalsIgnoreCase("C++")) {
            lessons = new String[][]{
                {"Introduction to C++", "C++ developed by Bjarne Stroustrup in 1983 as C extension. Supports procedural and OOP. Compiled language - very fast execution. Used in game dev, system software, embedded systems, OS. Includes STL with vectors, maps, sets. C++ gives direct memory control via pointers."},
                {"Pointers and Memory", "Pointers store memory addresses. int* ptr = &var; dereference with *ptr. Dynamic memory: new and delete. int* arr = new int[10]; delete[] arr; References: int& ref = var; Pass by reference is efficient. Memory management is manual in C++ unlike Java. Smart pointers: unique_ptr, shared_ptr."},
                {"Classes and OOP", "class Student { private: string name; int age; public: Student(string n, int a){name=n; age=a;} string getName(){return name;} }; Objects: Student s1(Alice,20); Inheritance: class Child: public Parent{}; Virtual functions enable polymorphism. Destructor: ~ClassName(){}."}
            };
        } else {
            lessons = new String[][]{
                {"HTML Basics", "HTML structures web pages. DOCTYPE declaration required. Tags: html, head, body. Headings h1-h6. Paragraphs with p. Links: a href=url. Images: img src=path. Lists: ul ol li. Forms: input, button, select. Semantic HTML5: header, nav, main, section, article, footer. Attributes add properties to tags."},
                {"CSS Basics", "CSS styles HTML. Selectors: element p{}, class .box{}, id #header{}. Box model: content+padding+border+margin. Flexbox: display:flex for layouts. Grid: display:grid. Colors, fonts, backgrounds. Media queries for responsive: @media(max-width:768px). CSS variables: --primary-color:#6366f1. Animations with @keyframes."},
                {"JavaScript Intro", "JavaScript adds interactivity. Variables: let x=10; const PI=3.14; Functions: function add(a,b){return a+b;} Arrow functions: (a,b)=>a+b. DOM: document.getElementById(), querySelector(). Events: element.addEventListener(click, handler). Arrays: map, filter, reduce. Fetch API for HTTP. JSON.parse() and JSON.stringify()."}
            };
        }

        for (int i = 0; i < lessons.length; i++) {
            String lt = lessons[i][0];
            String desc = lessons[i][1];
            JPanel card = new JPanel();
            card.setBackground(new Color(0x0d1520));
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            card.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel h = new JLabel(lt);
            h.setFont(new Font("Segoe UI", Font.BOLD, 14));
            h.setForeground(Color.WHITE);
            h.setAlignmentX(Component.LEFT_ALIGNMENT);

            JTextArea txt = new JTextArea(desc);
            txt.setLineWrap(true);
            txt.setWrapStyleWord(true);
            txt.setEditable(false);
            txt.setRows(5);
            txt.setBackground(new Color(0x0d1520));
            txt.setForeground(Color.WHITE);
            txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            txt.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            JScrollPane descScroll = new JScrollPane(txt);
            descScroll.setBorder(null);
            descScroll.getViewport().setBackground(new Color(0x0d1520));
            descScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
            descScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

            JButton complete = new JButton("Complete Lesson +5 XP");
            complete.setFont(new Font("Segoe UI", Font.BOLD, 13));
            complete.setBackground(new Color(0x0ea5e9));
            complete.setForeground(Color.WHITE);
            complete.setFocusPainted(false);
            complete.setBorderPainted(false);
            complete.setAlignmentX(Component.LEFT_ALIGNMENT);
            complete.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            int langIdFinal = langId;
            complete.addActionListener(e -> {
                complete.setEnabled(false);
                int newTotal = XPService.addXP(currentUser.getUserId(), currentUser.getTotalXp(), 5);
                currentUser.setTotalXp(newTotal);
                com.techsikho.dao.ProgressDAO.saveProgress(currentUser.getUserId(), langIdFinal, 0, 5);
                JOptionPane.showMessageDialog(this, "Lesson Complete! +5 XP", "Lesson", JOptionPane.INFORMATION_MESSAGE);
            });

            card.add(h);
            card.add(Box.createVerticalStrut(8));
            card.add(descScroll);

            String codeExample = getLessonCode(langName, i);
            if (codeExample != null && !codeExample.isEmpty()) {
                card.add(Box.createVerticalStrut(8));
                JTextArea codeArea = new JTextArea(codeExample);
                codeArea.setEditable(false);
                codeArea.setRows(6);
                codeArea.setLineWrap(true);
                codeArea.setWrapStyleWord(true);
                codeArea.setFont(new Font("Courier New", Font.PLAIN, 13));
                codeArea.setBackground(new Color(0x0d1520));
                codeArea.setForeground(new Color(0xb8c4cc));
                codeArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
                JScrollPane codeScroll = new JScrollPane(codeArea);
                codeScroll.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(new Color(0x0ea5e9)),
                    "Code Example", 0, 0, new Font("Segoe UI", Font.BOLD, 12), Color.WHITE));
                codeScroll.getViewport().setBackground(new Color(0x0d1520));
                codeScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
                codeScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
                card.add(codeScroll);
            }

            card.add(Box.createVerticalStrut(8));
            card.add(complete);

            JScrollPane cardScroll = new JScrollPane(card);
            cardScroll.setBorder(BorderFactory.createLineBorder(new Color(0x0d1520)));
            cardScroll.getViewport().setBackground(new Color(0x0d1520));
            cardScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            cardScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            cardScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
            cardScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 380));
            panel.add(cardScroll);
            panel.add(Box.createVerticalStrut(12));
        }

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(0x0d1520));
        contentPanel.add(scroll);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private String getLessonCode(String langName, int lessonIndex) {
        if (langName.equalsIgnoreCase("Java")) {
            switch (lessonIndex) {
                case 0: return "public class HelloWorld {\n    public static void main(String[] args) {\n        System.out.println(Welcome to Java!);\n    }\n}";
                case 1: return "int age = 20;\ndouble price = 99.9;\nString name = Java;\nboolean isActive = true;\nSystem.out.println(age + name);";
                case 2: return "class Animal {\n    String name;\n    void speak() { System.out.println(Generic sound); }\n}\nclass Dog extends Animal {\n    void speak() { System.out.println(Woof!); }\n}";
                default: return null;
            }
        } else if (langName.equalsIgnoreCase("Python")) {
            switch (lessonIndex) {
                case 0: return "print(Hello Python!)\nname = input(Enter name: )\nprint(Hello, + name)";
                case 1: return "fruits = [apple, banana, mango]\nfor fruit in fruits:\n    print(fruit)\nperson = {name: Alice, age: 20}";
                default: return null;
            }
        } else if (langName.equalsIgnoreCase("C++")) {
            if (lessonIndex == 0) return "#include<iostream>\nusing namespace std;\nint main() {\n    cout << Hello C++! << endl;\n    return 0;\n}";
            return null;
        } else if (langName.equalsIgnoreCase("Web Dev") || langName.equalsIgnoreCase("Web")) {
            if (lessonIndex == 0) return "<!DOCTYPE html>\n<html>\n<head><title>My Page</title></head>\n<body>\n    <h1>Hello World!</h1>\n    <p>Welcome to Web Dev</p>\n</body>\n</html>";
            return null;
        }
        return null;
    }

    // Ã¢â€â‚¬Ã¢â€â‚¬ PROGRESS Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬
    private void showProgress() {
        contentPanel.removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0x0d1520));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("Progress Tracker");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        panel.add(title, BorderLayout.NORTH);

        ProgressData progress = loadProgressData();

        JPanel languagePanel = new JPanel(new GridLayout(1, 4, 15, 0));
        languagePanel.setBackground(new Color(0x0d1520));
        languagePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        languagePanel.add(createMiniProgressCard("Java", getCompletedLessonCount(1), 3, new Color(245, 158, 11)));
        languagePanel.add(createMiniProgressCard("Python", getCompletedLessonCount(2), 3, new Color(16, 185, 129)));
        languagePanel.add(createMiniProgressCard("C++", getCompletedLessonCount(3), 3, new Color(0x0ea5e9)));
        languagePanel.add(createMiniProgressCard("Web", getCompletedLessonCount(4), 3, new Color(239, 68, 68)));
        panel.add(languagePanel, BorderLayout.CENTER);

        JPanel topStats = new JPanel(new GridLayout(1, 3, 20, 0));
        topStats.setBackground(new Color(0x0d1520));
        topStats.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        topStats.add(createStatCard("Completed Lessons", String.valueOf(progress.completedLevels), new Color(0x0ea5e9)));
        topStats.add(createStatCard("Progress XP", String.valueOf(progress.totalXp), new Color(16, 185, 129)));
        topStats.add(createStatCard("Recent Activity", String.valueOf(Math.min(progress.recentProgress.size(), 5)), new Color(245, 158, 11)));

        JPanel centerBlock = new JPanel();
        centerBlock.setLayout(new BoxLayout(centerBlock, BoxLayout.Y_AXIS));
        centerBlock.setBackground(new Color(0x0d1520));
        centerBlock.add(topStats);

        JProgressBar completionBar = new JProgressBar(0, 10);
        completionBar.setValue(Math.min(progress.completedLevels, 10));
        completionBar.setStringPainted(true);
        completionBar.setForeground(new Color(0x0ea5e9));
        completionBar.setBackground(new Color(0x0d1520));
        completionBar.setString(progress.completedLevels + " / 10 lessons completed");
        completionBar.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        centerBlock.add(completionBar);
        centerBlock.add(Box.createVerticalStrut(15));

        String[] cols = {"Date", "Language", "Lesson", "XP"};
        JTable progressTable = new JTable(progress.recentProgress.toArray(new String[0][]), cols);
        styleLeaderboardTable(progressTable);
        JScrollPane progressScroll = new JScrollPane(progressTable);
        progressScroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0x0ea5e9)),
            "Recent Completed Lessons", 0, 0, new Font("Segoe UI", Font.BOLD, 14), Color.WHITE));
        progressScroll.getViewport().setBackground(new Color(0x0d1520));
        progressScroll.setPreferredSize(new Dimension(0, 220));
        centerBlock.add(progressScroll);

        panel.add(centerBlock, BorderLayout.SOUTH);
        contentPanel.add(panel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private ProgressData loadProgressData() {
        ProgressData progress = new ProgressData();
        String totalsSql = "SELECT COUNT(*), COALESCE(SUM(xp_earned), 0) FROM user_progress " +
                           "WHERE user_id = ? AND is_completed = 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(totalsSql)) {
            ps.setInt(1, currentUser.getUserId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    progress.completedLevels = rs.getInt(1);
                    progress.totalXp = rs.getInt(2);
                }
            }
        } catch (SQLException e) {
            System.err.println("Progress totals error: " + e.getMessage());
        }

        String recentSql = "SELECT la.lang_name, lv.level_name, up.completion_date, up.xp_earned FROM user_progress up LEFT JOIN levels lv ON up.level_id = lv.level_id LEFT JOIN languages la ON lv.lang_id = la.lang_id WHERE up.user_id = ? AND up.is_completed = 1 ORDER BY up.progress_id DESC LIMIT 5";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(recentSql)) {
            ps.setInt(1, currentUser.getUserId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    progress.recentProgress.add(new String[]{
                        rs.getString("completion_date"),
                        rs.getString("lang_name"),
                        rs.getString("level_name"),
                        String.valueOf(rs.getInt("xp_earned"))
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Progress load error: " + e.getMessage());
        }
        return progress;
    }

    private static class ProgressData {
        int completedLevels = 0;
        int totalXp = 0;
        List<String[]> recentProgress = new ArrayList<>();
    }

    private JPanel createStatCard(String title, String value, Color accent) {
        JPanel card = new JPanel();
        card.setBackground(new Color(0x0d1520));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setForeground(new Color(150, 150, 180));
        t.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.BOLD, 28));
        v.setForeground(accent);
        v.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(t);
        card.add(Box.createVerticalStrut(8));
        card.add(v);
        return card;
    }

    private JPanel createLevelStatCard(int level) {
        JPanel card = new JPanel();
        card.setBackground(new Color(0x0d1520));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        JLabel t = new JLabel("Level");
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setForeground(new Color(150, 150, 180));
        t.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel v = new JLabel(String.valueOf(level));
        v.setFont(new Font("Segoe UI", Font.BOLD, 28));
        v.setForeground(new Color(16, 185, 129));
        v.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel subtitle = new JLabel(getLevelTitle(level));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(new Color(16, 185, 129));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(t);
        card.add(Box.createVerticalStrut(8));
        card.add(v);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        return card;
    }

    private JPanel createStreakStatCard(int streak) {
        JPanel card = new JPanel();
        card.setBackground(new Color(0x0d1520));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        JLabel t = new JLabel("Streak");
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setForeground(new Color(150, 150, 180));
        t.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel v = new JLabel(streak + " days");
        v.setFont(new Font("Segoe UI", Font.BOLD, 28));
        v.setForeground(new Color(245, 158, 11));
        v.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(t);
        card.add(Box.createVerticalStrut(8));
        card.add(v);
        String message = getStreakMessage(streak);
        if (!message.isEmpty()) {
            JLabel subtitle = new JLabel(message);
            subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            subtitle.setForeground(new Color(245, 158, 11));
            subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(Box.createVerticalStrut(4));
            card.add(subtitle);
        }
        return card;
    }

    private String getLevelTitle(int level) {
        switch (level) {
            case 1: return "Beginner";
            case 2: return "Learner";
            case 3: return "Coder";
            case 4: return "Developer";
            case 5: return "Engineer";
            case 6: return "Architect";
            case 7: return "Expert";
            case 8: return "Master";
            case 9: return "Champion";
            default: return "Legend";
        }
    }

    private String getStreakMessage(int streak) {
        if (streak >= 30) return "Mythic!";
        if (streak >= 14) return "Legend!";
        if (streak >= 7) return "Unstoppable!";
        if (streak >= 5) return "Week warrior!";
        if (streak >= 3) return "On fire!";
        if (streak >= 2) return "Getting warmer!";
        if (streak >= 1) return "Just started!";
        return "";
    }

    private Question fetchRandomQuestion() {
        String sql = "SELECT * FROM questions ORDER BY RAND() LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new Question(rs.getInt("question_id"), rs.getInt("level_id"),
                    rs.getString("question_text"), rs.getString("option_a"),
                    rs.getString("option_b"), rs.getString("option_c"),
                    rs.getString("option_d"), rs.getString("correct_ans"),
                    rs.getString("explanation"), rs.getString("difficulty"));
            }
        } catch (SQLException e) {
            System.err.println("Daily challenge fetch error: " + e.getMessage());
        }
        return null;
    }

    private int getCompletedLessonCount() {
        String sql = "SELECT COUNT(*) FROM user_progress WHERE user_id = ? AND is_completed = 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, currentUser.getUserId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Completed lesson count error: " + e.getMessage());
        }
        return 0;
    }

    private int getCompletedLessonCount(int langId) {
        String sql = "SELECT COUNT(*) FROM user_progress up JOIN levels l ON up.level_id = l.level_id " +
                     "WHERE up.user_id = ? AND l.lang_id = ? AND up.is_completed = 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, currentUser.getUserId());
            ps.setInt(2, langId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException ex) {
            System.err.println("Completed lesson count error: " + ex.getMessage());
        }
        return 0;
    }

    private JPanel createDailyChallengeCard() {
        JPanel wrapper = new JPanel();
        wrapper.setBackground(new Color(0x0d1520));
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0x0ea5e9)),
            "Daily Challenge", 0, 0, new Font("Segoe UI", Font.BOLD, 14), Color.WHITE));
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));

        Question question = fetchRandomQuestion();
        if (question == null) {
            JLabel empty = new JLabel("No questions available.");
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            empty.setForeground(new Color(150, 150, 180));
            empty.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            wrapper.add(empty);
            return wrapper;
        }

        JLabel questionLabel = new JLabel("<html><div style='width:600px;'>" + question.getQuestionText() + "</div></html>");
        questionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        questionLabel.setForeground(Color.WHITE);
        questionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        questionLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        wrapper.add(questionLabel);

        JPanel optionsPanel = new JPanel();
        optionsPanel.setBackground(new Color(0x0d1520));
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 10, 15));

        ButtonGroup group = new ButtonGroup();
        JRadioButton[] radios = new JRadioButton[4];
        String[] labels = {"A", "B", "C", "D"};
        String[] options = {question.getOptionA(), question.getOptionB(), question.getOptionC(), question.getOptionD()};
        for (int i = 0; i < 4; i++) {
            radios[i] = new JRadioButton(labels[i] + ". " + options[i]);
            radios[i].setFont(new Font("Segoe UI", Font.PLAIN, 13));
            radios[i].setForeground(Color.WHITE);
            radios[i].setBackground(new Color(0x0d1520));
            radios[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            group.add(radios[i]);
            optionsPanel.add(radios[i]);
        }
        wrapper.add(optionsPanel);

        JButton submitBtn = new JButton("Submit Answer");
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFocusPainted(false);
        submitBtn.setBorderPainted(false);
        submitBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        submitBtn.setMaximumSize(new Dimension(160, 36));

        if (dailyChallengeAttempted) {
            submitBtn.setEnabled(false);
            for (JRadioButton radio : radios) radio.setEnabled(false);
        } else {
            submitBtn.addActionListener(e -> {
                String selected = null;
                for (int i = 0; i < 4; i++) {
                    if (radios[i].isSelected()) { selected = labels[i]; break; }
                }
                if (selected == null) {
                    JOptionPane.showMessageDialog(this, "Please select an answer.", "Daily Challenge", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (selected.equals(question.getCorrectAns())) {
                    int newXp = XPService.addXP(currentUser.getUserId(), currentUser.getTotalXp(), 15);
                    currentUser.setTotalXp(newXp);
                    JOptionPane.showMessageDialog(this, "Correct! +15 XP", "Daily Challenge", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Wrong! Correct answer: " + question.getCorrectAns(), "Daily Challenge", JOptionPane.INFORMATION_MESSAGE);
                }
                dailyChallengeAttempted = true;
                submitBtn.setEnabled(false);
                for (JRadioButton radio : radios) radio.setEnabled(false);
            });
        }

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        btnPanel.setBackground(new Color(0x0d1520));
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnPanel.add(submitBtn);
        wrapper.add(btnPanel);
        return wrapper;
    }

    private void drawCenteredString(Graphics2D g2, String text, int width, int y) {
        FontMetrics fm = g2.getFontMetrics();
        int x = (width - fm.stringWidth(text)) / 2;
        g2.drawString(text, x, y);
    }

    private JPanel createFunFactCard() {
        JPanel card = new JPanel();
        card.setBackground(new Color(0x0d1520));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Fun Fact");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel factLabel = new JLabel("<html><div style='width:280px;'>" + getRandomFunFact() + "</div></html>");
        factLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        factLabel.setForeground(Color.WHITE);
        factLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setBorderPainted(false);
        refreshBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        refreshBtn.setMaximumSize(new Dimension(80, 28));
        refreshBtn.addActionListener(e -> factLabel.setText(
            "<html><div style='width:280px;'>" + getRandomFunFact() + "</div></html>"));

        card.add(title);
        card.add(Box.createVerticalStrut(8));
        card.add(factLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(refreshBtn);
        return card;
    }

    private String getRandomFunFact() {
        return FUN_FACTS[new Random().nextInt(FUN_FACTS.length)];
    }

    private JPanel createSpinWheelPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setBackground(new Color(0x0d1520));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JButton spinBtn = new JButton("Spin the Wheel");
        spinBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        spinBtn.setBackground(new Color(0x2dd4bf));
        spinBtn.setForeground(Color.WHITE);
        spinBtn.setFocusPainted(false);
        spinBtn.setBorderPainted(false);
        if (wheelSpunToday) spinBtn.setEnabled(false);
        spinBtn.addActionListener(e -> showSpinWheelDialog(spinBtn));
        panel.add(spinBtn);
        return panel;
    }

    private void showSpinWheelDialog(JButton dashboardSpinBtn) {
        JDialog dlg = new JDialog(this, "Spin the Wheel", true);
        dlg.setSize(420, 480);
        dlg.setLocationRelativeTo(this);
        dlg.getContentPane().setBackground(new Color(0x0d1520));

        WheelPanel wheelPanel = new WheelPanel();
        wheelPanel.setPreferredSize(new Dimension(360, 360));

        JButton spinActionBtn = new JButton("SPIN!");
        spinActionBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        spinActionBtn.setForeground(Color.WHITE);
        spinActionBtn.setFocusPainted(false);
        spinActionBtn.setBorderPainted(false);

        JPanel content = new JPanel();
        content.setBackground(new Color(0x0d1520));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        wheelPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        spinActionBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(wheelPanel);
        content.add(Box.createVerticalStrut(15));
        content.add(spinActionBtn);
        dlg.add(content);

        final int[] winIndex = {new Random().nextInt(6)};
        final int[] tickCount = {0};
        final int totalTicks = 60;
        final double[] currentRotation = {0};
        final int segmentCenter = winIndex[0] * 60 + 30;
        final double finalRotation = 360.0 * 5 + (90 - segmentCenter);

        spinActionBtn.addActionListener(e -> {
            spinActionBtn.setEnabled(false);
            tickCount[0] = 0;
            Timer animTimer = new Timer(50, null);
            animTimer.addActionListener(ev -> {
                tickCount[0]++;
                double progress = tickCount[0] / (double) totalTicks;
                double eased = 1 - Math.pow(1 - progress, 3);
                currentRotation[0] = eased * finalRotation;
                wheelPanel.setRotation(currentRotation[0]);
                wheelPanel.repaint();
                if (tickCount[0] >= totalTicks) {
                    animTimer.stop();
                    int wonXp = WHEEL_XP[winIndex[0]];
                    int newXp = XPService.addXP(currentUser.getUserId(), currentUser.getTotalXp(), wonXp);
                    currentUser.setTotalXp(newXp);
                    wheelSpunToday = true;
                    dashboardSpinBtn.setEnabled(false);
                    JOptionPane.showMessageDialog(dlg, "You won " + wonXp + " XP!", "Spin Result", JOptionPane.INFORMATION_MESSAGE);
                    dlg.dispose();
                }
            });
            animTimer.start();
        });

        dlg.setVisible(true);
    }

    private class WheelPanel extends JPanel {
        private double rotation = 0;
        WheelPanel() { setBackground(new Color(0x0d1520)); }
        void setRotation(double rotation) { this.rotation = rotation; }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int size = Math.min(getWidth(), getHeight()) - 20;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;
            g2.rotate(Math.toRadians(rotation), x + size / 2.0, y + size / 2.0);
            for (int i = 0; i < 6; i++) {
                g2.setColor(WHEEL_COLORS[i]);
                g2.fillArc(x, y, size, size, i * 60, 60);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                double midAngle = Math.toRadians(i * 60 + 30);
                int tx = (int) (x + size / 2.0 + Math.cos(midAngle) * size * 0.28);
                int ty = (int) (y + size / 2.0 + Math.sin(midAngle) * size * 0.28);
                String label = WHEEL_XP[i] + " XP";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(label, tx - fm.stringWidth(label) / 2, ty + fm.getAscent() / 2);
            }
            g2.rotate(-Math.toRadians(rotation), x + size / 2.0, y + size / 2.0);
            int cx = x + size / 2;
            int[] px = {cx - 12, cx + 12, cx};
            int[] py = {y - 5, y - 5, y + 18};
            g2.setColor(Color.WHITE);
            g2.fillPolygon(px, py, 3);
            g2.setColor(new Color(0x0d1520));
            g2.fillOval(cx - 14, y + size / 2 - 14, 28, 28);
        }
    }

    // Ã¢â€â‚¬Ã¢â€â‚¬ FLASH CARDS Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬
    private String[][] getFlashCardsForTopic(String topic) {
        switch (topic) {
            case "Java": return new String[][]{
                {"What is JVM?", "Java Virtual Machine - executes Java bytecode"},
                {"What is encapsulation?", "Binding data and methods together in a class"},
                {"What is inheritance?", "Child class acquiring properties of parent class"},
                {"What is polymorphism?", "One interface, multiple implementations"},
                {"What is abstraction?", "Hiding implementation details from user"},
                {"What is a constructor?", "Special method called when object is created"},
                {"What is an interface?", "Abstract type with method signatures only"},
                {"What is ArrayList?", "Resizable array implementation in Java Collections"}
            };
            case "Python": return new String[][]{
                {"What is a list in Python?", "Ordered mutable collection: [1,2,3]"},
                {"What is a dictionary?", "Key-value pairs: {key: value}"},
                {"What is a lambda?", "Anonymous function: lambda x: x*2"},
                {"What is PEP 8?", "Python style guide for clean readable code"},
                {"What is __init__?", "Constructor method in Python classes"}
            };
            case "C++": return new String[][]{
                {"What is a pointer?", "Variable storing memory address of another variable"},
                {"What is RAII?", "Resource Acquisition Is Initialization - memory management"},
                {"What is STL?", "Standard Template Library with containers and algorithms"}
            };
            case "Web Dev": return new String[][]{
                {"What is DOM?", "Document Object Model - tree structure of HTML"},
                {"What is CSS Box Model?", "Content + Padding + Border + Margin"},
                {"What is REST API?", "Architectural style for web services using HTTP"}
            };
            case "DSA": return new String[][]{
                {"What is an Array?", "Fixed-size sequential collection of elements"},
                {"What is a Linked List?", "Linear data structure with nodes pointing to next"},
                {"What is a Stack?", "LIFO - Last In First Out data structure"},
                {"What is a Queue?", "FIFO - First In First Out data structure"},
                {"What is Binary Search?", "Search algorithm on sorted array - O(log n)"},
                {"What is Big O notation?", "Describes algorithm time/space complexity"}
            };
            case "SQL": return new String[][]{
                {"What is SELECT?", "Retrieves data from database table"},
                {"What is JOIN?", "Combines rows from two or more tables"},
                {"What is PRIMARY KEY?", "Uniquely identifies each row in a table"},
                {"What is GROUP BY?", "Groups rows with same values together"},
                {"What is an INDEX?", "Speeds up data retrieval in database"}
            };
            case "AI / ML": return new String[][]{
                {"What is supervised learning?", "Model trained on labeled input-output pairs"},
                {"What is overfitting?", "Model performs well on training but fails on new data"},
                {"What is a neural network?", "Layers of interconnected nodes inspired by brain"},
                {"What is gradient descent?", "Optimization algorithm to minimize loss function"},
                {"What is NLP?", "Natural Language Processing - AI understanding text"}
            };
            case "Git & DevOps": return new String[][]{
                {"What is git commit?", "Saves snapshot of changes to local repository"},
                {"What is git merge?", "Combines two branches into one"},
                {"What is CI/CD?", "Continuous Integration and Continuous Deployment"},
                {"What is Docker?", "Platform to build and run containerized apps"},
                {"What is a branch?", "Separate line of development in git"}
            };
            default: return new String[0][0];
        }
    }

    private void showFlashCards() {
        contentPanel.removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0x0d1520));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("Flash Cards");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        JLabel sub = new JLabel("  Tap a topic, then flip cards to test yourself");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(0x475569));
        JPanel hdr = new JPanel(); hdr.setOpaque(false);
        hdr.setLayout(new BoxLayout(hdr, BoxLayout.Y_AXIS));
        hdr.add(title); hdr.add(Box.createVerticalStrut(4)); hdr.add(sub);

        JPanel topicBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        topicBar.setOpaque(false);
        String[][] topics = {
            {"Java","#f59e0b"},{"Python","#22c55e"},{"C++","#0ea5e9"},{"Web Dev","#ef4444"},
            {"DSA","#8b5cf6"},{"SQL","#06b6d4"},{"AI / ML","#ec4899"},{"Git & DevOps","#f97316"}
        };
        JPanel cardArea = new JPanel();
        cardArea.setBackground(new Color(0x0d1520));
        cardArea.setLayout(new BoxLayout(cardArea, BoxLayout.Y_AXIS));

        for (String[] t : topics) {
            Color c = Color.decode(t[1]);
            JButton btn = new JButton(t[0]) {
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D)g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getModel().isRollover() ? c : new Color(c.getRed(), c.getGreen(), c.getBlue(), 40));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setColor(getModel().isRollover() ? Color.WHITE : c);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2, (getHeight()+fm.getAscent()-fm.getDescent())/2);
                }
            };
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(110, 32));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            final String topicName = t[0];
            btn.addActionListener(e -> showFlashCardDeck(cardArea, topicName));
            topicBar.add(btn);
        }

        JPanel north = new JPanel(new BorderLayout());
        north.setOpaque(false);
        north.add(hdr, BorderLayout.NORTH);
        north.add(topicBar, BorderLayout.CENTER);
        panel.add(north, BorderLayout.NORTH);
        panel.add(cardArea, BorderLayout.CENTER);
        contentPanel.add(panel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }









































    private void showFlashCardDeck(JPanel cardArea, String topic) {
        cardArea.removeAll();
        String[][] cards = getFlashCardsForTopic(topic);
        if (cards.length == 0) return;

        final int[] index = {0};
        final boolean[] showingAnswer = {false};

        FlashCardPanel flashPanel = new FlashCardPanel(cards[0][0], cards[0][1], false);
        flashPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        flashPanel.setMaximumSize(new Dimension(650, 220));

        JLabel counter = new JLabel("Card 1 / " + cards.length);
        counter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        counter.setForeground(new Color(150, 150, 180));
        counter.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton flipBtn = new JButton("Flip Card");
        JButton nextBtn = new JButton("Next Card");
        JButton prevBtn = new JButton("Previous");
        for (JButton b : new JButton[]{flipBtn, nextBtn, prevBtn}) {
            b.setFont(new Font("Segoe UI", Font.BOLD, 13));
            b.setBackground(new Color(0x0ea5e9));
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false);
            b.setBorderPainted(false);
        }

        Runnable updateCard = () -> {
            flashPanel.setContent(showingAnswer[0] ? cards[index[0]][1] : cards[index[0]][0], showingAnswer[0]);
            counter.setText("Card " + (index[0] + 1) + " / " + cards.length);
            prevBtn.setEnabled(index[0] > 0);
            nextBtn.setEnabled(index[0] < cards.length - 1);
        };

        flipBtn.addActionListener(e -> { showingAnswer[0] = !showingAnswer[0]; updateCard.run(); });
        nextBtn.addActionListener(e -> { if (index[0] < cards.length - 1) { index[0]++; showingAnswer[0] = false; updateCard.run(); } });
        prevBtn.addActionListener(e -> { if (index[0] > 0) { index[0]--; showingAnswer[0] = false; updateCard.run(); } });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setBackground(new Color(0x0d1520));
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRow.add(flipBtn);
        btnRow.add(prevBtn);
        btnRow.add(nextBtn);

        cardArea.add(counter);
        cardArea.add(Box.createVerticalStrut(10));
        cardArea.add(flashPanel);
        cardArea.add(Box.createVerticalStrut(15));
        cardArea.add(btnRow);
        cardArea.revalidate();
        cardArea.repaint();
    }

    private class FlashCardPanel extends JPanel {
        private final JLabel typeLabel;
        private final JLabel contentLabel;

        FlashCardPanel(String question, String answer, boolean showingAnswer) {
            setLayout(new BorderLayout());
            setPreferredSize(new Dimension(650, 200));
            setBackground(new Color(0x0d1520));
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x0ea5e9), 2, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
            typeLabel = new JLabel(showingAnswer ? "Answer" : "Question");
            typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            typeLabel.setForeground(showingAnswer ? new Color(16, 185, 129) : new Color(0xb8c4cc));
            contentLabel = new JLabel("<html><div style='width:580px;'>" + (showingAnswer ? answer : question) + "</div></html>");
            contentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            contentLabel.setForeground(Color.WHITE);
            add(typeLabel, BorderLayout.NORTH);
            add(contentLabel, BorderLayout.CENTER);
        }

        void setContent(String text, boolean isAnswer) {
            typeLabel.setText(isAnswer ? "Answer" : "Question");
            typeLabel.setForeground(isAnswer ? new Color(16, 185, 129) : new Color(0xb8c4cc));
            contentLabel.setText("<html><div style='width:580px;'>" + text + "</div></html>");
        }
    }

    // Ã¢â€â‚¬Ã¢â€â‚¬ STUDY TIMER Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬
    private void stopPomodoroTimer() {
        if (pomodoroTimer != null) { pomodoroTimer.stop(); pomodoroTimer = null; }
    }

    private void showStudyTimer() {
        stopPomodoroTimer();
        contentPanel.removeAll();
        JPanel panel = new JPanel();
        panel.setBackground(new Color(0x0d1520));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("Study Timer");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));

        pomodoroSecondsLeft = 25 * 60;
        pomodoroTotalSeconds = 25 * 60;
        pomodoroIsBreak = false;
        pomodoroPanel = new PomodoroPanel();
        pomodoroPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(pomodoroPanel);

        pomodoroModeLabel = new JLabel("Study Session");
        pomodoroModeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        pomodoroModeLabel.setForeground(new Color(16, 185, 129));
        pomodoroModeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(10));
        panel.add(pomodoroModeLabel);
        panel.add(Box.createVerticalStrut(20));

        JButton studyBtn = new JButton("Start Study (25 min)");
        JButton breakBtn = new JButton("Start Break (5 min)");
        JButton resetBtn = new JButton("Reset");
        for (JButton b : new JButton[]{studyBtn, breakBtn, resetBtn}) {
            b.setFont(new Font("Segoe UI", Font.BOLD, 13));
            b.setBackground(new Color(0x0ea5e9));
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.setMaximumSize(new Dimension(220, 40));
        }
        studyBtn.addActionListener(e -> startPomodoro(25 * 60, false));
        breakBtn.addActionListener(e -> startPomodoro(5 * 60, true));
        resetBtn.addActionListener(e -> {
            stopPomodoroTimer();
            pomodoroSecondsLeft = 25 * 60;
            pomodoroTotalSeconds = 25 * 60;
            pomodoroIsBreak = false;
            pomodoroModeLabel.setText("Study Session");
            pomodoroModeLabel.setForeground(new Color(16, 185, 129));
            pomodoroPanel.repaint();
        });

        panel.add(studyBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(breakBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(resetBtn);

        contentPanel.add(panel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void startPomodoro(int seconds, boolean isBreak) {
        stopPomodoroTimer();
        pomodoroSecondsLeft = seconds;
        pomodoroTotalSeconds = seconds;
        pomodoroIsBreak = isBreak;
        pomodoroModeLabel.setText(isBreak ? "Break Time!" : "Study Session");
        pomodoroModeLabel.setForeground(isBreak ? new Color(245, 158, 11) : new Color(16, 185, 129));
        pomodoroPanel.repaint();
        pomodoroTimer = new Timer(1000, e -> {
            pomodoroSecondsLeft--;
            pomodoroPanel.repaint();
            if (pomodoroSecondsLeft <= 0) {
                stopPomodoroTimer();
                JOptionPane.showMessageDialog(this,
                    pomodoroIsBreak ? "Break over! Time to study!" : "Great work! Take a 5 min break!",
                    "Study Timer", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        pomodoroTimer.start();
    }

    private class PomodoroPanel extends JPanel {
        PomodoroPanel() { setPreferredSize(new Dimension(280, 280)); setBackground(new Color(0x0d1520)); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int size = Math.min(getWidth(), getHeight()) - 10;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;
            g2.setColor(new Color(0x0d1520));
            g2.setStroke(new BasicStroke(14));
            g2.drawOval(x, y, size, size);
            double progress = pomodoroTotalSeconds > 0 ? (double) pomodoroSecondsLeft / pomodoroTotalSeconds : 0;
            int arcAngle = (int) (360 * progress);
            g2.setColor(pomodoroIsBreak ? new Color(245, 158, 11) : new Color(16, 185, 129));
            g2.drawArc(x, y, size, size, 90, -arcAngle);
            int mins = pomodoroSecondsLeft / 60;
            int secs = pomodoroSecondsLeft % 60;
            String timeStr = String.format("%02d:%02d", mins, secs);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 36));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(timeStr, (getWidth() - fm.stringWidth(timeStr)) / 2, getHeight() / 2 + fm.getAscent() / 2);
        }
    }

    // Ã¢â€â‚¬Ã¢â€â‚¬ NOTES Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬
    private File getNotesFile() { return new File("notes_" + currentUser.getUserId() + ".txt"); }

    private String loadNotesFromFile() {
        File file = getNotesFile();
        if (!file.exists()) return "";
        StringBuilder sb = new StringBuilder();
        try (FileReader reader = new FileReader(file)) {
            char[] buf = new char[1024];
            int n;
            while ((n = reader.read(buf)) != -1) sb.append(buf, 0, n);
        } catch (IOException ex) {
            System.err.println("Notes load error: " + ex.getMessage());
        }
        return sb.toString();
    }

    private void showNotes() {
        stopPomodoroTimer();
        contentPanel.removeAll();
        JPanel panel = new JPanel();
        panel.setBackground(new Color(0x0d1520));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("My Notes");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(15));

        JTextArea notesArea = new JTextArea(loadNotesFromFile());
        notesArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        notesArea.setBackground(new Color(0x0d1520));
        notesArea.setForeground(Color.WHITE);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scroll = new JScrollPane(notesArea);
        scroll.setPreferredSize(new Dimension(0, 350));
        scroll.getViewport().setBackground(new Color(0x0d1520));
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(scroll);
        panel.add(Box.createVerticalStrut(15));

        JButton saveBtn = new JButton("Save Notes");
        JButton clearBtn = new JButton("Clear Notes");
        for (JButton b : new JButton[]{saveBtn, clearBtn}) {
            b.setFont(new Font("Segoe UI", Font.BOLD, 13));
            b.setBackground(new Color(0x0ea5e9));
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
            b.setMaximumSize(new Dimension(140, 38));
        }
        saveBtn.addActionListener(e -> {
            try (FileWriter writer = new FileWriter(getNotesFile())) {
                writer.write(notesArea.getText());
                JOptionPane.showMessageDialog(this, "Notes saved successfully.", "My Notes", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Unable to save notes: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        clearBtn.addActionListener(e -> notesArea.setText(""));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setBackground(new Color(0x0d1520));
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRow.add(saveBtn);
        btnRow.add(clearBtn);
        panel.add(btnRow);

        contentPanel.add(panel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createMiniProgressCard(String name, int count, int total, Color accent) {
        JPanel card = new JPanel();
        card.setBackground(new Color(0x0d1520));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JLabel nameLbl = new JLabel(name);
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLbl.setForeground(accent);
        nameLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel progressLbl = new JLabel(count + " / " + total + " lessons");
        progressLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        progressLbl.setForeground(new Color(0xb8c4cc));
        progressLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JProgressBar bar = new JProgressBar(0, total);
        bar.setValue(Math.min(count, total));
        bar.setForeground(accent);
        bar.setBackground(new Color(0x0d1520));
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 14));
        bar.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(nameLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(progressLbl);
        card.add(Box.createVerticalStrut(8));
        card.add(bar);
        return card;
    }

    // Ã¢â€â‚¬Ã¢â€â‚¬ LANGUAGES Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬

    private void showLanguages() {
        contentPanel.removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0x0d1520));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("Choose a Language");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        JLabel sub = new JLabel("  Select a topic to start your learning journey");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(0x475569));
        JPanel hdr = new JPanel(); hdr.setOpaque(false);
        hdr.setLayout(new BoxLayout(hdr, BoxLayout.Y_AXIS));
        hdr.add(title); hdr.add(Box.createVerticalStrut(4)); hdr.add(sub);
        panel.add(hdr, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 4, 16, 16));
        grid.setBackground(new Color(0x0d1520));
        grid.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        grid.add(createLangCard("Java",          "5 Levels Â· OOP, Collections", new Color(0xf59e0b), 1));
        grid.add(createLangCard("Python",        "3 Levels Â· Basics, OOP",      new Color(0x22c55e), 2));
        grid.add(createLangCard("C++",           "2 Levels Â· Pointers, STL",    new Color(0x0ea5e9), 3));
        grid.add(createLangCard("Web Dev",       "3 Levels Â· HTML, CSS, JS",    new Color(0xef4444), 4));
        grid.add(createLangCard("DSA",           "4 Levels Â· Arrays, Trees",    new Color(0x8b5cf6), 5));
        grid.add(createLangCard("SQL",           "3 Levels Â· Queries, Joins",   new Color(0x06b6d4), 6));
        grid.add(createLangCard("AI / ML",       "3 Levels Â· ML Basics",        new Color(0xec4899), 7));
        grid.add(createLangCard("Git & DevOps",  "2 Levels Â· Git, CI/CD",       new Color(0xf97316), 8));
        panel.add(grid, BorderLayout.CENTER);
        contentPanel.add(panel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }




















    private JPanel createLangCard(String name, String levels, Color accent, int langId) {
        JPanel card = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x0d1520));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 60));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.setColor(accent);
                g2.setStroke(new BasicStroke(3f));
                g2.drawLine(0, 0, getWidth(), 0);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 16, 20, 16));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        JLabel nameLbl = new JLabel(name);
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLbl.setForeground(accent);
        nameLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel levelsLbl = new JLabel(levels);
        levelsLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        levelsLbl.setForeground(new Color(0x64748b));
        levelsLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton startBtn = new JButton("Start Learning â†’") {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? accent : new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(getModel().isRollover() ? Color.WHITE : accent);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2, (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        startBtn.setContentAreaFilled(false);
        startBtn.setBorderPainted(false);
        startBtn.setFocusPainted(false);
        startBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        startBtn.setMaximumSize(new Dimension(140, 30));
        startBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        startBtn.addActionListener(e -> new QuizFrame(currentUser, langId).setVisible(true));
        card.add(nameLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(levelsLbl);
        card.add(Box.createVerticalGlue());
        card.add(Box.createVerticalStrut(14));
        card.add(startBtn);
        return card;
    }




























    // Ã¢â€â‚¬Ã¢â€â‚¬ ANALYTICS Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬
    private void showAnalytics() {
        AnalyticsData analytics = loadAnalyticsData();
        contentPanel.removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0x0d1520));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        JLabel title = new JLabel("Analytics");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        panel.add(title, BorderLayout.NORTH);

        JPanel centerContent = new JPanel();
        centerContent.setLayout(new BoxLayout(centerContent, BoxLayout.Y_AXIS));
        centerContent.setBackground(new Color(0x0d1520));

        if (analytics.totalAttempts == 0) {
            JLabel empty = new JLabel("No quiz attempts yet. Complete lessons and quizzes to see analytics!");
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            empty.setForeground(new Color(0xb8c4cc));
            empty.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            centerContent.add(empty);
        } else {
            JPanel summary = new JPanel(new GridLayout(1, 2, 20, 0));
            summary.setBackground(new Color(0x0d1520));
            summary.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
            summary.add(createStatCard("Quizzes Completed", String.valueOf(analytics.totalAttempts), new Color(0x0ea5e9)));
            summary.add(createStatCard("Total XP Earned", String.valueOf(analytics.totalXp), new Color(16, 185, 129)));
            centerContent.add(summary);

            JPanel charts = new JPanel(new GridLayout(1, 2, 20, 0));
            charts.setBackground(new Color(0x0d1520));
            charts.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            charts.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));
            charts.setAlignmentX(Component.LEFT_ALIGNMENT);
            charts.add(new BarChartPanel(analytics.attemptLabels, analytics.xpPerAttempt));
            charts.add(new PieChartPanel(analytics.languageCounts));
            centerContent.add(charts);
        }

        centerContent.add(Box.createVerticalStrut(20));
        ActivityHeatmapPanel heatmap = new ActivityHeatmapPanel(loadHeatmapData());
        heatmap.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerContent.add(heatmap);

        panel.add(centerContent, BorderLayout.CENTER);
        contentPanel.add(panel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private AnalyticsData loadAnalyticsData() {
        AnalyticsData analytics = new AnalyticsData();
        String sql = "SELECT up.xp_earned, l.lang_name, up.completion_date " +
                     "FROM user_progress up " +
                     "JOIN levels lev ON up.level_id = lev.level_id " +
                     "JOIN languages l ON lev.lang_id = l.lang_id " +
                     "WHERE up.user_id = ? AND up.is_completed = 1 " +
                     "ORDER BY up.completion_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, currentUser.getUserId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int xp = rs.getInt("xp_earned");
                    String language = rs.getString("lang_name");
                    String completionDate = rs.getString("completion_date");
                    analytics.totalAttempts++;
                    analytics.totalXp += xp;
                    analytics.xpPerAttempt.add(xp);
                    analytics.attemptLabels.add("Attempt " + analytics.totalAttempts);
                    analytics.languageCounts.put(language, analytics.languageCounts.getOrDefault(language, 0) + 1);
                    if (analytics.recentActivity.size() < 5)
                        analytics.recentActivity.add(new String[]{completionDate, language, String.valueOf(xp)});
                }
            }
        } catch (SQLException e) {
            System.err.println("Analytics load error: " + e.getMessage());
        }
        if (analytics.totalAttempts > 0)
            analytics.averageXp = analytics.totalXp / (double) analytics.totalAttempts;
        return analytics;
    }

    private static class AnalyticsData {
        int totalAttempts = 0;
        int totalXp = 0;
        double averageXp = 0.0;
        List<Integer> xpPerAttempt = new ArrayList<>();
        List<String> attemptLabels = new ArrayList<>();
        Map<String, Integer> languageCounts = new HashMap<>();
        List<String[]> recentActivity = new ArrayList<>();
    }

    private static class AchievementData {
        int totalAttempts = 0;
        int javaAttempts = 0;
        int pythonAttempts = 0;
        int totalXP = 0;
        int streakCount = 0;
    }

    // Ã¢â€â‚¬Ã¢â€â‚¬ ACHIEVEMENTS Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬
    private void showAchievements() {
        AchievementData data = loadAchievementData();
        contentPanel.removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0x0d1520));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        JLabel title = new JLabel("Achievements");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        panel.add(title, BorderLayout.NORTH);
        JPanel grid = new JPanel(new GridLayout(2, 3, 20, 20));
        grid.setBackground(new Color(0x0d1520));
        grid.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        grid.add(createAchievementCard("First Quiz", data.totalAttempts > 0, "Complete 1 quiz"));
        grid.add(createAchievementCard("Quiz Veteran", data.totalAttempts >= 5, "Complete 5 quizzes"));
        grid.add(createAchievementCard("XP Hunter", data.totalXP >= 100, "Earn 100 XP"));
        grid.add(createAchievementCard("Streak Master", data.streakCount >= 3, "Maintain a 3 day streak"));
        grid.add(createAchievementCard("Java Explorer", data.javaAttempts > 0, "Attempt Java quiz"));
        grid.add(createAchievementCard("Python Explorer", data.pythonAttempts > 0, "Attempt Python quiz"));
        panel.add(grid, BorderLayout.CENTER);
        contentPanel.add(panel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // Ã¢â€â‚¬Ã¢â€â‚¬ EXPORT Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬
    private void showExport() {
        contentPanel.removeAll();
        JPanel panel = new JPanel();
        panel.setBackground(new Color(0x0d1520));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        JLabel title = new JLabel("Export Progress");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(15));
        JLabel subtitle = new JLabel("Download your quiz progress as a CSV file.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(0xb8c4cc));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(25));

        JButton exportBtn = new JButton("Export Progress to CSV");
        exportBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        exportBtn.setForeground(Color.WHITE);
        exportBtn.setFocusPainted(false);
        exportBtn.setBorderPainted(false);
        exportBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        exportBtn.setMaximumSize(new Dimension(260, 42));
        exportBtn.addActionListener(e -> {
            java.util.List<String[]> rows = com.techsikho.dao.ProgressDAO.exportProgressData(currentUser.getUserId());
            if (rows.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No progress data found to export.", "Export Failed", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Save Progress CSV");
            chooser.setFileFilter(new FileNameExtensionFilter("CSV files", "csv"));
            chooser.setSelectedFile(new File("progress_export.csv"));
            if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
            File file = chooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".csv"))
                file = new File(file.getParentFile(), file.getName() + ".csv");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("Date,Language,XP Earned");
                writer.newLine();
                for (String[] row : rows) {
                    writer.write(escapeCsv(row[0]) + "," + escapeCsv(row[1]) + "," + escapeCsv(row[2]));
                    writer.newLine();
                }
                JOptionPane.showMessageDialog(this, "Progress exported to:\n" + file.getAbsolutePath(), "Export Complete", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Unable to save CSV: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(exportBtn);
        contentPanel.add(panel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // Ã¢â€â‚¬Ã¢â€â‚¬ XP HISTORY Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬
    private void showXPHistory() {
        contentPanel.removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0x0d1520));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        JLabel title = new JLabel("XP History");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {"Date", "Language", "XP Earned", "Type"};
        java.util.List<String[]> rows = new ArrayList<>();
        String sql = "SELECT up.completion_date, l.lang_name, up.xp_earned " +
                     "FROM user_progress up " +
                     "JOIN levels lev ON up.level_id = lev.level_id " +
                     "JOIN languages l ON lev.lang_id = l.lang_id " +
                     "WHERE up.user_id = ? ORDER BY up.completion_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, currentUser.getUserId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int xp = rs.getInt("xp_earned");
                    rows.add(new String[]{rs.getString("completion_date"), rs.getString("lang_name"), String.valueOf(xp), xp == 5 ? "Lesson" : "Quiz"});
                }
            }
        } catch (SQLException e) {
            System.err.println("XP history load error: " + e.getMessage());
        }

        if (rows.isEmpty()) {
            JLabel empty = new JLabel("No XP history yet. Complete lessons and quizzes!");
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            empty.setForeground(new Color(0xb8c4cc));
            empty.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
            panel.add(empty, BorderLayout.CENTER);
        } else {
            JTable table = new JTable(rows.toArray(new String[0][]), cols);
            styleDashboardTable(table);
            JScrollPane scroll = new JScrollPane(table);
            scroll.getViewport().setBackground(new Color(0x0d1520));
            scroll.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
            panel.add(scroll, BorderLayout.CENTER);
        }
        contentPanel.add(panel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // Ã¢â€â‚¬Ã¢â€â‚¬ SETTINGS Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬
    private void showSettings() {
        contentPanel.removeAll();
        JPanel panel = new JPanel();
        panel.setBackground(new Color(0x0d1520));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("Account Settings");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));

        JTextField usernameField = new JTextField(currentUser.getUsername());
        styleReadOnlyField(usernameField);
        JTextField emailField = new JTextField(currentUser.getEmail());
        styleReadOnlyField(emailField);
        panel.add(createLabeledField("Username", usernameField));
        panel.add(Box.createVerticalStrut(12));
        panel.add(createLabeledField("Email", emailField));
        panel.add(Box.createVerticalStrut(25));

        JLabel passwordTitle = new JLabel("Change Password");
        passwordTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        passwordTitle.setForeground(Color.WHITE);
        passwordTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(passwordTitle);
        panel.add(Box.createVerticalStrut(15));

        JPasswordField oldPassField = new JPasswordField();
        styleInputField(oldPassField);
        JPasswordField newPassField = new JPasswordField();
        styleInputField(newPassField);
        JPasswordField confirmPassField = new JPasswordField();
        styleInputField(confirmPassField);

        panel.add(createLabeledField("Old Password", oldPassField));
        panel.add(Box.createVerticalStrut(12));
        panel.add(createLabeledField("New Password", newPassField));
        panel.add(Box.createVerticalStrut(12));
        panel.add(createLabeledField("Confirm Password", confirmPassField));
        panel.add(Box.createVerticalStrut(20));

        JLabel status = new JLabel(" ");
        status.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        status.setForeground(new Color(239, 68, 68));
        status.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(status);
        panel.add(Box.createVerticalStrut(12));

        JButton updateBtn = new JButton("Update Password");
        updateBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setFocusPainted(false);
        updateBtn.setBorderPainted(false);
        updateBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        updateBtn.setMaximumSize(new Dimension(240, 42));
        updateBtn.addActionListener(e -> {
            String oldPass = new String(oldPassField.getPassword()).trim();
            String newPass = new String(newPassField.getPassword()).trim();
            String confirmPass = new String(confirmPassField.getPassword()).trim();
            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                status.setText("Please fill all password fields."); return;
            }
            if (!newPass.equals(confirmPass)) {
                status.setText("New passwords do not match."); return;
            }
            if (!com.techsikho.dao.UserDAO.verifyPassword(currentUser.getUserId(), oldPass)) {
                status.setText("Old password is incorrect."); return;
            }
            boolean updated = com.techsikho.dao.UserDAO.updatePassword(
                currentUser.getUserId(), com.techsikho.dao.UserDAO.hashPassword(newPass));
            if (updated) {
                status.setForeground(new Color(16, 185, 129));
                status.setText("Password updated successfully.");
                oldPassField.setText(""); newPassField.setText(""); confirmPassField.setText("");
            } else {
                status.setForeground(new Color(239, 68, 68));
                status.setText("Unable to update password. Try again.");
            }
        });
        panel.add(updateBtn);
        contentPanel.add(panel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void styleReadOnlyField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(new Color(0x0d1520));
        field.setForeground(Color.WHITE);
        field.setEditable(false);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x0ea5e9), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
    }

    private void styleInputField(JComponent field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(new Color(0x0d1520));
        field.setForeground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x0ea5e9), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
    }

    private void styleDashboardTable(JTable table) {
        table.setOpaque(true);
        table.setBackground(new Color(0x0d1520));
        table.setForeground(Color.WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(32);
        table.getTableHeader().setBackground(new Color(0x0ea5e9));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setGridColor(new Color(0x0d1520));
        table.setSelectionBackground(new Color(0x0ea5e9));
        table.setSelectionForeground(Color.WHITE);
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\n") || escaped.contains("\""))
            return "\"" + escaped + "\"";
        return escaped;
    }

    private AchievementData loadAchievementData() {
        AchievementData data = new AchievementData();
        data.totalXP = currentUser.getTotalXp();
        data.streakCount = currentUser.getStreakCount();
        String sql = "SELECT COUNT(*) AS total_count, " +
                     "SUM(CASE WHEN l.lang_id = 1 THEN 1 ELSE 0 END) AS java_count, " +
                     "SUM(CASE WHEN l.lang_id = 2 THEN 1 ELSE 0 END) AS python_count " +
                     "FROM user_progress up " +
                     "JOIN levels lev ON up.level_id = lev.level_id " +
                     "JOIN languages l ON lev.lang_id = l.lang_id " +
                     "WHERE up.user_id = ? AND up.is_completed = 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, currentUser.getUserId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data.totalAttempts = rs.getInt("total_count");
                    data.javaAttempts = rs.getInt("java_count");
                    data.pythonAttempts = rs.getInt("python_count");
                }
            }
        } catch (SQLException e) {
            System.err.println("Achievement load error: " + e.getMessage());
        }
        return data;
    }

    private JPanel createAchievementCard(String title, boolean unlocked, String description) {
        JPanel card = new JPanel();
        card.setBackground(new Color(0x0d1520));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(unlocked ? new Color(16, 185, 129) : new Color(0x1e293b), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(unlocked ? new Color(16, 185, 129) : new Color(170, 170, 170));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descriptionLabel.setForeground(new Color(0xb8c4cc));
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel statusLabel = new JLabel(unlocked ? "Unlocked" : "Locked");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLabel.setForeground(unlocked ? new Color(16, 185, 129) : new Color(120, 120, 120));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(descriptionLabel);
        card.add(Box.createVerticalGlue());
        card.add(statusLabel);
        return card;
    }

    private class BarChartPanel extends JPanel {
        private final List<String> labels;
        private final List<Integer> values;
        BarChartPanel(List<String> labels, List<Integer> values) {
            this.labels = labels; this.values = values;
            setBackground(new Color(0x0d1520));
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int width = getWidth(); int height = getHeight();
            g2.setColor(new Color(0x0d1520));
            g2.fillRect(0, 0, width, height);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g2.drawString("XP Progress Chart", 20, 30);
            if (values.isEmpty()) { g2.setFont(new Font("Segoe UI", Font.PLAIN, 14)); g2.drawString("No data.", 20, 60); return; }
            int maxValue = values.stream().mapToInt(Integer::intValue).max().orElse(1);
            int barAreaHeight = height - 80;
            int barWidth = Math.max(30, (width - 80) / values.size() - 10);
            int x = 40;
            for (int i = 0; i < values.size(); i++) {
                int barHeight = (int) ((values.get(i) / (double) maxValue) * barAreaHeight);
                int y = height - 40 - barHeight;
                g2.setColor(new Color(0x0ea5e9));
                g2.fillRoundRect(x, y, barWidth, barHeight, 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g2.drawString(labels.get(i), x, height - 20);
                g2.drawString(String.valueOf(values.get(i)), x, y - 8);
                x += barWidth + 20;
            }
        }
    }

    private class PieChartPanel extends JPanel {
        private final Map<String, Integer> slices;
        PieChartPanel(Map<String, Integer> slices) { this.slices = slices; setBackground(new Color(0x0d1520)); }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int width = getWidth(); int height = getHeight();
            g2.setColor(new Color(0x0d1520)); g2.fillRect(0, 0, width, height);
            g2.setColor(Color.WHITE); g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g2.drawString("Languages Attempted", 20, 30);
            int total = slices.values().stream().mapToInt(Integer::intValue).sum();
            if (total == 0) { g2.setFont(new Font("Segoe UI", Font.PLAIN, 14)); g2.drawString("No language data.", 20, 60); return; }
            int pieSize = Math.min(width, height) / 2 - 40;
            int x = 20; int y = 50; int startAngle = 0;
            Color[] colors = {new Color(0x0ea5e9), new Color(16,185,129), new Color(239,68,68), new Color(245,158,11), new Color(0x0ea5e9)};
            int colorIndex = 0;
            for (Map.Entry<String, Integer> entry : slices.entrySet()) {
                int angle = (int) Math.round(entry.getValue() / (double) total * 360);
                g2.setColor(colors[colorIndex % colors.length]);
                g2.fillArc(x, y, pieSize, pieSize, startAngle, angle);
                g2.setColor(Color.WHITE); g2.drawArc(x, y, pieSize, pieSize, startAngle, angle);
                startAngle += angle; colorIndex++;
            }
            int legendX = x + pieSize + 20; int legendY = y; colorIndex = 0;
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            for (Map.Entry<String, Integer> entry : slices.entrySet()) {
                g2.setColor(colors[colorIndex % colors.length]);
                g2.fillRect(legendX, legendY + colorIndex * 22, 14, 14);
                g2.setColor(Color.WHITE);
                g2.drawString(entry.getKey() + " (" + entry.getValue() + ")", legendX + 20, legendY + 12 + colorIndex * 22);
                colorIndex++;
            }
        }
    }

    // Ã¢â€â‚¬Ã¢â€â‚¬ LEADERBOARD Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬
    private void showLeaderboard() {
        contentPanel.removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0x0d1520));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel northBlock = new JPanel();
        northBlock.setBackground(new Color(0x0d1520));
        northBlock.setLayout(new BoxLayout(northBlock, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Leaderboard - Top Players");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        northBlock.add(title);
        northBlock.add(Box.createVerticalStrut(15));

        String[] cols = {"Rank", "Username", "Full Name", "XP", "Level", "Streak"};
        JTabbedPane tabs = new JTabbedPane();

        java.util.List<String[]> allData = LeaderboardDAO.getTopUsers();
        northBlock.add(createHallOfFamePanel(allData));
        northBlock.add(Box.createVerticalStrut(15));

        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(new Color(0x0d1520));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        JLabel searchLabel = new JLabel("Search Leaderboard:");
        searchLabel.setForeground(Color.WHITE);
        JTextField searchField = new JTextField();
        searchField.setBackground(new Color(0x0d1520));
        searchField.setForeground(Color.WHITE);
        searchField.setBorder(BorderFactory.createLineBorder(new Color(0x0ea5e9)));
        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        northBlock.add(searchPanel);

        String[][] allTable = allData.toArray(new String[0][]);
        JTable allTableComp = new JTable(allTable, cols);
        TableRowSorter<TableModel> allSorter = new TableRowSorter<>(allTableComp.getModel());
        allTableComp.setRowSorter(allSorter);
        styleLeaderboardTable(allTableComp);
        tabs.addTab("All Time", new JScrollPane(allTableComp));

        java.util.List<String[]> weekList = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT username, full_name, total_xp, current_level, streak_count FROM users WHERE last_login >= DATE_SUB(NOW(), INTERVAL 7 DAY) ORDER BY total_xp DESC");
             ResultSet rs = ps.executeQuery()) {
            int r = 1;
            while (rs.next())
                weekList.add(new String[]{String.valueOf(r++), rs.getString("username"), rs.getString("full_name"), String.valueOf(rs.getInt("total_xp")), String.valueOf(rs.getInt("current_level")), String.valueOf(rs.getInt("streak_count"))});
        } catch (SQLException ex) { System.err.println("Week leaderboard error: " + ex.getMessage()); }
        JTable weekComp = new JTable(weekList.toArray(new String[0][]), cols);
        TableRowSorter<TableModel> weekSorter = new TableRowSorter<>(weekComp.getModel());
        weekComp.setRowSorter(weekSorter);
        styleLeaderboardTable(weekComp);
        tabs.addTab("This Week", new JScrollPane(weekComp));

        java.util.List<String[]> monthList = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT username, full_name, total_xp, current_level, streak_count FROM users WHERE last_login >= DATE_SUB(NOW(), INTERVAL 30 DAY) ORDER BY total_xp DESC");
             ResultSet rs = ps.executeQuery()) {
            int r = 1;
            while (rs.next())
                monthList.add(new String[]{String.valueOf(r++), rs.getString("username"), rs.getString("full_name"), String.valueOf(rs.getInt("total_xp")), String.valueOf(rs.getInt("current_level")), String.valueOf(rs.getInt("streak_count"))});
        } catch (SQLException ex) { System.err.println("Month leaderboard error: " + ex.getMessage()); }
        JTable monthComp = new JTable(monthList.toArray(new String[0][]), cols);
        TableRowSorter<TableModel> monthSorter = new TableRowSorter<>(monthComp.getModel());
        monthComp.setRowSorter(monthSorter);
        styleLeaderboardTable(monthComp);
        tabs.addTab("This Month", new JScrollPane(monthComp));

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            private void applyFilter() {
                String text = searchField.getText();
                RowFilter<TableModel, Object> filter = text.trim().isEmpty() ? null :
                    RowFilter.regexFilter("(?i)" + Pattern.quote(text));
                allSorter.setRowFilter(filter);
                weekSorter.setRowFilter(filter);
                monthSorter.setRowFilter(filter);
            }
            public void insertUpdate(DocumentEvent e) { applyFilter(); }
            public void removeUpdate(DocumentEvent e) { applyFilter(); }
            public void changedUpdate(DocumentEvent e) { applyFilter(); }
        });

        panel.add(northBlock, BorderLayout.NORTH);
        panel.add(tabs, BorderLayout.CENTER);
        contentPanel.add(panel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createHallOfFamePanel(java.util.List<String[]> topUsers) {
        JPanel section = new JPanel();
        section.setBackground(new Color(0x0d1520));
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel hofTitle = new JLabel("Hall of Fame");
        hofTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        hofTitle.setForeground(new Color(255, 215, 0));
        hofTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.add(hofTitle);
        section.add(Box.createVerticalStrut(10));

        JPanel cards = new JPanel(new GridLayout(1, 3, 15, 0));
        cards.setBackground(new Color(0x0d1520));
        cards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        Color[] bgColors = {new Color(180, 140, 20), new Color(160, 160, 170), new Color(160, 100, 50)};
        String[] medals = {"1st", "2nd", "3rd"};
        String[] labels = {"CHAMPION", "", ""};

        for (int i = 0; i < 3; i++) {
            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBackground(bgColors[i]);
            card.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
            if (!labels[i].isEmpty()) {
                JLabel champ = new JLabel(labels[i]);
                champ.setFont(new Font("Segoe UI", Font.BOLD, 11));
                champ.setForeground(Color.WHITE);
                champ.setAlignmentX(Component.CENTER_ALIGNMENT);
                card.add(champ);
                card.add(Box.createVerticalStrut(4));
            }
            if (i < topUsers.size()) {
                String[] user = topUsers.get(i);
                JLabel medal = new JLabel(medals[i]);
                medal.setFont(new Font("Segoe UI", Font.BOLD, 14));
                medal.setForeground(Color.WHITE);
                medal.setAlignmentX(Component.CENTER_ALIGNMENT);
                JLabel uname = new JLabel(user[1]);
                uname.setFont(new Font("Segoe UI", Font.BOLD, 15));
                uname.setForeground(Color.WHITE);
                uname.setAlignmentX(Component.CENTER_ALIGNMENT);
                JLabel xpLbl = new JLabel("XP: " + user[3]);
                xpLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                xpLbl.setForeground(new Color(0x0d1520));
                xpLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
                JLabel lvlLbl = new JLabel("Level: " + user[4]);
                lvlLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                lvlLbl.setForeground(new Color(0x0d1520));
                lvlLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
                card.add(medal);
                card.add(Box.createVerticalStrut(4));
                card.add(uname);
                card.add(Box.createVerticalStrut(4));
                card.add(xpLbl);
                card.add(lvlLbl);
            } else {
                JLabel empty = new JLabel(medals[i] + " - Open");
                empty.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                empty.setForeground(Color.WHITE);
                empty.setAlignmentX(Component.CENTER_ALIGNMENT);
                card.add(empty);
            }
            cards.add(card);
        }
        section.add(cards);
        return section;
    }

    private void styleLeaderboardTable(JTable table) {
        table.setOpaque(true);
        table.setBackground(new Color(0x0d1520));
        table.setForeground(Color.WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(35);
        table.getTableHeader().setBackground(new Color(0x0ea5e9));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setGridColor(new Color(0x0d1520));
        table.setSelectionBackground(new Color(0x0ea5e9));
        table.setSelectionForeground(Color.WHITE);
    }

    // Ã¢â€â‚¬Ã¢â€â‚¬ PROFILE Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬
    private void showProfile() {
        contentPanel.removeAll();
        Color bg = new Color(0x0d1520);
        JPanel panel = new JPanel();
        panel.setBackground(bg);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        int level = XPService.calculateLevel(currentUser.getTotalXp());
        int rank = LeaderboardDAO.getUserRank(currentUser.getUserId());

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 0));
        header.setBackground(bg);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);

        AvatarDisplayPanel avatarPanel = new AvatarDisplayPanel(avatarColor, selectedBadge,
            purchasedItems.contains("Gold Badge"));
        avatarPanel.setPreferredSize(new Dimension(100, 130));
        header.add(avatarPanel);

        JPanel headerText = new JPanel();
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));
        headerText.setBackground(bg);
        JLabel nameLbl = new JLabel(currentUser.getUsername());
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        nameLbl.setForeground(Color.WHITE);
        String titleText = selectedTitle.isEmpty() ? "" : selectedTitle;
        JLabel titleLbl = new JLabel(titleText);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLbl.setForeground(new Color(255, 215, 0));
        JLabel welcome = new JLabel("Welcome back!");
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        welcome.setForeground(new Color(150, 150, 180));
        headerText.add(nameLbl);
        if (!titleText.isEmpty()) {
            headerText.add(Box.createVerticalStrut(4));
            headerText.add(titleLbl);
        }
        headerText.add(Box.createVerticalStrut(4));
        headerText.add(welcome);
        header.add(headerText);
        panel.add(header);
        panel.add(Box.createVerticalStrut(20));

        JTextField fullNameField = new JTextField(currentUser.getFullName());
        fullNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        fullNameField.setBackground(new Color(0x0d1520));
        fullNameField.setForeground(Color.WHITE);
        fullNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x0ea5e9)),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        JTextField usernameField = new JTextField(currentUser.getUsername());
        styleReadOnlyField(usernameField);
        JTextField emailField = new JTextField(currentUser.getEmail());
        styleReadOnlyField(emailField);

        panel.add(createLabeledField("Full Name", fullNameField));
        panel.add(Box.createVerticalStrut(12));
        panel.add(createLabeledField("Username", usernameField));
        panel.add(Box.createVerticalStrut(12));
        panel.add(createLabeledField("Email", emailField));
        panel.add(Box.createVerticalStrut(16));
        panel.add(profileRow("Total XP", String.valueOf(currentUser.getTotalXp())));
        panel.add(Box.createVerticalStrut(8));
        panel.add(profileRow("Level", String.valueOf(level)));
        panel.add(Box.createVerticalStrut(8));
        panel.add(profileRow("Streak", getStreakDisplayText()));
        panel.add(Box.createVerticalStrut(8));
        panel.add(profileRow("Global Rank", "#" + rank));
        panel.add(Box.createVerticalStrut(24));

        JLabel customizeTitle = new JLabel("Customize Avatar");
        customizeTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        customizeTitle.setForeground(Color.WHITE);
        customizeTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(customizeTitle);
        panel.add(Box.createVerticalStrut(12));

        AvatarDisplayPanel previewAvatar = new AvatarDisplayPanel(avatarColor, selectedBadge,
            purchasedItems.contains("Gold Badge"));
        previewAvatar.setPreferredSize(new Dimension(100, 130));
        previewAvatar.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(previewAvatar);
        panel.add(Box.createVerticalStrut(16));

        JLabel colorLbl = new JLabel("Head Color");
        colorLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        colorLbl.setForeground(new Color(150, 150, 180));
        colorLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(colorLbl);
        panel.add(Box.createVerticalStrut(8));
        JPanel colorRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        colorRow.setBackground(bg);
        colorRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        Color[] colors = {
            new Color(239, 68, 68), new Color(0x0ea5e9), new Color(16, 185, 129),
            new Color(0x2dd4bf), new Color(245, 158, 11), new Color(236, 72, 153)
        };
        for (Color c : colors) {
            JButton colorBtn = new JButton();
            colorBtn.setPreferredSize(new Dimension(32, 32));
            colorBtn.setBackground(c);
            colorBtn.setFocusPainted(false);
            colorBtn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
            colorBtn.addActionListener(e -> {
                avatarColor = c;
                avatarPanel.setHeadColor(c);
                previewAvatar.setHeadColor(c);
                avatarPanel.repaint();
                previewAvatar.repaint();
            });
            colorRow.add(colorBtn);
        }
        panel.add(colorRow);
        panel.add(Box.createVerticalStrut(16));

        JLabel badgeLbl = new JLabel("Badge");
        badgeLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        badgeLbl.setForeground(new Color(150, 150, 180));
        badgeLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(badgeLbl);
        panel.add(Box.createVerticalStrut(8));
        JPanel badgeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        badgeRow.setBackground(bg);
        badgeRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        String[] badges = {"Beginner", "Explorer", "Fighter", "Champion", "Legend"};
        for (String badge : badges) {
            boolean unlocked = isBadgeUnlocked(badge, level);
            JLabel badgeBtn = new JLabel(badge);
            badgeBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            badgeBtn.setOpaque(true);
            badgeBtn.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
            if (unlocked) {
                badgeBtn.setBackground(badge.equals(selectedBadge) ? new Color(0x0ea5e9) : new Color(0x0d1520));
                badgeBtn.setForeground(Color.WHITE);
                badgeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                badgeBtn.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        selectedBadge = badge;
                        avatarPanel.setBadge(badge);
                        previewAvatar.setBadge(badge);
                        avatarPanel.repaint();
                        previewAvatar.repaint();
                        showProfile();
                    }
                });
            } else {
                badgeBtn.setBackground(new Color(0x0d1520));
                badgeBtn.setForeground(new Color(0x1e293b));
            }
            badgeRow.add(badgeBtn);
        }
        panel.add(badgeRow);
        panel.add(Box.createVerticalStrut(16));

        if (purchasedItems.contains("Legend Title")) {
            JLabel titleSelLbl = new JLabel("Title");
            titleSelLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            titleSelLbl.setForeground(new Color(150, 150, 180));
            titleSelLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(titleSelLbl);
            panel.add(Box.createVerticalStrut(8));
            JComboBox<String> titleCombo = new JComboBox<>(new String[]{
                "Coder", "Hacker", "Ninja", "Legend", "Champion"});
            titleCombo.setSelectedItem(selectedTitle.isEmpty() ? "Coder" : selectedTitle);
            titleCombo.setMaximumSize(new Dimension(200, 32));
            titleCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
            titleCombo.addActionListener(e -> {
                selectedTitle = (String) titleCombo.getSelectedItem();
                showProfile();
            });
            panel.add(titleCombo);
            panel.add(Box.createVerticalStrut(16));
        }

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        btnRow.setBackground(bg);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton saveAvatarBtn = styleGameBtn("Save Avatar", new Color(16, 185, 129));
        saveAvatarBtn.addActionListener(e ->
            JOptionPane.showMessageDialog(this, "Avatar saved!", "Profile", JOptionPane.INFORMATION_MESSAGE));
        JButton saveBtn = styleGameBtn("Save Changes", new Color(0x0ea5e9));
        saveBtn.addActionListener(e -> {
            String newName = fullNameField.getText().trim();
            if (newName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Full name cannot be blank.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (UserDAO.updateFullName(currentUser.getUserId(), newName)) {
                currentUser.setFullName(newName);
                JOptionPane.showMessageDialog(this, "Profile updated successfully.", "Profile", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Unable to update profile.", "Profile Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnRow.add(saveAvatarBtn);
        btnRow.add(saveBtn);
        panel.add(btnRow);

        if (selectedBadge.isEmpty() || !isBadgeUnlocked(selectedBadge, level)) {
            selectedBadge = getBadgeForLevel(level);
        }

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(bg);
        contentPanel.add(scroll);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private class AvatarDisplayPanel extends JPanel {
        private Color headColor;
        private String badge;
        private boolean goldBadge;

        AvatarDisplayPanel(Color headColor, String badge, boolean goldBadge) {
            this.headColor = headColor;
            this.badge = badge;
            this.goldBadge = goldBadge;
            setOpaque(false);
        }

        void setHeadColor(Color c) { headColor = c; }
        void setBadge(String b) { badge = b; }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int cx = getWidth() / 2;
            g2.setColor(headColor);
            g2.fillOval(cx - 30, 10, 60, 60);
            g2.setColor(headColor.darker());
            g2.fillRoundRect(cx - 35, 72, 70, 40, 10, 10);
            g2.setColor(goldBadge ? new Color(255, 215, 0) : Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(badge);
            g2.drawString(badge, cx - tw / 2, 125);
        }
    }

    private JPanel createLabeledField(String labelText, JComponent field) {
        JPanel row = new JPanel();
        row.setBackground(new Color(0x0d1520));
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(new Color(150, 150, 180));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(label);
        row.add(Box.createVerticalStrut(6));
        row.add(field);
        return row;
    }

    private JPanel profileRow(String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setBackground(new Color(0x0d1520));
        row.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        JLabel lbl = new JLabel(label + ":  ");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setForeground(new Color(150, 150, 180));
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 14));
        val.setForeground(Color.WHITE);
        row.add(lbl);
        row.add(val);
        return row;
    }

private void showAdmin() {
        contentPanel.removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0x0d1520));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0x0d1520));
        JLabel title = new JLabel("Admin Panel");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        headerPanel.add(title, BorderLayout.NORTH);

        int totalUsers = 0, totalQuestions = 0, totalAttempts = 0;
        try (Connection conn = DBConnection.getConnection()) {
            ResultSet rs1 = conn.createStatement().executeQuery("SELECT COUNT(*) FROM users");
            if (rs1.next()) totalUsers = rs1.getInt(1);
            ResultSet rs2 = conn.createStatement().executeQuery("SELECT COUNT(*) FROM questions");
            if (rs2.next()) totalQuestions = rs2.getInt(1);
            ResultSet rs3 = conn.createStatement().executeQuery("SELECT COUNT(*) FROM user_progress");
            if (rs3.next()) totalAttempts = rs3.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }

        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        statsPanel.setBackground(new Color(0x0d1520));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        statsPanel.setPreferredSize(new Dimension(800, 120));
        statsPanel.add(createStatCard("Total Users", String.valueOf(totalUsers), new Color(0x0ea5e9)));
        statsPanel.add(createStatCard("Total Questions", String.valueOf(totalQuestions), new Color(16, 185, 129)));
        statsPanel.add(createStatCard("Total Attempts", String.valueOf(totalAttempts), new Color(245, 158, 11)));
        headerPanel.add(statsPanel, BorderLayout.SOUTH);

        JButton importCsvBtn = new JButton("Import Questions from CSV");
        importCsvBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        importCsvBtn.setForeground(Color.WHITE);
        importCsvBtn.setFocusPainted(false);
        importCsvBtn.setBorderPainted(false);
        importCsvBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("CSV files", "csv"));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                int count = importQuestionsFromCsv(chooser.getSelectedFile());
                JOptionPane.showMessageDialog(this, "Imported " + count + " questions successfully!", "Import Complete", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        JPanel importPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        importPanel.setBackground(new Color(0x0d1520));
        importPanel.add(importCsvBtn);
        headerPanel.add(importPanel, BorderLayout.CENTER);
        panel.add(headerPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "Username", "XP", "Level", "Streak"};
        java.util.List<String[]> data = getAllUsersData();
        JTable table = new JTable(data.toArray(new String[0][]), cols);
        table.setOpaque(true);
        table.setBackground(new Color(0x0d1520));
        table.setForeground(Color.WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.getTableHeader().setBackground(new Color(0x0ea5e9));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setGridColor(new Color(0x0d1520));
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(new Color(0x0d1520));
        scroll.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        panel.add(scroll, BorderLayout.CENTER);
        contentPanel.add(panel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private java.util.List<String[]> getAllUsersData() {
        java.util.List<String[]> users = new ArrayList<>();
        String sql = "SELECT user_id, username, total_xp, current_level, streak_count FROM users ORDER BY total_xp DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                users.add(new String[]{String.valueOf(rs.getInt("user_id")), rs.getString("username"), String.valueOf(rs.getInt("total_xp")), String.valueOf(rs.getInt("current_level")), String.valueOf(rs.getInt("streak_count"))});
        } catch (SQLException e) { System.err.println("Get users data error: " + e.getMessage()); }
        return users;
    }

    // Ã¢â€â‚¬Ã¢â€â‚¬ CERTIFICATE Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬
    private void showCertificate() { System.out.println("DEBUG: showCertificate called, XP=" + currentUser.getTotalXp() + " Level=" + XPService.calculateLevel(currentUser.getTotalXp()));
        contentPanel.removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0x0d1520));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("");
        title.setFont(new Font("Segoe UI", Font.BOLD, 1));
        title.setForeground(Color.WHITE);

        int xp = currentUser.getTotalXp();
        int level = XPService.calculateLevel(xp);
        int count = getCompletedLessonCount();
        int rank = LeaderboardDAO.getUserRank(currentUser.getUserId());

        // Lock condition - Level 10+ OR 1000+ XP
        boolean unlocked = (level >= 10 || xp >= 1000);

        if (!unlocked) {
            JPanel lockPanel = new JPanel();
            lockPanel.setOpaque(false);
            lockPanel.setLayout(new BoxLayout(lockPanel, BoxLayout.Y_AXIS));

            JLabel lockIcon = new JLabel("ðŸ”’", SwingConstants.CENTER);
            lockIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
            lockIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel lockMsg = new JLabel("Certificate Locked!", SwingConstants.CENTER);
            lockMsg.setFont(new Font("Segoe UI", Font.BOLD, 24));
            lockMsg.setForeground(new Color(0xef4444));
            lockMsg.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel req1 = new JLabel("Reach Level 10 OR earn 1000+ XP to unlock", SwingConstants.CENTER);
            req1.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            req1.setForeground(new Color(0x475569));
            req1.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel progress = new JLabel("Your Progress: Level " + level + " | " + xp + " XP", SwingConstants.CENTER);
            progress.setFont(new Font("Segoe UI", Font.BOLD, 14));
            progress.setForeground(new Color(0x0ea5e9));
            progress.setAlignmentX(Component.CENTER_ALIGNMENT);

            JProgressBar xpBar = new JProgressBar(0, 1000);
            xpBar.setValue(Math.min(xp, 1000));
            xpBar.setStringPainted(true);
            xpBar.setString(xp + " / 1000 XP");
            xpBar.setForeground(new Color(0x0ea5e9));
            xpBar.setBackground(new Color(0x0d1520));
            xpBar.setMaximumSize(new Dimension(400, 20));
            xpBar.setAlignmentX(Component.CENTER_ALIGNMENT);

            lockPanel.add(Box.createVerticalStrut(60));
            lockPanel.add(lockIcon);
            lockPanel.add(Box.createVerticalStrut(20));
            lockPanel.add(lockMsg);
            lockPanel.add(Box.createVerticalStrut(10));
            lockPanel.add(req1);
            lockPanel.add(Box.createVerticalStrut(20));
            lockPanel.add(progress);
            lockPanel.add(Box.createVerticalStrut(10));
            lockPanel.add(xpBar);

            panel.add(title, BorderLayout.NORTH);
            panel.add(lockPanel, BorderLayout.CENTER);
        } else {
            String badge = level >= 20 ? "LEGEND" : level >= 15 ? "MASTER" : level >= 10 ? "EXPERT" : level >= 5 ? "CODER" : "BEGINNER";
            String funMessage = xp >= 5000 ? "You are an absolute LEGEND!" : xp >= 2000 ? "Elite Coder Status!" : xp >= 1000 ? "Impressive Coder!" : "Keep Learning!";
            String dateStr = LocalDate.now().toString();
            String statsLine = "XP: " + xp + " | Level: " + level + " | Lessons: " + count + " | Rank: #" + rank;






            JPanel certBg = new JPanel() {
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D)g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(0xfdf6e3));
                    g2.fillRect(0,0,getWidth(),getHeight());
                    g2.setStroke(new BasicStroke(6f));
                    g2.setColor(new Color(0xc8960c));
                    g2.drawRect(8,8,getWidth()-16,getHeight()-16);
                    g2.setStroke(new BasicStroke(2f));
                    g2.setColor(new Color(0xf0c040));
                    g2.drawRect(20,20,getWidth()-40,getHeight()-40);
                    int sx=getWidth()/2, sy=getHeight()-110;
                    g2.setColor(new Color(0xc8960c));
                    for(int i=0;i<16;i++){double a=Math.toRadians(i*22.5);int x1=(int)(sx+28*Math.cos(a));int y1=(int)(sy+28*Math.sin(a));int x2=(int)(sx+38*Math.cos(a+Math.toRadians(11.25)));int y2=(int)(sy+38*Math.sin(a+Math.toRadians(11.25)));g2.drawLine(x1,y1,x2,y2);}
                    GradientPaint sg=new GradientPaint(sx-35,sy-35,new Color(0xffd700),sx+35,sy+35,new Color(0xb8860b));
                    g2.setPaint(sg);g2.fillOval(sx-35,sy-35,70,70);
                    g2.setColor(new Color(0xc8960c));g2.setStroke(new BasicStroke(2f));
                    g2.drawOval(sx-35,sy-35,70,70);g2.drawOval(sx-28,sy-28,56,56);
                    g2.setColor(new Color(0x5b3a00));g2.setFont(new Font("Segoe UI",Font.BOLD,12));
                    FontMetrics fm=g2.getFontMetrics();
                    g2.drawString("TS",sx-fm.stringWidth("TS")/2,sy+5);
                    g2.setColor(new Color(0x888888));g2.setStroke(new BasicStroke(1f));
                    g2.drawLine(sx-220,sy+50,sx-55,sy+50);
                    g2.drawLine(sx+55,sy+50,sx+220,sy+50);
                    g2.setFont(new Font("Segoe UI",Font.PLAIN,11));g2.setColor(new Color(0x666666));
                    g2.drawString("Authorized Signatory",sx-220,sy+65);
                    g2.drawString("Date: "+java.time.LocalDate.now().toString(),sx+60,sy+65);
                }
            };
            certBg.setLayout(new BoxLayout(certBg, BoxLayout.Y_AXIS));

            JLabel titleLbl = new JLabel("Certificate of Achievement", SwingConstants.CENTER);
            titleLbl.setFont(new Font("Palatino Linotype",Font.BOLD,46));
            titleLbl.setForeground(new Color(0x5b3a00));
            titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel presented = new JLabel("PROUDLY PRESENTED TO", SwingConstants.CENTER);
            presented.setFont(new Font("Segoe UI",Font.PLAIN,13));
            presented.setForeground(new Color(0x888888));
            presented.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel userName = new JLabel(currentUser.getUsername().toUpperCase(), SwingConstants.CENTER);
            userName.setFont(new Font("Palatino Linotype",Font.BOLD+Font.ITALIC,52));
            userName.setForeground(new Color(0x1a1a2e));
            userName.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel forLbl = new JLabel("FOR SUCCESSFULLY COMPLETING TECHSIKHO LEARNING PATH", SwingConstants.CENTER);
            forLbl.setFont(new Font("Segoe UI",Font.PLAIN,12));
            forLbl.setForeground(new Color(0x555555));
            forLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel badgeLbl = new JLabel(badge+" | "+funMessage, SwingConstants.CENTER);
            badgeLbl.setFont(new Font("Segoe UI",Font.BOLD,16));
            badgeLbl.setForeground(new Color(0xc8960c));
            badgeLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel statsLbl = new JLabel(statsLine, SwingConstants.CENTER);
            statsLbl.setFont(new Font("Segoe UI",Font.PLAIN,13));
            statsLbl.setForeground(new Color(0x666666));
            statsLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel orgLbl = new JLabel("TechSikho Academy", SwingConstants.CENTER);
            orgLbl.setFont(new Font("Palatino Linotype",Font.BOLD,18));
            orgLbl.setForeground(new Color(0xc8960c));
            orgLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

            certBg.add(Box.createVerticalStrut(40));
            certBg.add(titleLbl);
            certBg.add(Box.createVerticalStrut(30));
            certBg.add(presented);
            certBg.add(Box.createVerticalStrut(10));
            certBg.add(userName);
            certBg.add(Box.createVerticalStrut(25));
            certBg.add(forLbl);
            certBg.add(Box.createVerticalStrut(12));
            certBg.add(badgeLbl);
            certBg.add(Box.createVerticalStrut(12));
            certBg.add(statsLbl);
            certBg.add(Box.createVerticalStrut(12));
            certBg.add(orgLbl);
            certBg.add(Box.createVerticalGlue());

            panel.add(certBg, BorderLayout.CENTER);

















































































            panel.add(title, BorderLayout.NORTH);

        }
        contentPanel.removeAll();
        contentPanel.add(panel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }























































    private void showEscapeHacker() {
        contentPanel.removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0x0d1520));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("ðŸ” Escape the Hacker Room");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(0x0ea5e9));
        panel.add(title, BorderLayout.NORTH);

        String[][] puzzles = {
            {"Binary Decode", "01001000 01101001 = ?", "Ha", "Hi", "Ho", "He", "1"},
            {"Password Clue", "I have keys but no locks. I have space but no room. What am I?", "Map", "Keyboard", "Phone", "Clock", "1"},
            {"Network Puzzle", "Which port does HTTPS use?", "80", "443", "22", "21", "1"},
            {"Hex Decode", "0x48 0x61 0x63 0x6B = ?", "Back", "Hack", "Pack", "Rack", "1"}
        };

        int[] current = {0};
        int[] score = {0};

        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(new Color(0x0d1520));
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        JLabel roomLbl = new JLabel("ðŸšï¸ You are trapped! Solve puzzles to escape...");
        roomLbl.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        roomLbl.setForeground(new Color(0xef4444));
        roomLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel puzzleTypeLbl = new JLabel("Lock 1: " + puzzles[0][0]);
        puzzleTypeLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        puzzleTypeLbl.setForeground(new Color(0xf59e0b));
        puzzleTypeLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel questionLbl = new JLabel("<html><center>" + puzzles[0][1] + "</center></html>");
        questionLbl.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        questionLbl.setForeground(Color.WHITE);
        questionLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel feedbackLbl = new JLabel(" ");
        feedbackLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        feedbackLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel scoreLbl = new JLabel("Score: 0 / " + puzzles.length);
        scoreLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        scoreLbl.setForeground(new Color(0x22c55e));
        scoreLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel optPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        optPanel.setBackground(new Color(0x0d1520));
        optPanel.setMaximumSize(new Dimension(500, 120));

        Runnable[] loadPuzzle = {null};
        loadPuzzle[0] = () -> {
            optPanel.removeAll();
            int idx = current[0];
            if (idx >= puzzles.length) {
                puzzleTypeLbl.setText("ðŸŽ‰ ESCAPED! You solved all locks!");
                questionLbl.setText("Congratulations! You are a true hacker!");
                feedbackLbl.setForeground(new Color(0x22c55e));
                feedbackLbl.setText("Final Score: " + score[0] + "/" + puzzles.length);
                optPanel.repaint();
                return;
            }
            puzzleTypeLbl.setText("Lock " + (idx+1) + ": " + puzzles[idx][0]);
            questionLbl.setText("<html><center>" + puzzles[idx][1] + "</center></html>");
            feedbackLbl.setText(" ");
            for (int i = 2; i <= 5; i++) {
                final String opt = puzzles[idx][i];
                final boolean correct = (i-2) == Integer.parseInt(puzzles[idx][6]);
                JButton btn = new JButton(opt);
                btn.setBackground(new Color(0x0d1520));
                btn.setForeground(Color.WHITE);
                btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                btn.setBorder(BorderFactory.createLineBorder(new Color(0x0ea5e9)));
                btn.addActionListener(e -> {
                    if (correct) {
                        score[0]++;
                        feedbackLbl.setForeground(new Color(0x22c55e));
                        feedbackLbl.setText("âœ… Correct! Lock opened!");
                    } else {
                        feedbackLbl.setForeground(new Color(0xef4444));
                        feedbackLbl.setText("âŒ Wrong! The lock holds...");
                    }
                    scoreLbl.setText("Score: " + score[0] + " / " + puzzles.length);
                    current[0]++;
                    javax.swing.Timer t = new javax.swing.Timer(1000, ev -> { loadPuzzle[0].run(); optPanel.revalidate(); optPanel.repaint(); });
                    t.setRepeats(false); t.start();
                });
                optPanel.add(btn);
            }
            optPanel.revalidate(); optPanel.repaint();
        };
        loadPuzzle[0].run();

        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(roomLbl);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(puzzleTypeLbl);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(questionLbl);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(optPanel);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(feedbackLbl);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(scoreLbl);

        panel.add(centerPanel, BorderLayout.CENTER);
        contentPanel.removeAll();
        contentPanel.add(panel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showGuessOutput() {
        contentPanel.removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0x0d1520));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("ðŸ’» Guess the Output");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(0x0ea5e9));
        panel.add(title, BorderLayout.NORTH);

        String[][] questions = {
            {"int x=5,y=3;\nSystem.out.println(x%y);", "2", "1", "3", "0", "0"},
            {"System.out.println(\"5\"+3+2);", "532", "10", "55", "523", "0"},
            {"int a=10;\nSystem.out.println(a++);", "10", "11", "9", "Error", "0"},
            {"System.out.println(1+2+\"3\");", "33", "123", "6", "Error", "0"},
            {"boolean b=true;\nSystem.out.println(!b||b);", "true", "false", "Error", "null", "0"}
        };

        int[] current = {0};
        int[] score = {0};

        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(new Color(0x0d1520));
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        JLabel scoreLbl = new JLabel("Score: 0 / " + questions.length);
        scoreLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        scoreLbl.setForeground(new Color(0x22c55e));
        scoreLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea codeArea = new JTextArea(questions[0][0]);
        codeArea.setFont(new Font("Consolas", Font.BOLD, 16));
        codeArea.setBackground(new Color(0x0d1520));
        codeArea.setForeground(new Color(0x0ea5e9));
        codeArea.setEditable(false);
        codeArea.setBorder(BorderFactory.createLineBorder(new Color(0x0ea5e9)));
        codeArea.setMaximumSize(new Dimension(500, 80));
        codeArea.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel feedbackLbl = new JLabel(" ");
        feedbackLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        feedbackLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel optPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        optPanel.setBackground(new Color(0x0d1520));
        optPanel.setMaximumSize(new Dimension(500, 120));

        Runnable[] loadQ = {null};
        loadQ[0] = () -> {
            optPanel.removeAll();
            int idx = current[0];
            if (idx >= questions.length) {
                codeArea.setText("ðŸŽ‰ Quiz Complete!");
                feedbackLbl.setForeground(new Color(0x22c55e));
                feedbackLbl.setText("Final Score: " + score[0] + "/" + questions.length);
                if (score[0] >= 4) { XPService.addXP(currentUser.getUserId(), currentUser.getTotalXp(), 50); feedbackLbl.setText(feedbackLbl.getText() + " | +50 XP Earned!"); }
                return;
            }
            codeArea.setText(questions[idx][0]);
            feedbackLbl.setText(" ");
            for (int i = 1; i <= 4; i++) {
                final String opt = questions[idx][i];
                final boolean correct = (i-1) == Integer.parseInt(questions[idx][5]);
                JButton btn = new JButton(opt);
                btn.setBackground(new Color(0x0d1520));
                btn.setForeground(Color.WHITE);
                btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                btn.setBorder(BorderFactory.createLineBorder(new Color(0x0ea5e9)));
                btn.addActionListener(e -> {
                    if (correct) {
                        score[0]++;
                        feedbackLbl.setForeground(new Color(0x22c55e));
                        feedbackLbl.setText("âœ… Correct!");
                    } else {
                        feedbackLbl.setForeground(new Color(0xef4444));
                        feedbackLbl.setText("âŒ Wrong! Answer was: " + questions[idx][1]);
                    }
                    scoreLbl.setText("Score: " + score[0] + " / " + questions.length);
                    current[0]++;
                    javax.swing.Timer t = new javax.swing.Timer(1000, ev -> { loadQ[0].run(); optPanel.revalidate(); optPanel.repaint(); });
                    t.setRepeats(false); t.start();
                });
                optPanel.add(btn);
            }
            optPanel.revalidate(); optPanel.repaint();
        };
        loadQ[0].run();

        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(scoreLbl);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(codeArea);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(optPanel);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(feedbackLbl);

        panel.add(centerPanel, BorderLayout.CENTER);
        contentPanel.removeAll();
        contentPanel.add(panel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showAIPrompt() {
        contentPanel.removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0x0d1520));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("ðŸ¤– AI Prompt Challenge");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(0x0ea5e9));
        panel.add(title, BorderLayout.NORTH);

        String[] tasks = {
            "Write a prompt to make AI explain recursion to a 10-year-old",
            "Write a prompt to make AI generate a secure password policy",
            "Write a prompt to make AI debug a Java NullPointerException",
            "Write a prompt to make AI create a REST API design for a todo app"
        };

        int[] current = {0};
        int[] totalScore = {0};

        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(new Color(0x0d1520));
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        JLabel taskLbl = new JLabel("<html><center><b>Task:</b> " + tasks[0] + "</center></html>");
        taskLbl.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        taskLbl.setForeground(new Color(0xf59e0b));
        taskLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel hintLbl = new JLabel("Tips: Be specific, add context, define output format");
        hintLbl.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        hintLbl.setForeground(new Color(0x475569));
        hintLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea promptArea = new JTextArea(4, 40);
        promptArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        promptArea.setBackground(new Color(0x0d1520));
        promptArea.setForeground(Color.WHITE);
        promptArea.setCaretColor(Color.WHITE);
        promptArea.setBorder(BorderFactory.createLineBorder(new Color(0x0ea5e9)));
        promptArea.setLineWrap(true);
        promptArea.setWrapStyleWord(true);
        promptArea.setMaximumSize(new Dimension(600, 100));
        promptArea.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel scoreLbl = new JLabel("Creativity Score: 0");
        scoreLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        scoreLbl.setForeground(new Color(0x22c55e));
        scoreLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel feedbackLbl = new JLabel(" ");
        feedbackLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        feedbackLbl.setForeground(new Color(0x94a3b8));
        feedbackLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton submitBtn = new JButton("Submit Prompt âž¤");
        submitBtn.setBackground(new Color(0x0ea5e9));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        submitBtn.setBorderPainted(false);
        submitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitBtn.addActionListener(e -> {
            String prompt = promptArea.getText().trim();
            if (prompt.isEmpty()) { feedbackLbl.setText("Please write a prompt first!"); return; }
            int pts = 0;
            if (prompt.length() > 30) pts += 20;
            if (prompt.toLowerCase().contains("explain") || prompt.toLowerCase().contains("generate") || prompt.toLowerCase().contains("create") || prompt.toLowerCase().contains("write")) pts += 20;
            if (prompt.toLowerCase().contains("format") || prompt.toLowerCase().contains("example") || prompt.toLowerCase().contains("step")) pts += 20;
            if (prompt.toLowerCase().contains("context") || prompt.toLowerCase().contains("audience") || prompt.toLowerCase().contains("tone")) pts += 20;
            if (prompt.split(" ").length > 15) pts += 20;
            totalScore[0] += pts;
            scoreLbl.setText("Creativity Score: " + pts + "/100");
            String fb = pts >= 80 ? "ðŸ”¥ Excellent prompt! Very specific and detailed!" :
                        pts >= 60 ? "ðŸ‘ Good prompt! Add more context for higher score." :
                        pts >= 40 ? "ðŸ“ Decent. Try adding output format or audience." :
                                    "ðŸ’¡ Too short. Be more specific and detailed!";
            feedbackLbl.setText(fb);
            promptArea.setText("");
            current[0]++;
            if (current[0] < tasks.length) {
                taskLbl.setText("<html><center><b>Task:</b> " + tasks[current[0]] + "</center></html>");
            } else {
                taskLbl.setText("ðŸŽ‰ All tasks complete! Total: " + totalScore[0] + "/" + (tasks.length*100));
                if (totalScore[0] >= 300) { XPService.addXP(currentUser.getUserId(), currentUser.getTotalXp(), 60); feedbackLbl.setText("Brilliant! +60 XP Earned!"); }
                submitBtn.setEnabled(false);
            }
        });

        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(taskLbl);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(hintLbl);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(promptArea);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(submitBtn);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(scoreLbl);
        centerPanel.add(Box.createVerticalStrut(8));
        centerPanel.add(feedbackLbl);

        panel.add(centerPanel, BorderLayout.CENTER);
        contentPanel.removeAll();
        contentPanel.add(panel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showConfetti(int level) {
        JDialog dlg = new JDialog(this);
        dlg.setUndecorated(true);
        dlg.setModal(false);
        dlg.setSize(getWidth(), getHeight());
        dlg.setLocation(getLocationOnScreen());
        dlg.setBackground(new Color(0, 0, 0, 0));
        ((JComponent) dlg.getContentPane()).setOpaque(false);
        int w = getWidth(); int h = getHeight();
        JLayeredPane layered = new JLayeredPane();
        layered.setPreferredSize(new Dimension(w, h));
        layered.setSize(w, h);
        Random rand = new Random();
        final int[] cx = new int[80]; final int[] cy = new int[80];
        final int[] csize = new int[80]; final Color[] ccolor = new Color[80];
        for (int i = 0; i < 80; i++) {
            cx[i] = rand.nextInt(Math.max(1, w));
            cy[i] = -rand.nextInt(h) - 20;
            csize[i] = 8 + rand.nextInt(8);
            ccolor[i] = WHEEL_COLORS[rand.nextInt(WHEEL_COLORS.length)];
        }
        JPanel confettiPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                for (int i = 0; i < 80; i++) { g2.setColor(ccolor[i]); g2.fillRect(cx[i], cy[i], csize[i], csize[i]); }
            }
        };
        confettiPanel.setOpaque(false);
        confettiPanel.setBounds(0, 0, w, h);
        JLabel levelLbl = new JLabel("LEVEL UP! You reached Level " + level + "!", SwingConstants.CENTER);
        levelLbl.setFont(new Font("Segoe UI", Font.BOLD, 32));
        levelLbl.setForeground(new Color(255, 215, 0));
        levelLbl.setBounds(0, h / 2 - 40, w, 80);
        layered.add(confettiPanel, JLayeredPane.DEFAULT_LAYER);
        layered.add(levelLbl, JLayeredPane.PALETTE_LAYER);
        dlg.setContentPane(layered);
        final int[] ticks = {0};
        Timer anim = new Timer(30, e -> {
            ticks[0]++;
            for (int i = 0; i < 80; i++) cy[i] += 5;
            confettiPanel.repaint();
            if (ticks[0] >= 100) { ((Timer) e.getSource()).stop(); dlg.dispose(); }
        });
        anim.start();
        dlg.setVisible(true);
    }

    private void applyTheme(boolean dark) {
        Color bg = dark ? new Color(0x0d1520) : new Color(0xb8c4cc);
        Color sidebarBg;
        if (purchasedItems.contains("Dark Ninja Theme") && dark) {
            sidebarBg = new Color(0x0d1520);
        } else {
            sidebarBg = dark ? new Color(0x0d1117) : new Color(0xb8c4cc);
        }
        getContentPane().setBackground(bg);
        if (contentPanel != null) contentPanel.setBackground(bg);
        if (sidebarPanel != null) sidebarPanel.setBackground(sidebarBg);
        if (logoPanelRef != null) logoPanelRef.setBackground(sidebarBg);
        if (navButtonsPanel != null) navButtonsPanel.setBackground(sidebarBg);
        if (logoutPanelRef != null) logoutPanelRef.setBackground(sidebarBg);
        if (sidebarScrollPane != null) {
            sidebarScrollPane.setBackground(sidebarBg);
            sidebarScrollPane.getViewport().setBackground(sidebarBg);
        }
        repaint();
    }

    private boolean isWeeklyChampion() {
        String sql = "SELECT user_id FROM user_progress WHERE completion_date >= DATE_SUB(NOW(), INTERVAL 7 DAY) GROUP BY user_id ORDER BY COUNT(*) DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt("user_id") == currentUser.getUserId();
        } catch (SQLException e) { System.err.println("Weekly champion check error: " + e.getMessage()); }
        return false;
    }

    private Map<String, Integer> loadHeatmapData() {
        Map<String, Integer> data = new HashMap<>();
        String sql = "SELECT DATE(completion_date) as day, COUNT(*) as cnt FROM user_progress WHERE user_id = ? AND completion_date >= DATE_SUB(NOW(), INTERVAL 12 WEEK) GROUP BY DATE(completion_date)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, currentUser.getUserId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) data.put(rs.getString("day"), rs.getInt("cnt"));
            }
        } catch (SQLException e) { System.err.println("Heatmap load error: " + e.getMessage()); }
        return data;
    }

    private class ActivityHeatmapPanel extends JPanel {
        private final Map<String, Integer> activityData;
        ActivityHeatmapPanel(Map<String, Integer> activityData) {
            this.activityData = activityData;
            setBackground(new Color(0x0d1520));
            setPreferredSize(new Dimension(200, 200));
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            g2.drawString("Activity Heatmap (Last 12 Weeks)", 0, 18);
            int sq = 16; int gap = 2; int startX = 40; int startY = 30;
            LocalDate base = LocalDate.now().minusWeeks(12);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2.setColor(new Color(150, 150, 180));
            g2.drawString("Mon", 5, startY + sq);
            g2.drawString("Wed", 5, startY + 3 * (sq + gap) + sq);
            g2.drawString("Fri", 5, startY + 5 * (sq + gap) + sq);
            for (int row = 0; row < 12; row++) {
                for (int col = 0; col < 7; col++) {
                    LocalDate day = base.plusDays((long) row * 7 + col);
                    int cnt = activityData.getOrDefault(day.toString(), 0);
                    Color c = cnt == 0 ? new Color(0x0d1520) : cnt == 1 ? new Color(20,80,40) : cnt == 2 ? new Color(30,130,60) : new Color(50,200,80);
                    g2.setColor(c);
                    g2.fillRect(startX + col * (sq + gap), startY + row * (sq + gap), sq, sq);
                }
            }
        }
    }

    // Ã¢â€â‚¬Ã¢â€â‚¬ BOSS BATTLE Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬
    private List<Question> fetchBossQuestions() {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions ORDER BY RAND() LIMIT 20";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                questions.add(new Question(rs.getInt("question_id"), rs.getInt("level_id"),
                    rs.getString("question_text"), rs.getString("option_a"), rs.getString("option_b"),
                    rs.getString("option_c"), rs.getString("option_d"), rs.getString("correct_ans"),
                    rs.getString("explanation"), rs.getString("difficulty")));
        } catch (SQLException e) { System.err.println("Boss battle fetch error: " + e.getMessage()); }
        return questions;
    }

    private void stopBossTimer() {
        if (bossQuestionTimer != null) { bossQuestionTimer.stop(); bossQuestionTimer = null; }
    }

    private void showBossBattle() {
        stopBossTimer();
        contentPanel.removeAll();
        JPanel intro = new JPanel();
        intro.setBackground(new Color(0x0d1520));
        intro.setLayout(new BoxLayout(intro, BoxLayout.Y_AXIS));
        intro.setBorder(BorderFactory.createEmptyBorder(60, 40, 60, 40));

        JLabel title = new JLabel("BOSS BATTLE");
        title.setFont(new Font("Segoe UI", Font.BOLD, 36));
        title.setForeground(new Color(0xef4444));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel subtitle = new JLabel("20 Questions. 30 seconds each. Can you survive?");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(Color.WHITE);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton startBtn = new JButton("START BATTLE");
        startBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        startBtn.setBackground(new Color(0xef4444));
        startBtn.setForeground(Color.WHITE);
        startBtn.setFocusPainted(false);
        startBtn.setBorderPainted(false);
        startBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        startBtn.setMaximumSize(new Dimension(220, 50));

        intro.add(title);
        intro.add(Box.createVerticalStrut(20));
        intro.add(subtitle);
        intro.add(Box.createVerticalStrut(40));
        intro.add(startBtn);
        contentPanel.add(intro);

        startBtn.addActionListener(e -> {
            List<Question> questions = new ArrayList<>(fetchBossQuestions());
            if (questions.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No questions available.", "Boss Battle", JOptionPane.WARNING_MESSAGE);
                return;
            }
            while (questions.size() < 20) {
                List<Question> more = fetchBossQuestions();
                if (more.isEmpty()) break;
                questions.addAll(more);
            }
            if (questions.size() > 20) questions = new ArrayList<>(questions.subList(0, 20));
            startBossBattle(questions);
        });

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void startBossBattle(List<Question> questions) {
        final int[] index = {0}, correct = {0}, wrong = {0}, xpEarned = {0}, secondsLeft = {30};
        final boolean[] answered = {false};

        contentPanel.removeAll();
        JPanel battlePanel = new JPanel();
        battlePanel.setBackground(new Color(0x0d1520));
        battlePanel.setLayout(new BoxLayout(battlePanel, BoxLayout.Y_AXIS));
        battlePanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JLabel qNumLbl = new JLabel();
        qNumLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        qNumLbl.setForeground(Color.WHITE);
        qNumLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel timerLbl = new JLabel("30");
        timerLbl.setFont(new Font("Segoe UI", Font.BOLD, 48));
        timerLbl.setForeground(new Color(0xef4444));
        timerLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel qTextLbl = new JLabel();
        qTextLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        qTextLbl.setForeground(Color.WHITE);
        qTextLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel optionsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        optionsPanel.setBackground(new Color(0x0d1520));
        optionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        optionsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        battlePanel.add(qNumLbl);
        battlePanel.add(Box.createVerticalStrut(10));
        battlePanel.add(timerLbl);
        battlePanel.add(Box.createVerticalStrut(15));
        battlePanel.add(qTextLbl);
        battlePanel.add(Box.createVerticalStrut(20));
        battlePanel.add(optionsPanel);
        contentPanel.add(battlePanel);

        Runnable showQuestion = new Runnable() {
            public void run() {
                stopBossTimer();
                if (index[0] >= questions.size()) { showBossResults(correct[0], wrong[0], xpEarned[0]); return; }
                answered[0] = false;
                secondsLeft[0] = 30;
                Question q = questions.get(index[0]);
                qNumLbl.setText("Question " + (index[0] + 1) + "/20");
                qTextLbl.setText("<html><div style='width:620px;'>" + q.getQuestionText() + "</div></html>");
                timerLbl.setText("30");
                optionsPanel.removeAll();
                String[] labels = {"A", "B", "C", "D"};
                String[] opts = {q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD()};
                for (int i = 0; i < 4; i++) {
                    JButton optBtn = new JButton(labels[i] + ". " + opts[i]);
                    optBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    optBtn.setBackground(new Color(0x0d1520));
                    optBtn.setForeground(Color.WHITE);
                    optBtn.setFocusPainted(false);
                    optBtn.setBorderPainted(false);
                    String letter = labels[i];
                    optBtn.addActionListener(ev -> {
                        if (answered[0]) return;
                        answered[0] = true;
                        stopBossTimer();
                        if (letter.equals(q.getCorrectAns())) { correct[0]++; xpEarned[0] += 10; } else { wrong[0]++; }
                        index[0]++;
                        run();
                    });
                    optionsPanel.add(optBtn);
                }
                optionsPanel.revalidate();
                optionsPanel.repaint();
                bossQuestionTimer = new Timer(1000, ev -> {
                    secondsLeft[0]--;
                    timerLbl.setText(String.valueOf(secondsLeft[0]));
                    if (secondsLeft[0] <= 0 && !answered[0]) {
                        answered[0] = true; wrong[0]++; index[0]++;
                        stopBossTimer(); run();
                    }
                });
                bossQuestionTimer.start();
            }
        };
        showQuestion.run();
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showBossResults(int correct, int wrong, int xpEarned) {
        stopBossTimer();
        String grade = correct <= 5 ? "Defeated" : correct <= 10 ? "Survived" : correct <= 15 ? "Fighter" : "LEGEND";
        if (xpEarned > 0) {
            int newXp = XPService.addXP(currentUser.getUserId(), currentUser.getTotalXp(), xpEarned);
            currentUser.setTotalXp(newXp);
        }
        contentPanel.removeAll();
        JPanel results = new JPanel();
        results.setBackground(new Color(0x0d1520));
        results.setLayout(new BoxLayout(results, BoxLayout.Y_AXIS));
        results.setBorder(BorderFactory.createEmptyBorder(50, 40, 50, 40));
        JLabel title = new JLabel("BATTLE COMPLETE!");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(new Color(0xef4444));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel score = new JLabel("Score: " + correct + "/20 correct");
        score.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        score.setForeground(Color.WHITE);
        score.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel xpLbl = new JLabel("Total XP earned: " + xpEarned);
        xpLbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        xpLbl.setForeground(Color.WHITE);
        xpLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel gradeLbl = new JLabel("Grade: " + grade);
        gradeLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        gradeLbl.setForeground(new Color(255, 215, 0));
        gradeLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton playAgain = new JButton("Play Again");
        JButton dashboard = new JButton("Return to Dashboard");
        for (JButton b : new JButton[]{playAgain, dashboard}) {
            b.setFont(new Font("Segoe UI", Font.BOLD, 14));
            b.setBackground(new Color(0x0ea5e9));
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.setMaximumSize(new Dimension(220, 42));
        }
        playAgain.addActionListener(e -> showBossBattle());
        dashboard.addActionListener(e -> showDashboard());
        results.add(title); results.add(Box.createVerticalStrut(20));
        results.add(score); results.add(Box.createVerticalStrut(10));
        results.add(xpLbl); results.add(Box.createVerticalStrut(10));
        results.add(gradeLbl); results.add(Box.createVerticalStrut(30));
        results.add(playAgain); results.add(Box.createVerticalStrut(10));
        results.add(dashboard);
        contentPanel.add(results);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // Ã¢â€â‚¬Ã¢â€â‚¬ CSV IMPORT Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬
    private int importQuestionsFromCsv(File file) {
        int imported = 0;
        String sql = "INSERT INTO questions (level_id, question_text, option_a, option_b, option_c, option_d, correct_ans, explanation, difficulty) VALUES (?,?,?,?,?,?,?,?,?)";
        try (BufferedReader reader = new BufferedReader(new FileReader(file));
             Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String line;
            boolean first = true;
            while ((line = reader.readLine()) != null) {
                if (first) { first = false; continue; }
                if (line.trim().isEmpty()) continue;
                try {
                    String[] parts = line.split(",", -1);
                    if (parts.length < 9) continue;
                    ps.setInt(1, Integer.parseInt(parts[0].trim()));
                    for (int i = 1; i < 9; i++) ps.setString(i + 1, parts[i].trim());
                    ps.executeUpdate();
                    imported++;
                } catch (Exception ex) { System.err.println("CSV line import error: " + ex.getMessage()); }
            }
        } catch (IOException | SQLException e) { System.err.println("CSV import error: " + e.getMessage()); }
        return imported;
    }

    // Ã¢â€â‚¬Ã¢â€â‚¬ GLOSSARY Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬
    private void showGlossary() {
        contentPanel.removeAll();
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(new Color(0x0d1520));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        JLabel title = new JLabel("Tech Glossary");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        panel.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setBackground(new Color(0x0d1520));
        JTextField searchField = new JTextField("Search terms...");
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBackground(new Color(0x0d1520));
        searchField.setForeground(new Color(150, 150, 180));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x0ea5e9)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search terms...")) { searchField.setText(""); searchField.setForeground(Color.WHITE); }
            }
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) { searchField.setText("Search terms..."); searchField.setForeground(new Color(150, 150, 180)); }
            }
        });

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String term : GLOSSARY_TERMS) listModel.addElement(term);
        JList<String> termList = new JList<>(listModel);
        termList.setBackground(new Color(0x0d1520));
        termList.setForeground(Color.WHITE);
        termList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        termList.setSelectionBackground(new Color(0x0ea5e9));
        termList.setSelectionForeground(Color.WHITE);
        JScrollPane listScroll = new JScrollPane(termList);
        listScroll.setPreferredSize(new Dimension(0, 280));
        listScroll.getViewport().setBackground(new Color(0x0d1520));
        listScroll.setBorder(BorderFactory.createLineBorder(new Color(0x0ea5e9)));

        JLabel defPanel = new JLabel("Select a term to view its definition.");
        defPanel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        defPanel.setForeground(new Color(0xb8c4cc));
        defPanel.setBackground(new Color(0x0d1520));
        defPanel.setOpaque(true);
        defPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x0ea5e9)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        termList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = termList.getSelectedValue();
                if (selected != null) defPanel.setText("<html><div style='width:600px;'>" + selected + "</div></html>");
            }
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            private void filter() {
                String query = searchField.getText();
                if (query.equals("Search terms...")) query = "";
                query = query.trim().toLowerCase();
                listModel.clear();
                for (String term : GLOSSARY_TERMS)
                    if (query.isEmpty() || term.toLowerCase().contains(query)) listModel.addElement(term);
            }
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
        });

        center.add(searchField, BorderLayout.NORTH);
        center.add(listScroll, BorderLayout.CENTER);
        center.add(defPanel, BorderLayout.SOUTH);
        panel.add(center, BorderLayout.CENTER);
        contentPanel.add(panel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // Ã¢â€â‚¬Ã¢â€â‚¬ MINI GAMES Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬
    private void showMiniGames() {
        contentPanel.removeAll();
        JPanel panel = new JPanel();
        panel.setBackground(new Color(0x0d1520));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("Mini Games");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel subtitle = new JLabel("Learn by playing!");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(150, 150, 180));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(6));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(24));

        JPanel grid = new JPanel(new GridLayout(2, 3, 20, 20));
        grid.setBackground(new Color(0x0d1520));
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 520));
        grid.add(createMiniGameCard("Word Scramble", new Color(0x0ea5e9),
            "Unscramble coding terms", this::showWordScramble));
        grid.add(createMiniGameCard("Rapid Fire", new Color(0x0ea5e9),
            "True or False in 5 seconds", this::showRapidFire));
        grid.add(createMiniGameCard("Mystery Language", new Color(30, 144, 255),
            "Guess the language from code", this::showMysteryLanguage));
        grid.add(createMiniGameCard("Boss Battle", new Color(0x0ea5e9),
            "20 questions survival mode", this::showBossBattle));
        grid.add(createMiniGameCard("Code Breaker", new Color(50, 200, 80),
            "Fix the broken code!", this::showCodeBreaker));
        panel.add(grid);

        contentPanel.add(panel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createMiniGameCard(String name, Color borderColor, String desc, Runnable onPlay) {
        JPanel card = new JPanel();
        card.setBackground(new Color(0x0d1520));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(4, 0, 0, 0, borderColor),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        JLabel titleLbl = new JLabel(name);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLbl.setForeground(Color.WHITE);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel descLbl = new JLabel(desc);
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLbl.setForeground(new Color(150, 150, 180));
        descLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton playBtn = new JButton("PLAY");
        playBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        playBtn.setBackground(borderColor);
        playBtn.setForeground(Color.WHITE);
        playBtn.setFocusPainted(false);
        playBtn.setBorderPainted(false);
        playBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        playBtn.setMaximumSize(new Dimension(100, 34));
        playBtn.addActionListener(e -> onPlay.run());

        card.add(titleLbl);
        card.add(Box.createVerticalStrut(8));
        card.add(descLbl);
        card.add(Box.createVerticalStrut(16));
        card.add(playBtn);
        return card;
    }

    // Ã¢â€â‚¬Ã¢â€â‚¬ XP SHOP Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬
    private void showXPShop() {
        contentPanel.removeAll();
        Color bg = new Color(0x0d1520);
        Color cardBg = new Color(0x0d1520);
        Color gold = new Color(255, 215, 0);
        Color border = new Color(0x0ea5e9);

        JPanel panel = new JPanel();
        panel.setBackground(bg);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("XP Shop");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel subtitle = new JLabel("Spend your XP on rewards!");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(150, 150, 180));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel xpLbl = new JLabel("Your XP: " + currentUser.getTotalXp());
        xpLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        xpLbl.setForeground(gold);
        xpLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(6));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(12));
        panel.add(xpLbl);
        panel.add(Box.createVerticalStrut(24));

        JPanel grid = new JPanel(new GridLayout(2, 3, 16, 16));
        grid.setBackground(bg);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 380));

        Object[][] shopItems = {
            {"Streak Freeze", "Protect your streak for 1 day", 50},
            {"Double XP Boost", "Earn 2x XP for next 5 quizzes", 100},
            {"Gold Badge", "Show gold badge on profile", 150},
            {"Dark Ninja Theme", "Unlock dark ninja sidebar theme", 200},
            {"Legend Title", "Show LEGEND title next to name", 300},
            {"Hint Pack", "Get 10 free hints in Word Scramble", 75}
        };

        JLabel shopMsg = new JLabel(" ");
        shopMsg.setFont(new Font("Segoe UI", Font.BOLD, 13));
        shopMsg.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (Object[] item : shopItems) {
            String name = (String) item[0];
            String desc = (String) item[1];
            int cost = (Integer) item[2];
            JPanel card = new JPanel();
            card.setBackground(cardBg);
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(border, 1),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)));

            JLabel nameLbl = new JLabel(name);
            nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
            nameLbl.setForeground(Color.WHITE);
            nameLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            JLabel descLbl = new JLabel("<html><div style='width:160px;'>" + desc + "</div></html>");
            descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            descLbl.setForeground(new Color(150, 150, 180));
            descLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            JLabel costLbl = new JLabel("Cost: " + cost + " XP");
            costLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            costLbl.setForeground(gold);
            costLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

            card.add(nameLbl);
            card.add(Box.createVerticalStrut(6));
            card.add(descLbl);
            card.add(Box.createVerticalStrut(8));
            card.add(costLbl);
            card.add(Box.createVerticalStrut(10));

            if (purchasedItems.contains(name)) {
                JLabel owned = new JLabel("Owned");
                owned.setFont(new Font("Segoe UI", Font.BOLD, 12));
                owned.setForeground(new Color(16, 185, 129));
                owned.setAlignmentX(Component.LEFT_ALIGNMENT);
                card.add(owned);
            } else {
                JButton buyBtn = styleGameBtn("Buy", new Color(0x0ea5e9));
                buyBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
                buyBtn.setMaximumSize(new Dimension(80, 30));
                buyBtn.addActionListener(e -> {
                    if (currentUser.getTotalXp() < cost) {
                        shopMsg.setText("Not enough XP!");
                        shopMsg.setForeground(new Color(239, 68, 68));
                        return;
                    }
                    boolean ok = purchaseShopItem(name, cost, () -> {
                        switch (name) {
                            case "Streak Freeze": streakFreezeActive = true; break;
                            case "Double XP Boost":
                                doubleXPActive = true;
                                doubleXPQuizzesLeft = 5;
                                break;
                            case "Hint Pack": hintsRemaining += 10; break;
                            case "Dark Ninja Theme": applyTheme(isDarkMode); break;
                            default: break;
                        }
                    });
                    if (ok) {
                        shopMsg.setText("Purchased! " + name);
                        shopMsg.setForeground(new Color(16, 185, 129));
                        showXPShop();
                    }
                });
                card.add(buyBtn);
            }
            grid.add(card);
        }

        panel.add(grid);
        panel.add(Box.createVerticalStrut(16));
        panel.add(shopMsg);

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(bg);
        contentPanel.add(scroll);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // Ã¢â€â‚¬Ã¢â€â‚¬ CODE BREAKER Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬
    private static final String[][] CODE_BREAKER_CHALLENGES = {
        {"public class Hello {\n    public static void main(String[] args) {\n        System.out.println(Hello World)\n    }\n}",
            "Add semicolon and quotes around Hello World", "Hello World"},
        {"for(int i=0 i<5; i++) {\n    System.out.println(i);\n}",
            "Missing semicolon after i=0", "i=0;"},
        {"int x = 10\nint y = 20;\nSystem.out.println(x+y);",
            "Missing semicolon after int x = 10", "x = 10;"},
        {"if(x > 5)\n    System.out.println(Big);\nelse\n    System.out.println(Small)\n}",
            "Missing semicolon after println(Small)", "Small);"},
        {"String name = John;\nSystem.out.println(name);",
            "String value needs double quotes", "\"John\""},
        {"int arr[] = {1,2,3,4,5};\nSystem.out.println(arr[5]);",
            "Array index out of bounds, last index is 4", "arr[4]"},
        {"while(true) {\n    System.out.println(Running);\n}",
            "Infinite loop - add a break condition", "break;"},
        {"public int add(int a, int b) {\n    int result = a + b;\n}",
            "Missing return statement", "return result;"},
        {"class Dog extend Animal {\n    void bark() {}\n}",
            "extends keyword is wrong", "extends"},
        {"ArrayList list = new ArrayList();\nlist.add(Hello);\nlist.get(1);",
            "Index 1 doesn't exist, use index 0", "list.get(0)"}
    };

    private void showCodeBreaker() {
        stopBossTimer();
        contentPanel.removeAll();
        Color bg = new Color(0x0d1520);
        Color codeBg = new Color(0x0d1117);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(bg);
        main.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel top = new JPanel();
        top.setBackground(bg);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Code Breaker");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel subtitle = new JLabel("Fix the broken code!");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(150, 150, 180));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        top.add(title);
        top.add(Box.createVerticalStrut(6));
        top.add(subtitle);
        main.add(top, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setBackground(bg);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

        JTextArea codeArea = new JTextArea(10, 60);
        codeArea.setFont(new Font("Courier New", Font.PLAIN, 14));
        codeArea.setBackground(codeBg);
        codeArea.setForeground(Color.WHITE);
        codeArea.setCaretColor(Color.WHITE);
        codeArea.setLineWrap(true);
        codeArea.setWrapStyleWord(true);
        codeArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        codeArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        JScrollPane codeScroll = new JScrollPane(codeArea);
        codeScroll.setBorder(BorderFactory.createLineBorder(new Color(0x1e293b)));
        codeScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        codeScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        JLabel timerLbl = new JLabel("60");
        timerLbl.setFont(new Font("Segoe UI", Font.BOLD, 28));
        timerLbl.setForeground(new Color(0xef4444));
        timerLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel progressLbl = new JLabel("Challenge 1 of 10");
        progressLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        progressLbl.setForeground(new Color(150, 150, 180));
        progressLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel scoreLbl = new JLabel("Score: 0/10");
        scoreLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        scoreLbl.setForeground(new Color(150, 150, 180));
        scoreLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel feedbackLbl = new JLabel(" ");
        feedbackLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        feedbackLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel hintLbl = new JLabel(" ");
        hintLbl.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        hintLbl.setForeground(new Color(150, 150, 180));
        hintLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton hintBtn = styleGameBtn("Hint", new Color(245, 158, 11));
        JButton checkBtn = styleGameBtn("Check Answer", new Color(0x0ea5e9));
        JButton skipBtn = styleGameBtn("Skip", new Color(100, 100, 120));
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setBackground(bg);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRow.add(hintBtn);
        btnRow.add(checkBtn);
        btnRow.add(skipBtn);

        center.add(timerLbl);
        center.add(Box.createVerticalStrut(6));
        center.add(progressLbl);
        center.add(Box.createVerticalStrut(4));
        center.add(scoreLbl);
        center.add(Box.createVerticalStrut(12));
        center.add(codeScroll);
        center.add(Box.createVerticalStrut(12));
        center.add(btnRow);
        center.add(Box.createVerticalStrut(10));
        center.add(hintLbl);
        center.add(Box.createVerticalStrut(6));
        center.add(feedbackLbl);
        main.add(center, BorderLayout.CENTER);

        final int[] index = {0};
        final int[] correct = {0};
        final int[] totalXp = {0};
        final int[] wrongAttempts = {0};
        final int[] secondsLeft = {60};

        Runnable showResults = () -> {
            stopBossTimer();
            contentPanel.removeAll();
            JPanel results = new JPanel();
            results.setBackground(bg);
            results.setLayout(new BoxLayout(results, BoxLayout.Y_AXIS));
            results.setBorder(BorderFactory.createEmptyBorder(60, 40, 60, 40));
            JLabel resTitle = new JLabel("Code Breaker Complete");
            resTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
            resTitle.setForeground(Color.WHITE);
            resTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel resScore = new JLabel(correct[0] + "/10 fixed. Total XP: " + totalXp[0]);
            resScore.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            resScore.setForeground(new Color(150, 150, 180));
            resScore.setAlignmentX(Component.CENTER_ALIGNMENT);
            JButton playAgain = styleGameBtn("Play Again", new Color(50, 200, 80));
            playAgain.setAlignmentX(Component.CENTER_ALIGNMENT);
            playAgain.setMaximumSize(new Dimension(180, 44));
            playAgain.addActionListener(e -> showCodeBreaker());
            results.add(resTitle);
            results.add(Box.createVerticalStrut(16));
            results.add(resScore);
            results.add(Box.createVerticalStrut(30));
            results.add(playAgain);
            contentPanel.add(results);
            contentPanel.revalidate();
            contentPanel.repaint();
        };

        Runnable loadChallenge = new Runnable() {
            public void run() {
                stopBossTimer();
                if (index[0] >= CODE_BREAKER_CHALLENGES.length) {
                    showResults.run();
                    return;
                }
                wrongAttempts[0] = 0;
                secondsLeft[0] = 60;
                timerLbl.setText("60");
                hintLbl.setText(" ");
                feedbackLbl.setText(" ");
                progressLbl.setText("Challenge " + (index[0] + 1) + " of 10");
                scoreLbl.setText("Score: " + correct[0] + "/10");
                codeArea.setText(CODE_BREAKER_CHALLENGES[index[0]][0]);
                codeArea.setEnabled(true);
                hintBtn.setEnabled(true);
                checkBtn.setEnabled(true);
                skipBtn.setEnabled(true);

                bossQuestionTimer = new Timer(1000, ev -> {
                    secondsLeft[0]--;
                    timerLbl.setText(String.valueOf(secondsLeft[0]));
                    if (secondsLeft[0] <= 0) {
                        stopBossTimer();
                        feedbackLbl.setText("Time's up!");
                        feedbackLbl.setForeground(new Color(239, 68, 68));
                        codeArea.setEnabled(false);
                        hintBtn.setEnabled(false);
                        checkBtn.setEnabled(false);
                        skipBtn.setEnabled(false);
                        new Timer(800, ev2 -> {
                            ((Timer) ev2.getSource()).stop();
                            index[0]++;
                            run();
                        }).start();
                    }
                });
                bossQuestionTimer.start();
            }
        };

        Runnable advanceChallenge = () -> {
            index[0]++;
            Color flash = new Color(16, 80, 50);
            main.setBackground(flash);
            center.setBackground(flash);
            new Timer(400, ev -> {
                ((Timer) ev.getSource()).stop();
                main.setBackground(bg);
                center.setBackground(bg);
                loadChallenge.run();
            }).start();
        };

        hintBtn.addActionListener(e ->
            hintLbl.setText("Hint: " + CODE_BREAKER_CHALLENGES[index[0]][1]));

        checkBtn.addActionListener(e -> {
            String code = codeArea.getText();
            String keyword = CODE_BREAKER_CHALLENGES[index[0]][2];
            if (code.contains(keyword)) {
                stopBossTimer();
                correct[0]++;
                int xp = awardXP(15);
                totalXp[0] += xp;
                feedbackLbl.setText("+15 XP");
                feedbackLbl.setForeground(new Color(16, 185, 129));
                scoreLbl.setText("Score: " + correct[0] + "/10");
                codeArea.setEnabled(false);
                hintBtn.setEnabled(false);
                checkBtn.setEnabled(false);
                skipBtn.setEnabled(false);
                new Timer(600, ev -> { ((Timer) ev.getSource()).stop(); advanceChallenge.run(); }).start();
            } else {
                wrongAttempts[0]++;
                feedbackLbl.setText("Not quite! Try again");
                feedbackLbl.setForeground(new Color(239, 68, 68));
                if (wrongAttempts[0] >= 2) {
                    hintLbl.setText("Hint: " + CODE_BREAKER_CHALLENGES[index[0]][1]);
                }
            }
        });

        skipBtn.addActionListener(e -> {
            stopBossTimer();
            feedbackLbl.setText("Skipped");
            feedbackLbl.setForeground(new Color(239, 68, 68));
            codeArea.setEnabled(false);
            hintBtn.setEnabled(false);
            checkBtn.setEnabled(false);
            skipBtn.setEnabled(false);
            new Timer(600, ev -> { ((Timer) ev.getSource()).stop(); advanceChallenge.run(); }).start();
        });

        contentPanel.add(main);
        loadChallenge.run();
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // Ã¢â€â‚¬Ã¢â€â‚¬ CHALLENGE A FRIEND Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬
    private void showChallenge() {
        stopBossTimer();
        contentPanel.removeAll();
        Color bg = new Color(0x0d1520);
        JPanel setup = new JPanel();
        setup.setBackground(bg);
        setup.setLayout(new BoxLayout(setup, BoxLayout.Y_AXIS));
        setup.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("Challenge a Friend");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        setup.add(title);
        setup.add(Box.createVerticalStrut(24));

        JTextField friendField = new JTextField();
        friendField.setMaximumSize(new Dimension(300, 36));
        friendField.setBackground(new Color(0x0d1520));
        friendField.setForeground(Color.WHITE);
        friendField.setAlignmentX(Component.LEFT_ALIGNMENT);
        setup.add(createLabeledField("Enter friend's username:", friendField));
        setup.add(Box.createVerticalStrut(16));

        List<String[]> langs = loadLanguageOptions();
        String[] langNames = new String[langs.size()];
        for (int i = 0; i < langs.size(); i++) langNames[i] = langs.get(i)[1];
        JComboBox<String> langBox = new JComboBox<>(langNames);
        langBox.setMaximumSize(new Dimension(300, 36));
        langBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        setup.add(createLabeledField("Select Language:", langBox));
        setup.add(Box.createVerticalStrut(16));

        JComboBox<String> numBox = new JComboBox<>(new String[]{"5", "10", "15"});
        numBox.setMaximumSize(new Dimension(300, 36));
        numBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        setup.add(createLabeledField("Number of Questions:", numBox));
        setup.add(Box.createVerticalStrut(24));

        JButton startBtn = styleGameBtn("START CHALLENGE", new Color(0x0ea5e9));
        startBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        startBtn.setMaximumSize(new Dimension(220, 44));
        startBtn.addActionListener(e -> {
            String friendName = friendField.getText().trim();
            if (friendName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter a friend's username.", "Challenge", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Integer friendId = getUserIdByUsername(friendName);
            if (friendId == null) {
                JOptionPane.showMessageDialog(this, "Friend not found.", "Challenge", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (friendId == currentUser.getUserId()) {
                JOptionPane.showMessageDialog(this, "You cannot challenge yourself.", "Challenge", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int langIdx = langBox.getSelectedIndex();
            int langId = Integer.parseInt(langs.get(langIdx)[0]);
            int numQ = Integer.parseInt((String) numBox.getSelectedItem());
            startChallengeQuiz(friendName, friendId, langId, numQ);
        });
        setup.add(startBtn);

        contentPanel.add(setup);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void startChallengeQuiz(String friendName, int friendId, int langId, int numQuestions) {
        List<Question> questions = QuestionDAO.getQuestionsByLang(langId);
        if (questions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No questions available for this language.", "Challenge", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (questions.size() > numQuestions) {
            questions = new ArrayList<>(questions.subList(0, numQuestions));
        }

        final List<Question> gameQuestions = questions;
        final int[] index = {0};
        final int[] score = {0};
        final long startTime = System.currentTimeMillis();

        contentPanel.removeAll();
        Color bg = new Color(0x0d1520);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bg);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JLabel banner = new JLabel("Your Turn!");
        banner.setFont(new Font("Segoe UI", Font.BOLD, 28));
        banner.setForeground(new Color(0x0ea5e9));
        panel.add(banner, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setBackground(bg);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        JLabel qNumLbl = new JLabel();
        qNumLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        qNumLbl.setForeground(new Color(150, 150, 180));
        qNumLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel qTextLbl = new JLabel();
        qTextLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        qTextLbl.setForeground(Color.WHITE);
        qTextLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel optionsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        optionsPanel.setBackground(bg);
        optionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        optionsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        center.add(qNumLbl);
        center.add(Box.createVerticalStrut(16));
        center.add(qTextLbl);
        center.add(Box.createVerticalStrut(20));
        center.add(optionsPanel);
        panel.add(center, BorderLayout.CENTER);
        contentPanel.add(panel);

        Runnable showNext = new Runnable() {
            public void run() {
                if (index[0] >= gameQuestions.size()) {
                    long elapsed = (System.currentTimeMillis() - startTime) / 1000;
                    showChallengeResults(friendName, friendId, score[0], gameQuestions.size(), (int) elapsed);
                    return;
                }
                Question q = gameQuestions.get(index[0]);
                qNumLbl.setText("Question " + (index[0] + 1) + "/" + gameQuestions.size());
                qTextLbl.setText("<html><div style='width:620px;'>" + q.getQuestionText() + "</div></html>");
                optionsPanel.removeAll();
                String[] labels = {"A", "B", "C", "D"};
                String[] opts = {q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD()};
                for (int i = 0; i < 4; i++) {
                    JButton optBtn = new JButton(labels[i] + ". " + opts[i]);
                    optBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    optBtn.setBackground(new Color(0x0d1520));
                    optBtn.setForeground(Color.WHITE);
                    optBtn.setFocusPainted(false);
                    optBtn.setBorderPainted(false);
                    String letter = labels[i];
                    optBtn.addActionListener(ev -> {
                        if (letter.equals(q.getCorrectAns())) score[0]++;
                        index[0]++;
                        run();
                    });
                    optionsPanel.add(optBtn);
                }
                optionsPanel.revalidate();
                optionsPanel.repaint();
            }
        };
        showNext.run();
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showChallengeResults(String friendName, int friendId, int yourScore, int total, int timeSec) {
        int friendScore = getFriendRecentQuizScore(friendId);
        contentPanel.removeAll();
        Color bg = new Color(0x0d1520);
        JPanel results = new JPanel();
        results.setBackground(bg);
        results.setLayout(new BoxLayout(results, BoxLayout.Y_AXIS));
        results.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel yourLbl = new JLabel("Your Score: " + yourScore + "/" + total + " - Time: " + timeSec + "s");
        yourLbl.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        yourLbl.setForeground(Color.WHITE);
        yourLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel friendLbl = new JLabel(friendName + "'s recent score: " + friendScore);
        friendLbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        friendLbl.setForeground(new Color(150, 150, 180));
        friendLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel outcomeLbl = new JLabel();
        outcomeLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        outcomeLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        if (yourScore > friendScore) {
            outcomeLbl.setText("You Win! +20 XP");
            outcomeLbl.setForeground(new Color(16, 185, 129));
            awardXP(20);
        } else if (friendScore > yourScore) {
            outcomeLbl.setText("Friend Wins! Better luck next time");
            outcomeLbl.setForeground(new Color(239, 68, 68));
        } else {
            outcomeLbl.setText("Its a Tie! +10 XP each");
            outcomeLbl.setForeground(new Color(255, 215, 0));
            awardXP(10);
        }

        JButton againBtn = styleGameBtn("Challenge Again", new Color(0x0ea5e9));
        JButton dashBtn = styleGameBtn("Dashboard", new Color(100, 100, 120));
        againBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        dashBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        againBtn.setMaximumSize(new Dimension(180, 44));
        dashBtn.setMaximumSize(new Dimension(180, 44));
        againBtn.addActionListener(e -> showChallenge());
        dashBtn.addActionListener(e -> showDashboard());

        results.add(yourLbl);
        results.add(Box.createVerticalStrut(12));
        results.add(friendLbl);
        results.add(Box.createVerticalStrut(20));
        results.add(outcomeLbl);
        results.add(Box.createVerticalStrut(30));
        results.add(againBtn);
        results.add(Box.createVerticalStrut(10));
        results.add(dashBtn);

        contentPanel.add(results);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // Ã¢â€â‚¬Ã¢â€â‚¬ WORD SCRAMBLE Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬
    private static final String[][] SCRAMBLE_WORDS_DATA = {
        {"NOITPECXE", "EXCEPTION", "Error in program execution"},
        {"ELBAIRAIV", "VARIABLE", "Stores data in memory"},
        {"NOITCNUF", "FUNCTION", "Reusable block of code"},
        {"SSALC", "CLASS", "Blueprint for objects"},
        {"TNEMUGRA", "ARGUMENT", "Value passed to function"},
        {"NAELOB", "BOOLEAN", "True or false value"},
        {"YARRA", "ARRAY", "Collection of elements"},
        {"POOL", "LOOP", "Repeats code block"},
        {"TROS", "SORT", "Arrange in order"},
        {"RETNIOP", "POINTER", "Stores memory address"}
    };

    private void showWordScramble() {
        contentPanel.removeAll();
        Color bg = new Color(0x0d1520);
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(bg);

        JPanel topPanel = new JPanel();
        topPanel.setBackground(bg);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 10, 25));
        JLabel title = new JLabel("Word Scramble");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel subtitle = new JLabel("Unscramble the programming term!");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(150, 150, 180));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(title);
        topPanel.add(Box.createVerticalStrut(6));
        topPanel.add(subtitle);
        mainContent.add(topPanel, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setBackground(bg);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(20, 25, 25, 25));

        JLabel scrambledLbl = new JLabel(SCRAMBLE_WORDS_DATA[0][0]);
        scrambledLbl.setFont(new Font("Courier New", Font.BOLD, 40));
        scrambledLbl.setForeground(new Color(0x0ea5e9));
        scrambledLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel hintLbl = new JLabel(SCRAMBLE_WORDS_DATA[0][2]);
        hintLbl.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        hintLbl.setForeground(new Color(150, 150, 180));
        hintLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField answerField = new JTextField();
        answerField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        answerField.setBackground(new Color(0x0d1520));
        answerField.setForeground(Color.WHITE);
        answerField.setMaximumSize(new Dimension(300, 44));
        answerField.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel feedbackLbl = new JLabel(" ");
        feedbackLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        feedbackLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel scoreLbl = new JLabel("0 correct out of 0");
        scoreLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        scoreLbl.setForeground(new Color(150, 150, 180));
        scoreLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel progressLbl = new JLabel("Word 1 of 10");
        progressLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        progressLbl.setForeground(new Color(150, 150, 180));
        progressLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton submitBtn = styleGameBtn("Submit", new Color(0x0ea5e9));
        JButton skipBtn = styleGameBtn("Skip", new Color(100, 100, 120));
        JButton hintBtn = styleGameBtn("Hint -2 XP", new Color(245, 158, 11));
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnRow.setBackground(bg);
        btnRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRow.add(submitBtn);
        btnRow.add(skipBtn);
        btnRow.add(hintBtn);

        center.add(scrambledLbl);
        center.add(Box.createVerticalStrut(16));
        center.add(hintLbl);
        center.add(Box.createVerticalStrut(24));
        center.add(answerField);
        center.add(Box.createVerticalStrut(20));
        center.add(btnRow);
        center.add(Box.createVerticalStrut(16));
        center.add(feedbackLbl);
        center.add(Box.createVerticalStrut(12));
        center.add(scoreLbl);
        center.add(Box.createVerticalStrut(6));
        center.add(progressLbl);
        mainContent.add(center, BorderLayout.CENTER);

        final int[] wordIndex = {0};
        final int[] correctCount = {0};
        final int[] attemptedCount = {0};
        final int[] totalXp = {0};
        final boolean[] hintUsed = {false};
        final boolean[] wordDone = {false};

        Runnable showGameOver = () -> {
            contentPanel.removeAll();
            JPanel over = new JPanel();
            over.setBackground(bg);
            over.setLayout(new BoxLayout(over, BoxLayout.Y_AXIS));
            over.setBorder(BorderFactory.createEmptyBorder(60, 40, 60, 40));
            JLabel overTitle = new JLabel("Game Over!");
            overTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
            overTitle.setForeground(Color.WHITE);
            overTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel overScore = new JLabel(correctCount[0] + "/10 correct. Total XP: " + totalXp[0]);
            overScore.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            overScore.setForeground(new Color(150, 150, 180));
            overScore.setAlignmentX(Component.CENTER_ALIGNMENT);
            JButton playAgain = styleGameBtn("Play Again", new Color(0x0ea5e9));
            playAgain.setAlignmentX(Component.CENTER_ALIGNMENT);
            playAgain.setMaximumSize(new Dimension(180, 44));
            playAgain.addActionListener(e -> showWordScramble());
            over.add(overTitle);
            over.add(Box.createVerticalStrut(20));
            over.add(overScore);
            over.add(Box.createVerticalStrut(30));
            over.add(playAgain);
            contentPanel.add(over);
            contentPanel.revalidate();
            contentPanel.repaint();
        };

        Runnable loadWord = () -> {
            if (wordIndex[0] >= SCRAMBLE_WORDS_DATA.length) {
                showGameOver.run();
                return;
            }
            wordDone[0] = false;
            hintUsed[0] = false;
            String[] word = SCRAMBLE_WORDS_DATA[wordIndex[0]];
            scrambledLbl.setText(word[0]);
            hintLbl.setText(word[2]);
            answerField.setText("");
            feedbackLbl.setText(" ");
            progressLbl.setText("Word " + (wordIndex[0] + 1) + " of 10");
            scoreLbl.setText(correctCount[0] + " correct out of " + attemptedCount[0]);
            answerField.setEnabled(true);
            submitBtn.setEnabled(true);
            skipBtn.setEnabled(true);
            hintBtn.setEnabled(true);
        };

        Runnable advanceWord = () -> {
            wordIndex[0]++;
            if (wordIndex[0] >= SCRAMBLE_WORDS_DATA.length) {
                showGameOver.run();
            } else {
                loadWord.run();
            }
        };

        submitBtn.addActionListener(e -> {
            if (wordDone[0]) return;
            String guess = answerField.getText().trim().toUpperCase();
            String answer = SCRAMBLE_WORDS_DATA[wordIndex[0]][1];
            if (guess.equals(answer)) {
                int xp = hintUsed[0] ? 6 : 8;
                totalXp[0] += xp;
                correctCount[0]++;
                attemptedCount[0]++;
                wordDone[0] = true;
                int newXp = XPService.addXP(currentUser.getUserId(), currentUser.getTotalXp(), xp);
                currentUser.setTotalXp(newXp);
                feedbackLbl.setText("Correct! +" + xp + " XP"); playSound("correct");
                feedbackLbl.setForeground(new Color(16, 185, 129));
                scoreLbl.setText(correctCount[0] + " correct out of " + attemptedCount[0]);
                answerField.setEnabled(false);
                submitBtn.setEnabled(false);
                skipBtn.setEnabled(false);
                hintBtn.setEnabled(false);
                new Timer(800, ev -> { ((Timer) ev.getSource()).stop(); advanceWord.run(); }).start();
            } else {
                feedbackLbl.setText("Wrong!"); playSound("wrong");
                feedbackLbl.setForeground(new Color(239, 68, 68));
            }
        });

        skipBtn.addActionListener(e -> {
            if (wordDone[0]) return;
            wordDone[0] = true;
            attemptedCount[0]++;
            feedbackLbl.setText("Answer: " + SCRAMBLE_WORDS_DATA[wordIndex[0]][1]);
            feedbackLbl.setForeground(new Color(239, 68, 68));
            scoreLbl.setText(correctCount[0] + " correct out of " + attemptedCount[0]);
            answerField.setEnabled(false);
            submitBtn.setEnabled(false);
            skipBtn.setEnabled(false);
            hintBtn.setEnabled(false);
            new Timer(800, ev -> { ((Timer) ev.getSource()).stop(); advanceWord.run(); }).start();
        });

        hintBtn.addActionListener(e -> {
            if (wordDone[0] || hintUsed[0]) return;
            if (hintsRemaining > 0) {
                hintsRemaining--;
                hintLbl.setText(SCRAMBLE_WORDS_DATA[wordIndex[0]][2] + " (Free hint)");
                feedbackLbl.setText("Free hint used Ã¢â‚¬â€ answer still worth 8 XP");
                feedbackLbl.setForeground(new Color(245, 158, 11));
            } else {
                hintUsed[0] = true;
                hintLbl.setText(SCRAMBLE_WORDS_DATA[wordIndex[0]][2] + " (Hint used -2 XP)");
                feedbackLbl.setText("Hint revealed Ã¢â‚¬â€ correct answer worth 6 XP");
                feedbackLbl.setForeground(new Color(245, 158, 11));
            }
        });

        contentPanel.add(mainContent);
        loadWord.run();
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JButton styleGameBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // Ã¢â€â‚¬Ã¢â€â‚¬ RAPID FIRE Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬
    private static final Object[][] RAPID_FIRE_POOL = {
        {"Java is platform independent", true},
        {"Python uses curly braces for blocks", false},
        {"HTML stands for HyperText Markup Language", true},
        {"CSS is a programming language", false},
        {"JVM stands for Java Virtual Machine", true},
        {"Python was created by James Gosling", false},
        {"JavaScript and Java are the same language", false},
        {"Git is a version control system", true},
        {"SQL is used to manage databases", true},
        {"An array stores elements of same type", true},
        {"HTML is a programming language", false},
        {"Python is case sensitive", true},
        {"Recursion means a function calls itself", true},
        {"A compiler converts source code to machine code", true},
        {"C++ is a subset of Java", false}
    };

    private void showRapidFire() {
        stopBossTimer();
        contentPanel.removeAll();
        Color bg = new Color(0x0d1520);
        JPanel intro = new JPanel();
        intro.setBackground(bg);
        intro.setLayout(new BoxLayout(intro, BoxLayout.Y_AXIS));
        intro.setBorder(BorderFactory.createEmptyBorder(80, 40, 80, 40));

        JLabel title = new JLabel("RAPID FIRE");
        title.setFont(new Font("Segoe UI", Font.BOLD, 40));
        title.setForeground(new Color(0xef4444));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel subtitle = new JLabel("10 questions. 5 seconds each. How fast are you?");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(Color.WHITE);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton startBtn = new JButton("START");
        startBtn.setFont(new Font("Segoe UI", Font.BOLD, 22));
        startBtn.setForeground(Color.WHITE);
        startBtn.setFocusPainted(false);
        startBtn.setBorderPainted(false);
        startBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        startBtn.setMaximumSize(new Dimension(200, 56));

        intro.add(title);
        intro.add(Box.createVerticalStrut(20));
        intro.add(subtitle);
        intro.add(Box.createVerticalStrut(40));
        intro.add(startBtn);
        contentPanel.add(intro);

        startBtn.addActionListener(e -> startRapidFireGame());
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void startRapidFireGame() {
        stopBossTimer();
        List<Object[]> questions = new ArrayList<>();
        for (Object[] q : RAPID_FIRE_POOL) questions.add(q);
        Collections.shuffle(questions);
        if (questions.size() > 10) questions = questions.subList(0, 10);

        final List<Object[]> gameQuestions = questions;
        final int[] index = {0};
        final int[] correct = {0};
        final int[] secondsLeft = {5};
        final boolean[] answered = {false};

        contentPanel.removeAll();
        Color bg = new Color(0x0d1520);
        JPanel gamePanel = new JPanel(new BorderLayout());
        gamePanel.setBackground(bg);
        gamePanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 30, 30));

        JLabel qNumLbl = new JLabel("Q 1/10");
        qNumLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        qNumLbl.setForeground(new Color(150, 150, 180));
        qNumLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(bg);
        topBar.add(qNumLbl, BorderLayout.EAST);

        JPanel center = new JPanel();
        center.setBackground(bg);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        JLabel timerLbl = new JLabel("5");
        timerLbl.setFont(new Font("Segoe UI", Font.BOLD, 80));
        timerLbl.setForeground(new Color(0xef4444));
        timerLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel qTextLbl = new JLabel();
        qTextLbl.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        qTextLbl.setForeground(Color.WHITE);
        qTextLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton trueBtn = new JButton("TRUE");
        trueBtn.setFont(new Font("Segoe UI", Font.BOLD, 22));
        trueBtn.setBackground(new Color(16, 185, 129));
        trueBtn.setForeground(Color.WHITE);
        trueBtn.setFocusPainted(false);
        trueBtn.setBorderPainted(false);
        trueBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        JButton falseBtn = new JButton("FALSE");
        falseBtn.setFont(new Font("Segoe UI", Font.BOLD, 22));
        falseBtn.setBackground(new Color(239, 68, 68));
        falseBtn.setForeground(Color.WHITE);
        falseBtn.setFocusPainted(false);
        falseBtn.setBorderPainted(false);
        falseBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(bg);
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
        btnPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnPanel.setMaximumSize(new Dimension(500, 140));
        btnPanel.add(trueBtn);
        btnPanel.add(Box.createVerticalStrut(12));
        btnPanel.add(falseBtn);

        center.add(timerLbl);
        center.add(Box.createVerticalStrut(30));
        center.add(qTextLbl);
        center.add(Box.createVerticalStrut(40));
        center.add(btnPanel);

        gamePanel.add(topBar, BorderLayout.NORTH);
        gamePanel.add(center, BorderLayout.CENTER);
        contentPanel.add(gamePanel);

        Runnable showResults = () -> {
            stopBossTimer();
            int score = correct[0];
            int xpReward;
            String grade;
            if (score >= 8) {
                xpReward = 50;
                grade = "LEGENDARY! +50 XP";
            } else if (score >= 5) {
                xpReward = 25;
                grade = "Good Job! +25 XP";
            } else {
                xpReward = 10;
                grade = "Keep Practicing! +10 XP";
            }
            int newXp = XPService.addXP(currentUser.getUserId(), currentUser.getTotalXp(), xpReward);
            currentUser.setTotalXp(newXp);

            contentPanel.removeAll();
            JPanel results = new JPanel();
            results.setBackground(bg);
            results.setLayout(new BoxLayout(results, BoxLayout.Y_AXIS));
            results.setBorder(BorderFactory.createEmptyBorder(60, 40, 60, 40));
            JLabel resTitle = new JLabel("Rapid Fire Complete");
            resTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
            resTitle.setForeground(Color.WHITE);
            resTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel resScore = new JLabel("Score: " + score + "/10");
            resScore.setFont(new Font("Segoe UI", Font.PLAIN, 20));
            resScore.setForeground(Color.WHITE);
            resScore.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel resGrade = new JLabel(grade);
            resGrade.setFont(new Font("Segoe UI", Font.BOLD, 22));
            resGrade.setForeground(new Color(255, 215, 0));
            resGrade.setAlignmentX(Component.CENTER_ALIGNMENT);
            JButton playAgain = styleGameBtn("Play Again", new Color(0x0ea5e9));
            JButton dashboard = styleGameBtn("Dashboard", new Color(100, 100, 120));
            playAgain.setAlignmentX(Component.CENTER_ALIGNMENT);
            dashboard.setAlignmentX(Component.CENTER_ALIGNMENT);
            playAgain.setMaximumSize(new Dimension(180, 44));
            dashboard.setMaximumSize(new Dimension(180, 44));
            playAgain.addActionListener(ev -> showRapidFire());
            dashboard.addActionListener(ev -> showDashboard());
            results.add(resTitle);
            results.add(Box.createVerticalStrut(16));
            results.add(resScore);
            results.add(Box.createVerticalStrut(12));
            results.add(resGrade);
            results.add(Box.createVerticalStrut(30));
            results.add(playAgain);
            results.add(Box.createVerticalStrut(10));
            results.add(dashboard);
            contentPanel.add(results);
            contentPanel.revalidate();
            contentPanel.repaint();
        };

        final Runnable[] showNextQuestionRef = new Runnable[1];

        Consumer<Boolean> flashAndAdvance = isCorrect -> {
            Color flashColor = isCorrect ? new Color(16, 80, 50) : new Color(80, 20, 20);
            Color original = bg;
            gamePanel.setBackground(flashColor);
            center.setBackground(flashColor);
            topBar.setBackground(flashColor);
            btnPanel.setBackground(flashColor);
            new Timer(400, ev -> {
                ((Timer) ev.getSource()).stop();
                gamePanel.setBackground(original);
                center.setBackground(original);
                topBar.setBackground(original);
                btnPanel.setBackground(original);
                index[0]++;
                showNextQuestionRef[0].run();
            }).start();
        };

        Runnable showNextQuestion = new Runnable() {
            public void run() {
                stopBossTimer();
                if (index[0] >= gameQuestions.size()) {
                    showResults.run();
                    return;
                }
                answered[0] = false;
                secondsLeft[0] = 5;
                Object[] q = gameQuestions.get(index[0]);
                qNumLbl.setText("Q " + (index[0] + 1) + "/10");
                qTextLbl.setText("<html><div style='text-align:center;width:600px;'>" + q[0] + "</div></html>");
                timerLbl.setText("5");
                trueBtn.setEnabled(true);
                falseBtn.setEnabled(true);

                bossQuestionTimer = new Timer(1000, ev -> {
                    secondsLeft[0]--;
                    timerLbl.setText(String.valueOf(secondsLeft[0]));
                    if (secondsLeft[0] <= 0 && !answered[0]) {
                        answered[0] = true;
                        trueBtn.setEnabled(false);
                        falseBtn.setEnabled(false);
                        stopBossTimer();
                        flashAndAdvance.accept(false);
                    }
                });
                bossQuestionTimer.start();
            }
        };
        showNextQuestionRef[0] = showNextQuestion;

        Consumer<Boolean> handleAnswer = userAnswer -> {
            if (answered[0]) return;
            answered[0] = true;
            trueBtn.setEnabled(false);
            falseBtn.setEnabled(false);
            stopBossTimer();
            Object[] q = gameQuestions.get(index[0]);
            boolean correctAns = (Boolean) q[1];
            if (userAnswer == correctAns) correct[0]++;
            flashAndAdvance.accept(userAnswer == correctAns);
        };

        trueBtn.addActionListener(e -> handleAnswer.accept(true));
        falseBtn.addActionListener(e -> handleAnswer.accept(false));

        showNextQuestion.run();
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // Ã¢â€â‚¬Ã¢â€â‚¬ MYSTERY LANGUAGE Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬
    private static final String[][] MYSTERY_SNIPPETS = {
        {"print(Hello)\nfor i in range(5):\n    print(i)", "Python", "Java,C++,JavaScript"},
        {"System.out.println(Hello);\nfor(int i=0;i<5;i++)\n    System.out.println(i);", "Java", "Python,C#,C++"},
        {"cout<<Hello<<endl;\nfor(int i=0;i<5;i++)\n    cout<<i;", "C++", "C,Java,Rust"},
        {"console.log(Hello);\nfor(let i=0;i<5;i++)\n    console.log(i);", "JavaScript", "TypeScript,Java,Python"},
        {"printf(Hello\n);\nfor(int i=0;i<5;i++)\n    printf(d,i);", "C", "C++,Rust,Go"},
        {"fmt.Println(Hello)\nfor i:=0;i<5;i++{\n    fmt.Println(i)}", "Go", "Rust,Swift,Kotlin"},
        {"puts Hello\n5.times{|i| puts i}", "Ruby", "Perl,Python,PHP"}
    };

    private void showMysteryLanguage() {
        stopBossTimer();
        contentPanel.removeAll();
        Color bg = new Color(0x0d1520);
        Color codeBg = new Color(0x0d1117);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(bg);
        main.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel header = new JPanel();
        header.setBackground(bg);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Mystery Language");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel subtitle = new JLabel("Which language is this code?");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(150, 150, 180));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(title);
        header.add(Box.createVerticalStrut(6));
        header.add(subtitle);
        main.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setBackground(bg);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JTextArea codeArea = new JTextArea(8, 50);
        codeArea.setFont(new Font("Courier New", Font.PLAIN, 14));
        codeArea.setBackground(codeBg);
        codeArea.setForeground(Color.WHITE);
        codeArea.setEditable(false);
        codeArea.setLineWrap(true);
        codeArea.setWrapStyleWord(true);
        codeArea.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        codeArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        JScrollPane codeScroll = new JScrollPane(codeArea);
        codeScroll.setBorder(BorderFactory.createLineBorder(new Color(0x1e293b)));
        codeScroll.setAlignmentX(Component.CENTER_ALIGNMENT);
        codeScroll.setMaximumSize(new Dimension(650, 200));

        JPanel optionsPanel = new JPanel(new GridLayout(2, 2, 12, 12));
        optionsPanel.setBackground(bg);
        optionsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        optionsPanel.setMaximumSize(new Dimension(500, 120));

        JLabel feedbackLbl = new JLabel(" ");
        feedbackLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        feedbackLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel comboLbl = new JLabel(" ");
        comboLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        comboLbl.setForeground(new Color(245, 158, 11));
        comboLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel scoreLbl = new JLabel("Score: 0/" + MYSTERY_SNIPPETS.length);
        scoreLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        scoreLbl.setForeground(new Color(150, 150, 180));
        scoreLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel timerLbl = new JLabel("20");
        timerLbl.setFont(new Font("Segoe UI", Font.BOLD, 36));
        timerLbl.setForeground(new Color(0xef4444));
        timerLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        center.add(timerLbl);
        center.add(Box.createVerticalStrut(10));
        center.add(codeScroll);
        center.add(Box.createVerticalStrut(20));
        center.add(optionsPanel);
        center.add(Box.createVerticalStrut(16));
        center.add(feedbackLbl);
        center.add(Box.createVerticalStrut(8));
        center.add(comboLbl);
        center.add(Box.createVerticalStrut(8));
        center.add(scoreLbl);
        main.add(center, BorderLayout.CENTER);

        final int[] index = {0};
        final int[] correct = {0};
        final int[] totalXp = {0};
        final int[] combo = {0};
        final int[] secondsLeft = {20};
        final boolean[] answered = {false};

        Runnable showMysteryResults = () -> {
            stopBossTimer();
            contentPanel.removeAll();
            JPanel results = new JPanel();
            results.setBackground(bg);
            results.setLayout(new BoxLayout(results, BoxLayout.Y_AXIS));
            results.setBorder(BorderFactory.createEmptyBorder(60, 40, 60, 40));
            JLabel resTitle = new JLabel("Mystery Language Complete");
            resTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
            resTitle.setForeground(Color.WHITE);
            resTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel resScore = new JLabel("Score: " + correct[0] + "/" + MYSTERY_SNIPPETS.length);
            resScore.setFont(new Font("Segoe UI", Font.PLAIN, 20));
            resScore.setForeground(Color.WHITE);
            resScore.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel resXp = new JLabel("Total XP: " + totalXp[0]);
            resXp.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            resXp.setForeground(new Color(150, 150, 180));
            resXp.setAlignmentX(Component.CENTER_ALIGNMENT);
            JButton playAgain = styleGameBtn("Play Again", new Color(30, 144, 255));
            playAgain.setAlignmentX(Component.CENTER_ALIGNMENT);
            playAgain.setMaximumSize(new Dimension(180, 44));
            playAgain.addActionListener(e -> showMysteryLanguage());
            results.add(resTitle);
            results.add(Box.createVerticalStrut(16));
            results.add(resScore);
            results.add(Box.createVerticalStrut(10));
            results.add(resXp);
            results.add(Box.createVerticalStrut(30));
            results.add(playAgain);
            contentPanel.add(results);
            contentPanel.revalidate();
            contentPanel.repaint();
        };

        Runnable loadSnippet = new Runnable() {
            public void run() {
                stopBossTimer();
                if (index[0] >= MYSTERY_SNIPPETS.length) {
                    showMysteryResults.run();
                    return;
                }
                answered[0] = false;
                secondsLeft[0] = 20;
                timerLbl.setText("20");
                feedbackLbl.setText(" ");
                comboLbl.setText(combo[0] >= 2 ? "Combo x " + combo[0] + "!" : " ");
                scoreLbl.setText("Score: " + correct[0] + "/" + MYSTERY_SNIPPETS.length);

                String[] snippet = MYSTERY_SNIPPETS[index[0]];
                codeArea.setText(snippet[0]);
                optionsPanel.removeAll();
                List<String> choices = new ArrayList<>();
                choices.add(snippet[1]);
                for (String w : snippet[2].split(",")) choices.add(w.trim());
                Collections.shuffle(choices);

                for (String choice : choices) {
                    JButton optBtn = new JButton(choice);
                    optBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    optBtn.setBackground(new Color(0x0d1520));
                    optBtn.setForeground(Color.WHITE);
                    optBtn.setFocusPainted(false);
                    optBtn.setBorderPainted(false);
                    optBtn.addActionListener(e -> {
                        if (answered[0]) return;
                        answered[0] = true;
                        stopBossTimer();
                        boolean isCorrect = choice.equals(snippet[1]);
                        Color flashColor = isCorrect ? new Color(16, 80, 50) : new Color(80, 20, 20);
                        main.setBackground(flashColor);
                        center.setBackground(flashColor);
                        header.setBackground(flashColor);
                        optionsPanel.setBackground(flashColor);
                        if (isCorrect) {
                            correct[0]++;
                            combo[0]++;
                            totalXp[0] += 12;
                            int newXp = XPService.addXP(currentUser.getUserId(), currentUser.getTotalXp(), 12);
                            currentUser.setTotalXp(newXp);
                            feedbackLbl.setText("Correct! +12 XP");
                            feedbackLbl.setForeground(new Color(16, 185, 129));
                            comboLbl.setText(combo[0] >= 2 ? "Combo x " + combo[0] + "!" : " ");
                        } else {
                            combo[0] = 0;
                            feedbackLbl.setText("Wrong! Answer: " + snippet[1]);
                            feedbackLbl.setForeground(new Color(239, 68, 68));
                            comboLbl.setText(" ");
                        }
                        scoreLbl.setText("Score: " + correct[0] + "/" + MYSTERY_SNIPPETS.length);
                        for (Component c : optionsPanel.getComponents()) c.setEnabled(false);
                        new Timer(1000, ev -> {
                            ((Timer) ev.getSource()).stop();
                            main.setBackground(bg);
                            center.setBackground(bg);
                            header.setBackground(bg);
                            optionsPanel.setBackground(bg);
                            index[0]++;
                            run();
                        }).start();
                    });
                    optionsPanel.add(optBtn);
                }
                optionsPanel.revalidate();
                optionsPanel.repaint();

                bossQuestionTimer = new Timer(1000, ev -> {
                    secondsLeft[0]--;
                    timerLbl.setText(String.valueOf(secondsLeft[0]));
                    if (secondsLeft[0] <= 0 && !answered[0]) {
                        answered[0] = true;
                        stopBossTimer();
                        combo[0] = 0;
                        feedbackLbl.setText("Time's up! Answer: " + snippet[1]);
                        feedbackLbl.setForeground(new Color(239, 68, 68));
                        comboLbl.setText(" ");
                        for (Component c : optionsPanel.getComponents()) c.setEnabled(false);
                        new Timer(1000, ev2 -> {
                            ((Timer) ev2.getSource()).stop();
                            index[0]++;
                            run();
                        }).start();
                    }
                });
                bossQuestionTimer.start();
            }
        };

        contentPanel.add(main);
        loadSnippet.run();
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // Ã¢â€â‚¬Ã¢â€â‚¬ TYPING TEST Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬
    private void showTypingTest() {
        contentPanel.removeAll();
        JPanel panel = new JPanel();
        panel.setBackground(new Color(0x0d1520));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("Typing Speed Test");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));

        final String[] currentSnippet = {TYPING_SNIPPETS[new Random().nextInt(TYPING_SNIPPETS.length)]};
        final long[] startTime = {-1};

        JLabel snippetLabel = new JLabel("<html><pre style='font-family:monospace;color:#c8dcf8;'>" + currentSnippet[0] + "</pre></html>");
        snippetLabel.setFont(new Font("Monospaced", Font.PLAIN, 13));
        snippetLabel.setBackground(new Color(0x0d1520));
        snippetLabel.setOpaque(true);
        snippetLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x0ea5e9)),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        snippetLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        snippetLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        panel.add(snippetLabel);
        panel.add(Box.createVerticalStrut(15));

        JTextField typeField = new JTextField();
        typeField.setFont(new Font("Monospaced", Font.PLAIN, 13));
        typeField.setBackground(new Color(0x0d1520));
        typeField.setForeground(Color.WHITE);
        typeField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x0ea5e9)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        typeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        typeField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(typeField);
        panel.add(Box.createVerticalStrut(15));

        JLabel resultLabel = new JLabel(" ");
        resultLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        resultLabel.setForeground(new Color(16, 185, 129));
        resultLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(resultLabel);
        panel.add(Box.createVerticalStrut(15));

        JButton startBtn = new JButton("Start");
        JButton submitBtn = new JButton("Submit");
        JButton tryAgainBtn = new JButton("Try Again");
        for (JButton b : new JButton[]{startBtn, submitBtn, tryAgainBtn}) {
            b.setFont(new Font("Segoe UI", Font.BOLD, 13));
            b.setBackground(new Color(0x0ea5e9));
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false);
            b.setBorderPainted(false);
        }

        startBtn.addActionListener(e -> {
            startTime[0] = System.currentTimeMillis();
            typeField.setText("");
            typeField.requestFocus();
            resultLabel.setText("Timer started! Type the code and click Submit.");
            resultLabel.setForeground(new Color(0xb8c4cc));
        });

        submitBtn.addActionListener(e -> {
            if (startTime[0] < 0) {
                JOptionPane.showMessageDialog(this, "Click Start before submitting.", "Typing Test", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String typed = typeField.getText();
            double timeSec = (System.currentTimeMillis() - startTime[0]) / 1000.0;
            if (timeSec < 0.1) timeSec = 0.1;
            int wpm = (int) ((typed.length() / 5.0) / (timeSec / 60.0));
            int matches = 0;
            int total = currentSnippet[0].length();
            for (int i = 0; i < Math.min(typed.length(), total); i++)
                if (typed.charAt(i) == currentSnippet[0].charAt(i)) matches++;
            int accuracy = total > 0 ? (int) ((matches / (double) total) * 100) : 0;
            String result = String.format("WPM: %d | Accuracy: %d%% | Time: %.1fs", wpm, accuracy, timeSec);
            resultLabel.setText(result);
            resultLabel.setForeground(new Color(16, 185, 129));
            if (accuracy > 90) {
                int newXp = XPService.addXP(currentUser.getUserId(), currentUser.getTotalXp(), 10);
                currentUser.setTotalXp(newXp);
                JOptionPane.showMessageDialog(this, "Excellent! +10 XP\n" + result, "Typing Test", JOptionPane.INFORMATION_MESSAGE);
            }
            startTime[0] = -1;
        });

        tryAgainBtn.addActionListener(e -> {
            currentSnippet[0] = TYPING_SNIPPETS[new Random().nextInt(TYPING_SNIPPETS.length)];
            snippetLabel.setText("<html><pre style='font-family:monospace;color:#c8dcf8;'>" + currentSnippet[0] + "</pre></html>");
            typeField.setText("");
            startTime[0] = -1;
            resultLabel.setText(" ");
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setBackground(new Color(0x0d1520));
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRow.add(startBtn);
        btnRow.add(submitBtn);
        btnRow.add(tryAgainBtn);
        panel.add(btnRow);

        contentPanel.add(panel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    // ========== BUG SQUASH ==========
    private void showBugSquash() {
        contentPanel.removeAll();
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(0x0d1520));
        main.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel("ðŸ› Bug Squash");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(0x22c55e));
        JLabel sub = new JLabel("  Click the bugs before they escape! Miss 3 = Game Over!");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(0x475569));
        JPanel hdr = new JPanel(); hdr.setOpaque(false);
        hdr.setLayout(new BoxLayout(hdr, BoxLayout.Y_AXIS));
        hdr.add(title); hdr.add(Box.createVerticalStrut(4)); hdr.add(sub);

        JLabel scoreLabel = new JLabel("Score: 0  |  Misses: 0/3  |  Press START!");
        scoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        scoreLabel.setForeground(new Color(0x22c55e));

        JPanel gameArea = new JPanel();
        gameArea.setLayout(null);
        gameArea.setBackground(new Color(0x0d1117));
        gameArea.setBorder(BorderFactory.createLineBorder(new Color(0x22c55e), 1));

        String[] bugEmojis = {"ðŸ› BUG","ðŸ’¥ ERR","âš  NULL","ðŸ”¥ CRASH","âŒ BRK"};
        String[] errorMsgs = {"NullPointerException!","StackOverflow!","OutOfMemory!","Segfault!","404 Bug!"};
        int[] score = {0};
        int[] misses = {0};
        boolean[] running = {false};
        java.util.List<JLabel> activeBugs = new java.util.ArrayList<>();
        javax.swing.Timer[] gameTimer = {null};

        JButton startBtn = new JButton("â–¶ START SQUASHING!");
        startBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        startBtn.setBackground(new Color(0x22c55e));
        startBtn.setForeground(Color.WHITE);
        startBtn.setFocusPainted(false);
        startBtn.setBorderPainted(false);
        startBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        startBtn.addActionListener(e -> {
            if (running[0]) return;
            running[0] = true;
            score[0] = 0; misses[0] = 0;
            gameArea.removeAll();
            gameArea.revalidate(); gameArea.repaint();
            startBtn.setEnabled(false);
            startBtn.setText("ðŸŽ® Playing...");

            gameTimer[0] = new javax.swing.Timer(900, evt -> {
                if (misses[0] >= 3) {
                    gameTimer[0].stop();
                    running[0] = false;
                    gameArea.removeAll();
                    JLabel over = new JLabel("<html><center>ðŸ’€ GAME OVER!<br>Score: " + score[0] + "<br><font size='3'>Bugs won this time!</font></center></html>", SwingConstants.CENTER);
                    over.setFont(new Font("Segoe UI", Font.BOLD, 20));
                    over.setForeground(new Color(0xef4444));
                    over.setBounds(0, 0, gameArea.getWidth(), gameArea.getHeight());
                    gameArea.add(over);
                    gameArea.revalidate(); gameArea.repaint();
                    startBtn.setEnabled(true);
                    startBtn.setText("â–¶ PLAY AGAIN!");
                    return;
                }
                int w = gameArea.getWidth() > 60 ? gameArea.getWidth() - 60 : 300;
                int h = gameArea.getHeight() > 60 ? gameArea.getHeight() - 60 : 200;
                int bx = (int)(Math.random() * w);
                int by = (int)(Math.random() * h);
                int ri = (int)(Math.random() * bugEmojis.length);
                JLabel bug = new JLabel(bugEmojis[ri], SwingConstants.CENTER);
                bug.setFont(new Font("Segoe UI", Font.BOLD, 13)); bug.setForeground(Color.WHITE); bug.setOpaque(true); bug.setBackground(new Color(0xef4444));
                bug.setBounds(bx, by, 80, 40);
                bug.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                bug.setToolTipText(errorMsgs[ri]);
                activeBugs.add(bug);
                gameArea.add(bug);
                gameArea.revalidate(); gameArea.repaint();

                bug.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent me) {
                        if (activeBugs.contains(bug)) {
                            score[0]++;
                            scoreLabel.setText("Score: " + score[0] + "  |  Misses: " + misses[0] + "/3  |  " + errorMsgs[ri]);
                            activeBugs.remove(bug);
                            gameArea.remove(bug);
                            gameArea.revalidate(); gameArea.repaint();
                        }
                    }
                });

                javax.swing.Timer disappear = new javax.swing.Timer(2200, de -> {
                    if (activeBugs.contains(bug)) {
                        misses[0]++;
                        scoreLabel.setText("Score: " + score[0] + "  |  Misses: " + misses[0] + "/3  |  Bug escaped!");
                        activeBugs.remove(bug);
                        gameArea.remove(bug);
                        gameArea.revalidate(); gameArea.repaint();
                    }
                });
                disappear.setRepeats(false);
                disappear.start();
            });
            gameTimer[0].start();
        });

        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        south.setOpaque(false);
        south.add(startBtn);
        south.add(scoreLabel);

        main.add(hdr, BorderLayout.NORTH);
        main.add(gameArea, BorderLayout.CENTER);
        main.add(south, BorderLayout.SOUTH);
        contentPanel.add(main);
        contentPanel.revalidate();
        contentPanel.repaint();
    }










































































































    // ========== BINARY BLITZ ==========
    private void showBinaryBlitz() {
        contentPanel.removeAll();
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(0x0d1520));
        main.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("ðŸ’» Binary Blitz");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(0x0ea5e9));
        JLabel sub = new JLabel("  Convert decimal to binary before time runs out!");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(0x475569));
        JPanel hdr = new JPanel(); hdr.setOpaque(false);
        hdr.setLayout(new BoxLayout(hdr, BoxLayout.Y_AXIS));
        hdr.add(title); hdr.add(Box.createVerticalStrut(4)); hdr.add(sub);

        int[] number = {(int)(Math.random() * 128) + 1};
        int[] score = {0};
        int[] timeLeft = {15};

        JLabel numLabel = new JLabel(String.valueOf(number[0]));
        numLabel.setFont(new Font("Monospaced", Font.BOLD, 72));
        numLabel.setForeground(new Color(0x0ea5e9));
        numLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel timerLabel = new JLabel("â± 15s");
        timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        timerLabel.setForeground(new Color(0xf59e0b));
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        scoreLabel.setForeground(new Color(0x22c55e));
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel feedback = new JLabel(" ");
        feedback.setFont(new Font("Segoe UI", Font.BOLD, 14));
        feedback.setHorizontalAlignment(SwingConstants.CENTER);

        JTextField answerField = new JTextField();
        answerField.setFont(new Font("Monospaced", Font.BOLD, 20));
        answerField.setBackground(new Color(0x0d1520));
        answerField.setForeground(Color.WHITE);
        answerField.setCaretColor(Color.WHITE);
        answerField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x0ea5e9), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        answerField.setHorizontalAlignment(SwingConstants.CENTER);
        answerField.setMaximumSize(new Dimension(300, 45));

        javax.swing.Timer[] countdown = {null};

        Runnable nextQuestion = () -> {
            number[0] = (int)(Math.random() * 128) + 1;
            numLabel.setText(String.valueOf(number[0]));
            answerField.setText("");
            timeLeft[0] = 15;
            timerLabel.setText("â± 15s");
            timerLabel.setForeground(new Color(0xf59e0b));
        };

        countdown[0] = new javax.swing.Timer(1000, e -> {
            timeLeft[0]--;
            timerLabel.setText("â± " + timeLeft[0] + "s");
            if (timeLeft[0] <= 5) timerLabel.setForeground(new Color(0xef4444));
            if (timeLeft[0] <= 0) {
                feedback.setText("âŒ Time's up! Answer was: " + Integer.toBinaryString(number[0]));
                feedback.setForeground(new Color(0xef4444));
                nextQuestion.run();
            }
        });
        countdown[0].start();

        answerField.addActionListener(e -> {
            String ans = answerField.getText().trim();
            String correct = Integer.toBinaryString(number[0]);
            if (ans.equals(correct)) {
                score[0]++;
                scoreLabel.setText("Score: " + score[0]);
                feedback.setText("âœ… Correct! You're a binary beast!");
                feedback.setForeground(new Color(0x22c55e));
            } else {
                feedback.setText("âŒ Wrong! Correct: " + correct);
                feedback.setForeground(new Color(0xef4444));
            }
            nextQuestion.run();
        });

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(Box.createVerticalStrut(30));
        numLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        feedback.setAlignmentX(Component.CENTER_ALIGNMENT);
        answerField.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(numLabel);
        center.add(Box.createVerticalStrut(10));
        center.add(timerLabel);
        center.add(Box.createVerticalStrut(20));
        center.add(answerField);
        center.add(Box.createVerticalStrut(10));
        center.add(feedback);
        center.add(Box.createVerticalStrut(10));
        center.add(scoreLabel);

        main.add(hdr, BorderLayout.NORTH);
        main.add(center, BorderLayout.CENTER);
        contentPanel.add(main);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // ========== DEBUG DHAMAAL ==========
    private void showDebugDhamaal() {
        contentPanel.removeAll();
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(0x0d1520));
        main.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("ðŸ”¥ Debug Dhamaal");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(0xf59e0b));
        JLabel sub = new JLabel("  Spot the bug in the code! Funny errors guaranteed ðŸ˜‚");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(0x475569));
        JPanel hdr = new JPanel(); hdr.setOpaque(false);
        hdr.setLayout(new BoxLayout(hdr, BoxLayout.Y_AXIS));
        hdr.add(title); hdr.add(Box.createVerticalStrut(4)); hdr.add(sub);

        String[][] bugs = {
            {"int x = 5;\nif (x = 5) {\n  System.out.println(\"Hello\");\n}", "= should be ==", "Rookie mistake! = assigns, == compares. Your compiler is judging you ðŸ‘€"},
            {"for (int i=0; i<10; i--) {\n  System.out.println(i);\n}", "i-- should be i++", "Congratulations! You invented an infinite loop ðŸŽ‰ Your PC hates you now"},
            {"String s = null;\nSystem.out.println(s.length());", "s is null - NullPointerException", "NULL pointer?! Java's way of saying 'you played yourself' ðŸ¤¦"},
            {"int[] arr = new int[5];\narr[5] = 10;", "Index 5 out of bounds (0-4)", "Array index 5 on size 5 array? Bold move. ArrayIndexOutOfBounds says hi! ðŸ’¥"},
            {"while (true);\n{\n  doWork();\n}", "Semicolon after while kills the loop", "That semicolon after while() is your worst enemy. Empty loop goes brrr ðŸ”"}
        };

        int[] qi = {0};
        int[] score = {0};

        JTextArea codeArea = new JTextArea(bugs[0][0]);
        codeArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        codeArea.setBackground(new Color(0x0d1117));
        codeArea.setForeground(new Color(0x22c55e));
        codeArea.setEditable(false);
        codeArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel bugHint = new JLabel("What's the bug?");
        bugHint.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bugHint.setForeground(Color.WHITE);

        JLabel explanation = new JLabel(" ");
        explanation.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        explanation.setForeground(new Color(0xf59e0b));

        JLabel scoreLabel = new JLabel("Score: 0 / " + bugs.length);
        scoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        scoreLabel.setForeground(new Color(0x0ea5e9));

        JTextField ansField = new JTextField();
        ansField.setBackground(new Color(0x0d1520));
        ansField.setForeground(Color.WHITE);
        ansField.setCaretColor(Color.WHITE);
        ansField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ansField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xf59e0b), 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));

        JButton submitBtn = new JButton("Submit Fix ðŸ”§");
        submitBtn.setBackground(new Color(0xf59e0b));
        submitBtn.setForeground(Color.BLACK);
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        submitBtn.setFocusPainted(false);
        submitBtn.setBorderPainted(false);
        submitBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        submitBtn.addActionListener(e -> {
            String ans = ansField.getText().trim().toLowerCase();
            String correct = bugs[qi[0]][1].toLowerCase();
            if (ans.contains(correct.split(" ")[0]) || correct.contains(ans.split(" ")[0])) {
                score[0]++;
                explanation.setText("âœ… " + bugs[qi[0]][2]);
                explanation.setForeground(new Color(0x22c55e));
            } else {
                explanation.setText("âŒ Answer: " + bugs[qi[0]][1] + " | " + bugs[qi[0]][2]);
                explanation.setForeground(new Color(0xef4444));
            }
            scoreLabel.setText("Score: " + score[0] + " / " + bugs.length);
            qi[0]++;
            if (qi[0] < bugs.length) {
                javax.swing.Timer t = new javax.swing.Timer(2000, ev -> {
                    codeArea.setText(bugs[qi[0]][0]);
                    ansField.setText("");
                    explanation.setText(" ");
                });
                t.setRepeats(false); t.start();
            } else {
                javax.swing.Timer t = new javax.swing.Timer(2000, ev -> {
                    explanation.setText("ðŸŽ‰ Done! Final Score: " + score[0] + "/" + bugs.length + " | " + (score[0] >= 4 ? "Bug Slayer! ðŸ†" : "Keep practicing! ðŸ’ª"));
                    explanation.setForeground(new Color(0xf59e0b));
                    submitBtn.setEnabled(false);
                });
                t.setRepeats(false); t.start();
            }
        });

        JPanel inputRow = new JPanel(new BorderLayout(10, 0));
        inputRow.setOpaque(false);
        inputRow.add(ansField, BorderLayout.CENTER);
        inputRow.add(submitBtn, BorderLayout.EAST);

        JPanel south = new JPanel();
        south.setOpaque(false);
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
        south.add(bugHint);
        south.add(Box.createVerticalStrut(8));
        south.add(inputRow);
        south.add(Box.createVerticalStrut(8));
        south.add(explanation);
        south.add(Box.createVerticalStrut(8));
        south.add(scoreLabel);

        main.add(hdr, BorderLayout.NORTH);
        main.add(new JScrollPane(codeArea), BorderLayout.CENTER);
        main.add(south, BorderLayout.SOUTH);
        contentPanel.add(main);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // ========== STACK OVERFLOW ==========
    private void showStackOverflow() {
        contentPanel.removeAll();
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(0x0d1520));
        main.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("ðŸ’€ Stack Overflow");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(0xef4444));
        JLabel sub = new JLabel("  Answer correctly to stack blocks! Wrong answer = CRASH! ðŸ’¥");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(0x475569));
        JPanel hdr = new JPanel(); hdr.setOpaque(false);
        hdr.setLayout(new BoxLayout(hdr, BoxLayout.Y_AXIS));
        hdr.add(title); hdr.add(Box.createVerticalStrut(4)); hdr.add(sub);

        String[][] qs = {
            {"What does HTML stand for?", "HyperText Markup Language", "HyperText Making Language", "HighText Machine Language"},
            {"Which symbol ends Java statement?", ";", ":", "."},
            {"What is Git used for?", "Version Control", "Database", "Gaming"},
            {"Which is NOT a data type?", "String", "Boolean", "Float", "Fruit"},
            {"What does CPU stand for?", "Central Processing Unit", "Computer Personal Unit", "Control Program Utility"},
            {"Which tag is for heading in HTML?", "<h1>", "<head>", "<header>"},
            {"What is 2+2 in binary?", "100", "010", "110"},
            {"What does API stand for?", "Application Programming Interface", "App Process Integration", "Automated Program Input"}
        };
        String[] memes = {"ðŸ”¥ Stack growing!", "ðŸ’ª You're unstoppable!", "ðŸš€ To the moon!", "ðŸ˜¤ Keep going!", "ðŸ¤– Robot mode activated!", "âš¡ Lightning fast!", "ðŸ§  Big brain energy!", "ðŸ‘‘ Stack Master!"};
        String[] crashMsgs = {"ðŸ’¥ SEGFAULT! Your stack imploded!", "ðŸ”¥ StackOverflowException thrown!", "ðŸ˜± Stack crashed harder than prod on Friday!", "ðŸ’€ RIP Stack. Gone too soon."};

        int[] score = {0};
        int[] qi = {0};

        JPanel towerPanel = new JPanel();
        towerPanel.setLayout(new BoxLayout(towerPanel, BoxLayout.Y_AXIS));
        towerPanel.setBackground(new Color(0x0d1117));
        towerPanel.setPreferredSize(new Dimension(200, 400));

        JLabel questionLabel = new JLabel("<html><div style='width:400px'>" + qs[0][0] + "</div></html>");
        questionLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        questionLabel.setForeground(Color.WHITE);

        JLabel memeLabel = new JLabel(" ");
        memeLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        memeLabel.setForeground(new Color(0xf59e0b));

        JPanel optPanel = new JPanel(new GridLayout(1, 3, 8, 0)); optPanel.setMaximumSize(new Dimension(600, 45));
        optPanel.setOpaque(false);

        Color[] btnColors = {new Color(0x0ea5e9), new Color(0x22c55e), new Color(0x8b5cf6)};

        Runnable[] loadQ = {null};
        loadQ[0] = () -> {
            optPanel.removeAll();
            if (qi[0] >= qs.length) {
                questionLabel.setText("ðŸ† LEGENDARY! Stack height: " + score[0] + "! You're a Stack God!");
                questionLabel.setForeground(new Color(0xf59e0b));
                return;
            }
            questionLabel.setText("<html><div style='width:400px'>" + qs[qi[0]][0] + "</div></html>");
            for (int i = 1; i <= 3 && i < qs[qi[0]].length; i++) {
                final String opt = qs[qi[0]][i];
                final boolean isCorrect = (i == 1);
                JButton ob = new JButton(opt);
                ob.setBackground(btnColors[i-1]); ob.setPreferredSize(new Dimension(180, 40));
                ob.setForeground(Color.WHITE);
                ob.setFont(new Font("Segoe UI", Font.BOLD, 12));
                ob.setFocusPainted(false);
                ob.setBorderPainted(false);
                ob.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                ob.addActionListener(e -> {
                    if (isCorrect) {
                        score[0]++;
                        JLabel block = new JLabel("  Block #" + score[0] + " âœ…", SwingConstants.CENTER);
                        block.setFont(new Font("Monospaced", Font.BOLD, 12));
                        block.setForeground(Color.WHITE);
                        block.setBackground(btnColors[(score[0]-1) % 3]);
                        block.setOpaque(true);
                        block.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                        block.setMaximumSize(new Dimension(180, 28));
                        towerPanel.add(block, 0);
                        towerPanel.revalidate(); towerPanel.repaint();
                        memeLabel.setText(score[0] <= memes.length ? memes[score[0]-1] : "ðŸ”¥ UNSTOPPABLE!");
                        qi[0]++;
                        loadQ[0].run();
                    } else {
                        int ri = (int)(Math.random() * crashMsgs.length);
                        questionLabel.setText("<html>" + crashMsgs[ri] + "<br>Final Stack: " + score[0] + " blocks</html>");
                        questionLabel.setForeground(new Color(0xef4444));
                        optPanel.removeAll();
                        optPanel.revalidate(); optPanel.repaint();
                    }
                });
                optPanel.add(ob);
            }
            optPanel.revalidate(); optPanel.repaint();
        };
        loadQ[0].run();

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.add(questionLabel);
        right.add(Box.createVerticalStrut(15));
        right.add(optPanel);
        right.add(Box.createVerticalStrut(10));
        right.add(memeLabel);

        main.add(hdr, BorderLayout.NORTH);
        main.add(towerPanel, BorderLayout.WEST);
        main.add(right, BorderLayout.CENTER);
        contentPanel.add(main);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // ========== DEPLOY OR DIE ==========
    private void showDeployOrDie() {
        contentPanel.removeAll();
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(0x0d1520));
        main.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("ðŸš€ Deploy or Die");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(0xec4899));
        JLabel sub = new JLabel("  60 seconds to deploy! Disasters incoming â€” make quick decisions! ðŸ˜±");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(0x475569));
        JPanel hdr = new JPanel(); hdr.setOpaque(false);
        hdr.setLayout(new BoxLayout(hdr, BoxLayout.Y_AXIS));
        hdr.add(title); hdr.add(Box.createVerticalStrut(4)); hdr.add(sub);

        String[][] scenarios = {
            {"ðŸ”¥ Server is on fire!", "Call DevOps", "Restart server", "Pray to the cloud gods", "Call DevOps", "+20 XP! DevOps saves the day!", "-10 XP! Restart caused data loss!", "-5 XP! Cloud gods are AFK!"},
            {"ðŸ’€ Merge conflict detected!", "Fix manually", "Force push", "Delete repo", "Fix manually", "+20 XP! Conflict resolved like a pro!", "-15 XP! Force push destroyed prod!", "-50 XP! You deleted the repo???"},
            {"ðŸ˜± Boss wants demo in 5 mins!", "Show what works", "Fake a demo", "Call in sick", "Show what works", "+20 XP! Honest wins respect!", "-5 XP! Boss noticed the fake!", "-10 XP! Unprofessional move!"},
            {"ðŸ› Critical bug in production!", "Hotfix immediately", "Rollback", "Blame intern", "Hotfix immediately", "+25 XP! Hero move!", "+15 XP! Safe rollback!", "-20 XP! Intern quits, you're fired!"},
            {"ðŸ“§ Client wants feature by tomorrow!", "Negotiate deadline", "Work all night", "Say it's done (lie)", "Negotiate deadline", "+20 XP! Smart negotiation!", "+10 XP! Coffee fueled success!", "-25 XP! Client found out the truth!"}
        };

        int[] score = {0};
        int[] si = {0};
        int[] timeLeft = {60};

        JLabel timerLabel = new JLabel("â± 60s");
        timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        timerLabel.setForeground(new Color(0xef4444));

        JLabel scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        scoreLabel.setForeground(new Color(0xec4899));

        JLabel scenarioLabel = new JLabel("<html><div style='width:500px;font-size:16px'>" + scenarios[0][0] + "</div></html>");
        scenarioLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        scenarioLabel.setForeground(Color.WHITE);

        JLabel resultLabel = new JLabel(" ");
        resultLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        resultLabel.setForeground(new Color(0xf59e0b));

        JPanel btnPanel = new JPanel(new GridLayout(1, 3, 10, 0)); btnPanel.setMaximumSize(new Dimension(650, 50));
        btnPanel.setOpaque(false);

        Color[] cols = {new Color(0x0ea5e9), new Color(0x22c55e), new Color(0xef4444)};
        Runnable[] loadS = {null};
        javax.swing.Timer[] timer = {null};

        loadS[0] = () -> {
            btnPanel.removeAll();
            if (si[0] >= scenarios.length) {
                scenarioLabel.setText("<html>ðŸŽ‰ All scenarios survived!<br>Final Score: " + score[0] + " | " + (score[0] >= 80 ? "DevOps God! ðŸ†" : "Junior Dev ðŸ’ª") + "</html>");
                if (timer[0] != null) timer[0].stop();
                return;
            }
            scenarioLabel.setText("<html><div style='width:500px'>" + scenarios[si[0]][0] + "</div></html>");
            for (int i = 1; i <= 3; i++) {
                final int idx = i;
                JButton ob = new JButton(scenarios[si[0]][i]);
                ob.setBackground(cols[i-1]); ob.setPreferredSize(new Dimension(180, 45));
                ob.setForeground(Color.WHITE);
                ob.setFont(new Font("Segoe UI", Font.BOLD, 12));
                ob.setFocusPainted(false);
                ob.setBorderPainted(false);
                ob.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                ob.addActionListener(e -> {
                    int resIdx = 4 + (idx-1); String result = (resIdx < scenarios[si[0]].length) ? scenarios[si[0]][resIdx] : "No result";
                    int xp = idx == 1 ? 20 : (idx == 2 ? 10 : -10);
                    if (result.contains("+25")) xp = 25;
                    else if (result.contains("-50")) xp = -50;
                    else if (result.contains("-25")) xp = -25;
                    else if (result.contains("-20")) xp = -20;
                    else if (result.contains("-15")) xp = -15;
                    score[0] += xp;
                    scoreLabel.setText("Score: " + score[0]);
                    resultLabel.setText(result);
                    resultLabel.setForeground(xp > 0 ? new Color(0x22c55e) : new Color(0xef4444));
                    si[0]++;
                    javax.swing.Timer t = new javax.swing.Timer(2000, ev -> loadS[0].run());
                    t.setRepeats(false); t.start();
                });
                btnPanel.add(ob);
            }
            btnPanel.revalidate(); btnPanel.repaint();
        };

        timer[0] = new javax.swing.Timer(1000, e -> {
            timeLeft[0]--;
            timerLabel.setText("â± " + timeLeft[0] + "s");
            if (timeLeft[0] <= 0) {
                timer[0].stop();
                scenarioLabel.setText("<html>â° TIME'S UP!<br>Final Score: " + score[0] + "<br>" + (score[0] >= 50 ? "Not bad, junior dev! ðŸš€" : "Back to bootcamp! ðŸ˜‚") + "</html>");
                btnPanel.removeAll();
                btnPanel.revalidate(); btnPanel.repaint();
            }
        });
        timer[0].start();
        loadS[0].run();

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topBar.setOpaque(false);
        topBar.add(timerLabel);
        topBar.add(Box.createHorizontalStrut(20));
        topBar.add(scoreLabel);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(Box.createVerticalStrut(30));
        scenarioLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(scenarioLabel);
        center.add(Box.createVerticalStrut(20));
        center.add(btnPanel);
        center.add(Box.createVerticalStrut(15));
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(resultLabel);

        JPanel north = new JPanel(new BorderLayout());
        north.setOpaque(false);
        north.add(hdr, BorderLayout.NORTH);
        north.add(topBar, BorderLayout.SOUTH);

        main.add(north, BorderLayout.NORTH);
        main.add(center, BorderLayout.CENTER);
        contentPanel.add(main);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

}