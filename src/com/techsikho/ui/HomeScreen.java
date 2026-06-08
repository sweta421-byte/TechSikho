package com.techsikho.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class HomeScreen extends JFrame {

    private MatrixPanel matrix;
    private Timer typingTimer;
    private String fullText = "Learn. Code. Level Up.";
    private String displayText = "";
    private int charIndex = 0;
    private JLabel typingLabel;

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

    public HomeScreen() {
        setUndecorated(true);
        setSize(1100, 680);
        setLocationRelativeTo(null);

        JLayeredPane lp = new JLayeredPane();
        lp.setBackground(new Color(0x0a0a0a));
        lp.setOpaque(true);
        setContentPane(lp);

        matrix = new MatrixPanel(1100, 680);
        matrix.setBounds(0, 0, 1100, 680);
        lp.add(matrix, Integer.valueOf(0));

        JPanel center = new JPanel(null);
        center.setOpaque(false);
        center.setBounds(0, 0, 1100, 680);
        lp.add(center, Integer.valueOf(1));

        JButton closeBtn = new JButton("X");
        closeBtn.setBounds(1060, 10, 30, 28);
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
        center.add(closeBtn);

        final Point[] drag = {null};
        center.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { drag[0] = e.getPoint(); }
        });
        center.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point loc = getLocation();
                setLocation(loc.x + e.getX() - drag[0].x, loc.y + e.getY() - drag[0].y);
            }
        });

        JLabel badge = new JLabel("GAMIFIED LEARNING PLATFORM", SwingConstants.CENTER);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setForeground(new Color(0x0ea5e9));
        badge.setBounds(350, 80, 400, 24);
        center.add(badge);

        JLabel title1 = new JLabel("Master Coding", SwingConstants.CENTER);
        title1.setFont(new Font("Segoe UI", Font.BOLD, 58));
        title1.setForeground(Color.WHITE);
        title1.setBounds(150, 110, 800, 70);
        center.add(title1);

        JLabel title2 = new JLabel("The Fun Way", SwingConstants.CENTER);
        title2.setFont(new Font("Segoe UI", Font.BOLD, 58));
        title2.setForeground(new Color(0x0ea5e9));
        title2.setBounds(150, 175, 800, 70);
        center.add(title2);

        typingLabel = new JLabel("", SwingConstants.CENTER);
        typingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        typingLabel.setForeground(new Color(0x94a3b8));
        typingLabel.setBounds(150, 255, 800, 30);
        center.add(typingLabel);

        String[] cardTitles = {"Gamified", "Boss Battle", "XP & Levels", "Mini Games"};
        String[] descs = {"Learn by playing", "Fight to win XP", "Level up skills", "Fun challenges"};
        Color[] colors = {
            new Color(0x0ea5e9), new Color(0x06b6d4),
            new Color(0x0284c7), new Color(0x0369a1)
        };

        for (int i = 0; i < 4; i++) {
            final int idx = i;
            JPanel card = new JPanel(null) {
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(5,5,5,200));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    g2.setColor(colors[idx]);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                    g2.fillRoundRect(20, 0, 40, 3, 2, 2);
                }
            };
            card.setBounds(60 + i * 245, 310, 220, 110);
            card.setOpaque(false);

            JLabel t = new JLabel(cardTitles[idx], SwingConstants.CENTER);
            t.setFont(new Font("Segoe UI", Font.BOLD, 15));
            t.setForeground(colors[idx]);
            t.setBounds(0, 20, 220, 25);
            card.add(t);

            JLabel d = new JLabel(descs[idx], SwingConstants.CENTER);
            d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            d.setForeground(new Color(0x64748b));
            d.setBounds(0, 52, 220, 20);
            card.add(d);

            center.add(card);
        }

        JButton startBtn = new JButton("Get Started") {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(0x0284c7) : new Color(0x0ea5e9));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2,
                    (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        startBtn.setBounds(400, 460, 300, 54);
        startBtn.setContentAreaFilled(false);
        startBtn.setBorderPainted(false);
        startBtn.setFocusPainted(false);
        startBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        startBtn.addActionListener(e -> { matrix.stop(); dispose(); new LoginFrame().setVisible(true); });
        center.add(startBtn);

        JButton loginLink = new JButton("Already have an account? Login") {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                g2.setColor(getModel().isRollover() ? new Color(0x0ea5e9) : new Color(0x475569));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2,
                    (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        loginLink.setBounds(350, 525, 400, 30);
        loginLink.setContentAreaFilled(false);
        loginLink.setBorderPainted(false);
        loginLink.setFocusPainted(false);
        loginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginLink.addActionListener(e -> { matrix.stop(); dispose(); new LoginFrame().setVisible(true); });
        center.add(loginLink);

        JLabel ver = new JLabel("v1.0 | Built with Java Swing", SwingConstants.CENTER);
        ver.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        ver.setForeground(new Color(0x1e293b));
        ver.setBounds(350, 620, 400, 20);
        center.add(ver);

        typingTimer = new Timer(60, null);
        typingTimer.addActionListener(e -> {
            if (charIndex < fullText.length()) {
                displayText += fullText.charAt(charIndex);
                charIndex++;
                typingLabel.setText(displayText + "|");
            } else {
                typingLabel.setText(displayText);
                typingTimer.stop();
            }
        });
        typingTimer.start();
        setVisible(true);
    }
}