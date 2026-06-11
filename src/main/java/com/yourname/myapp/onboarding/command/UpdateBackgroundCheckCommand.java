package com.yourname.myapp.onboarding.command;

import com.yourname.myapp.onboarding.entity.OnboardingRecord;
import com.yourname.myapp.onboarding.repository.OnboardingRepository;

public class UpdateBackgroundCheckCommand implements Command {

    private final OnboardingRecord record;
    private final OnboardingRecord.BackgroundCheckStatus newStatus;
    private final OnboardingRepository repository;

    public UpdateBackgroundCheckCommand(OnboardingRecord record,
                                        OnboardingRecord.BackgroundCheckStatus newStatus,
                                        OnboardingRepository repository) {
        this.record = record;
        this.newStatus = newStatus;
        this.repository = repository;
    }

    @Override
    public void execute() {

        record.setBackgroundCheckStatus(newStatus);

        if (newStatus == OnboardingRecord.BackgroundCheckStatus.CLEARED) {
            record.setPipelineStatus(OnboardingRecord.PipelineStatus.DOCUMENT_VERIFICATION);
        } else if (newStatus == OnboardingRecord.BackgroundCheckStatus.FAILED) {
            record.setPipelineStatus(OnboardingRecord.PipelineStatus.EMPLOYEE_ASSIGNED);
        }

        record.addActivity("Background check updated to: " + newStatus);

        // ✅ NEW FIX: mark verified if BOTH checks are good
        if (record.getBackgroundCheckStatus() == OnboardingRecord.BackgroundCheckStatus.CLEARED &&
            record.getDocumentVerificationStatus() == OnboardingRecord.DocumentVerificationStatus.VERIFIED) {

            record.setVerifiedRecord(true);
            record.setPipelineStatus(OnboardingRecord.PipelineStatus.VERIFIED);
        }

        repository.update(record);
    }
}