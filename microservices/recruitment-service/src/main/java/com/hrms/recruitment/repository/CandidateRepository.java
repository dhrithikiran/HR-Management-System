package com.hrms.recruitment.repository;

import com.hrms.recruitment.model.Candidate;

import java.util.List;
import java.util.Optional;

public interface CandidateRepository {
    List<Candidate> findAll();
    List<Candidate> findAllByStatus(String status);
    Optional<Candidate> findById(String id);
    Candidate save(Candidate candidate);
    Candidate update(Candidate candidate);
    boolean delete(String id);
    long countAll();
}
