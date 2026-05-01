package com.vis.model;

import java.sql.Timestamp;

public class CustomerQuery {
    private int id;
    private int userId;
    private String userName;
    private String subject;
    private String message;
    private String status;
    private String response;
    private int respondedBy;
    private String responderName;
    private Timestamp createdAt;
    private Timestamp resolvedAt;
    
    public CustomerQuery() {}
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }
    
    public int getRespondedBy() { return respondedBy; }
    public void setRespondedBy(int respondedBy) { this.respondedBy = respondedBy; }
    
    public String getResponderName() { return responderName; }
    public void setResponderName(String responderName) { this.responderName = responderName; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public Timestamp getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Timestamp resolvedAt) { this.resolvedAt = resolvedAt; }
    
    public boolean isResolved() {
        return "RESOLVED".equals(status);
    }
}