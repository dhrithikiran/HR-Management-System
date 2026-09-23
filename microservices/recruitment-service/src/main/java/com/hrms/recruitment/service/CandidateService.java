package com.hrms.recruitment.service;

import com.hrms.recruitment.model.Candidate;
import com.hrms.recruitment.model.CandidateRequest;
import com.hrms.recruitment.model.CandidateSelectionResponse;

import java.util.List;
import java.util.Optional;

public interface CandidateService {
    Candidate createCandidate(CandidateRequest request);
    Optional<Candidate> getCandidateById(String id);
    List<Candidate> getAllCandidates(String status);
    CandidateSelectionResponse selectCandidate(String id);
    boolean deleteCandidate(String id);
}
