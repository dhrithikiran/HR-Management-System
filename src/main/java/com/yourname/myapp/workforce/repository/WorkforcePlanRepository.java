package com.yourname.myapp.workforce.repository;

import com.yourname.myapp.workforce.entity.WorkforcePlan;

import java.util.List;

public interface WorkforcePlanRepository {
    List<WorkforcePlan> findAll();
    List<WorkforcePlan> findByQuarter(String quarter);
    WorkforcePlan findById(Long id);
    void save(WorkforcePlan plan);
    void update(WorkforcePlan plan);
    boolean deleteById(Long id);
}
