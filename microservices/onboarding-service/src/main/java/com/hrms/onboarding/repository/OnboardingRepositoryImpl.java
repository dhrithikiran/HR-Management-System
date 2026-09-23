package com.hrms.onboarding.repository;

import com.hrms.onboarding.config.DatabaseConfig;
import com.hrms.onboarding.model.BackgroundCheckStatus;
import com.hrms.onboarding.model.DocumentVerificationStatus;
import com.hrms.onboarding.model.OnboardingRecord;
import com.hrms.onboarding.model.PipelineStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OnboardingRepositoryImpl implements OnboardingRepository {
    private static final Logger logger = LoggerFactory.getLogger(OnboardingRepositoryImpl.class);

    @Override
    public List<OnboardingRecord> findAll() {
        String sql = "SELECT onboarding_id, assigned_employee_id, employee_name, background_check_status, " +
                     "document_verification_status, verified_record, pipeline_status FROM onboarding_record ORDER BY onboarding_id ASC";
        List<OnboardingRecord> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error retrieving all onboarding records", e);
            throw new RuntimeException("Database error retrieving onboarding records", e);
        }
    }

    @Override
    public Optional<OnboardingRecord> findById(String onboardingId) {
        String sql = "SELECT onboarding_id, assigned_employee_id, employee_name, background_check_status, " +
                     "document_verification_status, verified_record, pipeline_status FROM onboarding_record WHERE onboarding_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, onboardingId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error("Error finding onboarding record by ID: {}", onboardingId, e);
            throw new RuntimeException("Database error finding onboarding record", e);
        }
    }

    @Override
    public Optional<OnboardingRecord> findByCandidateId(String candidateId) {
        String sql = "SELECT onboarding_id, assigned_employee_id, employee_name, background_check_status, " +
                     "document_verification_status, verified_record, pipeline_status FROM onboarding_record WHERE assigned_employee_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, candidateId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error("Error finding onboarding record by candidate ID: {}", candidateId, e);
            throw new RuntimeException("Database error finding onboarding record by candidate ID", e);
        }
    }

    @Override
    public OnboardingRecord save(OnboardingRecord record) {
        String sql = "INSERT INTO onboarding_record (onboarding_id, assigned_employee_id, employee_name, background_check_status, " +
                     "document_verification_status, verified_record, pipeline_status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, record.getOnboardingId());
            pstmt.setString(2, record.getAssignedEmployeeId());
            pstmt.setString(3, record.getEmployeeName());
            pstmt.setString(4, record.getBackgroundCheckStatus() != null ? record.getBackgroundCheckStatus().name() : BackgroundCheckStatus.PENDING.name());
            pstmt.setString(5, record.getDocumentVerificationStatus() != null ? record.getDocumentVerificationStatus().name() : DocumentVerificationStatus.PENDING.name());
            pstmt.setBoolean(6, record.isVerifiedRecord());
            pstmt.setString(7, record.getPipelineStatus() != null ? record.getPipelineStatus().name() : PipelineStatus.EMPLOYEE_ASSIGNED.name());
            pstmt.executeUpdate();
            logger.info("Saved onboarding record: {} for candidate: {}", record.getOnboardingId(), record.getAssignedEmployeeId());
            return record;
        } catch (SQLException e) {
            logger.error("Error saving onboarding record: {}", record.getOnboardingId(), e);
            throw new RuntimeException("Database error saving onboarding record", e);
        }
    }

    @Override
    public OnboardingRecord update(OnboardingRecord record) {
        String sql = "UPDATE onboarding_record SET assigned_employee_id = ?, employee_name = ?, background_check_status = ?, " +
                     "document_verification_status = ?, verified_record = ?, pipeline_status = ? WHERE onboarding_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, record.getAssignedEmployeeId());
            pstmt.setString(2, record.getEmployeeName());
            pstmt.setString(3, record.getBackgroundCheckStatus().name());
            pstmt.setString(4, record.getDocumentVerificationStatus().name());
            pstmt.setBoolean(5, record.isVerifiedRecord());
            pstmt.setString(6, record.getPipelineStatus().name());
            pstmt.setString(7, record.getOnboardingId());
            pstmt.executeUpdate();
            logger.info("Updated onboarding record: {}", record.getOnboardingId());
            return record;
        } catch (SQLException e) {
            logger.error("Error updating onboarding record: {}", record.getOnboardingId(), e);
            throw new RuntimeException("Database error updating onboarding record", e);
        }
    }

    @Override
    public boolean delete(String onboardingId) {
        String sql = "DELETE FROM onboarding_record WHERE onboarding_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, onboardingId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            logger.error("Error deleting onboarding record: {}", onboardingId, e);
            throw new RuntimeException("Database error deleting onboarding record", e);
        }
    }

    @Override
    public long countAll() {
        String sql = "SELECT COUNT(*) FROM onboarding_record";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting onboarding records", e);
            throw new RuntimeException("Database error counting onboarding records", e);
        }
    }

    private OnboardingRecord mapRow(ResultSet rs) throws SQLException {
        OnboardingRecord record = new OnboardingRecord();
        record.setOnboardingId(rs.getString("onboarding_id"));
        record.setAssignedEmployeeId(rs.getString("assigned_employee_id"));
        record.setEmployeeName(rs.getString("employee_name"));

        String bgStatus = rs.getString("background_check_status");
        if (bgStatus != null) {
            try {
                record.setBackgroundCheckStatus(BackgroundCheckStatus.valueOf(bgStatus));
            } catch (IllegalArgumentException ex) {
                record.setBackgroundCheckStatus(BackgroundCheckStatus.PENDING);
            }
        }

        String docStatus = rs.getString("document_verification_status");
        if (docStatus != null) {
            try {
                record.setDocumentVerificationStatus(DocumentVerificationStatus.valueOf(docStatus));
            } catch (IllegalArgumentException ex) {
                record.setDocumentVerificationStatus(DocumentVerificationStatus.PENDING);
            }
        }

        record.setVerifiedRecord(rs.getBoolean("verified_record"));

        String pipelineStatus = rs.getString("pipeline_status");
        if (pipelineStatus != null) {
            try {
                record.setPipelineStatus(PipelineStatus.valueOf(pipelineStatus));
            } catch (IllegalArgumentException ex) {
                record.setPipelineStatus(PipelineStatus.EMPLOYEE_ASSIGNED);
            }
        }

        return record;
    }
}
