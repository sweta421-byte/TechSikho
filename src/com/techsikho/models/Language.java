package com.techsikho.models;

public class Language {
    private int langId;
    private String langName;
    private String description;
    private String iconPath;

    public Language(int langId, String langName, 
                    String description, String iconPath) {
        this.langId      = langId;
        this.langName    = langName;
        this.description = description;
        this.iconPath    = iconPath;
    }

    public int getLangId()         { return langId; }
    public String getLangName()    { return langName; }
    public String getDescription() { return description; }
    public String getIconPath()    { return iconPath; }
}