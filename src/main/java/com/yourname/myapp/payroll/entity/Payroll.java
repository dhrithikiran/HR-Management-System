package com.yourname.myapp.payroll.entity;

import java.math.BigDecimal;
import com.yourname.myapp.entity.Employee;

/**
 * Payroll POJO - Payroll records per employee per month
 * 
 * Maps to: payroll table
 * Columns: payroll_id, employee_id, role, gross_salary, deductions, net_pay, current_month_total, salary_transfer_record, month, year
 */
public class Payroll {

    private Long payrollId;
    private Employee employee;
    private String role;
    private BigDecimal grossSalary;
    private BigDecimal deductions;
    private BigDecimal netPay;
    private BigDecimal currentMonthTotal;
    private String salaryTransferRecord;
    private String month;
    private int year;
    public Payroll() {}

    public Payroll(Employee employee, String month, int year) {
        this.employee = employee;
        this.role = employee.getJobRole();
        this.month = month;
        this.year = year;
    }

    // ✅ ALL GETTERS + SETTERS (MANDATORY)

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public BigDecimal getGrossSalary() { return grossSalary; }
    public void setGrossSalary(BigDecimal grossSalary) { this.grossSalary = grossSalary; }

    public BigDecimal getDeductions() { return deductions; }
    public void setDeductions(BigDecimal deductions) { this.deductions = deductions; }

    public BigDecimal getNetPay() { return netPay; }
    public void setNetPay(BigDecimal netPay) { this.netPay = netPay; }

    public BigDecimal getCurrentMonthTotal() { return currentMonthTotal; }
    public void setCurrentMonthTotal(BigDecimal currentMonthTotal) { this.currentMonthTotal = currentMonthTotal; }

    public String getSalaryTransferRecord() { return salaryTransferRecord; }
    public void setSalaryTransferRecord(String salaryTransferRecord) { this.salaryTransferRecord = salaryTransferRecord; }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
}