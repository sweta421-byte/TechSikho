package com.techsikho.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SplashFrame extends JFrame {
    private float alpha = 0f;
    private Timer animTimer, dotTimer, autoTimer;
    private int dotCount = 0;

    public SplashFrame() {
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel main = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();

                // BG
                GradientPaint bg = new GradientPaint(0,0,new Color(5,3,20),w,h,new Color(20,8,50));
                g2.setPaint(bg); g2.fillRect(0,0,w,h);

                // Glow orbs
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.12f));
                g2.setColor(new Color(139,92,246));
                g2.fillOval(-100,-100,500,500);
                g2.fillOval(w-300,h-300,500,500);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.06f));
                g2.fillOval(w/2-200,h/2-200,400,400);

                // Grid
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.04f));
                g2.setColor(new Color(139,92,246));
                g2.setStroke(new BasicStroke(1));
                for(int x=0;x<w;x+=80) g2.drawLine(x,0,x,h);
                for(int y=0;y<h;y+=80) g2.drawLine(0,y,w,y);

                // Content fade
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,alpha));

                // TS Circle
                int cx = w/2, cy = h/2 - 120;
                g2.setColor(new Color(139,92,246,50));
                g2.fillOval(cx-75,cy-75,150,150);
                g2.setColor(new Color(139,92,246));
                g2.setStroke(new BasicStroke(3));
                g2.drawOval(cx-75,cy-75,150,150);
                g2.setFont(new Font("Segoe UI",Font.BOLD,56));
                g2.setColor(new Color(200,180,255));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("TS", cx - fm.stringWidth("TS")/2, cy + fm.getAscent()/2 - 8);

                // Title
                g2.setFont(new Font("Segoe UI",Font.BOLD,72));
                GradientPaint tg = new GradientPaint(cx-250,0,new Color(200,180,255),cx+250,0,new Color(99,102,241));
                g2.setPaint(tg);
                fm = g2.getFontMetrics();
                g2.drawString("TechSikho", cx - fm.stringWidth("TechSikho")/2, cy+120);

                // Tagline
                g2.setFont(new Font("Segoe UI",Font.ITALIC,22));
                g2.setColor(new Color(148,163,184));
                fm = g2.getFontMetrics();
                g2.drawString("Learn. Code. Level Up.", cx - fm.stringWidth("Learn. Code. Level Up.")/2, cy+162);

                // Loading dots
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));
                for(int i=0;i<5;i++){
                    if(i<dotCount) g2.setColor(new Color(139,92,246));
                    else g2.setColor(new Color(50,40,80));
                    g2.setFont(new Font("Segoe UI",Font.PLAIN,20));
                    g2.drawString("●", cx-44+i*22, h-60);
                }

                g2.setFont(new Font("Segoe UI",Font.PLAIN,13));
                g2.setColor(new Color(60,60,90));
                fm = g2.getFontMetrics();
                g2.drawString("Click anywhere to continue", cx - fm.stringWidth("Click anywhere to continue")/2, h-30);
            }
        };
        main.setBackground(new Color(5,3,20));
        main.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){ openHome(); }
        });
        main.setFocusable(true);
        main.addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent e){ openHome(); }
        });
        add(main);

        animTimer = new Timer(25, e -> {
            alpha = Math.min(1f, alpha+0.03f);
            main.repaint();
            if(alpha>=1f) ((Timer)e.getSource()).stop();
        });
        animTimer.start();

        dotTimer = new Timer(350, e -> { dotCount=(dotCount+1)%6; main.repaint(); });
        dotTimer.start();

        autoTimer = new Timer(8000, e -> openHome());
        autoTimer.setRepeats(false);
        autoTimer.start();

        main.requestFocusInWindow();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SplashFrame().setVisible(true));
    }

    private void openHome() {
        if(dotTimer!=null) dotTimer.stop();
        if(animTimer!=null) animTimer.stop();
        if(autoTimer!=null) autoTimer.stop();
        dispose();
        SwingUtilities.invokeLater(() -> new HomeFrame().setVisible(true));
    }
}