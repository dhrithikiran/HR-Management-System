package com.yourname.myapp.repository;

import com.yourname.myapp.config.DatabaseConnection;
import com.yourname.myapp.entity.LeaveBalance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Optional;

public class LeaveBalanceRepository {
    private static final Logger logger = LoggerFactory.getLogger(LeaveBalanceRepository.class);

    public void save(LeaveBalance balance) {
        String sql = "INSERT INTO leave_balance (employee_id, balance) VALUES (?, ?) " +
                     "ON DUPLICATE KEY UPDATE balance = VALUES(balance)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, balance.getEmployeeId());
            pstmt.setInt(2, balance.getBalance());
            pstmt.executeUpdate();
            logger.info("Leave balance saved for employee: {}", balance.getEmployeeId());
        } catch (SQLException e) {
            logger.error("Error saving leave balance", e);
            throw new RuntimeException("Failed to save leave balance", e);
        }
    }

    public Optional<LeaveBalance> findByEmployeeId(String employeeId) {
        String sql = "SELECT * FROM leave_balance WHERE employee_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToLeaveBalance(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error("Error finding leave balance by employee: {}", employeeId, e);
            throw new RuntimeException("Failed to find leave balance", e);
        }
    }

    private LeaveBalance mapRowToLeaveBalance(ResultSet rs) throws SQLException {
        LeaveBalance balance = new LeaveBalance();
        balance.setId(rs.getLong("id"));
        balance.setEmployeeId(rs.getString("employee_id"));
        balance.setBalance(rs.getInt("balance"));
        return balance;
    }
}