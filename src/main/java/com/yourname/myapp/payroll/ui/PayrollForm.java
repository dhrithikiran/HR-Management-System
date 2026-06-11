package com.yourname.myapp.payroll.ui;

import com.yourname.myapp.payroll.entity.Payroll;
import com.yourname.myapp.payroll.service.PayrollService;
import com.yourname.myapp.payroll.service.PayrollServiceImpl;

import javax.swing.*;
import java.awt.*;

public class PayrollForm extends JFrame {

    private JTextField employeeIdField;
    private JComboBox<String> roleBox;
    private JComboBox<String> statusBox;
    private JTextArea resultArea;

    private PayrollService service = new PayrollServiceImpl();

    public PayrollForm() {

        setTitle("Payroll Form");
        setSize(400, 400);
        setLayout(new FlowLayout());

        employeeIdField = new JTextField(10);

        String[] roles = {"Manager","Developer","Analyst","Consultant","Coordinator","Executive","Other"};
        roleBox = new JComboBox<>(roles);

        String[] status = {"Pending","Success"};
        statusBox = new JComboBox<>(status);

        JButton generateBtn = new JButton("Generate & Save");

        resultArea = new JTextArea(10, 30);

        add(new JLabel("Employee ID"));
        add(employeeIdField);
        add(roleBox);
        add(statusBox);
        add(generateBtn);
        add(resultArea);

        generateBtn.addActionListener(e -> generatePayroll());

        setVisible(true);
    }

    private void generatePayroll() {

        String empId = employeeIdField.getText();

        Payroll payroll = service.generatePayroll(empId);

        payroll.setSalaryTransferRecord((String) statusBox.getSelectedItem());

        service.savePayroll(payroll);

        resultArea.setText(
            "Employee: " + payroll.getEmployee().getEmployeeName() +
            "\nRole: " + payroll.getRole() +
            "\nGross: " + payroll.getGrossSalary() +
            "\nDeductions: " + payroll.getDeductions() +
            "\nNet: " + payroll.getNetPay()
        );
    }
}