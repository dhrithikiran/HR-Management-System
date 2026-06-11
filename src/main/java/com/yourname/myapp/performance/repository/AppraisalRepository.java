package com.yourname.myapp.performance.repository;

import com.yourname.myapp.performance.entity.Appraisal;

import java.util.List;

public interface AppraisalRepository {
    List<Appraisal> findAll();
    Appraisal findById(String id);
    void save(Appraisal appraisal);
    void update(Appraisal appraisal);
    int deleteByEmployeeId(String employeeId);
    int deleteOrphanedAppraisals();
    long countByStatus(Appraisal.AppraisalStatus status);
    long countAll();
}
