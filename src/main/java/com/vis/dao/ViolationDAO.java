package com.vis.dao;

import com.vis.model.Violation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ViolationDAO {
    
    public ObservableList<Violation> getAllViolations() {
        ObservableList<Violation> violations = FXCollections.observableArrayList();
        String sql = "SELECT violation_id, violation_type, fine_amount, status, violation_date, vehicle_id FROM violations ORDER BY violation_id DESC";
        
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Violation violation = new Violation();
                violation.setViolationId(rs.getInt("violation_id"));
                violation.setViolationType(rs.getString("violation_type"));
                violation.setFineAmount(rs.getDouble("fine_amount"));
                violation.setStatus(rs.getString("status"));
                violation.setViolationDate(rs.getTimestamp("violation_date"));
                violation.setVehicleId(rs.getInt("vehicle_id"));
                violations.add(violation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return violations;
    }
    
    public ObservableList<Violation> getViolationsByUser(int userId) {
        ObservableList<Violation> violations = FXCollections.observableArrayList();
        String sql = "SELECT v.violation_id, v.violation_type, v.fine_amount, v.status, v.violation_date " +
                     "FROM violations v JOIN vehicles veh ON v.vehicle_id = veh.vehicle_id " +
                     "WHERE veh.owner_id = ?";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Violation violation = new Violation();
                violation.setViolationId(rs.getInt("violation_id"));
                violation.setViolationType(rs.getString("violation_type"));
                violation.setFineAmount(rs.getDouble("fine_amount"));
                violation.setStatus(rs.getString("status"));
                violation.setViolationDate(rs.getTimestamp("violation_date"));
                violations.add(violation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return violations;
    }
    
    public int getTotalViolations() {
        String sql = "SELECT COUNT(*) FROM violations";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public int getPendingViolations() {
        String sql = "SELECT COUNT(*) FROM violations WHERE status != 'Paid'";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public int getResolvedViolations() {
        String sql = "SELECT COUNT(*) FROM violations WHERE status = 'Paid'";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public double getTotalFineAmount() {
        String sql = "SELECT COALESCE(SUM(fine_amount), 0) FROM violations WHERE status = 'Paid'";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public Map<String, Integer> getViolationsByType() {
        Map<String, Integer> violations = new HashMap<>();
        String sql = "SELECT violation_type, COUNT(*) as count FROM violations GROUP BY violation_type";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                violations.put(rs.getString("violation_type"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return violations;
    }
    
    public boolean markAsPaid(int violationId) {
        String sql = "UPDATE violations SET status = 'Paid', paid_date = NOW() WHERE violation_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, violationId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean create(Violation violation) {
        String sql = "INSERT INTO violations (vehicle_id, officer_id, violation_type, description, location, fine_amount, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, violation.getVehicleId());
            pstmt.setInt(2, violation.getOfficerId());
            pstmt.setString(3, violation.getViolationType());
            pstmt.setString(4, violation.getDescription());
            pstmt.setString(5, violation.getLocation());
            pstmt.setDouble(6, violation.getFineAmount());
            pstmt.setString(7, violation.getStatus());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}