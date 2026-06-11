# Employee Information Management System (EIMS) + Recruitment & ATS

A comprehensive desktop application for managing employee information and recruitment processes built with Java, Swing, MySQL, and Hibernate ORM.

## Introduction

EIMS is a comprehensive desktop application designed to manage employee records efficiently. It provides a user-friendly interface for managing employees, including creating, updating, deleting, and viewing employee records with advanced filtering capabilities.

## Features

### Employee Management Module
- **Dashboard View**: Display key statistics (total employees, active employees, on-leave, new joiners)
- **Employee List**: View all employees with filtering by department and employment status
- **Search Functionality**: Real-time search by employee name
- **Add Employee**: Create new employee records with auto-generated IDs (EMP-XXXXXXXX format)
- **Update Employee**: Modify employee information
- **Delete Employee**: Remove employee records with confirmation
- **Builder Pattern**: Fluent API for employee object construction

### Recruitment & ATS Module (NEW)
- **Recruitment Dashboard**: Display key recruitment metrics
  - Total Applications Received
  - Shortlisted Count
  - Selected Count
  - Shortlist Rate (%)
  - Selection Rate (%)
  - Open Positions
- **Candidate List**: Manage recruitment candidates with:
  - Search functionality by candidate name and contact
  - Status filter (APPLIED, SHORTLISTED, INTERVIEW, SELECTED, REJECTED, ALL)
  - Interview score display
  - Status update capability
- **Add Candidate**: Create new candidate records with:
  - Auto-generated Candidate IDs (CND-001 format)
  - Chain of Responsibility validation (ContactInfo → Resume → Duplicate Check)
  - User-friendly error messages
- **Update Candidate**: Modify candidate details
- **Status Update**: Change candidate status with strict transition validation:
  - APPLIED → SHORTLISTED, REJECTED
  - SHORTLISTED → INTERVIEW, SELECTED, REJECTED
  - INTERVIEW → SELECTED, REJECTED
  - SELECTED → REJECTED
  - REJECTED → (no transitions)

### Technical Features
- **Builder Pattern**: Used for employee object construction
- **Chain of Responsibility Pattern**: Validation pipeline for candidate data
- **Repository Pattern**: Abstraction layer for data access
- **Service Layer**: Business logic separation with comprehensive error handling
- **Custom Exceptions**: Specific exception types for different error scenarios
- **JPA/Hibernate ORM**: Object-relational mapping for database operations
- **Centralized Exception Handling**: User-friendly error dialogs throughout the application
- **Status Transition Validation**: Enforce business rules for candidate status changes
- **Auto-Generated IDs**: Both employees (EMP-XXXXXXXX) and candidates (CND-XXX)

## Technology Stack

- **Java 11**: Programming language
- **Swing**: UI framework for desktop application
- **Hibernate 6.2.0.Final**: ORM framework
- **MySQL 8.0**: Database
- **Maven**: Build tool
- **Lombok**: Code generation library
- **SLF4J + Logback**: Logging framework
- **Jakarta Persistence API 3.1.0**: JPA specification

## Project Structure

