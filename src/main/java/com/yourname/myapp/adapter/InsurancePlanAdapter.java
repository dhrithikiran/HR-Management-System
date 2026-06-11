package com.yourname.myapp.adapter;

/**
 * Adapter Pattern — wraps raw insurancePlan String into BenefitPlan interface.
 */
public class InsurancePlanAdapter implements BenefitPlan {

    private final String rawInsurancePlan;

    public InsurancePlanAdapter(String rawInsurancePlan) {
        this.rawInsurancePlan = rawInsurancePlan != null ? rawInsurancePlan : "No Plan";
    }

    @Override
    public String getPlanName() {
        return "Insurance Plan: " + rawInsurancePlan;
    }

    @Override
    public String getCoverageDetails() {
        if (rawInsurancePlan == null || rawInsurancePlan.isEmpty()) {
            return "No insurance coverage selected.";
        }
        return "Insurance coverage includes life and accidental coverage under plan: " + rawInsurancePlan;
    }
}
