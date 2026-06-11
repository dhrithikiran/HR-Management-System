package com.yourname.myapp.ui.benefits;

import com.yourname.myapp.entity.BenefitEnrollment;
import com.yourname.myapp.service.BenefitService;

import javax.swing.*;
import java.awt.*;

public class EnrollmentForm extends JPanel {

    private final BenefitService benefitService;
    private JTextField empIdField;
    private JComboBox<String> statusCombo;
    private JTextField healthPlanField;
    private JTextField insurancePlanField;
    private JTextField updateIdField;

    public EnrollmentForm(BenefitService benefitService) {
        this.benefitService = benefitService;
        initUI();
    }

    private void initUI() {
        setLayout(new GridLayout(1, 2, 20, 0));
        setBackground(Color.WHITE);

        // Create panel
        JPanel createPanel = new JPanel(new GridBagLayout());
        createPanel.setBackground(Color.WHITE);
        createPanel.setBorder(BorderFactory.createTitledBorder("Create Enrollment"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel title = new JLabel("New Benefit Enrollment");
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        title.setForeground(new Color(39, 174, 96));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        createPanel.add(title, gbc);
        gbc.gridwidth = 1;

        gbc.gridy = 1; gbc.gridx = 0;
        createPanel.add(new JLabel("Employee ID:"), gbc);
        empIdField = new JTextField(15);
        gbc.gridx = 1; createPanel.add(empIdField, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        createPanel.add(new JLabel("Enrollment Status:"), gbc);
        statusCombo = new JComboBox<>(new String[]{"ENROLLED", "PENDING", "NOT_ENROLLED"});
        gbc.gridx = 1; createPanel.add(statusCombo, gbc);

        gbc.gridy = 3; gbc.gridx = 0;
        createPanel.add(new JLabel("Health Plan:"), gbc);
        healthPlanField = new JTextField(15);
        gbc.gridx = 1; createPanel.add(healthPlanField, gbc);

        gbc.gridy = 4; gbc.gridx = 0;
        createPanel.add(new JLabel("Insurance Plan:"), gbc);
        insurancePlanField = new JTextField(15);
        gbc.gridx = 1; createPanel.add(insurancePlanField, gbc);

        JButton createBtn = new JButton("Create Enrollment");
        styleButton(createBtn, new Color(39, 174, 96));
        createBtn.addActionListener(e -> createEnrollment());
        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 2;
        createPanel.add(createBtn, gbc);

        // Update panel
        JPanel updatePanel = new JPanel(new GridBagLayout());
        updatePanel.setBackground(Color.WHITE);
        updatePanel.setBorder(BorderFactory.createTitledBorder("Update Enrollment"));
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(8, 10, 8, 10);
        gbc2.anchor = GridBagConstraints.WEST;

        JLabel title2 = new JLabel("Update Existing Enrollment");
        title2.setFont(new Font("SansSerif", Font.BOLD, 14));
        title2.setForeground(new Color(52, 152, 219));
        gbc2.gridx = 0; gbc2.gridy = 0; gbc2.gridwidth = 2;
        updatePanel.add(title2, gbc2);
        gbc2.gridwidth = 1;

        gbc2.gridy = 1; gbc2.gridx = 0;
        updatePanel.add(new JLabel("Enrollment ID:"), gbc2);
        updateIdField = new JTextField(15);
        gbc2.gridx = 1; updatePanel.add(updateIdField, gbc2);

        JTextField uStatusField = new JTextField(15);
        JTextField uHealthField = new JTextField(15);
        JTextField uInsuranceField = new JTextField(15);

        gbc2.gridy = 2; gbc2.gridx = 0;
        updatePanel.add(new JLabel("New Status (ENROLLED/PENDING/NOT_ENROLLED):"), gbc2);
        gbc2.gridx = 1; updatePanel.add(uStatusField, gbc2);

        gbc2.gridy = 3; gbc2.gridx = 0;
        updatePanel.add(new JLabel("New Health Plan:"), gbc2);
        gbc2.gridx = 1; updatePanel.add(uHealthField, gbc2);

        gbc2.gridy = 4; gbc2.gridx = 0;
        updatePanel.add(new JLabel("New Insurance Plan:"), gbc2);
        gbc2.gridx = 1; updatePanel.add(uInsuranceField, gbc2);

        JButton updateBtn = new JButton("Update Enrollment");
        styleButton(updateBtn, new Color(52, 152, 219));
        updateBtn.addActionListener(e -> {
            try {
                Long id = Long.parseLong(updateIdField.getText().trim());
                benefitService.updateEnrollment(id, uStatusField.getText().trim(),
                        uHealthField.getText().trim(), uInsuranceField.getText().trim());
                JOptionPane.showMessageDialog(this, "Enrollment updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                updateIdField.setText(""); uStatusField.setText(""); uHealthField.setText(""); uInsuranceField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Enrollment ID.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        gbc2.gridy = 5; gbc2.gridx = 0; gbc2.gridwidth = 2;
        updatePanel.add(updateBtn, gbc2);

        add(createPanel);
        add(updatePanel);
    }

    private void createEnrollment() {
        try {
            String empId = empIdField.getText().trim();
            if (empId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Employee ID is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String status = (String) statusCombo.getSelectedItem();
            String health = healthPlanField.getText().trim();
            String insurance = insurancePlanField.getText().trim();

            benefitService.createEnrollment(empId, status, health, insurance);
            JOptionPane.showMessageDialog(this, "Enrollment created successfully!\nCoverage Status: " +
                    (status.equals("ENROLLED") ? "ACTIVE" : "INACTIVE"), "Success", JOptionPane.INFORMATION_MESSAGE);

            empIdField.setText(""); healthPlanField.setText(""); insurancePlanField.setText("");
            statusCombo.setSelectedIndex(0);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void styleButton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
