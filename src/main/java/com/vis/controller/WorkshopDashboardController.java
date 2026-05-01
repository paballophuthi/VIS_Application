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
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

public class WorkshopDashboardController {
    
    @FXML private Label welcomeLabel;
    @FXML private Label totalServicesLabel;
    @FXML private Label pendingServicesLabel;
    @FXML private Label completedServicesLabel;
    @FXML private Label totalRevenueLabel;
    @FXML private Label activeVehiclesLabel;
    @FXML private Label satisfiedCustomersLabel;
    
    @FXML private TableView<ServiceRecord> servicesTable;
    @FXML private TableView<Vehicle> vehiclesTable;
    @FXML private TableView<CustomerQuery> queriesTable;
    @FXML private TableView<Notification> notificationsTable;
    
    @FXML private TextField searchVehicleField;
    @FXML private TextField searchServiceField;
    @FXML private ComboBox<String> serviceStatusFilter;
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    
    @FXML private BarChart<String, Number> revenueChart;
    @FXML private PieChart serviceTypeChart;
    
    private ServiceRecordDAO serviceDAO;
    private VehicleDAO vehicleDAO;
    private CustomerQueryDAO queryDAO;
    private NotificationDAO notificationDAO;
    private UserDAO userDAO;
    
    private int currentWorkshopId;
    
    @FXML
    private void initialize() {
        serviceDAO = new ServiceRecordDAO();
        vehicleDAO = new VehicleDAO();
        queryDAO = new CustomerQueryDAO();
        notificationDAO = new NotificationDAO();
        userDAO = new UserDAO();
        
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentWorkshopId = currentUser.getUserId();
            welcomeLabel.setText("Welcome, " + currentUser.getFullName() + "!");
        }
        
