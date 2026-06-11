package com.yourname.myapp.performance.exception;

public class AppraisalDeadlineMissedException extends RuntimeException {
    public AppraisalDeadlineMissedException(String message) {
        super(message);
    }
}
