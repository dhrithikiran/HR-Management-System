package com.yourname.myapp.payroll.exception;

public class EmployeeNotFoundException extends RuntimeException {

    public EmployeeNotFoundException(String employeeId) {
        super("Employee not found: " + employeeId);
    }
}