package com.yourname.myapp.attendance;

import com.yourname.myapp.facade.LeaveManagementFacade;
import com.yourname.myapp.service.AttendanceService;
import com.yourname.myapp.service.LeaveService;

import javax.swing.*;
import java.awt.*;

/**
 * Main entry point for the Attendance & Leave module.
 * This panel contains tabs for all sub-screens.
 * Add this to the existing EmployeeManagementApp's main tabbed pane.
 *
 * Usage in EmployeeManagementApp.java:
 *   tabbedPane.addTab("Attendance & Leave", new AttendanceLeaveMainView());
 */
public class AttendanceLeaveMainView extends JPanel {

    public AttendanceLeaveMainView() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Header
        JLabel header = new JLabel("  Attendance & Leave Management", SwingConstants.LEFT);
        header.setFont(new Font("SansSerif", Font.BOLD, 18));
        header.setForeground(Color.WHITE);
        header.setOpaque(true);
        header.setBackground(new Color(142, 68, 173));
        header.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        add(header, BorderLayout.NORTH);

        // Services & Facade
        AttendanceService attendanceService = new AttendanceService();
        LeaveService leaveService = new LeaveService();
        LeaveManagementFacade facade = new LeaveManagementFacade();

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.PLAIN, 13));

        tabs.addTab("📊 Dashboard", new LeaveDashboardView(leaveService));
        tabs.addTab("📋 Log Attendance", new AttendanceLogForm(attendanceService));
        AttendanceHistoryView attendanceHistoryView = new AttendanceHistoryView(attendanceService);
        tabs.addTab("🗓 Attendance History", attendanceHistoryView);
        
        // Refresh attendance history when tab is selected
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedIndex() == 2) { // Attendance History tab
                attendanceHistoryView.refresh();
            }
        });
        
        tabs.addTab("📝 Request Leave", new LeaveRequestForm(leaveService));
        tabs.addTab("📁 Leave Requests", new LeaveListView(leaveService, facade));

        add(tabs, BorderLayout.CENTER);
    }
}
