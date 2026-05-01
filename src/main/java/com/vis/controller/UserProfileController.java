package com.vis.controller;

import com.vis.dao.UserDAO;
import com.vis.model.User;
import com.vis.utils.AlertUtil;
import com.vis.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class UserProfileController {
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField addressField;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label userIdLabel;
    @FXML private Label roleLabel;
    @FXML private Label pointsLabel;
    
    private UserDAO userDAO;
    private User currentUser;
    
    @FXML
    private void initialize() {
        userDAO = new UserDAO();
        loadUserProfile();
    }
    
    private void loadUserProfile() {
        currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            userIdLabel.setText(String.valueOf(currentUser.getUserId()));
            fullNameField.setText(currentUser.getFullName());
            emailField.setText(currentUser.getEmail());
            phoneField.setText(currentUser.getPhone());
            addressField.setText(currentUser.getAddress());
            roleLabel.setText(currentUser.getRole());
            pointsLabel.setText(String.valueOf(currentUser.getGamificationPoints()));
        }
    }
    
    @FXML
    private void handleUpdateProfile() {
        String fullName = fullNameField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();
        
        if (fullName.isEmpty()) {
            AlertUtil.showError("Error", "Full name is required");
            return;
        }
        
        currentUser.setFullName(fullName);
        currentUser.setPhone(phone);
        currentUser.setAddress(address);
        
        if (userDAO.update(currentUser)) {
            AlertUtil.showInfo("Success", "Profile updated successfully");
            SessionManager.getInstance().setCurrentUser(currentUser);
        } else {
            AlertUtil.showError("Error", "Failed to update profile");
        }
    }
    
    @FXML
    private void handleChangePassword() {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        if (currentPassword.isEmpty() || newPassword.isEmpty()) {
            AlertUtil.showError("Error", "Please enter current and new password");
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            AlertUtil.showError("Error", "New passwords do not match");
            return;
        }
        
        if (newPassword.length() < 6) {
            AlertUtil.showError("Error", "Password must be at least 6 characters");
            return;
        }
        
        // In production, verify current password before changing
        if (userDAO.changePassword(currentUser.getUserId(), newPassword)) {
            AlertUtil.showInfo("Success", "Password changed successfully");
            currentPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();
        } else {
            AlertUtil.showError("Error", "Failed to change password");
        }
    }
    
    @FXML
    private void handleClose() {
        Stage stage = (Stage) fullNameField.getScene().getWindow();
        stage.close();
    }
}