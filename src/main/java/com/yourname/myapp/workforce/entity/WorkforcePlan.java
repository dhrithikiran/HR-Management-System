package com.yourname.myapp.workforce.entity;

import java.math.BigDecimal;

/**
 * WorkforcePlan POJO - Workforce planning and hiring forecasts per department/quarter
 * 
 * Maps to: workforce_plan table
 * Columns: id, department, open_positions, hiring_forecast, hr_cost_projections, quarter, total_budget
 */
public class WorkforcePlan implements Cloneable {

    private Long id;
    private String department;
    private int openPositions;
    private int hiringForecast;
    private BigDecimal hrCostProjections;
    private String quarter;
    private BigDecimal totalBudget;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getOpenPositions() {
        return openPositions;
    }

    public void setOpenPositions(int openPositions) {
        this.openPositions = openPositions;
    }

    public int getHiringForecast() {
        return hiringForecast;
    }

    public void setHiringForecast(int hiringForecast) {
        this.hiringForecast = hiringForecast;
    }

    public BigDecimal getHrCostProjections() {
        return hrCostProjections;
    }

    public void setHrCostProjections(BigDecimal hrCostProjections) {
        this.hrCostProjections = hrCostProjections;
    }

    public String getQuarter() {
        return quarter;
    }

    public void setQuarter(String quarter) {
        this.quarter = quarter;
    }

    public BigDecimal getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(BigDecimal totalBudget) {
        this.totalBudget = totalBudget;
    }

    @Override
    public WorkforcePlan clone() {
        try {
            WorkforcePlan cloned = (WorkforcePlan) super.clone();
            cloned.setId(null);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Failed to clone WorkforcePlan", e);
        }
    }
}
