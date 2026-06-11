-- ========================================================================
-- ATTENDANCE & LEAVE MODULE - DATABASE SCHEMA
-- Add these tables to eims_db (after the employee table already exists)
-- Run: mysql -u root -p eims_db < attendance_leave_schema.sql
-- ========================================================================

-- Attendance Records Table
CREATE TABLE IF NOT EXISTS attendance_record (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id     VARCHAR(20) 
        CHARACTER SET utf8mb4
        COLLATE utf8mb4_unicode_ci
        NOT NULL COMMENT 'FK to employee.employee_id',
    attendance_date DATE        NOT NULL COMMENT 'Date of attendance',
    check_in_time   TIME                 COMMENT 'Check-in time',
    check_out_time  TIME                 COMMENT 'Check-out time',
    overtime_hours  DOUBLE DEFAULT 0.0   COMMENT 'Auto-calculated: hours beyond 8',
    CONSTRAINT fk_attendance_employee
        FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_attendance_employee (employee_id),
    INDEX idx_attendance_date (attendance_date)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci 
COMMENT 'Tracks daily employee attendance';

-- Leave Requests Table
CREATE TABLE IF NOT EXISTS leave_request (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id      VARCHAR(20) 
        CHARACTER SET utf8mb4
        COLLATE utf8mb4_unicode_ci
        NOT NULL COMMENT 'FK to employee.employee_id',
    leave_from_date  DATE        NOT NULL COMMENT 'Start date of leave',
    leave_to_date    DATE        NOT NULL COMMENT 'End date of leave',
    leave_status     VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                         COMMENT 'PENDING, APPROVED, REJECTED',
    CONSTRAINT fk_leave_employee
        FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_leave_employee (employee_id),
    INDEX idx_leave_status (leave_status)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci 
COMMENT 'Employee leave requests';

-- Leave Balance Table
CREATE TABLE IF NOT EXISTS leave_balance (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(20) 
        CHARACTER SET utf8mb4
        COLLATE utf8mb4_unicode_ci
        NOT NULL UNIQUE COMMENT 'FK to employee.employee_id',
    balance     INT         NOT NULL DEFAULT 20 COMMENT 'Remaining leave days',
    CONSTRAINT fk_balance_employee
        FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci 
COMMENT 'Tracks leave balance per employee (default 20 days)';
