-- ============================================================================
-- Employee Information Management System (EIMS) + Recruitment & ATS
-- Complete Database Schema - SQL File Based
-- ============================================================================
-- This file contains ALL SQL statements to initialize the database
-- It is executed automatically on application startup if tables don't exist
-- ============================================================================

-- ============================================================================
-- EMPLOYEE TABLE
-- ============================================================================
-- Stores employee records for the Employee Management module
CREATE TABLE IF NOT EXISTS employees (
  employee_id VARCHAR(20) PRIMARY KEY COMMENT 'Auto-generated ID in format EMP-XXXXXXXX',
  employee_name VARCHAR(100) NOT NULL COMMENT 'Employee full name',
  department VARCHAR(50) NOT NULL COMMENT 'Department assignment',
  job_role VARCHAR(100) NOT NULL COMMENT 'Job position/role',
  employment_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE, INACTIVE, ON_LEAVE',
  joining_date DATE COMMENT 'Date employee joined',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Record creation timestamp',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Record update timestamp',
  INDEX idx_department (department),
  INDEX idx_status (employment_status),
  INDEX idx_name (employee_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Employee records for EIMS system';

-- ============================================================================
-- CANDIDATE TABLE
-- ============================================================================
-- Stores job candidate records for the Recruitment & ATS module
CREATE TABLE IF NOT EXISTS candidate (
  candidate_id VARCHAR(20) PRIMARY KEY COMMENT 'Auto-generated ID in format CND-001, CND-002, etc.',
  candidate_name VARCHAR(100) NOT NULL COMMENT 'Candidate full name',
  contact_info VARCHAR(100) NOT NULL COMMENT 'Email or phone number',
  resume_data LONGTEXT NOT NULL COMMENT 'Resume URL or embedded content',
  interview_score DOUBLE COMMENT 'Interview performance score (0-100)',
  application_status VARCHAR(20) NOT NULL COMMENT 'APPLIED, SHORTLISTED, INTERVIEW, SELECTED, REJECTED',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Record creation timestamp',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Record update timestamp',
  INDEX idx_status (application_status),
  INDEX idx_contact (contact_info),
  INDEX idx_name (candidate_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Candidate records for Recruitment & ATS system';

-- ============================================================================
-- ATTENDANCE & LEAVE TABLES
-- ============================================================================

-- Attendance Records Table
CREATE TABLE IF NOT EXISTS attendance_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(20) NOT NULL COMMENT 'FK to employees.employee_id',
    attendance_date DATE NOT NULL COMMENT 'Date of attendance',
    check_in_time TIME COMMENT 'Check-in time',
    check_out_time TIME COMMENT 'Check-out time',
    overtime_hours DOUBLE DEFAULT 0.0 COMMENT 'Auto-calculated: hours beyond 8',
    CONSTRAINT fk_attendance_employee
        FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_attendance_employee (employee_id),
    INDEX idx_attendance_date (attendance_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Tracks daily employee attendance';

-- Leave Requests Table
CREATE TABLE IF NOT EXISTS leave_request (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(20) NOT NULL COMMENT 'FK to employees.employee_id',
    leave_from_date DATE NOT NULL COMMENT 'Start date of leave',
    leave_to_date DATE NOT NULL COMMENT 'End date of leave',
    leave_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, APPROVED, REJECTED',
    CONSTRAINT fk_leave_employee
        FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_leave_employee (employee_id),
    INDEX idx_leave_status (leave_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Employee leave requests';

-- Leave Balance Table
CREATE TABLE IF NOT EXISTS leave_balance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(20) NOT NULL UNIQUE COMMENT 'FK to employees.employee_id',
    balance INT NOT NULL DEFAULT 20 COMMENT 'Remaining leave days',
    CONSTRAINT fk_balance_employee
        FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Tracks leave balance per employee (default 20 days)';

-- ============================================================================
-- BENEFITS ADMINISTRATION TABLES
-- ============================================================================

CREATE TABLE IF NOT EXISTS benefit_enrollment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(20) NOT NULL COMMENT 'FK to employees.employee_id',
    enrollment_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, ACTIVE, INACTIVE',
    health_plan VARCHAR(100) COMMENT 'Health insurance plan name',
    insurance_plan VARCHAR(100) COMMENT 'Insurance plan name',
    insurance_coverage_status VARCHAR(20) DEFAULT 'INACTIVE' COMMENT 'ACTIVE, INACTIVE',
    CONSTRAINT fk_benefit_employee
        FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_benefit_employee (employee_id),
    INDEX idx_enrollment_status (enrollment_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Benefits enrollment records per employee';

CREATE TABLE IF NOT EXISTS claim (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(20) NOT NULL COMMENT 'FK to employees.employee_id',
    claim_type VARCHAR(100) NOT NULL COMMENT 'Medical, Dental, Vision, etc.',
    amount DECIMAL(10,2) NOT NULL COMMENT 'Claim amount',
    claim_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, APPROVED, REJECTED, PAID',
    CONSTRAINT fk_claim_employee
        FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_claim_employee (employee_id),
    INDEX idx_claim_status (claim_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Insurance claims per employee';

-- ============================================================================
-- PAYROLL TABLE
-- ============================================================================

CREATE TABLE IF NOT EXISTS payroll (
    payroll_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(20) NOT NULL COMMENT 'FK to employees.employee_id',
    role VARCHAR(255) COMMENT 'Job role at time of payroll',
    gross_salary DECIMAL(19,2) COMMENT 'Gross salary',
    deductions DECIMAL(19,2) COMMENT 'Total deductions',
    net_pay DECIMAL(19,2) COMMENT 'Net pay after deductions',
    current_month_total DECIMAL(19,2) COMMENT 'Monthly total',
    salary_transfer_record VARCHAR(255) COMMENT 'Transfer reference',
    month VARCHAR(255) NOT NULL COMMENT 'Month name or number',
    year INT NOT NULL COMMENT 'Year',
    CONSTRAINT fk_payroll_employee
        FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_payroll_employee (employee_id),
    INDEX idx_payroll_month_year (month, year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Payroll records per employee per month';

-- ============================================================================
-- PERFORMANCE MANAGEMENT TABLES
-- ============================================================================

CREATE TABLE IF NOT EXISTS appraisal (
    appraise_id VARCHAR(255) PRIMARY KEY COMMENT 'Appraisal ID',
    employee_id VARCHAR(20) NOT NULL COMMENT 'FK to employees.employee_id',
    rating DOUBLE NOT NULL COMMENT 'Performance rating (0-5)',
    feedback VARCHAR(500) COMMENT 'Appraisal feedback',
    appraisal_status VARCHAR(255) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, COMPLETED, APPROVED',
    deadline_date DATE NOT NULL COMMENT 'Appraisal deadline',
    locked BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Is appraisal locked',
    CONSTRAINT fk_appraisal_employee
        FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_appraisal_employee (employee_id),
    INDEX idx_appraisal_status (appraisal_status),
    INDEX idx_appraisal_deadline (deadline_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Employee performance appraisals';

CREATE TABLE IF NOT EXISTS promotion (
    promotion_id VARCHAR(255) PRIMARY KEY COMMENT 'Promotion ID',
    employee_id VARCHAR(20) NOT NULL COMMENT 'FK to employees.employee_id',
    new_role VARCHAR(255) NOT NULL COMMENT 'New promoted role',
    effective_date DATE NOT NULL COMMENT 'Promotion effective date',
    CONSTRAINT fk_promotion_employee
        FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_promotion_employee (employee_id),
    INDEX idx_promotion_effective_date (effective_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Employee promotion records';

-- ============================================================================
-- WORKFORCE PLANNING TABLE
-- ============================================================================

CREATE TABLE IF NOT EXISTS workforce_plan (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    department VARCHAR(255) NOT NULL COMMENT 'Department name',
    open_positions INT NOT NULL DEFAULT 0 COMMENT 'Number of open positions',
    hiring_forecast INT NOT NULL DEFAULT 0 COMMENT 'Expected hiring count',
    hr_cost_projections DECIMAL(19,2) NOT NULL DEFAULT 0 COMMENT 'HR budget projection',
    quarter VARCHAR(255) NOT NULL COMMENT 'Quarter (Q1, Q2, Q3, Q4)',
    total_budget DECIMAL(19,2) NOT NULL DEFAULT 0 COMMENT 'Total budget allocation',
    INDEX idx_workforce_department (department),
    INDEX idx_workforce_quarter (quarter),
    UNIQUE KEY unique_dept_quarter (department, quarter)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Workforce planning and hiring forecasts per department/quarter';

-- ============================================================================
-- ONBOARDING TABLES
-- ============================================================================

CREATE TABLE IF NOT EXISTS onboarding_record (
    onboarding_id VARCHAR(255) PRIMARY KEY COMMENT 'Onboarding record ID',
    assigned_employee_id VARCHAR(20) NOT NULL COMMENT 'FK to employees.employee_id',
    employee_name VARCHAR(255) NOT NULL COMMENT 'New employee name',
    background_check_status VARCHAR(255) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, APPROVED, REJECTED',
    document_verification_status VARCHAR(255) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, VERIFIED, REJECTED',
    verified_record BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Is record verified',
    pipeline_status VARCHAR(255) NOT NULL DEFAULT 'INITIATED' COMMENT 'Onboarding pipeline status',
    CONSTRAINT fk_onboarding_employee
        FOREIGN KEY (assigned_employee_id) REFERENCES employees(employee_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_onboarding_employee (assigned_employee_id),
    INDEX idx_onboarding_status (pipeline_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Employee onboarding records';

CREATE TABLE IF NOT EXISTS onboarding_activity_log (
    onboarding_id VARCHAR(255) NOT NULL COMMENT 'FK to onboarding_record.onboarding_id',
    activity VARCHAR(500) COMMENT 'Activity description',
    PRIMARY KEY (onboarding_id, activity),
    CONSTRAINT fk_onboarding_activity
        FOREIGN KEY (onboarding_id) REFERENCES onboarding_record(onboarding_id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Activity log for employee onboarding';

-- ============================================================================
-- SETUP VERIFICATION
-- ============================================================================
-- Run these commands to verify tables were created successfully:
-- SHOW TABLES;
-- SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='eims_db';
-- ============================================================================
