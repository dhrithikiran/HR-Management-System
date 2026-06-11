package com.yourname.myapp.ui;

import com.yourname.myapp.entity.Employee;
import com.yourname.myapp.entity.EmploymentStatus;
import com.yourname.myapp.exception.EmployeeNotFoundException;
import com.yourname.myapp.service.EmployeeService;
import com.yourname.myapp.ui.util.DialogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Employee List view with filtering capabilities (Swing version).
 */
public class EmployeeListView {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeListView.class);
    private final EmployeeService employeeService;
    private JPanel rootPane;
    private JTable employeeTable;
    private JComboBox<String> departmentFilter;
    private JComboBox<EmploymentStatus> statusFilter;
    private JTextField searchField;
    private Runnable onRefreshCallback;
    private Runnable onEditCallback;
    private Runnable onDeleteCallback;

    public EmployeeListView(EmployeeService employeeService) {
        this.employeeService = employeeService;
        initializeUI();
    }

    /**
     * Initialize the employee list UI
     */
    private void initializeUI() {
        rootPane = new JPanel(new BorderLayout(10, 10));
        rootPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        rootPane.setBackground(new Color(245, 245, 245));

        // Title
        JLabel titleLabel = new JLabel("Employee List");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        rootPane.add(titleLabel, BorderLayout.NORTH);

        // Filter bar
        JPanel filterBar = createFilterBar();
        rootPane.add(filterBar, BorderLayout.PAGE_START);

        // Employee table
        employeeTable = createEmployeeTable();
        JScrollPane scrollPane = new JScrollPane(employeeTable);
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        rootPane.add(tablePanel, BorderLayout.CENTER);

        // Button bar
        JPanel buttonBar = createButtonBar();
        rootPane.add(buttonBar, BorderLayout.SOUTH);

        // Load initial data
        loadEmployees();
    }

    /**
     * Create the filter bar
     */
    private JPanel createFilterBar() {
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterBar.setBackground(Color.WHITE);
        filterBar.setBorder(BorderFactory.createLineBorder(new Color(221, 221, 221)));

        // Search field
        JLabel searchLabel = new JLabel("Search:");
        searchField = new JTextField(20);
        searchField.setToolTipText("Enter employee name...");
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                if (searchField.getText().isEmpty()) {
                    loadEmployees();
                } else {
                    searchEmployees(searchField.getText());
                }
            }
        });

        // Department filter
        JLabel deptLabel = new JLabel("Department:");
        departmentFilter = new JComboBox<>();
        departmentFilter.addItem("All Departments");
        List<String> departments = employeeService.getAllDepartments();
        for (String dept : departments) {
            departmentFilter.addItem(dept);
        }
        departmentFilter.addActionListener(e -> applyFilters());

        // Status filter
        JLabel statusLabel = new JLabel("Status:");
        statusFilter = new JComboBox<>();
        statusFilter.addItem(null);
        for (EmploymentStatus status : EmploymentStatus.values()) {
            statusFilter.addItem(status);
        }
        statusFilter.addActionListener(e -> applyFilters());

        // Buttons
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadEmployees());

        JButton clearButton = new JButton("Clear Filters");
        clearButton.addActionListener(e -> clearFilters());

        filterBar.add(searchLabel);
        filterBar.add(searchField);
        filterBar.add(new JSeparator(SwingConstants.VERTICAL));
        filterBar.add(deptLabel);
        filterBar.add(departmentFilter);
        filterBar.add(statusLabel);
        filterBar.add(statusFilter);
        filterBar.add(new JSeparator(SwingConstants.VERTICAL));
        filterBar.add(refreshButton);
        filterBar.add(clearButton);

        return filterBar;
    }

    /**
     * Create the employee table
     */
    private JTable createEmployeeTable() {
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Employee ID", "Name", "Department", "Job Role", "Status"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setPreferredWidth(120);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);

        return table;
    }

    /**
     * Create the action button bar
     */
    private JPanel createButtonBar() {
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonBar.setBackground(Color.WHITE);
        buttonBar.setBorder(BorderFactory.createLineBorder(new Color(221, 221, 221)));

        JButton editButton = new JButton("Edit");
        editButton.setForeground(Color.BLACK);
        editButton.addActionListener(e -> {
            Employee selected = getSelectedEmployee();
            if (selected != null) {
                if (onEditCallback != null) {
                    onEditCallback.run();
                }
            } else {
                DialogUtil.showWarning("Selection Required", "", "Please select an employee to edit");
            }
        });

        JButton deleteButton = new JButton("Delete");
        deleteButton.setBackground(new Color(231, 76, 60));
        deleteButton.setForeground(Color.BLACK);
        deleteButton.addActionListener(e -> {
            Employee selected = getSelectedEmployee();
            if (selected != null) {
                if (DialogUtil.showConfirmation("Confirm Delete", "Delete Employee",
                        "Are you sure you want to delete " + selected.getEmployeeName() + "?")) {
                    deleteEmployee(selected.getEmployeeId());
                }
            } else {
                DialogUtil.showWarning("Selection Required", "", "Please select an employee to delete");
            }
        });

        buttonBar.add(editButton);
        buttonBar.add(deleteButton);

        return buttonBar;
    }

    /**
     * Load all employees into the table
     */
    public void loadEmployees() {
        try {
            List<Employee> employees = employeeService.getAllEmployees();
            DefaultTableModel model = (DefaultTableModel) employeeTable.getModel();
            model.setRowCount(0);

            for (Employee emp : employees) {
                model.addRow(new Object[]{
                        emp.getEmployeeId(),
                        emp.getEmployeeName(),
                        emp.getDepartment(),
                        emp.getJobRole(),
                        emp.getEmploymentStatus()
                });
            }
            logger.info("Loaded {} employees", employees.size());
        } catch (Exception e) {
            logger.error("Error loading employees", e);
            DialogUtil.showError("Load Error", "Failed to load employees", e.getMessage());
        }
    }

    /**
     * Apply filters
     */
    private void applyFilters() {
        try {
            String department = (String) departmentFilter.getSelectedItem();
            EmploymentStatus status = (EmploymentStatus) statusFilter.getSelectedItem();

            String deptFilter = (department != null && !department.equals("All Departments")) ? department : null;
            String statusFilter = (status != null) ? status.toString() : null;

            List<Employee> employees = employeeService.getAllEmployees(deptFilter, statusFilter);
            DefaultTableModel model = (DefaultTableModel) employeeTable.getModel();
            model.setRowCount(0);

            for (Employee emp : employees) {
                model.addRow(new Object[]{
                        emp.getEmployeeId(),
                        emp.getEmployeeName(),
                        emp.getDepartment(),
                        emp.getJobRole(),
                        emp.getEmploymentStatus()
                });
            }
        } catch (Exception e) {
            logger.error("Error applying filters", e);
            DialogUtil.showError("Filter Error", "Failed to apply filters", e.getMessage());
        }
    }

    /**
     * Search employees by name
     */
    private void searchEmployees(String searchTerm) {
        try {
            List<Employee> employees = employeeService.searchByName(searchTerm);
            DefaultTableModel model = (DefaultTableModel) employeeTable.getModel();
            model.setRowCount(0);

            for (Employee emp : employees) {
                model.addRow(new Object[]{
                        emp.getEmployeeId(),
                        emp.getEmployeeName(),
                        emp.getDepartment(),
                        emp.getJobRole(),
                        emp.getEmploymentStatus()
                });
            }
        } catch (Exception e) {
            logger.error("Error searching employees", e);
            DialogUtil.showError("Search Error", "Failed to search employees", e.getMessage());
        }
    }

    /**
     * Clear all filters
     */
    private void clearFilters() {
        searchField.setText("");
        departmentFilter.setSelectedIndex(0);
        statusFilter.setSelectedIndex(0);
        loadEmployees();
    }

    /**
     * Delete an employee
     */
    private void deleteEmployee(String employeeId) {
        try {
            employeeService.deleteEmployee(employeeId);
            DialogUtil.showInfo("Success", "Employee Deleted", "Employee has been deleted successfully");
            loadEmployees();
            if (onDeleteCallback != null) {
                onDeleteCallback.run();
            }
        } catch (EmployeeNotFoundException e) {
            DialogUtil.showError("Not Found", "Error", e.getMessage());
        } catch (Exception e) {
            logger.error("Error deleting employee", e);
            DialogUtil.showError("Delete Error", "Failed to delete employee", e.getMessage());
        }
    }

    /**
     * Get selected employee from table
     */
    public Employee getSelectedEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow >= 0) {
            DefaultTableModel model = (DefaultTableModel) employeeTable.getModel();
            String employeeId = (String) model.getValueAt(selectedRow, 0);
            return employeeService.getEmployeeById(employeeId);
        }
        return null;
    }

    /**
     * Set callback for edit action
     */
    public void setOnEditCallback(Runnable callback) {
        this.onEditCallback = callback;
    }

    /**
     * Set callback for delete action
     */
    public void setOnDeleteCallback(Runnable callback) {
        this.onDeleteCallback = callback;
    }

    /**
     * Get the root pane
     */
    public JPanel getRootPane() {
        return rootPane;
    }

    /**
     * Refresh the employee list
     */
    public void refresh() {
        loadEmployees();
    }
}
