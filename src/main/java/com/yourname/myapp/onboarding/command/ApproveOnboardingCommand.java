package com.yourname.myapp.onboarding.command;

import com.yourname.myapp.onboarding.entity.OnboardingRecord;
import com.yourname.myapp.onboarding.repository.OnboardingRepository;
import com.yourname.myapp.onboarding.exception.OnboardingVerificationPendingException;

public class ApproveOnboardingCommand implements Command {

    private final OnboardingRecord record;
    private final OnboardingRepository repository;

    public ApproveOnboardingCommand(OnboardingRecord record, OnboardingRepository repository) {
        this.record = record;
        this.repository = repository;
    }

    @Override
    public void execute() {

        boolean bgCleared =
                record.getBackgroundCheckStatus()
                        == OnboardingRecord.BackgroundCheckStatus.CLEARED;

        boolean docVerified =
                record.getDocumentVerificationStatus()
                        == OnboardingRecord.DocumentVerificationStatus.VERIFIED;

        // 🚨 BLOCK IF NOT READY
        if (!bgCleared || !docVerified) {
            throw new OnboardingVerificationPendingException();
        }

        // 🟣 STEP 1: VERIFIED stage only (NO final flags here)
        record.setPipelineStatus(OnboardingRecord.PipelineStatus.VERIFIED);
        record.addActivity("Onboarding verified successfully.");
        repository.update(record);

        // 🟢 STEP 2: FINAL STATE → ACTIVE ONBOARDING
        record.setPipelineStatus(OnboardingRecord.PipelineStatus.ACTIVE_ONBOARDING);
        record.setVerifiedRecord(true); // final completion marker
        record.addActivity("Employee moved to ACTIVE onboarding.");
        repository.update(record);
    }
}