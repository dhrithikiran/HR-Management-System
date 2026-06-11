package com.yourname.myapp.performance.repository;

import com.yourname.myapp.config.DatabaseConnection;
import com.yourname.myapp.performance.entity.Appraisal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppraisalRepositoryImpl implements AppraisalRepository {
    private static final Logger logger = LoggerFactory.getLogger(AppraisalRepositoryImpl.class);

    @Override
    public List<Appraisal> findAll() {
        String sql = "SELECT * FROM appraisal";
        List<Appraisal> appraisals = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                appraisals.add(mapRowToAppraisal(rs));
            }
            return appraisals;
        } catch (SQLException e) {
            logger.error("Error fetching all appraisals", e);
            throw new RuntimeException("Failed to fetch appraisals", e);
        }
    }

    @Override
    public Appraisal findById(String id) {
        String sql = "SELECT * FROM appraisal WHERE appraise_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToAppraisal(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            logger.error("Error finding appraisal by ID: {}", id, e);
            throw new RuntimeException("Failed to find appraisal", e);
        }
    }

    @Override
    public void save(Appraisal appraisal) {
        String sql = "INSERT INTO appraisal (appraise_id, employee_id, rating, feedback, appraisal_status, deadline_date, locked) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, appraisal.getAppraiseId());
            pstmt.setString(2, appraisal.getEmployeeId());
            pstmt.setDouble(3, appraisal.getRating());
            pstmt.setString(4, appraisal.getFeedback());
            pstmt.setString(5, appraisal.getAppraisalStatus().toString());
            pstmt.setDate(6, appraisal.getDeadlineDate() != null ? Date.valueOf(appraisal.getDeadlineDate()) : null);
            pstmt.setBoolean(7, appraisal.isLocked());
            pstmt.executeUpdate();
            logger.info("Appraisal saved: {}", appraisal.getAppraiseId());
        } catch (SQLException e) {
            logger.error("Error saving appraisal", e);
            throw new RuntimeException("Failed to save appraisal", e);
        }
    }

    @Override
    public void update(Appraisal appraisal) {
        String sql = "UPDATE appraisal SET employee_id = ?, rating = ?, feedback = ?, appraisal_status = ?, deadline_date = ?, locked = ? " +
                     "WHERE appraise_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, appraisal.getEmployeeId());
            pstmt.setDouble(2, appraisal.getRating());
            pstmt.setString(3, appraisal.getFeedback());
            pstmt.setString(4, appraisal.getAppraisalStatus().toString());
            pstmt.setDate(5, appraisal.getDeadlineDate() != null ? Date.valueOf(appraisal.getDeadlineDate()) : null);
            pstmt.setBoolean(6, appraisal.isLocked());
            pstmt.setString(7, appraisal.getAppraiseId());
            pstmt.executeUpdate();
            logger.info("Appraisal updated: {}", appraisal.getAppraiseId());
        } catch (SQLException e) {
            logger.error("Error updating appraisal", e);
            throw new RuntimeException("Failed to update appraisal", e);
        }
    }

    @Override
    public int deleteByEmployeeId(String employeeId) {
        String sql = "DELETE FROM appraisal WHERE employee_id = ? OR UPPER(TRIM(employee_id)) = UPPER(TRIM(?))";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employeeId);
            pstmt.setString(2, employeeId != null ? employeeId : "");
            int deletedCount = pstmt.executeUpdate();
            logger.info("Deleted {} appraisals for employee: {}", deletedCount, employeeId);
            return deletedCount;
        } catch (SQLException e) {
            logger.error("Error deleting appraisals by employee: {}", employeeId, e);
            throw new RuntimeException("Failed to delete appraisals by employee", e);
        }
    }

    @Override
    public int deleteOrphanedAppraisals() {
        String sql = "DELETE FROM appraisal WHERE employee_id NOT IN (SELECT employee_id FROM employees)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int deletedCount = pstmt.executeUpdate();
            logger.info("Deleted {} orphaned appraisals", deletedCount);
            return deletedCount;
        } catch (SQLException e) {
            logger.error("Error deleting orphaned appraisals", e);
            throw new RuntimeException("Failed to delete orphaned appraisals", e);
        }
    }

    @Override
    public long countByStatus(Appraisal.AppraisalStatus status) {
        String sql = "SELECT COUNT(*) FROM appraisal WHERE appraisal_status = ?";
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
            logger.error("Error counting appraisals by status: {}", status, e);
            throw new RuntimeException("Failed to count appraisals by status", e);
        }
    }

    @Override
    public long countAll() {
        String sql = "SELECT COUNT(*) FROM appraisal";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting all appraisals", e);
            throw new RuntimeException("Failed to count appraisals", e);
        }
    }

    private Appraisal mapRowToAppraisal(ResultSet rs) throws SQLException {
        Appraisal appraisal = new Appraisal();
        appraisal.setAppraiseId(rs.getString("appraise_id"));
        appraisal.setEmployeeId(rs.getString("employee_id"));
        appraisal.setRating(rs.getDouble("rating"));
        appraisal.setFeedback(rs.getString("feedback"));
        String statusStr = rs.getString("appraisal_status");
        if (statusStr != null) {
            appraisal.setAppraisalStatus(Appraisal.AppraisalStatus.valueOf(statusStr));
        }
        Date deadlineDate = rs.getDate("deadline_date");
        if (deadlineDate != null) {
            appraisal.setDeadlineDate(deadlineDate.toLocalDate());
        }
        appraisal.setLocked(rs.getBoolean("locked"));
        return appraisal;
    }
}
