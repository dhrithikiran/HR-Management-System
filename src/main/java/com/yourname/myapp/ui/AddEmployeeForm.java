package com.yourname.myapp.ui;

import com.yourname.myapp.dto.EmployeeRequest;
import com.yourname.myapp.entity.EmploymentStatus;
import com.yourname.myapp.service.EmployeeService;
import com.yourname.myapp.ui.util.DialogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Add Employee Form dialog (Swing version).
 */
public class AddEmployeeForm extends JDialog {
    private static final Logger logger = LoggerFactory.getLogger(AddEmployeeForm.class);
    private final EmployeeService employeeService;
    private Runnable onSuccessCallback;

    // Form fields
    private JTextField nameField;
    private JComboBox<String> departmentField;
    private JComboBox<String> roleField;
    private JComboBox<EmploymentStatus> statusField;

    public AddEmployeeForm(Frame parent, EmployeeService employeeService) {
        super(parent, "Add New Employee", true);
        this.employeeService = employeeService;
        this.onSuccessCallback = null;
        initializeUI();
        setLocationRelativeTo(parent);
        setModal(true);
    }

    /**
     * Initialize the form UI
     */
    private void initializeUI() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(550, 380);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 245, 245));

        // Title
        JLabel titleLabel = new JLabel("Add New Employee");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Form fields panel
        JPanel formPanel = createFormPanel();
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

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
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Row 0: Employee Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0;
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(200, 30));
        formPanel.add(nameField, gbc);

        // Row 1: Department
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        JLabel deptLabel = new JLabel("Department:");
        deptLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        formPanel.add(deptLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        departmentField = new JComboBox<>();
        String[] defaultDepts = {"IT", "HR", "Finance", "Operations", "Sales", "Marketing", "Development"};
        for (String dept : defaultDepts) {
            departmentField.addItem(dept);
        }
        try {
            List<String> serviceDepts = employeeService.getAllDepartments();
            for (String dept : serviceDepts) {
                if (!java.util.Arrays.asList(defaultDepts).contains(dept)) {
                    departmentField.addItem(dept);
                }
            }
        } catch (Exception e) {
            logger.debug("Could not load departments from service", e);
        }
        departmentField.setPreferredSize(new Dimension(200, 30));
        formPanel.add(departmentField, gbc);

        // Row 2: Job Role
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        JLabel roleLabel = new JLabel("Job Role:");
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        formPanel.add(roleLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        roleField = new JComboBox<>();
        roleField.addItem("Manager");
        roleField.addItem("Developer");
        roleField.addItem("Analyst");
        roleField.addItem("Consultant");
        roleField.addItem("Coordinator");
        roleField.addItem("Executive");
        roleField.addItem("Other");
        roleField.setSelectedIndex(0);
        roleField.setPreferredSize(new Dimension(200, 30));
        formPanel.add(roleField, gbc);

        // Row 3: Employment Status
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        JLabel statusLabel = new JLabel("Employment Status:");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        formPanel.add(statusLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        statusField = new JComboBox<>();
        for (EmploymentStatus status : EmploymentStatus.values()) {
            statusField.addItem(status);
        }
        statusField.setSelectedIndex(0);
        statusField.setPreferredSize(new Dimension(200, 30));
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

        JButton saveButton = new JButton("Save");
        saveButton.setPreferredSize(new Dimension(100, 35));
        saveButton.setFont(new Font("Arial", Font.PLAIN, 12));
        saveButton.addActionListener(this::handleSave);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 12));
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }

    /**
     * Handle save button action
     */
    private void handleSave(ActionEvent event) {
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

            // Create employee request
            EmployeeRequest request = new EmployeeRequest();
            request.setEmployeeName(name);
            request.setDepartment(department);
            request.setJobRole(role);
            request.setEmploymentStatus(status != null ? status : EmploymentStatus.ACTIVE);

            // Save employee
            employeeService.createEmployee(request);
            DialogUtil.showInfo("Success", "Employee Added", "Employee has been added successfully");
            
            if (onSuccessCallback != null) {
                onSuccessCallback.run();
            }
            
            dispose();
            logger.info("Employee created successfully");
        } catch (Exception e) {
            logger.error("Error creating employee", e);
            DialogUtil.showError("Error", "Failed to add employee", e.getMessage());
        }
    }

    /**
     * Set callback for successful save
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
