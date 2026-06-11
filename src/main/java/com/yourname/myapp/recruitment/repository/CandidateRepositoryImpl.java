package com.yourname.myapp.recruitment.repository;

import com.yourname.myapp.config.DatabaseConnection;
import com.yourname.myapp.recruitment.entity.Candidate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CandidateRepositoryImpl implements CandidateRepository {
    private static final Logger logger = LoggerFactory.getLogger(CandidateRepositoryImpl.class);

    @Override
    public List<Candidate> findAllByStatus(String status) {
        List<Candidate> candidates = new ArrayList<>();
        try {
            if (status == null || status.isBlank() || status.equalsIgnoreCase("ALL")) {
                String sql = "SELECT * FROM candidate";
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql);
                     ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        candidates.add(mapRowToCandidate(rs));
                    }
                }
            } else {
                String sql = "SELECT * FROM candidate WHERE application_status = ?";
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, Candidate.ApplicationStatus.valueOf(status).toString());
                    try (ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            candidates.add(mapRowToCandidate(rs));
                        }
                    }
                }
            }
            return candidates;
        } catch (SQLException e) {
            logger.error("Error finding candidates by status: {}", status, e);
            throw new RuntimeException("Failed to find candidates by status", e);
        }
    }

    @Override
    public Candidate findById(String id) {
        String sql = "SELECT * FROM candidate WHERE candidate_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToCandidate(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            logger.error("Error finding candidate by ID: {}", id, e);
            throw new RuntimeException("Failed to find candidate", e);
        }
    }

    @Override
    public void save(Candidate candidate) {
        String sql = "INSERT INTO candidate (candidate_id, candidate_name, contact_info, resume_data, interview_score, application_status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, candidate.getCandidateId());
            pstmt.setString(2, candidate.getCandidateName());
            pstmt.setString(3, candidate.getContactInfo());
            pstmt.setString(4, candidate.getResumeData());
            pstmt.setDouble(5, candidate.getInterviewScore());
            pstmt.setString(6, candidate.getApplicationStatus().toString());
            pstmt.executeUpdate();
            logger.info("Candidate saved: {}", candidate.getCandidateId());
        } catch (SQLException e) {
            logger.error("Error saving candidate", e);
            throw new RuntimeException("Failed to save candidate", e);
        }
    }

    @Override
    public void update(Candidate candidate) {
        String sql = "UPDATE candidate SET candidate_name = ?, contact_info = ?, resume_data = ?, interview_score = ?, application_status = ? " +
                     "WHERE candidate_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, candidate.getCandidateName());
            pstmt.setString(2, candidate.getContactInfo());
            pstmt.setString(3, candidate.getResumeData());
            pstmt.setDouble(4, candidate.getInterviewScore());
            pstmt.setString(5, candidate.getApplicationStatus().toString());
            pstmt.setString(6, candidate.getCandidateId());
            pstmt.executeUpdate();
            logger.info("Candidate updated: {}", candidate.getCandidateId());
        } catch (SQLException e) {
            logger.error("Error updating candidate", e);
            throw new RuntimeException("Failed to update candidate", e);
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM candidate WHERE candidate_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
            logger.info("Candidate deleted: {}", id);
        } catch (SQLException e) {
            logger.error("Error deleting candidate: {}", id, e);
            throw new RuntimeException("Failed to delete candidate", e);
        }
    }

    @Override
    public long countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM candidate WHERE application_status = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, Candidate.ApplicationStatus.valueOf(status).toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            logger.error("Error counting candidates by status: {}", status, e);
            throw new RuntimeException("Failed to count candidates by status", e);
        }
    }

    @Override
    public long countAll() {
        String sql = "SELECT COUNT(*) FROM candidate";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting all candidates", e);
            throw new RuntimeException("Failed to count candidates", e);
        }
    }

    private Candidate mapRowToCandidate(ResultSet rs) throws SQLException {
        Candidate candidate = new Candidate();
        candidate.setCandidateId(rs.getString("candidate_id"));
        candidate.setCandidateName(rs.getString("candidate_name"));
        candidate.setContactInfo(rs.getString("contact_info"));
        candidate.setResumeData(rs.getString("resume_data"));
        candidate.setInterviewScore(rs.getDouble("interview_score"));
        String statusStr = rs.getString("application_status");
        if (statusStr != null) {
            candidate.setApplicationStatus(Candidate.ApplicationStatus.valueOf(statusStr));
        }
        return candidate;
    }
}