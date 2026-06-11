package com.yourname.myapp.recruitment.ui;

import com.yourname.myapp.recruitment.entity.Candidate;
import com.yourname.myapp.recruitment.service.CandidateService;
import com.yourname.myapp.recruitment.service.CandidateServiceImpl;
import com.yourname.myapp.onboarding.ui.OnboardingVerificationForm;
import com.yourname.myapp.onboarding.service.OnboardingService;
import com.yourname.myapp.onboarding.service.OnboardingServiceImpl;

import javax.swing.*;
import java.awt.*;

public class StatusUpdateForm extends JFrame {
    private final CandidateService candidateService = new CandidateServiceImpl();
    private final OnboardingService onboardingService = new OnboardingServiceImpl();
    private final CandidateListView parent;
    private final String candidateId;
    private JComboBox<String> statusCombo;

    public StatusUpdateForm(CandidateListView parent, String candidateId) {
        this.parent = parent;
        this.candidateId = candidateId;
        setTitle("Update Candidate Status");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
        setVisible(true);
    }

    private void initUI() {
        Candidate candidate = candidateService.getCandidateById(candidateId);
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.add(new JLabel("Current Status:"));
        panel.add(new JLabel(candidate.getApplicationStatus().toString()));
        panel.add(new JLabel("New Status:"));
        statusCombo = new JComboBox<>(new String[]{"APPLIED", "SHORTLISTED", "INTERVIEW", "SELECTED", "REJECTED"});
        // Set the selected item to match current status
        statusCombo.setSelectedItem(candidate.getApplicationStatus().toString());
        panel.add(statusCombo);
        JButton updateBtn = new JButton("Update Status");
        updateBtn.addActionListener(e -> onUpdateStatus());
        panel.add(updateBtn);
        add(panel);
    }

    private void onUpdateStatus() {
        try {
            String newStatus = (String) statusCombo.getSelectedItem();
            candidateService.updateStatus(candidateId, newStatus);
            JOptionPane.showMessageDialog(this, "Status updated successfully.");
            
            // If transitioning to SELECTED, open onboarding verification form
            if ("SELECTED".equals(newStatus)) {
                // Get the onboarding record that was just created by updateStatus()
                // Use a small delay to ensure the record is committed to database
                Thread.sleep(100);
                
                var records = onboardingService.getAllRecords();
                var onboardingRecord = records.stream()
                    .filter(r -> candidateId.equals(r.getAssignedEmployeeId()))
                    .findFirst();
                
                if (onboardingRecord.isPresent()) {
                    new OnboardingVerificationForm(onboardingRecord.get().getOnboardingId());
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Onboarding record created successfully. You can now manage it from the Onboarding dashboard.",
                        "Info", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            
            parent.loadCandidates(null);
            dispose();
        } catch (Exception ex) {
            ex.printStackTrace();  // Print stack trace for debugging
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage() + "\n\nDetails: " + ex.getCause(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
