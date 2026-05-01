package com.vis.controller;

import com.vis.dao.*;
import com.vis.model.*;
import com.vis.utils.AlertUtil;
import com.vis.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.time.LocalDate;

public class CustomerDashboardController {
    
    @FXML private Label welcomeLabel;
    @FXML private Label totalVehiclesLabel;
    @FXML private Label pendingViolationsLabel;
    @FXML private Label totalFineAmountLabel;
    @FXML private Label loyaltyPointsLabel;
    
    @FXML private TableView<Vehicle> vehiclesTable;
    @FXML private TableView<Violation> violationsTable;
    @FXML private TableView<CustomerQuery> queriesTable;
    
    private VehicleDAO vehicleDAO;
    private ViolationDAO violationDAO;
    private CustomerQueryDAO queryDAO;
    private AchievementDAO achievementDAO;
    private UserDAO userDAO;
    
    private int currentUserId;
    
    @FXML
    private void initialize() {
        vehicleDAO = new VehicleDAO();
        violationDAO = new ViolationDAO();
        queryDAO = new CustomerQueryDAO();
        achievementDAO = new AchievementDAO();
        userDAO = new UserDAO();
        
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUserId();
            welcomeLabel.setText("Welcome, " + currentUser.getFullName() + "!");
            loyaltyPointsLabel.setText("Points: " + currentUser.getGamificationPoints());
        }
        
