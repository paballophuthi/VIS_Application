package com.vis.model;

import java.sql.Timestamp;

public class Violation {
    private int violationId;
    private int id;  // For compatibility
    private int vehicleId;
    private String registrationNumber;
    private int officerId;
    private String officerName;
    private String violationType;
    private String description;
    private String location;
    private Timestamp violationDate;
    private double fineAmount;
    private int points;
    private String status;
    private boolean paid;
    private Timestamp createdAt;
    
    public Violation() {}
    
    public int getViolationId() { return violationId; }
    public void setViolationId(int violationId) { this.violationId = violationId; this.id = violationId; }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; this.violationId = id; }
    
    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }
    
    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
    
    public int getOfficerId() { return officerId; }
    public void setOfficerId(int officerId) { this.officerId = officerId; }
    
    public String getOfficerName() { return officerName; }
    public void setOfficerName(String officerName) { this.officerName = officerName; }
    
    public String getViolationType() { return violationType; }
    public void setViolationType(String violationType) { this.violationType = violationType; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public Timestamp getViolationDate() { return violationDate; }
    public void setViolationDate(Timestamp violationDate) { this.violationDate = violationDate; }
    
    public double getFineAmount() { return fineAmount; }
    public void setFineAmount(double fineAmount) { this.fineAmount = fineAmount; }
    
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}