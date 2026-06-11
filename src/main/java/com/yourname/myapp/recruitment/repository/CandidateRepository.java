package com.yourname.myapp.recruitment.repository;

import com.yourname.myapp.recruitment.entity.Candidate;
import java.util.List;

/**
 * CandidateRepository - Data access interface for Candidate entities
 * 
 * Purpose: Define contract for candidate persistence operations
 * This interface abstracts database operations from business logic
 * 
 * Pattern: Repository Pattern
 * - Provides abstraction over data access logic
 * - Enables easy testing with mock implementations
 * - Separates domain logic from persistence logic
 * 
 * Implementation: CandidateRepositoryImpl (Hibernate-based)
 * 
 * @author OOAD Project
 * @version 1.0
 * @since 2024
 */
public interface CandidateRepository {
    /**
     * Find all candidates with optional status filter
     * 
     * @param status Status filter (null/"ALL" for all candidates, or specific ApplicationStatus)
     * @return List of candidates matching status
     */
    List<Candidate> findAllByStatus(String status);
    
    /**
     * Find candidate by ID
     * 
     * @param id Candidate ID (CND-XXX format)
     * @return Candidate if found, null otherwise
     */
    Candidate findById(String id);
    
    /**
     * Save new candidate to database
     * 
     * @param candidate Candidate entity to save
     */
    void save(Candidate candidate);
    
    /**
     * Update existing candidate in database
     * 
     * @param candidate Candidate entity to update (must have ID)
     */
    void update(Candidate candidate);
    
    /**
     * Delete candidate from database
     * 
     * @param id Candidate ID to delete
     */
    void delete(String id);
    
    /**
     * Count candidates by specific status
     * 
     * @param status ApplicationStatus to count
     * @return Number of candidates with that status
     */
    long countByStatus(String status);
    
    /**
     * Count total candidates in database
     * 
     * @return Total candidate count
     */
    long countAll();
}
