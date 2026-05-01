package com.vis.model;

import java.sql.Date;
import java.sql.Timestamp;

public class ServiceRecord {
    private int serviceId;
    private int id;
    private int vehicleId;
    private String registrationNumber;
    private int workshopId;
    private String workshopName;
    private String serviceType;
    private Date serviceDate;
    private double cost;
    private String description;
    private Date nextServiceDate;
    private int odometerReading;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    public ServiceRecord() {
        this.status = "COMPLETED";
    }
    
    public int getServiceId() { return serviceId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; this.id = serviceId; }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; this.serviceId = id; }
    
    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }
    
    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
    
    public int getWorkshopId() { return workshopId; }
    public void setWorkshopId(int workshopId) { this.workshopId = workshopId; }
    
    public String getWorkshopName() { return workshopName; }
    public void setWorkshopName(String workshopName) { this.workshopName = workshopName; }
    
    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
    
    public Date getServiceDate() { return serviceDate; }
    public void setServiceDate(Date serviceDate) { this.serviceDate = serviceDate; }
    
    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Date getNextServiceDate() { return nextServiceDate; }
    public void setNextServiceDate(Date nextServiceDate) { this.nextServiceDate = nextServiceDate; }
    
    public int getOdometerReading() { return odometerReading; }
    public void setOdometerReading(int odometerReading) { this.odometerReading = odometerReading; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}