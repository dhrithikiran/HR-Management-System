package com.hrms.onboarding.repository;

import com.hrms.onboarding.model.OnboardingRecord;

import java.util.List;
import java.util.Optional;

public interface OnboardingRepository {
    List<OnboardingRecord> findAll();
    Optional<OnboardingRecord> findById(String onboardingId);
    Optional<OnboardingRecord> findByCandidateId(String candidateId);
    OnboardingRecord save(OnboardingRecord record);
    OnboardingRecord update(OnboardingRecord record);
    boolean delete(String onboardingId);
    long countAll();
}
