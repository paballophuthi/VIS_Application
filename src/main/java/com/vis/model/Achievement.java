package com.vis.model;

import java.sql.Timestamp;

public class Achievement {
    private int id;
    private int userId;
    private String userName;
    private String achievementName;
    private String description;
    private int points;
    private Timestamp achievedAt;
    
    public Achievement() {}
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getAchievementName() { return achievementName; }
    public void setAchievementName(String achievementName) { this.achievementName = achievementName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
    
    public Timestamp getAchievedAt() { return achievedAt; }
    public void setAchievedAt(Timestamp achievedAt) { this.achievedAt = achievedAt; }
}