package com.techsikho.ui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class HomeFrame extends JFrame {
    public HomeFrame() {
        setTitle("TechSikho");
        setSize(1200,750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(8,5,25));

        // NAVBAR
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(new Color(12,8,35));
        nav.setPreferredSize(new Dimension(1200,60));
        nav.setBorder(BorderFactory.createMatteBorder(0,0,1,0,new Color(139,92,246,60)));
        JLabel logo = new JLabel("   TechSikho");
        logo.setFont(new Font("Segoe UI",Font.BOLD,22));
        logo.setForeground(new Color(167,139,250));
        JPanel navRight = new JPanel(new FlowLayout(FlowLayout.RIGHT,12,12));
        navRight.setBackground(new Color(12,8,35));
        JButton loginBtn = new JButton("Sign In");
        loginBtn.setFont(new Font("Segoe UI",Font.PLAIN,13));
        loginBtn.setForeground(new Color(167,139,250));
        loginBtn.setBackground(new Color(12,8,35));
        loginBtn.setBorder(BorderFactory.createLineBorder(new Color(139,92,246),1));
        loginBtn.setFocusPainted(false);
        loginBtn.setPreferredSize(new Dimension(85,32));
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginBtn.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });
        JButton regBtn = new JButton("Get Started");
        regBtn.setFont(new Font("Segoe UI",Font.BOLD,13));
        regBtn.setForeground(Color.WHITE);
        regBtn.setBackground(new Color(124,58,237));
        regBtn.setBorder(BorderFactory.createEmptyBorder(6,14,6,14));
        regBtn.setFocusPainted(false);
        regBtn.setPreferredSize(new Dimension(115,32));
        regBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        regBtn.addActionListener(e -> { dispose(); new RegisterFrame().setVisible(true); });
        JButton closeBtn = new JButton("X");
        closeBtn.setFont(new Font("Segoe UI",Font.BOLD,13));
        closeBtn.setForeground(new Color(150,150,180));
        closeBtn.setBackground(new Color(12,8,35));
        closeBtn.setBorder(BorderFactory.createEmptyBorder(4,12,4,12));
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e){ closeBtn.setForeground(new Color(239,68,68)); }
            public void mouseExited(MouseEvent e){ closeBtn.setForeground(new Color(150,150,180)); }
        });
        closeBtn.addActionListener(e -> System.exit(0));
        navRight.add(loginBtn); navRight.add(regBtn); navRight.add(closeBtn);
        nav.add(logo,BorderLayout.WEST); nav.add(navRight,BorderLayout.EAST);

        // HERO
        JPanel hero = new JPanel(){
            protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint bg=new GradientPaint(0,0,new Color(8,5,25),getWidth(),getHeight(),new Color(18,8,45));
                g2.setPaint(bg); g2.fillRect(0,0,getWidth(),getHeight());
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.08f));
                g2.setColor(new Color(139,92,246));
                g2.fillOval(-80,50,400,400);
                g2.fillOval(getWidth()-250,getHeight()-250,400,400);
            }
        };
        hero.setLayout(new GridBagLayout());

        JPanel hc = new JPanel();
        hc.setLayout(new BoxLayout(hc,BoxLayout.Y_AXIS));
        hc.setOpaque(false);

        JLabel badge = new JLabel("  GAMIFIED PROGRAMMING PLATFORM  ");
        badge.setFont(new Font("Segoe UI",Font.BOLD,11));
        badge.setForeground(new Color(167,139,250));
        badge.setBackground(new Color(139,92,246,30));
        badge.setOpaque(true);
        badge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(139,92,246,80),1),
            BorderFactory.createEmptyBorder(4,12,4,12)));
        badge.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel h1 = new JLabel("Learn to Code. Level Up. Win.");
        h1.setFont(new Font("Segoe UI",Font.BOLD,52));
        h1.setForeground(Color.WHITE);
        h1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel h2 = new JLabel("Master programming through games, boss battles & XP rewards.");
        h2.setFont(new Font("Segoe UI",Font.PLAIN,16));
        h2.setForeground(new Color(200,210,230));
        h2.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER,16,0));
        btnRow.setOpaque(false);
        JButton sb = new JButton("Get Started Free");
        sb.setFont(new Font("Segoe UI",Font.BOLD,15));
        sb.setForeground(Color.WHITE);
        sb.setBackground(new Color(124,58,237));
        sb.setBorder(BorderFactory.createEmptyBorder(12,28,12,28));
        sb.setFocusPainted(false);
        sb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sb.addActionListener(e -> { dispose(); new RegisterFrame().setVisible(true); });
        JButton si = new JButton("Sign In");
        si.setFont(new Font("Segoe UI",Font.PLAIN,15));
        si.setForeground(new Color(167,139,250));
        si.setBackground(new Color(8,5,25));
        si.setBorder(BorderFactory.createLineBorder(new Color(139,92,246),1));
        si.setFocusPainted(false);
        si.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        si.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });
        btnRow.add(sb); btnRow.add(si);

        JPanel statsRow = new JPanel(new FlowLayout(FlowLayout.CENTER,40,0));
        statsRow.setOpaque(false);
        String[][] stats={{"12+","Languages"},{"500+","Questions"},{"5","Mini Games"},{"XP","Rewards"}};
        for(String[] s:stats){
            JPanel sc=new JPanel(); sc.setLayout(new BoxLayout(sc,BoxLayout.Y_AXIS)); sc.setOpaque(false);
            JLabel num=new JLabel(s[0]); num.setFont(new Font("Segoe UI",Font.BOLD,28));
            num.setForeground(new Color(167,139,250)); num.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel lbl=new JLabel(s[1]); lbl.setFont(new Font("Segoe UI",Font.PLAIN,12));
            lbl.setForeground(new Color(160,160,200)); lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            sc.add(num); sc.add(lbl); statsRow.add(sc);
        }

        hc.add(badge);
        hc.add(Box.createVerticalStrut(20));
        hc.add(h1);
        hc.add(Box.createVerticalStrut(12));
        hc.add(h2);
        hc.add(Box.createVerticalStrut(28));
        hc.add(btnRow);
        hc.add(Box.createVerticalStrut(36));
        hc.add(statsRow);
        hero.add(hc);

        // FEATURES STRIP
        JPanel featStrip = new JPanel(new GridLayout(1,4,0,0));
        featStrip.setBackground(new Color(12,8,35));
        featStrip.setPreferredSize(new Dimension(1200,100));
        String[][] feats={
            {"BOSS BATTLES","Challenge bosses in timed quiz duels"},
            {"XP & LEVELS","Earn XP, level up & unlock rewards"},
            {"MINI GAMES","Word Scramble, Code Breaker & more"},
            {"LEADERBOARD","Compete globally with all learners"}
        };
        for(String[] f:feats){
            JPanel fc=new JPanel(); fc.setLayout(new BoxLayout(fc,BoxLayout.Y_AXIS));
            fc.setBackground(new Color(12,8,35));
            fc.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1,0,0,1,new Color(139,92,246,30)),
                BorderFactory.createEmptyBorder(16,24,16,24)));
            JLabel ft=new JLabel(f[0]); ft.setFont(new Font("Segoe UI",Font.BOLD,12));
            ft.setForeground(new Color(167,139,250));
            JLabel fd=new JLabel("<html><body style=width:150px>"+f[1]+"</body></html>");
            fd.setFont(new Font("Segoe UI",Font.PLAIN,12)); fd.setForeground(new Color(160,160,200));
            fc.add(ft); fc.add(Box.createVerticalStrut(5)); fc.add(fd);
            featStrip.add(fc);
        }

        root.add(nav,BorderLayout.NORTH);
        root.add(hero,BorderLayout.CENTER);
        root.add(featStrip,BorderLayout.SOUTH);
        setContentPane(root);
    }
}