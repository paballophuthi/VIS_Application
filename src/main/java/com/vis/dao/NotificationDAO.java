package com.vis.dao;

import com.vis.model.Notification;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class NotificationDAO {
    
    public ObservableList<Notification> getNotificationsByUser(int userId) {
        ObservableList<Notification> notifications = FXCollections.observableArrayList();
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                notifications.add(extractNotification(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }
    
    public boolean create(Notification notification) {
        String sql = "INSERT INTO notifications (user_id, title, message, type) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, notification.getUserId());
            pstmt.setString(2, notification.getTitle());
            pstmt.setString(3, notification.getMessage());
            pstmt.setString(4, notification.getType());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean markAsRead(int notificationId) {
        String sql = "UPDATE notifications SET is_read = true, read_at = NOW() WHERE notification_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, notificationId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean delete(int notificationId) {
        String sql = "DELETE FROM notifications WHERE notification_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, notificationId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public int getUnreadCount(int userId) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = false";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    private Notification extractNotification(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setId(rs.getInt("notification_id"));
        notification.setUserId(rs.getInt("user_id"));
        notification.setTitle(rs.getString("title"));
        notification.setMessage(rs.getString("message"));
        notification.setType(rs.getString("type"));
        notification.setRead(rs.getBoolean("is_read"));
        notification.setCreatedAt(rs.getTimestamp("created_at"));
        notification.setReadAt(rs.getTimestamp("read_at"));
        return notification;
    }
}