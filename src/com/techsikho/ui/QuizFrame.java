package com.techsikho.ui;

import com.techsikho.dao.QuestionDAO;
import com.techsikho.dao.ProgressDAO;
import com.techsikho.models.Question;
import com.techsikho.models.User;
import com.techsikho.services.XPService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class QuizFrame extends JFrame {

    private User currentUser;
    private int langId;
    private List<Question> questions;
    private int currentIndex = 0;
    private int score = 0;
    private java.util.List<String> userAnswers = new java.util.ArrayList<>();
    private int xpEarned = 0;

    private JLabel questionLabel, questionCounter, timerLabel;
    private JButton[] optionBtns = new JButton[4];
    private JButton nextBtn;
    private JLabel feedbackLabel;
    private JProgressBar quizProgress;
    private Timer countdownTimer;
    private int timeLeft = 30;

    public QuizFrame(User user, int langId) {
        this.currentUser = user;
        this.langId = langId;
        setTitle("TechSikho Quiz");
        setSize(750, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        loadQuestions();
        initUI();
        startTimer();
    }

    private void loadQuestions() {
        questions = QuestionDAO.getQuestionsByLang(langId);
        if (questions == null || questions.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Is language ke liye abhi questions nahi hain!",
                "No Questions", JOptionPane.WARNING_MESSAGE);
            dispose();
        }
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(18, 18, 35));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(18, 18, 35));

        questionCounter = new JLabel("Question 1 / " + questions.size());
        questionCounter.setFont(new Font("Segoe UI", Font.BOLD, 14));
        questionCounter.setForeground(new Color(150, 150, 180));

        timerLabel = new JLabel("30s");
        timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        timerLabel.setForeground(new Color(16, 185, 129));

        JLabel backBtn = new JLabel("Back");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        backBtn.setForeground(new Color(99, 102, 241));
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                stopTimer();
                dispose();
            }
        });

        topBar.add(backBtn, BorderLayout.WEST);
        topBar.add(questionCounter, BorderLayout.CENTER);
        topBar.add(timerLabel, BorderLayout.EAST);

        quizProgress = new JProgressBar(0, questions.size());
        quizProgress.setValue(0);
        quizProgress.setForeground(new Color(99, 102, 241));
        quizProgress.setBackground(new Color(40, 40, 65));
        quizProgress.setPreferredSize(new Dimension(700, 8));
        quizProgress.setBorderPainted(false);

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBackground(new Color(18, 18, 35));
        topSection.add(topBar, BorderLayout.NORTH);
        topSection.add(Box.createVerticalStrut(10), BorderLayout.CENTER);
        topSection.add(quizProgress, BorderLayout.SOUTH);

        mainPanel.add(topSection, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(new Color(18, 18, 35));
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        questionLabel = new JLabel("<html><div style='width:650px;'></div></html>");
        questionLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        questionLabel.setForeground(Color.WHITE);
        questionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(questionLabel);
        centerPanel.add(Box.createVerticalStrut(25));

        String[] labels = {"A", "B", "C", "D"};
        for (int i = 0; i < 4; i++) {
            optionBtns[i] = createOptionBtn(labels[i]);
            centerPanel.add(optionBtns[i]);
            centerPanel.add(Box.createVerticalStrut(10));
        }

        feedbackLabel = new JLabel(" ");
        feedbackLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        feedbackLabel.setForeground(new Color(16, 185, 129));
        feedbackLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(feedbackLabel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        nextBtn = new JButton("Next Question");
        nextBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        nextBtn.setBackground(new Color(99, 102, 241));
        nextBtn.setForeground(Color.WHITE);
        nextBtn.setFocusPainted(false);
        nextBtn.setBorderPainted(false);
        nextBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        nextBtn.setEnabled(false);
        nextBtn.setPreferredSize(new Dimension(700, 44));
        nextBtn.addActionListener(e -> nextQuestion());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(new Color(18, 18, 35));
        bottomPanel.add(nextBtn);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        loadQuestion();
    }

    private JButton createOptionBtn(String label) {
        JButton btn = new JButton();
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBackground(new Color(30, 30, 55));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 90), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(new Color(50, 50, 80));
            }
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled() &&
                    !btn.getBackground().equals(new Color(16, 185, 129)) &&
                    !btn.getBackground().equals(new Color(239, 68, 68)))
                    btn.setBackground(new Color(30, 30, 55));
            }
        });
        btn.addActionListener(e -> checkAnswer(label));
        return btn;
    }

    private void loadQuestion() {
        if (currentIndex >= questions.size()) return;
        Question q = questions.get(currentIndex);
        questionCounter.setText("Question " + (currentIndex + 1) + " / " + questions.size());
        quizProgress.setValue(currentIndex);
        questionLabel.setText("<html><div style='width:650px;'>" + q.getQuestionText() + "</div></html>");
        optionBtns[0].setText("A.  " + q.getOptionA());
        optionBtns[1].setText("B.  " + q.getOptionB());
        optionBtns[2].setText("C.  " + q.getOptionC());
        optionBtns[3].setText("D.  " + q.getOptionD());
        for (JButton btn : optionBtns) {
            btn.setBackground(new Color(30, 30, 55));
            btn.setEnabled(true);
        }
        feedbackLabel.setText(" ");
        nextBtn.setEnabled(false);
        resetTimer();
    }

    private void checkAnswer(String selected) {
        stopTimer();
        Question q = questions.get(currentIndex);
        boolean correct = selected.equals(q.getCorrectAns());
        userAnswers.add(selected);
        for (JButton btn : optionBtns) btn.setEnabled(false);
        String[] labels = {"A", "B", "C", "D"};
        for (int i = 0; i < 4; i++) {
            if (labels[i].equals(q.getCorrectAns())) {
                optionBtns[i].setBackground(new Color(16, 185, 129));
            } else if (labels[i].equals(selected) && !correct) {
                optionBtns[i].setBackground(new Color(239, 68, 68));
            }
        }
        if (correct) {
            score++;
            xpEarned += 10;
            feedbackLabel.setForeground(new Color(16, 185, 129));
            feedbackLabel.setText("Sahi jawab! +10 XP - " + q.getExplanation());
        } else {
            feedbackLabel.setForeground(new Color(239, 68, 68));
            feedbackLabel.setText("Galat! Sahi: " + q.getCorrectAns() + " - " + q.getExplanation());
        }
        nextBtn.setEnabled(true);
        if (currentIndex == questions.size() - 1) nextBtn.setText("Finish Quiz");
    }

    private void nextQuestion() {
        currentIndex++;
        if (currentIndex >= questions.size()) {
            finishQuiz();
        } else {
            loadQuestion();
            startTimer();
        }
    }

    private void finishQuiz() {
        stopTimer();
        int newTotalXp = XPService.addXP(currentUser.getUserId(), currentUser.getTotalXp(), xpEarned);
        currentUser.setTotalXp(newTotalXp);
        ProgressDAO.saveProgress(currentUser.getUserId(), langId, score, xpEarned);
        int total = questions.size();
        int pct = (score * 100) / total;
        JDialog result = new JDialog(this, "Quiz Result", true);
        result.setSize(480, 360);
        result.setLocationRelativeTo(this);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(20, 20, 50));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        JLabel titleLbl = new JLabel("Quiz Complete!");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLbl.setForeground(new Color(255, 215, 0));
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel scoreLbl = new JLabel("Score: " + score + " / " + total + "  (" + pct + "%)");
        scoreLbl.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        scoreLbl.setForeground(Color.WHITE);
        scoreLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel xpLbl = new JLabel("XP Earned: +" + xpEarned);
        xpLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        xpLbl.setForeground(new Color(99, 102, 241));
        xpLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel gradeLbl = new JLabel(pct >= 80 ? "Excellent!" : pct >= 50 ? "Achha kiya!" : "Practice karo!");
        gradeLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        gradeLbl.setForeground(pct >= 80 ? new Color(16,185,129) : pct >= 50 ? new Color(245,158,11) : new Color(239,68,68));
        gradeLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton reviewBtn = new JButton("Review Answers");
        reviewBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        reviewBtn.setBackground(new Color(99, 102, 241));
        reviewBtn.setForeground(Color.WHITE);
        reviewBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        reviewBtn.setMaximumSize(new Dimension(200, 40));
        reviewBtn.addActionListener(e -> { result.dispose(); showReview(); });
        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeBtn.setBackground(new Color(60, 60, 80));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeBtn.setMaximumSize(new Dimension(200, 40));
        closeBtn.addActionListener(e -> { result.dispose(); dispose(); });
        panel.add(titleLbl);
        panel.add(Box.createVerticalStrut(16));
        panel.add(scoreLbl);
        panel.add(Box.createVerticalStrut(8));
        panel.add(xpLbl);
        panel.add(Box.createVerticalStrut(8));
        panel.add(gradeLbl);
        panel.add(Box.createVerticalStrut(24));
        panel.add(reviewBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(closeBtn);
        result.add(panel);
        result.setVisible(true);
    }

    private void showReview() {
        JDialog review = new JDialog(this, "Review Answers", true);
        review.setSize(600, 500);
        review.setLocationRelativeTo(this);
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBackground(new Color(15, 15, 35));
        JLabel title = new JLabel("  Review Answers");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        title.setOpaque(true);
        title.setBackground(new Color(25, 25, 55));
        title.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(new Color(15, 15, 35));
        listPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            String userAns = userAnswers.size() > i ? userAnswers.get(i) : "No Answer";
            boolean correct = userAns.equals(q.getCorrectAns());
            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBackground(correct ? new Color(20,50,20) : new Color(50,20,20));
            card.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
            JLabel qLbl = new JLabel((i+1) + ". " + q.getQuestionText());
            qLbl.setForeground(Color.WHITE);
            qLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            JLabel yourAns = new JLabel("Your Answer: " + userAns + (correct ? " - Correct!" : " - Wrong!"));
            yourAns.setForeground(correct ? new Color(16,185,129) : new Color(239,68,68));
            yourAns.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            JLabel corrAns = new JLabel("Correct Answer: " + q.getCorrectAns());
            corrAns.setForeground(new Color(16,185,129));
            corrAns.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            card.add(qLbl);
            card.add(Box.createVerticalStrut(4));
            card.add(yourAns);
            if (!correct) card.add(corrAns);
            listPanel.add(card);
            listPanel.add(Box.createVerticalStrut(6));
        }
        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(15,15,35));
        JButton closeBtn = new JButton("Close");
        closeBtn.setBackground(new Color(99,102,241));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.addActionListener(e -> { review.dispose(); dispose(); });
        JPanel bottom = new JPanel();
        bottom.setBackground(new Color(15,15,35));
        bottom.add(closeBtn);
        review.add(title, java.awt.BorderLayout.NORTH);
        review.add(scroll, java.awt.BorderLayout.CENTER);
        review.add(bottom, java.awt.BorderLayout.SOUTH);
        review.setVisible(true);
    }

    private void startTimer() {
        timeLeft = 30;
        countdownTimer = new Timer(1000, e -> {
            timeLeft--;
            timerLabel.setText(timeLeft + "s");
            if (timeLeft <= 10) timerLabel.setForeground(new Color(239, 68, 68));
            else timerLabel.setForeground(new Color(16, 185, 129));
            if (timeLeft <= 0) {
                stopTimer();
                feedbackLabel.setForeground(new Color(245, 158, 11));
                feedbackLabel.setText("Time up!");
                for (JButton btn : optionBtns) btn.setEnabled(false);
                nextBtn.setEnabled(true);
            }
        });
        countdownTimer.start();
    }

    private void stopTimer() {
        if (countdownTimer != null) countdownTimer.stop();
    }

    private void resetTimer() {
        stopTimer();
        timeLeft = 30;
        timerLabel.setText("30s");
        timerLabel.setForeground(new Color(16, 185, 129));
    }
}
