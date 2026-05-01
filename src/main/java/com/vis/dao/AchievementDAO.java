package com.vis.dao;

import com.vis.model.Achievement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class AchievementDAO {
    
    public ObservableList<Achievement> getAchievementsByUser(int userId) {
        ObservableList<Achievement> achievements = FXCollections.observableArrayList();
        String sql = "SELECT * FROM achievements WHERE user_id = ? ORDER BY unlocked_at DESC";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                achievements.add(extractAchievement(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return achievements;
    }
    
    public boolean create(Achievement achievement) {
        String sql = "INSERT INTO achievements (user_id, badge_name, description, points_awarded) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, achievement.getUserId());
            pstmt.setString(2, achievement.getAchievementName());
            pstmt.setString(3, achievement.getDescription());
            pstmt.setInt(4, achievement.getPoints());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public int getTotalPoints(int userId) {
        String sql = "SELECT COALESCE(SUM(points_awarded), 0) FROM achievements WHERE user_id = ?";
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
    
    private Achievement extractAchievement(ResultSet rs) throws SQLException {
        Achievement achievement = new Achievement();
        achievement.setId(rs.getInt("achievement_id"));
        achievement.setUserId(rs.getInt("user_id"));
        achievement.setAchievementName(rs.getString("badge_name"));
        achievement.setDescription(rs.getString("description"));
        achievement.setPoints(rs.getInt("points_awarded"));
        achievement.setAchievedAt(rs.getTimestamp("unlocked_at"));
        return achievement;
    }
}