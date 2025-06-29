package com.university.main;

import com.university.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Test class to verify database connectivity and table structure
 */
public class DatabaseTest {
    public static void main(String[] args) {
        System.out.println("🧪 Testing Database Connection...");

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null) {
                System.out.println("✅ Database connection successful!");

                // Test table existence
                Statement stmt = conn.createStatement();

                // Test students table
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM students");
                if (rs.next()) {
                    System.out.println("✅ Students table exists. Count: " + rs.getInt(1));
                }

                // Test courses table
                rs = stmt.executeQuery("SELECT COUNT(*) FROM courses");
                if (rs.next()) {
                    System.out.println("✅ Courses table exists. Count: " + rs.getInt(1));
                }

                // Test registrations table
                rs = stmt.executeQuery("SELECT COUNT(*) FROM registrations");
                if (rs.next()) {
                    System.out.println("✅ Registrations table exists. Count: " + rs.getInt(1));
                }

                System.out.println("🎉 All database tests passed!");

            } else {
                System.out.println("❌ Database connection failed!");
            }
        } catch (Exception e) {
            System.err.println("❌ Database test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}