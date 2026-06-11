package com.yourname.myapp.repository;

import com.yourname.myapp.config.DatabaseConnection;
import com.yourname.myapp.entity.LeaveRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LeaveRequestRepository {
    private static final Logger logger = LoggerFactory.getLogger(LeaveRequestRepository.class);

    public void save(LeaveRequest request) {
        String sql = "INSERT INTO leave_request (employee_id, leave_from_date, leave_to_date, leave_status) " +
                     "VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE leave_from_date = VALUES(leave_from_date), leave_to_date = VALUES(leave_to_date), leave_status = VALUES(leave_status)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, request.getEmployeeId());
            pstmt.setDate(2, Date.valueOf(request.getLeaveFromDate()));
            pstmt.setDate(3, Date.valueOf(request.getLeaveToDate()));
            pstmt.setString(4, request.getLeaveStatus().toString());
            pstmt.executeUpdate();
            logger.info("Leave request saved: {}", request.getId());
        } catch (SQLException e) {
            logger.error("Error saving leave request", e);
            throw new RuntimeException("Failed to save leave request", e);
        }
    }

    public List<LeaveRequest> findAll() {
        String sql = "SELECT * FROM leave_request";
        List<LeaveRequest> requests = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                requests.add(mapRowToLeaveRequest(rs));
            }
            return requests;
        } catch (SQLException e) {
            logger.error("Error fetching all leave requests", e);
            throw new RuntimeException("Failed to fetch leave requests", e);
        }
    }

    public List<LeaveRequest> findByStatus(String status) {
        String sql = "SELECT * FROM leave_request WHERE leave_status = ?";
        List<LeaveRequest> requests = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, LeaveRequest.LeaveStatus.valueOf(status).toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapRowToLeaveRequest(rs));
                }
                return requests;
            }
        } catch (SQLException e) {
            logger.error("Error finding leave requests by status: {}", status, e);
            throw new RuntimeException("Failed to find leave requests by status", e);
        }
    }

    public List<LeaveRequest> findByEmployeeId(String employeeId) {
        String sql = "SELECT * FROM leave_request WHERE employee_id = ?";
        List<LeaveRequest> requests = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapRowToLeaveRequest(rs));
                }
                return requests;
            }
        } catch (SQLException e) {
            logger.error("Error finding leave requests by employee: {}", employeeId, e);
            throw new RuntimeException("Failed to find leave requests by employee", e);
        }
    }

    public Optional<LeaveRequest> findById(Long id) {
        String sql = "SELECT * FROM leave_request WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToLeaveRequest(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error("Error finding leave request by ID: {}", id, e);
            throw new RuntimeException("Failed to find leave request", e);
        }
    }

    public long countPending() {
        String sql = "SELECT COUNT(*) FROM leave_request WHERE leave_status = 'PENDING'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting pending leave requests", e);
            throw new RuntimeException("Failed to count pending leave requests", e);
        }
    }

    private LeaveRequest mapRowToLeaveRequest(ResultSet rs) throws SQLException {
        LeaveRequest request = new LeaveRequest();
        request.setId(rs.getLong("id"));
        request.setEmployeeId(rs.getString("employee_id"));
        Date fromDate = rs.getDate("leave_from_date");
        if (fromDate != null) {
            request.setLeaveFromDate(fromDate.toLocalDate());
        }
        Date toDate = rs.getDate("leave_to_date");
        if (toDate != null) {
            request.setLeaveToDate(toDate.toLocalDate());
        }
        String statusStr = rs.getString("leave_status");
        if (statusStr != null) {
            request.setLeaveStatus(LeaveRequest.LeaveStatus.valueOf(statusStr));
        }
        return request;
    }
}