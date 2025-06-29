package com.university.model;

import java.time.LocalDateTime;

/**
 * Registration model class representing student-course enrollment
 */
public class Registration {
    private int registrationId;
    private int studentId;
    private int courseId;
    private LocalDateTime enrollmentDate;
    private String grade;

    // Student and Course objects for joined queries
    private Student student;
    private Course course;

    // Default constructor
    public Registration() {}

    // Constructor for new registration
    public Registration(int studentId, int courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
    }

    // Constructor with all fields
    public Registration(int registrationId, int studentId, int courseId,
                        LocalDateTime enrollmentDate, String grade) {
        this.registrationId = registrationId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.enrollmentDate = enrollmentDate;
        this.grade = grade;
    }

    // Getters and Setters
    public int getRegistrationId() { return registrationId; }
    public void setRegistrationId(int registrationId) { this.registrationId = registrationId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public LocalDateTime getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDateTime enrollmentDate) { this.enrollmentDate = enrollmentDate; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    @Override
    public String toString() {
        return String.format("Registration{ID=%d, StudentID=%d, CourseID=%d, Date=%s, Grade='%s'}",
                registrationId, studentId, courseId, enrollmentDate, grade);
    }
}