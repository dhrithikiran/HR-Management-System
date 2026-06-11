package com.yourname.myapp.recruitment.ui;

import com.yourname.myapp.recruitment.entity.Candidate;
import com.yourname.myapp.recruitment.service.CandidateService;
import com.yourname.myapp.recruitment.service.CandidateServiceImpl;

import javax.swing.*;
import java.awt.*;

public class UpdateCandidateForm extends JFrame {
    private final CandidateService candidateService = new CandidateServiceImpl();
    private final CandidateListView parent;
    private final String candidateId;
    private JTextField nameField, contactField, resumeField, scoreField;

    public UpdateCandidateForm(CandidateListView parent, String candidateId) {
        this.parent = parent;
        this.candidateId = candidateId;
        setTitle("Update Candidate");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
        setVisible(true);
    }

    private void initUI() {
        Candidate candidate = candidateService.getCandidateById(candidateId);
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.add(new JLabel("Name:"));
        nameField = new JTextField(candidate.getCandidateName());
        panel.add(nameField);
        panel.add(new JLabel("Contact Info:"));
        contactField = new JTextField(candidate.getContactInfo());
        panel.add(contactField);
        panel.add(new JLabel("Resume Data:"));
        resumeField = new JTextField(candidate.getResumeData());
        panel.add(resumeField);
        panel.add(new JLabel("Interview Score:"));
        scoreField = new JTextField(String.valueOf(candidate.getInterviewScore()));
        panel.add(scoreField);
        JButton saveBtn = new JButton("Update");
        saveBtn.addActionListener(e -> onUpdate());
        panel.add(saveBtn);
        add(panel);
    }

    private void onUpdate() {
        try {
            Candidate updated = new Candidate();
            updated.setCandidateName(nameField.getText());
            updated.setContactInfo(contactField.getText());
            updated.setResumeData(resumeField.getText());
            updated.setInterviewScore(Double.parseDouble(scoreField.getText()));
            candidateService.updateCandidate(candidateId, updated);
            JOptionPane.showMessageDialog(this, "Candidate updated successfully.");
            parent.loadCandidates(null);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
