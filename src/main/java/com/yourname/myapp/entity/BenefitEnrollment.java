package com.yourname.myapp.entity;

/**
 * BenefitEnrollment POJO - Benefits enrollment per employee
 * 
 * Maps to: benefit_enrollment table
 * Columns: id, employee_id, enrollment_status, health_plan, insurance_plan, insurance_coverage_status
 */
public class BenefitEnrollment {

    private Long id;
    private String employeeId;
    private EnrollmentStatus enrollmentStatus;
    private String healthPlan;
    private String insurancePlan;
    private CoverageStatus insuranceCoverageStatus;

    public enum EnrollmentStatus {
        ENROLLED, PENDING, NOT_ENROLLED
    }

    public enum CoverageStatus {
        ACTIVE, INACTIVE
    }

    // Getters
    public Long getId() { return id; }
    public String getEmployeeId() { return employeeId; }
    public EnrollmentStatus getEnrollmentStatus() { return enrollmentStatus; }
    public String getHealthPlan() { return healthPlan; }
    public String getInsurancePlan() { return insurancePlan; }
    public CoverageStatus getInsuranceCoverageStatus() { return insuranceCoverageStatus; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public void setEnrollmentStatus(EnrollmentStatus enrollmentStatus) {
        this.enrollmentStatus = enrollmentStatus;
        // Auto-derive coverage status
        this.insuranceCoverageStatus = (enrollmentStatus == EnrollmentStatus.ENROLLED)
                ? CoverageStatus.ACTIVE : CoverageStatus.INACTIVE;
    }
    public void setHealthPlan(String healthPlan) { this.healthPlan = healthPlan; }
    public void setInsurancePlan(String insurancePlan) { this.insurancePlan = insurancePlan; }
    public void setInsuranceCoverageStatus(CoverageStatus insuranceCoverageStatus) {
        this.insuranceCoverageStatus = insuranceCoverageStatus;
    }
}
