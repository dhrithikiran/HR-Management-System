package com.yourname.myapp.performance.service;

import com.yourname.myapp.performance.entity.Appraisal;

import java.util.List;

public interface AppraisalServiceContract {
    List<Appraisal> getAllAppraisals(String role);
    Appraisal getById(String role, String id);
    Appraisal createAppraisal(String role, Appraisal appraisal);
    Appraisal updateAppraisal(String role, String id, Appraisal appraisal);
}
