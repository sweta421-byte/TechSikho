package com.techsikho.models;

public class Level {
    private int levelId;
    private int langId;
    private int levelNumber;
    private String levelName;
    private String difficulty;
    private int xpReward;
    private String description;

    public Level(int levelId, int langId, int levelNumber, String levelName,
                 String difficulty, int xpReward, String description) {
        this.levelId     = levelId;
        this.langId      = langId;
        this.levelNumber = levelNumber;
        this.levelName   = levelName;
        this.difficulty  = difficulty;
        this.xpReward    = xpReward;
        this.description = description;
    }

    public int getLevelId()        { return levelId; }
    public int getLangId()         { return langId; }
    public int getLevelNumber()    { return levelNumber; }
    public String getLevelName()   { return levelName; }
    public String getDifficulty()  { return difficulty; }
    public int getXpReward()       { return xpReward; }
    public String getDescription() { return description; }
}