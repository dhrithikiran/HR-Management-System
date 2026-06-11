package com.yourname.myapp.performance.repository;

import com.yourname.myapp.config.DatabaseConnection;
import com.yourname.myapp.performance.entity.Promotion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PromotionRepositoryImpl implements PromotionRepository {
    private static final Logger logger = LoggerFactory.getLogger(PromotionRepositoryImpl.class);

    @Override
    public List<Promotion> findAll() {
        String sql = "SELECT * FROM promotion";
        List<Promotion> promotions = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                promotions.add(mapRowToPromotion(rs));
            }
            return promotions;
        } catch (SQLException e) {
            logger.error("Error fetching all promotions", e);
            throw new RuntimeException("Failed to fetch promotions", e);
        }
    }

    @Override
    public void save(Promotion promotion) {
        String sql = "INSERT INTO promotion (promotion_id, employee_id, new_role, effective_date) " +
                     "VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, promotion.getPromotionId());
            pstmt.setString(2, promotion.getEmployeeId());
            pstmt.setString(3, promotion.getNewRole());
            pstmt.setDate(4, promotion.getEffectiveDate() != null ? Date.valueOf(promotion.getEffectiveDate()) : null);
            pstmt.executeUpdate();
            logger.info("Promotion saved: {}", promotion.getPromotionId());
        } catch (SQLException e) {
            logger.error("Error saving promotion", e);
            throw new RuntimeException("Failed to save promotion", e);
        }
    }

    @Override
    public int deleteByEmployeeId(String employeeId) {
        String sql = "DELETE FROM promotion WHERE employee_id = ? OR UPPER(TRIM(employee_id)) = UPPER(TRIM(?))";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employeeId);
            pstmt.setString(2, employeeId != null ? employeeId : "");
            int deletedCount = pstmt.executeUpdate();
            logger.info("Deleted {} promotions for employee: {}", deletedCount, employeeId);
            return deletedCount;
        } catch (SQLException e) {
            logger.error("Error deleting promotions by employee: {}", employeeId, e);
            throw new RuntimeException("Failed to delete promotions by employee", e);
        }
    }

    @Override
    public int deleteOrphanedPromotions() {
        String sql = "DELETE FROM promotion WHERE employee_id NOT IN (SELECT employee_id FROM employees)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int deletedCount = pstmt.executeUpdate();
            logger.info("Deleted {} orphaned promotions", deletedCount);
            return deletedCount;
        } catch (SQLException e) {
            logger.error("Error deleting orphaned promotions", e);
            throw new RuntimeException("Failed to delete orphaned promotions", e);
        }
    }

    @Override
    public long countAll() {
        String sql = "SELECT COUNT(*) FROM promotion";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting all promotions", e);
            throw new RuntimeException("Failed to count promotions", e);
        }
    }

    private Promotion mapRowToPromotion(ResultSet rs) throws SQLException {
        Promotion promotion = new Promotion();
        promotion.setPromotionId(rs.getString("promotion_id"));
        promotion.setEmployeeId(rs.getString("employee_id"));
        promotion.setNewRole(rs.getString("new_role"));
        Date effectiveDate = rs.getDate("effective_date");
        if (effectiveDate != null) {
            promotion.setEffectiveDate(effectiveDate.toLocalDate());
        }
        return promotion;
    }
}
