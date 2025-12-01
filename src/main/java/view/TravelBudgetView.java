package view;

import interface_adapter.ViewManagerModel;
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
import java.util.List;

/**
 * Travel Budget Calculator screen.
 *
 * Lets the user pick:
 *  - a HOME currency
 *  - up to 5 line items (amount + currency)
 *
 * The use case returns:
 *  - total in the home currency
 *  - a list of formatted line-item strings
 */
public class TravelBudgetView extends JPanel
        implements ActionListener, PropertyChangeListener {

    public final String viewName = "travel_budget";

    // Architecture
    private final ViewManagerModel viewManagerModel;
    private final TravelBudgetViewModel viewModel;
    private TravelBudgetController controller;

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

    public TravelBudgetView(ViewManagerModel viewManagerModel,
                            TravelBudgetViewModel viewModel,
                            List<String> allCurrencies) {
        this.viewManagerModel = viewManagerModel;
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);

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
        if (allCurrencies != null) {
            for (String c : allCurrencies) {
                homeCurrencyBox.addItem(c);
            }
        }
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
            if (allCurrencies != null) {
                for (String c : allCurrencies) {
                    currencyBoxes[i].addItem(c);
                }
            }
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
                viewManagerModel.firePropertyChanged();
            }
        });
    }

    // Wiring from AppBuilder
    public void setTravelBudgetController(TravelBudgetController controller) {
        this.controller = controller;
    }

    public String getViewName() {
        return viewName;
    }

    // Actions

    private void onCalculate() {
        if (controller == null) {
            return;
        }

        String home = (String) homeCurrencyBox.getSelectedItem();
        List<String> sourceCurrencies = new ArrayList<>();
        List<String> amountStrings = new ArrayList<>();

        for (int i = 0; i < MAX_ITEMS; i++) {
            String amountText = amountFields[i].getText().trim();
            String currencyName = (String) currencyBoxes[i].getSelectedItem();

            // Only send rows where user typed something and picked his currency
            if (!amountText.isEmpty() && currencyName != null) {
                amountStrings.add(amountText);
                sourceCurrencies.add(currencyName);
            }
        }

        if (sourceCurrencies.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter at least one item (amount + currency).",
                    "No items",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Controller will wrap into TravelBudgetInputData and call the interactor.
        controller.execute(home, sourceCurrencies, amountStrings);
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        TravelBudgetState state = viewModel.getState();
        if (state == null) {
            return;
        }

        if (state.getHomeCurrency() != null) {
            homeCurrencyBox.setSelectedItem(state.getHomeCurrency());
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
            JOptionPane.showMessageDialog(
                    this,
                    state.getError(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }
}
