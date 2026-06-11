package com.yourname.myapp.repository;

import com.yourname.myapp.entity.Employee;
import com.yourname.myapp.entity.EmploymentStatus;
import com.yourname.myapp.config.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of EmployeeRepository using JDBC.
 */
public class EmployeeRepositoryImpl implements EmployeeRepository {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeRepositoryImpl.class);

    @Override
    public Employee save(Employee employee) {
        String sql = "INSERT INTO employees (employee_id, employee_name, department, job_role, employment_status, joining_date, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE employee_name = VALUES(employee_name), department = VALUES(department), " +
                     "job_role = VALUES(job_role), employment_status = VALUES(employment_status), joining_date = VALUES(joining_date), updated_at = VALUES(updated_at)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employee.getEmployeeId());
            pstmt.setString(2, employee.getEmployeeName());
            pstmt.setString(3, employee.getDepartment());
            pstmt.setString(4, employee.getJobRole());
            pstmt.setString(5, employee.getEmploymentStatus().toString());
            pstmt.setDate(6, employee.getJoiningDate() != null ? Date.valueOf(employee.getJoiningDate()) : null);
            pstmt.setDate(7, employee.getCreatedAt() != null ? Date.valueOf(employee.getCreatedAt()) : null);
            pstmt.setDate(8, employee.getUpdatedAt() != null ? Date.valueOf(employee.getUpdatedAt()) : null);
            pstmt.executeUpdate();
            logger.info("Employee saved: {}", employee.getEmployeeId());
            return employee;
        } catch (SQLException e) {
            logger.error("Error saving employee", e);
            throw new RuntimeException("Failed to save employee", e);
        }
    }

    @Override
    public Optional<Employee> findById(String employeeId) {
        String sql = "SELECT * FROM employees WHERE employee_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToEmployee(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error("Error finding employee by ID: {}", employeeId, e);
            throw new RuntimeException("Failed to find employee", e);
        }
    }

    @Override
    public List<Employee> findAll() {
        String sql = "SELECT * FROM employees";
        List<Employee> employees = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                employees.add(mapRowToEmployee(rs));
            }
            return employees;
        } catch (SQLException e) {
            logger.error("Error fetching all employees", e);
            throw new RuntimeException("Failed to fetch employees", e);
        }
    }

    @Override
    public List<Employee> findByDepartment(String department) {
        String sql = "SELECT * FROM employees WHERE department = ?";
        List<Employee> employees = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, department);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    employees.add(mapRowToEmployee(rs));
                }
                return employees;
            }
        } catch (SQLException e) {
            logger.error("Error finding employees by department: {}", department, e);
            throw new RuntimeException("Failed to find employees by department", e);
        }
    }

    @Override
    public List<Employee> findByEmploymentStatus(EmploymentStatus status) {
        String sql = "SELECT * FROM employees WHERE employment_status = ?";
        List<Employee> employees = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    employees.add(mapRowToEmployee(rs));
                }
                return employees;
            }
        } catch (SQLException e) {
            logger.error("Error finding employees by status: {}", status, e);
            throw new RuntimeException("Failed to find employees by status", e);
        }
    }

    @Override
    public List<Employee> findByDepartmentAndEmploymentStatus(String department, EmploymentStatus status) {
        String sql = "SELECT * FROM employees WHERE department = ? AND employment_status = ?";
        List<Employee> employees = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, department);
            pstmt.setString(2, status.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    employees.add(mapRowToEmployee(rs));
                }
                return employees;
            }
        } catch (SQLException e) {
            logger.error("Error finding employees by department and status", e);
            throw new RuntimeException("Failed to find employees by department and status", e);
        }
    }

    @Override
    public List<Employee> findByEmployeeNameContains(String employeeName) {
        String sql = "SELECT * FROM employees WHERE employee_name LIKE ?";
        List<Employee> employees = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + employeeName + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    employees.add(mapRowToEmployee(rs));
                }
                return employees;
            }
        } catch (SQLException e) {
            logger.error("Error searching employees by name: {}", employeeName, e);
            throw new RuntimeException("Failed to search employees", e);
        }
    }

    @Override
    public boolean deleteById(String employeeId) {
        String sql = "DELETE FROM employees WHERE employee_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employeeId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Employee deleted: {}", employeeId);
                return true;
            }
            return false;
        } catch (SQLException e) {
            logger.error("Error deleting employee: {}", employeeId, e);
            throw new RuntimeException("Failed to delete employee", e);
        }
    }

    @Override
    public boolean existsById(String employeeId) {
        String sql = "SELECT 1 FROM employees WHERE employee_id = ? LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Error checking if employee exists: {}", employeeId, e);
            throw new RuntimeException("Failed to check employee existence", e);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM employees";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting employees", e);
            throw new RuntimeException("Failed to count employees", e);
        }
    }

    @Override
    public long countByEmploymentStatus(EmploymentStatus status) {
        String sql = "SELECT COUNT(*) FROM employees WHERE employment_status = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            logger.error("Error counting employees by status: {}", status, e);
            throw new RuntimeException("Failed to count employees by status", e);
        }
    }

    private Employee mapRowToEmployee(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setEmployeeId(rs.getString("employee_id"));
        employee.setEmployeeName(rs.getString("employee_name"));
        employee.setDepartment(rs.getString("department"));
        employee.setJobRole(rs.getString("job_role"));
        String statusStr = rs.getString("employment_status");
        if (statusStr != null) {
            employee.setEmploymentStatus(EmploymentStatus.valueOf(statusStr));
        }
        Date joiningDate = rs.getDate("joining_date");
        if (joiningDate != null) {
            employee.setJoiningDate(joiningDate.toLocalDate());
        }
        Date createdAt = rs.getDate("created_at");
        if (createdAt != null) {
            employee.setCreatedAt(createdAt.toLocalDate());
        }
        Date updatedAt = rs.getDate("updated_at");
        if (updatedAt != null) {
            employee.setUpdatedAt(updatedAt.toLocalDate());
        }
        return employee;
    }
}