        setupTables();
        loadData();
        loadAchievements();
    }
    
    private void setupTables() {
        // Vehicles Table
        if (vehiclesTable != null) {
            vehiclesTable.getColumns().clear();
            
            TableColumn<Vehicle, String> regCol = new TableColumn<>("Registration");
            regCol.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
            regCol.setPrefWidth(120);
            
            TableColumn<Vehicle, String> makeCol = new TableColumn<>("Make");
            makeCol.setCellValueFactory(new PropertyValueFactory<>("make"));
            makeCol.setPrefWidth(100);
            
            TableColumn<Vehicle, String> modelCol = new TableColumn<>("Model");
            modelCol.setCellValueFactory(new PropertyValueFactory<>("model"));
            modelCol.setPrefWidth(100);
            
            vehiclesTable.getColumns().addAll(regCol, makeCol, modelCol);
        }
        
        // Violations Table
        if (violationsTable != null) {
            violationsTable.getColumns().clear();
            
            TableColumn<Violation, String> typeCol = new TableColumn<>("Violation Type");
            typeCol.setCellValueFactory(new PropertyValueFactory<>("violationType"));
            typeCol.setPrefWidth(150);
            
            TableColumn<Violation, Double> fineCol = new TableColumn<>("Fine Amount");
            fineCol.setCellValueFactory(new PropertyValueFactory<>("fineAmount"));
            fineCol.setPrefWidth(100);
            
            TableColumn<Violation, String> statusCol = new TableColumn<>("Status");
            statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
            statusCol.setPrefWidth(80);
            
            violationsTable.getColumns().addAll(typeCol, fineCol, statusCol);
        }
        
        // Queries Table
        if (queriesTable != null) {
            queriesTable.getColumns().clear();
            
            TableColumn<CustomerQuery, String> subjectCol = new TableColumn<>("Subject");
            subjectCol.setCellValueFactory(new PropertyValueFactory<>("subject"));
            subjectCol.setPrefWidth(200);
            
            TableColumn<CustomerQuery, String> statusCol = new TableColumn<>("Status");
            statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
            statusCol.setPrefWidth(100);
            
            queriesTable.getColumns().addAll(subjectCol, statusCol);
        }
    }
    
    private void loadData() {
        // Load Vehicles
        ObservableList<Vehicle> vehicles = vehicleDAO.getVehiclesByOwner(currentUserId);
        if (vehiclesTable != null) {
            vehiclesTable.setItems(vehicles);
        }
        totalVehiclesLabel.setText("Vehicles: " + vehicles.size());
        
        // Load Violations
        ObservableList<Violation> violations = violationDAO.getViolationsByUser(currentUserId);
        if (violationsTable != null) {
            violationsTable.setItems(violations);
        }
        
        int pending = 0;
        double totalFine = 0;
        for (Violation v : violations) {
            if (!"Paid".equals(v.getStatus())) {
                pending++;
                totalFine += v.getFineAmount();
            }
        }
        pendingViolationsLabel.setText("Pending: " + pending);
        totalFineAmountLabel.setText("Total Fines: $" + String.format("%.2f", totalFine));
        
        // Load Queries
        ObservableList<CustomerQuery> queries = queryDAO.getQueriesByUser(currentUserId);
        if (queriesTable != null) {
            queriesTable.setItems(queries);
        }
    }
    
    private void loadAchievements() {
        int totalPoints = achievementDAO.getTotalPoints(currentUserId);
        loyaltyPointsLabel.setText("Points: " + totalPoints);
        
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUser.setGamificationPoints(totalPoints);
        }
    }
    
    @FXML
    private void handleRegisterVehicle() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Register Vehicle");
        dialog.setHeaderText("Enter Registration Number");
        dialog.setContentText("Registration Number:");
        
        dialog.showAndWait().ifPresent(reg -> {
            Vehicle vehicle = new Vehicle();
            vehicle.setRegistrationNumber(reg.toUpperCase());
            vehicle.setMake("Unknown");
            vehicle.setModel("Unknown");
            vehicle.setYear(LocalDate.now().getYear());
            vehicle.setOwnerId(currentUserId);
            
            if (vehicleDAO.register(vehicle)) {
                AlertUtil.showInfo("Success", "Vehicle registered: " + reg);
                loadData();
                
                if (vehicleDAO.getVehiclesByOwner(currentUserId).size() == 1) {
                    Achievement achievement = new Achievement();
                    achievement.setUserId(currentUserId);
                    achievement.setAchievementName("First Vehicle");
                    achievement.setDescription("Registered your first vehicle!");
                    achievement.setPoints(50);
                    achievementDAO.create(achievement);
                    loadAchievements();
                }
            } else {
                AlertUtil.showError("Error", "Registration number may already exist");
            }
        });
    }
    
    @FXML
    private void handleViewVehicleDetails() {
        if (vehiclesTable != null) {
            Vehicle selected = vehiclesTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Vehicle Details");
                alert.setHeaderText(selected.getRegistrationNumber());
                alert.setContentText("Make: " + selected.getMake() + "\nModel: " + selected.getModel() + "\nYear: " + selected.getYear());
                alert.showAndWait();
            } else {
                AlertUtil.showWarning("No Selection", "Please select a vehicle");
            }
        }
    }
    
    @FXML
    private void handleServiceHistory() {
        if (vehiclesTable != null) {
            Vehicle selected = vehiclesTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                AlertUtil.showInfo("Service History", "Service history for: " + selected.getRegistrationNumber());
            } else {
                AlertUtil.showWarning("No Selection", "Please select a vehicle");
            }
        }
    }
    
    @FXML
    private void handleScheduleService() {
        if (vehiclesTable != null) {
            Vehicle selected = vehiclesTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                AlertUtil.showInfo("Schedule Service", "Service scheduling for: " + selected.getRegistrationNumber());
            } else {
                AlertUtil.showWarning("No Selection", "Please select a vehicle");
            }
        }
    }
    
    @FXML
    private void handlePayViolation() {
        if (violationsTable != null) {
            Violation selected = violationsTable.getSelectionModel().getSelectedItem();
            if (selected != null && !"Paid".equals(selected.getStatus())) {
                boolean confirm = AlertUtil.showConfirmation("Pay Violation", 
                    "Pay violation of $" + selected.getFineAmount() + "?");
                if (confirm) {
                    if (violationDAO.markAsPaid(selected.getViolationId())) {
                        AlertUtil.showInfo("Success", "Violation paid successfully!");
                        loadData();
                        
                        Achievement achievement = new Achievement();
                        achievement.setUserId(currentUserId);
                        achievement.setAchievementName("Responsible Citizen");
                        achievement.setDescription("Paid a violation on time!");
                        achievement.setPoints(25);
                        achievementDAO.create(achievement);
                        loadAchievements();
                    } else {
                        AlertUtil.showError("Error", "Failed to process payment");
                    }
                }
            } else {
                AlertUtil.showWarning("No Selection", "Please select an unpaid violation");
            }
        }
    }
    
    @FXML
    private void handleSubmitQuery() {
        TextInputDialog subjectDialog = new TextInputDialog();
        subjectDialog.setTitle("New Query");
        subjectDialog.setHeaderText("Subject");
        subjectDialog.setContentText("Subject:");
        
        subjectDialog.showAndWait().ifPresent(subject -> {
            TextInputDialog messageDialog = new TextInputDialog();
            messageDialog.setTitle("New Query");
            messageDialog.setHeaderText("Message");
            messageDialog.setContentText("Message:");
            
            messageDialog.showAndWait().ifPresent(message -> {
                CustomerQuery query = new CustomerQuery();
                query.setUserId(currentUserId);
                query.setSubject(subject);
                query.setMessage(message);
                
                if (queryDAO.create(query)) {
                    AlertUtil.showInfo("Success", "Query submitted!");
                    loadData();
                    
                    Achievement achievement = new Achievement();
                    achievement.setUserId(currentUserId);
                    achievement.setAchievementName("First Query");
                    achievement.setDescription("Submitted your first support query!");
                    achievement.setPoints(25);
                    achievementDAO.create(achievement);
                    loadAchievements();
                } else {
                    AlertUtil.showError("Error", "Failed to submit query");
                }
            });
        });
    }
    
    @FXML
    private void handleProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user_profile.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("My Profile");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            loadData();
            loadAchievements();
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showError("Error", "Could not load profile: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleRefresh() {
        loadData();
        loadAchievements();
        AlertUtil.showInfo("Refreshed", "Data has been refreshed");
    }
    
    @FXML
    private void handleLogout() {
        if (AlertUtil.showConfirmation("Logout", "Are you sure you want to logout?")) {
            SessionManager.getInstance().clearSession();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) welcomeLabel.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Login - VIS Application");
                stage.centerOnScreen();
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}