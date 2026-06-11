package com.yourname.myapp.entity;

import java.math.BigDecimal;

/**
 * Claim POJO - Insurance claims per employee
 * 
 * Maps to: claim table
 * Columns: id, employee_id, claim_type, amount, claim_status
 */
public class Claim {

    private Long id;
    private String employeeId;
    private String claimType;
    private BigDecimal amount;
    private ClaimStatus claimStatus;

    public enum ClaimStatus {
        APPROVED, PENDING
    }

    // Getters
    public Long getId() { return id; }
    public String getEmployeeId() { return employeeId; }
    public String getClaimType() { return claimType; }
    public BigDecimal getAmount() { return amount; }
    public ClaimStatus getClaimStatus() { return claimStatus; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public void setClaimType(String claimType) { this.claimType = claimType; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setClaimStatus(ClaimStatus claimStatus) { this.claimStatus = claimStatus; }
}
