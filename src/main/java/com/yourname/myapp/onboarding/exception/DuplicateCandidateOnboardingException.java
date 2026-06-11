package com.yourname.myapp.onboarding.exception;

public class DuplicateCandidateOnboardingException extends RuntimeException {
    public DuplicateCandidateOnboardingException(String candidateId) {
        super("Onboarding already exists for candidate: " + candidateId);
    }
}