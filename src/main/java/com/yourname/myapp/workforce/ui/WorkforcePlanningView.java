package com.yourname.myapp.workforce.ui;

import com.yourname.myapp.workforce.entity.WorkforcePlan;
import com.yourname.myapp.workforce.service.WorkforcePlanService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class WorkforcePlanningView extends JPanel {

    private final WorkforcePlanService workforcePlanService = new WorkforcePlanService();

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"ID", "Department", "Quarter", "Open Positions", "Hiring Forecast", "HR Cost", "Total Budget"}, 0
    );
        private final JTable workforceTable = new JTable(tableModel);

    private final JTextField quarterFilter = new JTextField(10);
    private final JLabel budgetUtilizationLabel = new JLabel("0%");
    private final JLabel warningLabel = new JLabel(" ");

    public WorkforcePlanningView() {
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setBackground(new Color(245, 245, 245));

        add(buildHeader(), BorderLayout.NORTH);
        add(new JScrollPane(workforceTable), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        refresh();
    }

    private JPanel buildHeader() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        JLabel title = new JLabel("Workforce Planning & Budgeting");
        title.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);
        titlePanel.add(title);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);

        JButton create = new JButton("Create Plan");
        create.addActionListener(e -> openCreatePlanDialog());

        JButton update = new JButton("Update Plan");
        update.addActionListener(e -> openUpdatePlanDialog());

        JButton clone = new JButton("Clone Plan");
        clone.addActionListener(e -> openCloneDialog());

        JButton delete = new JButton("Delete Plan");
        delete.addActionListener(e -> deleteSelectedPlan());

        JButton generateReport = new JButton("Generate Report");
        generateReport.addActionListener(e -> openGenerateReportDialog());

        JButton exportCsv = new JButton("Export CSV");
        exportCsv.addActionListener(e -> exportCsv());

        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> refresh());

        actions.add(new JLabel("Quarter:"));
        actions.add(quarterFilter);
        actions.add(create);
        actions.add(update);
        actions.add(delete);
        actions.add(clone);
        actions.add(generateReport);
        actions.add(exportCsv);
        actions.add(refresh);

        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildFooter() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);

        panel.add(new JLabel("Budget Utilization:"));
        panel.add(budgetUtilizationLabel);
        warningLabel.setForeground(new Color(231, 76, 60));
        panel.add(warningLabel);
        return panel;
    }

    public void refresh() {
        String quarter = quarterFilter.getText().trim();
        if (quarter.isBlank()) {
            quarter = null;
        }

        List<WorkforcePlan> plans = workforcePlanService.getAllPlans(quarter);
        tableModel.setRowCount(0);
        for (WorkforcePlan plan : plans) {
            tableModel.addRow(new Object[]{
                    plan.getId(),
                    plan.getDepartment(),
                    plan.getQuarter(),
                    plan.getOpenPositions(),
                    plan.getHiringForecast(),
                    plan.getHrCostProjections(),
                    plan.getTotalBudget()
            });
        }

        Map<String, Object> stats = workforcePlanService.getStats();
        budgetUtilizationLabel.setText(stats.get("budgetUtilization") + "%");
        boolean threshold = Boolean.TRUE.equals(stats.get("BUDGET_THRESHOLD_EXCEEDED"));
        warningLabel.setText(threshold ? "Warning: BUDGET_THRESHOLD_EXCEEDED" : " ");
    }

    private void openCreatePlanDialog() {
        WorkforcePlan plan = promptPlanFields(null);
        if (plan == null) {
            return;
        }
        try {
            workforcePlanService.createPlan(plan);
            refresh();
            JOptionPane.showMessageDialog(this, "Workforce plan created");
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void openUpdatePlanDialog() {
        JTextField idField = new JTextField();
        int idResult = JOptionPane.showConfirmDialog(
                this,
                new Object[]{"Plan ID", idField},
                "Update Plan",
                JOptionPane.OK_CANCEL_OPTION
        );
        if (idResult != JOptionPane.OK_OPTION) {
            return;
        }

        WorkforcePlan plan = promptPlanFields(null);
        if (plan == null) {
            return;
        }

        try {
            workforcePlanService.updatePlan(Long.parseLong(idField.getText().trim()), plan);
            refresh();
            JOptionPane.showMessageDialog(this, "Workforce plan updated");
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void openCloneDialog() {
        JTextField source = new JTextField();
        JTextField target = new JTextField();
        int result = JOptionPane.showConfirmDialog(
                this,
                new Object[]{"Source Quarter", source, "Target Quarter", target},
                "Clone Workforce Plan",
                JOptionPane.OK_CANCEL_OPTION
        );
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            workforcePlanService.clonePlanFromPreviousQuarter(source.getText().trim(), target.getText().trim());
            refresh();
            JOptionPane.showMessageDialog(this, "Plans cloned successfully");
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void deleteSelectedPlan() {
        int selectedRow = workforceTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a plan to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object idValue = tableModel.getValueAt(selectedRow, 0);
        Long planId;
        try {
            if (idValue instanceof Number) {
                planId = ((Number) idValue).longValue();
            } else {
                planId = Long.parseLong(String.valueOf(idValue));
            }
        } catch (Exception ex) {
            showError("Invalid selected plan ID");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete plan ID " + planId + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            workforcePlanService.deletePlan(planId);
            refresh();
            JOptionPane.showMessageDialog(this, "Workforce plan deleted successfully");
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void openGenerateReportDialog() {
        JTextField quarter = new JTextField();
        int result = JOptionPane.showConfirmDialog(
                this,
                new Object[]{"Quarter (optional)", quarter},
                "Generate Workforce Report",
                JOptionPane.OK_CANCEL_OPTION
        );
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String quarterValue = quarter.getText().trim();
        if (quarterValue.isBlank()) {
            quarterValue = null;
        }

        AtomicBoolean reportCompleted = new AtomicBoolean(false);
        final String selectedQuarter = quarterValue;

        Timer indicatorTimer = new Timer(3000, e -> {
            if (!reportCompleted.get()) {
                JOptionPane.showMessageDialog(this, "Report is processing...", "Processing", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        indicatorTimer.setRepeats(false);
        indicatorTimer.start();

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                return workforcePlanService.generateReport(selectedQuarter);
            }

            @Override
            protected void done() {
                reportCompleted.set(true);
                indicatorTimer.stop();
                try {
                    JTextArea textArea = new JTextArea(get());
                    textArea.setEditable(false);
                    textArea.setCaretPosition(0);
                    JOptionPane.showMessageDialog(
                            WorkforcePlanningView.this,
                            new JScrollPane(textArea),
                            "Workforce Report",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } catch (Exception ex) {
                    showError(ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void exportCsv() {
        String quarter = quarterFilter.getText().trim();
        if (quarter.isBlank()) {
            quarter = null;
        }
        try {
            String path = workforcePlanService.exportReport(quarter);
            JOptionPane.showMessageDialog(this, "CSV exported to: " + path);
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private WorkforcePlan promptPlanFields(WorkforcePlan existing) {
        JTextField department = new JTextField(existing == null ? "" : existing.getDepartment());
        JTextField quarter = new JTextField(existing == null ? "" : existing.getQuarter());
        JTextField openPositions = new JTextField(existing == null ? "0" : String.valueOf(existing.getOpenPositions()));
        JTextField hiringForecast = new JTextField(existing == null ? "0" : String.valueOf(existing.getHiringForecast()));
        JTextField hrCost = new JTextField(existing == null ? "0" : String.valueOf(existing.getHrCostProjections()));
        JTextField totalBudget = new JTextField(existing == null ? "0" : String.valueOf(existing.getTotalBudget()));

        int result = JOptionPane.showConfirmDialog(
                this,
                new Object[]{
                        "Department", department,
                        "Quarter", quarter,
                        "Open Positions", openPositions,
                        "Hiring Forecast", hiringForecast,
                        "HR Cost Projections", hrCost,
                        "Total Budget", totalBudget
                },
                "Workforce Plan",
                JOptionPane.OK_CANCEL_OPTION
        );
        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        try {
            WorkforcePlan plan = new WorkforcePlan();
            plan.setDepartment(department.getText().trim());
            plan.setQuarter(quarter.getText().trim());
            plan.setOpenPositions(Integer.parseInt(openPositions.getText().trim()));
            plan.setHiringForecast(Integer.parseInt(hiringForecast.getText().trim()));
            plan.setHrCostProjections(new BigDecimal(hrCost.getText().trim()));
            plan.setTotalBudget(new BigDecimal(totalBudget.getText().trim()));
            return plan;
        } catch (Exception ex) {
            showError("Invalid input: " + ex.getMessage());
            return null;
        }
    }

    private void showError(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE)
        );
    }
}
