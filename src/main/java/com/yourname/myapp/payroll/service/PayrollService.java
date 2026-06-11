package com.yourname.myapp.payroll.service;

import com.yourname.myapp.payroll.entity.Payroll;

import java.util.List;

public interface PayrollService {

    Payroll generatePayroll(String employeeId);

    void savePayroll(Payroll payroll);
    void updatePayroll(Payroll payroll);
    void generatePayrollForMonth(String month, int year);
    Payroll getPayrollByEmployeeId(String employeeId);

    List<Payroll> getAllPayrolls();
    List<Payroll> generatePayrollForAllEmployees();
    Payroll getPayrollByEmployeeAndMonth(String employeeId, String month, int year);
}