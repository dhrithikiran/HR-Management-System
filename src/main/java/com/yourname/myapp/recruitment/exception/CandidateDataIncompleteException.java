package com.yourname.myapp.recruitment.exception;

/**
 * CandidateDataIncompleteException - Custom exception for recruitment validation errors
 * 
 * Purpose: Thrown when candidate data fails validation during the validation chain
 * 
 * When Thrown:
 * 1. Contact info is missing or empty (ContactInfoValidator)
 * 2. Resume data is missing or empty (ResumeValidator)
 * 3. Duplicate candidate detected (DuplicateCheckHandler)
 * 4. Other candidate data integrity issues
 * 
 * Exception Hierarchy:
 * Extends RuntimeException (unchecked exception) for easier handling
 * Allows validation errors to propagate without forcing try-catch
 * 
 * Exception Handling Flow:
 * 1. Thrown from validation chain in CandidateService
 * 2. Caught by CandidateService caller
 * 3. Error message displayed via UI dialog (JOptionPane)
 * 4. User sees descriptive message: "Contact info is required." etc.
 * 
 * @author OOAD Project
 * @version 1.0
 * @since 2024
 */
public class CandidateDataIncompleteException extends RuntimeException {
    /**
     * Construct exception with descriptive message
     * 
     * @param message Error message describing what validation failed
     */
    public CandidateDataIncompleteException(String message) {
        super(message);
    }
}
