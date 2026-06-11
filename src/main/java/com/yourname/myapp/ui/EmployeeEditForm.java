package com.yourname.myapp.ui;

import com.yourname.myapp.dto.EmployeeRequest;
import com.yourname.myapp.entity.Employee;
import com.yourname.myapp.entity.EmploymentStatus;
import com.yourname.myapp.service.EmployeeService;
import com.yourname.myapp.ui.util.DialogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Employee Edit Form Dialog
 * Allows editing of employee details including name, department, job role, and employment status
 */
public class EmployeeEditForm extends JDialog {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeEditForm.class);
    private final EmployeeService employeeService;
    private final Employee employee;
    private Runnable onSaveCallback;

    private JTextField nameField;
    private JComboBox<String> departmentCombo;
    private JTextField jobRoleField;
    private JComboBox<EmploymentStatus> statusCombo;

    public EmployeeEditForm(JFrame parent, EmployeeService employeeService, Employee employee) {
        super(parent, "Edit Employee", true);
        this.employeeService = employeeService;
        this.employee = employee;
        initializeUI();
        loadEmployeeData();
    }

    /**
     * Initialize the form UI
     */
    private void initializeUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 350);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(245, 245, 245));

        // Title
        JLabel titleLabel = new JLabel("Edit Employee");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    /**
     * Create the form input panel
     */
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(221, 221, 221)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Employee ID (read-only)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        panel.add(new JLabel("Employee ID:"), gbc);

        JTextField idField = new JTextField(employee.getEmployeeId());
        idField.setEditable(false);
        idField.setBackground(new Color(240, 240, 240));
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(idField, gbc);

        // Employee Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        panel.add(new JLabel("Name:"), gbc);

        nameField = new JTextField(employee.getEmployeeName());
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(nameField, gbc);

        // Department
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        panel.add(new JLabel("Department:"), gbc);

        departmentCombo = new JComboBox<>();
        departmentCombo.addItem(employee.getDepartment());
        List<String> departments = employeeService.getAllDepartments();
        for (String dept : departments) {
            if (!dept.equals(employee.getDepartment())) {
                departmentCombo.addItem(dept);
            }
        }
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(departmentCombo, gbc);

        // Job Role
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        panel.add(new JLabel("Job Role:"), gbc);

        jobRoleField = new JTextField(employee.getJobRole());
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(jobRoleField, gbc);

        // Employment Status
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.3;
        panel.add(new JLabel("Status:"), gbc);

        statusCombo = new JComboBox<>();
        for (EmploymentStatus status : EmploymentStatus.values()) {
            statusCombo.addItem(status);
        }
        statusCombo.setSelectedItem(employee.getEmploymentStatus());
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(statusCombo, gbc);

        return panel;
    }

    /**
     * Create the button panel
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(221, 221, 221)));

        JButton saveButton = new JButton("Save");
        saveButton.setBackground(new Color(52, 152, 219));
        saveButton.setForeground(Color.BLACK);
        saveButton.addActionListener(e -> saveEmployee());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        panel.add(saveButton);
        panel.add(cancelButton);

        return panel;
    }

    /**
     * Load employee data into form fields
     */
    private void loadEmployeeData() {
        nameField.setText(employee.getEmployeeName());
        departmentCombo.setSelectedItem(employee.getDepartment());
        jobRoleField.setText(employee.getJobRole());
        statusCombo.setSelectedItem(employee.getEmploymentStatus());
    }

    /**
     * Save employee changes
     */
    private void saveEmployee() {
        try {
            String name = nameField.getText().trim();
            String department = (String) departmentCombo.getSelectedItem();
            String jobRole = jobRoleField.getText().trim();

            // Validation
            if (name.isEmpty()) {
                DialogUtil.showWarning("Validation Error", "Invalid Input", "Employee name cannot be empty");
                return;
            }
            if (jobRole.isEmpty()) {
                DialogUtil.showWarning("Validation Error", "Invalid Input", "Job role cannot be empty");
                return;
            }

            // Create request object
            EmployeeRequest request = new EmployeeRequest();
            request.setEmployeeName(name);
            request.setDepartment(department);
            request.setJobRole(jobRole);

            // Update employee
            employeeService.updateEmployee(employee.getEmployeeId(), request);

            DialogUtil.showInfo("Success", "Employee Updated", "Employee details have been updated successfully");
            logger.info("Employee updated: {}", employee.getEmployeeId());

            if (onSaveCallback != null) {
                onSaveCallback.run();
            }

            dispose();
        } catch (Exception e) {
            logger.error("Error saving employee", e);
            DialogUtil.showError("Save Error", "Failed to save employee", e.getMessage());
        }
    }

    /**
     * Set callback for after save
     */
    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }
}
