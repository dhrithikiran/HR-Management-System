package com.yourname.myapp.repository;

import com.yourname.myapp.config.DatabaseConnection;
import com.yourname.myapp.entity.BenefitEnrollment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BenefitEnrollmentRepository {
    private static final Logger logger = LoggerFactory.getLogger(BenefitEnrollmentRepository.class);

    public void save(BenefitEnrollment enrollment) {
        String sql = "INSERT INTO benefit_enrollment (employee_id, enrollment_status, health_plan, insurance_plan, insurance_coverage_status) " +
                     "VALUES (?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE enrollment_status = VALUES(enrollment_status), health_plan = VALUES(health_plan), " +
                     "insurance_plan = VALUES(insurance_plan), insurance_coverage_status = VALUES(insurance_coverage_status)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, enrollment.getEmployeeId());
            pstmt.setString(2, enrollment.getEnrollmentStatus().toString());
            pstmt.setString(3, enrollment.getHealthPlan());
            pstmt.setString(4, enrollment.getInsurancePlan());
            pstmt.setString(5, enrollment.getInsuranceCoverageStatus().toString());
            pstmt.executeUpdate();
            logger.info("Benefit enrollment saved: {}", enrollment.getId());
        } catch (SQLException e) {
            logger.error("Error saving benefit enrollment", e);
            throw new RuntimeException("Failed to save benefit enrollment", e);
        }
    }

    public List<BenefitEnrollment> findAll() {
        String sql = "SELECT * FROM benefit_enrollment";
        List<BenefitEnrollment> enrollments = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                enrollments.add(mapRowToBenefitEnrollment(rs));
            }
            return enrollments;
        } catch (SQLException e) {
            logger.error("Error fetching all benefit enrollments", e);
            throw new RuntimeException("Failed to fetch benefit enrollments", e);
        }
    }

    public List<BenefitEnrollment> findByEmployeeId(String employeeId) {
        String sql = "SELECT * FROM benefit_enrollment WHERE employee_id = ?";
        List<BenefitEnrollment> enrollments = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    enrollments.add(mapRowToBenefitEnrollment(rs));
                }
                return enrollments;
            }
        } catch (SQLException e) {
            logger.error("Error finding benefit enrollments by employee: {}", employeeId, e);
            throw new RuntimeException("Failed to find benefit enrollments", e);
        }
    }

    public Optional<BenefitEnrollment> findById(Long id) {
        String sql = "SELECT * FROM benefit_enrollment WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToBenefitEnrollment(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error("Error finding benefit enrollment by ID: {}", id, e);
            throw new RuntimeException("Failed to find benefit enrollment", e);
        }
    }

    private BenefitEnrollment mapRowToBenefitEnrollment(ResultSet rs) throws SQLException {
        BenefitEnrollment enrollment = new BenefitEnrollment();
        enrollment.setId(rs.getLong("id"));
        enrollment.setEmployeeId(rs.getString("employee_id"));
        String enrollmentStatus = rs.getString("enrollment_status");
        if (enrollmentStatus != null) {
            enrollment.setEnrollmentStatus(BenefitEnrollment.EnrollmentStatus.valueOf(enrollmentStatus));
        }
        enrollment.setHealthPlan(rs.getString("health_plan"));
        enrollment.setInsurancePlan(rs.getString("insurance_plan"));
        String coverageStatus = rs.getString("insurance_coverage_status");
        if (coverageStatus != null) {
            enrollment.setInsuranceCoverageStatus(BenefitEnrollment.CoverageStatus.valueOf(coverageStatus));
        }
        return enrollment;
    }
}
