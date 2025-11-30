package view;


import interface_adapter.ViewManagerModel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


import interface_adapter.convert_currency.ConvertController;

import interface_adapter.convert_currency.ConvertState;

import interface_adapter.convert_currency.ConvertViewModel;

import interface_adapter.favourite_currency.FavouriteCurrencyController;

import interface_adapter.recent_currency.RecentCurrencyController;

import interface_adapter.recent_currency.RecentCurrencyViewModel;

import use_case.recent_currency.RecentCurrencyDataAccessInterface;

import interface_adapter.logged_in.HomeViewModel;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;





import javax.swing.*;

import java.awt.*;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;

import java.beans.PropertyChangeListener;

import java.util.Objects;

import java.awt.Insets;

import interface_adapter.favourite_currency.FavouriteCurrencyViewModel;



public class ConvertView extends JPanel implements ActionListener, PropertyChangeListener {


    public final String viewName = "convert";

    private final ConvertViewModel viewModel;

    private final java.util.List<String> baseCurrencies;

    private ConvertController convertController;

    private RecentCurrencyViewModel recentCurrencyViewModel;

    private FavouriteCurrencyViewModel favouriteCurrencyViewModel;


    // Controller for Use Case 5 (Favourites)
    private FavouriteCurrencyController favouriteCurrencyController;

    // Controller for Use Case 8 (Recent / Frequent currencies)
    private RecentCurrencyController recentCurrencyController;

    private RecentCurrencyDataAccessInterface recentDAO;

    private final HomeViewModel homeViewModel;




    // UI Components

    private final JComboBox<String> fromBox;

    private final JComboBox<String> toBox;

    private final JTextField amountField;

    private final JButton convertBtn; // NEW: The Submit Button

    private final JLabel resultLabel;

    private final JLabel rateDetailLabel;

    private final JLabel errorLabel;

    private final JButton backBtn;

    private JButton favouriteFromBtn;

    private JButton favouriteToBtn;

    // --- AUTO REFRESH UI ---
    private final JCheckBox autoRefreshCheckBox = new JCheckBox("Auto refresh");
    private final JLabel lastUpdatedLabel = new JLabel("Last update: --");

    // Swing Timer for auto refresh
    private javax.swing.Timer autoRefreshTimer;

    private static final DateTimeFormatter LAST_UPDATED_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");



    public ConvertView(ViewManagerModel viewManagerModel, ConvertViewModel viewModel, java.util.List<String> baseCurrencies, HomeViewModel homeViewModel) {

        this.viewModel = viewModel;

        this.baseCurrencies = baseCurrencies;

        this.homeViewModel = homeViewModel;

        this.viewModel.addPropertyChangeListener(this);


        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 5, 5, 5);


        // --- 1. Input Fields ---

        String[] currencies = baseCurrencies.toArray(new String[0]);
        favouriteFromBtn = new JButton("★");
        favouriteFromBtn.setMargin(new Insets(2, 6, 2, 6));
        favouriteFromBtn.setToolTipText("Add FROM currency to favourites");

        favouriteToBtn = new JButton("★");
        favouriteToBtn.setMargin(new Insets(2, 6, 2, 6));
        favouriteToBtn.setToolTipText("Add TO currency to favourites");


