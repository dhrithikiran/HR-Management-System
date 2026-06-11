package com.yourname.myapp.payroll.repository;

import com.yourname.myapp.payroll.entity.Payroll;

import java.util.List;

public interface PayrollRepository {

    void save(Payroll payroll);
    void update(Payroll payroll);

    List<Payroll> findAll();

    Payroll findByEmployeeId(String employeeId);
    Payroll findByEmployeeAndMonth(String employeeId, String month, int year);
}