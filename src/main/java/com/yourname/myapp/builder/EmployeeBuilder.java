package com.yourname.myapp.builder;

import com.yourname.myapp.entity.Employee;
import com.yourname.myapp.entity.EmploymentStatus;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Builder class for constructing Employee objects with fluent API.
 * Generates auto-incremented employee IDs in format EMP-XXXXXXXX.
 */
public class EmployeeBuilder {
    private String employeeId;
    private String employeeName;
    private String department;
    private String jobRole;
    private EmploymentStatus employmentStatus;
    private LocalDate joiningDate;

    /**
     * Constructor initializes employeeId with auto-generated value
     */
    public EmployeeBuilder() {
        this.employeeId = generateEmployeeId();
        this.employmentStatus = EmploymentStatus.ACTIVE;
        this.joiningDate = LocalDate.now();
    }

    /**
     * Generate unique employee ID with format EMP-XXXXXXXX
     */
    private String generateEmployeeId() {
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "EMP-" + uuid;
    }

    /**
     * Set employee name
     */
    public EmployeeBuilder withEmployeeName(String employeeName) {
        this.employeeName = employeeName;
        return this;
    }

    /**
     * Set department
     */
    public EmployeeBuilder withDepartment(String department) {
        this.department = department;
        return this;
    }

    /**
     * Set job role
     */
    public EmployeeBuilder withJobRole(String jobRole) {
        this.jobRole = jobRole;
        return this;
    }

    /**
     * Set employment status
     */
    public EmployeeBuilder withEmploymentStatus(EmploymentStatus employmentStatus) {
        this.employmentStatus = employmentStatus;
        return this;
    }

    /**
     * Set joining date (optional)
     */
    public EmployeeBuilder withJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
        return this;
    }

    /**
     * Set employee ID (useful for updates)
     */
    public EmployeeBuilder withEmployeeId(String employeeId) {
        this.employeeId = employeeId;
        return this;
    }

    /**
     * Build and return the Employee object
     */
    public Employee build() {
        validateRequiredFields();
        Employee employee = new Employee();
        employee.setEmployeeId(employeeId);
        employee.setEmployeeName(employeeName);
        employee.setDepartment(department);
        employee.setJobRole(jobRole);
        employee.setEmploymentStatus(employmentStatus);
        employee.setJoiningDate(joiningDate);
        return employee;
    }

    /**
     * Validate that all required fields are set
     */
    private void validateRequiredFields() {
        if (employeeName == null || employeeName.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee name is required");
        }
        if (department == null || department.trim().isEmpty()) {
            throw new IllegalArgumentException("Department is required");
        }
        if (jobRole == null || jobRole.trim().isEmpty()) {
            throw new IllegalArgumentException("Job role is required");
        }
    }

    @Override
    public String toString() {
        return "EmployeeBuilder{" +
                "employeeId='" + employeeId + '\'' +
                ", employeeName='" + employeeName + '\'' +
                ", department='" + department + '\'' +
                ", jobRole='" + jobRole + '\'' +
                ", employmentStatus=" + employmentStatus +
                ", joiningDate=" + joiningDate +
                '}';
    }
}
