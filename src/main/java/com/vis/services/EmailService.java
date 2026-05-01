package com.vis.services;

import com.vis.utils.EmailConfig;
import com.vis.utils.AlertUtil;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailService {
    
    private static Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", EmailConfig.SMTP_HOST);
        props.put("mail.smtp.port", EmailConfig.SMTP_PORT);
        
        if (EmailConfig.USE_TLS) {
            props.put("mail.smtp.starttls.enable", "true");
        }
        
        props.put("mail.smtp.auth", "true");
        
        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EmailConfig.FROM_EMAIL, EmailConfig.FROM_PASSWORD);
            }
        });
    }
    
    public static boolean sendEmail(String to, String subject, String body) {
        try {
            Session session = createSession();
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EmailConfig.FROM_EMAIL));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText(body);
            
            Transport.send(message);
            System.out.println("Email sent to: " + to);
            return true;
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
            return false;
        }
    }
    
    public static void sendViolationNotification(String to, String vehicleReg, String violationType, double fineAmount) {
        String body = String.format(
            "Dear Vehicle Owner,\n\n" +
            "A traffic violation has been recorded for your vehicle (%s).\n\n" +
            "Violation Type: %s\n" +
            "Fine Amount: $%.2f\n\n" +
            "Please login to the Vehicle Information System to view details and make payment.\n\n" +
            "Regards,\nVIS Team",
            vehicleReg, violationType, fineAmount
        );
        sendEmail(to, EmailConfig.VIOLATION_SUBJECT, body);
    }
    
    public static void sendServiceReminder(String to, String vehicleReg, String serviceType, String dueDate) {
        String body = String.format(
            "Dear Vehicle Owner,\n\n" +
            "Your vehicle (%s) is due for service.\n\n" +
            "Service Type: %s\n" +
            "Due Date: %s\n\n" +
            "Please schedule an appointment with your preferred workshop.\n\n" +
            "Regards,\nVIS Team",
            vehicleReg, serviceType, dueDate
        );
        sendEmail(to, EmailConfig.SERVICE_REMINDER_SUBJECT, body);
    }
    
    public static void sendInsuranceReminder(String to, String vehicleReg, String policyNumber, String expiryDate) {
        String body = String.format(
            "Dear Vehicle Owner,\n\n" +
            "Your vehicle insurance policy is expiring soon.\n\n" +
            "Vehicle: %s\n" +
            "Policy Number: %s\n" +
            "Expiry Date: %s\n\n" +
            "Please renew your insurance to avoid penalties.\n\n" +
            "Regards,\nVIS Team",
            vehicleReg, policyNumber, expiryDate
        );
        sendEmail(to, EmailConfig.INSURANCE_EXPIRY_SUBJECT, body);
    }
    
    public static void sendQueryResponse(String to, String subject, String response) {
        String body = String.format(
            "Dear User,\n\n" +
            "Your query has been addressed.\n\n" +
            "Subject: %s\n" +
            "Response: %s\n\n" +
            "If you have further questions, please don't hesitate to contact us.\n\n" +
            "Regards,\nVIS Support Team",
            subject, response
        );
        sendEmail(to, EmailConfig.QUERY_RESPONSE_SUBJECT, body);
    }
    
    public static void sendWelcomeEmail(String to, String username, String role) {
        String body = String.format(
            "Dear %s,\n\n" +
            "Welcome to the Vehicle Information System!\n\n" +
            "You have been registered as a %s.\n\n" +
            "You can now login to access your dashboard and manage your vehicles.\n\n" +
            "Regards,\nVIS Team",
            username, role
        );
        sendEmail(to, EmailConfig.WELCOME_SUBJECT, body);
    }
}