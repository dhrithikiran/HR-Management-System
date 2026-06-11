package com.yourname.myapp.service;

import com.yourname.myapp.builder.EmployeeBuilder;
import com.yourname.myapp.dto.DashboardStats;
import com.yourname.myapp.dto.EmployeeRequest;
import com.yourname.myapp.entity.Employee;
import com.yourname.myapp.entity.EmploymentStatus;
import com.yourname.myapp.exception.DuplicateEmployeeIdException;
import com.yourname.myapp.exception.EmployeeNotFoundException;
import com.yourname.myapp.performance.repository.AppraisalRepository;
import com.yourname.myapp.performance.repository.AppraisalRepositoryImpl;
import com.yourname.myapp.performance.repository.PromotionRepository;
import com.yourname.myapp.performance.repository.PromotionRepositoryImpl;
import com.yourname.myapp.repository.EmployeeRepository;
import com.yourname.myapp.repository.EmployeeRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

/**
 * Service class for Employee business logic.
 * Handles employee CRUD operations and dashboard statistics.
 * Uses Builder pattern for object construction.
 */
public class EmployeeService {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);
    private final EmployeeRepository employeeRepository;
    private final AppraisalRepository appraisalRepository;
    private final PromotionRepository promotionRepository;

    public EmployeeService() {
        this.employeeRepository = new EmployeeRepositoryImpl();
        this.appraisalRepository = new AppraisalRepositoryImpl();
        this.promotionRepository = new PromotionRepositoryImpl();
    }

    /**
     * Get all employees with optional filtering by department and status
     */
    public List<Employee> getAllEmployees(String department, String status) {
        try {
            if (department != null && !department.isEmpty() && status != null && !status.isEmpty()) {
                EmploymentStatus employmentStatus = EmploymentStatus.valueOf(status);
                return employeeRepository.findByDepartmentAndEmploymentStatus(department, employmentStatus);
            } else if (department != null && !department.isEmpty()) {
                return employeeRepository.findByDepartment(department);
            } else if (status != null && !status.isEmpty()) {
                EmploymentStatus employmentStatus = EmploymentStatus.valueOf(status);
                return employeeRepository.findByEmploymentStatus(employmentStatus);
            } else {
                return employeeRepository.findAll();
            }
        } catch (IllegalArgumentException e) {
            logger.error("Invalid employment status: {}", status);
            throw new IllegalArgumentException("Invalid employment status", e);
        } catch (Exception e) {
            logger.error("Error retrieving employees", e);
            throw new RuntimeException("Failed to retrieve employees", e);
        }
    }

    /**
     * Get all employees without filters
     */
    public List<Employee> getAllEmployees() {
        return getAllEmployees(null, null);
    }

    /**
     * Get employee by ID
     */
    public Employee getEmployeeById(String id) {
        try {
            Optional<Employee> employee = employeeRepository.findById(id);
            if (employee.isEmpty()) {
                throw new EmployeeNotFoundException("Employee not found with ID: " + id);
            }
            return employee.get();
        } catch (EmployeeNotFoundException e) {
            logger.warn("Employee not found: {}", id);
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving employee by ID: {}", id, e);
            throw new RuntimeException("Failed to retrieve employee", e);
        }
    }

    /**
     * Create a new employee using the Builder pattern
     */
    public Employee createEmployee(EmployeeRequest request) {
        try {
            // Validate request
            if (request == null) {
                throw new IllegalArgumentException("Employee request cannot be null");
            }

            // Use Builder pattern to create employee
            Employee employee = new EmployeeBuilder()
                    .withEmployeeName(request.getEmployeeName())
                    .withDepartment(request.getDepartment())
                    .withJobRole(request.getJobRole())
                    .withEmploymentStatus(request.getEmploymentStatus() != null ? 
                            request.getEmploymentStatus() : EmploymentStatus.ACTIVE)
                    .build();

            // Check for duplicate
            if (employeeRepository.existsById(employee.getEmployeeId())) {
                throw new DuplicateEmployeeIdException(
                        "Employee with ID " + employee.getEmployeeId() + " already exists"
                );
            }

            Employee savedEmployee = employeeRepository.save(employee);
            logger.info("Employee created successfully: {}", savedEmployee.getEmployeeId());
            return savedEmployee;
        } catch (DuplicateEmployeeIdException | IllegalArgumentException e) {
            logger.warn("Validation error while creating employee", e);
            throw e;
        } catch (Exception e) {
            logger.error("Error creating employee", e);
            throw new RuntimeException("Failed to create employee", e);
        }
    }

    /**
     * Update an existing employee
     */
    public Employee updateEmployee(String id, EmployeeRequest request) {
        try {
            // Get existing employee
            Employee employee = getEmployeeById(id);

            // Update fields
            if (request.getEmployeeName() != null && !request.getEmployeeName().isEmpty()) {
                employee.setEmployeeName(request.getEmployeeName());
            }
            if (request.getDepartment() != null && !request.getDepartment().isEmpty()) {
                employee.setDepartment(request.getDepartment());
            }
            if (request.getJobRole() != null && !request.getJobRole().isEmpty()) {
                employee.setJobRole(request.getJobRole());
            }
            if (request.getEmploymentStatus() != null) {
                employee.setEmploymentStatus(request.getEmploymentStatus());
            }

            Employee updatedEmployee = employeeRepository.save(employee);
            logger.info("Employee updated successfully: {}", id);
            return updatedEmployee;
        } catch (EmployeeNotFoundException e) {
            logger.warn("Cannot update - employee not found: {}", id);
            throw e;
        } catch (Exception e) {
            logger.error("Error updating employee: {}", id, e);
            throw new RuntimeException("Failed to update employee", e);
        }
    }

    /**
     * Delete an employee by ID
     */
    public void deleteEmployee(String id) {
        try {
            String normalizedId = id == null ? null : id.trim();
            if (normalizedId == null || normalizedId.isEmpty()) {
                throw new IllegalArgumentException("Employee ID is required");
            }

            // Verify employee exists before deletion
            getEmployeeById(normalizedId);

            int deletedAppraisals = appraisalRepository.deleteByEmployeeId(normalizedId);
            int deletedPromotions = promotionRepository.deleteByEmployeeId(normalizedId);

            boolean deleted = employeeRepository.deleteById(normalizedId);
            if (deleted) {
                logger.info("Employee deleted successfully: {} (appraisals removed: {}, promotions removed: {})", normalizedId, deletedAppraisals, deletedPromotions);
            } else {
                throw new EmployeeNotFoundException("Failed to delete employee with ID: " + normalizedId);
            }
        } catch (EmployeeNotFoundException e) {
            logger.warn("Cannot delete - employee not found: {}", id);
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting employee: {}", id, e);
            throw new RuntimeException("Failed to delete employee", e);
        }
    }

    /**
     * Get dashboard statistics for the admin panel
     */
    public DashboardStats getDashboardStats() {
        try {
            DashboardStats stats = new DashboardStats();

            try {
                // Total employee count
                stats.setTotalEmployeeCount(employeeRepository.count());
            } catch (Exception e) {
                logger.warn("Error getting total employee count: {}", e.getMessage());
                stats.setTotalEmployeeCount(0);
            }

            try {
                // Active employees count
                stats.setActiveEmployeeCount(employeeRepository.countByEmploymentStatus(EmploymentStatus.ACTIVE));
            } catch (Exception e) {
                logger.warn("Error getting active employee count: {}", e.getMessage());
                stats.setActiveEmployeeCount(0);
            }

            try {
                // On leave count
                stats.setOnLeaveCount(employeeRepository.countByEmploymentStatus(EmploymentStatus.ON_LEAVE));
            } catch (Exception e) {
                logger.warn("Error getting on-leave count: {}", e.getMessage());
                stats.setOnLeaveCount(0);
            }

            try {
                // New joiners this month
                long newJoinersCount = getNewJoinersThisMonth();
                stats.setNewJoinersCount(newJoinersCount);
            } catch (Exception e) {
                logger.warn("Error getting new joiners count: {}", e.getMessage());
                stats.setNewJoinersCount(0);
            }

            logger.info("Dashboard stats retrieved: {}", stats);
            return stats;
        } catch (Exception e) {
            logger.error("Error retrieving dashboard statistics", e);
            // Return empty stats instead of throwing exception
            return new DashboardStats(0, 0, 0, 0);
        }
    }

    /**
     * Get count of employees who joined this month
     */
    private long getNewJoinersThisMonth() {
        try {
            List<Employee> allEmployees = employeeRepository.findAll();
            LocalDate now = LocalDate.now();
            YearMonth currentMonth = YearMonth.from(now);

            return allEmployees.stream()
                    .filter(emp -> emp.getJoiningDate() != null)
                    .filter(emp -> YearMonth.from(emp.getJoiningDate()).equals(currentMonth))
                    .count();
        } catch (Exception e) {
            logger.error("Error calculating new joiners count", e);
            return 0;
        }
    }

    /**
     * Search employees by name
     */
    public List<Employee> searchByName(String name) {
        try {
            if (name == null || name.trim().isEmpty()) {
                return getAllEmployees();
            }
            return employeeRepository.findByEmployeeNameContains(name);
        } catch (Exception e) {
            logger.error("Error searching employees by name: {}", name, e);
            throw new RuntimeException("Failed to search employees", e);
        }
    }

    /**
     * Get all departments (unique department values)
     */
    public List<String> getAllDepartments() {
        try {
            List<Employee> employees = employeeRepository.findAll();
            return employees.stream()
                    .map(Employee::getDepartment)
                    .distinct()
                    .sorted()
                    .toList();
        } catch (Exception e) {
            logger.error("Error retrieving departments", e);
            throw new RuntimeException("Failed to retrieve departments", e);
        }
    }
}
