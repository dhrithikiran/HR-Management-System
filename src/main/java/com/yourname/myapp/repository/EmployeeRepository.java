package com.yourname.myapp.repository;

import com.yourname.myapp.entity.Employee;
import com.yourname.myapp.entity.EmploymentStatus;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Employee entity - defines data access operations.
 */
public interface EmployeeRepository {
    
    /**
     * Save or update an employee
     */
    Employee save(Employee employee);

    /**
     * Find employee by ID
     */
    Optional<Employee> findById(String employeeId);

    /**
     * Get all employees
     */
    List<Employee> findAll();

    /**
     * Find employees by department
     */
    List<Employee> findByDepartment(String department);

    /**
     * Find employees by employment status
     */
    List<Employee> findByEmploymentStatus(EmploymentStatus status);

    /**
     * Find employees by department and employment status
     */
    List<Employee> findByDepartmentAndEmploymentStatus(String department, EmploymentStatus status);

    /**
     * Find employees by name (partial match)
     */
    List<Employee> findByEmployeeNameContains(String employeeName);

    /**
     * Delete an employee by ID
     */
    boolean deleteById(String employeeId);

    /**
     * Check if employee exists
     */
    boolean existsById(String employeeId);

    /**
     * Get total count of employees
     */
    long count();

    /**
     * Get count of active employees
     */
    long countByEmploymentStatus(EmploymentStatus status);
}
