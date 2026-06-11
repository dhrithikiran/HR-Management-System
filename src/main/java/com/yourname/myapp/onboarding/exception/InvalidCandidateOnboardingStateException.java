package com.yourname.myapp.onboarding.exception;

public class InvalidCandidateOnboardingStateException extends RuntimeException {
    public InvalidCandidateOnboardingStateException(String message) {
        super("Invalid onboarding operation: " + message);
    }
}