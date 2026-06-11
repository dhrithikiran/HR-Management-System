package com.yourname.myapp.onboarding.service;

import com.yourname.myapp.recruitment.entity.Candidate;
import com.yourname.myapp.recruitment.repository.CandidateRepository;
import com.yourname.myapp.recruitment.repository.CandidateRepositoryImpl;

import com.yourname.myapp.onboarding.command.*;
import com.yourname.myapp.onboarding.entity.OnboardingRecord;
import com.yourname.myapp.onboarding.repository.OnboardingRepository;
import com.yourname.myapp.onboarding.repository.OnboardingRepositoryImpl;

import com.yourname.myapp.onboarding.exception.*;

import java.util.*;

public class OnboardingServiceImpl implements OnboardingService {

    private final OnboardingRepository repository = new OnboardingRepositoryImpl();
    private final CommandInvoker invoker = new CommandInvoker();

    private final CandidateRepository candidateRepository = new CandidateRepositoryImpl();

    @Override
    public List<OnboardingRecord> getAllRecords() {
        return repository.findAll();
    }

    @Override
    public OnboardingRecord getById(String id) {
        OnboardingRecord record = repository.findById(id);
        if (record == null) {
            throw new NoSuchElementException("Onboarding record not found: " + id);
        }
        return record;
    }

    @Override
    public OnboardingRecord createRecord(String candidateId) {

        if (candidateId == null || candidateId.isBlank()) {
            throw new IllegalArgumentException("Candidate ID cannot be empty");
        }

        Candidate candidate = candidateRepository.findById(candidateId);

        if (candidate == null) {
            throw new RuntimeException("Candidate does not exist with ID: " + candidateId);
        }

        if (candidate.getApplicationStatus() != Candidate.ApplicationStatus.SELECTED) {
            throw new IllegalStateException("Only SELECTED candidates can be onboarded");
        }

        OnboardingRecord record = new OnboardingRecord();

        long count = repository.countAll() + 1;
        record.setOnboardingId(String.format("ONB-%03d", count));

        // Store candidate ID to track which candidate this onboarding is for
        // assigned_employee_id will be set later when employee is created after onboarding approval
        record.setAssignedEmployeeId(candidateId);
        record.setEmployeeName(candidate.getCandidateName());

        record.setPipelineStatus(OnboardingRecord.PipelineStatus.EMPLOYEE_ASSIGNED);
        record.setBackgroundCheckStatus(OnboardingRecord.BackgroundCheckStatus.PENDING);
        record.setDocumentVerificationStatus(OnboardingRecord.DocumentVerificationStatus.PENDING);
        record.setVerifiedRecord(false);

        repository.save(record);
        return record;
    }

    @Override
    public void updateBackgroundCheck(String id, String status) {

        OnboardingRecord record = getById(id);

        if (record.isVerifiedRecord()) {
            throw new InvalidCandidateOnboardingStateException("Already finalized");
        }

        invoker.executeCommand(
                new UpdateBackgroundCheckCommand(
                        record,
                        OnboardingRecord.BackgroundCheckStatus.valueOf(status),
                        repository
                )
        );
    }

    @Override
    public void updateDocumentVerification(String id, String status) {

        OnboardingRecord record = getById(id);

        if (record.isVerifiedRecord()) {
            throw new InvalidCandidateOnboardingStateException("Already finalized");
        }

        invoker.executeCommand(
                new UpdateDocumentVerificationCommand(
                        record,
                        OnboardingRecord.DocumentVerificationStatus.valueOf(status),
                        repository
                )
        );
    }

    @Override
    public void approveOnboarding(String id) {

        OnboardingRecord record = getById(id);

        // 🚨 FIX: block only final ACTIVE state, not VERIFIED
        if (record.getPipelineStatus() ==
                OnboardingRecord.PipelineStatus.ACTIVE_ONBOARDING) {
            throw new OnboardingAlreadyVerifiedException(id);
        }

        invoker.executeCommand(
                new ApproveOnboardingCommand(record, repository)
        );

        // 👇 Employee creation ONLY after ACTIVE
        if (record.getPipelineStatus() ==
                OnboardingRecord.PipelineStatus.ACTIVE_ONBOARDING) {

            Candidate candidate =
                    candidateRepository.findById(record.getAssignedEmployeeId());

            if (candidate != null) {
                com.yourname.myapp.dto.EmployeeRequest req =
                        new com.yourname.myapp.dto.EmployeeRequest();

                req.setEmployeeName(candidate.getCandidateName());
                req.setDepartment("DEFAULT");
                req.setJobRole("NEW_JOINER");

                new com.yourname.myapp.service.EmployeeService()
                        .createEmployee(req);
            }
        }
    }

    @Override
    public void deleteRecord(String id) {
        getById(id);
        repository.delete(id);
    }

    @Override
    public Map<String, Object> getOnboardingStats() {

        List<OnboardingRecord> records = repository.findAll();

        long total = records.size();

        long active = records.stream()
                .filter(r -> r.getPipelineStatus()
                        == OnboardingRecord.PipelineStatus.ACTIVE_ONBOARDING)
                .count();

        long verified = records.stream()
                .filter(r -> r.getPipelineStatus()
                        == OnboardingRecord.PipelineStatus.VERIFIED)
                .count();

        long pending = records.stream()
                .filter(r -> r.getPipelineStatus()
                        == OnboardingRecord.PipelineStatus.EMPLOYEE_ASSIGNED)
                .count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRecords", total);
        stats.put("activeOnboarding", active);
        stats.put("verified", verified);
        stats.put("pending", pending);

        return stats;
    }
}