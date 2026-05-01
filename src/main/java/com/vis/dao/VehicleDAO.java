package com.vis.dao;

import com.vis.model.Vehicle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class VehicleDAO {
    
    public ObservableList<Vehicle> getAllVehicles() {
        ObservableList<Vehicle> vehicles = FXCollections.observableArrayList();
        String sql = "SELECT vehicle_id, registration_number, make, model, year, color, status, owner_id FROM vehicles ORDER BY vehicle_id";
        
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Vehicle vehicle = new Vehicle();
                vehicle.setVehicleId(rs.getInt("vehicle_id"));
                vehicle.setRegistrationNumber(rs.getString("registration_number"));
                vehicle.setMake(rs.getString("make"));
                vehicle.setModel(rs.getString("model"));
                vehicle.setYear(rs.getInt("year"));
                vehicle.setColor(rs.getString("color"));
                vehicle.setStatus(rs.getString("status"));
                vehicle.setOwnerId(rs.getInt("owner_id"));
                vehicles.add(vehicle);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicles;
    }
    
    public ObservableList<Vehicle> getVehiclesByOwner(int ownerId) {
        ObservableList<Vehicle> vehicles = FXCollections.observableArrayList();
        String sql = "SELECT vehicle_id, registration_number, make, model, year, color, status FROM vehicles WHERE owner_id = ?";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, ownerId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Vehicle vehicle = new Vehicle();
                vehicle.setVehicleId(rs.getInt("vehicle_id"));
                vehicle.setRegistrationNumber(rs.getString("registration_number"));
                vehicle.setMake(rs.getString("make"));
                vehicle.setModel(rs.getString("model"));
                vehicle.setYear(rs.getInt("year"));
                vehicle.setColor(rs.getString("color"));
                vehicle.setStatus(rs.getString("status"));
                vehicle.setOwnerId(ownerId);
                vehicles.add(vehicle);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicles;
    }
    
    public ObservableList<Vehicle> searchVehicles(String search) {
        ObservableList<Vehicle> vehicles = FXCollections.observableArrayList();
        String sql = "SELECT vehicle_id, registration_number, make, model, year, color, status, owner_id FROM vehicles WHERE registration_number ILIKE ? OR make ILIKE ? OR model ILIKE ?";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            String pattern = "%" + search + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            pstmt.setString(3, pattern);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Vehicle vehicle = new Vehicle();
                vehicle.setVehicleId(rs.getInt("vehicle_id"));
                vehicle.setRegistrationNumber(rs.getString("registration_number"));
                vehicle.setMake(rs.getString("make"));
                vehicle.setModel(rs.getString("model"));
                vehicle.setYear(rs.getInt("year"));
                vehicle.setColor(rs.getString("color"));
                vehicle.setStatus(rs.getString("status"));
                vehicle.setOwnerId(rs.getInt("owner_id"));
                vehicles.add(vehicle);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicles;
    }
    
    public int getTotalVehicles() {
        String sql = "SELECT COUNT(*) FROM vehicles";
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
    
    public boolean register(Vehicle vehicle) {
        String sql = "INSERT INTO vehicles (registration_number, owner_id, make, model, year, color, status) VALUES (?, ?, ?, ?, ?, ?, 'ACTIVE')";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, vehicle.getRegistrationNumber());
            pstmt.setInt(2, vehicle.getOwnerId());
            pstmt.setString(3, vehicle.getMake());
            pstmt.setString(4, vehicle.getModel());
            pstmt.setInt(5, vehicle.getYear());
            pstmt.setString(6, vehicle.getColor());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public Map<String, Integer> getRegistrationsByMonth() {
        Map<String, Integer> registrations = new HashMap<>();
        registrations.put("2024-01", 5);
        registrations.put("2024-02", 8);
        registrations.put("2024-03", 12);
        return registrations;
    }
    public Vehicle getByRegistrationNumber(String registrationNumber) {
        String sql = "SELECT v.*, u.full_name as owner_name FROM vehicles v LEFT JOIN users u ON v.owner_id = u.user_id WHERE v.registration_number = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, registrationNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Vehicle vehicle = new Vehicle();
                vehicle.setVehicleId(rs.getInt("vehicle_id"));
                vehicle.setRegistrationNumber(rs.getString("registration_number"));
                vehicle.setMake(rs.getString("make"));
                vehicle.setModel(rs.getString("model"));
                vehicle.setYear(rs.getInt("year"));
                vehicle.setColor(rs.getString("color"));
                vehicle.setStatus(rs.getString("status"));
                vehicle.setOwnerId(rs.getInt("owner_id"));
                vehicle.setOwnerName(rs.getString("owner_name"));
                return vehicle;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Vehicle getById(int vehicleId) {
        String sql = "SELECT v.*, u.full_name as owner_name FROM vehicles v LEFT JOIN users u ON v.owner_id = u.user_id WHERE v.vehicle_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, vehicleId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Vehicle vehicle = new Vehicle();
                vehicle.setVehicleId(rs.getInt("vehicle_id"));
                vehicle.setRegistrationNumber(rs.getString("registration_number"));
                vehicle.setMake(rs.getString("make"));
                vehicle.setModel(rs.getString("model"));
                vehicle.setYear(rs.getInt("year"));
                vehicle.setColor(rs.getString("color"));
                vehicle.setStatus(rs.getString("status"));
                vehicle.setOwnerId(rs.getInt("owner_id"));
                vehicle.setOwnerName(rs.getString("owner_name"));
                return vehicle;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}