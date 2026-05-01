package com.vis.controller;

import com.vis.dao.UserDAO;
import com.vis.model.User;
import com.vis.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    
    private UserDAO userDAO;
    
    public LoginController() {
        userDAO = new UserDAO();
    }
    
    @FXML
    private void handleLogin() {
        String email = usernameField.getText().trim();
        String password = passwordField.getText();
        
        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter both email and password");
            return;
        }
        
        User user = userDAO.getUserByEmail(email);
        
        if (user != null) {
            SessionManager.getInstance().setCurrentUser(user);
            
            try {
                String dashboardFxml = "";
                String title = "";
                String role = user.getRole();
                
                // Handle both "ADMIN" and "Admin" formats
                if (role.equalsIgnoreCase("ADMIN")) {
                    dashboardFxml = "/fxml/admin_dashboard.fxml";
                    title = "Admin Dashboard - VIS Application";
                } else if (role.equalsIgnoreCase("POLICE")) {
                    dashboardFxml = "/fxml/police_dashboard.fxml";
                    title = "Police Dashboard - VIS Application";
                } else if (role.equalsIgnoreCase("CUSTOMER")) {
                    dashboardFxml = "/fxml/customer_dashboard.fxml";
                    title = "Customer Dashboard - VIS Application";
                } else if (role.equalsIgnoreCase("WORKSHOP")) {
                    dashboardFxml = "/fxml/workshop_dashboard.fxml";
                    title = "Workshop Dashboard - VIS Application";
                } else {
                    dashboardFxml = "/fxml/customer_dashboard.fxml";
                    title = "Dashboard - VIS Application";
                }
                
                FXMLLoader loader = new FXMLLoader(getClass().getResource(dashboardFxml));
                Parent root = loader.load();
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle(title);
                stage.setMaximized(true);
                stage.show();
                
            } catch (Exception e) {
                e.printStackTrace();
                showError("Could not load dashboard: " + e.getMessage());
            }
        } else {
            showError("Invalid email or password");
        }
    }
    
    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Register - VIS Application");
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not load registration page: " + e.getMessage());
        }
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Failed");
        alert.setContentText(message);
        alert.showAndWait();
    }
}