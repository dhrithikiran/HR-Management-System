package com.yourname.myapp.dto;

import com.yourname.myapp.entity.EmploymentStatus;

/**
 * Data Transfer Object for Employee creation and update requests.
 */
public class EmployeeRequest {
    private String employeeName;
    private String department;
    private String jobRole;
    private EmploymentStatus employmentStatus;

    public EmployeeRequest() {}

    public EmployeeRequest(String employeeName, String department, String jobRole, EmploymentStatus employmentStatus) {
        this.employeeName = employeeName;
        this.department = department;
        this.jobRole = jobRole;
        this.employmentStatus = employmentStatus;
    }

    // Getters
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

    // Setters
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

    @Override
    public String toString() {
        return "EmployeeRequest{" +
                "employeeName='" + employeeName + '\'' +
                ", department='" + department + '\'' +
                ", jobRole='" + jobRole + '\'' +
                ", employmentStatus=" + employmentStatus +
                '}';
    }
}
