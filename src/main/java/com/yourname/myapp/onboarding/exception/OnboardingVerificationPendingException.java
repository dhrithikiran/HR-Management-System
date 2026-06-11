package com.yourname.myapp.onboarding.exception;

public class OnboardingVerificationPendingException extends RuntimeException {
    public OnboardingVerificationPendingException() {
        super("Cannot verify onboarding: background or document checks are still pending");
    }
}