package com.techsikho.ui;

import com.techsikho.dao.UserDAO;
import com.techsikho.models.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel errorLabel;
    private MatrixPanel matrix;

    static class MatrixPanel extends JPanel {
        private static final int FS = 15;
        private int cols;
        private int[] drops;
        private Color[] colColor;
        private Timer timer;
        private Random rand = new Random();
        private int W, H;
        private BufferedImage buffer;
        private Graphics2D bg2;

        MatrixPanel(int w, int h) {
            this.W = w; this.H = h;
            setOpaque(true);
            buffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            bg2 = buffer.createGraphics();
            bg2.setColor(new Color(0x0a0a0a));
            bg2.fillRect(0, 0, w, h);
            cols = w / FS;
            drops = new int[cols];
            colColor = new Color[cols];
            for (int i = 0; i < cols; i++) {
                drops[i] = -(rand.nextInt(h / FS));
                float t = (float)i / cols;
                colColor[i] = new Color(
                    Math.min(255,(int)(45+t*140)),
                    Math.min(255,(int)(210-t*30)),
                    Math.min(255,(int)(185+t*25)));
            }
            String pool = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            timer = new Timer(45, e -> {
                bg2.setColor(new Color(10,10,10,55));
                bg2.fillRect(0,0,W,H);
                bg2.setFont(new Font("Monospaced",Font.BOLD,FS));
                for (int i = 0; i < cols; i++) {
                    char ch = pool.charAt(rand.nextInt(pool.length()));
                    int x = i*FS, y = drops[i]*FS;
                    bg2.setColor(colColor[i]);
                    if (y > 0 && y < H) bg2.drawString(String.valueOf(ch), x, y);
                    drops[i]++;
                    if (drops[i]*FS > H && rand.nextFloat() > 0.97f)
                        drops[i] = -(rand.nextInt(15));
                }
                repaint();
            });
            timer.start();
        }

        void stop() { timer.stop(); bg2.dispose(); }

        protected void paintComponent(Graphics g) {
            g.drawImage(buffer, 0, 0, null);
        }
    }

    public LoginFrame() {
        setUndecorated(true);
        setSize(960, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLayeredPane lp = new JLayeredPane();
        lp.setBackground(new Color(0x0a0a0a));
        lp.setOpaque(true);
        setContentPane(lp);

        matrix = new MatrixPanel(960, 600);
        matrix.setBounds(0, 0, 960, 600);
        lp.add(matrix, Integer.valueOf(0));

        JPanel root = new JPanel(null);
        root.setOpaque(false);
        root.setBounds(0, 0, 960, 600);
        lp.add(root, Integer.valueOf(1));

        // Title bar
        JPanel titleBar = new JPanel(null);
        titleBar.setOpaque(false);
        titleBar.setBounds(0, 0, 960, 36);

        JLabel appName = new JLabel("  TechSikho");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        appName.setForeground(new Color(0x0ea5e9));
        appName.setBounds(0, 0, 200, 36);

        JButton closeBtn = new JButton("X");
        closeBtn.setBounds(920, 4, 30, 28);
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setBackground(new Color(0x1a1a1a));
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { closeBtn.setBackground(new Color(0xef4444)); }
            public void mouseExited(MouseEvent e) { closeBtn.setBackground(new Color(0x1a1a1a)); }
        });
        closeBtn.addActionListener(e -> System.exit(0));
        titleBar.add(appName);
        titleBar.add(closeBtn);
        root.add(titleBar);

        final Point[] dragPoint = {null};
        titleBar.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { dragPoint[0] = e.getPoint(); }
        });
        titleBar.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point loc = getLocation();
                setLocation(loc.x + e.getX() - dragPoint[0].x, loc.y + e.getY() - dragPoint[0].y);
            }
        });

        // LEFT PANEL
        JPanel left = new JPanel(null) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(5,5,5,200));
                g2.fillRect(0,0,getWidth(),getHeight());
            }
        };
        left.setOpaque(false);
        left.setBounds(0, 36, 380, 564);

        JLabel logo = new JLabel("TS", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 72));
        logo.setForeground(new Color(0x0ea5e9));
        logo.setBounds(90, 60, 200, 90);
        left.add(logo);

        JLabel brand = new JLabel("TechSikho", SwingConstants.CENTER);
        brand.setFont(new Font("Segoe UI", Font.BOLD, 26));
        brand.setForeground(Color.WHITE);
        brand.setBounds(70, 155, 240, 35);
        left.add(brand);

        JLabel tagline = new JLabel("Learn. Code. Level Up.", SwingConstants.CENTER);
        tagline.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        tagline.setForeground(new Color(0x64748b));
        tagline.setBounds(70, 193, 240, 25);
        left.add(tagline);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(0x0ea5e9));
        sep.setBounds(140, 228, 100, 2);
        left.add(sep);

        String[] features = {"Gamified Learning","Boss Battles","XP & Levels","Mini Games"};
        for (int i = 0; i < features.length; i++) {
            JLabel feat = new JLabel(">>> " + features[i]);
            feat.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            feat.setForeground(new Color(0xcbd5e1));
            feat.setBounds(80, 255 + i * 36, 220, 28);
            left.add(feat);
        }

        JLabel ver = new JLabel("v1.0", SwingConstants.CENTER);
        ver.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        ver.setForeground(new Color(0x334155));
        ver.setBounds(140, 510, 100, 20);
        left.add(ver);

        // Divider
        JPanel divider = new JPanel() {
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(0x0ea5e9));
                g.fillRect(0,0,getWidth(),getHeight());
            }
        };
        divider.setBounds(380, 36, 1, 564);
        divider.setOpaque(false);
        root.add(divider);
        root.add(left);

        // RIGHT PANEL
        JPanel right = new JPanel(null);
        right.setOpaque(false);
        right.setBounds(381, 36, 579, 564);

        JLabel welcome = new JLabel("Welcome Back", SwingConstants.CENTER);
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 32));
        welcome.setForeground(Color.WHITE);
        welcome.setBounds(90, 60, 400, 45);
        right.add(welcome);

        JPanel accent = new JPanel() {
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(0x0ea5e9));
                g.fillRoundRect(0,0,getWidth(),getHeight(),4,4);
            }
        };
        accent.setBounds(235, 110, 110, 3);
        accent.setOpaque(false);
        right.add(accent);

        JLabel sub = new JLabel("Sign in to continue your journey", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(0x64748b));
        sub.setBounds(90, 122, 400, 25);
        right.add(sub);

        JLabel userLbl = new JLabel("USERNAME");
        userLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        userLbl.setForeground(new Color(0x0ea5e9));
        userLbl.setBounds(90, 175, 200, 18);
        right.add(userLbl);

        usernameField = new JTextField();
        usernameField.setBounds(90, 196, 400, 44);
        usernameField.setBackground(new Color(0x0d1520));
        usernameField.setForeground(Color.WHITE);
        usernameField.setCaretColor(new Color(0x0ea5e9));
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x1e3a5f), 1),
            BorderFactory.createEmptyBorder(0, 12, 0, 12)));
        usernameField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                usernameField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0x0ea5e9), 1),
                    BorderFactory.createEmptyBorder(0, 12, 0, 12)));
            }
            public void focusLost(FocusEvent e) {
                usernameField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0x1e3a5f), 1),
                    BorderFactory.createEmptyBorder(0, 12, 0, 12)));
            }
        });
        right.add(usernameField);

        JLabel passLbl = new JLabel("PASSWORD");
        passLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        passLbl.setForeground(new Color(0x0ea5e9));
        passLbl.setBounds(90, 255, 200, 18);
        right.add(passLbl);

        passwordField = new JPasswordField();
        passwordField.setBounds(90, 276, 360, 44);
        passwordField.setBackground(new Color(0x0d1520));
        passwordField.setForeground(Color.WHITE);
        passwordField.setCaretColor(new Color(0x0ea5e9));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x1e3a5f), 1),
            BorderFactory.createEmptyBorder(0, 12, 0, 12)));
        passwordField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0x0ea5e9), 1),
                    BorderFactory.createEmptyBorder(0, 12, 0, 12)));
            }
            public void focusLost(FocusEvent e) {
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0x1e3a5f), 1),
                    BorderFactory.createEmptyBorder(0, 12, 0, 12)));
            }
        });
        right.add(passwordField);
        JButton eyeBtn = new JButton("👁");
        eyeBtn.setBounds(455, 276, 44, 44);
        eyeBtn.setBackground(new Color(0x0d1520));
        eyeBtn.setForeground(new Color(0x0ea5e9));
        eyeBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        eyeBtn.setFocusPainted(false);
        eyeBtn.setBorderPainted(false);
        eyeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        eyeBtn.setBorder(BorderFactory.createLineBorder(new Color(0x0ea5e9), 1));
        final boolean[] showing = {false};
        eyeBtn.addActionListener(e -> {
            showing[0] = !showing[0];
            passwordField.setEchoChar(showing[0] ? (char)0 : (char)0x2022);
            eyeBtn.setText(showing[0] ? "🙈" : "👁");
        });
        right.add(eyeBtn);

        errorLabel = new JLabel("", SwingConstants.CENTER);
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(new Color(0xef4444));
        errorLabel.setBounds(90, 330, 400, 20);
        right.add(errorLabel);

        JButton loginBtn = new JButton("LOGIN") {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(0x0284c7) : new Color(0x0ea5e9));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,
                    (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        loginBtn.setBounds(90, 360, 400, 50);
        loginBtn.setContentAreaFilled(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setFocusPainted(false);
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginBtn.addActionListener(e -> doLogin());

        JButton forgotBtn = new JButton("Forgot Password?") {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g2.setColor(getModel().isRollover() ? new Color(0x0ea5e9) : new Color(0x475569));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2, (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        forgotBtn.setBounds(90, 418, 400, 26);
        forgotBtn.setContentAreaFilled(false);
        forgotBtn.setBorderPainted(false);
        forgotBtn.setFocusPainted(false);
        forgotBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotBtn.addActionListener(e -> { String email = JOptionPane.showInputDialog(null, "Enter your registered email:", "Forgot Password", JOptionPane.QUESTION_MESSAGE); if (email != null && !email.trim().isEmpty()) { boolean success = com.techsikho.dao.UserDAO.resetPasswordByEmail(email.trim()); if (success) { JOptionPane.showMessageDialog(null, "Password reset!\nTemporary password: temp1234\nLogin aur password change karo.", "Success", JOptionPane.INFORMATION_MESSAGE); } else { JOptionPane.showMessageDialog(null, "Email not found!", "Error", JOptionPane.ERROR_MESSAGE); } } });
        right.add(forgotBtn);
        right.add(loginBtn);

        passwordField.addActionListener(e -> doLogin());

        JLabel divLine = new JLabel("------------- OR -------------", SwingConstants.CENTER);
        divLine.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        divLine.setForeground(new Color(0x1e293b));
        divLine.setBounds(90, 422, 400, 20);
        right.add(divLine);

        JButton regBtn = new JButton("Create New Account") {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(14,165,233,20));
                    g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                }
                g2.setColor(new Color(0x0ea5e9));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,8,8);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,
                    (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        regBtn.setBounds(90, 452, 400, 44);
        regBtn.setContentAreaFilled(false);
        regBtn.setBorderPainted(false);
        regBtn.setFocusPainted(false);
        regBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        regBtn.addActionListener(e -> { matrix.stop(); dispose(); new RegisterFrame().setVisible(true); });
        right.add(regBtn);

        root.add(right);
        setVisible(true);
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill all fields!");
            return;
        }
        User user = UserDAO.loginUser(username, password);
        if (user == null) {
            try { Thread.sleep(500); } catch (Exception ex) {}
            user = UserDAO.loginUser(username, password);
        }
        if (user != null) {
            matrix.stop();
            dispose();
            new DashboardFrame(user).setVisible(true);
        } else {
            errorLabel.setText("Invalid username or password!");
            passwordField.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}