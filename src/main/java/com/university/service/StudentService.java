package com.university.service;

import com.university.dao.StudentDAO;
import com.university.model.Student;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Service class for Student operations
 * Handles business logic and user interaction for student management
 */
public class StudentService {
    private StudentDAO studentDAO;
    private Scanner scanner;

    public StudentService() {
        this.studentDAO = new StudentDAO();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Interactive student registration form
     * Collects student information from user input and saves to database
     */
    public void registerNewStudent() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           STUDENT REGISTRATION FORM");
        System.out.println("=".repeat(50));

        try {
            // Collect student information
            System.out.print("Enter First Name: ");
            String firstName = scanner.nextLine().trim();
            if (firstName.isEmpty()) {
                System.out.println("❌ First name cannot be empty!");
                return;
            }

            System.out.print("Enter Last Name: ");
            String lastName = scanner.nextLine().trim();
            if (lastName.isEmpty()) {
                System.out.println("❌ Last name cannot be empty!");
                return;
            }

            System.out.print("Enter Email: ");
            String email = scanner.nextLine().trim();
            if (!isValidEmail(email)) {
                System.out.println("❌ Invalid email format!");
                return;
            }

            // Check if email already exists
            if (studentDAO.emailExists(email)) {
                System.out.println("❌ A student with this email already exists!");
                return;
            }

            System.out.print("Enter Phone Number: ");
            String phone = scanner.nextLine().trim();
            if (!isValidPhone(phone)) {
                System.out.println("❌ Invalid phone number format!");
                return;
            }

            System.out.print("Enter Date of Birth (YYYY-MM-DD): ");
            String dobString = scanner.nextLine().trim();
            LocalDate dateOfBirth;
            try {
                dateOfBirth = LocalDate.parse(dobString, DateTimeFormatter.ISO_LOCAL_DATE);
                if (dateOfBirth.isAfter(LocalDate.now())) {
                    System.out.println("❌ Date of birth cannot be in the future!");
                    return;
                }
            } catch (DateTimeParseException e) {
                System.out.println("❌ Invalid date format! Please use YYYY-MM-DD format.");
                return;
            }

            // Create new student object
            Student student = new Student(firstName, lastName, email, phone, dateOfBirth);

            // Display confirmation
            System.out.println("\n" + "-".repeat(40));
            System.out.println("CONFIRM STUDENT DETAILS:");
            System.out.println("-".repeat(40));
            System.out.println("Name: " + student.getFullName());
            System.out.println("Email: " + student.getEmail());
            System.out.println("Phone: " + student.getPhone());
            System.out.println("Date of Birth: " + student.getDateOfBirth());
            System.out.println("-".repeat(40));
            System.out.print("Save this student? (y/n): ");

            String confirmation = scanner.nextLine().trim().toLowerCase();
            if (confirmation.equals("y") || confirmation.equals("yes")) {
                // Save student to database
                int studentId = studentDAO.addStudent(student);
                if (studentId > 0) {
                    System.out.println("\n🎉 SUCCESS! Student registered with ID: " + studentId);
                    System.out.println("Student Name: " + student.getFullName());
                } else {
                    System.out.println("❌ Failed to register student. Please try again.");
                }
            } else {
                System.out.println("❌ Student registration cancelled.");
            }

        } catch (Exception e) {
            System.err.println("❌ Error during student registration: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Display all registered students
     */
    public void displayAllStudents() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("                        ALL REGISTERED STUDENTS");
        System.out.println("=".repeat(80));

        List<Student> students = studentDAO.getAllStudents();

        if (students.isEmpty()) {
            System.out.println("📭 No students found in the database.");
            return;
        }

        System.out.printf("%-5s %-15s %-15s %-25s %-15s %-12s%n",
                "ID", "First Name", "Last Name", "Email", "Phone", "Birth Date");
        System.out.println("-".repeat(80));

        for (Student student : students) {
            System.out.printf("%-5d %-15s %-15s %-25s %-15s %-12s%n",
                    student.getStudentId(),
                    student.getFirstName(),
                    student.getLastName(),
                    student.getEmail(),
                    student.getPhone(),
                    student.getDateOfBirth());
        }

        System.out.println("-".repeat(80));
        System.out.println("Total Students: " + students.size());
    }

    /**
     * Get student by ID with error handling
     * @param studentId Student ID to search for
     * @return Student object if found, null otherwise
     */
    public Student getStudentById(int studentId) {
        return studentDAO.getStudentById(studentId);
    }

    /**
     * Get all students
     * @return List of all students
     */
    public List<Student> getAllStudents() {
        return studentDAO.getAllStudents();
    }

    /**
     * Validate email format
     * @param email Email to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    /**
     * Validate phone number format
     * @param phone Phone number to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidPhone(String phone) {
        // Allow digits, spaces, hyphens, and parentheses
        String phoneRegex = "^[\\d\\s\\-\\(\\)\\+]{10,15}$";
        Pattern pattern = Pattern.compile(phoneRegex);
        return pattern.matcher(phone).matches();
    }
}