package com.yourname.myapp.attendance;

import com.yourname.myapp.exception.InvalidDateRangeException;
import com.yourname.myapp.service.LeaveService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class LeaveRequestForm extends JPanel {

    private final LeaveService leaveService;
    private JTextField empIdField;
    private JTextField fromDateField;
    private JTextField toDateField;
    private JLabel balanceLabel;

    public LeaveRequestForm(LeaveService leaveService) {
        this.leaveService = leaveService;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Submit Leave Request"));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel title = new JLabel("New Leave Request");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(new Color(142, 68, 173));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(title, gbc);
        gbc.gridwidth = 1;

        // Employee ID
        gbc.gridy = 1; gbc.gridx = 0;
        formPanel.add(new JLabel("Employee ID:"), gbc);
        empIdField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(empIdField, gbc);

        // Check balance button
        JButton checkBalBtn = new JButton("Check Balance");
        styleButton(checkBalBtn, new Color(52, 152, 219));
        checkBalBtn.addActionListener(e -> checkBalance());
        gbc.gridy = 2; gbc.gridx = 0;
        formPanel.add(checkBalBtn, gbc);

        balanceLabel = new JLabel("—");
        balanceLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        gbc.gridx = 1;
        formPanel.add(balanceLabel, gbc);

        // From date
        gbc.gridy = 3; gbc.gridx = 0;
        formPanel.add(new JLabel("From Date (YYYY-MM-DD):"), gbc);
        fromDateField = new JTextField(LocalDate.now().toString(), 20);
        gbc.gridx = 1;
        formPanel.add(fromDateField, gbc);

        // To date
        gbc.gridy = 4; gbc.gridx = 0;
        formPanel.add(new JLabel("To Date (YYYY-MM-DD):"), gbc);
        toDateField = new JTextField(LocalDate.now().plusDays(1).toString(), 20);
        gbc.gridx = 1;
        formPanel.add(toDateField, gbc);

        // Submit
        JButton submitBtn = new JButton("Submit Leave Request");
        styleButton(submitBtn, new Color(39, 174, 96));
        submitBtn.addActionListener(e -> submitRequest());
        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 2;
        formPanel.add(submitBtn, gbc);

        add(formPanel, BorderLayout.CENTER);
    }

    private void checkBalance() {
        String empId = empIdField.getText().trim();
        if (empId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Employee ID first.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int balance = leaveService.getBalance(empId);
        balanceLabel.setText(balance + " days remaining");
        if (balance <= 2) {
            balanceLabel.setForeground(new Color(192, 57, 43));
            JOptionPane.showMessageDialog(this,
                    "Warning: Low leave balance! Only " + balance + " days remaining.",
                    "Low Balance Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            balanceLabel.setForeground(new Color(39, 174, 96));
        }
    }

    private void submitRequest() {
        try {
            String empId = empIdField.getText().trim();
            if (empId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Employee ID is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            LocalDate from = LocalDate.parse(fromDateField.getText().trim());
            LocalDate to = LocalDate.parse(toDateField.getText().trim());

            LeaveService.LeaveResult result = leaveService.createLeaveRequest(empId, from, to);

            if (result.lowBalanceWarning) {
                JOptionPane.showMessageDialog(this,
                        result.message,
                        "Request Submitted (Low Balance Warning)", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        result.message,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            clearForm();

        } catch (InvalidDateRangeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid Date Range", JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.", "Format Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        empIdField.setText("");
        fromDateField.setText(LocalDate.now().toString());
        toDateField.setText(LocalDate.now().plusDays(1).toString());
        balanceLabel.setText("—");
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
