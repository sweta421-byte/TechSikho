package com.techsikho.models;

public class Badge {
    private int badgeId;
    private String badgeName;
    private String description;
    private String iconPath;
    private int xpRequired;

    public Badge(int badgeId, String badgeName, String description,
                 String iconPath, int xpRequired) {
        this.badgeId     = badgeId;
        this.badgeName   = badgeName;
        this.description = description;
        this.iconPath    = iconPath;
        this.xpRequired  = xpRequired;
    }

    public int getBadgeId()        { return badgeId; }
    public String getBadgeName()   { return badgeName; }
    public String getDescription() { return description; }
    public String getIconPath()    { return iconPath; }
    public int getXpRequired()     { return xpRequired; }
}