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

        void resizeMatrix(int w, int h) {
            this.W = w; this.H = h;
            buffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            bg2 = buffer.createGraphics();
            bg2.setColor(new Color(0x0d1520));
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
            setPreferredSize(new Dimension(w, h));
            repaint();
        }

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
            // background card
            g2.setColor(new Color(5,5,5,220));
            g2.fillRoundRect(cx-190, cy-160, 380, 320, 16, 16);
            g2.setColor(new Color(45,212,191,90));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(cx-190, cy-160, 380, 320, 16, 16);
            // robot
            int rx = cx, ry = cy-140;
            // crown
            int[]crx={rx-14,rx-9,rx-4,rx,rx+4,rx+9,rx+14};
            int[]cry={ry+12,ry+4,ry+9,ry+2,ry+9,ry+4,ry+12};
            g2.setColor(new Color(0x0ea5e9));g2.fillPolygon(crx,cry,7);
            g2.setColor(new Color(0x2dd4bf));g2.setStroke(new BasicStroke(1f));g2.drawPolygon(crx,cry,7);
            // head
            g2.setColor(new Color(0x0d1520));g2.fillRoundRect(rx-24,ry+12,48,32,8,8);
            g2.setColor(new Color(0x0ea5e9));g2.setStroke(new BasicStroke(2f));g2.drawRoundRect(rx-24,ry+12,48,32,8,8);
            // eyes
            g2.setColor(new Color(0x2dd4bf));g2.fillRoundRect(rx-18,ry+20,14,9,3,3);g2.fillRoundRect(rx+4,ry+20,14,9,3,3);
            g2.setColor(Color.WHITE);g2.fillOval(rx-14,ry+22,6,5);g2.fillOval(rx+8,ry+22,6,5);
            // mouth
            g2.setColor(new Color(0x0ea5e9));g2.fillRoundRect(rx-12,ry+33,24,5,3,3);
            // neck
            g2.setColor(new Color(0x0d1520));g2.fillRect(rx-6,ry+44,12,7);
            g2.setColor(new Color(0x0ea5e9));g2.setStroke(new BasicStroke(1f));g2.drawRect(rx-6,ry+44,12,7);
            // body
            g2.setColor(new Color(0x071520));g2.fillRoundRect(rx-30,ry+51,60,40,6,6);
            g2.setColor(new Color(0x0ea5e9));g2.setStroke(new BasicStroke(2f));g2.drawRoundRect(rx-30,ry+51,60,40,6,6);
            // chest TS
            g2.setFont(new Font("Segoe UI",Font.BOLD,18));
            g2.setColor(new Color(0x0ea5e9));g2.drawString("T",rx-12,ry+78);
            g2.setColor(new Color(0x2dd4bf));g2.drawString("S",rx+2,ry+78);
            // arms - symmetric
            g2.setColor(new Color(0x0d1520));
            g2.fillRoundRect(rx-48,ry+51,16,28,4,4);g2.fillRoundRect(rx+32,ry+51,16,28,4,4);
            g2.setColor(new Color(0x0ea5e9));g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(rx-48,ry+51,16,28,4,4);g2.drawRoundRect(rx+32,ry+51,16,28,4,4);
            // hands
            g2.setColor(new Color(0x0d1520));
            g2.fillRoundRect(rx-48,ry+79,16,12,3,3);g2.fillRoundRect(rx+32,ry+79,16,12,3,3);
            g2.setColor(new Color(0x2dd4bf));g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(rx-48,ry+79,16,12,3,3);g2.drawRoundRect(rx+32,ry+79,16,12,3,3);
            // legs
            g2.setColor(new Color(0x0d1520));
            g2.fillRoundRect(rx-22,ry+91,18,20,4,4);g2.fillRoundRect(rx+4,ry+91,18,20,4,4);
            g2.setColor(new Color(0x0ea5e9));g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(rx-22,ry+91,18,20,4,4);g2.drawRoundRect(rx+4,ry+91,18,20,4,4);
            // amber dots
            g2.setColor(new Color(0xf59e0b));g2.fillOval(rx-38,ry+5,6,6);g2.fillOval(rx+32,ry+5,6,6);
            g2.setColor(new Color(0x2dd4bf));g2.fillOval(rx-28,ry+2,4,4);g2.fillOval(rx+24,ry+2,4,4);
            // TECHSIKHO
            g2.setFont(new Font("Segoe UI", Font.BOLD, 38));
            g2.setColor(new Color(0x0ea5e9));
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth("TECHSIKHO");
            g2.drawString("TECH", cx-tw/2, cy+30);
            g2.setColor(new Color(0x2dd4bf));
            g2.drawString("SIKHO", cx-tw/2+fm.stringWidth("TECH"), cy+30);
            // tagline
            g2.setFont(new Font("Segoe UI", Font.ITALIC, 15));
            g2.setColor(new Color(45,212,191,170));
            fm = g2.getFontMetrics();
            String tag = "Learn. Code. Level Up.";
            g2.drawString(tag, cx-fm.stringWidth(tag)/2, cy+58);
            // progress bar
            g2.setColor(new Color(25,35,50));
            g2.fillRoundRect(cx-140, cy+80, 280, 4, 4, 4);
            g2.setColor(new Color(0x0ea5e9));
            g2.fillRoundRect(cx-140, cy+80, (int)(280*alpha), 4, 4, 4);
        }
    }

    public SplashScreen() {
        setUndecorated(false);
        setTitle("TechSikho");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
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

        // Resize listener - center logo and fill bg on maximize
        lp.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                int w = lp.getWidth(), h = lp.getHeight();
                matrix.setBounds(0, 0, w, h);
                matrix.resizeMatrix(w, h);
                logo.setBounds(0, 0, w, h);
                lp.revalidate();
                lp.repaint();
            }
        });

        logo.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                fadeTimer.stop(); matrix.stop(); dispose();
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
                    matrix.stop(); dispose();
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