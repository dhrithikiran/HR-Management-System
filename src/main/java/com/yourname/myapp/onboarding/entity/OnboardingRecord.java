package com.yourname.myapp.onboarding.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * OnboardingRecord POJO - Employee onboarding records
 * 
 * Maps to: onboarding_record table
 * Columns: onboarding_id, assigned_employee_id, employee_name, background_check_status, document_verification_status, verified_record, pipeline_status
 */
public class OnboardingRecord {

    public OnboardingRecord() {}

    private String onboardingId;
    private String assignedEmployeeId;
    private String employeeName;
    private BackgroundCheckStatus backgroundCheckStatus = BackgroundCheckStatus.PENDING;
    private DocumentVerificationStatus documentVerificationStatus = DocumentVerificationStatus.PENDING;
    private boolean verifiedRecord = false;
    private List<String> recentActivityLog = new ArrayList<>();
    private PipelineStatus pipelineStatus = PipelineStatus.EMPLOYEE_ASSIGNED;

    public enum BackgroundCheckStatus {
        PENDING,
        CLEARED,
        FAILED
    }

    public enum DocumentVerificationStatus {
        PENDING,
        VERIFIED,
        REJECTED
    }

    public enum PipelineStatus {
        EMPLOYEE_ASSIGNED,
        BACKGROUND_CHECK,
        DOCUMENT_VERIFICATION,
        VERIFIED,
        ACTIVE_ONBOARDING
    }

    // Getters and Setters

    public String getOnboardingId() { return onboardingId; }
    public void setOnboardingId(String onboardingId) { this.onboardingId = onboardingId; }

    public String getAssignedEmployeeId() { return assignedEmployeeId; }
    public void setAssignedEmployeeId(String assignedEmployeeId) { this.assignedEmployeeId = assignedEmployeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public BackgroundCheckStatus getBackgroundCheckStatus() { return backgroundCheckStatus; }
    public void setBackgroundCheckStatus(BackgroundCheckStatus backgroundCheckStatus) { this.backgroundCheckStatus = backgroundCheckStatus; }

    public DocumentVerificationStatus getDocumentVerificationStatus() { return documentVerificationStatus; }
    public void setDocumentVerificationStatus(DocumentVerificationStatus documentVerificationStatus) { this.documentVerificationStatus = documentVerificationStatus; }

    public boolean isVerifiedRecord() { return verifiedRecord; }
    public void setVerifiedRecord(boolean verifiedRecord) { this.verifiedRecord = verifiedRecord; }

    public List<String> getRecentActivityLog() { return recentActivityLog; }
    public void setRecentActivityLog(List<String> recentActivityLog) { this.recentActivityLog = recentActivityLog; }

    public PipelineStatus getPipelineStatus() { return pipelineStatus; }
    public void setPipelineStatus(PipelineStatus pipelineStatus) { this.pipelineStatus = pipelineStatus; }

    public void addActivity(String activity) {
        this.recentActivityLog.add(java.time.LocalDateTime.now() + " — " + activity);
    }

    @Override
    public String toString() {
        return "OnboardingRecord{" +
                "onboardingId='" + onboardingId + '\'' +
                ", assignedEmployeeId='" + assignedEmployeeId + '\'' +
                ", employeeName='" + employeeName + '\'' +
                ", backgroundCheckStatus=" + backgroundCheckStatus +
                ", documentVerificationStatus=" + documentVerificationStatus +
                ", verifiedRecord=" + verifiedRecord +
                ", pipelineStatus=" + pipelineStatus +
                '}';
    }
}