package com.yourname.myapp.recruitment.validation;

import com.yourname.myapp.recruitment.entity.Candidate;

/**
 * ContactInfoValidator - First stage of validation chain
 * 
 * Purpose: Validates that candidate has valid contact information
 * 
 * Validation Rules:
 * - Contact info cannot be null
 * - Contact info cannot be empty string
 * - Should contain valid email or phone format (optional enhancement)
 * 
 * Chain Position: FIRST (initial validator in chain)
 * Next validator: ResumeValidator
 * 
 * Error Handling: Throws CandidateDataIncompleteException if validation fails
 * 
 * @author OOAD Project
 * @version 1.0
 * @since 2024
 */
public class ContactInfoValidator extends ValidationHandler {
    /**
     * Validate candidate's contact information
     * 
     * @param candidate Candidate to validate
     * @throws CandidateDataIncompleteException if contact info is missing/empty
     */
    @Override
    public void validate(Candidate candidate) {
        if (candidate.getContactInfo() == null || candidate.getContactInfo().isEmpty()) {
            throw new com.yourname.myapp.recruitment.exception.CandidateDataIncompleteException("Contact info is required.");
        }
        // Pass to next validator in chain
        if (next != null) next.validate(candidate);
    }
}
