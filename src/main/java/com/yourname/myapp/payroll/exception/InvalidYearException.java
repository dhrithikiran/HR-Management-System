package com.yourname.myapp.payroll.exception;

public class InvalidYearException extends RuntimeException {

    public InvalidYearException(String input) {
        super("Invalid year entered: " + input);
    }
}