package com.yourname.myapp.adapter;

/**
 * Adapter Pattern (Structural)
 *
 * BenefitPlan is the unified interface.
 * HealthPlanAdapter and InsurancePlanAdapter wrap raw String plan fields
 * into this interface — used in BenefitService when returning enrollment details.
 */
public interface BenefitPlan {
    String getPlanName();
    String getCoverageDetails();
}
