package com.yourname.myapp.performance.service;

import com.yourname.myapp.performance.entity.Appraisal;
import com.yourname.myapp.performance.repository.AppraisalRepository;
import com.yourname.myapp.performance.repository.AppraisalRepositoryImpl;
import com.yourname.myapp.performance.repository.PromotionRepository;
import com.yourname.myapp.performance.repository.PromotionRepositoryImpl;

import java.util.HashMap;
import java.util.Map;

public class PerformanceStatsService {

    private final AppraisalRepository appraisalRepository = new AppraisalRepositoryImpl();
    private final PromotionRepository promotionRepository = new PromotionRepositoryImpl();

    public Map<String, Object> getPerformanceStats() {
        Map<String, Object> stats = new HashMap<>();
        long completed = appraisalRepository.countByStatus(Appraisal.AppraisalStatus.COMPLETED);
        long pending = appraisalRepository.countByStatus(Appraisal.AppraisalStatus.PENDING);
        long promotionsRecommended = promotionRepository.countAll();

        stats.put("completedReviews", completed);
        stats.put("pendingReviews", pending);
        stats.put("promotionsRecommended", promotionsRecommended);
        return stats;
    }
}