```
ooad-project/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/yourname/myapp/
│   │   │       ├── App.java                          # Application entry point
│   │   │       ├── EmployeeManagementApp.java        # Main Swing application
│   │   │       ├── builder/
│   │   │       │   └── EmployeeBuilder.java          # Builder pattern for Employee
│   │   │       ├── config/
│   │   │       │   └── HibernateUtil.java            # Hibernate session factory & config
│   │   │       ├── dto/
│   │   │       │   ├── EmployeeRequest.java          # Employee request DTO
│   │   │       │   └── DashboardStats.java           # Dashboard statistics DTO
│   │   │       ├── entity/
│   │   │       │   ├── Employee.java                 # Employee JPA entity
│   │   │       │   └── EmploymentStatus.java         # Enum for employment status
│   │   │       ├── exception/
│   │   │       │   ├── EmployeeNotFoundException.java
│   │   │       │   └── DuplicateEmployeeIdException.java
│   │   │       ├── repository/
│   │   │       │   ├── EmployeeRepository.java       # Repository interface
│   │   │       │   └── EmployeeRepositoryImpl.java    # Repository implementation
│   │   │       ├── service/
│   │   │       │   └── EmployeeService.java          # Employee business logic
│   │   │       ├── ui/
│   │   │       │   ├── DashboardView.java            # Employee dashboard UI
│   │   │       │   ├── EmployeeListView.java         # Employee list UI
│   │   │       │   ├── AddEmployeeForm.java          # Add employee form
│   │   │       │   ├── UpdateEmployeeForm.java       # Update employee form
│   │   │       │   └── util/
│   │   │       │       └── DialogUtil.java           # Dialog utilities
│   │   │       │
│   │   │       └── recruitment/                      # Recruitment & ATS Module
│   │   │           ├── entity/
│   │   │           │   └── Candidate.java            # Candidate JPA entity (APPLIED, SHORTLISTED, etc.)
│   │   │           ├── repository/
│   │   │           │   ├── CandidateRepository.java       # Repository interface
│   │   │           │   └── CandidateRepositoryImpl.java    # Hibernate implementation
│   │   │           ├── service/
│   │   │           │   ├── CandidateService.java         # Service interface
│   │   │           │   └── CandidateServiceImpl.java      # Service implementation (validation chain, status transitions)
│   │   │           ├── validation/
│   │   │           │   ├── ValidationHandler.java        # Chain of Responsibility base
│   │   │           │   ├── ContactInfoValidator.java     # Validates contact information
│   │   │           │   ├── ResumeValidator.java          # Validates resume data
│   │   │           │   └── DuplicateCheckHandler.java    # Checks for duplicate candidates
│   │   │           ├── exception/
│   │   │           │   └── CandidateDataIncompleteException.java  # Custom exception
│   │   │           └── ui/
│   │   │               ├── RecruitmentDashboardView.java  # Recruitment metrics dashboard
│   │   │               ├── CandidateListView.java         # Candidate list with filters
│   │   │               ├── AddCandidateForm.java          # Add candidate form
│   │   │               ├── UpdateCandidateForm.java       # Update candidate form
│   │   │               └── StatusUpdateForm.java          # Status update dropdown form
│   │   └── resources/
│   │       └── logback.xml                  # Logging configuration
│   └── test/
│       └── java/...
├── pom.xml                                 # Maven configuration
├── README.md                               # Project documentation (this file)
└── logs/                                   # Application logs directory
```

## Prerequisites

- Java 11 or higher
- Maven 3.6.0 or higher
- MySQL 8.0 or higher
- Git (optional, for cloning)

## Quick Start (For New Developers)

After cloning this repository:

```bash
# 1. Clone the repository
git clone <repository-url>
cd ooad-project

# 2. Create MySQL database
mysql -u root -p -e "CREATE DATABASE eims_db;"

# 3. (Optional) Set database credentials via environment variables
# If using non-default credentials, set these environment variables:
export DB_URL="jdbc:mysql://localhost:3306/eims_db"
export DB_USERNAME="your_mysql_username"
export DB_PASSWORD="your_mysql_password"

# If not set, defaults are: username=root, password=empty

# 4. Build the project
mvn clean install

# 5. Run the application
mvn exec:java -Dexec.mainClass="com.yourname.myapp.EmployeeManagementApp"

# 6. On first run:
# - Tables will be created automatically from SQL schema (src/main/resources/schema.sql)
# - No manual SQL setup needed
```

**Important Notes**:
- The application requires MySQL running with database `eims_db` created before startup
- Database credentials use environment variables for security (no hardcoded passwords)
- Default credentials: username=`root`, password=empty (localhost)
- Change these for production deployments

## Database Setup

### Step 1: Create Database

Open MySQL command line or MySQL Workbench and execute:

```sql
CREATE DATABASE eims_db;
USE eims_db;
```

