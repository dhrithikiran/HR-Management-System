package com.yourname.myapp.exception;

/**
 * Exception thrown when attempting to create an employee with a duplicate ID.
 */
public class DuplicateEmployeeIdException extends RuntimeException {
    public DuplicateEmployeeIdException(String message) {
        super(message);
    }

    public DuplicateEmployeeIdException(String message, Throwable cause) {
        super(message, cause);
    }
}
