package com.vis.dao;

import com.vis.model.CustomerQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class CustomerQueryDAO {
    
    public ObservableList<CustomerQuery> getAllQueries() {
        ObservableList<CustomerQuery> queries = FXCollections.observableArrayList();
        String sql = "SELECT query_id, subject, query_text, status, created_at FROM customer_queries ORDER BY query_id DESC";
        
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                CustomerQuery query = new CustomerQuery();
                query.setId(rs.getInt("query_id"));
                query.setSubject(rs.getString("subject"));
                query.setMessage(rs.getString("query_text"));
                query.setStatus(rs.getString("status"));
                query.setCreatedAt(rs.getTimestamp("created_at"));
                queries.add(query);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return queries;
    }
    
    public ObservableList<CustomerQuery> getQueriesByUser(int userId) {
        ObservableList<CustomerQuery> queries = FXCollections.observableArrayList();
        String sql = "SELECT query_id, subject, query_text, status, created_at FROM customer_queries WHERE customer_id = ?";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                CustomerQuery query = new CustomerQuery();
                query.setId(rs.getInt("query_id"));
                query.setSubject(rs.getString("subject"));
                query.setMessage(rs.getString("query_text"));
                query.setStatus(rs.getString("status"));
                query.setCreatedAt(rs.getTimestamp("created_at"));
                queries.add(query);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return queries;
    }
    
    public boolean create(CustomerQuery query) {
        String sql = "INSERT INTO customer_queries (customer_id, subject, query_text, status) VALUES (?, ?, ?, 'Open')";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, query.getUserId());
            pstmt.setString(2, query.getSubject());
            pstmt.setString(3, query.getMessage());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean respondToQuery(int queryId, String response, int respondedBy) {
        String sql = "UPDATE customer_queries SET response_text = ?, responded_by = ?, status = 'Answered', updated_at = NOW() WHERE query_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, response);
            pstmt.setInt(2, respondedBy);
            pstmt.setInt(3, queryId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}