### Step 2: SQL-Based Schema Initialization

The application uses **SQL files for schema creation** instead of Hibernate's auto-schema generation.

When the application starts for the first time:
1. DatabaseInitializer checks if tables exist in the database
2. If tables don't exist, it automatically executes `src/main/resources/schema.sql`
3. All required tables are created with proper relationships and indexes
4. No manual SQL setup required - everything is automatic!

**Advantages:**
- Full control over table structure and indexes via SQL files
- Consistent schema across all environments
- Better performance through optimized indexes
- Clear database migration path
- No dependency on Hibernate's DDL generation

**Tables Created Automatically:**
- `employees` - Employee records
- `candidate` - Recruitment candidates
- `attendance_record` - Attendance tracking
- `leave_request` - Leave request management
- `leave_balance` - Employee leave balance
- `benefit_enrollment` - Benefits enrollment
- `claim` - Insurance claims
- `payroll` - Payroll records
- `appraisal` - Performance appraisals
- `promotion` - Employee promotions
- `workforce_plan` - Workforce planning
- `onboarding_record` - Employee onboarding
- `onboarding_activity_log` - Onboarding activity

**Default values:**
- **URL**: jdbc:mysql://localhost:3306/eims_db
- **Username**: root
- **Password**: (check HibernateUtil.java for default)

### Step 3 (OPTIONAL): Manual Table Creation

If you want to pre-create tables before running the application (instead of auto-initialization):

```bash
# Linux/Mac
mysql -u root -p eims_db < src/main/resources/schema.sql

# Or use MySQL Workbench to run the schema.sql file
```

Verify tables were created:
```sql
USE eims_db;
SHOW TABLES;
DESCRIBE employees;
DESCRIBE candidate;
```

## Building and Running

### Option 1: Using Maven

```bash
# Navigate to project directory
cd ooad-project

# Clean and build
mvn clean package

# Run the application
mvn exec:java -Dexec.mainClass="com.yourname.myapp.EmployeeManagementApp"

# Or run the generated JAR
java -jar target/eims-desktop-1.0-SNAPSHOT.jar
```

### Option 2: IDE Execution

If using IntelliJ IDEA or Eclipse:
1. Right-click on `EmployeeManagementApp.java`
2. Select "Run 'EmployeeManagementApp.main()'"

## Usage Guide

### EMPLOYEE MANAGEMENT MODULE

#### Dashboard View
- Displays four key metrics:
  - **Total Employees**: Total count of all employees in the system
  - **Active Employees**: Count of employees with ACTIVE status
  - **On Leave**: Count of employees on leave
  - **New Joiners**: Count of employees who joined in the current month

#### Employee List View
- **Search**: Enter employee name to search in real-time
- **Filter by Department**: Select a specific department to filter
- **Filter by Status**: Filter employees by their employment status
- **Clear Filters**: Reset all filters to view all employees
- **Refresh**: Manually refresh the employee list
- **Edit**: Select an employee and click Edit to modify details
- **Delete**: Select an employee and click Delete to remove (requires confirmation)

#### Add Employee
1. Click the "Add Employee" button in the sidebar
2. Fill in all required fields:
   - Employee Name
   - Department (dropdown selector)
   - Job Role (dropdown selector)
   - Employment Status (defaults to ACTIVE)
3. Click "Save" to create the employee
4. An auto-generated Employee ID in format EMP-XXXXXXXX will be assigned

#### Update Employee
1. In Employee List, select an employee
2. Click "Edit" button
3. Modify the desired fields
4. Click "Update" to save changes

#### Delete Employee
1. In Employee List, select an employee
2. Click "Delete" button
3. Confirm deletion in the dialog
4. Employee will be removed from the system

---

### RECRUITMENT & ATS MODULE

#### Recruitment Dashboard
1. Click "Recruitment Dashboard" in the sidebar
2. View key metrics in colored stat cards:
   - **Total Applications**: All candidates received
   - **Shortlisted**: Candidates who passed initial screening
   - **Selected**: Candidates who passed all rounds
   - **Shortlist Rate**: Percentage of applicants shortlisted
   - **Selection Rate**: Percentage of shortlisted candidates selected
   - **Open Positions**: Current open job positions

