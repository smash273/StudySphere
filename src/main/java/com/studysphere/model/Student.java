package com.studysphere.model;

import java.time.LocalDateTime;

/**
 * Student entity demonstrating Inheritance.
 * Extends {@link User} by adding academic profiling attributes.
 */
public class Student extends User {
    private static final long serialVersionUID = 1L;

    private String studentId;
    private String enrollmentNo;
    private String major;
    private int semester;
    private double overallScore;

    public Student() {
        super();
        setRole(UserRole.STUDENT);
        this.semester = 1;
        this.overallScore = 0.0;
    }

    public Student(String username, String email, String passwordHash, String fullName,
                   String studentId, String enrollmentNo, String major, int semester) {
        super(username, email, passwordHash, fullName, UserRole.STUDENT);
        this.studentId = studentId;
        this.enrollmentNo = enrollmentNo;
        this.major = major;
        this.semester = semester;
        this.overallScore = 0.0;
    }

    public Student(int id, String username, String email, String passwordHash, String fullName,
                   UserRole role, LocalDateTime createdAt, LocalDateTime updatedAt,
                   String studentId, String enrollmentNo, String major, int semester, double overallScore) {
        super(id, username, email, passwordHash, fullName, role, createdAt, updatedAt);
        this.studentId = studentId;
        this.enrollmentNo = enrollmentNo;
        this.major = major;
        this.semester = semester;
        this.overallScore = overallScore;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getEnrollmentNo() {
        return enrollmentNo;
    }

    public void setEnrollmentNo(String enrollmentNo) {
        this.enrollmentNo = enrollmentNo;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public double getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(double overallScore) {
        this.overallScore = overallScore;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + getId() +
                ", username='" + getUsername() + '\'' +
                ", studentId='" + studentId + '\'' +
                ", enrollmentNo='" + enrollmentNo + '\'' +
                ", major='" + major + '\'' +
                ", semester=" + semester +
                ", overallScore=" + overallScore +
                '}';
    }
}
