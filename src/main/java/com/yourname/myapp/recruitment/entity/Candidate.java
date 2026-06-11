package com.yourname.myapp.recruitment.entity;

/**
 * Candidate POJO - Represents a job candidate in the Recruitment & ATS system
 * 
 * Maps to: candidate table
 * Columns: candidate_id, candidate_name, contact_info, resume_data, interview_score, application_status
 * 
 * Application Status Enum - Represents the candidate's current position in recruitment workflow
 * 
 * Valid Status Transitions:
 * - APPLIED: Initial status when candidate applies
 * - SHORTLISTED: After passing initial screening
 * - INTERVIEW: Candidate is being interviewed
 * - SELECTED: Final stage - candidate selected for hire
 * - REJECTED: Candidate not selected (terminal state)
 * 
 * Transition Rules (enforced by CandidateServiceImpl):
 * APPLIED -> [SHORTLISTED, REJECTED]
 * SHORTLISTED -> [INTERVIEW, SELECTED, REJECTED]
 * INTERVIEW -> [SELECTED, REJECTED]
 * SELECTED -> [REJECTED]
 * REJECTED -> []  (no transitions from rejected)
 */
public class Candidate {
    public Candidate() {}

    private String candidateId;
    private String candidateName;
    private String contactInfo;
    private String resumeData;
    private double interviewScore;
    private ApplicationStatus applicationStatus;

    public enum ApplicationStatus {
        APPLIED,        // Initial application state
        SHORTLISTED,    // Passed screening
        INTERVIEW,      // In interview process
        SELECTED,       // Selected for hire
        REJECTED        // Application rejected (terminal state)
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

    public double getInterviewScore() {
        return interviewScore;
    }

    public void setInterviewScore(double interviewScore) {
        this.interviewScore = interviewScore;
    }

    public ApplicationStatus getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(ApplicationStatus applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    @Override
    public String toString() {
        return "Candidate{" +
                "candidateId='" + candidateId + '\'' +
                ", candidateName='" + candidateName + '\'' +
                ", contactInfo='" + contactInfo + '\'' +
                ", resumeData='" + resumeData + '\'' +
                ", interviewScore=" + interviewScore +
                ", applicationStatus=" + applicationStatus +
                '}';
    }
}
