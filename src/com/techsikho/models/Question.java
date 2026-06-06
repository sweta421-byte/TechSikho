package com.techsikho.models;

public class Question {
    private int questionId;
    private int levelId;
    private String questionText;
    private String optionA, optionB, optionC, optionD;
    private String correctAns;
    private String explanation;
    private String difficulty;

    public Question() {
    }

    public Question(int questionId, int levelId, String questionText,
                    String optionA, String optionB, String optionC, String optionD,
                    String correctAns, String explanation, String difficulty) {
        this.questionId = questionId;
        this.levelId = levelId;
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAns = correctAns;
        this.explanation = explanation;
        this.difficulty = difficulty;
    }

    // ===== GETTERS =====
    public int getQuestionId() { return questionId; }
    public int getLevelId() { return levelId; }
    public String getQuestionText() { return questionText; }
    public String getOptionA() { return optionA; }
    public String getOptionB() { return optionB; }
    public String getOptionC() { return optionC; }
    public String getOptionD() { return optionD; }
    public String getCorrectAns() { return correctAns; }
    public String getExplanation() { return explanation; }
    public String getDifficulty() { return difficulty; }

    // ===== SETTERS (IMPORTANT FIX) =====
    public void setQuestionId(int questionId) { this.questionId = questionId; }
    public void setLevelId(int levelId) { this.levelId = levelId; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public void setOptionA(String optionA) { this.optionA = optionA; }
    public void setOptionB(String optionB) { this.optionB = optionB; }
    public void setOptionC(String optionC) { this.optionC = optionC; }
    public void setOptionD(String optionD) { this.optionD = optionD; }
    public void setCorrectAns(String correctAns) { this.correctAns = correctAns; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
}