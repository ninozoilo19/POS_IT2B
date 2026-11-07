package com.parish.model;

public class ActivityLog {
    private int id;
    private int userId;
    private String actionType;
    private String description;
    private String timestamp;

    public ActivityLog(int id, int userId, String actionType, String description, String timestamp) {
        this.id = id;
        this.userId = userId;
        this.actionType = actionType;
        this.description = description;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}