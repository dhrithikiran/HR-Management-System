-- ============================================================================
-- Employee Information Management System (EIMS) + Recruitment & ATS
-- Database Schema
-- ============================================================================
-- This file contains SQL scripts to manually create all database tables.
-- Use this ONLY if you prefer manual table creation instead of auto-schema.
-- 
-- Auto-Schema Setup (DEFAULT):
-- If using Hibernate with hbm2ddl.auto=update, tables will be auto-created
-- on first application run. You do NOT need to run this file.
--
-- Manual Setup (ALTERNATIVE):
-- If you want to pre-create tables before running the application:
-- 1. Create database: CREATE DATABASE eims_db;
-- 2. Execute this file: mysql -u root -p eims_db < DATABASE_SCHEMA.sql
-- ============================================================================

-- ============================================================================
-- EMPLOYEE TABLE
-- ============================================================================
-- Stores employee records for the Employee Management module
-- Fields:
--   - employee_id: AUTO-GENERATED primary key (EMP-XXXXXXXX format)
--   - employee_name: Employee's full name (required)
--   - department: Department assignment (IT, HR, Finance, etc.)
--   - job_role: Job position/role
--   - employment_status: ACTIVE, INACTIVE, ON_LEAVE
--   - joining_date: Date employee joined organization
--   - created_at: Timestamp when record was created
--   - updated_at: Timestamp when record was last updated
-- ============================================================================
CREATE TABLE IF NOT EXISTS employee (
  employee_id VARCHAR(20) PRIMARY KEY COMMENT 'Auto-generated ID in format EMP-XXXXXXXX',
  employee_name VARCHAR(100) NOT NULL COMMENT 'Employee full name',
  department VARCHAR(50) COMMENT 'Department assignment',
  job_role VARCHAR(50) COMMENT 'Job position/role',
  employment_status VARCHAR(20) COMMENT 'ACTIVE, INACTIVE, ON_LEAVE',
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
-- Fields:
--   - candidate_id: AUTO-GENERATED primary key (CND-XXX format)
--   - candidate_name: Candidate's full name (required)
--   - contact_info: Email or phone number (required)
--   - resume_data: Resume URL or embedded content (required)
--   - interview_score: Interview performance score (0-100 scale, optional)
--   - application_status: APPLIED, SHORTLISTED, INTERVIEW, SELECTED, REJECTED
--   - created_at: Timestamp when record was created
--   - updated_at: Timestamp when record was last updated
--
-- Status Transition Rules (enforced at application level):
--   APPLIED -> [SHORTLISTED, REJECTED]
--   SHORTLISTED -> [INTERVIEW, SELECTED, REJECTED]
--   INTERVIEW -> [SELECTED, REJECTED]
--   SELECTED -> [REJECTED]
--   REJECTED -> [] (terminal state)
-- ============================================================================
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
-- SETUP VERIFICATION
-- ============================================================================
-- Run these commands to verify tables were created successfully:
-- SHOW TABLES;
-- DESCRIBE employee;
-- DESCRIBE candidate;
-- ============================================================================
