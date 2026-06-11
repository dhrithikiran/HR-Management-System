package com.yourname.myapp.attendance;

import com.yourname.myapp.entity.LeaveRequest;
import com.yourname.myapp.exception.LeaveBalanceExceededException;
import com.yourname.myapp.facade.LeaveManagementFacade;
import com.yourname.myapp.service.LeaveService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LeaveListView extends JPanel {

    private final LeaveService leaveService;
    private final LeaveManagementFacade facade;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusFilter;

    public LeaveListView(LeaveService leaveService, LeaveManagementFacade facade) {
        this.leaveService = leaveService;
        this.facade = facade;
        initUI();
        loadLeaves("ALL");
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Leave Requests"));

        // Top panel: filter + actions
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        topPanel.setBackground(new Color(245, 247, 250));

        topPanel.add(new JLabel("Filter by Status:"));
        statusFilter = new JComboBox<>(new String[]{"ALL", "PENDING", "APPROVED", "REJECTED"});
        statusFilter.addActionListener(e -> loadLeaves((String) statusFilter.getSelectedItem()));
        topPanel.add(statusFilter);

        JButton refreshBtn = new JButton("Refresh");
        styleButton(refreshBtn, new Color(127, 140, 141));
        refreshBtn.addActionListener(e -> loadLeaves((String) statusFilter.getSelectedItem()));
        topPanel.add(refreshBtn);

        JButton approveBtn = new JButton("✓ Approve Selected");
        styleButton(approveBtn, new Color(39, 174, 96));
        approveBtn.addActionListener(e -> approveSelected());
        topPanel.add(approveBtn);

        JButton rejectBtn = new JButton("✗ Reject Selected");
        styleButton(rejectBtn, new Color(192, 57, 43));
        rejectBtn.addActionListener(e -> rejectSelected());
        topPanel.add(rejectBtn);

        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Employee ID", "From", "To", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(26);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setBackground(new Color(142, 68, 173));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.setSelectionBackground(new Color(245, 183, 177));

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void loadLeaves(String status) {
        tableModel.setRowCount(0);
        List<LeaveRequest> list = "ALL".equals(status)
                ? leaveService.getAllLeaves()
                : leaveService.getLeavesByStatus(status);
        for (LeaveRequest r : list) {
            tableModel.addRow(new Object[]{
                    r.getId(),
                    r.getEmployeeId(),
                    r.getLeaveFromDate(),
                    r.getLeaveToDate(),
                    r.getLeaveStatus()
            });
        }
    }

    private void approveSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a leave request to approve.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Long id = (Long) tableModel.getValueAt(row, 0);
        try {
            facade.approveLeave(id);
            JOptionPane.showMessageDialog(this, "Leave request approved successfully.", "Approved", JOptionPane.INFORMATION_MESSAGE);
            loadLeaves((String) statusFilter.getSelectedItem());
        } catch (LeaveBalanceExceededException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Insufficient Balance", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rejectSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a leave request to reject.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Long id = (Long) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to reject this leave request?",
                "Confirm Rejection", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            facade.rejectLeave(id);
            JOptionPane.showMessageDialog(this, "Leave request rejected.", "Rejected", JOptionPane.INFORMATION_MESSAGE);
            loadLeaves((String) statusFilter.getSelectedItem());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
