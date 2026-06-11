package com.yourname.myapp.service;

import com.yourname.myapp.adapter.BenefitPlan;
import com.yourname.myapp.adapter.HealthPlanAdapter;
import com.yourname.myapp.adapter.InsurancePlanAdapter;
import com.yourname.myapp.entity.BenefitEnrollment;
import com.yourname.myapp.repository.BenefitEnrollmentRepository;
import java.util.List;
import java.util.Optional;

public class BenefitService {

    private final BenefitEnrollmentRepository repo = new BenefitEnrollmentRepository();

    public List<BenefitEnrollment> getAllEnrollments() {
        return repo.findAll();
    }

    public List<BenefitEnrollment> getByEmployeeId(String employeeId) {
        return repo.findByEmployeeId(employeeId);
    }

    public void createEnrollment(String employeeId, String enrollmentStatus,
                                  String healthPlan, String insurancePlan) {
        // Check if employee already has an enrollment
        List<BenefitEnrollment> existing = repo.findByEmployeeId(employeeId);
        if (!existing.isEmpty()) {
            throw new RuntimeException("Employee " + employeeId + " already has a benefit enrollment. Use Update instead.");
        }

        BenefitEnrollment enrollment = new BenefitEnrollment();
        enrollment.setEmployeeId(employeeId);
        enrollment.setEnrollmentStatus(BenefitEnrollment.EnrollmentStatus.valueOf(enrollmentStatus));
        enrollment.setHealthPlan(healthPlan);
        enrollment.setInsurancePlan(insurancePlan);
        repo.save(enrollment);
    }

    public void updateEnrollment(Long id, String enrollmentStatus,
                                  String healthPlan, String insurancePlan) {
        BenefitEnrollment enrollment = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Enrollment not found with id: " + id));
        enrollment.setEnrollmentStatus(BenefitEnrollment.EnrollmentStatus.valueOf(enrollmentStatus));
        enrollment.setHealthPlan(healthPlan);
        enrollment.setInsurancePlan(insurancePlan);
        repo.save(enrollment);
    }

    // Use Adapter pattern to get enriched plan details
    public BenefitPlan getHealthPlanDetails(String employeeId) {
        List<BenefitEnrollment> enrollments = repo.findByEmployeeId(employeeId);
        String rawPlan = enrollments.isEmpty() ? null : enrollments.get(0).getHealthPlan();
        return new HealthPlanAdapter(rawPlan);
    }

    public BenefitPlan getInsurancePlanDetails(String employeeId) {
        List<BenefitEnrollment> enrollments = repo.findByEmployeeId(employeeId);
        String rawPlan = enrollments.isEmpty() ? null : enrollments.get(0).getInsurancePlan();
        return new InsurancePlanAdapter(rawPlan);
    }
}
