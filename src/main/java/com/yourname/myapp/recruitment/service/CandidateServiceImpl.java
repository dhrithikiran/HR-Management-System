package com.yourname.myapp.recruitment.service;

import com.yourname.myapp.recruitment.entity.Candidate;
import com.yourname.myapp.recruitment.entity.Candidate.ApplicationStatus;
import com.yourname.myapp.recruitment.repository.CandidateRepository;
import com.yourname.myapp.recruitment.repository.CandidateRepositoryImpl;
import com.yourname.myapp.recruitment.validation.*;
import com.yourname.myapp.recruitment.exception.CandidateDataIncompleteException;
import com.yourname.myapp.onboarding.service.OnboardingService;
import com.yourname.myapp.onboarding.service.OnboardingServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * CandidateServiceImpl - Business logic layer for candidate management
 * 
 * Responsibilities:
 * 1. Orchestrate validation chain for new candidates (Chain of Responsibility pattern)
 * 2. Enforce status transition rules (State Transition pattern)
 * 3. Generate auto-generated candidate IDs (CND-XXX format)
 * 4. Calculate recruitment statistics for dashboard
 * 5. Provide CRUD operations for candidates
 * 
 * Validation Chain Flow:
 * ContactInfoValidator → ResumeValidator → DuplicateCheckHandler
 * 
 * Exception Handling: All validation errors throw CandidateDataIncompleteException
 * with descriptive messages for UI display
 * 
 * @author OOAD Project
 * @version 1.0
 * @since 2024
 */
public class CandidateServiceImpl implements CandidateService {
    private static final Logger logger = LoggerFactory.getLogger(CandidateServiceImpl.class);
    private final CandidateRepository candidateRepository = new CandidateRepositoryImpl();
    private final OnboardingService onboardingService = new OnboardingServiceImpl();

    /**
     * Builds the validation chain following Chain of Responsibility pattern
     * Each validator processes the candidate and passes to next validator
     * 
     * Chain Order:
     * 1. ContactInfoValidator - validates non-empty contact info
     * 2. ResumeValidator - validates non-empty resume data  
     * 3. DuplicateCheckHandler - checks database for duplicate candidates
     * 
     * @return ContactInfoValidator (head of chain)
     */
    private ValidationHandler buildValidationChain() {
        ContactInfoValidator contactInfoValidator = new ContactInfoValidator();
        ResumeValidator resumeValidator = new ResumeValidator();
        DuplicateCheckHandler duplicateCheckHandler = new DuplicateCheckHandler(candidateRepository);
        contactInfoValidator.setNext(resumeValidator).setNext(duplicateCheckHandler);
        return contactInfoValidator;
    }

    /**
     * State Transition Map - Defines allowed status transitions per workflow rules
     * 
     * This map enforces business logic: candidates can only transition through valid states
     * For example: A candidate in APPLIED state can only move to SHORTLISTED or REJECTED
     * 
     * Transition Rules:
     * - APPLIED: Can go to SHORTLISTED (for screening) or REJECTED (if not qualified)
     * - SHORTLISTED: Can go to INTERVIEW (for interviews), SELECTED (if no interview needed), or REJECTED
     * - INTERVIEW: Can go to SELECTED (passed interview) or REJECTED (failed interview)
     * - SELECTED: Can only go to REJECTED (reverse decision)
     * - REJECTED: Terminal state - no transitions allowed
     */
    private static final Map<ApplicationStatus, List<ApplicationStatus>> allowedTransitions = Map.of(
        ApplicationStatus.APPLIED, List.of(ApplicationStatus.SHORTLISTED, ApplicationStatus.REJECTED),
        ApplicationStatus.SHORTLISTED, List.of(ApplicationStatus.INTERVIEW, ApplicationStatus.SELECTED, ApplicationStatus.REJECTED),
        ApplicationStatus.INTERVIEW, List.of(ApplicationStatus.SELECTED, ApplicationStatus.REJECTED),
        ApplicationStatus.SELECTED, List.of(ApplicationStatus.REJECTED),  // Allow reversing selected to rejected
        ApplicationStatus.REJECTED, List.of()  // No transitions from rejected
    );

    @Override
    public List<Candidate> getAllCandidates(String status) {
        return candidateRepository.findAllByStatus(status);
    }

    @Override
    public Candidate getCandidateById(String id) {
        Candidate candidate = candidateRepository.findById(id);
        if (candidate == null) throw new NoSuchElementException("Candidate not found");
        return candidate;
    }

    /**
     * Create a new candidate with validation chain
     * 
     * Process:
     * 1. Execute validation chain (ContactInfo → Resume → Duplicate check)
     * 2. Generate auto-ID in format CND-XXX
     * 3. Set initial status to APPLIED
     * 4. Save to database
     * 
     * @param candidate Candidate object with name, contact, resume populated
     * @return Created candidate with auto-generated ID
     * @throws CandidateDataIncompleteException if validation fails
     */
    @Override
    public Candidate createCandidate(Candidate candidate) {
        buildValidationChain().validate(candidate);
        candidate.setCandidateId(generateCandidateId());
        candidate.setApplicationStatus(ApplicationStatus.APPLIED);
        candidateRepository.save(candidate);
        return candidate;
    }

