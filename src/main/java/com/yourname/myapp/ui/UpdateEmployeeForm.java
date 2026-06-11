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
import java.awt.event.ActionEvent;

/**
 * Form for updating an existing employee (Swing version).
 */
public class UpdateEmployeeForm extends JDialog {
    private static final Logger logger = LoggerFactory.getLogger(UpdateEmployeeForm.class);
    private final EmployeeService employeeService;
    private final Employee employee;
    private Runnable onSuccessCallback;

    // Form fields
    private JLabel idField;
    private JTextField nameField;
    private JComboBox<String> departmentField;
    private JComboBox<String> roleField;
    private JComboBox<EmploymentStatus> statusField;

    public UpdateEmployeeForm(Frame parent, EmployeeService employeeService, Employee employee) {
        super(parent, "Update Employee", true);
        this.employeeService = employeeService;
        this.employee = employee;
        initializeUI();
        setLocationRelativeTo(parent);
    }

    /**
     * Initialize the form UI with existing employee data
     */
    private void initializeUI() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(500, 450);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(245, 245, 245));

        // Title
        JLabel titleLabel = new JLabel("Update Employee");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Form fields panel
        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    /**
     * Create the form fields panel
     */
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Employee ID (read-only)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        JLabel idLabel = new JLabel("Employee ID:");
        idLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        formPanel.add(idLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        idField = new JLabel(employee.getEmployeeId());
        idField.setFont(new Font("Arial", Font.PLAIN, 12));
        idField.setBorder(BorderFactory.createLineBorder(new Color(221, 221, 221)));
        formPanel.add(idField, gbc);

        // Employee Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel nameLabel = new JLabel("Employee Name:");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        nameField = new JTextField(20);
        nameField.setText(employee.getEmployeeName());
        nameField.setPreferredSize(new Dimension(300, 25));
        formPanel.add(nameField, gbc);

        // Department
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        JLabel deptLabel = new JLabel("Department:");
        deptLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        formPanel.add(deptLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        departmentField = new JComboBox<>();
        departmentField.addItem("IT");
        departmentField.addItem("HR");
        departmentField.addItem("Finance");
        departmentField.addItem("Operations");
        departmentField.addItem("Sales");
        departmentField.addItem("Marketing");
        departmentField.addItem("Development");
        departmentField.setSelectedItem(employee.getDepartment());
        departmentField.setPreferredSize(new Dimension(300, 25));
        formPanel.add(departmentField, gbc);

        // Job Role
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        JLabel roleLabel = new JLabel("Job Role:");
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        formPanel.add(roleLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        roleField = new JComboBox<>();
        roleField.addItem("Manager");
        roleField.addItem("Developer");
        roleField.addItem("Analyst");
        roleField.addItem("Consultant");
        roleField.addItem("Coordinator");
        roleField.addItem("Executive");
        roleField.addItem("Other");
        roleField.setSelectedItem(employee.getJobRole());
        roleField.setPreferredSize(new Dimension(300, 25));
        formPanel.add(roleField, gbc);

        // Employment Status
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.3;
        JLabel statusLabel = new JLabel("Employment Status:");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        formPanel.add(statusLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        statusField = new JComboBox<>();
        for (EmploymentStatus status : EmploymentStatus.values()) {
            statusField.addItem(status);
        }
        statusField.setSelectedItem(employee.getEmploymentStatus());
        statusField.setPreferredSize(new Dimension(300, 25));
        formPanel.add(statusField, gbc);

        return formPanel;
    }

    /**
     * Create the button panel
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createLineBorder(new Color(221, 221, 221)));

        JButton updateButton = new JButton("Update");
        updateButton.setPreferredSize(new Dimension(100, 35));
        updateButton.setFont(new Font("Arial", Font.PLAIN, 12));
        updateButton.addActionListener(this::handleUpdate);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 12));
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(updateButton);
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }

    /**
     * Handle update button action
     */
    private void handleUpdate(ActionEvent event) {
        try {
            // Validate inputs
            String name = nameField.getText().trim();
            String department = (String) departmentField.getSelectedItem();
            String role = (String) roleField.getSelectedItem();
            EmploymentStatus status = (EmploymentStatus) statusField.getSelectedItem();

            if (name.isEmpty()) {
                DialogUtil.showWarning("Validation Error", "Empty Field", "Please enter employee name");
                return;
            }
            if (department == null) {
                DialogUtil.showWarning("Validation Error", "Empty Field", "Please select department");
                return;
            }
            if (role == null) {
                DialogUtil.showWarning("Validation Error", "Empty Field", "Please select job role");
                return;
            }

            // Create update request
            EmployeeRequest request = new EmployeeRequest();
            request.setEmployeeName(name);
            request.setDepartment(department);
            request.setJobRole(role);
            request.setEmploymentStatus(status != null ? status : EmploymentStatus.ACTIVE);

            // Update employee
            employeeService.updateEmployee(employee.getEmployeeId(), request);
            DialogUtil.showInfo("Success", "Employee Updated", "Employee has been updated successfully");
            
            if (onSuccessCallback != null) {
                onSuccessCallback.run();
            }
            
            dispose();
            logger.info("Employee updated successfully: {}", employee.getEmployeeId());
        } catch (Exception e) {
            logger.error("Error updating employee", e);
            DialogUtil.showError("Error", "Failed to update employee", e.getMessage());
        }
    }

    /**
     * Set callback for successful update
     */
    public void setOnSuccessCallback(Runnable callback) {
        this.onSuccessCallback = callback;
    }

    /**
     * Show the form
     */
    @Override
    public void show() {
        super.show();
    }
}
