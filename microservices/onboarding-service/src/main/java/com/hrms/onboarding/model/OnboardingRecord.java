package com.hrms.onboarding.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OnboardingRecord {
    private String onboardingId;
    private String assignedEmployeeId; // Stores candidateId / assigned employee id
    private String employeeName;
    private BackgroundCheckStatus backgroundCheckStatus;
    private DocumentVerificationStatus documentVerificationStatus;
    private boolean verifiedRecord;
    private PipelineStatus pipelineStatus;

    public OnboardingRecord() {
        this.backgroundCheckStatus = BackgroundCheckStatus.PENDING;
        this.documentVerificationStatus = DocumentVerificationStatus.PENDING;
        this.verifiedRecord = false;
        this.pipelineStatus = PipelineStatus.EMPLOYEE_ASSIGNED;
    }

    public OnboardingRecord(String onboardingId, String assignedEmployeeId, String employeeName) {
        this.onboardingId = onboardingId;
        this.assignedEmployeeId = assignedEmployeeId;
        this.employeeName = employeeName;
        this.backgroundCheckStatus = BackgroundCheckStatus.PENDING;
        this.documentVerificationStatus = DocumentVerificationStatus.PENDING;
        this.verifiedRecord = false;
        this.pipelineStatus = PipelineStatus.EMPLOYEE_ASSIGNED;
    }

    public String getOnboardingId() {
        return onboardingId;
    }

    public void setOnboardingId(String onboardingId) {
        this.onboardingId = onboardingId;
    }

    public String getAssignedEmployeeId() {
        return assignedEmployeeId;
    }

    public void setAssignedEmployeeId(String assignedEmployeeId) {
        this.assignedEmployeeId = assignedEmployeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public BackgroundCheckStatus getBackgroundCheckStatus() {
        return backgroundCheckStatus;
    }

    public void setBackgroundCheckStatus(BackgroundCheckStatus backgroundCheckStatus) {
        this.backgroundCheckStatus = backgroundCheckStatus;
    }

    public DocumentVerificationStatus getDocumentVerificationStatus() {
        return documentVerificationStatus;
    }

    public void setDocumentVerificationStatus(DocumentVerificationStatus documentVerificationStatus) {
        this.documentVerificationStatus = documentVerificationStatus;
    }

    public boolean isVerifiedRecord() {
        return verifiedRecord;
    }

    public void setVerifiedRecord(boolean verifiedRecord) {
        this.verifiedRecord = verifiedRecord;
    }

    public PipelineStatus getPipelineStatus() {
        return pipelineStatus;
    }

    public void setPipelineStatus(PipelineStatus pipelineStatus) {
        this.pipelineStatus = pipelineStatus;
    }
}
