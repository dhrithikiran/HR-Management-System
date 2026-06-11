package com.yourname.myapp.attendance;

import com.yourname.myapp.service.LeaveService;

import javax.swing.*;
import java.awt.*;

public class LeaveDashboardView extends JPanel {

    private final LeaveService leaveService;
    private JLabel pendingCountLabel;
    private JLabel approvedCountLabel;
    private JLabel rejectedCountLabel;

    public LeaveDashboardView(LeaveService leaveService) {
        this.leaveService = leaveService;
        initUI();
        refreshStats();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Leave Dashboard"));

        JLabel title = new JLabel("Leave Statistics", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(new Color(142, 68, 173));
        title.setBorder(BorderFactory.createEmptyBorder(16, 0, 16, 0));
        add(title, BorderLayout.NORTH);

        // Stats cards
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        cardsPanel.setBackground(Color.WHITE);
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        pendingCountLabel = new JLabel("—", SwingConstants.CENTER);
        approvedCountLabel = new JLabel("—", SwingConstants.CENTER);
        rejectedCountLabel = new JLabel("—", SwingConstants.CENTER);

        cardsPanel.add(createCard("Pending Requests", pendingCountLabel, new Color(243, 156, 18)));
        cardsPanel.add(createCard("Approved Leaves", approvedCountLabel, new Color(39, 174, 96)));
        cardsPanel.add(createCard("Rejected Leaves", rejectedCountLabel, new Color(192, 57, 43)));

        add(cardsPanel, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh Stats");
        styleButton(refreshBtn, new Color(142, 68, 173));
        refreshBtn.addActionListener(e -> refreshStats());
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(refreshBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private JPanel createCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        titleLabel.setForeground(Color.WHITE);
        card.add(titleLabel, BorderLayout.NORTH);

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 42));
        valueLabel.setForeground(Color.WHITE);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    public void refreshStats() {
        try {
            long pending = leaveService.getPendingCount();
            long approved = leaveService.getLeavesByStatus("APPROVED").size();
            long rejected = leaveService.getLeavesByStatus("REJECTED").size();

            pendingCountLabel.setText(String.valueOf(pending));
            approvedCountLabel.setText(String.valueOf(approved));
            rejectedCountLabel.setText(String.valueOf(rejected));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading stats: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void styleButton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
