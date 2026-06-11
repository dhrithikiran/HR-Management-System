package com.yourname.myapp.onboarding.ui;

import com.yourname.myapp.onboarding.entity.OnboardingRecord;
import com.yourname.myapp.onboarding.service.OnboardingService;
import com.yourname.myapp.onboarding.service.OnboardingServiceImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class OnboardingDashboardView {

    private final OnboardingService onboardingService = new OnboardingServiceImpl();

    private JPanel rootPane;
    private JTable table;
    private DefaultTableModel tableModel;

    public OnboardingDashboardView() {
        initializeUI();
    }

    private void initializeUI() {

        rootPane = new JPanel(new BorderLayout(10, 10));
        rootPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Onboarding Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 24));

        rootPane.add(title, BorderLayout.NORTH);
        rootPane.add(createStatsPanel(), BorderLayout.NORTH);
        rootPane.add(createTablePanel(), BorderLayout.CENTER);
        rootPane.add(createButtonBar(), BorderLayout.SOUTH);
    }

    private JPanel createStatsPanel() {

        JPanel panel = new JPanel(new GridLayout(1, 4, 10, 10));

        Map<String, Object> stats = onboardingService.getOnboardingStats();

        panel.add(createCard("Total", stats.get("totalRecords"), Color.BLUE));
        panel.add(createCard("Active", stats.get("activeOnboarding"), Color.GREEN));
        panel.add(createCard("Verified", stats.get("verified"), Color.MAGENTA));
        panel.add(createCard("Pending", stats.get("pending"), Color.ORANGE));

        return panel;
    }

    private JPanel createCard(String title, Object value, Color color) {

        JPanel card = new JPanel(new BorderLayout());

        JLabel t = new JLabel(title);
        JLabel v = new JLabel(String.valueOf(value), SwingConstants.CENTER);

        v.setFont(new Font("Arial", Font.BOLD, 20));
        v.setForeground(color);

        card.add(t, BorderLayout.NORTH);
        card.add(v, BorderLayout.CENTER);

        return card;
    }

    private JPanel createTablePanel() {

        tableModel = new DefaultTableModel(
                new String[]{"ID", "Candidate ID", "Name",
                        "Background Verification Status",
                        "Document Verification Status",
                        "Verified", "Pipeline"}, 0
        );

        table = new JTable(tableModel);

        loadRecords();

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    public void loadRecords() {

        tableModel.setRowCount(0);

        List<OnboardingRecord> records = onboardingService.getAllRecords();

        for (OnboardingRecord r : records) {

            tableModel.addRow(new Object[]{
                    r.getOnboardingId(),
                    r.getAssignedEmployeeId(),
                    r.getEmployeeName(),
                    r.getBackgroundCheckStatus(),
                    r.getDocumentVerificationStatus(),
                    r.isVerifiedRecord(),
                    r.getPipelineStatus()
            });
        }

        tableModel.fireTableDataChanged();
    }

    private JPanel createButtonBar() {

        JPanel bar = new JPanel(new FlowLayout());

        JButton add = new JButton("Add Record");
        add.addActionListener(e -> {
            String empId = JOptionPane.showInputDialog("Enter Employee ID");

            try {
                onboardingService.createRecord(empId);
                loadRecords();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(rootPane, ex.getMessage());
            }
        });

        JButton update = new JButton("Update Record");
        update.addActionListener(e -> {

            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(rootPane, "Select a record first");
                return;
            }

            String id = (String) tableModel.getValueAt(row, 0);
            new UpdateOnboardingForm(this, id);
        });

        JButton approve = new JButton("Approve Onboarding");
        approve.addActionListener(e -> {

            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(rootPane, "Select a record first");
                return;
            }

            String id = (String) tableModel.getValueAt(row, 0);

            try {
                onboardingService.approveOnboarding(id);
                loadRecords(); // ✅ ONLY runs on success
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(rootPane,
                        "Approval Failed: " + ex.getMessage());
            }
        });

        JButton delete = new JButton("Delete Record");
        delete.setForeground(Color.BLACK);
        delete.addActionListener(e -> {

            int row = table.getSelectedRow();
            if (row == -1) return;

            String id = (String) tableModel.getValueAt(row, 0);

            onboardingService.deleteRecord(id);
            loadRecords();
        });

        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> loadRecords());

        bar.add(add);
        bar.add(update);
        bar.add(approve);
        bar.add(delete);
        bar.add(refresh);

        return bar;
    }

    public JPanel getRootPane() {
        return rootPane;
    }

    public void refresh() {
        loadRecords();
    }
}