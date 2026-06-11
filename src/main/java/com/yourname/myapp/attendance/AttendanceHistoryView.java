package com.yourname.myapp.attendance;

import com.yourname.myapp.entity.AttendanceRecord;
import com.yourname.myapp.service.AttendanceService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class AttendanceHistoryView extends JPanel {

    private final AttendanceService attendanceService;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField empIdFilter;
    private JTextField fromDateFilter;
    private JTextField toDateFilter;

    public AttendanceHistoryView(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
        initUI();
        // Load data after UI is fully initialized
        SwingUtilities.invokeLater(this::loadAll);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Attendance History"));

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        filterPanel.setBackground(new Color(245, 247, 250));

        filterPanel.add(new JLabel("Employee ID:"));
        empIdFilter = new JTextField(12);
        filterPanel.add(empIdFilter);

        filterPanel.add(new JLabel("From (YYYY-MM-DD):"));
        fromDateFilter = new JTextField(10);
        filterPanel.add(fromDateFilter);

        filterPanel.add(new JLabel("To (YYYY-MM-DD):"));
        toDateFilter = new JTextField(10);
        filterPanel.add(toDateFilter);

        JButton filterByEmpBtn = new JButton("Filter by Employee");
        styleButton(filterByEmpBtn, new Color(52, 152, 219));
        filterByEmpBtn.addActionListener(e -> filterByEmployee());
        filterPanel.add(filterByEmpBtn);

        JButton filterByDateBtn = new JButton("Filter by Date Range");
        styleButton(filterByDateBtn, new Color(155, 89, 182));
        filterByDateBtn.addActionListener(e -> filterByDateRange());
        filterPanel.add(filterByDateBtn);

        JButton showAllBtn = new JButton("Show All");
        styleButton(showAllBtn, new Color(127, 140, 141));
        showAllBtn.addActionListener(e -> loadAll());
        filterPanel.add(showAllBtn);

        add(filterPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Employee ID", "Date", "Check-In", "Check-Out", "Overtime (hrs)"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(26);
        table.getTableHeader().setBackground(new Color(33, 97, 140));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.setSelectionBackground(new Color(214, 234, 248));

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Record count label
        JLabel countLabel = new JLabel("  Total records shown above");
        countLabel.setForeground(Color.GRAY);
        add(countLabel, BorderLayout.SOUTH);
    }

    private void loadAll() {
        List<AttendanceRecord> records = attendanceService.getAllAttendance();
        populateTable(records);
    }

    public void refresh() {
        loadAll();
    }

    private void filterByEmployee() {
        String empId = empIdFilter.getText().trim();
        if (empId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an Employee ID.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        List<AttendanceRecord> records = attendanceService.getAttendanceByEmployeeId(empId);
        populateTable(records);
    }

    private void filterByDateRange() {
        try {
            LocalDate from = LocalDate.parse(fromDateFilter.getText().trim());
            LocalDate to = LocalDate.parse(toDateFilter.getText().trim());
            List<AttendanceRecord> records = attendanceService.getAttendanceByDateRange(from, to);
            populateTable(records);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.", "Format Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateTable(List<AttendanceRecord> records) {
        tableModel.setRowCount(0);
        for (AttendanceRecord r : records) {
            tableModel.addRow(new Object[]{
                    r.getId(),
                    r.getEmployeeId(),
                    r.getAttendanceDate(),
                    r.getCheckInTime() != null ? r.getCheckInTime() : "—",
                    r.getCheckOutTime() != null ? r.getCheckOutTime() : "—",
                    r.getOvertimeHours()
            });
        }
    }

    private void styleButton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 11));
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
