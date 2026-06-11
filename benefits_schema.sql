-- ========================================================================
-- BENEFITS ADMINISTRATION MODULE - DATABASE SCHEMA
-- Run after employee table exists in eims_db
-- ========================================================================

SET FOREIGN_KEY_CHECKS=0;

CREATE TABLE IF NOT EXISTS benefit_enrollment (
    id                        BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id               VARCHAR(20) NOT NULL,
    enrollment_status         VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    health_plan               VARCHAR(100),
    insurance_plan            VARCHAR(100),
    insurance_coverage_status VARCHAR(20) DEFAULT 'INACTIVE',
    INDEX idx_benefit_employee (employee_id)
);

CREATE TABLE IF NOT EXISTS claim (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id  VARCHAR(20) NOT NULL,
    claim_type   VARCHAR(100),
    amount       DECIMAL(10,2),
    claim_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    INDEX idx_claim_employee (employee_id)
);

SET FOREIGN_KEY_CHECKS=1;
