package com.yourname.myapp.onboarding.ui;

import com.yourname.myapp.onboarding.entity.OnboardingRecord;
import com.yourname.myapp.onboarding.service.OnboardingService;
import com.yourname.myapp.onboarding.service.OnboardingServiceImpl;
import com.yourname.myapp.recruitment.entity.Candidate;
import com.yourname.myapp.recruitment.repository.CandidateRepository;
import com.yourname.myapp.recruitment.repository.CandidateRepositoryImpl;

import javax.swing.*;
import java.awt.*;

/**
 * OnboardingVerificationForm - Allows HR to verify onboarding details before finalizing
 * 
 * Workflow:
 * 1. User selects a candidate for hire (status = SELECTED)
 * 2. Onboarding record is automatically created
 * 3. HR opens this form to verify background check and documents
 * 4. Once both are verified, HR clicks "Finalize Onboarding"
 * 5. Employee record is created and candidate joins the team
 */
public class OnboardingVerificationForm extends JFrame {
    private final OnboardingService onboardingService = new OnboardingServiceImpl();
    private final CandidateRepository candidateRepository = new CandidateRepositoryImpl();
    private final String onboardingId;
    private JComboBox<String> bgCheckCombo;
    private JComboBox<String> docVerificationCombo;
    private JLabel statusLabel;

    public OnboardingVerificationForm(String onboardingId) {
        this.onboardingId = onboardingId;
        setTitle("Onboarding Verification");
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
        setVisible(true);
    }

    private void initUI() {
        try {
            OnboardingRecord record = onboardingService.getById(onboardingId);
            Candidate candidate = candidateRepository.findById(record.getAssignedEmployeeId());

            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            // Header panel with candidate info
            JPanel headerPanel = new JPanel(new GridLayout(3, 2, 10, 10));
            headerPanel.setBorder(BorderFactory.createTitledBorder("Candidate Information"));
            headerPanel.add(new JLabel("Onboarding ID:"));
            headerPanel.add(new JLabel(record.getOnboardingId()));
            headerPanel.add(new JLabel("Candidate Name:"));
            headerPanel.add(new JLabel(record.getEmployeeName()));
            headerPanel.add(new JLabel("Pipeline Status:"));
            headerPanel.add(new JLabel(record.getPipelineStatus().toString()));
            mainPanel.add(headerPanel, BorderLayout.NORTH);

            // Verification panel
            JPanel verificationPanel = new JPanel(new GridLayout(3, 2, 10, 10));
            verificationPanel.setBorder(BorderFactory.createTitledBorder("Verification Status"));

            // Background Check
            verificationPanel.add(new JLabel("Background Check:"));
            bgCheckCombo = new JComboBox<>(new String[]{"PENDING", "CLEARED", "FAILED"});
            bgCheckCombo.setSelectedItem(record.getBackgroundCheckStatus().toString());
            verificationPanel.add(bgCheckCombo);

            // Document Verification
            verificationPanel.add(new JLabel("Document Verification:"));
            docVerificationCombo = new JComboBox<>(new String[]{"PENDING", "VERIFIED", "REJECTED"});
            docVerificationCombo.setSelectedItem(record.getDocumentVerificationStatus().toString());
            verificationPanel.add(docVerificationCombo);

            // Status message
            verificationPanel.add(new JLabel("Overall Status:"));
            statusLabel = new JLabel(getOverallStatus(record));
            statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
            verificationPanel.add(statusLabel);

            mainPanel.add(verificationPanel, BorderLayout.CENTER);

            // Buttons panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
            
            JButton saveBtn = new JButton("Save Changes");
            saveBtn.addActionListener(e -> onSaveChanges(record));
            
            JButton finalizeBtn = new JButton("Finalize Onboarding");
            finalizeBtn.addActionListener(e -> onFinalizeOnboarding(record));
            
            JButton cancelBtn = new JButton("Cancel");
            cancelBtn.addActionListener(e -> dispose());
            
            buttonPanel.add(saveBtn);
            buttonPanel.add(finalizeBtn);
            buttonPanel.add(cancelBtn);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            add(mainPanel);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading onboarding record: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    private void onSaveChanges(OnboardingRecord record) {
        try {
            String bgStatus = (String) bgCheckCombo.getSelectedItem();
            String docStatus = (String) docVerificationCombo.getSelectedItem();

            onboardingService.updateBackgroundCheck(onboardingId, bgStatus);
            onboardingService.updateDocumentVerification(onboardingId, docStatus);

            statusLabel.setText(getOverallStatusAfterUpdate(bgStatus, docStatus));
            JOptionPane.showMessageDialog(this, "Verification details saved successfully.", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving changes: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onFinalizeOnboarding(OnboardingRecord record) {
        try {
            String bgStatus = (String) bgCheckCombo.getSelectedItem();
            String docStatus = (String) docVerificationCombo.getSelectedItem();

            // Check if both verifications are complete
            if (!bgStatus.equals("CLEARED")) {
                JOptionPane.showMessageDialog(this, 
                        "Background check must be CLEARED before finalizing.", 
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!docStatus.equals("VERIFIED")) {
                JOptionPane.showMessageDialog(this, 
                        "Documents must be VERIFIED before finalizing.", 
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Save changes first
            onboardingService.updateBackgroundCheck(onboardingId, bgStatus);
            onboardingService.updateDocumentVerification(onboardingId, docStatus);

            // Approve onboarding (this creates the employee)
            onboardingService.approveOnboarding(onboardingId);

            JOptionPane.showMessageDialog(this, 
                    "Onboarding finalized! Employee has been added to the system.", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error finalizing onboarding: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getOverallStatus(OnboardingRecord record) {
        if (record.isVerifiedRecord()) {
            return "✓ COMPLETED - Employee Added";
        }
        return getOverallStatusAfterUpdate(
                record.getBackgroundCheckStatus().toString(),
                record.getDocumentVerificationStatus().toString()
        );
    }

    private String getOverallStatusAfterUpdate(String bgStatus, String docStatus) {
        if ("CLEARED".equals(bgStatus) && "VERIFIED".equals(docStatus)) {
            return "✓ READY TO FINALIZE";
        } else if ("FAILED".equals(bgStatus) || "REJECTED".equals(docStatus)) {
            return "✗ VERIFICATION FAILED";
        } else {
            return "⏳ PENDING VERIFICATION";
        }
    }
}
