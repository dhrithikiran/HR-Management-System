package com.yourname.myapp.exception;

public class LeaveBalanceExceededException extends RuntimeException {
    public LeaveBalanceExceededException(String message) {
        super(message);
    }
}
