package com.hrms.onboarding.service;

import com.hrms.onboarding.model.OnboardingCreateRequest;
import com.hrms.onboarding.model.OnboardingRecord;

import java.util.List;
import java.util.Optional;

public interface OnboardingService {
    OnboardingRecord createOnboardingRecord(OnboardingCreateRequest request);
    Optional<OnboardingRecord> getRecordById(String id);
    Optional<OnboardingRecord> getRecordByCandidateOrOnboardingId(String id);
    List<OnboardingRecord> getAllRecords();
    boolean deleteRecord(String id);
}