        gbc.gridx = 0; gbc.gridy = 0; add(new JLabel("From:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; fromBox = new JComboBox<>(); add(fromBox, gbc);
        // Add star button for FROM
        gbc.gridx = 2; gbc.gridy = 0;
        add(favouriteFromBtn, gbc);



        gbc.gridx = 0; gbc.gridy = 1; add(new JLabel("To:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1; toBox = new JComboBox<>(); add(toBox, gbc);
        // Add star button for TO
        gbc.gridx = 2; gbc.gridy = 1;
        add(favouriteToBtn, gbc);



        gbc.gridx = 0; gbc.gridy = 2; add(new JLabel("Amount:"), gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 3; amountField = new JTextField(15); add(amountField, gbc);


        // --- 2. Convert Button (The Submit Action) ---

        convertBtn = new JButton("Convert");

        convertBtn.setFont(new Font("SansSerif", Font.BOLD, 15));

        convertBtn.setBackground(new Color(100, 200, 100)); // Greenish hint



        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 1;

        add(convertBtn, gbc);

        // --- 3. Output Display ---

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4;

        resultLabel = new JLabel("Enter amount and click Convert.");

        resultLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);

        add(resultLabel, gbc);


        gbc.gridy = 5;

        rateDetailLabel = new JLabel("");

        rateDetailLabel.setHorizontalAlignment(SwingConstants.CENTER);

        add(rateDetailLabel, gbc);


        gbc.gridy = 6;

        errorLabel = new JLabel("");

        errorLabel.setForeground(Color.RED);

        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);

        add(errorLabel, gbc);

        // --- 3.5 AUTO REFRESH UI ---
        gbc.gridy = 7;
        gbc.gridwidth = 4;
        add(autoRefreshCheckBox, gbc);

        gbc.gridy = 8;
        add(lastUpdatedLabel, gbc);



        // --- 4. Navigation ---

        backBtn = new JButton("Back to Hub");

        gbc.gridy = 9; gbc.gridwidth = 4;

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
                                if (recentCurrencyController != null) {
                                    String userId = homeViewModel.getState().getUsername();
                                    recentCurrencyController.execute(userId, from, to);
                                }
                            }

                        }

                    }

                }

        );
        favouriteFromBtn.addActionListener(e -> {
            if (favouriteCurrencyController == null) {
                return;
            }

            String userId = homeViewModel.getState().getUsername();
            Object selected = fromBox.getSelectedItem();
            if (selected == null) {
                return;
            }
            String currencyCode = selected.toString();

            favouriteCurrencyController.execute(userId, currencyCode, true);

            //if (recentCurrencyController != null) {
              //  recentCurrencyController.execute(userId, currencyCode, currencyCode);
            //}
        });

        favouriteToBtn.addActionListener(e -> {
            if (favouriteCurrencyController == null) {
                return;
            }

            String userId = homeViewModel.getState().getUsername();
            Object selected = toBox.getSelectedItem();
            if (selected == null) {
                return;
            }
            String currencyCode = selected.toString();

            favouriteCurrencyController.execute(userId, currencyCode, true);

            //if (recentCurrencyController != null) {
             //   recentCurrencyController.execute(userId, currencyCode, currencyCode);
            //}
        });


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

        // reshow ConvertView
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                updateCurrencyDropdown();
            }
        });


        updateCurrencyDropdown();

        autoRefreshCheckBox.addActionListener(e -> {
            if (autoRefreshCheckBox.isSelected()) {
                int intervalMillis = 60 * 60 * 1000; // currency updates every 1 hour if user enables the autorefresh

                autoRefreshTimer = new javax.swing.Timer(intervalMillis, ev -> {
                    convertBtn.doClick();
                });
                autoRefreshTimer.start();

            } else {
                if (autoRefreshTimer != null) {
                    autoRefreshTimer.stop();
                    autoRefreshTimer = null;
                }
            }
        });


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

                // Updates 'Last updated" on every successful conversion
                lastUpdatedLabel.setText(
                        "Last update: " + LocalDateTime.now().format(LAST_UPDATED_FMT)
                );
            }

        }

    }


    public String getViewName() { return viewName; }


    public void setConvertController(ConvertController convertController) {

        this.convertController = convertController;

    }
    public void setFavouriteCurrencyController(FavouriteCurrencyController controller) {
        this.favouriteCurrencyController = controller;
    }

    public void setRecentCurrencyController(RecentCurrencyController controller) {
        this.recentCurrencyController = controller;
    }
    public void setRecentCurrencyDAO(RecentCurrencyDataAccessInterface dao) {
        this.recentDAO = dao;
        // When the DAO is wired, refresh the dropdown so favourites from CSV
        // appear at the top even before any in-session favourite actions.
        updateCurrencyDropdown();
    }

    public void setRecentCurrencyViewModel(RecentCurrencyViewModel viewModel) {
        this.recentCurrencyViewModel = viewModel;
        // When recent/frequent currencies change, update dropdowns
        this.recentCurrencyViewModel.addPropertyChangeListener(evt -> updateCurrencyDropdown());
    }
    public void setFavouriteCurrencyViewModel(FavouriteCurrencyViewModel vm) {
        this.favouriteCurrencyViewModel = vm;

        // When favourite currencies update → request UI to update dropdown
        this.favouriteCurrencyViewModel.addPropertyChangeListener(evt -> updateCurrencyDropdown());
    }

    private void updateCurrencyDropdown() {
        java.util.List<String> ordered = null;

        // 1. get new order from DAO
        if (recentDAO != null && homeViewModel != null && homeViewModel.getState() != null) {
            String userId = homeViewModel.getState().getUsername();
            if (userId != null && !userId.isEmpty()) {
                ordered = recentDAO.getOrderedCurrenciesForUser(userId);
            }
        }

        // 2. if DAO not return，use baseCurrencies
        if ((ordered == null || ordered.isEmpty()) && baseCurrencies != null) {
            ordered = baseCurrencies;
        }

        if (ordered == null || ordered.isEmpty()) {
            return;
        }

        fromBox.removeAllItems();
        toBox.removeAllItems();

        for (String code : ordered) {
            fromBox.addItem(code);
            toBox.addItem(code);
        }
    }
}