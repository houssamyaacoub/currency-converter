package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.convert_currency.ConvertController;
import interface_adapter.convert_currency.ConvertState;
import interface_adapter.convert_currency.ConvertViewModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.*;
import java.util.Objects;

public class ConvertView extends JPanel implements PropertyChangeListener {

    public final String viewName = "convert";
    private final ConvertViewModel viewModel;
    private ConvertController convertController;
    // UI Components for input/output
    private final JComboBox<String> fromBox;
    private final JComboBox<String> toBox;
    private final JTextField amountField;
    private final JLabel resultLabel;
    private final JLabel rateDetailLabel;
    private final JLabel errorLabel;

    public ConvertView(ViewManagerModel viewManagerModel, ConvertViewModel viewModel) {
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this); // View listens to the ViewModel

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // --- Input Fields ---
        String[] currencies = {"CAD", "USD", "EUR", "JPY"};

        gbc.gridx = 0; gbc.gridy = 0; add(new JLabel("From:"), gbc);
        gbc.gridx = 1; fromBox = new JComboBox<>(currencies); add(fromBox, gbc);

        gbc.gridx = 2; gbc.gridy = 0; add(new JLabel("To:"), gbc);
        gbc.gridx = 3; toBox = new JComboBox<>(currencies); add(toBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1; add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; amountField = new JTextField(15); add(amountField, gbc);

        // --- Output Display ---
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        resultLabel = new JLabel("Result: Select currencies and enter amount.");
        resultLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(resultLabel, gbc);

        gbc.gridy = 3;
        rateDetailLabel = new JLabel("Rate: N/A");
        add(rateDetailLabel, gbc);

        gbc.gridy = 4;
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        add(errorLabel, gbc);

        // --- Back Button ---
        gbc.gridy = 5; gbc.gridwidth = 4;
        JButton backBtn = new JButton("Back to Hub");
        add(backBtn, gbc);

        // --- LISTENERS ---

        // Helper to trigger the full conversion logic
        Runnable conversionTrigger = () -> {
            String amountText = amountField.getText();
            String from = Objects.requireNonNull(fromBox.getSelectedItem()).toString();
            String to = Objects.requireNonNull(toBox.getSelectedItem()).toString();

            // 1. Update ViewModel with current input (raw state)
            ConvertState currentState = viewModel.getState();
            currentState.setAmount(amountText);
            currentState.setFromCurrency(from);
            currentState.setToCurrency(to);
            viewModel.setState(currentState);

        };

        // 1. Live Conversion Trigger (DocumentListener on Amount Field)
        amountField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { conversionTrigger.run(); }
            @Override public void removeUpdate(DocumentEvent e) { conversionTrigger.run(); }
            @Override public void changedUpdate(DocumentEvent e) {}
        });

        // 2. Currency Selection Listener (Triggers conversion if amount is already present)
        fromBox.addActionListener(e -> conversionTrigger.run());
        toBox.addActionListener(e -> conversionTrigger.run());

        // 3. Navigation
        backBtn.addActionListener(e -> {
            viewManagerModel.setState("home");
            viewManagerModel.firePropertyChange();
        });

        // Set initial state from ViewModel
        ConvertState initialState = viewModel.getState();
        fromBox.setSelectedItem(initialState.getFromCurrency());
        toBox.setSelectedItem(initialState.getToCurrency());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // Update display when the Presenter updates the ViewModel state
        ConvertState state = (ConvertState) viewModel.getState();

        if (state.getError() != null) {
            errorLabel.setText("Error: " + state.getError());
            resultLabel.setText("Conversion Failed.");
            rateDetailLabel.setText("Rate: N/A");
        } else {
            errorLabel.setText("");
            resultLabel.setText(
                    state.getAmount() + " " + state.getFromCurrency() +
                            " = " + state.getConvertedAmountResult() + " " + state.getToCurrency()
            );
            rateDetailLabel.setText(state.getRateDetails());
        }
    }

    public String getViewName() { return viewName; }

    public void setConvertController(ConvertController convertController) {}
}