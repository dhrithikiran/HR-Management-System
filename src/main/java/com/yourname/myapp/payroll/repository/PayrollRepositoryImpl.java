package com.yourname.myapp.payroll.repository;

import com.yourname.myapp.config.DatabaseConnection;
import com.yourname.myapp.entity.Employee;
import com.yourname.myapp.entity.EmploymentStatus;
import com.yourname.myapp.payroll.entity.Payroll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PayrollRepositoryImpl implements PayrollRepository {
    private static final Logger logger = LoggerFactory.getLogger(PayrollRepositoryImpl.class);

    @Override
    public void save(Payroll payroll) {
        String sql = "INSERT INTO payroll (employee_id, role, gross_salary, deductions, net_pay, current_month_total, salary_transfer_record, month, year) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, payroll.getEmployee().getEmployeeId());
            pstmt.setString(2, payroll.getRole());
            pstmt.setBigDecimal(3, payroll.getGrossSalary());
            pstmt.setBigDecimal(4, payroll.getDeductions());
            pstmt.setBigDecimal(5, payroll.getNetPay());
            pstmt.setBigDecimal(6, payroll.getCurrentMonthTotal());
            pstmt.setString(7, payroll.getSalaryTransferRecord());
            pstmt.setString(8, payroll.getMonth());
            pstmt.setInt(9, payroll.getYear());
            pstmt.executeUpdate();
            logger.info("Saved to DB: {}", payroll.getEmployee().getEmployeeId());
        } catch (SQLException e) {
            logger.error("Error saving payroll", e);
            throw new RuntimeException("Failed to save payroll", e);
        }
    }

    @Override
    public void update(Payroll payroll) {
        String sql = "UPDATE payroll SET role = ?, gross_salary = ?, deductions = ?, net_pay = ?, " +
                     "current_month_total = ?, salary_transfer_record = ? WHERE employee_id = ? AND month = ? AND year = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, payroll.getRole());
            pstmt.setBigDecimal(2, payroll.getGrossSalary());
            pstmt.setBigDecimal(3, payroll.getDeductions());
            pstmt.setBigDecimal(4, payroll.getNetPay());
            pstmt.setBigDecimal(5, payroll.getCurrentMonthTotal());
            pstmt.setString(6, payroll.getSalaryTransferRecord());
            pstmt.setString(7, payroll.getEmployee().getEmployeeId());
            pstmt.setString(8, payroll.getMonth());
            pstmt.setInt(9, payroll.getYear());
            pstmt.executeUpdate();
            logger.info("Updated payroll for: {}", payroll.getEmployee().getEmployeeId());
        } catch (SQLException e) {
            logger.error("Error updating payroll", e);
            throw new RuntimeException("Failed to update payroll", e);
        }
    }

    @Override
    public List<Payroll> findAll() {
        String sql = "SELECT * FROM payroll";
        List<Payroll> payrolls = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                payrolls.add(mapRowToPayroll(rs, conn));
            }
            return payrolls;
        } catch (SQLException e) {
            logger.error("Error fetching all payrolls", e);
            throw new RuntimeException("Failed to fetch payrolls", e);
        }
    }

    @Override
    public Payroll findByEmployeeId(String employeeId) {
        String sql = "SELECT * FROM payroll WHERE employee_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToPayroll(rs, conn);
                }
                return null;
            }
        } catch (SQLException e) {
            logger.error("Error finding payroll by employee: {}", employeeId, e);
            throw new RuntimeException("Failed to find payroll", e);
        }
    }

    @Override
    public Payroll findByEmployeeAndMonth(String employeeId, String month, int year) {
        String sql = "SELECT * FROM payroll WHERE employee_id = ? AND month = ? AND year = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employeeId);
            pstmt.setString(2, month);
            pstmt.setInt(3, year);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToPayroll(rs, conn);
                }
                return null;
            }
        } catch (SQLException e) {
            logger.error("Error finding payroll by employee and month", e);
            throw new RuntimeException("Failed to find payroll", e);
        }
    }

    private Payroll mapRowToPayroll(ResultSet rs, Connection conn) throws SQLException {
        Payroll payroll = new Payroll();
        // payroll_id is auto-generated, no need to set it from ResultSet
        
        String employeeId = rs.getString("employee_id");
        Employee employee = findEmployeeById(employeeId, conn);
        payroll.setEmployee(employee);
        
        payroll.setRole(rs.getString("role"));
        payroll.setGrossSalary(rs.getBigDecimal("gross_salary"));
        payroll.setDeductions(rs.getBigDecimal("deductions"));
        payroll.setNetPay(rs.getBigDecimal("net_pay"));
        payroll.setCurrentMonthTotal(rs.getBigDecimal("current_month_total"));
        payroll.setSalaryTransferRecord(rs.getString("salary_transfer_record"));
        payroll.setMonth(rs.getString("month"));
        payroll.setYear(rs.getInt("year"));
        
        return payroll;
    }

    private Employee findEmployeeById(String employeeId, Connection conn) throws SQLException {
        String sql = "SELECT * FROM employees WHERE employee_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Employee employee = new Employee();
                    employee.setEmployeeId(rs.getString("employee_id"));
                    employee.setEmployeeName(rs.getString("employee_name"));
                    employee.setDepartment(rs.getString("department"));
                    employee.setJobRole(rs.getString("job_role"));
                    String statusStr = rs.getString("employment_status");
                    if (statusStr != null) {
                        employee.setEmploymentStatus(EmploymentStatus.valueOf(statusStr));
                    }
                    return employee;
                }
            }
        }
        return null;
    }

    public static class PayrollId {
        private Long payrollId;

        public PayrollId(Long payrollId) {
            this.payrollId = payrollId;
        }

        public Long getPayrollId() { return payrollId; }
        public void setPayrollId(Long payrollId) { this.payrollId = payrollId; }
    }
}
