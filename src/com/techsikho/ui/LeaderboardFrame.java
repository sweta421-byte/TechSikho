package com.techsikho.ui;

import com.techsikho.dao.LeaderboardDAO;
import com.techsikho.models.User;
import com.techsikho.services.XPService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class LeaderboardFrame extends JFrame {

    private User currentUser;

    public LeaderboardFrame(User user) {
        this.currentUser = user;
        setTitle("TechSikho — Leaderboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(18, 18, 35));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // ── Header ───────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(18, 18, 35));

        JLabel title = new JLabel("🏆 Global Leaderboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);

        int myRank = LeaderboardDAO.getUserRank(currentUser.getUserId());
        int myLevel = XPService.calculateLevel(currentUser.getTotalXp());

        JLabel myStats = new JLabel("Your Rank: #" + myRank
            + "   |   XP: " + currentUser.getTotalXp()
            + "   |   Level: " + myLevel);
        myStats.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        myStats.setForeground(new Color(99, 102, 241));

        header.add(title, BorderLayout.NORTH);
        header.add(myStats, BorderLayout.SOUTH);
        mainPanel.add(header, BorderLayout.NORTH);

        // ── Table ─────────────────────────────────────────────
        String[] cols = {"#", "Username", "Full Name", "XP", "Level", "🔥 Streak"};
        List<String[]> data = LeaderboardDAO.getTopUsers();
        String[][] tableData = data.toArray(new String[0][]);

        JTable table = new JTable(tableData, cols) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table.setBackground(new Color(25, 25, 45));
        table.setForeground(Color.WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(38);
        table.setGridColor(new Color(40, 40, 65));
        table.setSelectionBackground(new Color(99, 102, 241));
        table.setSelectionForeground(Color.WHITE);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        // Header styling
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setBackground(new Color(99, 102, 241));
        tableHeader.setForeground(Color.WHITE);
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableHeader.setPreferredSize(new Dimension(0, 40));
        tableHeader.setReorderingAllowed(false);

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(130);
        table.getColumnModel().getColumn(2).setPreferredWidth(160);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setPreferredWidth(70);
        table.getColumnModel().getColumn(5).setPreferredWidth(80);

        // Center-align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < cols.length; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Top 3 row colors — gold, silver, bronze
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (sel) {
                    c.setBackground(new Color(99, 102, 241));
                    c.setForeground(Color.WHITE);
                } else if (row == 0) {
                    c.setBackground(new Color(60, 50, 20));
                    c.setForeground(new Color(255, 215, 0));   // Gold
                } else if (row == 1) {
                    c.setBackground(new Color(40, 45, 55));
                    c.setForeground(new Color(192, 192, 192)); // Silver
                } else if (row == 2) {
                    c.setBackground(new Color(45, 35, 25));
                    c.setForeground(new Color(205, 127, 50));  // Bronze
                } else {
                    c.setBackground(row % 2 == 0
                        ? new Color(25, 25, 45)
                        : new Color(30, 30, 52));
                    c.setForeground(Color.WHITE);
                }
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBackground(new Color(18, 18, 35));
        scroll.getViewport().setBackground(new Color(25, 25, 45));
        scroll.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        mainPanel.add(scroll, BorderLayout.CENTER);

        // ── Back Button ───────────────────────────────────────
        JButton backBtn = new JButton("← Back to Dashboard");
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backBtn.setBackground(new Color(99, 102, 241));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setBorderPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.setPreferredSize(new Dimension(200, 40));
        backBtn.addActionListener(e -> dispose());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(new Color(18, 18, 35));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        bottomPanel.add(backBtn);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }
}