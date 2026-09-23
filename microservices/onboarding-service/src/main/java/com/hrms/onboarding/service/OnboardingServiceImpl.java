package com.hrms.onboarding.service;

import com.hrms.onboarding.model.*;
import com.hrms.onboarding.repository.OnboardingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class OnboardingServiceImpl implements OnboardingService {
    private static final Logger logger = LoggerFactory.getLogger(OnboardingServiceImpl.class);

    private final OnboardingRepository onboardingRepository;

    public OnboardingServiceImpl(OnboardingRepository onboardingRepository) {
        this.onboardingRepository = onboardingRepository;
    }

    @Override
    public OnboardingRecord createOnboardingRecord(OnboardingCreateRequest request) {
        if (request.getCandidateId() == null || request.getCandidateId().trim().isEmpty()) {
            throw new IllegalArgumentException("Candidate ID is required to initiate onboarding");
        }
        if (request.getCandidateName() == null || request.getCandidateName().trim().isEmpty()) {
            throw new IllegalArgumentException("Candidate name is required to initiate onboarding");
        }

        String candidateId = request.getCandidateId().trim();
        String candidateName = request.getCandidateName().trim();

        // Check if an onboarding record already exists for this candidate
        Optional<OnboardingRecord> existing = onboardingRepository.findByCandidateId(candidateId);
        if (existing.isPresent()) {
            logger.info("Onboarding record already exists for candidate {}: {}", candidateId, existing.get().getOnboardingId());
            return existing.get();
        }

        String onboardingId = generateOnboardingId();
        OnboardingRecord record = new OnboardingRecord(onboardingId, candidateId, candidateName);
        record.setBackgroundCheckStatus(BackgroundCheckStatus.PENDING);
        record.setDocumentVerificationStatus(DocumentVerificationStatus.PENDING);
        record.setVerifiedRecord(false);
        record.setPipelineStatus(PipelineStatus.EMPLOYEE_ASSIGNED);

        OnboardingRecord saved = onboardingRepository.save(record);
        logger.info("Successfully created onboarding record {} for candidate {}", onboardingId, candidateId);
        return saved;
    }

    @Override
    public Optional<OnboardingRecord> getRecordById(String id) {
        return onboardingRepository.findById(id);
    }

    @Override
    public Optional<OnboardingRecord> getRecordByCandidateOrOnboardingId(String id) {
        // First try by candidateId
        Optional<OnboardingRecord> byCandidate = onboardingRepository.findByCandidateId(id);
        if (byCandidate.isPresent()) {
            return byCandidate;
        }
        // Fallback by onboardingId
        return onboardingRepository.findById(id);
    }

    @Override
    public List<OnboardingRecord> getAllRecords() {
        return onboardingRepository.findAll();
    }

    @Override
    public boolean deleteRecord(String id) {
        return onboardingRepository.delete(id);
    }

    private synchronized String generateOnboardingId() {
        long count = onboardingRepository.countAll() + 1;
        String id = String.format("ONB-%03d", count);
        while (onboardingRepository.findById(id).isPresent()) {
            count++;
            id = String.format("ONB-%03d", count);
        }
        return id;
    }
}
