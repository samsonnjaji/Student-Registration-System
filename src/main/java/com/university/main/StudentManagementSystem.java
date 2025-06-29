package com.university.main;

import com.university.database.DatabaseConnection;
import com.university.service.DisplayService;
import com.university.service.RegistrationService;
import com.university.service.StudentService;

import java.util.Scanner;

/**
 * Main application class for Student Registration & Course Management System
 * BY NJAJI SIBONA  SAMSON
 *
 * This system demonstrates:
 * - JDBC database connectivity
 * - PreparedStatement usage
 * - Student registration
 * - Course assignment
 * - Data retrieval using SQL JOINs
 *
 * @author Your Name
 * @studentId Your Student ID
 * @course Database Programming CAT
 */
public class StudentManagementSystem {

    private static StudentService studentService;
    private static RegistrationService registrationService;
    private static DisplayService displayService;
    private static Scanner scanner;

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║              STUDENT REGISTRATION & COURSE MANAGEMENT        ║");
        System.out.println("║                         SYSTEM v1.0                         ║");
        System.out.println("║                                                              ║");
        System.out.println("║  A JDBC-based Student Management System                     ║");
        System.out.println("║  Created for Database Programming CAT                       ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        // Initialize services
        initializeSystem();

        // Test database connection
        if (!DatabaseConnection.testConnection()) {
            System.err.println("❌ Failed to connect to database. Please check your configuration.");
            System.err.println("   Make sure MySQL is running and credentials are correct.");
            return;
        }

        // Start main application loop
        runMainMenu();

        // Cleanup
        cleanup();
    }

    /**
     * Initialize all system components
     */
    private static void initializeSystem() {
        studentService = new StudentService();
        registrationService = new RegistrationService();
        displayService = new DisplayService();
        scanner = new Scanner(System.in);

        System.out.println("✅ System initialized successfully!");
        System.out.println("🔌 Database connection established!");
    }

    /**
     * Main application menu loop
     */
    private static void runMainMenu() {
        while (true) {
            displayMainMenu();

            try {
                System.out.print("Enter your choice (1-7): ");
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1:
                        studentService.registerNewStudent();
                        break;
                    case 2:
                        studentService.displayAllStudents();
                        break;
                    case 3:
                        registrationService.assignCourseToStudent();
                        break;
                    case 4:
                        displayService.displayAllRegistrations();
                        break;
                    case 5:
                        displayService.displayRegistrationStatistics();
                        break;
                    case 6:
                        displayService.displayCourseEnrollmentReport();
                        break;
                    case 7:
                        System.out.println("\n👋 Thank you for using the Student Management System!");
                        System.out.println("💾 All data has been saved to the database.");
                        System.out.println("🔒 Closing database connections...");
                        return;
                    default:
                        System.out.println("❌ Invalid choice! Please select a number between 1-7.");
                }

                // Pause before showing menu again
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();

            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input! Please enter a number between 1-7.");
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
            } catch (Exception e) {
                System.err.println("❌ An unexpected error occurred: " + e.getMessage());
                e.printStackTrace();
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
            }
        }
    }

    /**
     * Display the main menu interface
     */
    private static void displayMainMenu() {
        System.out.println("\n\n" + "╔" + "═".repeat(62) + "╗");
        System.out.println("║                    MAIN MENU                            ║");
        System.out.println("╠" + "═".repeat(62) + "╣");
        System.out.println("║  1. 📝 Register New Student                             ║");
        System.out.println("║  2. 👥 View All Students                                ║");
        System.out.println("║  3. 📚 Assign Course to Student                         ║");
        System.out.println("║  4. 📊 View All Registrations (with SQL JOIN)          ║");
        System.out.println("║  5. 📈 Registration Statistics                          ║");
        System.out.println("║  6. 📋 Course Enrollment Report                         ║");
        System.out.println("║  7. 🚪 Exit System                                      ║");
        System.out.println("╚" + "═".repeat(62) + "╝");
        System.out.println();
    }

    /**
     * Cleanup resources before exit
     */
    private static void cleanup() {
        try {
            if (scanner != null) {
                scanner.close();
            }
            DatabaseConnection.closeConnection();
            System.out.println("✅ System shutdown completed successfully.");
        } catch (Exception e) {
            System.err.println("❌ Error during cleanup: " + e.getMessage());
        }
    }
}