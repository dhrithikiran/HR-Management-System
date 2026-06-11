package com.yourname.myapp.attendance;

import com.yourname.myapp.service.AttendanceService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class AttendanceLogForm extends JPanel {

    private final AttendanceService attendanceService;

    private JTextField empIdField;
    private JTextField dateField;
    private JTextField checkInField;
    private JTextField checkOutField;
    private JLabel overtimeLabel;

    public AttendanceLogForm(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Log Attendance"));
        setBackground(Color.WHITE);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Title
        JLabel title = new JLabel("Log Employee Attendance");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(new Color(33, 97, 140));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(title, gbc);
        gbc.gridwidth = 1;

        // Employee ID
        gbc.gridy = 1; gbc.gridx = 0;
        formPanel.add(new JLabel("Employee ID (EMP-XXXXXXXX):"), gbc);
        empIdField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(empIdField, gbc);

        // Date
        gbc.gridy = 2; gbc.gridx = 0;
        formPanel.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        dateField = new JTextField(LocalDate.now().toString(), 20);
        gbc.gridx = 1;
        formPanel.add(dateField, gbc);

        // Check In
        gbc.gridy = 3; gbc.gridx = 0;
        formPanel.add(new JLabel("Check-In Time (HH:MM):"), gbc);
        checkInField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(checkInField, gbc);

        // Check Out
        gbc.gridy = 4; gbc.gridx = 0;
        formPanel.add(new JLabel("Check-Out Time (HH:MM):"), gbc);
        checkOutField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(checkOutField, gbc);

        // Overtime preview
        gbc.gridy = 5; gbc.gridx = 0;
        formPanel.add(new JLabel("Overtime (auto-calculated):"), gbc);
        overtimeLabel = new JLabel("—");
        overtimeLabel.setForeground(new Color(41, 128, 185));
        gbc.gridx = 1;
        formPanel.add(overtimeLabel, gbc);

        // Preview button
        JButton previewBtn = new JButton("Preview Overtime");
        styleButton(previewBtn, new Color(52, 152, 219));
        previewBtn.addActionListener(e -> previewOvertime());
        gbc.gridy = 6; gbc.gridx = 0;
        formPanel.add(previewBtn, gbc);

        // Submit button
        JButton submitBtn = new JButton("Log Attendance");
        styleButton(submitBtn, new Color(39, 174, 96));
        submitBtn.addActionListener(e -> submitForm());
        gbc.gridx = 1;
        formPanel.add(submitBtn, gbc);

        add(formPanel, BorderLayout.CENTER);
    }

    private void previewOvertime() {
        try {
            LocalTime in = LocalTime.parse(checkInField.getText().trim());
            LocalTime out = LocalTime.parse(checkOutField.getText().trim());
            long minutes = java.time.temporal.ChronoUnit.MINUTES.between(in, out);
            double hours = minutes / 60.0;
            if (hours > 8) {
                double ot = Math.round((hours - 8) * 100.0) / 100.0;
                overtimeLabel.setText(ot + " hrs");
                overtimeLabel.setForeground(new Color(192, 57, 43));
            } else {
                overtimeLabel.setText("0.0 hrs (no overtime)");
                overtimeLabel.setForeground(new Color(39, 174, 96));
            }
        } catch (DateTimeParseException ex) {
            overtimeLabel.setText("Invalid time format");
            overtimeLabel.setForeground(Color.RED);
        }
    }

    private void submitForm() {
        try {
            String empId = empIdField.getText().trim();
            if (empId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Employee ID is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            LocalDate date = LocalDate.parse(dateField.getText().trim());
            LocalTime checkIn = checkInField.getText().trim().isEmpty() ? null
                    : LocalTime.parse(checkInField.getText().trim());
            LocalTime checkOut = checkOutField.getText().trim().isEmpty() ? null
                    : LocalTime.parse(checkOutField.getText().trim());

            attendanceService.logAttendance(empId, date, checkIn, checkOut);

            JOptionPane.showMessageDialog(this,
                    "Attendance logged successfully for " + empId,
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid date or time format. Use YYYY-MM-DD and HH:MM.",
                    "Format Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        empIdField.setText("");
        dateField.setText(LocalDate.now().toString());
        checkInField.setText("");
        checkOutField.setText("");
        overtimeLabel.setText("—");
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
