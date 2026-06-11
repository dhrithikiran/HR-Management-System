package com.yourname.myapp.adapter;

/**
 * Adapter Pattern — wraps raw healthPlan String into BenefitPlan interface.
 */
public class HealthPlanAdapter implements BenefitPlan {

    private final String rawHealthPlan;

    public HealthPlanAdapter(String rawHealthPlan) {
        this.rawHealthPlan = rawHealthPlan != null ? rawHealthPlan : "No Plan";
    }

    @Override
    public String getPlanName() {
        return "Health Plan: " + rawHealthPlan;
    }

    @Override
    public String getCoverageDetails() {
        if (rawHealthPlan == null || rawHealthPlan.isEmpty()) {
            return "No health coverage selected.";
        }
        return "Health coverage includes medical, dental, and vision under plan: " + rawHealthPlan;
    }
}
