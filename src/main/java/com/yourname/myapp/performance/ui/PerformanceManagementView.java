package com.yourname.myapp.performance.ui;

import com.yourname.myapp.performance.entity.Appraisal;
import com.yourname.myapp.performance.entity.Promotion;
import com.yourname.myapp.performance.service.AppraisalService;
import com.yourname.myapp.performance.service.AppraisalServiceContract;
import com.yourname.myapp.performance.service.AppraisalServiceProxy;
import com.yourname.myapp.performance.service.PerformanceStatsService;
import com.yourname.myapp.performance.service.PromotionService;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class PerformanceManagementView extends JPanel {

    private final AppraisalServiceContract appraisalService = new AppraisalServiceProxy(new AppraisalService());
    private final PromotionService promotionService = new PromotionService();
    private final PerformanceStatsService statsService = new PerformanceStatsService();

    private final DefaultTableModel appraisalModel = new DefaultTableModel(
            new String[]{"Appraise ID", "Employee ID", "Rating", "Status", "Deadline", "Locked"}, 0
    );
    private final DefaultTableModel promotionModel = new DefaultTableModel(
            new String[]{"Promotion ID", "Employee ID", "New Role", "Effective Date"}, 0
    );

    private final JLabel completedLabel = new JLabel("0", SwingConstants.CENTER);
    private final JLabel pendingLabel = new JLabel("0", SwingConstants.CENTER);
    private final JLabel promotionLabel = new JLabel("0", SwingConstants.CENTER);

    public PerformanceManagementView() {
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setBackground(new Color(245, 245, 245));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);

        refresh();
    }

    private JPanel buildHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel title = new JLabel("Performance Management");
        title.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);

        JButton addAppraisal = new JButton("Create Appraisal");
        addAppraisal.addActionListener(e -> openCreateAppraisalDialog());

        JButton updateAppraisal = new JButton("Update Appraisal");
        updateAppraisal.addActionListener(e -> openUpdateAppraisalDialog());

        JButton recommendPromotion = new JButton("Recommend Promotion");
        recommendPromotion.addActionListener(e -> openRecommendPromotionDialog());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refresh());

        actions.add(addAppraisal);
        actions.add(updateAppraisal);
        actions.add(recommendPromotion);
        actions.add(refreshButton);

        panel.add(title, BorderLayout.WEST);
        panel.add(actions, BorderLayout.EAST);
        return panel;
    }

    private JPanel buildCenter() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setOpaque(false);

        panel.add(buildStatsPanel(), BorderLayout.NORTH);

        JTable appraisalTable = new JTable(appraisalModel);
        JTable promotionTable = new JTable(promotionModel);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(appraisalTable),
                new JScrollPane(promotionTable)
        );
        splitPane.setResizeWeight(0.6);

        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildStatsPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        panel.add(createStatCard("Completed Reviews", completedLabel, new Color(46, 204, 113)));
        panel.add(createStatCard("Pending Reviews", pendingLabel, new Color(241, 196, 15)));
        panel.add(createStatCard("Promotions Recommended", promotionLabel, new Color(52, 152, 219)));
        return panel;
    }

    private JPanel createStatCard(String label, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout(4, 4));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);

        JLabel title = new JLabel(label, SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 12));
        valueLabel.setFont(new Font("Arial", Font.BOLD, 20));
        valueLabel.setForeground(color);

        card.add(title, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    public void refresh() {
        loadStats();
        loadAppraisals();
        loadPromotions();
    }

    private void loadStats() {
        try {
            Map<String, Object> stats = statsService.getPerformanceStats();
            completedLabel.setText(String.valueOf(stats.get("completedReviews")));
            pendingLabel.setText(String.valueOf(stats.get("pendingReviews")));
            promotionLabel.setText(String.valueOf(stats.get("promotionsRecommended")));
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void loadAppraisals() {
        appraisalModel.setRowCount(0);
        List<Appraisal> appraisals = appraisalService.getAllAppraisals("ADMIN");
        for (Appraisal appraisal : appraisals) {
            appraisalModel.addRow(new Object[]{
                    appraisal.getAppraiseId(),
                    appraisal.getEmployeeId(),
                    appraisal.getRating(),
                    appraisal.getAppraisalStatus(),
                    appraisal.getDeadlineDate(),
                    appraisal.isLocked()
            });
        }
    }

    private void loadPromotions() {
        promotionModel.setRowCount(0);
        List<Promotion> promotions = promotionService.getAllPromotions();
        for (Promotion promotion : promotions) {
            promotionModel.addRow(new Object[]{
                    promotion.getPromotionId(),
                    promotion.getEmployeeId(),
                    promotion.getNewRole(),
                    promotion.getEffectiveDate()
            });
        }
    }

    private void openCreateAppraisalDialog() {
        JTextField employeeId = new JTextField();
        JTextField rating = new JTextField();
        JTextField feedback = new JTextField();
        JComboBox<Appraisal.AppraisalStatus> status = new JComboBox<>(Appraisal.AppraisalStatus.values());
        status.setSelectedItem(Appraisal.AppraisalStatus.PENDING);
        JTextField deadline = new JTextField(LocalDate.now().plusDays(30).toString());

        Object[] fields = {
                "Employee ID", employeeId,
                "Rating (0-5)", rating,
                "Feedback", feedback,
                "Status (PENDING/COMPLETED)", status,
                "Deadline (YYYY-MM-DD)", deadline
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Create Appraisal", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            Appraisal appraisal = new Appraisal();
            appraisal.setEmployeeId(employeeId.getText().trim());
            appraisal.setRating(Double.parseDouble(rating.getText().trim()));
            appraisal.setFeedback(feedback.getText().trim());
            appraisal.setAppraisalStatus((Appraisal.AppraisalStatus) status.getSelectedItem());
            appraisal.setDeadlineDate(LocalDate.parse(deadline.getText().trim()));
            appraisalService.createAppraisal("ADMIN", appraisal);
            refresh();
            JOptionPane.showMessageDialog(this, "Appraisal created successfully");
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void openUpdateAppraisalDialog() {
        JTextField appraisalId = new JTextField();
        JTextField rating = new JTextField();
        JTextField feedback = new JTextField();
        JComboBox<Appraisal.AppraisalStatus> status = new JComboBox<>(Appraisal.AppraisalStatus.values());
        status.setSelectedItem(Appraisal.AppraisalStatus.COMPLETED);
        JTextField deadline = new JTextField(LocalDate.now().plusDays(30).toString());

        Object[] fields = {
                "Appraisal ID", appraisalId,
                "Rating (0-5)", rating,
                "Feedback", feedback,
                "Status (PENDING/COMPLETED)", status,
                "Deadline (YYYY-MM-DD)", deadline
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Update Appraisal", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            Appraisal request = new Appraisal();
            request.setRating(Double.parseDouble(rating.getText().trim()));
            request.setFeedback(feedback.getText().trim());
            request.setAppraisalStatus((Appraisal.AppraisalStatus) status.getSelectedItem());
            request.setDeadlineDate(LocalDate.parse(deadline.getText().trim()));
            appraisalService.updateAppraisal("ADMIN", appraisalId.getText().trim(), request);
            refresh();
            JOptionPane.showMessageDialog(this, "Appraisal updated successfully");
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void openRecommendPromotionDialog() {
        JTextField employeeId = new JTextField();
        JTextField newRole = new JTextField();
        JTextField effectiveDate = new JTextField(LocalDate.now().plusDays(7).toString());

        Object[] fields = {
                "Employee ID", employeeId,
                "New Role", newRole,
                "Effective Date (YYYY-MM-DD)", effectiveDate
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Recommend Promotion", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            Promotion promotion = new Promotion();
            promotion.setEmployeeId(employeeId.getText().trim());
            promotion.setNewRole(newRole.getText().trim());
            promotion.setEffectiveDate(LocalDate.parse(effectiveDate.getText().trim()));
            promotionService.recommendPromotion(promotion);
            refresh();
            JOptionPane.showMessageDialog(this, "Promotion recommended successfully");
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
