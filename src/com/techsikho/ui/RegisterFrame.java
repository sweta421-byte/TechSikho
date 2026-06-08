package com.techsikho.ui;

import com.techsikho.dao.UserDAO;
import com.techsikho.models.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class RegisterFrame extends JFrame {

    private JTextField nameField, usernameField, emailField;
    private JPasswordField passField, confirmField;
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

    public RegisterFrame() {
        setUndecorated(true);
        setSize(980, 680);
        setLocationRelativeTo(null);

        JLayeredPane lp = new JLayeredPane();
        lp.setBackground(new Color(0x0a0a0a));
        lp.setOpaque(true);
        setContentPane(lp);

        matrix = new MatrixPanel(980, 680);
        matrix.setBounds(0, 0, 980, 680);
        lp.add(matrix, Integer.valueOf(0));

        JPanel root = new JPanel(null);
        root.setOpaque(false);
        root.setBounds(0, 0, 980, 680);
        lp.add(root, Integer.valueOf(1));

        // Title bar
        JPanel tb = new JPanel(null);
        tb.setOpaque(false);
        tb.setBounds(0, 0, 980, 38);
        JLabel appLbl = new JLabel("   TechSikho");
        appLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        appLbl.setForeground(new Color(0x0ea5e9));
        appLbl.setBounds(0, 0, 180, 38);
        JButton xBtn = new JButton("X");
        xBtn.setBounds(944, 6, 28, 26);
        xBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        xBtn.setForeground(new Color(0x94a3b8));
        xBtn.setBackground(new Color(0x1a1a1a));
        xBtn.setBorderPainted(false);
        xBtn.setFocusPainted(false);
        xBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        xBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { xBtn.setBackground(new Color(0xef4444)); xBtn.setForeground(Color.WHITE); }
            public void mouseExited(MouseEvent e) { xBtn.setBackground(new Color(0x1a1a1a)); xBtn.setForeground(new Color(0x94a3b8)); }
        });
        xBtn.addActionListener(e -> System.exit(0));
        tb.add(appLbl); tb.add(xBtn);
        root.add(tb);

        final Point[] dp = {null};
        tb.addMouseListener(new MouseAdapter() { public void mousePressed(MouseEvent e) { dp[0] = e.getPoint(); } });
        tb.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point l = getLocation();
                setLocation(l.x + e.getX() - dp[0].x, l.y + e.getY() - dp[0].y);
            }
        });

        // LEFT PANEL
        JPanel left = new JPanel(null) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setColor(new Color(5, 5, 5, 200));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        left.setOpaque(false);
        left.setBounds(0, 38, 400, 642);

        JLabel logo = new JLabel("TS", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 72));
        logo.setForeground(new Color(0x0ea5e9));
        logo.setBounds(100, 60, 200, 90);
        left.add(logo);

        JLabel brand = new JLabel("TechSikho", SwingConstants.CENTER);
        brand.setFont(new Font("Segoe UI", Font.BOLD, 26));
        brand.setForeground(Color.WHITE);
        brand.setBounds(60, 158, 280, 36);
        left.add(brand);

        JLabel tag = new JLabel("Join thousands of learners!", SwingConstants.CENTER);
        tag.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        tag.setForeground(new Color(0x64748b));
        tag.setBounds(60, 198, 280, 24);
        left.add(tag);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(0x0ea5e9));
        sep.setBounds(150, 232, 100, 2);
        left.add(sep);

        String[] feats = {"  Gamified Learning", "  Boss Battles", "  XP & Levels", "  Mini Games"};
        Color[] fc = {new Color(0x0ea5e9), new Color(0x06b6d4), new Color(0x0284c7), new Color(0x0369a1)};
        for (int i = 0; i < feats.length; i++) {
            JLabel fl = new JLabel("> " + feats[i].trim());
            fl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            fl.setForeground(fc[i]);
            fl.setBounds(80, 255 + i * 38, 240, 26);
            left.add(fl);
        }

        JLabel ver = new JLabel("v1.0 | Java Swing", SwingConstants.CENTER);
        ver.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        ver.setForeground(new Color(0x1e293b));
        ver.setBounds(100, 615, 200, 18);
        left.add(ver);

        JPanel vdiv = new JPanel() {
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(0x0ea5e9));
                g.fillRect(0, 0, 1, getHeight());
            }
        };
        vdiv.setOpaque(false);
        vdiv.setBounds(400, 38, 1, 642);
        root.add(vdiv);
        root.add(left);

        // RIGHT PANEL
        JPanel right = new JPanel(null);
        right.setOpaque(false);
        right.setBounds(401, 38, 579, 642);

        JLabel title = new JLabel("Create Account", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        title.setBounds(60, 28, 460, 44);
        right.add(title);

        JPanel acc = new JPanel() {
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(0x0ea5e9));
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 3, 3);
            }
        };
        acc.setOpaque(false);
        acc.setBounds(200, 76, 180, 3);
        right.add(acc);

        JLabel sub = new JLabel("Fill in your details to get started", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(0x64748b));
        sub.setBounds(60, 86, 460, 22);
        right.add(sub);

        // Fields
        nameField = createField(right, "FULL NAME", false, 60, 122, 460, 44);
        usernameField = createField(right, "USERNAME", false, 60, 180, 460, 44);
        emailField = createField(right, "EMAIL", false, 60, 238, 460, 44);
        passField = (JPasswordField) createField(right, "PASSWORD", true, 60, 296, 460, 44);
        confirmField = (JPasswordField) createField(right, "CONFIRM PASSWORD", true, 60, 354, 460, 44);

        errorLabel = new JLabel("", SwingConstants.CENTER);
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        errorLabel.setForeground(new Color(0xef4444));
        errorLabel.setBounds(60, 408, 460, 18);
        right.add(errorLabel);

        JButton regBtn = new JButton("CREATE ACCOUNT") {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(0x0284c7) : new Color(0x0ea5e9));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2,
                    (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        regBtn.setBounds(60, 435, 460, 50);
        regBtn.setContentAreaFilled(false);
        regBtn.setBorderPainted(false);
        regBtn.setFocusPainted(false);
        regBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        regBtn.addActionListener(e -> doRegister());
        right.add(regBtn);

        JButton loginBtn = new JButton("Already have an account? Login") {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g2.setColor(getModel().isRollover() ? new Color(0x0ea5e9) : new Color(0x475569));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2,
                    (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        loginBtn.setBounds(60, 496, 460, 28);
        loginBtn.setContentAreaFilled(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setFocusPainted(false);
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginBtn.addActionListener(e -> { matrix.stop(); dispose(); new LoginFrame().setVisible(true); });

        JLabel orLabel = new JLabel("------- OR -------", SwingConstants.CENTER);
        orLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        orLabel.setForeground(new Color(0x334155));
        orLabel.setBounds(60, 532, 460, 20);
        right.add(orLabel);

        JButton googleBtn = new JButton("Continue with Google") {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(0x1e293b) : new Color(0x0d1520));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.setColor(new Color(0x334155));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.setColor(new Color(0xcbd5e1));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2, (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        googleBtn.setBounds(60, 558, 220, 42);
        googleBtn.setContentAreaFilled(false);
        googleBtn.setBorderPainted(false);
        googleBtn.setFocusPainted(false);
        googleBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        googleBtn.addActionListener(e -> JOptionPane.showMessageDialog(null, "Google OAuth coming soon!", "Info", JOptionPane.INFORMATION_MESSAGE));
        right.add(googleBtn);

        JButton githubBtn = new JButton("Continue with GitHub") {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(0x1e293b) : new Color(0x0d1520));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.setColor(new Color(0x334155));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.setColor(new Color(0xcbd5e1));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2, (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        githubBtn.setBounds(290, 558, 230, 42);
        githubBtn.setContentAreaFilled(false);
        githubBtn.setBorderPainted(false);
        githubBtn.setFocusPainted(false);
        githubBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        githubBtn.addActionListener(e -> JOptionPane.showMessageDialog(null, "GitHub OAuth coming soon!", "Info", JOptionPane.INFORMATION_MESSAGE));
        right.add(githubBtn);
        right.add(loginBtn);

        root.add(right);
        setVisible(true);
    }

    private JTextField createField(JPanel parent, String label, boolean isPass, int x, int y, int w, int h) {
        JTextField field = isPass ? new JPasswordField() : new JTextField();
        field.setBackground(new Color(0x0d1520));
        field.setForeground(Color.WHITE);
        field.setCaretColor(new Color(0x0ea5e9));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x1e3a5f), 1),
            BorderFactory.createEmptyBorder(0, 12, 0, 12)));
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0x0ea5e9), 1),
                    BorderFactory.createEmptyBorder(0, 12, 0, 12)));
            }
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0x1e3a5f), 1),
                    BorderFactory.createEmptyBorder(0, 12, 0, 12)));
            }
        });

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(new Color(0x0ea5e9));
        lbl.setBounds(x, y - 16, 300, 14);
        parent.add(lbl);

        field.setBounds(x, y, w, h);
        parent.add(field);
        return field;
    }

    private void doRegister() {
        String name = nameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String pass = new String(passField.getPassword());
        String confirm = new String(confirmField.getPassword());
        if (name.isEmpty() || username.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            errorLabel.setText("All fields are required!"); return;
        }
        if (!pass.equals(confirm)) {
            errorLabel.setText("Passwords do not match!"); return;
        }
        if (pass.length() < 6) {
            errorLabel.setText("Password must be at least 6 characters!"); return;
        }
        User user = new User();
        user.setFullName(name); user.setUsername(username);
        user.setEmail(email); user.setPasswordHash(pass);
        boolean success = UserDAO.registerUser(user);
        if (success) {
            errorLabel.setForeground(new Color(0x22c55e));
            errorLabel.setText("Account created! Redirecting to login...");
            Timer t = new Timer(1500, ev -> { matrix.stop(); dispose(); new LoginFrame().setVisible(true); });
            t.setRepeats(false); t.start();
        } else {
            errorLabel.setForeground(new Color(0xef4444));
            errorLabel.setText("Registration failed. Username may already exist.");
        }
    }
}