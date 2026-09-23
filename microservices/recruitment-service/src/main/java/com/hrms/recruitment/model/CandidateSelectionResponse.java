package com.hrms.recruitment.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CandidateSelectionResponse {
    private String status;
    private String message;
    private String error;
    private Candidate candidate;
    private Object onboarding;
    private boolean onboardingInitiated;

    public CandidateSelectionResponse() {
    }

    public static CandidateSelectionResponse success(String message, Candidate candidate, Object onboarding) {
        CandidateSelectionResponse resp = new CandidateSelectionResponse();
        resp.setStatus("SUCCESS");
        resp.setMessage(message);
        resp.setCandidate(candidate);
        resp.setOnboarding(onboarding);
        resp.setOnboardingInitiated(true);
        return resp;
    }

    public static CandidateSelectionResponse partialSuccess(String message, String error, Candidate candidate) {
        CandidateSelectionResponse resp = new CandidateSelectionResponse();
        resp.setStatus("PARTIAL_SUCCESS");
        resp.setMessage(message);
        resp.setError(error);
        resp.setCandidate(candidate);
        resp.setOnboardingInitiated(false);
        return resp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public Object getOnboarding() {
        return onboarding;
    }

    public void setOnboarding(Object onboarding) {
        this.onboarding = onboarding;
    }

    public boolean isOnboardingInitiated() {
        return onboardingInitiated;
    }

    public void setOnboardingInitiated(boolean onboardingInitiated) {
        this.onboardingInitiated = onboardingInitiated;
    }
}
