package com.yourname.myapp.onboarding.repository;

import com.yourname.myapp.config.DatabaseConnection;
import com.yourname.myapp.onboarding.entity.OnboardingRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OnboardingRepositoryImpl implements OnboardingRepository {
    private static final Logger logger = LoggerFactory.getLogger(OnboardingRepositoryImpl.class);

    @Override
    public List<OnboardingRecord> findAll() {
        String sql = "SELECT * FROM onboarding_record";
        List<OnboardingRecord> records = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                records.add(mapRowToOnboardingRecord(rs));
            }
            return records;
        } catch (SQLException e) {
            logger.error("Error fetching all onboarding records", e);
            throw new RuntimeException("Failed to fetch onboarding records", e);
        }
    }

    @Override
    public OnboardingRecord findById(String id) {
        String sql = "SELECT * FROM onboarding_record WHERE onboarding_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToOnboardingRecord(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            logger.error("Error finding onboarding record by ID: {}", id, e);
            throw new RuntimeException("Failed to find onboarding record", e);
        }
    }

    @Override
    public void save(OnboardingRecord record) {
        String sql = "INSERT INTO onboarding_record (onboarding_id, assigned_employee_id, employee_name, background_check_status, " +
                     "document_verification_status, verified_record, pipeline_status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, record.getOnboardingId());
            pstmt.setString(2, record.getAssignedEmployeeId());
            pstmt.setString(3, record.getEmployeeName());
            pstmt.setString(4, record.getBackgroundCheckStatus().toString());
            pstmt.setString(5, record.getDocumentVerificationStatus().toString());
            pstmt.setBoolean(6, record.isVerifiedRecord());
            pstmt.setString(7, record.getPipelineStatus().toString());
            pstmt.executeUpdate();
            logger.info("Onboarding record saved: {}", record.getOnboardingId());
        } catch (SQLException e) {
            logger.error("Error saving onboarding record", e);
            throw new RuntimeException("Failed to save onboarding record", e);
        }
    }

    @Override
    public void update(OnboardingRecord record) {
        String sql = "UPDATE onboarding_record SET assigned_employee_id = ?, employee_name = ?, background_check_status = ?, " +
                     "document_verification_status = ?, verified_record = ?, pipeline_status = ? WHERE onboarding_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, record.getAssignedEmployeeId());
            pstmt.setString(2, record.getEmployeeName());
            pstmt.setString(3, record.getBackgroundCheckStatus().toString());
            pstmt.setString(4, record.getDocumentVerificationStatus().toString());
            pstmt.setBoolean(5, record.isVerifiedRecord());
            pstmt.setString(6, record.getPipelineStatus().toString());
            pstmt.setString(7, record.getOnboardingId());
            pstmt.executeUpdate();
            logger.info("Onboarding record updated: {}", record.getOnboardingId());
        } catch (SQLException e) {
            logger.error("Error updating onboarding record", e);
            throw new RuntimeException("Failed to update onboarding record", e);
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM onboarding_record WHERE onboarding_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
            logger.info("Onboarding record deleted: {}", id);
        } catch (SQLException e) {
            logger.error("Error deleting onboarding record: {}", id, e);
            throw new RuntimeException("Failed to delete onboarding record", e);
        }
    }

    @Override
    public long countAll() {
        String sql = "SELECT COUNT(*) FROM onboarding_record";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting all onboarding records", e);
            throw new RuntimeException("Failed to count onboarding records", e);
        }
    }

    @Override
    public long countByPipelineStatus(String status) {
        String sql = "SELECT COUNT(*) FROM onboarding_record WHERE pipeline_status = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, OnboardingRecord.PipelineStatus.valueOf(status).toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            logger.error("Error counting onboarding records by pipeline status: {}", status, e);
            throw new RuntimeException("Failed to count onboarding records by pipeline status", e);
        }
    }

    private OnboardingRecord mapRowToOnboardingRecord(ResultSet rs) throws SQLException {
        OnboardingRecord record = new OnboardingRecord();
        record.setOnboardingId(rs.getString("onboarding_id"));
        record.setAssignedEmployeeId(rs.getString("assigned_employee_id"));
        record.setEmployeeName(rs.getString("employee_name"));
        String backgroundCheckStatus = rs.getString("background_check_status");
        if (backgroundCheckStatus != null) {
            record.setBackgroundCheckStatus(OnboardingRecord.BackgroundCheckStatus.valueOf(backgroundCheckStatus));
        }
        String documentVerificationStatus = rs.getString("document_verification_status");
        if (documentVerificationStatus != null) {
            record.setDocumentVerificationStatus(OnboardingRecord.DocumentVerificationStatus.valueOf(documentVerificationStatus));
        }
        record.setVerifiedRecord(rs.getBoolean("verified_record"));
        String pipelineStatus = rs.getString("pipeline_status");
        if (pipelineStatus != null) {
            record.setPipelineStatus(OnboardingRecord.PipelineStatus.valueOf(pipelineStatus));
        }
        return record;
    }
}