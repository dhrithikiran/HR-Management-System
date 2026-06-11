package com.yourname.myapp.workforce.service;

import com.yourname.myapp.workforce.entity.WorkforcePlan;
import com.yourname.myapp.workforce.repository.WorkforcePlanRepository;
import com.yourname.myapp.workforce.repository.WorkforcePlanRepositoryImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WorkforcePlanService {

    private final WorkforcePlanRepository workforcePlanRepository = new WorkforcePlanRepositoryImpl();

    public List<WorkforcePlan> getAllPlans(String quarter) {
        if (quarter == null || quarter.isBlank()) {
            return workforcePlanRepository.findAll();
        }
        return workforcePlanRepository.findByQuarter(quarter);
    }

    public WorkforcePlan createPlan(WorkforcePlan plan) {
        validatePlan(plan);
        workforcePlanRepository.save(plan);
        return plan;
    }

    public WorkforcePlan updatePlan(Long id, WorkforcePlan request) {
        WorkforcePlan existing = workforcePlanRepository.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Workforce plan not found: " + id);
        }

        existing.setDepartment(request.getDepartment());
        existing.setOpenPositions(request.getOpenPositions());
        existing.setHiringForecast(request.getHiringForecast());
        existing.setHrCostProjections(request.getHrCostProjections());
        existing.setQuarter(request.getQuarter());
        existing.setTotalBudget(request.getTotalBudget());
        validatePlan(existing);
        workforcePlanRepository.update(existing);
        return existing;
    }

    public void deletePlan(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Plan ID is required");
        }
        WorkforcePlan existing = workforcePlanRepository.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Workforce plan not found: " + id);
        }
        boolean deleted = workforcePlanRepository.deleteById(id);
        if (!deleted) {
            throw new IllegalStateException("Failed to delete workforce plan: " + id);
        }
    }

    public Map<String, Object> getStats() {
        List<WorkforcePlan> plans = workforcePlanRepository.findAll();

        int totalOpenPositions = plans.stream().mapToInt(WorkforcePlan::getOpenPositions).sum();
        int totalHiringForecast = plans.stream().mapToInt(WorkforcePlan::getHiringForecast).sum();

        BigDecimal totalHrCostProjections = plans.stream()
                .map(WorkforcePlan::getHrCostProjections)
                .filter(v -> v != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalBudget = plans.stream()
                .map(WorkforcePlan::getTotalBudget)
                .filter(v -> v != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal budgetUtilization = BigDecimal.ZERO;
        if (totalBudget.compareTo(BigDecimal.ZERO) > 0) {
            budgetUtilization = totalHrCostProjections
                    .multiply(BigDecimal.valueOf(100))
                    .divide(totalBudget, 2, RoundingMode.HALF_UP);
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("budgetUtilization", budgetUtilization);
        stats.put("totalOpenPositions", totalOpenPositions);
        stats.put("totalHiringForecast", totalHiringForecast);
        stats.put("totalHrCostProjections", totalHrCostProjections);
        stats.put("BUDGET_THRESHOLD_EXCEEDED", budgetUtilization.compareTo(BigDecimal.valueOf(80)) > 0);
        return stats;
    }

    public String generateReport(String quarter) {
        List<WorkforcePlan> plans = getAllPlans(quarter);
        String header = "Department | Quarter | Open Positions | Hiring Forecast | HR Cost Projections | Total Budget";
        String body = plans.stream()
                .map(plan -> String.format(
                        "%s | %s | %d | %d | %s | %s",
                        plan.getDepartment(),
                        plan.getQuarter(),
                        plan.getOpenPositions(),
                        plan.getHiringForecast(),
                        plan.getHrCostProjections(),
                        plan.getTotalBudget()
                ))
                .collect(Collectors.joining("\n"));
        return header + "\n" + body;
    }

    public String exportReport(String quarter) {
        List<WorkforcePlan> plans = getAllPlans(quarter);
        String csv = "department,quarter,openPositions,hiringForecast,hrCostProjections,totalBudget\n" +
                plans.stream()
                        .map(plan -> String.join(",",
                                safe(plan.getDepartment()),
                                safe(plan.getQuarter()),
                                String.valueOf(plan.getOpenPositions()),
                                String.valueOf(plan.getHiringForecast()),
                                String.valueOf(plan.getHrCostProjections()),
                                String.valueOf(plan.getTotalBudget())
                        ))
                        .collect(Collectors.joining("\n"));

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        Path exportPath = Path.of(System.getProperty("user.home"), "workforce_report_" + timestamp + ".csv");
        try {
            Files.writeString(exportPath, csv);
            return exportPath.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to export report", e);
        }
    }

    public List<WorkforcePlan> clonePlanFromPreviousQuarter(String sourceQuarter, String targetQuarter) {
        if (sourceQuarter == null || sourceQuarter.isBlank() || targetQuarter == null || targetQuarter.isBlank()) {
            throw new IllegalArgumentException("Source and target quarter are required");
        }
        List<WorkforcePlan> source = workforcePlanRepository.findByQuarter(sourceQuarter);
        return source.stream().map(plan -> {
            WorkforcePlan cloned = plan.clone();
            cloned.setQuarter(targetQuarter);
            workforcePlanRepository.save(cloned);
            return cloned;
        }).collect(Collectors.toList());
    }

    private void validatePlan(WorkforcePlan plan) {
        if (plan.getDepartment() == null || plan.getDepartment().isBlank()) {
            throw new IllegalArgumentException("Department is required");
        }
        if (plan.getQuarter() == null || plan.getQuarter().isBlank()) {
            throw new IllegalArgumentException("Quarter is required");
        }
        if (plan.getHrCostProjections() == null || plan.getTotalBudget() == null) {
            throw new IllegalArgumentException("HR cost projections and total budget are required");
        }
    }

    private String safe(String input) {
        return input == null ? "" : input.replace(",", " ");
    }
}
