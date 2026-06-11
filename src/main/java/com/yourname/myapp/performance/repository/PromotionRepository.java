package com.yourname.myapp.performance.repository;

import com.yourname.myapp.performance.entity.Promotion;

import java.util.List;

public interface PromotionRepository {
    List<Promotion> findAll();
    void save(Promotion promotion);
    int deleteByEmployeeId(String employeeId);
    int deleteOrphanedPromotions();
    long countAll();
}
