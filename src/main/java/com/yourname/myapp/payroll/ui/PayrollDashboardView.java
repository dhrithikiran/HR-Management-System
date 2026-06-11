package com.yourname.myapp.payroll.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

import com.yourname.myapp.payroll.exception.PayrollAlreadyExistsException;
import com.yourname.myapp.payroll.exception.InvalidYearException;

import com.yourname.myapp.payroll.entity.Payroll;
import com.yourname.myapp.payroll.service.PayrollService;
import com.yourname.myapp.payroll.service.PayrollServiceImpl;


public class PayrollDashboardView extends JPanel {

    private PayrollService service = new PayrollServiceImpl();
    private JTable table;
    private DefaultTableModel model;

    private JTextField searchField;
    private JComboBox<String> roleFilter;
    private JComboBox<String> monthFilter;
    private JTextField yearField;


    public PayrollDashboardView() {

        setLayout(new BorderLayout());

        // ✅ TITLE
        JLabel title = new JLabel("Payroll Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));

        // ✅ TOP PANEL (SEARCH + FILTER + BUTTONS)
        JPanel topPanel = new JPanel();

        searchField = new JTextField(15);

        String[] roles = {"All", "Manager", "Developer", "Analyst", "Consultant", "Coordinator", "Executive", "Other"};
        roleFilter = new JComboBox<>(roles);

        String[] months = {
            "JANUARY","FEBRUARY","MARCH","APRIL","MAY","JUNE",
            "JULY","AUGUST","SEPTEMBER","OCTOBER","NOVEMBER","DECEMBER"
        };

        monthFilter = new JComboBox<>(months);
        yearField = new JTextField(5);

        // default current year
        yearField.setText(String.valueOf(java.time.LocalDate.now().getYear()));

        JButton refreshBtn = new JButton("Refresh");
        JButton generateBtn = new JButton("Generate Payroll");
        JButton editBtn = new JButton("Edit Payroll");

        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);

        topPanel.add(new JLabel("Role:"));
        topPanel.add(roleFilter);

        topPanel.add(new JLabel("Month:"));
        topPanel.add(monthFilter);

        topPanel.add(new JLabel("Year:"));
        topPanel.add(yearField);

        topPanel.add(refreshBtn);
        topPanel.add(generateBtn);
        topPanel.add(editBtn);

        // ✅ HEADER (FIXED PROPERLY)
        JPanel header = new JPanel(new BorderLayout());
        header.add(title, BorderLayout.NORTH);
        header.add(topPanel, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);

        // ✅ TABLE
        String[] columns = {
                "Employee ID", "Name", "Role",
                "Gross", "Deductions", "Net", "Status"
        };

        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // ✅ LISTENERS
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                refreshTable();
            }
        });

        roleFilter.addActionListener(e -> refreshTable());

        generateBtn.addActionListener(e -> generateForSelectedMonth());

        monthFilter.addActionListener(e -> refreshTable());
        yearField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                refreshTable();
            }
        });

        refreshBtn.addActionListener(e -> refreshTable());

        editBtn.addActionListener(e -> openEditDialog());

        // ✅ LOAD DATA
        refreshTable();
    }

    private void generateForSelectedMonth() {

        String month = (String) monthFilter.getSelectedItem();

        try {
            int year;
            try {
                year = Integer.parseInt(yearField.getText());
            } catch (Exception e) {
                throw new InvalidYearException(yearField.getText());
            }
            service.generatePayrollForMonth(month, year);

            JOptionPane.showMessageDialog(this, "Payroll generated successfully!");

        } catch (InvalidYearException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());

        } catch (PayrollAlreadyExistsException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }

        refreshTable();
    }

    // 🔥 EDIT DIALOG
    private void openEditDialog() {

        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee first!");
            return;
        }

        String empId = (String) model.getValueAt(selectedRow, 0);
        String month = (String) monthFilter.getSelectedItem();
        int year = Integer.parseInt(yearField.getText());

        Payroll payroll = service.getPayrollByEmployeeAndMonth(empId, month, year);

        if (payroll == null) {
            JOptionPane.showMessageDialog(this, "Payroll not found!");
            return;
        }

        JDialog dialog = new JDialog((Frame) null, "Edit Payroll", true);
        dialog.setSize(300, 200);
        dialog.setLayout(new FlowLayout());

        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Pending", "Success"});
        statusBox.setSelectedItem(payroll.getSalaryTransferRecord());

        JButton saveBtn = new JButton("Save");

        dialog.add(new JLabel("Update Status:"));
        dialog.add(statusBox);
        dialog.add(saveBtn);

        saveBtn.addActionListener(ev -> {

            payroll.setSalaryTransferRecord((String) statusBox.getSelectedItem());

            service.updatePayroll(payroll); // ✅ SAVE TO DB

            dialog.dispose();

            refreshTable(); // reload UI
        });

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // 🔥 LOAD DATA FROM DB (NOT GENERATION)
    private void loadData(DefaultTableModel model) {

        String searchText = searchField.getText().toLowerCase();
        String selectedRole = (String) roleFilter.getSelectedItem();
        String selectedMonth = (String) monthFilter.getSelectedItem();
        int selectedYear;

        try {
            selectedYear = Integer.parseInt(yearField.getText());
        } catch (Exception e) {
            selectedYear = java.time.LocalDate.now().getYear();
        }

        List<Payroll> list = service.getAllPayrolls(); // ✅ FIXED

        for (Payroll p : list) {

            String name = p.getEmployee().getEmployeeName().toLowerCase();
            String empId = p.getEmployee().getEmployeeId().toLowerCase();
            String role = p.getRole();

            boolean matchesSearch = name.contains(searchText) || empId.contains(searchText);
            boolean matchesRole = selectedRole.equals("All") || role.equals(selectedRole);
            boolean matchesMonth = p.getMonth().equals(selectedMonth) && p.getYear() == selectedYear;
            
            if (matchesSearch && matchesRole && matchesMonth) {
                model.addRow(new Object[]{
                        p.getEmployee().getEmployeeId(),
                        p.getEmployee().getEmployeeName(),
                        role,
                        p.getGrossSalary(),
                        p.getDeductions(),
                        p.getNetPay(),
                        p.getSalaryTransferRecord()
                });
            }
        }
    }

    private void refreshTable() {
        model.setRowCount(0);
        loadData(model);
    }
}