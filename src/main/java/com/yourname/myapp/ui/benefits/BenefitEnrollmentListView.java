package com.yourname.myapp.ui.benefits;

import com.yourname.myapp.adapter.BenefitPlan;
import com.yourname.myapp.entity.BenefitEnrollment;
import com.yourname.myapp.service.BenefitService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BenefitEnrollmentListView extends JPanel {

    private final BenefitService benefitService;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public BenefitEnrollmentListView(BenefitService benefitService) {
        this.benefitService = benefitService;
        initUI();
        loadAll();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Benefit Enrollments"));

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        searchPanel.setBackground(new Color(245, 247, 250));
        searchPanel.add(new JLabel("Search by Employee ID:"));
        searchField = new JTextField(15);
        searchPanel.add(searchField);

        JButton searchBtn = new JButton("Search");
        styleButton(searchBtn, new Color(52, 152, 219));
        searchBtn.addActionListener(e -> searchByEmployee());
        searchPanel.add(searchBtn);

        JButton showAllBtn = new JButton("Show All");
        styleButton(showAllBtn, new Color(127, 140, 141));
        showAllBtn.addActionListener(e -> loadAll());
        searchPanel.add(showAllBtn);

        JButton viewPlanBtn = new JButton("View Plan Details");
        styleButton(viewPlanBtn, new Color(142, 68, 173));
        viewPlanBtn.addActionListener(e -> viewPlanDetails());
        searchPanel.add(viewPlanBtn);

        add(searchPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Employee ID", "Enrollment Status", "Health Plan", "Insurance Plan", "Coverage Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(26);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setBackground(new Color(39, 174, 96));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.setSelectionBackground(new Color(200, 230, 201));

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void loadAll() {
        List<BenefitEnrollment> list = benefitService.getAllEnrollments();
        populateTable(list);
    }

    private void searchByEmployee() {
        String empId = searchField.getText().trim();
        if (empId.isEmpty()) { loadAll(); return; }
        List<BenefitEnrollment> list = benefitService.getByEmployeeId(empId);
        populateTable(list);
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No enrollment found for Employee ID: " + empId, "Not Found", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void populateTable(List<BenefitEnrollment> list) {
        tableModel.setRowCount(0);
        for (BenefitEnrollment e : list) {
            tableModel.addRow(new Object[]{
                    e.getId(),
                    e.getEmployeeId(),
                    e.getEnrollmentStatus(),
                    e.getHealthPlan() != null ? e.getHealthPlan() : "—",
                    e.getInsurancePlan() != null ? e.getInsurancePlan() : "—",
                    e.getInsuranceCoverageStatus()
            });
        }
    }

    private void viewPlanDetails() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an enrollment first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String empId = (String) tableModel.getValueAt(row, 1);
        BenefitPlan health = benefitService.getHealthPlanDetails(empId);
        BenefitPlan insurance = benefitService.getInsurancePlanDetails(empId);

        String message = health.getPlanName() + "\n" + health.getCoverageDetails()
                + "\n\n" + insurance.getPlanName() + "\n" + insurance.getCoverageDetails();

        JOptionPane.showMessageDialog(this, message, "Plan Details for " + empId, JOptionPane.INFORMATION_MESSAGE);
    }

    public void refresh() { loadAll(); }

    private void styleButton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 11));
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
