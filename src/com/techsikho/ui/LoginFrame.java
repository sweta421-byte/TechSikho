package com.techsikho.ui;

import com.techsikho.models.User;
import com.techsikho.services.AuthService;
import com.techsikho.services.StreakService;
import com.techsikho.dao.UserDAO;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class LoginFrame extends JFrame {

    // ── Fields ──────────────────────────────────────────────────────────────
    private UnderlineField usernameField;
    private UnderlinePasswordField passwordField;
    private PurpleButton  loginBtn;
    private JButton       registerBtn;
    private JLabel        statusLabel;
    private Point         dragOrigin;

    // ── Palette ─────────────────────────────────────────────────────────────
    private static final Color C_LEFT_TOP    = new Color(30,  15,  60);
    private static final Color C_LEFT_BOT    = new Color(10,   5,  30);
    private static final Color C_PURPLE      = new Color(139, 92, 246);
    private static final Color C_PURPLE_SOFT = new Color(167,139, 250);
    private static final Color C_RIGHT_BG    = new Color(13,  13,  30);
    private static final Color C_FIELD_BG    = new Color(22,  22,  45);
    private static final Color C_LABEL_FG    = new Color(220,220, 255);
    private static final Color C_SUBTITLE    = new Color(148,163, 184);
    private static final Color C_BORDER_IDLE = new Color(99, 102, 241);
    private static final Color C_BTN_NORMAL  = new Color(124, 58, 237);
    private static final Color C_BTN_HOVER   = new Color(109, 40, 217);
    private static final Color C_ERROR       = new Color(239, 68,  68);
    private static final Color C_DIVIDER     = new Color(50,  50,  80);
    private static final Color C_VERSION     = new Color(100,100, 130);

    // ── Constructor ──────────────────────────────────────────────────────────
    public LoginFrame() {
        setUndecorated(true);
        setSize(1000, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(new Color(0, 0, 0, 0)); // transparent root for rounded feel
        initUI();
    }

    // ── UI Assembly ──────────────────────────────────────────────────────────
    private void initUI() {

        // Root panel — fills the whole frame
        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // thin 1-px window border
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

        // Drag-to-move: attach to left panel
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
        JPanel right = buildRightPanel();
        root.add(right, BorderLayout.CENTER);
    }

    // ── LEFT PANEL CLASS ─────────────────────────────────────────────────────
    private static class LeftPanel extends JPanel {

        LeftPanel() {
            setOpaque(false);
            setLayout(new GridBagLayout());

            GridBagConstraints g = new GridBagConstraints();
            g.gridx    = 0;
            g.fill     = GridBagConstraints.HORIZONTAL;
            g.anchor   = GridBagConstraints.CENTER;
            g.weightx  = 1.0;

            // ── "TS" glowing logo ─────────────────────────────────────────
            g.gridy  = 0;
            g.insets = new Insets(70, 40, 0, 40);
            JPanel tsPanel = new JPanel() {
                @Override protected void paintComponent(Graphics g0) {
                    super.paintComponent(g0);
                    Graphics2D g2 = (Graphics2D) g0.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                    // Glow layers — paint progressively larger, more transparent
                    Font f = new Font("Segoe UI", Font.BOLD, 80);
                    g2.setFont(f);
                    FontMetrics fm = g2.getFontMetrics();
                    String txt = "TS";
                    int tx = (getWidth()  - fm.stringWidth(txt)) / 2;
                    int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;

                    // outer glow rings
                    int[] alphas = {20, 35, 55, 80};
                    int[] offsets = {8, 5, 3, 1};
                    for (int i = 0; i < alphas.length; i++) {
                        g2.setColor(new Color(139, 92, 246, alphas[i]));
                        g2.drawString(txt, tx - offsets[i], ty);
                        g2.drawString(txt, tx + offsets[i], ty);
                        g2.drawString(txt, tx, ty - offsets[i]);
                        g2.drawString(txt, tx, ty + offsets[i]);
                    }
                    // shadow drop
                    g2.setColor(new Color(0, 0, 0, 120));
                    g2.drawString(txt, tx + 3, ty + 4);
                    // actual text
                    g2.setColor(new Color(139, 92, 246));
                    g2.drawString(txt, tx, ty);
                    g2.dispose();
                }
            };
            tsPanel.setOpaque(false);
            tsPanel.setPreferredSize(new Dimension(320, 110));
            add(tsPanel, g);

            // ── TechSikho ─────────────────────────────────────────────────
            g.gridy  = 1;
            g.insets = new Insets(4, 40, 0, 40);
            JLabel brand = new JLabel("TechSikho", SwingConstants.CENTER);
            brand.setFont(new Font("Segoe UI", Font.BOLD, 28));
            brand.setForeground(Color.WHITE);
            add(brand, g);

            // ── Slogan ────────────────────────────────────────────────────
            g.gridy  = 2;
            g.insets = new Insets(6, 40, 0, 40);
            JLabel slogan = new JLabel("Learn. Code. Level Up.", SwingConstants.CENTER);
            slogan.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            slogan.setForeground(new Color(167, 139, 250));
            add(slogan, g);

            // ── Separator ─────────────────────────────────────────────────
            g.gridy  = 3;
            g.insets = new Insets(18, 0, 18, 0);
            add(buildSeparator(), g);

            // ── Feature list ──────────────────────────────────────────────
            String[] features = {
                "[G] Gamified Learning",
                "[X] Boss Battles",
                "[*] XP & Levels",
                "[O] Mini Games"
            };
            String[] icons = { "\u25B6", "\u2694", "\u2605", "\u25CE" };
            String[] labels = {
                " Gamified Learning",
                " Boss Battles",
                " XP & Levels",
                " Mini Games"
            };
            Color[] iconColors = {
                new Color(251,191, 36),
                new Color(239, 68, 68),
                new Color(250,204, 21),
                new Color( 52,211,153)
            };

            JPanel featureBox = new JPanel();
            featureBox.setOpaque(false);
            featureBox.setLayout(new BoxLayout(featureBox, BoxLayout.Y_AXIS));
            for (int i = 0; i < labels.length; i++) {
                featureBox.add(buildFeatureRow(icons[i], labels[i], iconColors[i]));
                if (i < labels.length - 1) featureBox.add(Box.createVerticalStrut(10));
            }

            g.gridy  = 4;
            g.insets = new Insets(0, 40, 0, 40);
            add(featureBox, g);

            // ── Version (pushed to bottom with weighty filler) ─────────────
            g.gridy   = 5;
            g.weighty = 1.0;
            g.fill    = GridBagConstraints.BOTH;
            g.insets  = new Insets(0, 0, 0, 0);
            add(Box.createGlue(), g);

            g.gridy   = 6;
            g.weighty = 0;
            g.fill    = GridBagConstraints.HORIZONTAL;
            g.insets  = new Insets(0, 40, 20, 40);
            JLabel version = new JLabel("v1.0", SwingConstants.CENTER);
            version.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            version.setForeground(new Color(100, 100, 130));
            add(version, g);
        }

        // Paints the gradient bg + right-edge accent line
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Deep purple gradient
            GradientPaint gp = new GradientPaint(0, 0, C_LEFT_TOP, 0, getHeight(), C_LEFT_BOT);
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Right-edge vertical glow line
            GradientPaint vLine = new GradientPaint(
                0, 0,           new Color(139, 92, 246, 0),
                0, getHeight(), new Color(139, 92, 246, 200)
            );
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
                    GradientPaint gp = new GradientPaint(
                        cx - 30, 0, new Color(139, 92, 246, 0),
                        cx,      0, new Color(139, 92, 246, 255)
                    );
                    g2.setPaint(gp);
                    g2.fillRect(cx - 30, 0, 30, 2);
                    GradientPaint gp2 = new GradientPaint(
                        cx,      0, new Color(139, 92, 246, 255),
                        cx + 30, 0, new Color(139, 92, 246, 0)
                    );
                    g2.setPaint(gp2);
                    g2.fillRect(cx, 0, 30, 2);
                    g2.dispose();
                }
            };
            sep.setOpaque(false);
            sep.setPreferredSize(new Dimension(320, 2));
            return sep;
        }

        private static JPanel buildFeatureRow(String icon, String label, Color iconColor) {
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

    // ── RIGHT PANEL BUILDER ──────────────────────────────────────────────────
    private JPanel buildRightPanel() {
        JPanel right = new JPanel(new BorderLayout());
        right.setBackground(C_RIGHT_BG);

        // ── TOP-RIGHT close button ────────────────────────────────────────
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
            @Override public void mouseExited (MouseEvent e) { closeBtn.setForeground(new Color(100,100,130)); }
        });
        closeBtn.addActionListener(e -> System.exit(0));
        topBar.add(closeBtn);

        // Allow dragging from the top bar of the right panel too
        topBar.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { dragOrigin = SwingUtilities.convertPoint(topBar, e.getPoint(), LoginFrame.this); }
        });
        topBar.addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                if (dragOrigin == null) return;
                Point cur = SwingUtilities.convertPoint(topBar, e.getPoint(), LoginFrame.this);
                Point loc = getLocation();
                setLocation(loc.x + cur.x - dragOrigin.x, loc.y + cur.y - dragOrigin.y);
                dragOrigin = cur;
            }
        });

        right.add(topBar, BorderLayout.NORTH);

        // ── CENTER form panel ─────────────────────────────────────────────
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        // Vertical centering wrapper
        JPanel vWrap = new JPanel(new GridBagLayout());
        vWrap.setOpaque(false);
        vWrap.add(center);

        right.add(vWrap, BorderLayout.CENTER);

        // ── "Welcome Back" ────────────────────────────────────────────────
        JLabel welcome = new JLabel("Welcome Back");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 32));
        welcome.setForeground(Color.WHITE);
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(welcome);

        // ── Purple underline accent ───────────────────────────────────────
        center.add(Box.createVerticalStrut(6));
        JPanel accent = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(139, 92, 246, 0),
                    20, 0, C_PURPLE
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, 40, 3, 3, 3);
                g2.dispose();
            }
        };
        accent.setOpaque(false);
        accent.setPreferredSize(new Dimension(40, 3));
        accent.setMaximumSize (new Dimension(40, 3));
        accent.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(accent);

        // ── Subtitle ──────────────────────────────────────────────────────
        center.add(Box.createVerticalStrut(10));
        JLabel subtitle = new JLabel("Enter your credentials to continue");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(C_SUBTITLE);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(subtitle);

        // ── Gap ───────────────────────────────────────────────────────────
        center.add(Box.createVerticalStrut(30));

        // ── USERNAME ──────────────────────────────────────────────────────
        center.add(fieldGroup("USERNAME", false));
        center.add(Box.createVerticalStrut(18));

        // ── PASSWORD ──────────────────────────────────────────────────────
        center.add(fieldGroup("PASSWORD", true));

        // ── Forgot password ───────────────────────────────────────────────
        center.add(Box.createVerticalStrut(8));
        JPanel forgotWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        forgotWrap.setOpaque(false);
        forgotWrap.setMaximumSize(new Dimension(380, 22));
        forgotWrap.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton forgotBtn = new JButton("Forgot Password?");
        forgotBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        forgotBtn.setForeground(C_PURPLE);
        forgotBtn.setContentAreaFilled(false);
        forgotBtn.setBorderPainted(false);
        forgotBtn.setFocusPainted(false);
        forgotBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { forgotBtn.setForeground(Color.WHITE); }
            @Override public void mouseExited (MouseEvent e) { forgotBtn.setForeground(C_PURPLE); }
        });
        forgotBtn.addActionListener(e -> handleForgotPassword());
        forgotWrap.add(forgotBtn);
        center.add(forgotWrap);

        // ── Error label ───────────────────────────────────────────────────
        center.add(Box.createVerticalStrut(4));
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(C_ERROR);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setVisible(false);
        center.add(statusLabel);

        // ── Gap ───────────────────────────────────────────────────────────
        center.add(Box.createVerticalStrut(18));

        // ── LOGIN BUTTON ──────────────────────────────────────────────────
        loginBtn = new PurpleButton("LOGIN");
        loginBtn.setMaximumSize (new Dimension(380, 50));
        loginBtn.setPreferredSize(new Dimension(380, 50));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(loginBtn);

        // ── OR Divider ────────────────────────────────────────────────────
        center.add(Box.createVerticalStrut(20));
        center.add(buildOrDivider());

        // ── REGISTER BUTTON ───────────────────────────────────────────────
        center.add(Box.createVerticalStrut(14));
        registerBtn = new OutlineButton("Create New Account");
        registerBtn.setMaximumSize (new Dimension(380, 44));
        registerBtn.setPreferredSize(new Dimension(380, 44));
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(registerBtn);

        // ── Wire up actions ───────────────────────────────────────────────
        loginBtn.addActionListener(e -> handleLogin());
        passwordField.addActionListener(e -> handleLogin());
        registerBtn.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            dispose();
        });

        return right;
    }

    // Builds a label + field group and wires the field reference
    private JPanel fieldGroup(String labelText, boolean isPassword) {
        JPanel group = new JPanel();
        group.setOpaque(false);
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setMaximumSize(new Dimension(380, 70));
        group.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Small uppercase label
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(C_PURPLE);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        group.add(lbl);
        group.add(Box.createVerticalStrut(5));

        if (isPassword) {
            passwordField = new UnderlinePasswordField("Enter password");
            passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
            group.add(passwordField);
        } else {
            usernameField = new UnderlineField("Enter username");
            usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
            group.add(usernameField);
        }

        return group;
    }

    // OR divider
    private static JComponent buildOrDivider() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cy = getHeight() / 2;
                g2.setColor(C_DIVIDER);
                g2.drawLine(0, cy, getWidth() / 2 - 22, cy);
                g2.drawLine(getWidth() / 2 + 22, cy, getWidth(), cy);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                g2.setColor(new Color(100, 100, 130));
                FontMetrics fm = g2.getFontMetrics();
                String or = "OR";
                g2.drawString(or, (getWidth() - fm.stringWidth(or)) / 2, cy + fm.getAscent() / 2);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setMaximumSize (new Dimension(380, 18));
        p.setPreferredSize(new Dimension(380, 18));
        p.setAlignmentX(Component.CENTER_ALIGNMENT);
        return p;
    }

    // ── Auth Handlers ────────────────────────────────────────────────────────
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Username and password are required!");
            return;
        }

        loginBtn.setEnabled(false);
        loginBtn.setText("LOGGING IN...");

        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override protected User doInBackground() { return AuthService.login(username, password); }
            @Override protected void done() {
                try {
                    User user = get();
                    if (user != null) {
                        StreakService.updateStreak(user.getUserId());
                        new DashboardFrame(user).setVisible(true);
                        dispose();
                    } else {
                        showError("Wrong username or password!");
                        loginBtn.setEnabled(true);
                        loginBtn.setText("LOGIN");
                    }
                } catch (Exception ex) {
                    showError("Error: " + ex.getMessage());
                    loginBtn.setEnabled(true);
                    loginBtn.setText("LOGIN");
                }
            }
        };
        worker.execute();
    }

    private void handleForgotPassword() {
        JTextField emailField = new JTextField();
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField.setBackground(C_FIELD_BG);
        emailField.setForeground(Color.WHITE);
        emailField.setCaretColor(Color.WHITE);
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_PURPLE, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        emailField.setPreferredSize(new Dimension(320, 40));

        JPanel dlg = new JPanel();
        dlg.setBackground(new Color(18, 18, 35));
        dlg.setLayout(new BoxLayout(dlg, BoxLayout.Y_AXIS));
        dlg.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel prompt = new JLabel("Enter your registered email:");
        prompt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        prompt.setForeground(new Color(200, 200, 220));
        prompt.setAlignmentX(Component.LEFT_ALIGNMENT);
        dlg.add(prompt);
        dlg.add(Box.createVerticalStrut(10));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        dlg.add(emailField);

        int opt = JOptionPane.showConfirmDialog(this, dlg, "Forgot Password",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opt != JOptionPane.OK_OPTION) return;

        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your email.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        boolean ok = UserDAO.resetPasswordByEmail(email);
        if (ok) JOptionPane.showMessageDialog(this, "Password reset! New password: temp1234", "Done", JOptionPane.INFORMATION_MESSAGE);
        else    JOptionPane.showMessageDialog(this, "Email not found or reset failed.", "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showError(String msg) {
        statusLabel.setText(msg);
        statusLabel.setVisible(true);
        statusLabel.revalidate();
        statusLabel.repaint();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  CUSTOM COMPONENT CLASSES
    // ════════════════════════════════════════════════════════════════════════

    // ── Bottom-underline text field ──────────────────────────────────────────
    static class UnderlineField extends JTextField implements FocusListener {
        private final String placeholder;
        private boolean      placeholderActive = true;
        private boolean      focused           = false;

        UnderlineField(String placeholder) {
            this.placeholder = placeholder;
            setOpaque(false);
            setBackground(new Color(0, 0, 0, 0));
            setForeground(new Color(150, 150, 180));
            setCaretColor(Color.WHITE);
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setBorder(BorderFactory.createEmptyBorder(6, 2, 6, 2));
            setPreferredSize(new Dimension(380, 42));
            setMaximumSize (new Dimension(380, 42));
            setText(placeholder);
            addFocusListener(this);
        }

        @Override public String getText() { return placeholderActive ? "" : super.getText(); }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // field background
            g2.setColor(new Color(22, 22, 45));
            g2.fillRect(0, 0, getWidth(), getHeight());
            // bottom line
            int lineY = getHeight() - 1;
            if (focused) {
                // glowing purple line
                g2.setColor(new Color(139, 92, 246, 60));
                g2.fillRect(0, lineY - 1, getWidth(), 3);
                g2.setColor(new Color(139, 92, 246));
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(0, lineY, getWidth(), lineY);
            } else {
                g2.setColor(C_BORDER_IDLE);
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(0, lineY, getWidth(), lineY);
            }
            g2.dispose();
            super.paintComponent(g);
        }

        @Override public void focusGained(FocusEvent e) {
            focused = true;
            if (placeholderActive) {
                placeholderActive = false;
                setText("");
                setForeground(Color.WHITE);
            }
            repaint();
        }
        @Override public void focusLost(FocusEvent e) {
            focused = false;
            if (super.getText().trim().isEmpty()) {
                placeholderActive = true;
                setText(placeholder);
                setForeground(new Color(150, 150, 180));
            }
            repaint();
        }
    }

    // ── Bottom-underline password field ─────────────────────────────────────
    static class UnderlinePasswordField extends JPasswordField implements FocusListener {
        private final String placeholder;
        private boolean      placeholderActive = true;
        private boolean      focused           = false;
        private final char   realEcho;

        UnderlinePasswordField(String placeholder) {
            this.placeholder = placeholder;
            realEcho = getEchoChar();
            setEchoChar((char) 0);
            setOpaque(false);
            setBackground(new Color(0, 0, 0, 0));
            setForeground(new Color(150, 150, 180));
            setCaretColor(Color.WHITE);
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setBorder(BorderFactory.createEmptyBorder(6, 2, 6, 2));
            setPreferredSize(new Dimension(380, 42));
            setMaximumSize (new Dimension(380, 42));
            setText(placeholder);
            addFocusListener(this);
        }

        @Override public char[] getPassword() { return placeholderActive ? new char[0] : super.getPassword(); }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(22, 22, 45));
            g2.fillRect(0, 0, getWidth(), getHeight());
            int lineY = getHeight() - 1;
            if (focused) {
                g2.setColor(new Color(139, 92, 246, 60));
                g2.fillRect(0, lineY - 1, getWidth(), 3);
                g2.setColor(new Color(139, 92, 246));
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(0, lineY, getWidth(), lineY);
            } else {
                g2.setColor(C_BORDER_IDLE);
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(0, lineY, getWidth(), lineY);
            }
            g2.dispose();
            super.paintComponent(g);
        }

        @Override public void focusGained(FocusEvent e) {
            focused = true;
            if (placeholderActive) {
                placeholderActive = false;
                setText("");
                setEchoChar(realEcho);
                setForeground(Color.WHITE);
            }
            repaint();
        }
        @Override public void focusLost(FocusEvent e) {
            focused = false;
            if (new String(super.getPassword()).trim().isEmpty()) {
                placeholderActive = true;
                setText(placeholder);
                setEchoChar((char) 0);
                setForeground(new Color(150, 150, 180));
            }
            repaint();
        }
    }

    // ── Filled purple gradient login button ──────────────────────────────────
    static class PurpleButton extends JButton {
        private boolean hovered = false;
        private String label;

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
            // Subtle vertical gradient on the button itself
            GradientPaint gp = new GradientPaint(
                0, 0,          base.brighter(),
                0, getHeight(), base
            );
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

            // Thin highlight line on top
            g2.setColor(new Color(255, 255, 255, 30));
            g2.fillRoundRect(0, 0, getWidth(), getHeight() / 3, 8, 8);

            // Text
            g2.setFont(getFont());
            g2.setColor(getForeground());
            FontMetrics fm = g2.getFontMetrics();
            int tx = (getWidth()  - fm.stringWidth(label)) / 2;
            int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(label, tx, ty);

            g2.dispose();
        }
    }

    // ── Outline purple register button ───────────────────────────────────────
    static class OutlineButton extends JButton {
        private boolean hovered = false;

        OutlineButton(String text) {
            super(text);
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setForeground(C_PURPLE);
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

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (hovered) {
                g2.setColor(new Color(139, 92, 246, 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            }
            g2.setColor(C_PURPLE);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

            // Text
            g2.setFont(getFont());
            g2.setColor(getForeground());
            FontMetrics fm = g2.getFontMetrics();
            String txt = getText();
            int tx = (getWidth()  - fm.stringWidth(txt)) / 2;
            int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(txt, tx, ty);

            g2.dispose();
        }
    }

    // ── Entry point ──────────────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new LoginFrame().setVisible(true);
        });
    }
}