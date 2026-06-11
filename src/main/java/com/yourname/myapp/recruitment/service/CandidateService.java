package com.yourname.myapp.recruitment.service;

import com.yourname.myapp.recruitment.entity.Candidate;
import java.util.List;
import java.util.Map;

public interface CandidateService {
    List<Candidate> getAllCandidates(String status);
    Candidate getCandidateById(String id);
    Candidate createCandidate(Candidate candidate);
    Candidate updateCandidate(String id, Candidate candidate);
    Candidate updateStatus(String id, String newStatus);
    void deleteCandidate(String id);
    Map<String, Object> getRecruitmentStats();
}
