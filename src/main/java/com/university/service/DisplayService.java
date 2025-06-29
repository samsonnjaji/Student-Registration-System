package com.university.service;

import com.university.dao.RegistrationDAO;
import com.university.model.Course;
import com.university.model.Registration;
import com.university.model.Student;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class for displaying data and reports
 * Uses SQL JOINs to retrieve and display comprehensive registration information
 */
public class DisplayService {
    private RegistrationDAO registrationDAO;

    public DisplayService() {
        this.registrationDAO = new RegistrationDAO();
    }

    /**
     * Display all student registrations with course details using SQL JOIN
     * This method demonstrates the use of SQL joins as required by the CAT
     */
    public void displayAllRegistrations() {
        System.out.println("\n" + "=".repeat(100));
        System.out.println("                           STUDENT COURSE REGISTRATIONS REPORT");
        System.out.println("                               (Using SQL JOIN Query)");
        System.out.println("=".repeat(100));

        List<Registration> registrations = registrationDAO.getAllRegistrationsWithDetails();

        if (registrations.isEmpty()) {
            System.out.println("📭 No student registrations found in the database.");
            return;
        }

        // Group registrations by student for better display
        Map<Integer, List<Registration>> registrationsByStudent = registrations.stream()
                .collect(Collectors.groupingBy(Registration::getStudentId));

        int totalRegistrations = 0;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Map.Entry<Integer, List<Registration>> entry : registrationsByStudent.entrySet()) {
            List<Registration> studentRegistrations = entry.getValue();
            Student student = studentRegistrations.get(0).getStudent(); // Get student info from first registration

            System.out.println("\n" + "─".repeat(100));
            System.out.printf("📚 STUDENT: %s (ID: %d)%n", student.getFullName(), student.getStudentId());
            System.out.printf("   Email: %s | Phone: %s | DOB: %s%n",
                    student.getEmail(), student.getPhone(), student.getDateOfBirth());
            System.out.println("─".repeat(100));

            System.out.printf("%-8s %-30s %-8s %-15s %-15s %-18s %-6s%n",
                    "Code", "Course Name", "Credits", "Department", "Semester", "Enrolled Date", "Grade");
            System.out.println("-".repeat(100));

            for (Registration registration : studentRegistrations) {
                Course course = registration.getCourse();
                String enrollmentDate = registration.getEnrollmentDate() != null
                        ? registration.getEnrollmentDate().format(formatter)
                        : "N/A";
                String grade = registration.getGrade() != null ? registration.getGrade() : "N/A";

                System.out.printf("%-8s %-30s %-8d %-15s %-15s %-18s %-6s%n",
                        course.getCourseCode(),
                        course.getCourseName(),
                        course.getCredits(),
                        course.getDepartment(),
                        course.getSemester(),
                        enrollmentDate,
                        grade);

                totalRegistrations++;
            }

            // Calculate total credits for this student
            int totalCredits = studentRegistrations.stream()
                    .mapToInt(reg -> reg.getCourse().getCredits())
                    .sum();

            System.out.println("-".repeat(100));
            System.out.printf("   📊 STUDENT SUMMARY: %d courses registered | %d total credits%n",
                    studentRegistrations.size(), totalCredits);
        }

