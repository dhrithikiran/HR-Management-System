package com.yourname.myapp.recruitment.validation;

import com.yourname.myapp.recruitment.entity.Candidate;
import com.yourname.myapp.recruitment.repository.CandidateRepository;

/**
 * DuplicateCheckHandler - Third stage of validation chain (Terminal handler)
 * 
 * Purpose: Check if candidate already exists in database to prevent duplicates
 * 
 * Validation Rules:
 * - For NEW candidates (candidateId == null): Skip check (ID not yet generated)
 * - For EXISTING candidates (candidateId != null): Check if ID already exists in DB
 * 
 * Special Logic:
 * This is the TERMINAL handler - it has no next validator
 * For new candidates, the ID is generated AFTER validation in CandidateServiceImpl
 * So duplicate check only applies to existing candidate updates
 * 
 * Chain Position: THIRD/LAST (called after ResumeValidator)
 * Previous validator: ResumeValidator
 * Next validator: NONE (terminal)
 * 
 * Error Handling: Throws CandidateDataIncompleteException if duplicate found
 * 
 * @author OOAD Project
 * @version 1.0
 * @since 2024
 */
public class DuplicateCheckHandler extends ValidationHandler {
    private final CandidateRepository candidateRepository;

    /**
     * Constructor - requires repository for database lookup
     * 
     * @param candidateRepository Repository to check for duplicates
     */
    public DuplicateCheckHandler(CandidateRepository candidateRepository) {
        this.candidateRepository = candidateRepository;
    }

    /**
     * Check for duplicate candidates in database
     * 
     * Important Note:
     * - For NEW candidates: candidateId is null (not yet generated), so this check is skipped
     * - For EXISTING candidates: candidateId is set, check DB for conflicts
     * - This design allows new candidates to pass through without DB lookup
     * 
     * @param candidate Candidate to validate
     * @throws CandidateDataIncompleteException if duplicate candidate ID found in DB
     */
    @Override
    public void validate(Candidate candidate) {
        // Only check for duplicates if the candidate has an ID (for updates).
        // For new candidates, the ID is generated after validation.
        if (candidate.getCandidateId() != null && candidateRepository.findById(candidate.getCandidateId()) != null) {
            throw new com.yourname.myapp.recruitment.exception.CandidateDataIncompleteException("Duplicate candidate ID.");
        }
        // Since this is terminal handler, no next.validate() call
        if (next != null) next.validate(candidate);
    }
}
