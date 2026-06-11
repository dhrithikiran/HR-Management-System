package com.yourname.myapp.entity;

import java.time.LocalDate;

/**
 * Employee POJO (Plain Old Java Object) representing an employee in the system.
 * Uses auto-generated ID with format EMP-XXXXXXXX
 * 
 * Maps to: employees table
 * Columns: employee_id, employee_name, department, job_role, employment_status, joining_date, created_at, updated_at
 */
public class Employee {

    private String employeeId;
    private String employeeName;
    private String department;
    private String jobRole;
    private EmploymentStatus employmentStatus;
    private LocalDate joiningDate;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    // Constructors
    public Employee() {}

    public Employee(String employeeId, String employeeName, String department, String jobRole, EmploymentStatus employmentStatus) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.department = department;
        this.jobRole = jobRole;
        this.employmentStatus = employmentStatus;
    }

    // Getters
    public String getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getDepartment() {
        return department;
    }

    public String getJobRole() {
        return jobRole;
    }

    public EmploymentStatus getEmploymentStatus() {
        return employmentStatus;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setJobRole(String jobRole) {
        this.jobRole = jobRole;
    }

    public void setEmploymentStatus(EmploymentStatus employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Initialize default values before saving (call manually when creating new employee)
     */
    public void initializeDefaults() {
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
        if (this.employmentStatus == null) {
            this.employmentStatus = EmploymentStatus.ACTIVE;
        }
        if (this.joiningDate == null) {
            this.joiningDate = LocalDate.now();
        }
    }

    /**
     * Update the lastModified timestamp (call manually when updating employee)
     */
    public void updateTimestamp() {
        this.updatedAt = LocalDate.now();
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId='" + employeeId + '\'' +
                ", employeeName='" + employeeName + '\'' +
                ", department='" + department + '\'' +
                ", jobRole='" + jobRole + '\'' +
                ", employmentStatus=" + employmentStatus +
                '}';
    }
}
