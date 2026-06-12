package com.techsikho.ui;
import com.techsikho.models.User;
import com.techsikho.dao.UserDAO;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private boolean passVisible = false;

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
            bg2.setColor(new Color(0x0a0a0a));
            bg2.fillRect(0, 0, w, h);
            cols = w / FS;
            drops = new int[cols];
            colColor = new Color[cols];
            for (int i = 0; i < cols; i++) {
                drops[i] = -(rand.nextInt(Math.max(1, h / FS)));
                float t = (float)i / cols;
                colColor[i] = new Color(Math.min(255,(int)(45+t*140)),Math.min(255,(int)(210-t*30)),Math.min(255,(int)(185+t*25)));
            }
            repaint();
        }

        MatrixPanel(int w, int h) {
            this.W = w; this.H = h;
            setOpaque(true);
            buffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            bg2 = buffer.createGraphics();
            bg2.setColor(new Color(0x0a0a0a));
            bg2.fillRect(0, 0, w, h);
            cols = w / FS; drops = new int[cols]; colColor = new Color[cols];
            for (int i = 0; i < cols; i++) {
                drops[i] = -(rand.nextInt(h / FS));
                float t = (float)i / cols;
                colColor[i] = new Color(Math.min(255,(int)(45+t*140)),Math.min(255,(int)(210-t*30)),Math.min(255,(int)(185+t*25)));
            }
            String pool = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            timer = new Timer(80, e -> {
                bg2.setColor(new Color(10,10,10,55)); bg2.fillRect(0,0,W,H);
                bg2.setFont(new Font("Monospaced",Font.BOLD,FS));
                for (int i = 0; i < cols; i++) {
                    char ch = pool.charAt(rand.nextInt(pool.length()));
                    int x = i*FS, y = drops[i]*FS;
                    bg2.setColor(colColor[i]);
                    if (y > 0 && y < H) bg2.drawString(String.valueOf(ch), x, y);
                    drops[i]++;
                    if (drops[i]*FS > H && rand.nextFloat() > 0.97f) drops[i] = -(rand.nextInt(15));
                }
                repaint();
            });
            timer.start();
        }
        protected void paintComponent(Graphics g) { g.drawImage(buffer, 0, 0, null); }
    }

    public LoginFrame() {
        setTitle("TechSikho");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setSize(960, 636);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        MatrixPanel root = new MatrixPanel(960, 600);
        root.setLayout(null);
        setContentPane(root);

        // LEFT PANEL
        JPanel left = new JPanel(null) {
            protected void paintComponent(Graphics g) {
                ((Graphics2D)g).setColor(new Color(5,8,15,210));
                g.fillRect(0,0,getWidth(),getHeight());
            }
        };
        left.setOpaque(false);
        left.setBounds(0, 0, 380, 600);
        root.add(left);

        JPanel robotLogo = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2=(Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                int cx=getWidth()/2, cy=5;
                int[]crx={cx-10,cx-7,cx-3,cx,cx+3,cx+7,cx+10};
                int[]cry={cy+9,cy+3,cy+7,cy+1,cy+7,cy+3,cy+9};
                g2.setColor(new Color(0x0ea5e9));g2.fillPolygon(crx,cry,7);
                g2.setColor(new Color(0x0d1520));g2.fillRoundRect(cx-18,cy+9,36,26,8,8);
                g2.setColor(new Color(0x0ea5e9));g2.setStroke(new BasicStroke(1.5f));g2.drawRoundRect(cx-18,cy+9,36,26,8,8);
                g2.setColor(new Color(0x2dd4bf));g2.fillRoundRect(cx-13,cy+15,10,7,3,3);g2.fillRoundRect(cx+3,cy+15,10,7,3,3);
                g2.setColor(Color.WHITE);g2.fillOval(cx-10,cy+17,4,3);g2.fillOval(cx+6,cy+17,4,3);
                g2.setColor(new Color(0x0ea5e9));g2.fillRoundRect(cx-8,cy+26,16,4,3,3);
                g2.setColor(new Color(0x0d1520));g2.fillRoundRect(cx-32,cy+38,10,18,4,4);g2.fillRoundRect(cx+22,cy+38,10,18,4,4);
                g2.setColor(new Color(0x0ea5e9));g2.setStroke(new BasicStroke(1f));g2.drawRoundRect(cx-32,cy+38,10,18,4,4);g2.drawRoundRect(cx+22,cy+38,10,18,4,4);
                g2.setColor(new Color(0x0d1520));g2.fillRoundRect(cx-32,cy+56,10,8,3,3);g2.fillRoundRect(cx+22,cy+56,10,8,3,3);
                g2.setColor(new Color(0x2dd4bf));g2.setStroke(new BasicStroke(1f));g2.drawRoundRect(cx-32,cy+56,10,8,3,3);g2.drawRoundRect(cx+22,cy+56,10,8,3,3);
                g2.setColor(new Color(0x071520));g2.fillRoundRect(cx-20,cy+38,40,28,6,6);
                g2.setColor(new Color(0x0ea5e9));g2.setStroke(new BasicStroke(1.5f));g2.drawRoundRect(cx-20,cy+38,40,28,6,6);
                g2.setFont(new Font("Segoe UI",Font.BOLD,13));
                g2.setColor(new Color(0x0ea5e9));g2.drawString("T",cx-8,cy+57);
                g2.setColor(new Color(0x2dd4bf));g2.drawString("S",cx+2,cy+57);
                g2.setColor(new Color(0x0d1520));g2.fillRoundRect(cx-14,cy+66,11,14,4,4);g2.fillRoundRect(cx+3,cy+66,11,14,4,4);
                g2.setColor(new Color(0x0ea5e9));g2.drawRoundRect(cx-14,cy+66,11,14,4,4);g2.drawRoundRect(cx+3,cy+66,11,14,4,4);
                g2.setColor(new Color(0xf59e0b));g2.fillOval(cx-24,cy+3,4,4);g2.fillOval(cx+20,cy+3,4,4);
            }
        };
        robotLogo.setOpaque(false); robotLogo.setBounds(115,60,150,95); left.add(robotLogo);

        JLabel brand = new JLabel("TechSikho", SwingConstants.CENTER);
        brand.setFont(new Font("Segoe UI",Font.BOLD,26)); brand.setForeground(Color.WHITE);
        brand.setBounds(70,163,240,35); left.add(brand);

        JLabel tagline = new JLabel("Learn. Code. Level Up.", SwingConstants.CENTER);
        tagline.setFont(new Font("Segoe UI",Font.ITALIC,13)); tagline.setForeground(new Color(0x64748b));
        tagline.setBounds(70,203,240,20); left.add(tagline);

        JPanel divLine = new JPanel(){protected void paintComponent(Graphics g){g.setColor(new Color(0x0ea5e9));g.fillRect(0,0,getWidth(),getHeight());}};
        divLine.setBounds(90,232,200,1); divLine.setOpaque(false); left.add(divLine);

        String[] feats = {">>> Gamified Learning",">>> Boss Battles",">>> XP & Levels",">>> Mini Games"};
        int fy = 260;
        for (String f : feats) {
            JLabel fl = new JLabel("  "+f);
            fl.setFont(new Font("Segoe UI",Font.PLAIN,13)); fl.setForeground(new Color(0x94a3b8));
            fl.setBounds(50,fy,280,24); left.add(fl); fy+=40;
        }
        JLabel ver = new JLabel("v1.0", SwingConstants.CENTER);
        ver.setFont(new Font("Segoe UI",Font.PLAIN,11)); ver.setForeground(new Color(0x334155));
        ver.setBounds(140,550,100,20); left.add(ver);

        // DIVIDER
        JPanel divider = new JPanel(){protected void paintComponent(Graphics g){g.setColor(new Color(0x0ea5e9));g.fillRect(0,0,getWidth(),getHeight());}};
        divider.setBounds(380,0,1,600); divider.setOpaque(false); root.add(divider);

        // RIGHT PANEL - uses JLayeredPane so eye button not clipped
        JLayeredPane right = new JLayeredPane();
        right.setBounds(381, 0, 579, 600);
        right.setOpaque(false);
        root.add(right);

        // Right bg overlay
        JPanel rightBg = new JPanel() {
            protected void paintComponent(Graphics g) {
                ((Graphics2D)g).setColor(new Color(5,8,15,150));
                g.fillRect(0,0,getWidth(),getHeight());
            }
        };
        rightBg.setOpaque(false);
        rightBg.setBounds(0,0,579,600);
        right.add(rightBg, Integer.valueOf(0));

        JLabel welcome = new JLabel("Welcome Back", SwingConstants.CENTER);
        welcome.setFont(new Font("Segoe UI",Font.BOLD,32)); welcome.setForeground(Color.WHITE);
        welcome.setBounds(0,110,579,45); right.add(welcome, Integer.valueOf(1));

        JPanel accent = new JPanel(){protected void paintComponent(Graphics g){g.setColor(new Color(0x0ea5e9));g.fillRoundRect(0,0,getWidth(),getHeight(),4,4);}};
        accent.setBounds(235,152,110,3); accent.setOpaque(false); right.add(accent, Integer.valueOf(1));

        JLabel sub = new JLabel("Sign in to continue your journey", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI",Font.PLAIN,13)); sub.setForeground(new Color(0x64748b));
        sub.setBounds(0,163,579,25); right.add(sub, Integer.valueOf(1));

        JLabel userLbl = new JLabel("USERNAME");
        userLbl.setFont(new Font("Segoe UI",Font.BOLD,10)); userLbl.setForeground(new Color(0x0ea5e9));
        userLbl.setBounds(90,210,200,18); right.add(userLbl, Integer.valueOf(1));

        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI",Font.PLAIN,14));
        usernameField.setBackground(new Color(0x0d1520)); usernameField.setForeground(Color.WHITE);
        usernameField.setCaretColor(new Color(0x0ea5e9));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x1e3a5f),1),
            BorderFactory.createEmptyBorder(0,12,0,12)));
        usernameField.setBounds(90,233,400,42); right.add(usernameField, Integer.valueOf(1));

        JLabel passLbl = new JLabel("PASSWORD");
        passLbl.setFont(new Font("Segoe UI",Font.BOLD,10)); passLbl.setForeground(new Color(0x0ea5e9));
        passLbl.setBounds(90,290,200,18); right.add(passLbl, Integer.valueOf(1));

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI",Font.PLAIN,14));
        passwordField.setBackground(new Color(0x0d1520)); passwordField.setForeground(Color.WHITE);
        passwordField.setCaretColor(new Color(0x0ea5e9)); passwordField.setEchoChar('\u2022');
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x1e3a5f),1),
            BorderFactory.createEmptyBorder(0,12,0,12)));
        passwordField.setBounds(90,313,400,42); right.add(passwordField, Integer.valueOf(1));

        // EYE BUTTON - directly on JLayeredPane, higher layer
        JButton eyeBtn = new JButton() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x0d1520));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),6,6);

                int cx=getWidth()/2, cy=getHeight()/2;
                if (passVisible) {
                    // Open eye - almond shape
                    g2.setColor(new Color(0x0ea5e9));
                    g2.setStroke(new BasicStroke(2f));
                    // Top arc
                    g2.drawArc(cx-12, cy-8, 24, 16, 0, 180);
                    // Bottom arc
                    g2.drawArc(cx-12, cy-8, 24, 16, 180, 180);
                    // Pupil outer
                    g2.setColor(new Color(0x0ea5e9));
                    g2.drawOval(cx-5, cy-5, 10, 10);
                    // Pupil inner filled
                    g2.fillOval(cx-3, cy-3, 6, 6);
                } else {
                    // Closed eye - almond shape with slash
                    g2.setColor(new Color(0x4a7090));
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawArc(cx-12, cy-8, 24, 16, 0, 180);
                    g2.drawArc(cx-12, cy-8, 24, 16, 180, 180);
                    g2.drawOval(cx-5, cy-5, 10, 10);
                    g2.fillOval(cx-3, cy-3, 6, 6);
                    // Slash line
                    g2.setColor(new Color(0x0ea5e9));
                    g2.setStroke(new BasicStroke(2.2f));
                    g2.drawLine(cx-10, cy+9, cx+10, cy-9);
                }
            }
        };
        eyeBtn.setBounds(452,316,36,36);
        eyeBtn.setContentAreaFilled(false); eyeBtn.setBorderPainted(false); eyeBtn.setFocusPainted(false);
        eyeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        eyeBtn.addActionListener(e -> {
            passVisible = !passVisible;
            passwordField.setEchoChar(passVisible ? (char)0 : '\u2022');

            eyeBtn.repaint();
        });
        right.add(eyeBtn, Integer.valueOf(2));

        JButton loginBtn = new JButton("LOGIN") {
            protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover()?new Color(0x0284c7):new Color(0x0ea5e9));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                g2.setFont(new Font("Segoe UI",Font.BOLD,15)); g2.setColor(Color.WHITE);
                FontMetrics fm=g2.getFontMetrics();
                g2.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        loginBtn.setBounds(90,380,400,48);
        loginBtn.setContentAreaFilled(false); loginBtn.setBorderPainted(false); loginBtn.setFocusPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.addActionListener(e -> doLogin());
        right.add(loginBtn, Integer.valueOf(1));

        JLabel forgotLbl = new JLabel("---- Forgot Password? ----", SwingConstants.CENTER);
        forgotLbl.setFont(new Font("Segoe UI",Font.PLAIN,12)); forgotLbl.setForeground(new Color(0x475569));
        forgotLbl.setBounds(0,443,579,25); right.add(forgotLbl, Integer.valueOf(1));

        JButton regBtn = new JButton("Create New Account") {
            protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover()?new Color(0x1e3a5f):new Color(0x0d1a2a));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                g2.setColor(new Color(0x0ea5e9)); g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1,1,getWidth()-2,getHeight()-2,8,8);
                g2.setFont(new Font("Segoe UI",Font.PLAIN,14)); g2.setColor(new Color(0x0ea5e9));
                FontMetrics fm=g2.getFontMetrics();
                g2.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        regBtn.setBounds(90,483,400,48);
        regBtn.setContentAreaFilled(false); regBtn.setBorderPainted(false); regBtn.setFocusPainted(false);
        regBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        regBtn.addActionListener(e -> { dispose(); new RegisterFrame().setVisible(true); });
        right.add(regBtn, Integer.valueOf(1));

        usernameField.addActionListener(e -> doLogin());
        passwordField.addActionListener(e -> doLogin());

        // RESIZE
        root.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                int w = root.getWidth(), h = root.getHeight();
                root.resizeMatrix(w, h);
                left.setBounds(0, 0, 380, h);
                divider.setBounds(380, 0, 1, h);
                int rw = w - 381;
                right.setBounds(381, 0, rw, h);
                rightBg.setBounds(0, 0, rw, h);
                int cx = rw / 2;
                welcome.setBounds(0, 110, rw, 45);
                accent.setBounds(cx-55, 152, 110, 3);
                sub.setBounds(0, 163, rw, 25);
                userLbl.setBounds(cx-200, 210, 200, 18);
                usernameField.setBounds(cx-200, 233, 400, 42);
                passLbl.setBounds(cx-200, 290, 200, 18);
                passwordField.setBounds(cx-200, 313, 355, 42);
                passwordField.setBounds(cx-200, 313, 400, 42);
                eyeBtn.setBounds(cx-200+362, 316, 36, 36);
                loginBtn.setBounds(cx-200, 380, 400, 48);
                forgotLbl.setBounds(0, 443, rw, 25);
                regBtn.setBounds(cx-200, 483, 400, 48);
                root.revalidate(); root.repaint();
            }
        });

        setVisible(true);
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        if (username.isEmpty()||password.isEmpty()){
            JOptionPane.showMessageDialog(this,"Please fill in all fields.","Error",JOptionPane.ERROR_MESSAGE);return;
        }
        User user = UserDAO.loginUser(username, password);
        if (user!=null){dispose();new DashboardFrame(user).setVisible(true);}
        else JOptionPane.showMessageDialog(this,"Invalid username or password.","Login Failed",JOptionPane.ERROR_MESSAGE);
    }
}