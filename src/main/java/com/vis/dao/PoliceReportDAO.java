package com.vis.dao;

import com.vis.model.PoliceReport;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class PoliceReportDAO {
    
    public ObservableList<PoliceReport> getAllReports() {
        ObservableList<PoliceReport> reports = FXCollections.observableArrayList();
        String sql = "SELECT p.*, v.registration_number, u.full_name as officer_name FROM police_reports p " +
                     "LEFT JOIN vehicles v ON p.vehicle_id = v.vehicle_id " +
                     "LEFT JOIN users u ON p.officer_id = u.user_id " +
                     "ORDER BY p.report_id DESC";
        
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                reports.add(extractReport(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reports;
    }
    
    public ObservableList<PoliceReport> getReportsByVehicle(int vehicleId) {
        ObservableList<PoliceReport> reports = FXCollections.observableArrayList();
        String sql = "SELECT p.*, v.registration_number, u.full_name as officer_name FROM police_reports p " +
                     "LEFT JOIN vehicles v ON p.vehicle_id = v.vehicle_id " +
                     "LEFT JOIN users u ON p.officer_id = u.user_id " +
                     "WHERE p.vehicle_id = ? ORDER BY p.report_date DESC";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, vehicleId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                reports.add(extractReport(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reports;
    }
    
    public boolean create(PoliceReport report) {
        String sql = "INSERT INTO police_reports (vehicle_id, officer_id, report_type, description, location, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, report.getVehicleId());
            pstmt.setInt(2, report.getOfficerId());
            pstmt.setString(3, report.getIncidentType());
            pstmt.setString(4, report.getDescription());
            pstmt.setString(5, report.getLocation());
            pstmt.setString(6, report.getStatus());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private PoliceReport extractReport(ResultSet rs) throws SQLException {
        PoliceReport report = new PoliceReport();
        report.setId(rs.getInt("report_id"));
        report.setVehicleId(rs.getInt("vehicle_id"));
        try {
            report.setRegistrationNumber(rs.getString("registration_number"));
        } catch (SQLException e) {}
        report.setOfficerId(rs.getInt("officer_id"));
        try {
            report.setOfficerName(rs.getString("officer_name"));
        } catch (SQLException e) {}
        report.setIncidentType(rs.getString("report_type"));
        report.setDescription(rs.getString("description"));
        report.setLocation(rs.getString("location"));
        report.setReportDate(rs.getTimestamp("report_date"));
        report.setStatus(rs.getString("status"));
        // Generate a report number based on ID
        report.setReportNumber("PR-" + report.getId());
        return report;
    }
}