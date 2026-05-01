package com.vis.controller;

import com.vis.dao.*;
import com.vis.model.*;
import com.vis.services.PDFExportService;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

public class PoliceDashboardController {
    
    @FXML private Label welcomeLabel;
    @FXML private Label totalViolationsLabel;
    @FXML private Label pendingViolationsLabel;
    @FXML private Label paidViolationsLabel;
    @FXML private Label totalFineAmountLabel;
    @FXML private Label reportsGeneratedLabel;
    @FXML private Label vehiclesCheckedLabel;
    
    @FXML private TableView<Violation> violationsTable;
    @FXML private TableView<Vehicle> vehiclesTable;
    @FXML private TableView<PoliceReport> reportsTable;
    @FXML private TableView<Notification> notificationsTable;
    
    @FXML private TextField searchVehicleField;
    @FXML private TextField searchViolationField;
    @FXML private ComboBox<String> violationStatusFilter;
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    
    @FXML private BarChart<String, Number> violationChart;
    @FXML private PieChart statusChart;
    
    private ViolationDAO violationDAO;
    private VehicleDAO vehicleDAO;
    private PoliceReportDAO reportDAO;
    private NotificationDAO notificationDAO;
    private UserDAO userDAO;
    
    private int currentOfficerId;
    
    @FXML
    private void initialize() {
        violationDAO = new ViolationDAO();
        vehicleDAO = new VehicleDAO();
        reportDAO = new PoliceReportDAO();
        notificationDAO = new NotificationDAO();
        userDAO = new UserDAO();
        
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentOfficerId = currentUser.getUserId();
            welcomeLabel.setText("Welcome, Officer " + currentUser.getFullName() + "!");
        }
        
