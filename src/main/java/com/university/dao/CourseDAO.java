package com.university.dao;

import com.university.database.DatabaseConnection;
import com.university.model.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Course operations
 * Handles all database operations related to courses
 */
public class CourseDAO {

    /**
     * Retrieve all courses from database
     * @return List of all courses
     */
    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses ORDER BY course_code";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

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
            System.err.println("❌ Error retrieving courses: " + e.getMessage());
            e.printStackTrace();
        }

        return courses;
    }

    /**
     * Find course by ID
     * @param courseId Course ID to search for
     * @return Course object if found, null otherwise
     */
    public Course getCourseById(int courseId) {
        String sql = "SELECT * FROM courses WHERE course_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Course course = new Course();
                course.setCourseId(rs.getInt("course_id"));
                course.setCourseCode(rs.getString("course_code"));
                course.setCourseName(rs.getString("course_name"));
                course.setCredits(rs.getInt("credits"));
                course.setDepartment(rs.getString("department"));
                course.setSemester(rs.getString("semester"));

                return course;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error finding course: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Add a new course to the database
     * @param course Course object to be added
     * @return Generated course ID if successful, -1 if failed
     */
    public int addCourse(Course course) {
        String sql = "INSERT INTO courses (course_code, course_name, credits, department, semester) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, course.getCourseCode());
            pstmt.setString(2, course.getCourseName());
            pstmt.setInt(3, course.getCredits());
            pstmt.setString(4, course.getDepartment());
            pstmt.setString(5, course.getSemester());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int courseId = generatedKeys.getInt(1);
                        course.setCourseId(courseId);
                        System.out.println("✅ Course added successfully with ID: " + courseId);
                        return courseId;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error adding course: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }
}