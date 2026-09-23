package com.hrms.recruitment.model;

public class CandidateRequest {
    private String candidateName;
    private String contactInfo;
    private String resumeData;
    private Double interviewScore;

    public CandidateRequest() {
    }

    public CandidateRequest(String candidateName, String contactInfo, String resumeData, Double interviewScore) {
        this.candidateName = candidateName;
        this.contactInfo = contactInfo;
        this.resumeData = resumeData;
        this.interviewScore = interviewScore;
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

    public String getResumeData() {
        return resumeData;
    }

    public void setResumeData(String resumeData) {
        this.resumeData = resumeData;
    }

    public Double getInterviewScore() {
        return interviewScore;
    }

    public void setInterviewScore(Double interviewScore) {
        this.interviewScore = interviewScore;
    }
}
