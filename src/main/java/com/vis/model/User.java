package com.vis.model;

import java.sql.Timestamp;

public class User {
    private int userId;
    private int id;
    private String fullName;
    private String email;
    private String passwordHash;
    private String phone;
    private String address;
    private String role;
    private String status;
    private String profileImage;
    private String themePreference;
    private int gamificationPoints;
    private Timestamp lastLogin;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String username;
    private String password;
    private String fullNameAlt;
    private boolean active;
    
    public User() {}
    
    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; this.id = userId; }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; this.userId = id; }
    
    public String getFullName() { return fullName != null ? fullName : fullNameAlt; }
    public void setFullName(String fullName) { this.fullName = fullName; this.fullNameAlt = fullName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
    
    public String getThemePreference() { return themePreference; }
    public void setThemePreference(String themePreference) { this.themePreference = themePreference; }
    
    public int getGamificationPoints() { return gamificationPoints; }
    public void setGamificationPoints(int gamificationPoints) { this.gamificationPoints = gamificationPoints; }
    
    public Timestamp getLastLogin() { return lastLogin; }
    public void setLastLogin(Timestamp lastLogin) { this.lastLogin = lastLogin; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    
    public String getUsername() { return username != null ? username : email; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getFullNameAlt() { return fullNameAlt; }
    public void setFullNameAlt(String fullNameAlt) { this.fullNameAlt = fullNameAlt; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    @Override
    public String toString() {
        return fullName != null ? fullName : (fullNameAlt != null ? fullNameAlt : email);
    }
}