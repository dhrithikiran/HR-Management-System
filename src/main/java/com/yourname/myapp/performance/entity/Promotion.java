package com.yourname.myapp.performance.entity;

import java.time.LocalDate;

/**
 * Promotion POJO - Employee promotion records
 * 
 * Maps to: promotion table
 * Columns: promotion_id, employee_id, new_role, effective_date
 */
public class Promotion {

    private String promotionId;
    private String employeeId;
    private String newRole;
    private LocalDate effectiveDate;

    public String getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(String promotionId) {
        this.promotionId = promotionId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getNewRole() {
        return newRole;
    }

    public void setNewRole(String newRole) {
        this.newRole = newRole;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
}
