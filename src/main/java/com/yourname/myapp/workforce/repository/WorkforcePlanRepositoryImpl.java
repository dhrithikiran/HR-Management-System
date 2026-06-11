package com.yourname.myapp.workforce.repository;

import com.yourname.myapp.config.DatabaseConnection;
import com.yourname.myapp.workforce.entity.WorkforcePlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkforcePlanRepositoryImpl implements WorkforcePlanRepository {
    private static final Logger logger = LoggerFactory.getLogger(WorkforcePlanRepositoryImpl.class);

    @Override
    public List<WorkforcePlan> findAll() {
        String sql = "SELECT * FROM workforce_plan";
        List<WorkforcePlan> plans = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                plans.add(mapRowToWorkforcePlan(rs));
            }
            return plans;
        } catch (SQLException e) {
            logger.error("Error fetching all workforce plans", e);
            throw new RuntimeException("Failed to fetch workforce plans", e);
        }
    }

    @Override
    public List<WorkforcePlan> findByQuarter(String quarter) {
        String sql = "SELECT * FROM workforce_plan WHERE quarter = ?";
        List<WorkforcePlan> plans = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, quarter);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    plans.add(mapRowToWorkforcePlan(rs));
                }
                return plans;
            }
        } catch (SQLException e) {
            logger.error("Error finding workforce plans by quarter: {}", quarter, e);
            throw new RuntimeException("Failed to find workforce plans by quarter", e);
        }
    }

    @Override
    public WorkforcePlan findById(Long id) {
        String sql = "SELECT * FROM workforce_plan WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToWorkforcePlan(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            logger.error("Error finding workforce plan by ID: {}", id, e);
            throw new RuntimeException("Failed to find workforce plan", e);
        }
    }

    @Override
    public void save(WorkforcePlan plan) {
        String sql = "INSERT INTO workforce_plan (department, open_positions, hiring_forecast, hr_cost_projections, quarter, total_budget) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, plan.getDepartment());
            pstmt.setInt(2, plan.getOpenPositions());
            pstmt.setInt(3, plan.getHiringForecast());
            pstmt.setBigDecimal(4, plan.getHrCostProjections());
            pstmt.setString(5, plan.getQuarter());
            pstmt.setBigDecimal(6, plan.getTotalBudget());
            pstmt.executeUpdate();
            logger.info("Workforce plan saved: {}", plan.getId());
        } catch (SQLException e) {
            logger.error("Error saving workforce plan", e);
            throw new RuntimeException("Failed to save workforce plan", e);
        }
    }

    @Override
    public void update(WorkforcePlan plan) {
        String sql = "UPDATE workforce_plan SET department = ?, open_positions = ?, hiring_forecast = ?, hr_cost_projections = ?, " +
                     "quarter = ?, total_budget = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, plan.getDepartment());
            pstmt.setInt(2, plan.getOpenPositions());
            pstmt.setInt(3, plan.getHiringForecast());
            pstmt.setBigDecimal(4, plan.getHrCostProjections());
            pstmt.setString(5, plan.getQuarter());
            pstmt.setBigDecimal(6, plan.getTotalBudget());
            pstmt.setLong(7, plan.getId());
            pstmt.executeUpdate();
            logger.info("Workforce plan updated: {}", plan.getId());
        } catch (SQLException e) {
            logger.error("Error updating workforce plan", e);
            throw new RuntimeException("Failed to update workforce plan", e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM workforce_plan WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Workforce plan deleted: {}", id);
                return true;
            }
            return false;
        } catch (SQLException e) {
            logger.error("Error deleting workforce plan: {}", id, e);
            throw new RuntimeException("Failed to delete workforce plan", e);
        }
    }

    private WorkforcePlan mapRowToWorkforcePlan(ResultSet rs) throws SQLException {
        WorkforcePlan plan = new WorkforcePlan();
        plan.setId(rs.getLong("id"));
        plan.setDepartment(rs.getString("department"));
        plan.setOpenPositions(rs.getInt("open_positions"));
        plan.setHiringForecast(rs.getInt("hiring_forecast"));
        plan.setHrCostProjections(rs.getBigDecimal("hr_cost_projections"));
        plan.setQuarter(rs.getString("quarter"));
        plan.setTotalBudget(rs.getBigDecimal("total_budget"));
        return plan;
    }
}
