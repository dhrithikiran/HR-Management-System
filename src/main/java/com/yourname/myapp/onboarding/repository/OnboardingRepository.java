package com.yourname.myapp.onboarding.repository;

import com.yourname.myapp.onboarding.entity.OnboardingRecord;
import java.util.List;

public interface OnboardingRepository {

    List<OnboardingRecord> findAll();

    OnboardingRecord findById(String id);

    void save(OnboardingRecord record);

    void update(OnboardingRecord record);

    void delete(String id);

    long countAll();

    long countByPipelineStatus(String status);
}