        setupFilters();
        setupTables();
        loadData();
        loadCharts();
        loadNotifications();
    }
    
    private void setupFilters() {
        violationStatusFilter.getItems().addAll("ALL", "Pending", "Paid", "Disputed");
        violationStatusFilter.setValue("ALL");
        violationStatusFilter.setOnAction(e -> filterViolations());
        
        searchViolationField.textProperty().addListener((obs, oldVal, newVal) -> filterViolations());
        searchVehicleField.textProperty().addListener((obs, oldVal, newVal) -> searchVehicles());
        
        fromDatePicker.setValue(LocalDate.now().minusMonths(1));
        toDatePicker.setValue(LocalDate.now());
    }
    
    private void setupTables() {
        // Violations Table
        violationsTable.getColumns().clear();
        
        TableColumn<Violation, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("violationId"));
        idCol.setPrefWidth(50);
        
        TableColumn<Violation, String> vehicleCol = new TableColumn<>("Vehicle");
        vehicleCol.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
        vehicleCol.setPrefWidth(120);
        
        TableColumn<Violation, String> typeCol = new TableColumn<>("Violation Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("violationType"));
        typeCol.setPrefWidth(150);
        
        TableColumn<Violation, Double> fineCol = new TableColumn<>("Fine Amount");
        fineCol.setCellValueFactory(new PropertyValueFactory<>("fineAmount"));
        fineCol.setPrefWidth(100);
        
        TableColumn<Violation, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(80);
        
        TableColumn<Violation, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("violationDate"));
        dateCol.setPrefWidth(120);
        
        TableColumn<Violation, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        locationCol.setPrefWidth(150);
        
        violationsTable.getColumns().addAll(idCol, vehicleCol, typeCol, fineCol, statusCol, dateCol, locationCol);
        
        // Vehicles Table (for search)
        vehiclesTable.getColumns().clear();
        
        TableColumn<Vehicle, Integer> vidCol = new TableColumn<>("ID");
        vidCol.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        vidCol.setPrefWidth(50);
        
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
        
        TableColumn<Vehicle, String> ownerCol = new TableColumn<>("Owner");
        ownerCol.setCellValueFactory(new PropertyValueFactory<>("ownerName"));
        ownerCol.setPrefWidth(150);
        
        vehiclesTable.getColumns().addAll(vidCol, regCol, makeCol, modelCol, yearCol, ownerCol);
        
        // Reports Table
        reportsTable.getColumns().clear();
        
        TableColumn<PoliceReport, Integer> ridCol = new TableColumn<>("ID");
        ridCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        ridCol.setPrefWidth(50);
        
        TableColumn<PoliceReport, String> reportNumCol = new TableColumn<>("Report #");
        reportNumCol.setCellValueFactory(new PropertyValueFactory<>("reportNumber"));
        reportNumCol.setPrefWidth(120);
        
        TableColumn<PoliceReport, String> incidentCol = new TableColumn<>("Incident Type");
        incidentCol.setCellValueFactory(new PropertyValueFactory<>("incidentType"));
        incidentCol.setPrefWidth(120);
        
        TableColumn<PoliceReport, String> vehicleRegCol = new TableColumn<>("Vehicle");
        vehicleRegCol.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
        vehicleRegCol.setPrefWidth(120);
        
        TableColumn<PoliceReport, String> statusRCol = new TableColumn<>("Status");
        statusRCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusRCol.setPrefWidth(80);
        
        TableColumn<PoliceReport, String> dateRCol = new TableColumn<>("Date");
        dateRCol.setCellValueFactory(new PropertyValueFactory<>("reportDate"));
        dateRCol.setPrefWidth(120);
        
        reportsTable.getColumns().addAll(ridCol, reportNumCol, incidentCol, vehicleRegCol, statusRCol, dateRCol);
        
        // Notifications Table
        notificationsTable.getColumns().clear();
        
        TableColumn<Notification, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(200);
        
        TableColumn<Notification, String> msgCol = new TableColumn<>("Message");
        msgCol.setCellValueFactory(new PropertyValueFactory<>("message"));
        msgCol.setPrefWidth(300);
        
        TableColumn<Notification, String> typeColN = new TableColumn<>("Type");
        typeColN.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeColN.setPrefWidth(100);
        
        TableColumn<Notification, String> dateColN = new TableColumn<>("Date");
        dateColN.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        dateColN.setPrefWidth(120);
        
        notificationsTable.getColumns().addAll(titleCol, msgCol, typeColN, dateColN);
    }
    
    private void loadData() {
        // Load violations
        ObservableList<Violation> violations = violationDAO.getAllViolations();
        violationsTable.setItems(violations);
        
        // Update statistics
        updateStatistics();
    }
    
    private void updateStatistics() {
        ObservableList<Violation> violations = violationDAO.getAllViolations();
        int total = violations.size();
        int pending = 0;
        int paid = 0;
        double totalFine = 0;
        
        for (Violation v : violations) {
            if ("Paid".equals(v.getStatus())) {
                paid++;
                totalFine += v.getFineAmount();
            } else if ("Pending".equals(v.getStatus()) || "Unpaid".equals(v.getStatus())) {
                pending++;
            }
        }
        
        totalViolationsLabel.setText(String.valueOf(total));
        pendingViolationsLabel.setText(String.valueOf(pending));
        paidViolationsLabel.setText(String.valueOf(paid));
        totalFineAmountLabel.setText(String.format("$%.2f", totalFine));
        reportsGeneratedLabel.setText(String.valueOf(reportDAO.getAllReports().size()));
    }
    
    private void loadCharts() {
        // Violations by Type Chart
        violationChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Violations by Type");
        
        Map<String, Integer> violationsByType = violationDAO.getViolationsByType();
        for (Map.Entry<String, Integer> entry : violationsByType.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        violationChart.getData().add(series);
        
        // Status Pie Chart
        ObservableList<Violation> violations = violationDAO.getAllViolations();
        int pending = 0, paid = 0, disputed = 0;
        for (Violation v : violations) {
            if ("Paid".equals(v.getStatus())) paid++;
            else if ("Pending".equals(v.getStatus())) pending++;
            else if ("Disputed".equals(v.getStatus())) disputed++;
        }
        
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
            new PieChart.Data("Paid", paid),
            new PieChart.Data("Pending", pending),
            new PieChart.Data("Disputed", disputed)
        );
        statusChart.setData(pieData);
    }
    
    private void filterViolations() {
        String search = searchViolationField.getText().toLowerCase();
        String statusFilter = violationStatusFilter.getValue();
        
        ObservableList<Violation> filtered = FXCollections.observableArrayList();
        for (Violation v : violationDAO.getAllViolations()) {
            boolean matchesSearch = search.isEmpty() ||
                v.getViolationType().toLowerCase().contains(search) ||
                (v.getRegistrationNumber() != null && v.getRegistrationNumber().toLowerCase().contains(search));
            
            boolean matchesStatus = statusFilter.equals("ALL") || v.getStatus().equals(statusFilter);
            
            if (matchesSearch && matchesStatus) {
                filtered.add(v);
            }
        }
        violationsTable.setItems(filtered);
    }
    
    private void searchVehicles() {
        String search = searchVehicleField.getText().toLowerCase();
        if (search.isEmpty()) {
            vehiclesTable.setItems(FXCollections.observableArrayList());
        } else {
            vehiclesTable.setItems(vehicleDAO.searchVehicles(search));
            vehiclesCheckedLabel.setText("Vehicles Checked: " + vehiclesTable.getItems().size());
        }
    }
    
    private void loadNotifications() {
        notificationsTable.setItems(notificationDAO.getNotificationsByUser(currentOfficerId));
    }
    
    @FXML
    private void handleAddViolation() {
        Dialog<Violation> dialog = new Dialog<>();
        dialog.setTitle("Add Violation");
        dialog.setHeaderText("Record New Violation");
        
        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField regField = new TextField();
        regField.setPromptText("Vehicle Registration");
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Speeding", "Parking", "Red Light", "No Insurance", "No Seatbelt", "Drunk Driving", "Reckless Driving");
        typeCombo.setValue("Speeding");
        TextField fineField = new TextField();
        fineField.setPromptText("Fine Amount");
        TextArea descArea = new TextArea();
        descArea.setPromptText("Description");
        descArea.setPrefRowCount(3);
        TextField locationField = new TextField();
        locationField.setPromptText("Location");
        
        grid.add(new Label("Vehicle Registration:"), 0, 0);
        grid.add(regField, 1, 0);
        grid.add(new Label("Violation Type:"), 0, 1);
        grid.add(typeCombo, 1, 1);
        grid.add(new Label("Fine Amount:"), 0, 2);
        grid.add(fineField, 1, 2);
        grid.add(new Label("Location:"), 0, 3);
        grid.add(locationField, 1, 3);
        grid.add(new Label("Description:"), 0, 4);
        grid.add(descArea, 1, 4);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                Violation violation = new Violation();
                violation.setViolationType(typeCombo.getValue());
                try {
                    violation.setFineAmount(Double.parseDouble(fineField.getText()));
                } catch (NumberFormatException e) {
                    violation.setFineAmount(0);
                }
                violation.setDescription(descArea.getText());
                violation.setLocation(locationField.getText());
                violation.setStatus("Pending");
                violation.setOfficerId(currentOfficerId);
                return violation;
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(violation -> {
            // First find vehicle by registration
            Vehicle vehicle = vehicleDAO.getByRegistrationNumber(regField.getText());
            if (vehicle != null) {
                violation.setVehicleId(vehicle.getVehicleId());
                if (violationDAO.create(violation)) {
                    AlertUtil.showInfo("Success", "Violation recorded successfully!");
                    loadData();
                    loadCharts();
                    
                    // Send notification
                    Notification notif = new Notification();
                    notif.setUserId(vehicle.getOwnerId());
                    notif.setTitle("New Violation");
                    notif.setMessage("Your vehicle " + vehicle.getRegistrationNumber() + " has received a violation.");
                    notif.setType("ALERT");
                    notificationDAO.create(notif);
                } else {
                    AlertUtil.showError("Error", "Failed to record violation");
                }
            } else {
                AlertUtil.showError("Error", "Vehicle not found");
            }
        });
    }
    
    @FXML
    private void handleEditViolation() {
        Violation selected = violationsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            AlertUtil.showInfo("Edit Violation", "Edit violation #" + selected.getViolationId());
        } else {
            AlertUtil.showWarning("No Selection", "Please select a violation to edit");
        }
    }
    
    @FXML
    private void handleMarkAsPaid() {
        Violation selected = violationsTable.getSelectionModel().getSelectedItem();
        if (selected != null && !"Paid".equals(selected.getStatus())) {
            if (AlertUtil.showConfirmation("Mark as Paid", "Mark violation #" + selected.getViolationId() + " as paid?")) {
                if (violationDAO.markAsPaid(selected.getViolationId())) {
                    AlertUtil.showInfo("Success", "Violation marked as paid");
                    loadData();
                    loadCharts();
                } else {
                    AlertUtil.showError("Error", "Failed to update violation");
                }
            }
        } else {
            AlertUtil.showWarning("No Selection", "Please select an unpaid violation");
        }
    }
    
    @FXML
    private void handleGenerateReport() {
        Violation selected = violationsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Dialog<PoliceReport> dialog = new Dialog<>();
            dialog.setTitle("Generate Police Report");
            dialog.setHeaderText("Generate Report for Violation #" + selected.getViolationId());
            
            ButtonType generateButton = new ButtonType("Generate", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(generateButton, ButtonType.CANCEL);
            
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20));
            
            ComboBox<String> typeCombo = new ComboBox<>();
            typeCombo.getItems().addAll("Accident", "Theft", "Inspection", "Recovery");
            typeCombo.setValue("Inspection");
            TextArea descArea = new TextArea();
            descArea.setPromptText("Report Description");
            descArea.setPrefRowCount(4);
            descArea.setText(selected.getDescription());
            
            grid.add(new Label("Report Type:"), 0, 0);
            grid.add(typeCombo, 1, 0);
            grid.add(new Label("Description:"), 0, 1);
            grid.add(descArea, 1, 1);
            
            dialog.getDialogPane().setContent(grid);
            
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == generateButton) {
                    PoliceReport report = new PoliceReport();
                    report.setReportNumber("PR-" + System.currentTimeMillis());
                    report.setVehicleId(selected.getVehicleId());
                    report.setOfficerId(currentOfficerId);
                    report.setIncidentType(typeCombo.getValue());
                    report.setDescription(descArea.getText());
                    report.setStatus("Active");
                    return report;
                }
                return null;
            });
            
            dialog.showAndWait().ifPresent(report -> {
                if (reportDAO.create(report)) {
                    AlertUtil.showInfo("Success", "Police report generated!");
                    loadData();
                    reportsGeneratedLabel.setText(String.valueOf(reportDAO.getAllReports().size()));
                } else {
                    AlertUtil.showError("Error", "Failed to generate report");
                }
            });
        } else {
            AlertUtil.showWarning("No Selection", "Please select a violation to generate report");
        }
    }
    
    @FXML
    private void handleViewReports() {
        reportsTable.setItems(reportDAO.getAllReports());
        TabPane tabPane = (TabPane) reportsTable.getScene().lookup(".tab-pane");
        if (tabPane != null) {
            tabPane.getSelectionModel().select(3); // Reports tab index
        }
    }
    
    @FXML
    private void handleExportViolations() {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filePath = System.getProperty("user.home") + "/Desktop/violations_report_" + timestamp + ".csv";
            
            StringBuilder sb = new StringBuilder();
            sb.append("Violation ID,Vehicle,Violation Type,Fine Amount,Status,Date,Location\n");
            
            for (Violation v : violationsTable.getItems()) {
                sb.append(v.getViolationId()).append(",");
                sb.append(v.getRegistrationNumber() != null ? v.getRegistrationNumber() : "").append(",");
                sb.append(v.getViolationType()).append(",");
                sb.append(v.getFineAmount()).append(",");
                sb.append(v.getStatus()).append(",");
                sb.append(v.getViolationDate()).append(",");
                sb.append(v.getLocation() != null ? v.getLocation() : "").append("\n");
            }
            
            java.nio.file.Files.write(java.nio.file.Paths.get(filePath), sb.toString().getBytes());
            AlertUtil.showInfo("Export Complete", "Violations exported to:\n" + filePath);
        } catch (Exception e) {
            AlertUtil.showError("Error", "Failed to export: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleViewVehicleDetails() {
        Vehicle selected = vehiclesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Vehicle Details");
            alert.setHeaderText(selected.getRegistrationNumber());
            
            VBox vbox = new VBox(10);
            vbox.getChildren().addAll(
                new Label("Make: " + selected.getMake()),
                new Label("Model: " + selected.getModel()),
                new Label("Year: " + selected.getYear()),
                new Label("Color: " + (selected.getColor() != null ? selected.getColor() : "N/A")),
                new Label("Status: " + selected.getStatus()),
                new Label("Owner: " + (selected.getOwnerName() != null ? selected.getOwnerName() : "N/A"))
            );
            alert.getDialogPane().setContent(vbox);
            alert.showAndWait();
        } else {
            AlertUtil.showWarning("No Selection", "Please select a vehicle");
        }
    }
    
    @FXML
    private void handleClearFilters() {
        searchViolationField.clear();
        violationStatusFilter.setValue("ALL");
        fromDatePicker.setValue(LocalDate.now().minusMonths(1));
        toDatePicker.setValue(LocalDate.now());
        loadData();
    }
    
    @FXML
    private void handleRefresh() {
        loadData();
        loadCharts();
        loadNotifications();
        AlertUtil.showInfo("Refreshed", "Data has been refreshed");
    }
    
    @FXML
    private void handleProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user_profile.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Officer Profile");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showError("Error", "Could not load profile: " + e.getMessage());
        }
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