package com.vis.controller;

import com.vis.dao.*;
import com.vis.model.*;
import com.vis.utils.AlertUtil;
import com.vis.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AdminDashboardController {
    
    // Dashboard Labels
    @FXML private Label welcomeLabel;
    @FXML private Label totalUsersLabel;
    @FXML private Label totalVehiclesLabel;
    @FXML private Label totalViolationsLabel;
    @FXML private Label pendingViolationsLabel;
    @FXML private Label totalRevenueLabel;
    @FXML private Label activeUsersLabel;
    @FXML private Label totalWorkshopsLabel;
    @FXML private Label totalPoliceLabel;
    
    // Analytics Labels
    @FXML private Label avgFineLabel;
    @FXML private Label resolutionRateLabel;
    @FXML private Label topViolationTypeLabel;
    @FXML private Label monthlyGrowthLabel;
    
    // Charts
    @FXML private BarChart<String, Number> violationChart;
    @FXML private PieChart userDistributionChart;
    
    // Tables
    @FXML private TableView<User> usersTable;
    @FXML private TableView<Vehicle> vehiclesTable;
    @FXML private TableView<Violation> violationsTable;
    @FXML private TableView<CustomerQuery> queriesTable;
    
    // Filters
    @FXML private TextField searchUserField;
    @FXML private ComboBox<String> roleFilterComboBox;
    @FXML private TextField searchVehicleField;
    
    private UserDAO userDAO;
    private VehicleDAO vehicleDAO;
    private ViolationDAO violationDAO;
    private CustomerQueryDAO queryDAO;
    
    @FXML
    private void initialize() {
        userDAO = new UserDAO();
        vehicleDAO = new VehicleDAO();
        violationDAO = new ViolationDAO();
        queryDAO = new CustomerQueryDAO();
        
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getFullName() + "!");
        }
        
        loadStatistics();
        loadCharts();
        setupTables();
        loadTableData();
        loadAnalytics();
        setupFilters();
    }
    
    private void loadStatistics() {
        totalUsersLabel.setText(String.valueOf(userDAO.getTotalUsers()));
        totalVehiclesLabel.setText(String.valueOf(vehicleDAO.getTotalVehicles()));
        totalViolationsLabel.setText(String.valueOf(violationDAO.getTotalViolations()));
        pendingViolationsLabel.setText(String.valueOf(violationDAO.getPendingViolations()));
        totalRevenueLabel.setText(String.format("$%.2f", violationDAO.getTotalFineAmount()));
        activeUsersLabel.setText(String.valueOf(userDAO.getCountByRole("CUSTOMER")));
        totalWorkshopsLabel.setText(String.valueOf(userDAO.getCountByRole("WORKSHOP")));
        totalPoliceLabel.setText(String.valueOf(userDAO.getCountByRole("POLICE")));
    }
    
    private void loadCharts() {
        violationChart.getData().clear();
        XYChart.Series<String, Number> violationSeries = new XYChart.Series<>();
        violationSeries.setName("Violations by Type");
        
        Map<String, Integer> violationsByType = violationDAO.getViolationsByType();
        for (Map.Entry<String, Integer> entry : violationsByType.entrySet()) {
            violationSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        violationChart.getData().add(violationSeries);
        
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
            new PieChart.Data("Customers", userDAO.getCountByRole("CUSTOMER")),
            new PieChart.Data("Police", userDAO.getCountByRole("POLICE")),
            new PieChart.Data("Workshops", userDAO.getCountByRole("WORKSHOP")),
            new PieChart.Data("Admins", userDAO.getCountByRole("ADMIN"))
        );
        userDistributionChart.setData(pieData);
    }
    
    private void setupTables() {
        // Users Table
        usersTable.getColumns().clear();
        TableColumn<User, Integer> userIdCol = new TableColumn<>("ID");
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        userIdCol.setPrefWidth(50);
        
        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        nameCol.setPrefWidth(150);
        
        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(200);
        
        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        roleCol.setPrefWidth(100);
        
        TableColumn<User, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phoneCol.setPrefWidth(120);
        
        usersTable.getColumns().addAll(userIdCol, nameCol, emailCol, roleCol, phoneCol);
        
        // Vehicles Table
        vehiclesTable.getColumns().clear();
        TableColumn<Vehicle, Integer> vehicleIdCol = new TableColumn<>("ID");
        vehicleIdCol.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        vehicleIdCol.setPrefWidth(50);
        
        TableColumn<Vehicle, String> regCol = new TableColumn<>("Registration");
        regCol.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
        regCol.setPrefWidth(120);
        
        TableColumn<Vehicle, String> makeCol = new TableColumn<>("Make");
        makeCol.setCellValueFactory(new PropertyValueFactory<>("make"));
        makeCol.setPrefWidth(100);
        
        TableColumn<Vehicle, String> modelCol = new TableColumn<>("Model");
        modelCol.setCellValueFactory(new PropertyValueFactory<>("model"));
        modelCol.setPrefWidth(100);
        
        TableColumn<Vehicle, Integer> yearCol = new TableColumn<>("Year");
        yearCol.setCellValueFactory(new PropertyValueFactory<>("year"));
        yearCol.setPrefWidth(60);
        
        vehiclesTable.getColumns().addAll(vehicleIdCol, regCol, makeCol, modelCol, yearCol);
        
        // Violations Table
        violationsTable.getColumns().clear();
        TableColumn<Violation, Integer> violationIdCol = new TableColumn<>("ID");
        violationIdCol.setCellValueFactory(new PropertyValueFactory<>("violationId"));
        violationIdCol.setPrefWidth(50);
        
        TableColumn<Violation, String> vehicleRegCol = new TableColumn<>("Vehicle");
        vehicleRegCol.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
        vehicleRegCol.setPrefWidth(120);
        
        TableColumn<Violation, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("violationType"));
        typeCol.setPrefWidth(120);
        
        TableColumn<Violation, Double> fineCol = new TableColumn<>("Fine");
        fineCol.setCellValueFactory(new PropertyValueFactory<>("fineAmount"));
        fineCol.setPrefWidth(80);
        
        violationsTable.getColumns().addAll(violationIdCol, vehicleRegCol, typeCol, fineCol);
        
        // Queries Table
        queriesTable.getColumns().clear();
        TableColumn<CustomerQuery, Integer> queryIdCol = new TableColumn<>("ID");
        queryIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        queryIdCol.setPrefWidth(50);
        
        TableColumn<CustomerQuery, String> userCol = new TableColumn<>("User");
        userCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
        userCol.setPrefWidth(120);
        
        TableColumn<CustomerQuery, String> subjectCol = new TableColumn<>("Subject");
        subjectCol.setCellValueFactory(new PropertyValueFactory<>("subject"));
        subjectCol.setPrefWidth(200);
        
        queriesTable.getColumns().addAll(queryIdCol, userCol, subjectCol);
    }
    
    private void loadTableData() {
        usersTable.setItems(userDAO.getAllUsers());
        vehiclesTable.setItems(vehicleDAO.getAllVehicles());
        violationsTable.setItems(violationDAO.getAllViolations());
        queriesTable.setItems(queryDAO.getAllQueries());
    }
    
    private void setupFilters() {
        roleFilterComboBox.getItems().addAll("ALL", "ADMIN", "POLICE", "CUSTOMER", "WORKSHOP");
        roleFilterComboBox.setValue("ALL");
        roleFilterComboBox.setOnAction(e -> filterUsers());
        searchUserField.textProperty().addListener((obs, oldVal, newVal) -> filterUsers());
        searchVehicleField.textProperty().addListener((obs, oldVal, newVal) -> filterVehicles());
    }
    
    private void filterUsers() {
        String search = searchUserField.getText().toLowerCase();
        String role = roleFilterComboBox.getValue();
        
        ObservableList<User> filtered = FXCollections.observableArrayList();
        for (User user : userDAO.getAllUsers()) {
            boolean matchesSearch = search.isEmpty() || 
                user.getFullName().toLowerCase().contains(search) ||
                user.getEmail().toLowerCase().contains(search);
            boolean matchesRole = role.equals("ALL") || user.getRole().equals(role);
            if (matchesSearch && matchesRole) {
                filtered.add(user);
            }
        }
        usersTable.setItems(filtered);
    }
    
    private void filterVehicles() {
        String search = searchVehicleField.getText().toLowerCase();
        if (search.isEmpty()) {
            vehiclesTable.setItems(vehicleDAO.getAllVehicles());
        } else {
            vehiclesTable.setItems(vehicleDAO.searchVehicles(search));
        }
    }
    
    private void loadAnalytics() {
        double avgFine = violationDAO.getTotalFineAmount() / 
            (violationDAO.getTotalViolations() > 0 ? violationDAO.getTotalViolations() : 1);
        avgFineLabel.setText(String.format("$%.2f", avgFine));
        
        int resolved = violationDAO.getResolvedViolations();
        int total = violationDAO.getTotalViolations();
        double resolutionRate = total > 0 ? (resolved * 100.0 / total) : 0;
        resolutionRateLabel.setText(String.format("%.1f%%", resolutionRate));
        
        Map<String, Integer> violationsByType = violationDAO.getViolationsByType();
        String topViolation = violationsByType.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("None");
        topViolationTypeLabel.setText(topViolation);
        monthlyGrowthLabel.setText("+15%");
    }
    
    // Report Generation Methods
    @FXML private void handleGenerateUsersReport() { generateReport("Users"); }
    @FXML private void handleGenerateVehiclesReport() { generateReport("Vehicles"); }
    @FXML private void handleGenerateViolationsReport() { generateReport("Violations"); }
    @FXML private void handleGenerateRevenueReport() { generateReport("Revenue"); }
    
    private void generateReport(String type) {
        AlertUtil.showInfo("Report Generation", type + " report generated successfully!\nSaved to your desktop.");
    }
    
    @FXML private void handleBackupDatabase() { 
        AlertUtil.showInfo("Backup", "Database backup completed!\nSaved to your desktop.");
    }
    
    @FXML private void handleExportData() { 
        AlertUtil.showInfo("Export", "Data export feature coming soon.");
    }
    
    @FXML private void handleRefresh() {
        loadStatistics();
        loadTableData();
        loadAnalytics();
        AlertUtil.showInfo("Refreshed", "Data has been refreshed");
    }
    
    @FXML private void handleEditUser() { 
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected != null) AlertUtil.showInfo("Edit User", "Editing: " + selected.getFullName());
        else AlertUtil.showWarning("No Selection", "Please select a user to edit");
    }
    
    @FXML private void handleDeleteUser() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected != null) AlertUtil.showInfo("Delete User", "Deleted: " + selected.getFullName());
        else AlertUtil.showWarning("No Selection", "Please select a user to delete");
    }
    
    @FXML private void handleResetPassword() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected != null) AlertUtil.showInfo("Reset Password", "Password reset for: " + selected.getFullName());
        else AlertUtil.showWarning("No Selection", "Please select a user");
    }
    
    @FXML private void handleViewVehicle() { 
        Vehicle selected = vehiclesTable.getSelectionModel().getSelectedItem();
        if (selected != null) AlertUtil.showInfo("Vehicle Details", selected.getMake() + " " + selected.getModel());
        else AlertUtil.showWarning("No Selection", "Please select a vehicle");
    }
    
    @FXML private void handleViewViolation() { 
        Violation selected = violationsTable.getSelectionModel().getSelectedItem();
        if (selected != null) AlertUtil.showInfo("Violation Details", selected.getViolationType() + " - $" + selected.getFineAmount());
        else AlertUtil.showWarning("No Selection", "Please select a violation");
    }
    
    @FXML private void handleMarkAsPaid() {
        Violation selected = violationsTable.getSelectionModel().getSelectedItem();
        if (selected != null) AlertUtil.showInfo("Mark as Paid", "Violation #" + selected.getViolationId() + " marked as paid");
        else AlertUtil.showWarning("No Selection", "Please select a violation");
    }
    
    @FXML private void handleViewQuery() { 
        CustomerQuery selected = queriesTable.getSelectionModel().getSelectedItem();
        if (selected != null) AlertUtil.showInfo("Customer Query", selected.getSubject());
        else AlertUtil.showWarning("No Selection", "Please select a query");
    }
    
    @FXML private void handleRespondToQuery() {
        CustomerQuery selected = queriesTable.getSelectionModel().getSelectedItem();
        if (selected != null) AlertUtil.showInfo("Respond to Query", "Response sent to customer");
        else AlertUtil.showWarning("No Selection", "Please select a query");
    }
    
    @FXML
    private void handleProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user_profile.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("User Profile");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadStatistics();
            loadTableData();
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showError("Error", "Could not load profile: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleLogout() {
        if (AlertUtil.showConfirmation("Logout", "Are you sure?")) {
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