#### Candidate List View
1. Click "Candidate List" in the sidebar
2. **Search**: Enter candidate name or contact to search
3. **Filter by Status**: Select status from dropdown:
   - ALL: Show all candidates
   - APPLIED: Initial applicants
   - SHORTLISTED: Candidates passed screening
   - INTERVIEW: Candidates in interview process
   - SELECTED: Final selected candidates
   - REJECTED: Rejected candidates
4. **Refresh**: Update list to see latest changes
5. **Clear Filters**: Reset all filters to view all candidates

#### Add Candidate
1. Click "Add Candidate" button in Candidate List
2. Fill in required fields:
   - Candidate Name (required)
   - Contact Info (required, email/phone)
   - Resume Data (required, URL or text)
   - Interview Score (optional, 0-100)
3. **Validation Chain** automatically checks:
   - Contact information validity
   - Resume data completeness
   - No duplicate candidates
4. Click "Save" to add candidate
5. Auto-generated Candidate ID (CND-001, CND-002, etc.) will be assigned
6. Status automatically set to APPLIED

#### Update Candidate
1. In Candidate List, select a candidate
2. Click "Update" button
3. Modify candidate details (name, contact, resume, interview score)
4. Validation chain executes before saving
5. Click "Save" to update

#### Update Candidate Status
1. In Candidate List, select a candidate
2. Click "Update Status" button
3. A dropdown shows all allowed transitions based on current status:
   - **From APPLIED**: Can select SHORTLISTED or REJECTED
   - **From SHORTLISTED**: Can select INTERVIEW, SELECTED, or REJECTED
   - **From INTERVIEW**: Can select SELECTED or REJECTED
   - **From SELECTED**: Can select REJECTED only
   - **From REJECTED**: No transitions allowed
4. Select new status and click "Update"
5. Status transition validation ensures business rules are enforced

#### Delete Candidate
1. In Candidate List, select a candidate
2. Click "Delete" button
3. Confirm deletion in the dialog
4. Candidate will be removed from the system

## Design Patterns Used

### 1. Builder Pattern (Employee Module)
**Purpose**: Encapsulate complex object construction with a fluent API

**Implementation**: `EmployeeBuilder` class provides a flexible way to construct Employee objects with optional fields

**Example**:
```java
Employee employee = new EmployeeBuilder()
    .withEmployeeName("John Doe")
    .withDepartment("IT")
    .withJobRole("Developer")
    .withEmploymentStatus(EmploymentStatus.ACTIVE)
    .build();
```

### 2. Chain of Responsibility Pattern (Recruitment Module - Validation)
**Purpose**: Process candidate data through a series of validators in a chain

**Implementation**: Three validators executed in sequence before candidate is saved:
1. **ContactInfoValidator**: Validates non-empty contact information
2. **ResumeValidator**: Validates non-empty resume data
3. **DuplicateCheckHandler**: Checks for duplicate candidates in database

**Flow**:
```
ContactInfoValidator → ResumeValidator → DuplicateCheckHandler
         ↓                    ↓                      ↓
    Validates            Validates            Checks DB
    Contact Info         Resume Data          for Duplicates
```

**Exception Handling**: If any validator fails, `CandidateDataIncompleteException` is thrown with descriptive message

### 3. Repository Pattern (Both Modules)
**Purpose**: Abstract data access logic from business logic

**Implementation**: 
- `EmployeeRepository` interface + `EmployeeRepositoryImpl` (Hibernate-based)
- `CandidateRepository` interface + `CandidateRepositoryImpl` (Hibernate-based)

**Benefits**: Easy to swap implementations, unit testing, consistent data access

### 4. Service Layer Pattern (Both Modules)
**Purpose**: Centralize business logic and validation

