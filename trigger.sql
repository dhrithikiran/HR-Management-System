DELIMITER $$

CREATE TRIGGER sync_employee_table
AFTER INSERT ON employees
FOR EACH ROW
BEGIN
    INSERT INTO employee (
        employee_id,
        employee_name,
        department,
        job_role,
        employment_status,
        joining_date,
        created_at,
        updated_at
    )
    VALUES (
        NEW.employeeId,
        NEW.employeeName,
        NEW.department,
        NEW.jobRole,
        NEW.employmentStatus,
        NEW.joining_date,
        NEW.created_at,
        NEW.updated_at
    );
END$$

DELIMITER ;