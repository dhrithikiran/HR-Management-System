package com.yourname.myapp.onboarding.service;

import com.yourname.myapp.onboarding.entity.OnboardingRecord;
import java.util.List;
import java.util.Map;

public interface OnboardingService {

    List<OnboardingRecord> getAllRecords();

    OnboardingRecord getById(String id);

    // 🔁 CHANGED: candidateId instead of employeeId
    OnboardingRecord createRecord(String candidateId);

    void updateBackgroundCheck(String id, String status);

    void updateDocumentVerification(String id, String status);

    void approveOnboarding(String id);

    void deleteRecord(String id);

    Map<String, Object> getOnboardingStats();
}