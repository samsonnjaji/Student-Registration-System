package com.university.dao;

import com.university.database.DatabaseConnection;
import com.university.model.Student;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Student operations
 * Handles all database operations related to students
 */
public class StudentDAO {

    /**
     * Add a new student to the database
     * @param student Student object to be added
     * @return Generated student ID if successful, -1 if failed
     */
    public int addStudent(Student student) {
        String sql = "INSERT INTO students (first_name, last_name, email, phone, date_of_birth) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, student.getFirstName());
            pstmt.setString(2, student.getLastName());
            pstmt.setString(3, student.getEmail());
            pstmt.setString(4, student.getPhone());
            pstmt.setDate(5, Date.valueOf(student.getDateOfBirth()));

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int studentId = generatedKeys.getInt(1);
                        student.setStudentId(studentId);
                        System.out.println("✅ Student added successfully with ID: " + studentId);
                        return studentId;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error adding student: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Retrieve all students from database
     * @return List of all students
     */
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY last_name, first_name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Student student = new Student();
                student.setStudentId(rs.getInt("student_id"));
                student.setFirstName(rs.getString("first_name"));
                student.setLastName(rs.getString("last_name"));
                student.setEmail(rs.getString("email"));
                student.setPhone(rs.getString("phone"));

                Date dobDate = rs.getDate("date_of_birth");
                if (dobDate != null) {
                    student.setDateOfBirth(dobDate.toLocalDate());
                }

                Timestamp regTimestamp = rs.getTimestamp("registration_date");
                if (regTimestamp != null) {
                    student.setRegistrationDate(regTimestamp.toLocalDateTime());
                }

                students.add(student);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error retrieving students: " + e.getMessage());
            e.printStackTrace();
        }

        return students;
    }

    /**
     * Find student by ID
     * @param studentId Student ID to search for
     * @return Student object if found, null otherwise
     */
    public Student getStudentById(int studentId) {
        String sql = "SELECT * FROM students WHERE student_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Student student = new Student();
                student.setStudentId(rs.getInt("student_id"));
                student.setFirstName(rs.getString("first_name"));
                student.setLastName(rs.getString("last_name"));
                student.setEmail(rs.getString("email"));
                student.setPhone(rs.getString("phone"));

                Date dobDate = rs.getDate("date_of_birth");
                if (dobDate != null) {
                    student.setDateOfBirth(dobDate.toLocalDate());
                }

                Timestamp regTimestamp = rs.getTimestamp("registration_date");
                if (regTimestamp != null) {
                    student.setRegistrationDate(regTimestamp.toLocalDateTime());
                }

                return student;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error finding student: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Check if email already exists in database
     * @param email Email to check
     * @return true if email exists, false otherwise
     */
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM students WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error checking email: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}