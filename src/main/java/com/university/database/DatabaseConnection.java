package com.university.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection utility class for Student Management System
 * Handles MySQL database connectivity using JDBC
 */
public class DatabaseConnection {
    // Database configuration constants
    private static final String URL = "jdbc:mysql://localhost:3306/student_management_system";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "11202004"; // Your MySQL password

    private static Connection connection = null;

    /**
     * Establishes and returns database connection
     * @return Connection object or null if connection fails
     */
    public static Connection getConnection() {
        try {
            // Check if connection is null or closed
            if (connection == null || connection.isClosed()) {
                // Load MySQL JDBC driver
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Establish connection
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("✅ Database connected successfully!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Driver not found: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Closes the database connection
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔒 Database connection closed successfully.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error closing database connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Test database connectivity
     * @return true if connection successful, false otherwise
     */
    public static boolean testConnection() {
        Connection testConn = getConnection();
        return testConn != null;
    }
}