package com.yourname.myapp.onboarding.command;

import com.yourname.myapp.onboarding.entity.OnboardingRecord;
import com.yourname.myapp.onboarding.repository.OnboardingRepository;

public class UpdateDocumentVerificationCommand implements Command {

    private final OnboardingRecord record;
    private final OnboardingRecord.DocumentVerificationStatus newStatus;
    private final OnboardingRepository repository;

    public UpdateDocumentVerificationCommand(OnboardingRecord record,
                                             OnboardingRecord.DocumentVerificationStatus newStatus,
                                             OnboardingRepository repository) {
        this.record = record;
        this.newStatus = newStatus;
        this.repository = repository;
    }

    @Override
    public void execute() {

        record.setDocumentVerificationStatus(newStatus);

        if (newStatus == OnboardingRecord.DocumentVerificationStatus.VERIFIED) {
            record.setPipelineStatus(OnboardingRecord.PipelineStatus.VERIFIED);
        } else if (newStatus == OnboardingRecord.DocumentVerificationStatus.REJECTED) {
            record.setPipelineStatus(OnboardingRecord.PipelineStatus.DOCUMENT_VERIFICATION);
        }

        record.addActivity("Document verification updated to: " + newStatus);

        // ✅ NEW FIX: mark verified if BOTH checks are good
        if (record.getBackgroundCheckStatus() == OnboardingRecord.BackgroundCheckStatus.CLEARED &&
            record.getDocumentVerificationStatus() == OnboardingRecord.DocumentVerificationStatus.VERIFIED) {

            record.setVerifiedRecord(true);
            record.setPipelineStatus(OnboardingRecord.PipelineStatus.VERIFIED);
        }

        repository.update(record);
    }
}