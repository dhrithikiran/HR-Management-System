package com.yourname.myapp.performance.service;

import com.yourname.myapp.exception.EmployeeNotFoundException;
import com.yourname.myapp.performance.entity.Promotion;
import com.yourname.myapp.performance.repository.PromotionRepository;
import com.yourname.myapp.performance.repository.PromotionRepositoryImpl;
import com.yourname.myapp.repository.EmployeeRepository;
import com.yourname.myapp.repository.EmployeeRepositoryImpl;

import java.time.LocalDate;
import java.util.List;

public class PromotionService {

    private final PromotionRepository promotionRepository = new PromotionRepositoryImpl();
    private final EmployeeRepository employeeRepository = new EmployeeRepositoryImpl();

    public List<Promotion> getAllPromotions() {
        promotionRepository.deleteOrphanedPromotions();
        return promotionRepository.findAll();
    }

    public Promotion recommendPromotion(Promotion promotion) {
        if (promotion.getEmployeeId() == null || promotion.getEmployeeId().isBlank()) {
            throw new IllegalArgumentException("Employee ID is required");
        }
        if (!employeeRepository.existsById(promotion.getEmployeeId())) {
            throw new EmployeeNotFoundException("Employee not found: " + promotion.getEmployeeId());
        }
        if (promotion.getNewRole() == null || promotion.getNewRole().isBlank()) {
            throw new IllegalArgumentException("New role is required");
        }
        if (promotion.getEffectiveDate() == null) {
            promotion.setEffectiveDate(LocalDate.now().plusDays(7));
        }

        long next = promotionRepository.countAll() + 1;
        promotion.setPromotionId(String.format("PRO-%03d", next));
        promotionRepository.save(promotion);
        return promotion;
    }
}
