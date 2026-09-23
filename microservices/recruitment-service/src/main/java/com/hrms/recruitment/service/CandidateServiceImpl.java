package com.hrms.recruitment.service;

import com.hrms.recruitment.client.OnboardingServiceClient;
import com.hrms.recruitment.model.*;
import com.hrms.recruitment.repository.CandidateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public class CandidateServiceImpl implements CandidateService {
    private static final Logger logger = LoggerFactory.getLogger(CandidateServiceImpl.class);

    private final CandidateRepository candidateRepository;
    private final OnboardingServiceClient onboardingServiceClient;

    public CandidateServiceImpl(CandidateRepository candidateRepository, OnboardingServiceClient onboardingServiceClient) {
        this.candidateRepository = candidateRepository;
        this.onboardingServiceClient = onboardingServiceClient;
    }

    @Override
    public Candidate createCandidate(CandidateRequest request) {
        if (request.getCandidateName() == null || request.getCandidateName().trim().isEmpty()) {
            throw new IllegalArgumentException("Candidate name is required");
        }
        if (request.getContactInfo() == null || request.getContactInfo().trim().isEmpty()) {
            throw new IllegalArgumentException("Contact info is required");
        }

        String candidateId = generateCandidateId();
        Candidate candidate = new Candidate(
                candidateId,
                request.getCandidateName().trim(),
                request.getContactInfo().trim(),
                request.getResumeData() != null ? request.getResumeData() : "Resume content for " + request.getCandidateName(),
                request.getInterviewScore() != null ? request.getInterviewScore() : 85.0,
                ApplicationStatus.APPLIED
        );

        return candidateRepository.save(candidate);
    }

    @Override
    public Optional<Candidate> getCandidateById(String id) {
        return candidateRepository.findById(id);
    }

    @Override
    public List<Candidate> getAllCandidates(String status) {
        if (status == null || status.trim().isEmpty() || "ALL".equalsIgnoreCase(status)) {
            return candidateRepository.findAll();
        }
        return candidateRepository.findAllByStatus(status.toUpperCase());
    }

    @Override
    public CandidateSelectionResponse selectCandidate(String id) {
        Optional<Candidate> optionalCandidate = candidateRepository.findById(id);
        if (optionalCandidate.isEmpty()) {
            throw new NoSuchElementException("Candidate not found with ID: " + id);
        }

        Candidate candidate = optionalCandidate.get();
        candidate.setApplicationStatus(ApplicationStatus.SELECTED);
        candidateRepository.update(candidate);
        logger.info("Candidate {} status transitioned to SELECTED", id);

        // Make HTTP REST call to Onboarding Microservice
        OnboardingPayload payload = new OnboardingPayload(
                candidate.getCandidateId(),
                candidate.getCandidateName(),
                candidate.getContactInfo()
        );

        try {
            Map<String, Object> onboardingResult = onboardingServiceClient.initiateOnboarding(payload);
            logger.info("Successfully notified Onboarding Service for candidate {}: {}", id, onboardingResult);
            return CandidateSelectionResponse.success(
                    "Candidate selected and onboarding record created successfully via HTTP inter-service call.",
                    candidate,
                    onboardingResult
            );
        } catch (Exception e) {
            logger.error("Inter-service HTTP call to Onboarding Service failed for candidate {}: {}", id, e.getMessage());
            return CandidateSelectionResponse.partialSuccess(
                    "Candidate status updated to SELECTED in Recruitment database, but inter-service HTTP call to Onboarding Service failed.",
                    e.getMessage(),
                    candidate
            );
        }
    }

    @Override
    public boolean deleteCandidate(String id) {
        return candidateRepository.delete(id);
    }

    private synchronized String generateCandidateId() {
        long count = candidateRepository.countAll() + 1;
        String id = String.format("CND-%03d", count);
        // Ensure uniqueness if records were deleted
        while (candidateRepository.findById(id).isPresent()) {
            count++;
            id = String.format("CND-%03d", count);
        }
        return id;
    }
}
