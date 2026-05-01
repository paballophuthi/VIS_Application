package com.vis.dao;

import java.sql.*;

public class DatabaseConnection {
    private static Connection connection = null;
    private static final String URL = "jdbc:postgresql://localhost:5433/vis_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "59114340";
    
    static {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connected to PostgreSQL 18 on port 5433 successfully!");
            System.out.println("   Database: vis_db");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ PostgreSQL Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed!");
            System.err.println("   Error: " + e.getMessage());
            System.err.println("   Make sure PostgreSQL is running on port 5433 and database 'vis_db' exists");
        }
    }
    
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ New database connection established");
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to get connection: " + e.getMessage());
        }
        return connection;
    }
    
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static boolean testConnection() {
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT 1")) {
            System.out.println("✅ Database connection test successful!");
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Database connection test failed: " + e.getMessage());
            return false;
        }
    }
}