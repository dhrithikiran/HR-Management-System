package com.yourname.myapp.performance.service;

import com.yourname.myapp.exception.EmployeeNotFoundException;
import com.yourname.myapp.performance.entity.Appraisal;
import com.yourname.myapp.performance.exception.AppraisalDeadlineMissedException;
import com.yourname.myapp.performance.repository.AppraisalRepository;
import com.yourname.myapp.performance.repository.AppraisalRepositoryImpl;
import com.yourname.myapp.repository.EmployeeRepository;
import com.yourname.myapp.repository.EmployeeRepositoryImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

public class AppraisalService implements AppraisalServiceContract {

    private final AppraisalRepository appraisalRepository = new AppraisalRepositoryImpl();
    private final EmployeeRepository employeeRepository = new EmployeeRepositoryImpl();

    @Override
    public List<Appraisal> getAllAppraisals(String role) {
        appraisalRepository.deleteOrphanedAppraisals();
        return appraisalRepository.findAll();
    }

    @Override
    public Appraisal getById(String role, String id) {
        Appraisal appraisal = appraisalRepository.findById(id);
        if (appraisal == null) {
            throw new NoSuchElementException("Appraisal not found: " + id);
        }
        validateDeadline(appraisal);
        return appraisal;
    }

    @Override
    public Appraisal createAppraisal(String role, Appraisal appraisal) {
        if (appraisal.getEmployeeId() == null || appraisal.getEmployeeId().isBlank()) {
            throw new IllegalArgumentException("Employee ID is required");
        }
        if (!employeeRepository.existsById(appraisal.getEmployeeId())) {
            throw new EmployeeNotFoundException("Employee not found: " + appraisal.getEmployeeId());
        }
        if (appraisal.getRating() < 0 || appraisal.getRating() > 5) {
            throw new IllegalArgumentException("Rating should be between 0 and 5");
        }

        long next = appraisalRepository.countAll() + 1;
        appraisal.setAppraiseId(String.format("APR-%03d", next));
        if (appraisal.getAppraisalStatus() == null) {
            appraisal.setAppraisalStatus(Appraisal.AppraisalStatus.PENDING);
        }
        if (appraisal.getDeadlineDate() == null) {
            appraisal.setDeadlineDate(LocalDate.now().plusDays(30));
        }
        appraisal.setLocked(false);
        validateDeadline(appraisal);
        appraisalRepository.save(appraisal);
        return appraisal;
    }

    @Override
    public Appraisal updateAppraisal(String role, String id, Appraisal request) {
        Appraisal existing = getById(role, id);
        if (existing.isLocked()) {
            throw new AppraisalDeadlineMissedException("Appraisal is locked due to missed deadline: " + id);
        }

        if (request.getRating() >= 0 && request.getRating() <= 5) {
            existing.setRating(request.getRating());
        }
        if (request.getFeedback() != null) {
            existing.setFeedback(request.getFeedback());
        }
        if (request.getAppraisalStatus() != null) {
            existing.setAppraisalStatus(request.getAppraisalStatus());
        }
        if (request.getDeadlineDate() != null) {
            existing.setDeadlineDate(request.getDeadlineDate());
        }

        validateDeadline(existing);
        appraisalRepository.update(existing);
        return existing;
    }

    private void validateDeadline(Appraisal appraisal) {
        if (appraisal.getAppraisalStatus() == Appraisal.AppraisalStatus.PENDING
                && appraisal.getDeadlineDate() != null
                && LocalDate.now().isAfter(appraisal.getDeadlineDate())) {
            appraisal.setLocked(true);
            appraisalRepository.update(appraisal);
            throw new AppraisalDeadlineMissedException(
                    "Deadline missed and appraisal locked: " + appraisal.getAppraiseId()
            );
        }
    }
}
