package com.techsikho.ui;

import com.techsikho.services.AuthService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RegisterFrame extends JFrame {

    // ── Fields ───────────────────────────────────────────────────────────────
    private UTextField      fullNameField;
    private UTextField      usernameField;
    private UTextField      emailField;
    private UPasswordField  passwordField;
    private UPasswordField  confirmPassField;
    private JButton         registerBtn;
    private JLabel          statusLabel;
    private Point           dragOrigin;

    // ── Palette (identical to LoginFrame) ────────────────────────────────────
    private static final Color C_LEFT_TOP    = new Color( 30,  15,  60);
    private static final Color C_LEFT_BOT    = new Color( 10,   5,  30);
    private static final Color C_PURPLE      = new Color(139,  92, 246);
    private static final Color C_RIGHT_BG    = new Color( 13,  13,  30);
    private static final Color C_FIELD_BG    = new Color( 22,  22,  45);
    private static final Color C_SUBTITLE    = new Color(148, 163, 184);
    private static final Color C_BORDER_IDLE = new Color( 99, 102, 241);
    private static final Color C_BTN_NORMAL  = new Color(124,  58, 237);
    private static final Color C_BTN_HOVER   = new Color(109,  40, 217);
    private static final Color C_ERROR       = new Color(239,  68,  68);
    private static final Color C_SUCCESS     = new Color( 52, 211, 153);

    // ── Constructor ──────────────────────────────────────────────────────────
    public RegisterFrame() {
        setUndecorated(true);
        setSize(1000, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
    }

    // ── UI Assembly ──────────────────────────────────────────────────────────
    private void initUI() {

        // Root panel
        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(C_PURPLE.darker());
                g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();
            }
        };
        root.setBackground(C_RIGHT_BG);
        setContentPane(root);

        // ── LEFT PANEL ───────────────────────────────────────────────────────
        LeftPanel left = new LeftPanel();
        left.setPreferredSize(new Dimension(400, 620));

        // Drag-to-move on left panel
        left.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { dragOrigin = e.getPoint(); }
        });
        left.addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                Point loc = getLocation();
                setLocation(loc.x + e.getX() - dragOrigin.x,
                            loc.y + e.getY() - dragOrigin.y);
            }
        });
        root.add(left, BorderLayout.WEST);

        // ── RIGHT PANEL ──────────────────────────────────────────────────────
        root.add(buildRightPanel(), BorderLayout.CENTER);
    }

    // ── RIGHT PANEL ──────────────────────────────────────────────────────────
    private JPanel buildRightPanel() {
        JPanel right = new JPanel(new BorderLayout());
        right.setBackground(C_RIGHT_BG);

        // Top-right close button
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        topBar.setOpaque(false);
        JButton closeBtn = new JButton("X");
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        closeBtn.setForeground(new Color(100, 100, 130));
        closeBtn.setContentAreaFilled(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { closeBtn.setForeground(C_ERROR); }
            @Override public void mouseExited (MouseEvent e) { closeBtn.setForeground(new Color(100, 100, 130)); }
        });
        closeBtn.addActionListener(e -> System.exit(0));
        topBar.add(closeBtn);
        right.add(topBar, BorderLayout.NORTH);

        // ── Scrollable center form ────────────────────────────────────────
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        // Vertical centering wrapper using GridBagLayout
        JPanel vWrap = new JPanel(new GridBagLayout());
        vWrap.setOpaque(false);
        vWrap.add(form);
        right.add(vWrap, BorderLayout.CENTER);

        // ── "Create Account" heading ──────────────────────────────────────
        JLabel heading = new JLabel("Create Account");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 30));
        heading.setForeground(Color.WHITE);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(heading);

        // ── Purple underline accent ───────────────────────────────────────
        form.add(Box.createVerticalStrut(6));
        JPanel accent = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(139, 92, 246, 0), 40, 0, C_PURPLE);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, 40, 3, 3, 3);
                g2.dispose();
            }
        };
        accent.setOpaque(false);
        accent.setPreferredSize(new Dimension(40, 3));
        accent.setMaximumSize(new Dimension(40, 3));
        accent.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(accent);

        // ── Subtitle ─────────────────────────────────────────────────────
        form.add(Box.createVerticalStrut(8));
        JLabel sub = new JLabel("Join thousands of learners!");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(C_SUBTITLE);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(sub);

        // ── Fields ───────────────────────────────────────────────────────
        form.add(Box.createVerticalStrut(20));
        fullNameField    = makeTextField("FULL NAME",        "Enter full name",     false);
        usernameField    = makeTextField("USERNAME",         "Enter username",      false);
        emailField       = makeTextField("EMAIL",            "Enter email",         false);
        passwordField    = makePasswordField("PASSWORD",     "Enter password");
        confirmPassField = makePasswordField("CONFIRM PASSWORD", "Re-enter password");

        // Arrange fields in two columns: (fullname) / (username) on row1
        //                                (email)    / (password) on row2
        //                                (confirm)              on row3
        // For simplicity and clean alignment: single column, compact spacing
        int fieldGap = 12;
        addFormRow(form, fullNameField,    "FULL NAME");
        form.add(Box.createVerticalStrut(fieldGap));
        addFormRow(form, usernameField,   "USERNAME");
        form.add(Box.createVerticalStrut(fieldGap));
        addFormRow(form, emailField,      "EMAIL");
        form.add(Box.createVerticalStrut(fieldGap));
        addFormRow(form, passwordField,   "PASSWORD");
        form.add(Box.createVerticalStrut(fieldGap));
        addFormRow(form, confirmPassField,"CONFIRM PASSWORD");

        // ── Status label ─────────────────────────────────────────────────
        form.add(Box.createVerticalStrut(8));
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(C_ERROR);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setVisible(false);
        form.add(statusLabel);

        // ── REGISTER button ───────────────────────────────────────────────
        form.add(Box.createVerticalStrut(14));
        registerBtn = new PurpleButton("REGISTER");
        registerBtn.setMaximumSize(new Dimension(380, 50));
        registerBtn.setPreferredSize(new Dimension(380, 50));
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(registerBtn);

        // ── Back-to-login link ────────────────────────────────────────────
        form.add(Box.createVerticalStrut(12));
        JButton backBtn = new JButton("Already have an account? Login");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        backBtn.setForeground(C_PURPLE);
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { backBtn.setForeground(Color.WHITE); }
            @Override public void mouseExited (MouseEvent e) { backBtn.setForeground(C_PURPLE); }
        });
        backBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        form.add(backBtn);

        // ── Wire actions ─────────────────────────────────────────────────
        registerBtn.addActionListener(e -> handleRegister());

        return right;
    }

    // Adds a label + field row to the form
    private void addFormRow(JPanel form, JComponent field, String labelText) {
        JPanel group = new JPanel();
        group.setOpaque(false);
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setMaximumSize(new Dimension(380, 68));
        group.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(C_PURPLE);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        group.add(lbl);
        group.add(Box.createVerticalStrut(4));

        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        group.add(field);

        form.add(group);
    }

    // ── Field factories (matching LoginFrame style) ───────────────────────────
    private UTextField makeTextField(String label, String placeholder, boolean dummy) {
        UTextField f = new UTextField(placeholder);
        f.setPreferredSize(new Dimension(380, 42));
        f.setMaximumSize(new Dimension(380, 42));
        return f;
    }

    private UPasswordField makePasswordField(String label, String placeholder) {
        UPasswordField f = new UPasswordField(placeholder);
        f.setPreferredSize(new Dimension(380, 42));
        f.setMaximumSize(new Dimension(380, 42));
        return f;
    }

    // ── Auth Logic (unchanged from original) ────────────────────────────────
    private void handleRegister() {
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email    = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirm  = new String(confirmPassField.getPassword()).trim();

        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showStatus("All fields are required!", false);
            return;
        }
        if (!password.equals(confirm)) {
            showStatus("Passwords do not match!", false);
            return;
        }
        if (password.length() < 6) {
            showStatus("Password must be at least 6 characters!", false);
            return;
        }

        registerBtn.setEnabled(false);
        registerBtn.setText("CREATING...");

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override protected Boolean doInBackground() {
                return AuthService.register(username, email, password, fullName);
            }
            @Override protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        JOptionPane.showMessageDialog(RegisterFrame.this,
                            "Account created! You can now login.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                        new LoginFrame().setVisible(true);
                        dispose();
                    } else {
                        showStatus("Username already exists or error!", false);
                        registerBtn.setEnabled(true);
                        registerBtn.setText("REGISTER");
                    }
                } catch (Exception ex) {
                    showStatus("Error: " + ex.getMessage(), false);
                    registerBtn.setEnabled(true);
                    registerBtn.setText("REGISTER");
                }
            }
        };
        worker.execute();
    }

    private void showStatus(String msg, boolean success) {
        statusLabel.setText(msg);
        statusLabel.setForeground(success ? C_SUCCESS : C_ERROR);
        statusLabel.setVisible(true);
        statusLabel.revalidate();
        statusLabel.repaint();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  LEFT PANEL — identical to LoginFrame.LeftPanel
    // ════════════════════════════════════════════════════════════════════════
    private static class LeftPanel extends JPanel {

        LeftPanel() {
            setOpaque(false);
            setLayout(new GridBagLayout());

            GridBagConstraints g = new GridBagConstraints();
            g.gridx   = 0;
            g.fill    = GridBagConstraints.HORIZONTAL;
            g.anchor  = GridBagConstraints.CENTER;
            g.weightx = 1.0;

            // Glowing "TS" logo
            g.gridy  = 0;
            g.insets = new Insets(70, 40, 0, 40);
            JPanel tsPanel = new JPanel() {
                @Override protected void paintComponent(Graphics g0) {
                    super.paintComponent(g0);
                    Graphics2D g2 = (Graphics2D) g0.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                    Font f  = new Font("Segoe UI", Font.BOLD, 80);
                    g2.setFont(f);
                    FontMetrics fm  = g2.getFontMetrics();
                    String txt = "TS";
                    int tx = (getWidth()  - fm.stringWidth(txt)) / 2;
                    int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;

                    int[] alphas  = {20, 35, 55, 80};
                    int[] offsets = { 8,  5,  3,  1};
                    for (int i = 0; i < alphas.length; i++) {
                        g2.setColor(new Color(139, 92, 246, alphas[i]));
                        g2.drawString(txt, tx - offsets[i], ty);
                        g2.drawString(txt, tx + offsets[i], ty);
                        g2.drawString(txt, tx, ty - offsets[i]);
                        g2.drawString(txt, tx, ty + offsets[i]);
                    }
                    g2.setColor(new Color(0, 0, 0, 120));
                    g2.drawString(txt, tx + 3, ty + 4);
                    g2.setColor(new Color(139, 92, 246));
                    g2.drawString(txt, tx, ty);
                    g2.dispose();
                }
            };
            tsPanel.setOpaque(false);
            tsPanel.setPreferredSize(new Dimension(320, 110));
            add(tsPanel, g);

            // TechSikho
            g.gridy  = 1;
            g.insets = new Insets(4, 40, 0, 40);
            JLabel brand = new JLabel("TechSikho", SwingConstants.CENTER);
            brand.setFont(new Font("Segoe UI", Font.BOLD, 28));
            brand.setForeground(Color.WHITE);
            add(brand, g);

            // Slogan
            g.gridy  = 2;
            g.insets = new Insets(6, 40, 0, 40);
            JLabel slogan = new JLabel("Learn. Code. Level Up.", SwingConstants.CENTER);
            slogan.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            slogan.setForeground(new Color(167, 139, 250));
            add(slogan, g);

            // Separator
            g.gridy  = 3;
            g.insets = new Insets(18, 0, 18, 0);
            add(buildSeparator(), g);

            // Feature rows
            String[] icons  = {"\u25B6", "\u2694", "\u2605", "\u25CE"};
            String[] labels = {" Gamified Learning", " Boss Battles", " XP & Levels", " Mini Games"};
            Color[]  iColors = {
                new Color(251, 191,  36),
                new Color(239,  68,  68),
                new Color(250, 204,  21),
                new Color( 52, 211, 153)
            };

            JPanel featureBox = new JPanel();
            featureBox.setOpaque(false);
            featureBox.setLayout(new BoxLayout(featureBox, BoxLayout.Y_AXIS));
            for (int i = 0; i < labels.length; i++) {
                featureBox.add(buildRow(icons[i], labels[i], iColors[i]));
                if (i < labels.length - 1) featureBox.add(Box.createVerticalStrut(10));
            }

            g.gridy  = 4;
            g.insets = new Insets(0, 40, 0, 40);
            add(featureBox, g);

            // Glue
            g.gridy   = 5;
            g.weighty = 1.0;
            g.fill    = GridBagConstraints.BOTH;
            g.insets  = new Insets(0, 0, 0, 0);
            add(Box.createGlue(), g);

            // Version
            g.gridy   = 6;
            g.weighty = 0;
            g.fill    = GridBagConstraints.HORIZONTAL;
            g.insets  = new Insets(0, 40, 20, 40);
            JLabel version = new JLabel("v1.0", SwingConstants.CENTER);
            version.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            version.setForeground(new Color(100, 100, 130));
            add(version, g);
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = new GradientPaint(0, 0, C_LEFT_TOP, 0, getHeight(), C_LEFT_BOT);
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
            GradientPaint vLine = new GradientPaint(
                0, 0,           new Color(139, 92, 246, 0),
                0, getHeight(), new Color(139, 92, 246, 200));
            g2.setPaint(vLine);
            g2.fillRect(getWidth() - 2, 0, 2, getHeight());
            g2.dispose();
            super.paintComponent(g);
        }

        private static JPanel buildSeparator() {
            JPanel sep = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int cx = getWidth() / 2;
                    g2.setPaint(new GradientPaint(cx - 30, 0, new Color(139, 92, 246, 0), cx, 0, new Color(139, 92, 246)));
                    g2.fillRect(cx - 30, 0, 30, 2);
                    g2.setPaint(new GradientPaint(cx, 0, new Color(139, 92, 246), cx + 30, 0, new Color(139, 92, 246, 0)));
                    g2.fillRect(cx, 0, 30, 2);
                    g2.dispose();
                }
            };
            sep.setOpaque(false);
            sep.setPreferredSize(new Dimension(320, 2));
            return sep;
        }

        private static JPanel buildRow(String icon, String label, Color iconColor) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            row.setOpaque(false);
            JLabel ico = new JLabel(icon);
            ico.setFont(new Font("Segoe UI Symbol", Font.BOLD, 13));
            ico.setForeground(iconColor);
            JLabel txt = new JLabel(label);
            txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            txt.setForeground(new Color(220, 220, 255));
            row.add(ico);
            row.add(txt);
            return row;
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  SHARED CUSTOM COMPONENTS (self-contained, no dependency on LoginFrame)
    // ════════════════════════════════════════════════════════════════════════

    // Bottom-underline text field
    static class UTextField extends JTextField implements FocusListener {
        private final String placeholder;
        private boolean      placeholderActive = true;
        private boolean      focused           = false;

        UTextField(String placeholder) {
            this.placeholder = placeholder;
            setOpaque(false);
            setForeground(new Color(150, 150, 180));
            setCaretColor(Color.WHITE);
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setBorder(BorderFactory.createEmptyBorder(6, 2, 6, 2));
            setText(placeholder);
            addFocusListener(this);
        }

        @Override public String getText() { return placeholderActive ? "" : super.getText(); }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(22, 22, 45));
            g2.fillRect(0, 0, getWidth(), getHeight());
            int ly = getHeight() - 1;
            if (focused) {
                g2.setColor(new Color(139, 92, 246, 60));
                g2.fillRect(0, ly - 1, getWidth(), 3);
                g2.setColor(new Color(139, 92, 246));
                g2.setStroke(new BasicStroke(2f));
            } else {
                g2.setColor(new Color(99, 102, 241));
                g2.setStroke(new BasicStroke(1f));
            }
            g2.drawLine(0, ly, getWidth(), ly);
            g2.dispose();
            super.paintComponent(g);
        }

        @Override public void focusGained(FocusEvent e) {
            focused = true;
            if (placeholderActive) { placeholderActive = false; setText(""); setForeground(Color.WHITE); }
            repaint();
        }
        @Override public void focusLost(FocusEvent e) {
            focused = false;
            if (super.getText().trim().isEmpty()) {
                placeholderActive = true; setText(placeholder); setForeground(new Color(150, 150, 180));
            }
            repaint();
        }
    }

    // Bottom-underline password field
    static class UPasswordField extends JPasswordField implements FocusListener {
        private final String placeholder;
        private boolean      placeholderActive = true;
        private boolean      focused           = false;
        private final char   realEcho;

        UPasswordField(String placeholder) {
            this.placeholder = placeholder;
            realEcho = getEchoChar();
            setEchoChar((char) 0);
            setOpaque(false);
            setForeground(new Color(150, 150, 180));
            setCaretColor(Color.WHITE);
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setBorder(BorderFactory.createEmptyBorder(6, 2, 6, 2));
            setText(placeholder);
            addFocusListener(this);
        }

        @Override public char[] getPassword() { return placeholderActive ? new char[0] : super.getPassword(); }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(22, 22, 45));
            g2.fillRect(0, 0, getWidth(), getHeight());
            int ly = getHeight() - 1;
            if (focused) {
                g2.setColor(new Color(139, 92, 246, 60));
                g2.fillRect(0, ly - 1, getWidth(), 3);
                g2.setColor(new Color(139, 92, 246));
                g2.setStroke(new BasicStroke(2f));
            } else {
                g2.setColor(new Color(99, 102, 241));
                g2.setStroke(new BasicStroke(1f));
            }
            g2.drawLine(0, ly, getWidth(), ly);
            g2.dispose();
            super.paintComponent(g);
        }

        @Override public void focusGained(FocusEvent e) {
            focused = true;
            if (placeholderActive) {
                placeholderActive = false; setText(""); setEchoChar(realEcho); setForeground(Color.WHITE);
            }
            repaint();
        }
        @Override public void focusLost(FocusEvent e) {
            focused = false;
            if (new String(super.getPassword()).trim().isEmpty()) {
                placeholderActive = true; setText(placeholder); setEchoChar((char) 0); setForeground(new Color(150, 150, 180));
            }
            repaint();
        }
    }

    // Filled purple gradient button
    static class PurpleButton extends JButton {
        private boolean hovered = false;
        private String  label;

        PurpleButton(String text) {
            super(text);
            this.label = text;
            setFont(new Font("Segoe UI", Font.BOLD, 15));
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                @Override public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
            });
        }

        @Override public void setText(String t) { this.label = t; super.setText(t); }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color base = hovered ? C_BTN_HOVER : C_BTN_NORMAL;
            g2.setPaint(new GradientPaint(0, 0, base.brighter(), 0, getHeight(), base));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            g2.setColor(new Color(255, 255, 255, 30));
            g2.fillRoundRect(0, 0, getWidth(), getHeight() / 3, 8, 8);
            g2.setFont(getFont());
            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(label, (getWidth() - fm.stringWidth(label)) / 2,
                          (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            g2.dispose();
        }
    }
}