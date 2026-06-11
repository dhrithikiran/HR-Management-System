package com.yourname.myapp.performance.entity;

import java.time.LocalDate;

/**
 * Appraisal POJO - Employee performance appraisals
 * 
 * Maps to: appraisal table
 * Columns: appraise_id, employee_id, rating, feedback, appraisal_status, deadline_date, locked
 */
public class Appraisal {

    private String appraiseId;
    private String employeeId;
    private double rating;
    private String feedback;
    private AppraisalStatus appraisalStatus;
    private LocalDate deadlineDate;
    private boolean locked;

    public enum AppraisalStatus {
        PENDING,
        COMPLETED
    }

    public String getAppraiseId() {
        return appraiseId;
    }

    public void setAppraiseId(String appraiseId) {
        this.appraiseId = appraiseId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public AppraisalStatus getAppraisalStatus() {
        return appraisalStatus;
    }

    public void setAppraisalStatus(AppraisalStatus appraisalStatus) {
        this.appraisalStatus = appraisalStatus;
    }

    public LocalDate getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(LocalDate deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
