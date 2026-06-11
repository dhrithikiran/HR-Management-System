package com.yourname.myapp.recruitment.validation;

import com.yourname.myapp.recruitment.entity.Candidate;

/**
 * ResumeValidator - Second stage of validation chain
 * 
 * Purpose: Validates that candidate has provided resume data
 * 
 * Validation Rules:
 * - Resume data cannot be null
 * - Resume data cannot be empty string
 * - Resume data should contain meaningful content
 * 
 * Chain Position: SECOND (called after ContactInfoValidator)
 * Previous validator: ContactInfoValidator
 * Next validator: DuplicateCheckHandler
 * 
 * Error Handling: Throws CandidateDataIncompleteException if validation fails
 * 
 * @author OOAD Project
 * @version 1.0
 * @since 2024
 */
public class ResumeValidator extends ValidationHandler {
    /**
     * Validate candidate's resume data
     * 
     * @param candidate Candidate to validate
     * @throws CandidateDataIncompleteException if resume data is missing/empty
     */
    @Override
    public void validate(Candidate candidate) {
        if (candidate.getResumeData() == null || candidate.getResumeData().isEmpty()) {
            throw new com.yourname.myapp.recruitment.exception.CandidateDataIncompleteException("Resume data is required.");
        }
        // Pass to next validator in chain
        if (next != null) next.validate(candidate);
    }
}