**Implementation**:
- `EmployeeService`: Manages employee operations
- `CandidateService`: Manages candidate operations with validation chain orchestration

**Benefits**: Separation of concerns, centralized validation, reusable business logic

### 5. State Transition Pattern (Recruitment Module - Status Management)
**Purpose**: Enforce valid state transitions for candidate status

**Implementation**: Static map of allowed transitions in `CandidateServiceImpl`:
```java
ApplicationStatus.APPLIED → [SHORTLISTED, REJECTED]
ApplicationStatus.SHORTLISTED → [INTERVIEW, SELECTED, REJECTED]
ApplicationStatus.INTERVIEW → [SELECTED, REJECTED]
ApplicationStatus.SELECTED → [REJECTED]
ApplicationStatus.REJECTED → []
```

**Validation**: `updateStatus()` method checks if transition is allowed, throws `IllegalStateException` if invalid

---

## Database Schema

### Employee Table
```sql
CREATE TABLE employee (
  employee_id VARCHAR(20) PRIMARY KEY,
  employee_name VARCHAR(100) NOT NULL,
  department VARCHAR(50),
  job_role VARCHAR(50),
  employment_status VARCHAR(20),
  joining_date DATE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### Candidate Table
```sql
CREATE TABLE candidate (
  candidate_id VARCHAR(20) PRIMARY KEY,
  candidate_name VARCHAR(100) NOT NULL,
  contact_info VARCHAR(100) NOT NULL,
  resume_data LONGTEXT NOT NULL,
  interview_score DOUBLE,
  application_status VARCHAR(20) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### Tables Auto-Created
Tables are automatically created on first application run due to Hibernate configuration:
```xml
<property name="hibernate.hbm2ddl.auto">update</property>
```

---

## Class Documentation

### Entity Classes

#### Employee
- **Location**: `entity/Employee.java`
- **JPA Mappings**: @Entity, @Table(name="employee")
- **Fields**:
  - `employeeId` (String, Primary Key): Auto-generated format EMP-XXXXXXXX
  - `employeeName` (String): Required, max 100 chars
  - `department` (String): Department classification
  - `jobRole` (String): Job position
  - `employmentStatus` (Enum): ACTIVE, INACTIVE, ON_LEAVE
  - `joiningDate` (Date): Employee joining date
  - `createdAt`, `updatedAt`: Audit timestamps
- **Methods**: Getters, setters, toString()

#### EmploymentStatus
- **Location**: `entity/EmploymentStatus.java`
- **Values**: ACTIVE, INACTIVE, ON_LEAVE
- **Purpose**: Enum for employee status tracking

#### Candidate
- **Location**: `recruitment/entity/Candidate.java`
- **JPA Mappings**: @Entity, @Table(name="candidate")
- **Fields**:
  - `candidateId` (String, Primary Key): Auto-generated format CND-001, CND-002, etc.
  - `candidateName` (String): Required, max 100 chars
  - `contactInfo` (String): Required, email/phone, max 100 chars
  - `resumeData` (String): Required, resume content/URL
  - `interviewScore` (Double): Interview performance score (0-100)
  - `applicationStatus` (Enum): APPLIED, SHORTLISTED, INTERVIEW, SELECTED, REJECTED
- **Methods**: Getters, setters, toString()

#### ApplicationStatus
- **Location**: `recruitment/entity/Candidate.java` (nested enum)
- **Values**: APPLIED, SHORTLISTED, INTERVIEW, SELECTED, REJECTED
- **Purpose**: Candidate workflow status tracking

### Service Layer

#### EmployeeService
- **Location**: `service/EmployeeService.java`
- **Key Methods**:
  - `getAllEmployees()`: List all employees
  - `getAllEmployees(String dept, String status)`: Get employees with filters
  - `getEmployeeById(String id)`: Fetch specific employee
  - `createEmployee(EmployeeRequest dto)`: Create new employee (uses Builder pattern)
  - `updateEmployee(String id, EmployeeRequest dto)`: Update employee
  - `deleteEmployee(String id)`: Delete employee with validation
  - `getDashboardStats()`: Get dashboard statistics
  - `searchByName(String name)`: Search employees by name
  - `getAllDepartments()`: Get unique departments

#### CandidateService
- **Location**: `recruitment/service/CandidateService.java`
- **Key Methods**:
  - `getAllCandidates(String status)`: Get candidates, optionally filtered by status
  - `getCandidateById(String id)`: Fetch specific candidate
  - `createCandidate(Candidate)`: Create candidate (executes validation chain)
  - `updateCandidate(String id, Candidate)`: Update candidate details
  - `updateStatus(String id, String newStatus)`: Update status with transition validation
  - `deleteCandidate(String id)`: Delete candidate
  - `getRecruitmentStats()`: Get recruitment dashboard statistics
- **Recruitment Stats Return Values**:
  - `applicationsReceived`: Total candidates
  - `shortlistedCount`: Candidates in SHORTLISTED status
  - `selectedCount`: Candidates in SELECTED status
  - `openPositions`: Available job openings
  - `hiringForecast`: Estimated hires based on current pipeline

### Repository Layer

#### EmployeeRepository & EmployeeRepositoryImpl
- **Location**: `repository/EmployeeRepository.java` (interface), `repository/EmployeeRepositoryImpl.java` (implementation)
- **Custom Methods**:
  - `save(Employee)`: Insert/update employee
  - `findById(String id)`: Find by ID
  - `findAll()`: Get all employees
  - `findByDepartment(String)`: Filter by department
  - `findByEmploymentStatus(EmploymentStatus)`: Filter by status
  - `findByDepartmentAndEmploymentStatus()`: Combined filter
  - `findByEmployeeNameContains(String)`: Search by name
  - `deleteById(String)`: Delete employee
  - `existsById(String)`: Check existence
  - `count()`: Total count
  - `countByEmploymentStatus()`: Count by status
- **Implementation**: Hibernate Session + Transaction management

#### CandidateRepository & CandidateRepositoryImpl
- **Location**: `recruitment/repository/CandidateRepository.java` (interface), `recruitment/repository/CandidateRepositoryImpl.java` (implementation)
- **Custom Methods**:
  - `save(Candidate)`: Insert/update candidate
  - `findById(String id)`: Find by ID
  - `findAll()`: Get all candidates
  - `findAllByStatus(String status)`: Filter by status (handles null for "ALL")
  - `findByContactInfo(String)`: Search by contact
  - `update(Candidate)`: Update candidate
  - `delete(String id)`: Delete candidate
  - `countByStatus(String)`: Count by status
  - `countAll()`: Total count
- **Implementation**: Hibernate Session + Transaction management + proper error handling

### Validation Chain (Recruitment Module)

#### ValidationHandler (Abstract Base)
- **Location**: `recruitment/validation/ValidationHandler.java`
- **Pattern**: Chain of Responsibility abstract class
- **Methods**:
  - `setNext(ValidationHandler)`: Set next validator in chain
  - `validate(Candidate)`: Abstract method for validation logic
  - Protected `next` field: Reference to next handler

#### ContactInfoValidator
- **Location**: `recruitment/validation/ContactInfoValidator.java`
- **Validation**: Ensures contact info is not null/empty
- **Error**: Throws `CandidateDataIncompleteException` if invalid
- **Next**: Passes to ResumeValidator

#### ResumeValidator
- **Location**: `recruitment/validation/ResumeValidator.java`
- **Validation**: Ensures resume data is not null/empty
- **Error**: Throws `CandidateDataIncompleteException` if invalid
- **Next**: Passes to DuplicateCheckHandler

#### DuplicateCheckHandler
- **Location**: `recruitment/validation/DuplicateCheckHandler.java`
- **Validation**: Checks if candidate already exists in database (by contact info)
- **Logic**: 
  - For new candidates (candidateId == null): Check for duplicate by contact
  - For existing candidates: Skip duplicate check to allow updates
- **Error**: Throws `CandidateDataIncompleteException` if duplicate found
- **Next**: None (terminal handler)

### Exception Classes

#### EmployeeNotFoundException
- **Location**: `exception/EmployeeNotFoundException.java`
- **When Thrown**: When employee is not found in database
- **Handling**: Caught in service layer, displayed as error dialog

#### DuplicateEmployeeIdException
- **Location**: `exception/DuplicateEmployeeIdException.java`
- **When Thrown**: When attempting to create employee with duplicate ID
- **Handling**: Caught in service layer, displayed as error dialog

#### CandidateDataIncompleteException
- **Location**: `recruitment/exception/CandidateDataIncompleteException.java`
- **When Thrown**: During validation chain if candidate data is incomplete
- **Handling**: Caught in service layer, displayed as error dialog to user
- **Triggers**: Invalid contact info, empty resume, duplicate candidate

### UI Components

#### Employee Management
- **DashboardView.java**: Statistics dashboard with 4 metric cards
- **EmployeeListView.java**: Table-based list with search and filters
- **AddEmployeeForm.java**: Form dialog to create new employee
- **UpdateEmployeeForm.java**: Form dialog to edit employee
- **DialogUtil.java**: Reusable dialog utilities

#### Recruitment Management
- **RecruitmentDashboardView.java**: Statistics dashboard with 6 metric cards (colored borders)
- **CandidateListView.java**: Table-based list with search and status filter
- **AddCandidateForm.java**: Form dialog to create new candidate (with validation feedback)
- **UpdateCandidateForm.java**: Form dialog to edit candidate details
- **StatusUpdateForm.java**: Dialog with dropdown for status transitions

#### Main Application
- **EmployeeManagementApp.java**: Main Swing window with sidebar navigation
- **MainWindow**: BorderLayout-based UI with 7 sidebar buttons
- **Sidebar Navigation**:
  1. Dashboard (Employee)
  2. Employee List
  3. Recruitment Dashboard
  4. Candidate List
  5. Logout/Exit (if applicable)

## Error Handling

The application implements comprehensive error handling:
- **Validation Errors**: Validated at form level before submission
- **Business Logic Errors**: Caught in service layer with specific exception types
- **Database Errors**: Caught and logged with user-friendly messages
- **UI Dialogs**: All errors displayed as information/warning/error dialogs

## Logging

Logs are configured using Logback:
- **Console**: DEBUG messages displayed in console
- **File**: INFO level messages saved to `logs/eims.log`
- **Rolling**: Log files rotate daily or at 10MB

Access logs at: `logs/eims.log`

## Configuration

### Database Configuration
Located in: `src/main/java/com/yourname/myapp/config/HibernateUtil.java`

Key settings:
- Driver: com.mysql.cj.jdbc.Driver
- Dialect: MySQL8Dialect
- Auto DDL: Update (auto-creates/updates schema)
- Show SQL: true (for debugging)
- Batch Size: 10 (for performance)

### Logging Configuration
Located in: `src/main/resources/logback.xml`

## Troubleshooting

### Database Connection Issues
- Ensure MySQL is running
- Check database credentials in HibernateUtil.java
- Verify database exists (eims_db)
- Check MySQL port (default: 3306)

### JavaFX Not Starting
- Ensure Java 11+ is installed
- Verify JavaFX libraries are in classpath (Maven handles this)
- Check for display server (on Linux, may need DISPLAY variable)

### Employee Not Found Error
- Verify employee ID exists
- Check if employee was deleted
- Reload the employee list

## Future Enhancements

- Export employee data to Excel/PDF
- Email notifications for employee updates
- Authentication and user roles
- Attendance tracking
- Salary management
- Performance reviews
- Advanced reporting and analytics
- Data backup and restore functionality

## License

This project is created for educational purposes.

## Support

For issues or questions, check the error logs in `logs/eims.log` for detailed error messages.

---

**Version**: 1.0  
**Last Updated**: April 2026