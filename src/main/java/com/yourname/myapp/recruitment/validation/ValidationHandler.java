package com.yourname.myapp.recruitment.validation;

import com.yourname.myapp.recruitment.entity.Candidate;

/**
 * ValidationHandler - Abstract base class for Chain of Responsibility pattern
 * 
 * Purpose:
 * Provides a framework for implementing a chain of validators that process candidate data
 * Each validator can pass control to the next validator in the chain
 * 
 * Chain Structure:
 * ContactInfoValidator → ResumeValidator → DuplicateCheckHandler
 * 
 * Usage:
 * 1. Create validator instances
 * 2. Link them using setNext()
 * 3. Call validate() on the first validator to start chain
 * 
 * Pattern: Chain of Responsibility
 * - Each handler knows only about the next handler
 * - Request passes through chain until handled/rejected
 * - Promotes loose coupling between validators
 * 
 * @author OOAD Project
 * @version 1.0
 * @since 2024
 */
public abstract class ValidationHandler {
    /** Reference to next handler in chain */
    protected ValidationHandler next;

    /**
     * Set the next handler in the validation chain
     * 
     * @param next Next ValidationHandler to call after this one
     * @return The next handler (for fluent chaining: h1.setNext(h2).setNext(h3))
     */
    public ValidationHandler setNext(ValidationHandler next) {
        this.next = next;
        return next;
    }

    /**
     * Validate the candidate and pass to next handler if validation passes
     * 
     * Must be implemented by each validator with specific validation logic
     * Throws CandidateDataIncompleteException if validation fails
     * 
     * @param candidate Candidate to validate
     * @throws CandidateDataIncompleteException if validation fails
     */
    public abstract void validate(Candidate candidate);
}
