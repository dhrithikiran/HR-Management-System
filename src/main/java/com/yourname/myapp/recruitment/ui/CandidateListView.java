package com.yourname.myapp.recruitment.ui;

import com.yourname.myapp.recruitment.entity.Candidate;
import com.yourname.myapp.recruitment.service.CandidateService;
import com.yourname.myapp.recruitment.service.CandidateServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * CandidateListView - UI for managing candidates with CRUD operations
 * 
 * Purpose: Provide interface for viewing, searching, and managing candidates
 * 
 * Features:
 * - Candidate table with 5 columns: ID, Name, Contact, Status, Score
 * - Real-time search by candidate name
 * - Status filter dropdown (ALL, APPLIED, SHORTLISTED, INTERVIEW, SELECTED, REJECTED)
 * - Action buttons: Add, Update, Update Status, Delete
 * - Refresh data, clear filters
 * - Auto-refresh after CRUD operations
 * 
 * Architecture:
 * - Uses internal rootPane (JPanel) for consistent view switching
 * - getRootPane() method returns the view for integration with main app
 * - Filter panel, table, and button bar organized in BorderLayout
 * - Communicates with CandidateService for all operations
 * 
 * UI Components:
 * - Filter bar: Search field, status dropdown, refresh, clear buttons
 * - Table: Non-editable DefaultTableModel showing all candidates
 * - Button bar: Add, Update, Update Status, Delete buttons
 * 
 * Exception Handling:
 * - CRUD errors caught and displayed via JOptionPane
 * - CandidateDataIncompleteException shows user-friendly messages
 * 
 * @author OOAD Project
 * @version 1.0
 * @since 2024
 */
public class CandidateListView {
    private static final Logger logger = LoggerFactory.getLogger(CandidateListView.class);
    private final CandidateService candidateService = new CandidateServiceImpl();
    
    /** Main panel containing entire view */
    private JPanel rootPane;
    
    /** Table displaying candidate data */
    private JTable table;
    
    /** Status filter dropdown */
    private JComboBox<String> statusFilter;
    
    /** Table data model (non-editable) */
    private DefaultTableModel tableModel;
    
    /** Search field for candidate name lookup */
    private JTextField searchField;

    /**
     * Constructor - Initialize candidate list view
     */
    public CandidateListView() {
        initializeUI();
    }

    /**
     * Initialize UI components
     * Creates title, filter bar, candidate table, and action buttons
     */
    private void initializeUI() {
        rootPane = new JPanel(new BorderLayout(10, 10));
        rootPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        rootPane.setBackground(new Color(245, 245, 245));

        // Title
        JLabel titleLabel = new JLabel("Candidate List");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        rootPane.add(titleLabel, BorderLayout.NORTH);
        
        // Filter bar
        JPanel filterBar = createFilterBar();
        rootPane.add(filterBar, BorderLayout.PAGE_START);

        // Candidate table
        table = createCandidateTable();
        JScrollPane scrollPane = new JScrollPane(table);
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        rootPane.add(tablePanel, BorderLayout.CENTER);

        // Button bar
        JPanel buttonBar = createButtonBar();
        rootPane.add(buttonBar, BorderLayout.SOUTH);

        // Load initial data
        loadCandidates(null);
    }

