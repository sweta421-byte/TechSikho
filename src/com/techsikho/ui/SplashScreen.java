package com.techsikho.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class SplashScreen extends JFrame {
    private float alpha = 0f;
    private Timer fadeTimer;
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

    static class LogoPanel extends JPanel {
        float alpha = 0f;
        LogoPanel() { setOpaque(false); }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            int cx = getWidth()/2, cy = getHeight()/2;
            g2.setColor(new Color(5,5,5,220));
            g2.fillRoundRect(cx-170, cy-135, 340, 275, 16, 16);
            g2.setColor(new Color(45,212,191,90));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(cx-170, cy-135, 340, 275, 16, 16);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 86));
            g2.setColor(new Color(0x0ea5e9));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString("TS", cx - fm.stringWidth("TS")/2, cy-18);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 28));
            g2.setColor(Color.WHITE);
            fm = g2.getFontMetrics();
            g2.drawString("TechSikho", cx - fm.stringWidth("TechSikho")/2, cy+32);
            g2.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            g2.setColor(new Color(45,212,191,170));
            fm = g2.getFontMetrics();
            String tag = "Learn. Code. Level Up.";
            g2.drawString(tag, cx - fm.stringWidth(tag)/2, cy+60);
            g2.setColor(new Color(25,35,50));
            g2.fillRoundRect(cx-130, cy+88, 260, 3, 3, 3);
            g2.setColor(new Color(0x0ea5e9));
            g2.fillRoundRect(cx-130, cy+88, (int)(260*alpha), 3, 3, 3);
        }
    }

    public SplashScreen() {
        setUndecorated(true);
        setSize(960, 600);
        setLocationRelativeTo(null);

        JLayeredPane lp = new JLayeredPane();
        lp.setPreferredSize(new Dimension(960,600));
        lp.setBackground(new Color(0x0a0a0a));
        lp.setOpaque(true);
        setContentPane(lp);

        matrix = new MatrixPanel(960, 600);
        matrix.setBounds(0, 0, 960, 600);
        lp.add(matrix, Integer.valueOf(0));

        LogoPanel logo = new LogoPanel();
        logo.setBounds(0, 0, 960, 600);
        lp.add(logo, Integer.valueOf(1));

        // Click anywhere to skip
        logo.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { if (e.getClickCount() < 1) return;
                fadeTimer.stop();
                matrix.stop();
                dispose();
                new HomeScreen().setVisible(true);
            }
        });

        fadeTimer = new Timer(20, null);
        fadeTimer.addActionListener(e -> {
            alpha += 0.018f;
            if (alpha >= 1f) {
                alpha = 1f;
                fadeTimer.stop();
                Timer wait = new Timer(8000, ev -> {
                    matrix.stop();
                    dispose();
                    new HomeScreen().setVisible(true);
                });
                wait.setRepeats(false);
                wait.start();
            }
            logo.alpha = alpha;
            logo.repaint();
        });
        fadeTimer.start();
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SplashScreen());
    }
}