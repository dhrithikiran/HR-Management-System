package com.yourname.myapp.recruitment.ui;

import com.yourname.myapp.recruitment.entity.Candidate;
import com.yourname.myapp.recruitment.service.CandidateService;
import com.yourname.myapp.recruitment.service.CandidateServiceImpl;

import javax.swing.*;
import java.awt.*;

public class AddCandidateForm extends JFrame {
    private final CandidateService candidateService = new CandidateServiceImpl();
    private final CandidateListView parent;
    private JTextField nameField, contactField, resumeField;

    public AddCandidateForm(CandidateListView parent) {
        this.parent = parent;
        setTitle("Add Candidate");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
        setVisible(true);
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.add(new JLabel("Name:"));
        nameField = new JTextField();
        panel.add(nameField);
        panel.add(new JLabel("Contact Info:"));
        contactField = new JTextField();
        panel.add(contactField);
        panel.add(new JLabel("Resume Data:"));
        resumeField = new JTextField();
        panel.add(resumeField);
        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(e -> onSave());
        panel.add(saveBtn);
        add(panel);
    }

    private void onSave() {
        try {
            Candidate candidate = new Candidate();
            candidate.setCandidateName(nameField.getText());
            candidate.setContactInfo(contactField.getText());
            candidate.setResumeData(resumeField.getText());
            candidateService.createCandidate(candidate);
            JOptionPane.showMessageDialog(this, "Candidate added successfully.");
            parent.loadCandidates(null);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
