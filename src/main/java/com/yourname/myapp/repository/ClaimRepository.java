package com.yourname.myapp.repository;

import com.yourname.myapp.config.DatabaseConnection;
import com.yourname.myapp.entity.Claim;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClaimRepository {
    private static final Logger logger = LoggerFactory.getLogger(ClaimRepository.class);

    public void save(Claim claim) {
        String sql = "INSERT INTO claim (employee_id, claim_type, amount, claim_status) VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE claim_type = VALUES(claim_type), amount = VALUES(amount), claim_status = VALUES(claim_status)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, claim.getEmployeeId());
            pstmt.setString(2, claim.getClaimType());
            pstmt.setBigDecimal(3, claim.getAmount());
            pstmt.setString(4, claim.getClaimStatus().toString());
            pstmt.executeUpdate();
            logger.info("Claim saved: {}", claim.getId());
        } catch (SQLException e) {
            logger.error("Error saving claim", e);
            throw new RuntimeException("Failed to save claim", e);
        }
    }

    public List<Claim> findAll() {
        String sql = "SELECT * FROM claim";
        List<Claim> claims = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                claims.add(mapRowToClaim(rs));
            }
            return claims;
        } catch (SQLException e) {
            logger.error("Error fetching all claims", e);
            throw new RuntimeException("Failed to fetch claims", e);
        }
    }

    public List<Claim> findByEmployeeId(String employeeId) {
        String sql = "SELECT * FROM claim WHERE employee_id = ?";
        List<Claim> claims = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    claims.add(mapRowToClaim(rs));
                }
                return claims;
            }
        } catch (SQLException e) {
            logger.error("Error finding claims by employee: {}", employeeId, e);
            throw new RuntimeException("Failed to find claims", e);
        }
    }

    public Optional<Claim> findById(Long id) {
        String sql = "SELECT * FROM claim WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToClaim(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error("Error finding claim by ID: {}", id, e);
            throw new RuntimeException("Failed to find claim", e);
        }
    }

    private Claim mapRowToClaim(ResultSet rs) throws SQLException {
        Claim claim = new Claim();
        claim.setId(rs.getLong("id"));
        claim.setEmployeeId(rs.getString("employee_id"));
        claim.setClaimType(rs.getString("claim_type"));
        claim.setAmount(rs.getBigDecimal("amount"));
        String statusStr = rs.getString("claim_status");
        if (statusStr != null) {
            claim.setClaimStatus(Claim.ClaimStatus.valueOf(statusStr));
        }
        return claim;
    }
}
