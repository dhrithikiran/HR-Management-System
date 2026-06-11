package com.yourname.myapp.ui.benefits;

import com.yourname.myapp.service.BenefitService;
import com.yourname.myapp.service.ClaimService;

import javax.swing.*;
import java.awt.*;

/**
 * Main entry point for the Benefits Administration module.
 *
 * Add to EmployeeManagementApp.java:
 *   import com.yourname.myapp.ui.benefits.BenefitsMainView;
 *   // in sidebar:
 *   tabbedPane or switchToView(benefitsMainView, benefitsMainView);
 */
public class BenefitsMainView extends JPanel {

    public BenefitsMainView() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Header
        JLabel header = new JLabel("  Benefits Administration", SwingConstants.LEFT);
        header.setFont(new Font("SansSerif", Font.BOLD, 18));
        header.setForeground(Color.WHITE);
        header.setOpaque(true);
        header.setBackground(new Color(39, 174, 96));
        header.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        add(header, BorderLayout.NORTH);

        BenefitService benefitService = new BenefitService();
        ClaimService claimService = new ClaimService();

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.PLAIN, 13));

        tabs.addTab("📋 Enrollments", new BenefitEnrollmentListView(benefitService));
        tabs.addTab("➕ Create/Update Enrollment", new EnrollmentForm(benefitService));
        tabs.addTab("🏥 Claims", new ClaimsListView(claimService));

        add(tabs, BorderLayout.CENTER);
    }
}
