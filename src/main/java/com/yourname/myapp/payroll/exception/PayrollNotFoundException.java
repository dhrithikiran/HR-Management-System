package com.yourname.myapp.payroll.exception;

public class PayrollNotFoundException extends RuntimeException {

    public PayrollNotFoundException(String employeeId) {
        super("Payroll not found for employee: " + employeeId);
    }
}