    /**
     * Create filter bar with search, status dropdown, and action buttons
     * 
     * @return Filter bar panel
     */
    private JPanel createFilterBar() {
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterBar.setBackground(Color.WHITE);
        filterBar.setBorder(BorderFactory.createLineBorder(new Color(221, 221, 221)));

        // Search field
        JLabel searchLabel = new JLabel("Search:");
        searchField = new JTextField(20);
        searchField.setToolTipText("Enter candidate name...");
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                loadCandidates(null);
            }
        });

        // Status filter
        JLabel statusLabel = new JLabel("Status:");
        statusFilter = new JComboBox<>(new String[]{"ALL", "APPLIED", "SHORTLISTED", "INTERVIEW", "SELECTED", "REJECTED"});
        statusFilter.addActionListener(this::onFilter);

        // Buttons
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadCandidates(null));

        JButton clearButton = new JButton("Clear Filters");
        clearButton.addActionListener(e -> {
            searchField.setText("");
            statusFilter.setSelectedIndex(0);
            loadCandidates(null);
        });

        filterBar.add(searchLabel);
        filterBar.add(searchField);
        filterBar.add(new JSeparator(SwingConstants.VERTICAL));
        filterBar.add(statusLabel);
        filterBar.add(statusFilter);
        filterBar.add(new JSeparator(SwingConstants.VERTICAL));
        filterBar.add(refreshButton);
        filterBar.add(clearButton);

        return filterBar;
    }

    private JTable createCandidateTable() {
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Candidate ID", "Name", "Contact", "Status", "Interview Score"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setPreferredWidth(120);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(130);
        table.setRowHeight(25);
        
        this.tableModel = model;
        return table;
    }

    private JPanel createButtonBar() {
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonBar.setBackground(Color.WHITE);
        buttonBar.setBorder(BorderFactory.createLineBorder(new Color(221, 221, 221)));

        JButton addBtn = new JButton("Add Candidate");
        addBtn.addActionListener(e -> new AddCandidateForm(this));

        JButton updateBtn = new JButton("Update Candidate");
        updateBtn.addActionListener(e -> onUpdateCandidate());

        JButton statusBtn = new JButton("Update Status");
        statusBtn.addActionListener(e -> onUpdateStatus());

        JButton deleteBtn = new JButton("Delete Candidate");
        deleteBtn.addActionListener(e -> onDeleteCandidate());

        buttonBar.add(addBtn);
        buttonBar.add(updateBtn);
        buttonBar.add(statusBtn);
        buttonBar.add(deleteBtn);

        return buttonBar;
    }

    private void onFilter(ActionEvent e) {
        String status = (String) statusFilter.getSelectedItem();
        loadCandidates("ALL".equals(status) ? null : status);
    }

    public void loadCandidates(String status) {
        tableModel.setRowCount(0);
        try {
            List<Candidate> candidates = (status == null) ? candidateService.getAllCandidates(null) : candidateService.getAllCandidates(status);
            String searchTerm = searchField.getText().toLowerCase();
            
            for (Candidate c : candidates) {
                if (searchTerm.isEmpty() || c.getCandidateName().toLowerCase().contains(searchTerm)) {
                    tableModel.addRow(new Object[]{
                        c.getCandidateId(),
                        c.getCandidateName(),
                        c.getContactInfo(),
                        c.getApplicationStatus(),
                        String.format("%.1f", c.getInterviewScore())
                    });
                }
            }
            logger.info("Loaded {} candidates", tableModel.getRowCount());
        } catch (Exception e) {
            logger.error("Error loading candidates", e);
            JOptionPane.showMessageDialog(rootPane, "Error loading candidates: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onUpdateCandidate() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(rootPane, "Select a candidate to update.");
            return;
        }
        String id = (String) tableModel.getValueAt(row, 0);
        new UpdateCandidateForm(this, id);
    }

    private void onUpdateStatus() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(rootPane, "Select a candidate to update status.");
            return;
        }
        String id = (String) tableModel.getValueAt(row, 0);
        new StatusUpdateForm(this, id);
    }

    private void onDeleteCandidate() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(rootPane, "Select a candidate to delete.");
            return;
        }
        String id = (String) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(rootPane, "Delete this candidate?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                candidateService.deleteCandidate(id);
                loadCandidates(null);
                JOptionPane.showMessageDialog(rootPane, "Candidate deleted successfully.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(rootPane, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public JPanel getRootPane() {
        return rootPane;
    }

    public void refresh() {
        String status = (String) statusFilter.getSelectedItem();
        loadCandidates("ALL".equals(status) ? null : status);
    }
}