    /**
     * Update candidate details (name, contact, resume, interview score)
     * Executes validation chain but skips duplicate check for existing candidates
     * 
     * @param id Candidate ID to update
     * @param updated Updated candidate data
     * @return Updated candidate object
     * @throws CandidateDataIncompleteException if validation fails
     */
    @Override
    public Candidate updateCandidate(String id, Candidate updated) {
        Candidate existing = getCandidateById(id);
        buildValidationChain().validate(updated);
        existing.setCandidateName(updated.getCandidateName());
        existing.setContactInfo(updated.getContactInfo());
        existing.setResumeData(updated.getResumeData());
        existing.setInterviewScore(updated.getInterviewScore());
        candidateRepository.update(existing);
        return existing;
    }

    /**
     * Update candidate status with transition validation
     * 
     * Validates that the status transition is allowed per workflow rules
     * Throws IllegalStateException if invalid transition attempted
     * 
     * Example Valid Transitions:
     * - APPLIED to SHORTLISTED ✓
     * - APPLIED to INTERVIEW ✗ (invalid - must go through SHORTLISTED first)
     * - SELECTED to REJECTED ✓
     * 
     * @param id Candidate ID
     * @param newStatus New application status
     * @return Updated candidate
     * @throws IllegalStateException if transition not allowed
     * @throws NoSuchElementException if candidate not found
     */
    @Override
    public Candidate updateStatus(String id, String newStatus) {
        try {
            Candidate candidate = getCandidateById(id);
            ApplicationStatus current = candidate.getApplicationStatus();
            ApplicationStatus next = ApplicationStatus.valueOf(newStatus);
            if (!allowedTransitions.getOrDefault(current, List.of()).contains(next)) {
                throw new IllegalStateException("Invalid status transition");
            }
            candidate.setApplicationStatus(next);
            
            try {
                candidateRepository.update(candidate);
                logger.info("Candidate status updated successfully: {} -> {}", id, newStatus);
            } catch (Exception e) {
                logger.error("Failed to update candidate status in database: {}", id, e);
                throw new RuntimeException("Failed to update candidate status: " + e.getMessage(), e);
            }
            
            // If transitioning to SELECTED, create onboarding record
            if (next == ApplicationStatus.SELECTED) {
                try {
                    onboardingService.createRecord(id);
                    logger.info("Onboarding record created successfully for candidate: {}", id);
                } catch (Exception e) {
                    logger.error("Failed to create onboarding record for candidate: {}", id, e);
                    // Don't throw - the candidate status was already updated successfully
                    // The onboarding record can be created later from the dashboard
                }
            }
            
            return candidate;
        } catch (Exception e) {
            logger.error("Error updating candidate status: {}", id, e);
            throw e;
        }
    }

    @Override
    public void deleteCandidate(String id) {
        Candidate candidate = getCandidateById(id);
        candidateRepository.delete(id);
    }

    /**
     * Get recruitment dashboard statistics
     * 
     * Calculates key metrics for recruitment dashboard:
     * - Total applications received (all candidates)
     * - Shortlisted count (candidates in SHORTLISTED status)
     * - Selected count (candidates in SELECTED status)
     * - Open positions (hardcoded for now, can be made dynamic)
     * - Hiring forecast (estimated hires based on current pipeline)
     * 
     * @return Map containing:
     *   - applicationsReceived: Total candidate count
     *   - shortlistedCount: Count of SHORTLISTED candidates
     *   - selectedCount: Count of SELECTED candidates
     *   - openPositions: Number of open job positions
     *   - hiringForecast: Estimated candidates to hire
     */
    @Override
    public Map<String, Object> getRecruitmentStats() {
        long total = candidateRepository.countAll();
        long shortlisted = candidateRepository.countByStatus("SHORTLISTED");
        long selected = candidateRepository.countByStatus("SELECTED");
        int openPositions = 5; // Dummy value
        int hiringForecast = (int) (selected + (shortlisted * 0.5)); // Simple logic
        Map<String, Object> stats = new HashMap<>();
        stats.put("applicationsReceived", total);
        stats.put("shortlistedCount", shortlisted);
        stats.put("selectedCount", selected);
        stats.put("openPositions", openPositions);
        stats.put("hiringForecast", hiringForecast);
        return stats;
    }

    /**
     * Generate auto-incremented candidate ID in format CND-XXX
     * 
     * Example IDs: CND-001, CND-002, CND-003, etc.
     * Uses total candidate count + 1 to generate next ID
     * 
     * @return Generated candidate ID
     */
    private String generateCandidateId() {
        long count = candidateRepository.countAll() + 1;
        return String.format("CND-%03d", count);
    }
}
