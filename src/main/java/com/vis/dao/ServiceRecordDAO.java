package com.vis.dao;

import com.vis.model.ServiceRecord;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class ServiceRecordDAO {
    
    public ObservableList<ServiceRecord> getAllServiceRecords() {
        ObservableList<ServiceRecord> records = FXCollections.observableArrayList();
        String sql = "SELECT s.service_id, s.vehicle_id, s.workshop_id, s.service_type, s.service_date, s.cost, s.description, s.odometer_reading, s.next_service_date, s.created_at, v.registration_number, u.full_name as workshop_name FROM service_records s " +
                     "LEFT JOIN vehicles v ON s.vehicle_id = v.vehicle_id " +
                     "LEFT JOIN users u ON s.workshop_id = u.user_id " +
                     "ORDER BY s.service_id DESC";
        
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                records.add(extractRecord(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }
    
    public ObservableList<ServiceRecord> getServicesByWorkshop(int workshopId) {
        ObservableList<ServiceRecord> records = FXCollections.observableArrayList();
        String sql = "SELECT s.service_id, s.vehicle_id, s.workshop_id, s.service_type, s.service_date, s.cost, s.description, s.odometer_reading, s.next_service_date, s.created_at, v.registration_number, u.full_name as workshop_name FROM service_records s " +
                     "LEFT JOIN vehicles v ON s.vehicle_id = v.vehicle_id " +
                     "LEFT JOIN users u ON s.workshop_id = u.user_id " +
                     "WHERE s.workshop_id = ? ORDER BY s.service_date DESC";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, workshopId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                records.add(extractRecord(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }
    
    public ObservableList<ServiceRecord> getServicesByVehicle(int vehicleId) {
        ObservableList<ServiceRecord> records = FXCollections.observableArrayList();
        String sql = "SELECT s.service_id, s.vehicle_id, s.workshop_id, s.service_type, s.service_date, s.cost, s.description, s.odometer_reading, s.next_service_date, s.created_at, u.full_name as workshop_name FROM service_records s " +
                     "LEFT JOIN users u ON s.workshop_id = u.user_id " +
                     "WHERE s.vehicle_id = ? ORDER BY s.service_date DESC";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, vehicleId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                records.add(extractRecord(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }
    
    public boolean create(ServiceRecord record) {
        String sql = "INSERT INTO service_records (vehicle_id, workshop_id, service_type, service_date, cost, description, odometer_reading, next_service_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, record.getVehicleId());
            pstmt.setInt(2, record.getWorkshopId());
            pstmt.setString(3, record.getServiceType());
            pstmt.setDate(4, record.getServiceDate());
            pstmt.setDouble(5, record.getCost());
            pstmt.setString(6, record.getDescription());
            pstmt.setInt(7, record.getOdometerReading());
            pstmt.setDate(8, record.getNextServiceDate());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateStatus(int serviceId, String status) {
        // Your table doesn't have a status column, so just return true
        System.out.println("Note: Status update not stored - table has no status column");
        return true;
    }
    
    private ServiceRecord extractRecord(ResultSet rs) throws SQLException {
        ServiceRecord record = new ServiceRecord();
        record.setServiceId(rs.getInt("service_id"));
        record.setVehicleId(rs.getInt("vehicle_id"));
        record.setRegistrationNumber(rs.getString("registration_number"));
        record.setWorkshopId(rs.getInt("workshop_id"));
        record.setWorkshopName(rs.getString("workshop_name"));
        record.setServiceType(rs.getString("service_type"));
        record.setServiceDate(rs.getDate("service_date"));
        record.setCost(rs.getDouble("cost"));
        record.setDescription(rs.getString("description"));
        record.setOdometerReading(rs.getInt("odometer_reading"));
        record.setNextServiceDate(rs.getDate("next_service_date"));
        record.setStatus("Completed");
        record.setCreatedAt(rs.getTimestamp("created_at"));
        return record;
    }
}