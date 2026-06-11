package com.yourname.myapp.payroll.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class SalaryConfigSingleton {

    private static SalaryConfigSingleton instance;

    private Map<String, BigDecimal> salaryMap = new HashMap<>();
    private Map<String, BigDecimal> deductionMap = new HashMap<>();

    private SalaryConfigSingleton() {

        salaryMap.put("Manager", new BigDecimal("100000"));
        salaryMap.put("Developer", new BigDecimal("80000"));
        salaryMap.put("Analyst", new BigDecimal("70000"));
        salaryMap.put("Consultant", new BigDecimal("90000"));
        salaryMap.put("Coordinator", new BigDecimal("50000"));
        salaryMap.put("Executive", new BigDecimal("60000"));
        salaryMap.put("Other", new BigDecimal("40000"));

        deductionMap.put("Manager", new BigDecimal("10000"));
        deductionMap.put("Developer", new BigDecimal("8000"));
        deductionMap.put("Analyst", new BigDecimal("7000"));
        deductionMap.put("Consultant", new BigDecimal("9000"));
        deductionMap.put("Coordinator", new BigDecimal("5000"));
        deductionMap.put("Executive", new BigDecimal("6000"));
        deductionMap.put("Other", new BigDecimal("4000"));
    }

    public static SalaryConfigSingleton getInstance() {
        if (instance == null) {
            instance = new SalaryConfigSingleton();
        }
        return instance;
    }

    public BigDecimal getSalary(String role) {
        return salaryMap.getOrDefault(role, BigDecimal.ZERO);
    }

    public BigDecimal getDeduction(String role) {
        return deductionMap.getOrDefault(role, BigDecimal.ZERO);
    }
}