# HR Subsystem – Human Resource Management System

A comprehensive desktop application for managing HR operations including employee information, recruitment, attendance, payroll, onboarding, benefits, performance management, and workforce planning built with Java, Swing, MySQL, and Hibernate ORM.

## Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Local Database Setup](#local-database-setup)
- [Build & Run Instructions](#build--run-instructions)
- [Usage Guide](#usage-guide)
- [Design Patterns Used](#design-patterns-used)
- [Troubleshooting](#troubleshooting)
- [Logging](#logging)
- [Future Enhancements](#future-enhancements)
- [License](#license)
- [Acknowledgments](#acknowledgments)

## Features

### 1. Employee Information Management (EIMS)
- **Dashboard**: Key statistics including total employees, active employees, employees on leave, and new joiners
- **Employee List**: View all employees with real-time search and filtering by department/employment status
- **Add/Update/Delete**: Create, modify, or remove employee records
- **Builder Pattern**: Fluent API for employee object construction
- **Auto-Generated IDs**: EMP-XXXXXXXX format

### 2. Recruitment & ATS
- **Recruitment Dashboard**: View recruitment metrics and key performance indicators
- **Candidate List**: Search and filter candidates by status (APPLIED, SHORTLISTED, INTERVIEW, SELECTED, REJECTED)
- **Add Candidate**: Create candidates with Chain of Responsibility validation (ContactInfo → Resume → Duplicate Check)
- **Status Transitions**: Strict state machine enforcing valid transitions:
  - APPLIED → SHORTLISTED or REJECTED
  - SHORTLISTED → INTERVIEW, SELECTED, or REJECTED
  - INTERVIEW → SELECTED or REJECTED
  - SELECTED → REJECTED
  - REJECTED → (no transitions)
- **Auto-Generated IDs**: CND-XXX format

### 3. Attendance & Leave
- **Check-In/Check-Out**: Record attendance with timestamps
- **Leave Requests**: Submit leave requests with instant filtering (Pending, Approved, Rejected)
- **Manager Approval**: Approve or reject leave requests based on balance
- **Leave Balance Tracking**: Display available leave balance per employee

### 4. Payroll Management
- **Payroll Dashboard**: Overview of payroll operations
- **Payslip Generation**: Generate payslips for employees
- **Transfer Confirmation**: Approve payroll transfers
- **Role-Based Configuration**: SalaryConfigSingleton for MANAGER and ADMIN salary configurations

### 5. Onboarding Management
- **Onboarding Dashboard**: Track onboarding progress for new hires
- **Task Tracking**: Monitor onboarding task status and completion
- **Activity Log**: Maintain historical record of onboarding activities
- **Command Pattern**: Encapsulate actions (ApproveOnboardingCommand, UpdateBackgroundCheckCommand, UpdateDocumentVerificationCommand) with CommandInvoker for history

### 6. Benefits Administration
- **Benefits Enrollment**: Manage employee benefits enrollment records
- **Claims Processing**: Track and process insurance claims
- **Adapter Pattern**: Unified BenefitPlan with HealthPlanAdapter and InsurancePlanAdapter for different benefit types

### 7. Performance Management
- **Appraisal Table**: Record and manage employee appraisals
- **Promotion Tracking**: Track employee promotions
- **Proxy-Based Access Control**: AppraisalServiceProxy restricts access to MANAGER and ADMIN roles only

### 8. Workforce Planning & Budgeting
- **Workforce Planning**: Create workforce plans with headcount projections
- **Budget Forecasting**: Quarterly budget projections and HR cost analysis
- **Report Export**: Export planning data for further analysis

## Technology Stack

- **Java 11**: Programming language
- **Swing**: UI framework for desktop application
- **Hibernate 6.2**: ORM framework for database operations
- **MySQL 8.0**: Relational database management system
- **Maven**: Build automation tool
- **Lombok**: Java library for reducing boilerplate code
- **SLF4J + Logback**: Logging framework

## Project Structure

```
HR-Management-System/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/yourname/myapp/
│   │   │       ├── EmployeeManagementApp.java         # Main application entry point
│   │   │       ├── config/                            # Configuration (Hibernate, etc.)
│   │   │       ├── ui/                                # UI components (Dashboard, Forms, etc.)
│   │   │       ├── service/                           # Business logic layer
│   │   │       ├── repository/                        # Data access layer
│   │   │       ├── entity/                            # JPA entities
│   │   │       ├── exception/                         # Custom exceptions
│   │   │       ├── builder/                           # Builder pattern implementations
│   │   │       ├── dto/                               # Data transfer objects
│   │   │       │
│   │   │       ├── recruitment/                       # Recruitment & ATS Module
│   │   │       │   ├── entity/
│   │   │       │   ├── repository/
│   │   │       │   ├── service/
│   │   │       │   ├── validation/                    # Chain of Responsibility validators
│   │   │       │   ├── exception/
│   │   │       │   └── ui/
│   │   │       │
│   │   │       ├── attendance/                        # Attendance Module
│   │   │       │   ├── entity/
│   │   │       │   ├── repository/
│   │   │       │   ├── service/
│   │   │       │   └── ui/
│   │   │       │
│   │   │       ├── leave/                             # Leave Management Module
│   │   │       │   ├── entity/
│   │   │       │   ├── repository/
│   │   │       │   ├── service/
│   │   │       │   └── ui/
│   │   │       │
│   │   │       ├── payroll/                           # Payroll Module
│   │   │       │   ├── entity/
│   │   │       │   ├── repository/
│   │   │       │   ├── service/
│   │   │       │   ├── ui/
│   │   │       │   └── util/                          # SalaryConfigSingleton
│   │   │       │
│   │   │       ├── onboarding/                        # Onboarding Module
│   │   │       │   ├── entity/
│   │   │       │   ├── repository/
│   │   │       │   ├── service/
│   │   │       │   ├── command/                       # Command pattern implementations
│   │   │       │   └── ui/
│   │   │       │
│   │   │       ├── benefits/                          # Benefits Administration Module
│   │   │       │   ├── entity/
│   │   │       │   ├── repository/
│   │   │       │   ├── service/
│   │   │       │   ├── adapter/                       # BenefitPlan, HealthPlanAdapter, InsurancePlanAdapter
│   │   │       │   └── ui/
│   │   │       │
│   │   │       ├── performance/                       # Performance Management Module
│   │   │       │   ├── entity/
│   │   │       │   ├── repository/
│   │   │       │   ├── service/                       # AppraisalServiceProxy for access control
│   │   │       │   ├── proxy/                         # Proxy pattern implementations
│   │   │       │   └── ui/
│   │   │       │
│   │   │       └── workforce/                         # Workforce Planning Module
│   │   │           ├── entity/
│   │   │           ├── repository/
│   │   │           ├── service/
│   │   │           └── ui/
│   │   │
│   │   └── resources/
│   │       ├── config.properties                      # Database credentials (gitignored)
│   │       ├── logback.xml                            # Logging configuration
│   │       └── schema.sql                             # Database schema
│   │
│   └── test/
│       └── java/                                      # Unit tests
│
├── pom.xml                                            # Maven configuration
├── README.md                                          # This file
├── .gitignore                                         # Git ignore rules
└── logs/                                              # Application logs directory
```

## Prerequisites

- Java 11 or higher
- Maven 3.6.0 or higher
- MySQL 8.0 or higher
- Git (optional, for cloning)

## Local Database Setup

Follow these steps to configure database credentials and initialize the database for local development:

### Step 1: Create Database

Create the `hr_ooad` database in MySQL:

```bash
mysql -u root -p -e "CREATE DATABASE hr_ooad;"
```

### Step 2: Create Configuration File

Create a `config.properties` file in `src/main/resources/` with your MySQL password:

```bash
mkdir -p src/main/resources
echo "db.password=YOUR_MYSQL_PASSWORD" > src/main/resources/config.properties
```

Replace `YOUR_MYSQL_PASSWORD` with your actual MySQL root password.

### Step 3: Verify Configuration

The configuration file is automatically gitignored (see `.gitignore`) to prevent committing sensitive credentials. The application reads the database password from `src/main/resources/config.properties` using the key `db.password`. If this file is missing or the key is not found, the application falls back to the `DB_PASSWORD` environment variable.

**Important**: Never commit `config.properties` to version control. It is listed in `.gitignore` for this reason.

## Build & Run Instructions

### Build the Project

```bash
mvn clean compile
```

### Run the Application

Start the HR Management System application:

```bash
mvn clean compile exec:java -Dexec.mainClass="com.yourname.myapp.EmployeeManagementApp"
```

On first run, the application will automatically initialize the database schema from `schema.sql` and populate all required tables.

## Usage Guide

The application provides a tabbed or sidebar-based interface to access different HR modules. Select a module to manage the corresponding HR function.

### Employee Information Management (EIMS)
- **Dashboard**: View key metrics (total employees, active count, on-leave, new joiners)
- **Employee List**: Search and filter employees by department or employment status
- **Add Employee**: Create new employee with auto-generated ID (EMP-XXXXXXXX)
- **Update/Delete**: Modify or remove employee records

### Recruitment & ATS
- **Recruitment Dashboard**: Monitor recruitment metrics and KPIs
- **Candidate List**: Search candidates by name/contact; filter by status
- **Add Candidate**: Create candidates with automated validation
- **Status Updates**: Advance candidates through defined workflow stages
- **Delete Candidate**: Remove candidate records

### Attendance & Leave
- **Check-In/Check-Out**: Record attendance with timestamps
- **Leave Requests**: Submit and track leave requests
- **Manager Approval**: Approve or reject pending requests
- **Leave Balance**: Monitor available leave per employee

### Payroll Management
- **Payroll Dashboard**: View payroll operations overview
- **Generate Payslips**: Create payslips for employees
- **Transfer Confirmation**: Approve salary transfers
- **Salary Configuration**: Role-based configuration for MANAGER and ADMIN

### Onboarding Management
- **Onboarding Dashboard**: Track progress for new hires
- **Task Tracking**: Manage and monitor onboarding tasks
- **Activity Log**: Review onboarding history
- **Command Actions**: Execute and undo onboarding commands

### Benefits Administration
- **Benefits Enrollment**: Manage employee benefit enrollments
- **Claims Processing**: Track and process insurance claims
- **Unified Plan Access**: Access health and insurance plans through unified interface

### Performance Management
- **Appraisals**: Record employee appraisals (MANAGER/ADMIN only)
- **Promotions**: Track employee promotion history
- **Access Control**: Restricted to authorized roles

### Workforce Planning & Budgeting
- **Workforce Plans**: Create workforce projections
- **Budget Forecasting**: View quarterly budget and cost projections
- **Report Export**: Export planning data for analysis

## Design Patterns Used

### 1. Builder Pattern
**Purpose**: Encapsulate complex object construction with a fluent API
**Implementation**: Employee object construction with optional fields
**Location**: `builder/` package

### 2. Singleton Pattern
**Purpose**: Ensure only one instance of a class exists
**Implementation**: `SalaryConfigSingleton` for centralized salary configuration
**Location**: `payroll/util/SalaryConfigSingleton.java`

### 3. Adapter Pattern
**Purpose**: Convert interface of a class into another interface clients expect
**Implementation**: `BenefitPlan` with `HealthPlanAdapter` and `InsurancePlanAdapter`
**Location**: `benefits/adapter/` package
**Use Case**: Unifying different benefit plan types through a common interface

### 4. Proxy Pattern
**Purpose**: Provide a surrogate or placeholder for another object
**Implementation**: `AppraisalServiceProxy` for access control
**Location**: `performance/proxy/` package
**Use Case**: Restrict appraisal access to MANAGER and ADMIN roles only

### 5. Facade Pattern
**Purpose**: Provide unified interface to a set of interfaces in a subsystem
**Implementation**: Service layer acts as Facade to repository and entity layers
**Benefit**: Simplifies client interactions with complex subsystems

### 6. Chain of Responsibility Pattern
**Purpose**: Pass requests along a chain of handlers
**Implementation**: Candidate validation pipeline
**Location**: `recruitment/validation/` package
**Validators in Sequence**:
   1. `ContactInfoValidator`: Validate contact information
   2. `ResumeValidator`: Validate resume data
   3. `DuplicateCheckHandler`: Check for duplicate candidates

### 7. Command Pattern
**Purpose**: Encapsulate a request as an object
**Implementation**: Onboarding actions with execution history
**Location**: `onboarding/command/` package
**Commands**:
   - `ApproveOnboardingCommand`
   - `UpdateBackgroundCheckCommand`
   - `UpdateDocumentVerificationCommand`
   - `CommandInvoker`: Executes and maintains history

## Troubleshooting

### Access Denied / Authentication Issues
- **Issue**: "Access denied for user 'root'@'localhost'"
- **Solution**: Verify MySQL credentials in `src/main/resources/config.properties`. Ensure `db.password` matches your MySQL root password.

### Missing Tables / Schema Initialization Issues
- **Issue**: "Table 'hr_ooad.employee' doesn't exist"
- **Solution**: Ensure `schema.sql` exists in `src/main/resources/`. Run the application to trigger automatic schema initialization on first startup.

### MySQL Connection Refused
- **Issue**: "Communications link failure" or "Connect refused"
- **Solution**:
  1. Verify MySQL service is running: `mysql --version`
  2. Check MySQL port (default: 3306)
  3. Restart MySQL service if needed

### Missing config.properties File
- **Issue**: "Property 'db.password' not found"
- **Solution**: Create `src/main/resources/config.properties` with `db.password=YOUR_PASSWORD` or set `DB_PASSWORD` environment variable.

### Logging / File Permission Issues
- **Issue**: Cannot write to `logs/` directory
- **Solution**: Ensure `logs/` directory exists and has write permissions:
  ```bash
  mkdir -p logs
  chmod 755 logs
  ```

## Logging

The application uses **Logback** for comprehensive logging:

- **Configuration**: `src/main/resources/logback.xml`
- **Log File**: `logs/eims.log`
- **Log Levels**:
  - DEBUG: Development-level details (console only)
  - INFO: Application events (file and console)
  - WARN: Warning messages
  - ERROR: Error messages with stack traces

**Log File Features**:
- Automatic daily rollover
- 10MB size-based rollover
- 30-day retention policy
- Separate error logging when enabled

Access logs at: `logs/eims.log`

## Future Enhancements

- **Export Functionality**: Export employee data and reports to Excel/PDF
- **Email Notifications**: Automated email alerts for leave approvals, performance reviews
- **Authentication & Authorization**: User login with role-based access control
- **Advanced Analytics**: Dashboards with charts and trend analysis
- **Data Backup & Restore**: Automated database backups
- **Multi-Tenant Support**: Support for multiple organizations
- **Mobile App Integration**: REST API for mobile app connectivity
- **Audit Trail**: Complete audit log of all user actions
- **Performance Optimization**: Database query optimization and caching

## License

This project is created for educational purposes.

## Acknowledgments

This HR Management System was developed as part of an academic project with the following contributors:

- **Bhanavi D**
- **Dhrithi Kiran**
- **Diya Saigal**
- **Harshini Somangali**

**Project Guide**: M S Anand (PESU Faculty)

---

**Version**: 1.0  
**Last Updated**: June 2026