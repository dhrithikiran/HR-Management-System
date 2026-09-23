package com.hrms.recruitment.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Candidate {
    private String candidateId;
    private String candidateName;
    private String contactInfo;
    private String resumeData;
    private Double interviewScore;
    private ApplicationStatus applicationStatus;

    public Candidate() {
    }

    public Candidate(String candidateId, String candidateName, String contactInfo, String resumeData, Double interviewScore, ApplicationStatus applicationStatus) {
        this.candidateId = candidateId;
        this.candidateName = candidateName;
        this.contactInfo = contactInfo;
        this.resumeData = resumeData;
        this.interviewScore = interviewScore;
        this.applicationStatus = applicationStatus;
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

    public ApplicationStatus getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(ApplicationStatus applicationStatus) {
        this.applicationStatus = applicationStatus;
    }
}
