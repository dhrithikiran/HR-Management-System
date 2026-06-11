package com.yourname.myapp.onboarding.exception;

public class CandidateOnboardingNotFoundException extends RuntimeException {
    public CandidateOnboardingNotFoundException(String candidateId) {
        super("Onboarding record not found for candidate: " + candidateId);
    }
}