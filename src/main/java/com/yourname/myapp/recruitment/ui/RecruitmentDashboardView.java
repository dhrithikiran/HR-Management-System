package com.yourname.myapp.recruitment.ui;

import com.yourname.myapp.recruitment.service.CandidateService;
import com.yourname.myapp.recruitment.service.CandidateServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Map;

/**
 * RecruitmentDashboardView - UI dashboard displaying recruitment metrics and statistics
 * 
 * Purpose: Provide at-a-glance view of current recruitment status with key metrics
 * 
 * Displayed Metrics (6 stat cards in 2x3 grid):
 * 1. Total Applications - All candidates received
 * 2. Shortlisted - Candidates in SHORTLISTED status
 * 3. Selected - Candidates in SELECTED status
 * 4. Shortlist Rate (%) - Percentage of applicants shortlisted
 * 5. Selection Rate (%) - Percentage of shortlisted converted to selected
 * 6. Open Positions - Available job positions
 * 
 * Architecture:
 * - Uses internal rootPane (JPanel) for consistent view switching
 * - getRootPane() method returns the panel for integration with main app
 * - Stat cards have colored borders (blue, purple, green) for visual distinction
 * - Auto-refreshes from CandidateService.getRecruitmentStats()
 * 
 * UI Components:
 * - Title label at top
 * - GridLayout(2,3) for metric cards
 * - Each card shows metric name, value, and colored border
 * 
 * @author OOAD Project
 * @version 1.0
 * @since 2024
 */
public class RecruitmentDashboardView {
    private static final Logger logger = LoggerFactory.getLogger(RecruitmentDashboardView.class);
    private final CandidateService candidateService = new CandidateServiceImpl();
    
    /** Main panel containing entire dashboard UI */
    private JPanel rootPane;

    /**
     * Constructor - Initialize recruitment dashboard view
     */
    public RecruitmentDashboardView() {
        initializeUI();
    }

    /**
     * Initialize dashboard UI components
     * Creates title and statistics panel with metric cards
     */
    private void initializeUI() {
        rootPane = new JPanel(new BorderLayout(20, 20));
        rootPane.setBackground(new Color(245, 245, 245));
        rootPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("Recruitment Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        rootPane.add(titleLabel, BorderLayout.NORTH);
        
        // Statistics panel
        JPanel statsPanel = createStatisticsPanel();
        rootPane.add(statsPanel, BorderLayout.CENTER);
    }
    
    private JPanel createStatisticsPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(245, 245, 245));
        
        JPanel panel = new JPanel(new GridLayout(2, 3, 15, 15));
        panel.setBackground(new Color(245, 245, 245));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
        panel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 220));
        
        try {
            Map<String, Object> stats = candidateService.getRecruitmentStats();
            
            long total = Long.parseLong(stats.get("applicationsReceived").toString());
            long shortlisted = Long.parseLong(stats.get("shortlistedCount").toString());
            long selected = Long.parseLong(stats.get("selectedCount").toString());
            
            // Total Applications Card
            JPanel totalCard = createStatCard(
                "Total Applications", 
                String.valueOf(total), 
                new Color(52, 152, 219)
            );
            panel.add(totalCard);
            
            // Shortlisted Card
            JPanel shortlistedCard = createStatCard(
                "Shortlisted", 
                String.valueOf(shortlisted), 
                new Color(155, 89, 182)
            );
            panel.add(shortlistedCard);
            
            // Selected Card
            JPanel selectedCard = createStatCard(
                "Selected", 
                String.valueOf(selected), 
                new Color(46, 204, 113)
            );
            panel.add(selectedCard);
            
            // Shortlist Rate
            double shortlistRate = total > 0 ? (shortlisted * 100.0 / total) : 0;
            JPanel shortlistRateCard = createStatCard(
                "Shortlist Rate", 
                String.format("%.1f%%", shortlistRate), 
                new Color(241, 196, 15)
            );
            panel.add(shortlistRateCard);
            
            // Selection Rate
            double selectionRate = total > 0 ? (selected * 100.0 / total) : 0;
            JPanel selectionRateCard = createStatCard(
                "Selection Rate", 
                String.format("%.1f%%", selectionRate), 
                new Color(231, 76, 60)
            );
            panel.add(selectionRateCard);
            
            // Open Positions
            JPanel openPosCard = createStatCard(
                "Open Positions", 
                String.valueOf(stats.get("openPositions")), 
                new Color(39, 174, 96)
            );
            panel.add(openPosCard);
            
            logger.info("Recruitment dashboard loaded successfully");
        } catch (Exception e) {
            logger.error("Error loading recruitment statistics", e);
            JLabel errorLabel = new JLabel("Error loading statistics: " + e.getMessage());
            errorLabel.setForeground(Color.RED);
            wrapper.add(errorLabel, BorderLayout.CENTER);
        }
        
        wrapper.add(panel, BorderLayout.NORTH);
        return wrapper;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(color, 2, true));
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(color, 2, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setForeground(new Color(100, 100, 100));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 28));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }

    public JPanel getRootPane() {
        return rootPane;
    }

    public void refresh() {
        rootPane.removeAll();
        rootPane.add(new JLabel("Recruitment Dashboard"), BorderLayout.NORTH);
        rootPane.add(createStatisticsPanel(), BorderLayout.CENTER);
        rootPane.revalidate();
        rootPane.repaint();
    }
}
