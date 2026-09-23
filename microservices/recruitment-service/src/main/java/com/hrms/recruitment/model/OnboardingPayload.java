package com.hrms.recruitment.model;

public class OnboardingPayload {
    private String candidateId;
    private String candidateName;
    private String contactInfo;

    public OnboardingPayload() {
    }

    public OnboardingPayload(String candidateId, String candidateName, String contactInfo) {
        this.candidateId = candidateId;
        this.candidateName = candidateName;
        this.contactInfo = contactInfo;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
}
