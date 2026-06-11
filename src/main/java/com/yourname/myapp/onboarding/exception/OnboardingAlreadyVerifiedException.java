package com.yourname.myapp.onboarding.exception;

public class OnboardingAlreadyVerifiedException extends RuntimeException {
    public OnboardingAlreadyVerifiedException(String id) {
        super("This onboarding is already completed: " + id);
    }
}