package com.vis.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController {
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private TextField fullNameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> roleComboBox;
    
    @FXML
    private void initialize() {
        roleComboBox.getItems().addAll("CUSTOMER", "WORKSHOP");
        roleComboBox.setValue("CUSTOMER");
    }
    
    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String fullName = fullNameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        if (username.isEmpty() || email.isEmpty() || fullName.isEmpty() || password.isEmpty()) {
            showError("Please fill all required fields");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }
        
        // Simple registration (in real app, save to database)
        showInfo("Registration successful! Please login.");
        handleBackToLogin();
    }
    
    @FXML
    private void handleBackToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login - VIS Application");
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not load login page: " + e.getMessage());
        }
    }
    
    private void showError(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Registration Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfo(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Registration");
        alert.setContentText(message);
        alert.showAndWait();
    }
}