package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.convert_currency.ConvertViewModel; // REQUIRED for currency list
import interface_adapter.load_currencies.LoadCurrenciesController;
import interface_adapter.travel_budget.TravelBudgetController;
import interface_adapter.travel_budget.TravelBudgetState;
import interface_adapter.travel_budget.TravelBudgetViewModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TravelBudgetView extends JPanel implements ActionListener, PropertyChangeListener {

    public final String viewName = "travel_budget";

    // Architecture
    private final ViewManagerModel viewManagerModel;
    private final TravelBudgetViewModel travelBudgetViewModel;
    private final ConvertViewModel convertViewModel; // Source of currency list
    private TravelBudgetController controller;
    private LoadCurrenciesController loadCurrenciesController;

    // UI constants
    private static final Color BG = new Color(245, 247, 250);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final int MAX_ITEMS = 5;

    // --- UI widgets ---
    private final JComboBox<String> homeCurrencyBox;
    private final JTextField[] amountFields = new JTextField[MAX_ITEMS];
    private final JComboBox<String>[] currencyBoxes = new JComboBox[MAX_ITEMS];

    private final JLabel totalLabel;
    private final JTextArea breakdownArea;
    private final JButton calculateButton;
    private final JButton backButton;

    // Constructor Update: Removed List<String>, Added ConvertViewModel
    public TravelBudgetView(ViewManagerModel viewManagerModel,
                            TravelBudgetViewModel travelBudgetViewModel,
                            ConvertViewModel convertViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.travelBudgetViewModel = travelBudgetViewModel;
        this.convertViewModel = convertViewModel;

        // Listen to both ViewModels
        this.travelBudgetViewModel.addPropertyChangeListener(this);
        this.convertViewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());
        setBackground(BG);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BG);
        content.setBorder(new EmptyBorder(20, 40, 20, 40));

        // Title
        JLabel title = new JLabel("Travel Budget Calculator", SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(20));

        // Home currency row
        JPanel homePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        homePanel.setBackground(BG);
        JLabel homeLabel = new JLabel("Convert to:");
        homeLabel.setFont(LABEL_FONT);
        homePanel.add(homeLabel);

        homeCurrencyBox = new JComboBox<>();
        homePanel.add(homeCurrencyBox);
        content.add(homePanel);
        content.add(Box.createVerticalStrut(20));

        // --- Line-item rows ---
        JPanel itemsPanel = new JPanel(new GridBagLayout());
        itemsPanel.setBackground(BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 4, 8, 4);
        gbc.anchor = GridBagConstraints.WEST;

        for (int i = 0; i < MAX_ITEMS; i++) {
            gbc.gridy = i;

            // "Item n:"
            gbc.gridx = 0;
            itemsPanel.add(new JLabel("Item " + (i + 1) + ":"), gbc);

            // amount field
            gbc.gridx = 1;
            amountFields[i] = new JTextField(10);
            itemsPanel.add(amountFields[i], gbc);

            // "in"
            gbc.gridx = 2;
            itemsPanel.add(new JLabel("in"), gbc);

            // currency combo
            gbc.gridx = 3;
            gbc.weightx = 1.0;
            currencyBoxes[i] = new JComboBox<>();
            itemsPanel.add(currencyBoxes[i], gbc);
            gbc.weightx = 0.0;
        }

        content.add(itemsPanel);
        content.add(Box.createVerticalStrut(20));

        // --- Total label ---
        totalLabel = new JLabel("Total: --");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(totalLabel);
        content.add(Box.createVerticalStrut(10));

        // --- Breakdown area ---
        breakdownArea = new JTextArea(4, 60);
        breakdownArea.setEditable(false);
        breakdownArea.setLineWrap(true);
        breakdownArea.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(breakdownArea);
        content.add(scroll);
        content.add(Box.createVerticalStrut(10));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(BG);

        calculateButton = new JButton("Calculate");
        backButton = new JButton("Back to Home");

        buttonPanel.add(calculateButton);
        buttonPanel.add(backButton);
        content.add(buttonPanel);

        add(content, BorderLayout.CENTER);

        // Listeners
        calculateButton.addActionListener(e -> onCalculate());

        backButton.addActionListener(e -> {
            if (controller != null) {
                controller.switchToHome();
            } else {
                viewManagerModel.setActiveView("home");
                viewManagerModel.firePropertyChange();
            }
        });

        // Load Data on Show
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                if (homeCurrencyBox.getItemCount() == 0 && loadCurrenciesController != null) {
                    loadCurrenciesController.execute();
                } else {
                    updateCurrencyDropdowns(); // Ensure latest list
                }
            }
        });
    }

    // --- Helper: Populate all dropdowns from ConvertViewModel ---
    private void updateCurrencyDropdowns() {
        String[] codes = convertViewModel.getState().getCurrencyCodes();

        if (codes == null || codes.length == 0) return;

        // Update Home Box
        Object currentHome = homeCurrencyBox.getSelectedItem();
        homeCurrencyBox.removeAllItems();

        // Update All Line Item Boxes
        for (JComboBox<String> box : currencyBoxes) {
            box.removeAllItems();
        }

        // Iterator Pattern
        java.util.List<String> codeList = java.util.Arrays.asList(codes);
        Iterator<String> iterator = codeList.iterator();

        while (iterator.hasNext()) {
            String code = iterator.next();
            homeCurrencyBox.addItem(code);
            for (JComboBox<String> box : currencyBoxes) {
                box.addItem(code);
            }
        }

        // Restore selection if possible
        if (currentHome != null) homeCurrencyBox.setSelectedItem(currentHome);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // Handle List Update
        if ("currencyListLoaded".equals(evt.getPropertyName())) {
            updateCurrencyDropdowns();
        }

        // Handle Budget State Update
        TravelBudgetState state = travelBudgetViewModel.getState();
        if (state == null) return;

        if (state.getHomeCurrency() != null) {
            // Only update if selection changed logic is needed, usually ignored to keep user selection
        }

        if (state.getTotalFormatted() != null) {
            totalLabel.setText("Total: " + state.getTotalFormatted());
        }

        if (state.getLineItems() != null) {
            StringBuilder sb = new StringBuilder();
            for (String line : state.getLineItems()) {
                sb.append(line).append('\n');
            }
            breakdownArea.setText(sb.toString());
        }

        if (state.getError() != null && !state.getError().isEmpty()) {
            JOptionPane.showMessageDialog(this, state.getError(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCalculate() {
        if (controller == null) return;

        String home = (String) homeCurrencyBox.getSelectedItem();
        List<String> sourceCurrencies = new ArrayList<>();
        List<String> amounts = new ArrayList<>(); // Fixed: Logic requires Double list

        for (int i = 0; i < MAX_ITEMS; i++) {
            String amountText = amountFields[i].getText().trim();
            String currencyName = (String) currencyBoxes[i].getSelectedItem();

            if (!amountText.isEmpty() && currencyName != null) {
                try {
                    String val = amountText;
                    amounts.add(val);
                    sourceCurrencies.add(currencyName);
                } catch (NumberFormatException e) {
                    // Ignore bad input or show error
                }
            }
        }

        if (sourceCurrencies.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter at least one item.", "No items", JOptionPane.WARNING_MESSAGE);
            return;
        }

        controller.execute(home, sourceCurrencies, amounts);
    }

    @Override
    public void actionPerformed(ActionEvent e) {}

    // Setters
    public void setTravelBudgetController(TravelBudgetController controller) {
        this.controller = controller;
    }

    public void setLoadCurrenciesController(LoadCurrenciesController controller) {
        this.loadCurrenciesController = controller;
    }

    public String getViewName() { return viewName; }

}