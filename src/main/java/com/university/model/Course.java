package com.university.model;

/**
 * Course model class representing course entity
 */
public class Course {
    private int courseId;
    private String courseCode;
    private String courseName;
    private int credits;
    private String department;
    private String semester;

    // Default constructor
    public Course() {}

    // Constructor for new course (without ID)
    public Course(String courseCode, String courseName, int credits, String department, String semester) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
        this.department = department;
        this.semester = semester;
    }

    // Constructor with all fields
    public Course(int courseId, String courseCode, String courseName,
                  int credits, String department, String semester) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
        this.department = department;
        this.semester = semester;
    }

    // Getters and Setters
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    @Override
    public String toString() {
        return String.format("Course{ID=%d, Code='%s', Name='%s', Credits=%d, Department='%s'}",
                courseId, courseCode, courseName, credits, department);
    }
}