        setupFilters();
        setupTables();
        loadData();
        loadCharts();
        loadNotifications();
    }
    
    private void setupFilters() {
        serviceStatusFilter.getItems().addAll("ALL", "Pending", "In Progress", "Completed", "Cancelled");
        serviceStatusFilter.setValue("ALL");
        serviceStatusFilter.setOnAction(e -> filterServices());
        
        searchServiceField.textProperty().addListener((obs, oldVal, newVal) -> filterServices());
        searchVehicleField.textProperty().addListener((obs, oldVal, newVal) -> searchVehicles());
        
        fromDatePicker.setValue(LocalDate.now().minusMonths(1));
        toDatePicker.setValue(LocalDate.now());
    }
    
    private void setupTables() {
        // Services Table
        servicesTable.getColumns().clear();
        
        TableColumn<ServiceRecord, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("serviceId"));
        idCol.setPrefWidth(50);
        
        TableColumn<ServiceRecord, String> vehicleCol = new TableColumn<>("Vehicle");
        vehicleCol.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
        vehicleCol.setPrefWidth(120);
        
        TableColumn<ServiceRecord, String> serviceTypeCol = new TableColumn<>("Service Type");
        serviceTypeCol.setCellValueFactory(new PropertyValueFactory<>("serviceType"));
        serviceTypeCol.setPrefWidth(150);
        
        TableColumn<ServiceRecord, String> dateCol = new TableColumn<>("Service Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("serviceDate"));
        dateCol.setPrefWidth(100);
        
        TableColumn<ServiceRecord, Double> costCol = new TableColumn<>("Cost");
        costCol.setCellValueFactory(new PropertyValueFactory<>("cost"));
        costCol.setPrefWidth(80);
        
        TableColumn<ServiceRecord, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(100);
        
        TableColumn<ServiceRecord, String> nextServiceCol = new TableColumn<>("Next Service");
        nextServiceCol.setCellValueFactory(new PropertyValueFactory<>("nextServiceDate"));
        nextServiceCol.setPrefWidth(100);
        
        servicesTable.getColumns().addAll(idCol, vehicleCol, serviceTypeCol, dateCol, costCol, statusCol, nextServiceCol);
        
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
        
        // Queries Table
        queriesTable.getColumns().clear();
        
        TableColumn<CustomerQuery, Integer> qidCol = new TableColumn<>("ID");
        qidCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        qidCol.setPrefWidth(50);
        
        TableColumn<CustomerQuery, String> subjectCol = new TableColumn<>("Subject");
        subjectCol.setCellValueFactory(new PropertyValueFactory<>("subject"));
        subjectCol.setPrefWidth(200);
        
        TableColumn<CustomerQuery, String> customerCol = new TableColumn<>("Customer");
        customerCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
        customerCol.setPrefWidth(150);
        
        TableColumn<CustomerQuery, String> qStatusCol = new TableColumn<>("Status");
        qStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        qStatusCol.setPrefWidth(100);
        
        TableColumn<CustomerQuery, String> qDateCol = new TableColumn<>("Date");
        qDateCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        qDateCol.setPrefWidth(120);
        
        queriesTable.getColumns().addAll(qidCol, subjectCol, customerCol, qStatusCol, qDateCol);
        
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
        ObservableList<ServiceRecord> services = serviceDAO.getServicesByWorkshop(currentWorkshopId);
        servicesTable.setItems(services);
        updateStatistics();
    }
    
    private void updateStatistics() {
        ObservableList<ServiceRecord> services = serviceDAO.getServicesByWorkshop(currentWorkshopId);
        int total = services.size();
        int pending = 0;
        int completed = 0;
        double revenue = 0;
        
        for (ServiceRecord s : services) {
            if ("Pending".equals(s.getStatus())) pending++;
            else if ("Completed".equals(s.getStatus())) {
                completed++;
                revenue += s.getCost();
            }
        }
        
        totalServicesLabel.setText(String.valueOf(total));
        pendingServicesLabel.setText(String.valueOf(pending));
        completedServicesLabel.setText(String.valueOf(completed));
        totalRevenueLabel.setText(String.format("$%.2f", revenue));
        
        ObservableList<Vehicle> allVehicles = vehicleDAO.getAllVehicles();
        activeVehiclesLabel.setText(String.valueOf(allVehicles.size()));
        satisfiedCustomersLabel.setText(String.valueOf(userDAO.getCountByRole("CUSTOMER")));
    }
    
    private void loadCharts() {
        revenueChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Monthly Revenue");
        
        series.getData().add(new XYChart.Data<>("Jan", 1200));
        series.getData().add(new XYChart.Data<>("Feb", 1500));
        series.getData().add(new XYChart.Data<>("Mar", 1800));
        series.getData().add(new XYChart.Data<>("Apr", 2100));
        revenueChart.getData().add(series);
        
        ObservableList<ServiceRecord> services = serviceDAO.getServicesByWorkshop(currentWorkshopId);
        Map<String, Integer> typeCounts = new java.util.HashMap<>();
        for (ServiceRecord s : services) {
            typeCounts.put(s.getServiceType(), typeCounts.getOrDefault(s.getServiceType(), 0) + 1);
        }
        
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : typeCounts.entrySet()) {
            pieData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }
        serviceTypeChart.setData(pieData);
    }
    
    private void filterServices() {
        String search = searchServiceField.getText().toLowerCase();
        String statusFilter = serviceStatusFilter.getValue();
        
        ObservableList<ServiceRecord> filtered = FXCollections.observableArrayList();
        for (ServiceRecord s : serviceDAO.getServicesByWorkshop(currentWorkshopId)) {
            boolean matchesSearch = search.isEmpty() ||
                s.getServiceType().toLowerCase().contains(search) ||
                (s.getRegistrationNumber() != null && s.getRegistrationNumber().toLowerCase().contains(search));
            boolean matchesStatus = statusFilter.equals("ALL") || s.getStatus().equals(statusFilter);
            if (matchesSearch && matchesStatus) {
                filtered.add(s);
            }
        }
        servicesTable.setItems(filtered);
    }
    
    private void searchVehicles() {
        String search = searchVehicleField.getText().toLowerCase();
        if (search.isEmpty()) {
            vehiclesTable.setItems(FXCollections.observableArrayList());
        } else {
            vehiclesTable.setItems(vehicleDAO.searchVehicles(search));
        }
    }
    
    private void loadNotifications() {
        notificationsTable.setItems(notificationDAO.getNotificationsByUser(currentWorkshopId));
    }
    
    @FXML
    private void handleAddService() {
        Dialog<ServiceRecord> dialog = new Dialog<>();
        dialog.setTitle("Add Service Record");
        dialog.setHeaderText("Record New Service");
        
        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField regField = new TextField();
        regField.setPromptText("Vehicle Registration");
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Oil Change", "Brake Service", "Tire Rotation", "Major Service", "Engine Repair", "Transmission Service");
        typeCombo.setValue("Oil Change");
        DatePicker serviceDate = new DatePicker(LocalDate.now());
        TextField costField = new TextField();
        costField.setPromptText("Cost");
        TextArea descArea = new TextArea();
        descArea.setPromptText("Description");
        descArea.setPrefRowCount(3);
        TextField odometerField = new TextField();
        odometerField.setPromptText("Odometer Reading");
        DatePicker nextServiceDate = new DatePicker(LocalDate.now().plusMonths(6));
        
        grid.add(new Label("Vehicle Registration:"), 0, 0);
        grid.add(regField, 1, 0);
        grid.add(new Label("Service Type:"), 0, 1);
        grid.add(typeCombo, 1, 1);
        grid.add(new Label("Service Date:"), 0, 2);
        grid.add(serviceDate, 1, 2);
        grid.add(new Label("Cost:"), 0, 3);
        grid.add(costField, 1, 3);
        grid.add(new Label("Odometer Reading:"), 0, 4);
        grid.add(odometerField, 1, 4);
        grid.add(new Label("Next Service Date:"), 0, 5);
        grid.add(nextServiceDate, 1, 5);
        grid.add(new Label("Description:"), 0, 6);
        grid.add(descArea, 1, 6);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                ServiceRecord record = new ServiceRecord();
                record.setServiceType(typeCombo.getValue());
                record.setServiceDate(java.sql.Date.valueOf(serviceDate.getValue()));
                try {
                    record.setCost(Double.parseDouble(costField.getText()));
                } catch (NumberFormatException e) {
                    record.setCost(0);
                }
                record.setDescription(descArea.getText());
                try {
                    record.setOdometerReading(Integer.parseInt(odometerField.getText()));
                } catch (NumberFormatException e) {
                    record.setOdometerReading(0);
                }
                record.setNextServiceDate(java.sql.Date.valueOf(nextServiceDate.getValue()));
                record.setStatus("Pending");
                record.setWorkshopId(currentWorkshopId);
                return record;
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(record -> {
            Vehicle vehicle = vehicleDAO.getByRegistrationNumber(regField.getText());
            if (vehicle != null) {
                record.setVehicleId(vehicle.getVehicleId());
                if (serviceDAO.create(record)) {
                    AlertUtil.showInfo("Success", "Service record added successfully!");
                    loadData();
                    loadCharts();
                    
                    Notification notif = new Notification();
                    notif.setUserId(vehicle.getOwnerId());
                    notif.setTitle("Service Scheduled");
                    notif.setMessage("Your vehicle " + vehicle.getRegistrationNumber() + " has been scheduled for service.");
                    notif.setType("INFO");
                    notificationDAO.create(notif);
                } else {
                    AlertUtil.showError("Error", "Failed to add service record");
                }
            } else {
                AlertUtil.showError("Error", "Vehicle not found");
            }
        });
    }
    
    @FXML
    private void handleUpdateStatus() {
        ServiceRecord selected = servicesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            ChoiceDialog<String> dialog = new ChoiceDialog<>(selected.getStatus(), "Pending", "In Progress", "Completed", "Cancelled");
            dialog.setTitle("Update Status");
            dialog.setHeaderText("Update Service Status");
            dialog.setContentText("New Status:");
            
            dialog.showAndWait().ifPresent(newStatus -> {
                if (serviceDAO.updateStatus(selected.getServiceId(), newStatus)) {
                    AlertUtil.showInfo("Success", "Status updated to: " + newStatus);
                    loadData();
                    loadCharts();
                    
                    Vehicle vehicle = vehicleDAO.getById(selected.getVehicleId());
                    if (vehicle != null) {
                        Notification notif = new Notification();
                        notif.setUserId(vehicle.getOwnerId());
                        notif.setTitle("Service Status Update");
                        notif.setMessage("Your vehicle service status changed to: " + newStatus);
                        notif.setType("INFO");
                        notificationDAO.create(notif);
                    }
                } else {
                    AlertUtil.showError("Error", "Failed to update status");
                }
            });
        } else {
            AlertUtil.showWarning("No Selection", "Please select a service record");
        }
    }
    
    @FXML
    private void handleGenerateInvoice() {
        ServiceRecord selected = servicesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Vehicle vehicle = vehicleDAO.getById(selected.getVehicleId());
            User customer = userDAO.getUserById(vehicle.getOwnerId());
            
            try {
                String invoicePath = PDFExportService.generateInvoice(selected, vehicle, customer, 
                    System.getProperty("user.home") + "/Desktop/");
                AlertUtil.showInfo("Invoice Generated", "Invoice saved to:\n" + invoicePath);
            } catch (Exception e) {
                AlertUtil.showError("Error", "Failed to generate invoice: " + e.getMessage());
            }
        } else {
            AlertUtil.showWarning("No Selection", "Please select a service record");
        }
    }
    
    @FXML
    private void handleViewVehicle() {
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
    private void handleRespondToQuery() {
        CustomerQuery selected = queriesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Optional<String> response = AlertUtil.showInputDialog("Respond to Query", 
                "Query from: " + selected.getUserName(), "Enter your response:");
            if (response.isPresent() && !response.get().isEmpty()) {
                queryDAO.respondToQuery(selected.getId(), response.get(), currentWorkshopId);
                AlertUtil.showInfo("Success", "Response sent to customer");
                loadData();
            }
        } else {
            AlertUtil.showWarning("No Selection", "Please select a query");
        }
    }
    
    @FXML
    private void handleExportServices() {
        try {
            String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String filePath = System.getProperty("user.home") + "/Desktop/services_report_" + timestamp + ".csv";
            
            StringBuilder sb = new StringBuilder();
            sb.append("Service ID,Vehicle,Service Type,Date,Cost,Status,Next Service\n");
            
            for (ServiceRecord s : servicesTable.getItems()) {
                sb.append(s.getServiceId()).append(",");
                sb.append(s.getRegistrationNumber() != null ? s.getRegistrationNumber() : "").append(",");
                sb.append(s.getServiceType()).append(",");
                sb.append(s.getServiceDate()).append(",");
                sb.append(s.getCost()).append(",");
                sb.append(s.getStatus()).append(",");
                sb.append(s.getNextServiceDate() != null ? s.getNextServiceDate() : "").append("\n");
            }
            
            java.nio.file.Files.write(java.nio.file.Paths.get(filePath), sb.toString().getBytes());
            AlertUtil.showInfo("Export Complete", "Services exported to:\n" + filePath);
        } catch (Exception e) {
            AlertUtil.showError("Error", "Failed to export: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleClearFilters() {
        searchServiceField.clear();
        serviceStatusFilter.setValue("ALL");
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
            stage.setTitle("Workshop Profile");
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