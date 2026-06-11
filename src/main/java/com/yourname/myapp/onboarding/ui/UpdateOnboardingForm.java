package com.yourname.myapp.onboarding.ui;

import com.yourname.myapp.onboarding.service.OnboardingService;
import com.yourname.myapp.onboarding.service.OnboardingServiceImpl;

import javax.swing.*;
import java.awt.*;

public class UpdateOnboardingForm extends JFrame {

    private final OnboardingService service = new OnboardingServiceImpl();
    private final OnboardingDashboardView parent;
    private final String recordId;

    public UpdateOnboardingForm(OnboardingDashboardView parent, String recordId) {

        this.parent = parent;
        this.recordId = recordId;

        setTitle("Update Onboarding Record");
        setSize(350, 200);
        setLayout(new GridLayout(3, 2));

        JComboBox<String> bg = new JComboBox<>(new String[]{
                "PENDING", "CLEARED", "FAILED"
        });

        JComboBox<String> doc = new JComboBox<>(new String[]{
                "PENDING", "VERIFIED", "REJECTED"
        });

        add(new JLabel("Background Check"));
        add(bg);

        add(new JLabel("Document Verification"));
        add(doc);

        JButton save = new JButton("Save Changes");

        save.addActionListener(e -> {

            service.updateBackgroundCheck(recordId, (String) bg.getSelectedItem());
            service.updateDocumentVerification(recordId, (String) doc.getSelectedItem());

            parent.refresh();
            dispose();
        });

        add(save);

        setVisible(true);
    }
}