        System.out.println("\n" + "=".repeat(100));
        System.out.println("📈 OVERALL SUMMARY:");
        System.out.println("   Total Students with Registrations: " + registrationsByStudent.size());
        System.out.println("   Total Course Registrations: " + totalRegistrations);
        System.out.println("   Average Courses per Student: " +
                String.format("%.2f", (double) totalRegistrations / registrationsByStudent.size()));
        System.out.println("=".repeat(100));
    }

    /**
     * Display detailed registration statistics
     */
    public void displayRegistrationStatistics() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("                 REGISTRATION STATISTICS");
        System.out.println("=".repeat(70));

        List<Registration> registrations = registrationDAO.getAllRegistrationsWithDetails();

        if (registrations.isEmpty()) {
            System.out.println("📭 No registration data available for statistics.");
            return;
        }

        // Department statistics
        Map<String, Long> departmentCounts = registrations.stream()
                .collect(Collectors.groupingBy(
                        reg -> reg.getCourse().getDepartment(),
                        Collectors.counting()
                ));

        System.out.println("\n📊 REGISTRATIONS BY DEPARTMENT:");
        System.out.println("-".repeat(40));
        departmentCounts.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .forEach(entry ->
                        System.out.printf("%-25s: %d registrations%n", entry.getKey(), entry.getValue())
                );

        // Semester statistics
        Map<String, Long> semesterCounts = registrations.stream()
                .collect(Collectors.groupingBy(
                        reg -> reg.getCourse().getSemester(),
                        Collectors.counting()
                ));

        System.out.println("\n📅 REGISTRATIONS BY SEMESTER:");
        System.out.println("-".repeat(40));
        semesterCounts.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .forEach(entry ->
                        System.out.printf("%-25s: %d registrations%n", entry.getKey(), entry.getValue())
                );

        // Credit distribution
        Map<Integer, Long> creditCounts = registrations.stream()
                .collect(Collectors.groupingBy(
                        reg -> reg.getCourse().getCredits(),
                        Collectors.counting()
                ));

        System.out.println("\n💳 REGISTRATIONS BY CREDIT HOURS:");
        System.out.println("-".repeat(40));
        creditCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry ->
                        System.out.printf("%d credit courses       : %d registrations%n",
                                entry.getKey(), entry.getValue())
                );

        System.out.println("=".repeat(70));
    }

    /**
     * Display course enrollment report
     */
    public void displayCourseEnrollmentReport() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("                      COURSE ENROLLMENT REPORT");
        System.out.println("=".repeat(80));

        List<Registration> registrations = registrationDAO.getAllRegistrationsWithDetails();

        if (registrations.isEmpty()) {
            System.out.println("📭 No enrollment data available.");
            return;
        }

        // Group by course
        Map<Integer, List<Registration>> enrollmentsByCourse = registrations.stream()
                .collect(Collectors.groupingBy(Registration::getCourseId));

        System.out.printf("%-10s %-30s %-12s %-15s %-10s%n",
                "Code", "Course Name", "Enrolled", "Department", "Credits");
        System.out.println("-".repeat(80));

        enrollmentsByCourse.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().size() - e1.getValue().size()) // Sort by enrollment count
                .forEach(entry -> {
                    List<Registration> courseRegistrations = entry.getValue();
                    Course course = courseRegistrations.get(0).getCourse();
                    int enrollmentCount = courseRegistrations.size();

                    System.out.printf("%-10s %-30s %-12d %-15s %-10d%n",
                            course.getCourseCode(),
                            course.getCourseName(),
                            enrollmentCount,
                            course.getDepartment(),
                            course.getCredits());
                });

        System.out.println("-".repeat(80));
        System.out.println("Total Courses with Enrollments: " + enrollmentsByCourse.size());

        // Find most and least popular courses
        if (!enrollmentsByCourse.isEmpty()) {
            Map.Entry<Integer, List<Registration>> mostPopular = enrollmentsByCourse.entrySet().stream()
                    .max((e1, e2) -> e1.getValue().size() - e2.getValue().size())
                    .orElse(null);

            Map.Entry<Integer, List<Registration>> leastPopular = enrollmentsByCourse.entrySet().stream()
                    .min((e1, e2) -> e1.getValue().size() - e2.getValue().size())
                    .orElse(null);

            if (mostPopular != null) {
                Course course = mostPopular.getValue().get(0).getCourse();
                System.out.println("\n🏆 Most Popular Course: " + course.getCourseCode() +
                        " (" + mostPopular.getValue().size() + " students)");
            }

            if (leastPopular != null && !leastPopular.equals(mostPopular)) {
                Course course = leastPopular.getValue().get(0).getCourse();
                System.out.println("📉 Least Popular Course: " + course.getCourseCode() +
                        " (" + leastPopular.getValue().size() + " students)");
            }
        }

        System.out.println("=".repeat(80));
    }
}