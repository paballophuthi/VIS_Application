package com.vis.model;

import java.sql.Timestamp;

public class PoliceReport {
    private int id;
    private String reportNumber;
    private int vehicleId;
    private String registrationNumber;
    private int officerId;
    private String officerName;
    private String incidentType;
    private String description;
    private String location;
    private Timestamp incidentDate;
    private Timestamp reportDate;
    private String filePath;
    private String status;
    
    public PoliceReport() {}
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getReportNumber() { return reportNumber; }
    public void setReportNumber(String reportNumber) { this.reportNumber = reportNumber; }
    
    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }
    
    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
    
    public int getOfficerId() { return officerId; }
    public void setOfficerId(int officerId) { this.officerId = officerId; }
    
    public String getOfficerName() { return officerName; }
    public void setOfficerName(String officerName) { this.officerName = officerName; }
    
    public String getIncidentType() { return incidentType; }
    public void setIncidentType(String incidentType) { this.incidentType = incidentType; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public Timestamp getIncidentDate() { return incidentDate; }
    public void setIncidentDate(Timestamp incidentDate) { this.incidentDate = incidentDate; }
    
    public Timestamp getReportDate() { return reportDate; }
    public void setReportDate(Timestamp reportDate) { this.reportDate = reportDate; }
    
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}