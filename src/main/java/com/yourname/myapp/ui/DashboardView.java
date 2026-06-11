package com.yourname.myapp.ui;

import com.yourname.myapp.dto.DashboardStats;
import com.yourname.myapp.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

/**
 * Dashboard view showing employee statistics and metrics.
 */
public class DashboardView {
    private static final Logger logger = LoggerFactory.getLogger(DashboardView.class);
    private final EmployeeService employeeService;
    private JPanel rootPane;

    public DashboardView(EmployeeService employeeService) {
        this.employeeService = employeeService;
        initializeUI();
    }

    /**
     * Initialize the dashboard UI
     */
    private void initializeUI() {
        rootPane = new JPanel(new BorderLayout(20, 20));
        rootPane.setBackground(new Color(245, 245, 245));
        rootPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        rootPane.add(titleLabel, BorderLayout.NORTH);

        // Statistics cards panel
        JPanel statsPanel = createStatisticsPanel();
        rootPane.add(statsPanel, BorderLayout.CENTER);
    }

    /**
     * Create the statistics panel with employee metrics
     */
    private JPanel createStatisticsPanel() {
        // Outer wrapper to prevent the cards from stretching too large
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(245, 245, 245));
        
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 15));
        panel.setBackground(new Color(245, 245, 245));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        panel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 120));

        try {
            DashboardStats stats = employeeService.getDashboardStats();

            // Total Employees Card
            JPanel totalCard = createStatCard("Total Employees", String.valueOf(stats.getTotalEmployeeCount()), new Color(52, 152, 219));
            panel.add(totalCard);

            // Active Employees Card
            JPanel activeCard = createStatCard("Active Employees", String.valueOf(stats.getActiveEmployeeCount()), new Color(46, 204, 113));
            panel.add(activeCard);

            // On Leave Card
            JPanel onLeaveCard = createStatCard("On Leave", String.valueOf(stats.getOnLeaveCount()), new Color(243, 156, 18));
            panel.add(onLeaveCard);

            // New Joiners Card
            JPanel newJoinersCard = createStatCard("New Joiners (This Month)", String.valueOf(stats.getNewJoinersCount()), new Color(155, 89, 182));
            panel.add(newJoinersCard);

            logger.info("Dashboard statistics loaded successfully");
        } catch (Exception e) {
            logger.error("Error loading dashboard statistics", e);
            JLabel errorLabel = new JLabel("Error loading statistics: " + e.getMessage());
            errorLabel.setForeground(Color.RED);
            wrapper.add(errorLabel, BorderLayout.CENTER);
        }

        wrapper.add(panel, BorderLayout.NORTH);
        return wrapper;
    }

    /**
     * Create a statistics card with count and title
     */
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(color, 2, true));
        card.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                new LineBorder(color, 2, true),
                javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(color);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        titleLabel.setForeground(new Color(51, 51, 51));

        card.add(valueLabel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);

        return card;
    }

    /**
     * Get the root pane of this view
     */
    public JPanel getRootPane() {
        return rootPane;
    }

    /**
     * Refresh the dashboard data
     */
    public void refresh() {
        rootPane.remove(rootPane.getComponentCount() - 1);
        JPanel statsPanel = createStatisticsPanel();
        rootPane.add(statsPanel, BorderLayout.CENTER);
        rootPane.revalidate();
        rootPane.repaint();
    }
}
