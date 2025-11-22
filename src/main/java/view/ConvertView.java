package view;


import interface_adapter.ViewManagerModel;

import interface_adapter.convert_currency.ConvertController;

import interface_adapter.convert_currency.ConvertState;

import interface_adapter.convert_currency.ConvertViewModel;


import javax.swing.*;

import java.awt.*;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;

import java.beans.PropertyChangeListener;

import java.util.Objects;


public class ConvertView extends JPanel implements ActionListener, PropertyChangeListener {


    public final String viewName = "convert";

    private final ConvertViewModel viewModel;

    private ConvertController convertController;


    // UI Components

    private final JComboBox<String> fromBox;

    private final JComboBox<String> toBox;

    private final JTextField amountField;

    private final JButton convertBtn; // NEW: The Submit Button

    private final JLabel resultLabel;

    private final JLabel rateDetailLabel;

    private final JLabel errorLabel;

    private final JButton backBtn;


    public ConvertView(ViewManagerModel viewManagerModel, ConvertViewModel viewModel) {

        this.viewModel = viewModel;

        this.viewModel.addPropertyChangeListener(this);


        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 5, 5, 5);


        // --- 1. Input Fields ---

        // Note: Ensure your Repository handles these full names, or switch to codes like "USD"

        String[] currencies = {"Turkish Lira", "Lebanese Pound", "United States Dollar"};


        gbc.gridx = 0; gbc.gridy = 0; add(new JLabel("From:"), gbc);

        gbc.gridx = 1; fromBox = new JComboBox<>(currencies); add(fromBox, gbc);


        gbc.gridx = 2; gbc.gridy = 0; add(new JLabel("To:"), gbc);

        gbc.gridx = 3; toBox = new JComboBox<>(currencies); add(toBox, gbc);


        gbc.gridx = 0; gbc.gridy = 1; add(new JLabel("Amount:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 3; amountField = new JTextField(15); add(amountField, gbc);


        // --- 2. Convert Button (The Submit Action) ---

        convertBtn = new JButton("Convert");

        convertBtn.setFont(new Font("SansSerif", Font.BOLD, 15));

        convertBtn.setBackground(new Color(100, 200, 100)); // Greenish hint



        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 2;

        add(convertBtn, gbc);


        // --- 3. Output Display ---

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;

        resultLabel = new JLabel("Enter amount and click Convert.");

        resultLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);

        add(resultLabel, gbc);


        gbc.gridy = 4;

        rateDetailLabel = new JLabel("");

        rateDetailLabel.setHorizontalAlignment(SwingConstants.CENTER);

        add(rateDetailLabel, gbc);


        gbc.gridy = 5;

        errorLabel = new JLabel("");

        errorLabel.setForeground(Color.RED);

        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);

        add(errorLabel, gbc);


        // --- 4. Navigation ---

        backBtn = new JButton("Back to Hub");

        gbc.gridy = 6; gbc.gridwidth = 4;

        add(backBtn, gbc);



        // --- LISTENERS ---


        // A. CONVERT BUTTON LISTENER (The logic source)

        convertBtn.addActionListener(

                new ActionListener() {

                    public void actionPerformed(ActionEvent evt) {

                        if (evt.getSource().equals(convertBtn)) {



                            // 1. Gather Data from View

                            String amountText = amountField.getText();

                            String from = Objects.requireNonNull(fromBox.getSelectedItem()).toString();

                            String to = Objects.requireNonNull(toBox.getSelectedItem()).toString();


                            // 2. Update ViewModel State (To preserve input if view changes)

                            ConvertState currentState = viewModel.getState();

                            currentState.setAmount(amountText);

                            currentState.setFromCurrency(from);

                            currentState.setToCurrency(to);

                            viewModel.setState(currentState);


                            // 3. Call Controller (Triggers the Use Case)

                            if (convertController != null) {

                                // This executes the Interactor -> API -> Presenter -> ViewModel flow

                                convertController.execute(amountText, from, to);

                            }

                        }

                    }

                }

        );


        // B. Navigation Listener

        backBtn.addActionListener(e -> {

            viewManagerModel.setActiveView("home");

            viewManagerModel.firePropertyChange();

        });


        // Initialize fields from ViewModel state (if returning to this screen)

        ConvertState initialState = viewModel.getState();

        if (initialState.getFromCurrency() != null) fromBox.setSelectedItem(initialState.getFromCurrency());

        if (initialState.getToCurrency() != null) toBox.setSelectedItem(initialState.getToCurrency());

        if (initialState.getAmount() != null) amountField.setText(initialState.getAmount());

    }


    @Override

    public void actionPerformed(ActionEvent e) {

        // Standard ActionListener implementation (optional if using lambdas above)

    }


    @Override

    public void propertyChange(PropertyChangeEvent evt) {

        // Update display when the Presenter updates the ViewModel state

        ConvertState state = (ConvertState) viewModel.getState();


        if (state.getError() != null) {

            errorLabel.setText("Error: " + state.getError());

            resultLabel.setText("Conversion Failed.");

            rateDetailLabel.setText("");

        } else {

            errorLabel.setText("");

            // Only update the result label if we actually have a result

            if (state.getConvertedAmountResult() != null && !state.getConvertedAmountResult().equals("0.00")) {

                resultLabel.setText(

                        state.getAmount() + " " + state.getFromCurrency() +

                                " = " + state.getConvertedAmountResult() + " " + state.getToCurrency()

                );

                rateDetailLabel.setText(state.getRateDetails());

            }

        }

    }


    public String getViewName() { return viewName; }


    public void setConvertController(ConvertController convertController) {

        this.convertController = convertController;

    }

}