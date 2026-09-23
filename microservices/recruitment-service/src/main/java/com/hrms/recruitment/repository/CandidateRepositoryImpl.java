package com.hrms.recruitment.repository;

import com.hrms.recruitment.config.DatabaseConfig;
import com.hrms.recruitment.model.ApplicationStatus;
import com.hrms.recruitment.model.Candidate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CandidateRepositoryImpl implements CandidateRepository {
    private static final Logger logger = LoggerFactory.getLogger(CandidateRepositoryImpl.class);

    @Override
    public List<Candidate> findAll() {
        String sql = "SELECT candidate_id, candidate_name, contact_info, resume_data, interview_score, application_status FROM candidate ORDER BY candidate_id ASC";
        List<Candidate> candidates = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                candidates.add(mapRow(rs));
            }
            return candidates;
        } catch (SQLException e) {
            logger.error("Error retrieving all candidates", e);
            throw new RuntimeException("Database error retrieving candidates", e);
        }
    }

    @Override
    public List<Candidate> findAllByStatus(String status) {
        String sql = "SELECT candidate_id, candidate_name, contact_info, resume_data, interview_score, application_status FROM candidate WHERE application_status = ? ORDER BY candidate_id ASC";
        List<Candidate> candidates = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    candidates.add(mapRow(rs));
                }
            }
            return candidates;
        } catch (SQLException e) {
            logger.error("Error retrieving candidates by status: {}", status, e);
            throw new RuntimeException("Database error retrieving candidates by status", e);
        }
    }

    @Override
    public Optional<Candidate> findById(String id) {
        String sql = "SELECT candidate_id, candidate_name, contact_info, resume_data, interview_score, application_status FROM candidate WHERE candidate_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error("Error finding candidate by ID: {}", id, e);
            throw new RuntimeException("Database error finding candidate", e);
        }
    }

    @Override
    public Candidate save(Candidate candidate) {
        String sql = "INSERT INTO candidate (candidate_id, candidate_name, contact_info, resume_data, interview_score, application_status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, candidate.getCandidateId());
            pstmt.setString(2, candidate.getCandidateName());
            pstmt.setString(3, candidate.getContactInfo());
            pstmt.setString(4, candidate.getResumeData() != null ? candidate.getResumeData() : "");
            if (candidate.getInterviewScore() != null) {
                pstmt.setDouble(5, candidate.getInterviewScore());
            } else {
                pstmt.setNull(5, Types.DOUBLE);
            }
            pstmt.setString(6, candidate.getApplicationStatus() != null ? candidate.getApplicationStatus().name() : ApplicationStatus.APPLIED.name());
            pstmt.executeUpdate();
            logger.info("Saved candidate: {}", candidate.getCandidateId());
            return candidate;
        } catch (SQLException e) {
            logger.error("Error saving candidate: {}", candidate.getCandidateId(), e);
            throw new RuntimeException("Database error saving candidate", e);
        }
    }

    @Override
    public Candidate update(Candidate candidate) {
        String sql = "UPDATE candidate SET candidate_name = ?, contact_info = ?, resume_data = ?, interview_score = ?, application_status = ? WHERE candidate_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, candidate.getCandidateName());
            pstmt.setString(2, candidate.getContactInfo());
            pstmt.setString(3, candidate.getResumeData() != null ? candidate.getResumeData() : "");
            if (candidate.getInterviewScore() != null) {
                pstmt.setDouble(4, candidate.getInterviewScore());
            } else {
                pstmt.setNull(4, Types.DOUBLE);
            }
            pstmt.setString(5, candidate.getApplicationStatus().name());
            pstmt.setString(6, candidate.getCandidateId());
            pstmt.executeUpdate();
            logger.info("Updated candidate: {}", candidate.getCandidateId());
            return candidate;
        } catch (SQLException e) {
            logger.error("Error updating candidate: {}", candidate.getCandidateId(), e);
            throw new RuntimeException("Database error updating candidate", e);
        }
    }

    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM candidate WHERE candidate_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            logger.error("Error deleting candidate: {}", id, e);
            throw new RuntimeException("Database error deleting candidate", e);
        }
    }

    @Override
    public long countAll() {
        String sql = "SELECT COUNT(*) FROM candidate";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting candidates", e);
            throw new RuntimeException("Database error counting candidates", e);
        }
    }

    private Candidate mapRow(ResultSet rs) throws SQLException {
        Candidate candidate = new Candidate();
        candidate.setCandidateId(rs.getString("candidate_id"));
        candidate.setCandidateName(rs.getString("candidate_name"));
        candidate.setContactInfo(rs.getString("contact_info"));
        candidate.setResumeData(rs.getString("resume_data"));
        double score = rs.getDouble("interview_score");
        if (!rs.wasNull()) {
            candidate.setInterviewScore(score);
        }
        String status = rs.getString("application_status");
        if (status != null) {
            try {
                candidate.setApplicationStatus(ApplicationStatus.valueOf(status));
            } catch (IllegalArgumentException ex) {
                candidate.setApplicationStatus(ApplicationStatus.APPLIED);
            }
        }
        return candidate;
    }
}
