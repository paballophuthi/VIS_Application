package com.vis.utils;

public class EmailConfig {
    // Email Configuration - Update with your email settings
    public static final String SMTP_HOST = "smtp.gmail.com";
    public static final String SMTP_PORT = "587";
    public static final String FROM_EMAIL = "your-email@gmail.com"; // Update this
    public static final String FROM_PASSWORD = "your-app-password"; // Update this (use App Password for Gmail)
    public static final boolean USE_TLS = true;
    
    // Email templates
    public static final String VIOLATION_SUBJECT = "Traffic Violation Notice - VIS";
    public static final String SERVICE_REMINDER_SUBJECT = "Service Reminder - VIS";
    public static final String INSURANCE_EXPIRY_SUBJECT = "Insurance Expiry Alert - VIS";
    public static final String QUERY_RESPONSE_SUBJECT = "Response to Your Query - VIS";
    public static final String WELCOME_SUBJECT = "Welcome to VIS";
}