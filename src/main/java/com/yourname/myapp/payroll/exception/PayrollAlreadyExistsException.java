package com.yourname.myapp.payroll.exception;

public class PayrollAlreadyExistsException extends RuntimeException {

    public PayrollAlreadyExistsException(String employeeId, String month, int year) {
        super("Payroll already exists for employee " + employeeId +
              " for " + month + " " + year);
    }
}