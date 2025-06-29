package com.university.service;

import com.university.dao.CourseDAO;
import com.university.dao.RegistrationDAO;
import com.university.dao.StudentDAO;
import com.university.model.Course;
import com.university.model.Student;

import java.util.List;
import java.util.Scanner;

/**
 * Service class for Registration operations
 * Handles business logic for student course assignments
 */
public class RegistrationService {
    private RegistrationDAO registrationDAO;
    private StudentDAO studentDAO;
    private CourseDAO courseDAO;
    private Scanner scanner;

    public RegistrationService() {
        this.registrationDAO = new RegistrationDAO();
        this.studentDAO = new StudentDAO();
        this.courseDAO = new CourseDAO();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Interactive course assignment interface
     * Allows user to select a student and assign courses
     */
    public void assignCourseToStudent() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("              COURSE ASSIGNMENT SYSTEM");
        System.out.println("=".repeat(60));

        try {
            // Step 1: Display and select student
            Student selectedStudent = selectStudent();
            if (selectedStudent == null) {
                return;
            }

            // Step 2: Display student's current courses
            displayStudentCurrentCourses(selectedStudent.getStudentId());

            // Step 3: Display available courses and assign
            assignCoursesToStudent(selectedStudent);

        } catch (Exception e) {
            System.err.println("❌ Error during course assignment: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Display students and allow user to select one
     * @return Selected Student object or null if cancelled
     */
    private Student selectStudent() {
        List<Student> students = studentDAO.getAllStudents();

        if (students.isEmpty()) {
            System.out.println("❌ No students found! Please register students first.");
            return null;
        }

        System.out.println("\nAVAILABLE STUDENTS:");
        System.out.println("-".repeat(70));
        System.out.printf("%-5s %-20s %-25s %-15s%n", "ID", "Name", "Email", "Phone");
        System.out.println("-".repeat(70));

        for (Student student : students) {
            System.out.printf("%-5d %-20s %-25s %-15s%n",
                    student.getStudentId(),
                    student.getFullName(),
                    student.getEmail(),
                    student.getPhone());
        }

        System.out.println("-".repeat(70));
        System.out.print("Enter Student ID to assign courses (0 to cancel): ");

        try {
            int studentId = Integer.parseInt(scanner.nextLine().trim());

            if (studentId == 0) {
                System.out.println("❌ Course assignment cancelled.");
                return null;
            }

            Student selectedStudent = studentDAO.getStudentById(studentId);
            if (selectedStudent == null) {
                System.out.println("❌ Student with ID " + studentId + " not found!");
                return null;
            }

            System.out.println("\n✅ Selected Student: " + selectedStudent.getFullName() +
                    " (ID: " + selectedStudent.getStudentId() + ")");
            return selectedStudent;

        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input! Please enter a valid student ID.");
            return null;
        }
    }

    /**
     * Display current courses for a student
     * @param studentId Student ID
     */
    private void displayStudentCurrentCourses(int studentId) {
        List<Course> currentCourses = registrationDAO.getCoursesForStudent(studentId);

        System.out.println("\n" + "-".repeat(50));
        System.out.println("CURRENT REGISTERED COURSES:");
        System.out.println("-".repeat(50));

        if (currentCourses.isEmpty()) {
            System.out.println("📭 No courses currently registered.");
        } else {
            System.out.printf("%-8s %-25s %-8s %-15s%n", "Code", "Course Name", "Credits", "Department");
            System.out.println("-".repeat(50));

            for (Course course : currentCourses) {
                System.out.printf("%-8s %-25s %-8d %-15s%n",
                        course.getCourseCode(),
                        course.getCourseName(),
                        course.getCredits(),
                        course.getDepartment());
            }
        }
    }

    /**
     * Assign courses to selected student
     * @param student Selected student
     */
    private void assignCoursesToStudent(Student student) {
        while (true) {
            List<Course> allCourses = courseDAO.getAllCourses();

            if (allCourses.isEmpty()) {
                System.out.println("❌ No courses available in the system!");
                return;
            }

            System.out.println("\n" + "-".repeat(80));
            System.out.println("AVAILABLE COURSES:");
            System.out.println("-".repeat(80));
            System.out.printf("%-5s %-8s %-30s %-8s %-15s %-15s%n",
                    "ID", "Code", "Course Name", "Credits", "Department", "Semester");
            System.out.println("-".repeat(80));

            for (Course course : allCourses) {
                // Check if student is already registered
                boolean isRegistered = registrationDAO.isStudentRegisteredForCourse(
                        student.getStudentId(), course.getCourseId());
                String status = isRegistered ? " [REGISTERED]" : "";

                System.out.printf("%-5d %-8s %-30s %-8d %-15s %-15s%s%n",
                        course.getCourseId(),
                        course.getCourseCode(),
                        course.getCourseName(),
                        course.getCredits(),
                        course.getDepartment(),
                        course.getSemester(),
                        status);
            }

            System.out.println("-".repeat(80));
            System.out.print("Enter Course ID to assign (0 to finish): ");

            try {
                int courseId = Integer.parseInt(scanner.nextLine().trim());

                if (courseId == 0) {
                    System.out.println("✅ Course assignment completed!");
                    break;
                }

                Course selectedCourse = courseDAO.getCourseById(courseId);
                if (selectedCourse == null) {
                    System.out.println("❌ Course with ID " + courseId + " not found!");
                    continue;
                }

                // Check if already registered
                if (registrationDAO.isStudentRegisteredForCourse(student.getStudentId(), courseId)) {
                    System.out.println("⚠️ Student is already registered for this course!");
                    continue;
                }

                // Confirm assignment
                System.out.println("\n" + "-".repeat(50));
                System.out.println("CONFIRM COURSE ASSIGNMENT:");
                System.out.println("Student: " + student.getFullName());
                System.out.println("Course: " + selectedCourse.getCourseCode() + " - " + selectedCourse.getCourseName());
                System.out.println("Credits: " + selectedCourse.getCredits());
                System.out.println("-".repeat(50));
                System.out.print("Confirm assignment? (y/n): ");

                String confirmation = scanner.nextLine().trim().toLowerCase();
                if (confirmation.equals("y") || confirmation.equals("yes")) {
                    boolean success = registrationDAO.registerStudentForCourse(
                            student.getStudentId(), courseId);

                    if (success) {
                        System.out.println("🎉 SUCCESS! " + student.getFullName() +
                                " has been registered for " + selectedCourse.getCourseCode());
                    } else {
                        System.out.println("❌ Failed to register student for course.");
                    }
                } else {
                    System.out.println("❌ Assignment cancelled.");
                }

            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input! Please enter a valid course ID.");
            }
        }
    }

    /**
     * Get registration DAO for external use
     * @return RegistrationDAO instance
     */
    public RegistrationDAO getRegistrationDAO() {
        return registrationDAO;
    }
}