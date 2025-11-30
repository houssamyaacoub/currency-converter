package view;

import interface_adapter.ViewManagerModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import interface_adapter.convert_currency.ConvertController;
import interface_adapter.convert_currency.ConvertState;
import interface_adapter.convert_currency.ConvertViewModel;

import interface_adapter.favourite_currency.FavouriteCurrencyController;
import interface_adapter.favourite_currency.FavouriteCurrencyViewModel;

import interface_adapter.recent_currency.RecentCurrencyController;
import interface_adapter.recent_currency.RecentCurrencyViewModel;
import use_case.recent_currency.RecentCurrencyDataAccessInterface;

import interface_adapter.logged_in.HomeViewModel;

import interface_adapter.compare_currencies.CompareCurrenciesController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class ConvertView extends JPanel implements ActionListener, PropertyChangeListener {

    public final String viewName = "convert";

    private final ConvertViewModel viewModel;
    private final java.util.List<String> baseCurrencies;
    private final HomeViewModel homeViewModel;

    private ConvertController convertController;
    private RecentCurrencyViewModel recentCurrencyViewModel;
    private FavouriteCurrencyViewModel favouriteCurrencyViewModel;
    private FavouriteCurrencyController favouriteCurrencyController;
    private RecentCurrencyController recentCurrencyController;
    private RecentCurrencyDataAccessInterface recentDAO;

    // NEW: controller for Use Case 6 (multi-currency compare)
    private CompareCurrenciesController compareCurrenciesController;

    // UI Components
    private final JComboBox<String> fromBox;
    private final JComboBox<String> toBox;
    private final JTextField amountField;
    private final JButton convertBtn;
    private final JButton compareMultipleBtn;
    private final JLabel resultLabel;
    private final JLabel rateDetailLabel;
    private final JLabel errorLabel;
    private final JButton backBtn;
    private JButton favouriteFromBtn;
    private JButton favouriteToBtn;

    // Auto refresh stuff
    private final JCheckBox autoRefreshCheckBox = new JCheckBox("Auto refresh");
    private final JLabel lastUpdatedLabel = new JLabel("Last update: --");
    private javax.swing.Timer autoRefreshTimer;
    private static final DateTimeFormatter LAST_UPDATED_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ConvertView(ViewManagerModel viewManagerModel,
                       ConvertViewModel viewModel,
                       java.util.List<String> baseCurrencies,
                       HomeViewModel homeViewModel) {

        this.viewModel = viewModel;
        this.baseCurrencies = baseCurrencies;
        this.homeViewModel = homeViewModel;

        this.viewModel.addPropertyChangeListener(this);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // --- 1. Input fields (From / To / Amount) ---

        favouriteFromBtn = new JButton("★");
        favouriteFromBtn.setMargin(new Insets(2, 6, 2, 6));
        favouriteFromBtn.setToolTipText("Add FROM currency to favourites");

        favouriteToBtn = new JButton("★");
        favouriteToBtn.setMargin(new Insets(2, 6, 2, 6));
        favouriteToBtn.setToolTipText("Add TO currency to favourites");

        // From
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("From:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        fromBox = new JComboBox<>();
        add(fromBox, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        add(favouriteFromBtn, gbc);

        // To
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("To:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        toBox = new JComboBox<>();
        add(toBox, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        add(favouriteToBtn, gbc);

        // Amount
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Amount:"), gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 3;
        amountField = new JTextField(15);
        add(amountField, gbc);

        // --- 2. Convert + Compare Multiple buttons ---

        convertBtn = new JButton("Convert");
        convertBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        convertBtn.setBackground(new Color(100, 200, 100)); // green-ish

        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 1;
        add(convertBtn, gbc);

        // NEW button for use case 6
        compareMultipleBtn = new JButton("Compare Multiple");
        compareMultipleBtn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        compareMultipleBtn.setBackground(new Color(180, 200, 255));

        gbc.gridx = 2; gbc.gridy = 3; gbc.gridwidth = 1;
        add(compareMultipleBtn, gbc);

        // --- 3. Output labels (single conversion) ---

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

        // --- 3.5 Auto refresh controls ---

        gbc.gridy = 7;
        gbc.gridwidth = 4;
        add(autoRefreshCheckBox, gbc);

        gbc.gridy = 8;
        add(lastUpdatedLabel, gbc);

        // --- 4. Navigation back ---

        backBtn = new JButton("Back to Hub");
        gbc.gridy = 9; gbc.gridwidth = 4;
        add(backBtn, gbc);

        // --- Listeners: Convert button ---

        convertBtn.addActionListener(evt -> {
            if (!evt.getSource().equals(convertBtn)) return;

            String amountText = amountField.getText();
            Object fromObj = fromBox.getSelectedItem();
            Object toObj = toBox.getSelectedItem();

            if (fromObj == null || toObj == null) {
                JOptionPane.showMessageDialog(this,
                        "Please select both FROM and TO currencies.",
                        "Missing selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String from = fromObj.toString();
            String to = toObj.toString();

            // Keep state updated so if we come back later, we see the same inputs.
            ConvertState currentState = viewModel.getState();
            currentState.setAmount(amountText);
            currentState.setFromCurrency(from);
            currentState.setToCurrency(to);
            viewModel.setState(currentState);

            if (convertController != null) {
                convertController.execute(amountText, from, to);

                // Also record as recent usage for this user
                if (recentCurrencyController != null && homeViewModel != null
                        && homeViewModel.getState() != null) {
                    String userId = homeViewModel.getState().getUsername();
                    if (userId != null && !userId.isEmpty()) {
                        recentCurrencyController.execute(userId, from, to);
                    }
                }
            }
        });

        // --- Listeners: favourite buttons ---

        favouriteFromBtn.addActionListener(e -> {
            if (favouriteCurrencyController == null || homeViewModel == null || homeViewModel.getState() == null) {
                return;
            }

            String userId = homeViewModel.getState().getUsername();
            Object selected = fromBox.getSelectedItem();
            if (userId == null || userId.isEmpty() || selected == null) {
                return;
            }

            String currencyCode = selected.toString();
            favouriteCurrencyController.execute(userId, currencyCode, true);
        });

        favouriteToBtn.addActionListener(e -> {
            if (favouriteCurrencyController == null || homeViewModel == null || homeViewModel.getState() == null) {
                return;
            }

            String userId = homeViewModel.getState().getUsername();
            Object selected = toBox.getSelectedItem();
            if (userId == null || userId.isEmpty() || selected == null) {
                return;
            }

            String currencyCode = selected.toString();
            favouriteCurrencyController.execute(userId, currencyCode, true);
        });

        // --- Navigation: back to hub ---

        backBtn.addActionListener(e -> {
            viewManagerModel.setActiveView("home");
            viewManagerModel.firePropertyChange();
        });

        // --- Show-time hook: whenever this view becomes visible, refresh dropdown ---

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                updateCurrencyDropdown();
            }
        });

        // --- NEW: Compare Multiple button behaviour ---

        compareMultipleBtn.addActionListener(e -> {
            if (compareCurrenciesController == null) {
                JOptionPane.showMessageDialog(this,
                        "Multi-compare feature is not wired yet.",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Object fromSelected = fromBox.getSelectedItem();
            if (fromSelected == null) {
                JOptionPane.showMessageDialog(this,
                        "Please choose a base currency first.",
                        "Missing base",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String base = fromSelected.toString();
            openMultiCompareDialog(base);
        });

        // --- Auto-refresh checkbox ---

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

        // Initialize dropdown contents & restore previous state
        updateCurrencyDropdown();
        ConvertState initialState = viewModel.getState();
        if (initialState.getFromCurrency() != null) {
            fromBox.setSelectedItem(initialState.getFromCurrency());
        }
        if (initialState.getToCurrency() != null) {
            toBox.setSelectedItem(initialState.getToCurrency());
        }
        if (initialState.getAmount() != null) {
            amountField.setText(initialState.getAmount());
        }
    }

    // --- Helper: open the "selection page" for multiple compare, with checkboxes ---

    private void openMultiCompareDialog(String baseCurrency) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(5, 5));

        JLabel info = new JLabel("Pick up to 5 currencies to compare against " + baseCurrency);
        panel.add(info, BorderLayout.NORTH);

        JPanel checkBoxPanel = new JPanel();
        checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.Y_AXIS));

        List<JCheckBox> boxes = new ArrayList<>();

        for (String code : baseCurrencies) {
            // Optionally skip the base currency itself
            if (code.equals(baseCurrency)) {
                continue;
            }

            JCheckBox cb = new JCheckBox(code);
            boxes.add(cb);
            checkBoxPanel.add(cb);
        }

        JScrollPane scrollPane = new JScrollPane(checkBoxPanel);
        scrollPane.setPreferredSize(new Dimension(260, 220));
        panel.add(scrollPane, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Compare multiple currencies",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        List<String> selectedTargets = new ArrayList<>();
        for (JCheckBox cb : boxes) {
            if (cb.isSelected()) {
                selectedTargets.add(cb.getText());
            }
        }

        if (selectedTargets.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select at least one target currency.",
                    "No selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedTargets.size() > 5) {
            JOptionPane.showMessageDialog(this,
                    "You can compare at most 5 currencies.",
                    "Too many selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Fire the use case → interactor will talk to API, presenter will fill ConvertState
        compareCurrenciesController.execute(baseCurrency, selectedTargets);
    }

    // --- Helper: show the bar chart popup for the multi-compare results ---

    private void showComparisonChart(String baseCurrency,
                                     List<String> targets,
                                     List<Double> rates) {
        if (targets == null || rates == null || targets.isEmpty() || rates.isEmpty()) {
            return;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Each bar: "how many units of target per 1 base"
        for (int i = 0; i < targets.size(); i++) {
            String target = targets.get(i);
            Double rate = rates.get(i);
            if (rate != null) {
                dataset.addValue(rate, "Rate", target);
            }
        }

        String title = "Relative strength vs " + baseCurrency;
        String xLabel = "Target currency";
        String yLabel = "Units of target per 1 " + baseCurrency;

        JFreeChart chart = ChartFactory.createBarChart(
                title,
                xLabel,
                yLabel,
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 400));

        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Multi-currency comparison",
                Dialog.ModalityType.MODELESS
        );
        dialog.getContentPane().add(chartPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // not really used, we’re wiring actions with lambdas
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // Single-conversion display (old behaviour)
        ConvertState state = viewModel.getState();

        if (state.getError() != null) {
            errorLabel.setText("Error: " + state.getError());
            resultLabel.setText("Conversion Failed.");
            rateDetailLabel.setText("");
        } else {
            errorLabel.setText("");

            if (state.getConvertedAmountResult() != null
                    && !state.getConvertedAmountResult().equals("0.00")) {

                resultLabel.setText(
                        state.getAmount() + " " + state.getFromCurrency() +
                                " = " + state.getConvertedAmountResult() + " " + state.getToCurrency()
                );
                rateDetailLabel.setText(state.getRateDetails());
            }
            // Updates 'Last updated" on every successful conversion
            lastUpdatedLabel.setText(
                    "Last update: " + LocalDateTime.now().format(LAST_UPDATED_FMT)
            );

        }

        // NEW: check if the compare use case has populated extra data
        if (state.getCompareTargets() != null
                && !state.getCompareTargets().isEmpty()
                && state.getCompareRates() != null
                && !state.getCompareRates().isEmpty()) {

            List<String> targets = new ArrayList<>(state.getCompareTargets());
            List<Double> rates = new ArrayList<>(state.getCompareRates());

            // clear so we don't re-trigger on the next propertyChange
            state.setCompareTargets(new ArrayList<>());
            state.setCompareRates(new ArrayList<>());
            viewModel.setState(state);

            showComparisonChart(state.getFromCurrency(), targets, rates);
        }
    }

    public String getViewName() {
        return viewName;
    }

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
        updateCurrencyDropdown();
    }

    public void setRecentCurrencyViewModel(RecentCurrencyViewModel viewModel) {
        this.recentCurrencyViewModel = viewModel;
        this.recentCurrencyViewModel.addPropertyChangeListener(evt -> updateCurrencyDropdown());
    }

    public void setFavouriteCurrencyViewModel(FavouriteCurrencyViewModel vm) {
        this.favouriteCurrencyViewModel = vm;
        this.favouriteCurrencyViewModel.addPropertyChangeListener(evt -> updateCurrencyDropdown());
    }

    public void setCompareCurrenciesController(CompareCurrenciesController controller) {
        this.compareCurrenciesController = controller;
    }

    private void updateCurrencyDropdown() {
        java.util.List<String> ordered = null;

        if (recentDAO != null && homeViewModel != null && homeViewModel.getState() != null) {
            String userId = homeViewModel.getState().getUsername();
            if (userId != null && !userId.isEmpty()) {
                ordered = recentDAO.getOrderedCurrenciesForUser(userId);
            }
        }

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
