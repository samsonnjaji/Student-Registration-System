package com.university.dao;

import com.university.database.DatabaseConnection;
import com.university.model.Course;
import com.university.model.Registration;
import com.university.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Registration operations
 * Handles all database operations related to student-course registrations
 */
public class RegistrationDAO {

    /**
     * Register a student for a course
     * @param studentId Student ID
     * @param courseId Course ID
     * @return true if registration successful, false otherwise
     */
    public boolean registerStudentForCourse(int studentId, int courseId) {
        String sql = "INSERT INTO registrations (student_id, course_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("✅ Student registered for course successfully!");
                return true;
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Duplicate entry error
                System.err.println("❌ Student is already registered for this course!");
            } else {
                System.err.println("❌ Error registering student for course: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return false;
    }

    /**
     * Get all registrations with student and course details using SQL JOIN
     * @return List of Registration objects with populated Student and Course data
     */
    public List<Registration> getAllRegistrationsWithDetails() {
        List<Registration> registrations = new ArrayList<>();

        String sql = """
                SELECT r.registration_id, r.student_id, r.course_id, r.enrollment_date, r.grade,
                       s.first_name, s.last_name, s.email, s.phone, s.date_of_birth, s.registration_date,
                       c.course_code, c.course_name, c.credits, c.department, c.semester
                FROM registrations r
                INNER JOIN students s ON r.student_id = s.student_id
                INNER JOIN courses c ON r.course_id = c.course_id
                ORDER BY s.last_name, s.first_name, c.course_code
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Registration registration = new Registration();
                registration.setRegistrationId(rs.getInt("registration_id"));
                registration.setStudentId(rs.getInt("student_id"));
                registration.setCourseId(rs.getInt("course_id"));

                Timestamp enrollmentTimestamp = rs.getTimestamp("enrollment_date");
                if (enrollmentTimestamp != null) {
                    registration.setEnrollmentDate(enrollmentTimestamp.toLocalDateTime());
                }

                registration.setGrade(rs.getString("grade"));

                // Create and populate Student object
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

                registration.setStudent(student);

                // Create and populate Course object
                Course course = new Course();
                course.setCourseId(rs.getInt("course_id"));
                course.setCourseCode(rs.getString("course_code"));
                course.setCourseName(rs.getString("course_name"));
                course.setCredits(rs.getInt("credits"));
                course.setDepartment(rs.getString("department"));
                course.setSemester(rs.getString("semester"));

                registration.setCourse(course);

                registrations.add(registration);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error retrieving registrations: " + e.getMessage());
            e.printStackTrace();
        }

        return registrations;
    }

    /**
     * Get courses registered by a specific student
     * @param studentId Student ID
     * @return List of courses the student is registered for
     */
    public List<Course> getCoursesForStudent(int studentId) {
        List<Course> courses = new ArrayList<>();

        String sql = """
                SELECT c.course_id, c.course_code, c.course_name, c.credits, c.department, c.semester
                FROM registrations r
                INNER JOIN courses c ON r.course_id = c.course_id
                WHERE r.student_id = ?
                ORDER BY c.course_code
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Course course = new Course();
                course.setCourseId(rs.getInt("course_id"));
                course.setCourseCode(rs.getString("course_code"));
                course.setCourseName(rs.getString("course_name"));
                course.setCredits(rs.getInt("credits"));
                course.setDepartment(rs.getString("department"));
                course.setSemester(rs.getString("semester"));

                courses.add(course);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error retrieving student courses: " + e.getMessage());
            e.printStackTrace();
        }

        return courses;
    }

    /**
     * Check if student is already registered for a course
     * @param studentId Student ID
     * @param courseId Course ID
     * @return true if already registered, false otherwise
     */
    public boolean isStudentRegisteredForCourse(int studentId, int courseId) {
        String sql = "SELECT COUNT(*) FROM registrations WHERE student_id = ? AND course_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error checking registration: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}
