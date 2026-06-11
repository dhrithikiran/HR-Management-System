package com.yourname.myapp.ui.benefits;

import com.yourname.myapp.entity.Claim;
import com.yourname.myapp.service.ClaimService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class ClaimsListView extends JPanel {

    private final ClaimService claimService;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public ClaimsListView(ClaimService claimService) {
        this.claimService = claimService;
        initUI();
        loadAll();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Claims"));

        // Top panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        topPanel.setBackground(new Color(245, 247, 250));

        topPanel.add(new JLabel("Search by Employee ID:"));
        searchField = new JTextField(12);
        topPanel.add(searchField);

        JButton searchBtn = new JButton("Search");
        styleButton(searchBtn, new Color(52, 152, 219));
        searchBtn.addActionListener(e -> searchClaims());
        topPanel.add(searchBtn);

        JButton showAllBtn = new JButton("Show All");
        styleButton(showAllBtn, new Color(127, 140, 141));
        showAllBtn.addActionListener(e -> loadAll());
        topPanel.add(showAllBtn);

        JButton approveBtn = new JButton("✓ Approve Selected");
        styleButton(approveBtn, new Color(39, 174, 96));
        approveBtn.addActionListener(e -> updateStatus("APPROVED"));
        topPanel.add(approveBtn);

        add(topPanel, BorderLayout.NORTH);

        // Add Claim Form (inline)
        JPanel addClaimPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        addClaimPanel.setBackground(new Color(236, 240, 241));
        addClaimPanel.setBorder(BorderFactory.createTitledBorder("Add New Claim"));

        JTextField empIdField = new JTextField(10);
        JTextField claimTypeField = new JTextField(10);
        JTextField amountField = new JTextField(8);

        addClaimPanel.add(new JLabel("Employee ID:"));
        addClaimPanel.add(empIdField);
        addClaimPanel.add(new JLabel("Claim Type:"));
        addClaimPanel.add(claimTypeField);
        addClaimPanel.add(new JLabel("Amount:"));
        addClaimPanel.add(amountField);

        JButton addBtn = new JButton("Add Claim");
        styleButton(addBtn, new Color(243, 156, 18));
        addBtn.addActionListener(e -> {
            try {
                String empId = empIdField.getText().trim();
                String type = claimTypeField.getText().trim();
                BigDecimal amount = new BigDecimal(amountField.getText().trim());
                if (empId.isEmpty() || type.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Employee ID and Claim Type are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                claimService.addClaim(empId, type, amount);
                JOptionPane.showMessageDialog(this, "Claim added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                empIdField.setText(""); claimTypeField.setText(""); amountField.setText("");
                loadAll();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount. Enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        addClaimPanel.add(addBtn);

        add(addClaimPanel, BorderLayout.SOUTH);

        // Table
        String[] columns = {"ID", "Employee ID", "Claim Type", "Amount", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(26);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setBackground(new Color(243, 156, 18));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void loadAll() {
        List<Claim> list = claimService.getAllClaims();
        populateTable(list);
    }

    private void searchClaims() {
        String empId = searchField.getText().trim();
        if (empId.isEmpty()) { loadAll(); return; }
        populateTable(claimService.getByEmployeeId(empId));
    }

    private void populateTable(List<Claim> list) {
        tableModel.setRowCount(0);
        for (Claim c : list) {
            tableModel.addRow(new Object[]{
                    c.getId(), c.getEmployeeId(), c.getClaimType(), c.getAmount(), c.getClaimStatus()
            });
        }
    }

    private void updateStatus(String status) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a claim.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Long id = (Long) tableModel.getValueAt(row, 0);
        try {
            claimService.updateClaimStatus(id, status);
            JOptionPane.showMessageDialog(this, "Claim status updated to " + status, "Updated", JOptionPane.INFORMATION_MESSAGE);
            loadAll();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
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
