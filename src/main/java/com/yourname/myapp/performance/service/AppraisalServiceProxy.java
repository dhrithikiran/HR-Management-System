package com.yourname.myapp.performance.service;

import com.yourname.myapp.performance.entity.Appraisal;
import com.yourname.myapp.performance.exception.UnauthorizedAccessException;

import java.util.List;

public class AppraisalServiceProxy implements AppraisalServiceContract {

    private final AppraisalServiceContract delegate;

    public AppraisalServiceProxy(AppraisalServiceContract delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<Appraisal> getAllAppraisals(String role) {
        authorize(role);
        return delegate.getAllAppraisals(role);
    }

    @Override
    public Appraisal getById(String role, String id) {
        authorize(role);
        return delegate.getById(role, id);
    }

    @Override
    public Appraisal createAppraisal(String role, Appraisal appraisal) {
        authorize(role);
        return delegate.createAppraisal(role, appraisal);
    }

    @Override
    public Appraisal updateAppraisal(String role, String id, Appraisal appraisal) {
        authorize(role);
        return delegate.updateAppraisal(role, id, appraisal);
    }

    private void authorize(String role) {
        if (role == null) {
            throw new UnauthorizedAccessException("Role is required");
        }
        String normalized = role.trim().toUpperCase();
        if (!"MANAGER".equals(normalized) && !"ADMIN".equals(normalized)) {
            throw new UnauthorizedAccessException("Access denied for role: " + role);
        }
    }
}
