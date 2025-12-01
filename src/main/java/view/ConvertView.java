package view;

import interface_adapter.ViewManagerModel;
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
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.renderer.category.BarRenderer;

import interface_adapter.offline_viewing.OfflineViewModel;
import interface_adapter.offline_viewing.OfflineViewController;



/**
 * ConvertView
 * <p>
 * The main screen for performing currency conversions.
 * Features:
 * 1. Single pair conversion (From -> To).
 * 2. Multi-currency comparison (Bar Chart).
 * 3. Auto-refresh capability.
 * 4. Integration with Favourites and Recent History.
 */
public class ConvertView extends JPanel implements ActionListener, PropertyChangeListener {

    public final String viewName = "convert";

    // --- UI Constants ---
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font RESULT_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color PANEL_COLOR = Color.WHITE;
    private static final Color ACTION_BTN_COLOR = new Color(34, 197, 94);   // Green
    private static final Color COMPARE_BTN_COLOR = new Color(99, 102, 241); // Indigo
    private static final Color TEXT_COLOR = new Color(31, 41, 55);

    // --- Architecture Components ---
    private final ConvertViewModel viewModel;
    private final HomeViewModel homeViewModel;
    private final ViewManagerModel viewManagerModel;
    private final List<String> baseCurrencies;

    // Offline Viewing
    private OfflineViewModel offlineViewModel;           // NEW
    private OfflineViewController offlineViewController;

    // Controllers
    private ConvertController convertController;
    private FavouriteCurrencyController favouriteCurrencyController;
    private RecentCurrencyController recentCurrencyController;
    private CompareCurrenciesController compareCurrenciesController;

    // View Models & DAO
    private RecentCurrencyViewModel recentCurrencyViewModel;
    private FavouriteCurrencyViewModel favouriteCurrencyViewModel;
    private RecentCurrencyDataAccessInterface recentDAO;

    // --- UI Components ---
    private final JComboBox<String> fromBox;
    private final JComboBox<String> toBox;
    private final JTextField amountField;

    private final JButton convertBtn;
    private final JButton compareMultipleBtn;
    private final JButton backBtn;
    private final JButton favouriteFromBtn;
    private final JButton favouriteToBtn;

    private final JLabel resultLabel;
    private final JLabel rateDetailLabel;
    private final JLabel errorLabel;
    private final JLabel lastUpdatedLabel;
    private final JCheckBox autoRefreshCheckBox;

    private final JLabel offlineStatusLabel;

    private javax.swing.Timer autoRefreshTimer;
    private static final DateTimeFormatter LAST_UPDATED_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Constructs the ConvertView.
     */
    public ConvertView(ViewManagerModel viewManagerModel,
                       ConvertViewModel viewModel,
                       List<String> baseCurrencies,
                       HomeViewModel homeViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.viewModel = viewModel;
        this.baseCurrencies = baseCurrencies;
        this.homeViewModel = homeViewModel;

        this.viewModel.addPropertyChangeListener(this);

        // Initialize Components
        fromBox = new JComboBox<>();
        toBox = new JComboBox<>();
        amountField = new JTextField(10);
        amountField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        convertBtn = createStyledButton("Convert", ACTION_BTN_COLOR, Color.WHITE);
        compareMultipleBtn = createStyledButton("Compare Multiple", COMPARE_BTN_COLOR, Color.WHITE);
        backBtn = new JButton("Back to Hub");

        favouriteFromBtn = new JButton("★");
        favouriteFromBtn.setMargin(new Insets(2, 6, 2, 6));
        favouriteFromBtn.setToolTipText("Add FROM currency to favourites");

        favouriteToBtn = new JButton("★");
        favouriteToBtn.setMargin(new Insets(2, 6, 2, 6));
        favouriteToBtn.setToolTipText("Add TO currency to favourites");

        resultLabel = new JLabel("Enter amount and click Convert.");
        resultLabel.setFont(RESULT_FONT);
        resultLabel.setForeground(TEXT_COLOR);
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);

        rateDetailLabel = new JLabel(" ");
        rateDetailLabel.setHorizontalAlignment(SwingConstants.CENTER);

        errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);

        autoRefreshCheckBox = new JCheckBox("Auto-refresh (1h)");
        autoRefreshCheckBox.setBackground(PANEL_COLOR);
        lastUpdatedLabel = new JLabel("Last update: --");
        lastUpdatedLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lastUpdatedLabel.setForeground(Color.GRAY);

        offlineStatusLabel = new JLabel(" ");
        offlineStatusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        offlineStatusLabel.setForeground(Color.GRAY);

        initializeUI();
        setupListeners();

        // Initial State Load
        updateCurrencyDropdown();
        restoreState();
    }

    /**
     * Sets up the main visual layout using GridBagLayout for centering.
     */
    private void initializeUI() {
        setLayout(new GridBagLayout());
        setBackground(BACKGROUND_COLOR);

        // Main Card Panel
        JPanel card = new JPanel(new BorderLayout(0, 20));
        card.setBackground(PANEL_COLOR);
        card.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
                new EmptyBorder(30, 40, 30, 40)
        ));

        // 1. Header
        JLabel title = new JLabel("Currency Converter");
        title.setFont(TITLE_FONT);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(title, BorderLayout.NORTH);

        // 2. Center Content
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(PANEL_COLOR);

        // Currency Selectors
        centerPanel.add(createCurrencySelectionPanel());
        centerPanel.add(Box.createVerticalStrut(20));

        // Action Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        actionPanel.setBackground(PANEL_COLOR);
        actionPanel.add(convertBtn);
        actionPanel.add(compareMultipleBtn);
        centerPanel.add(actionPanel);
        centerPanel.add(Box.createVerticalStrut(20));

        // Amount Input (Between Buttons and Results)
        JPanel amountPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        amountPanel.setBackground(PANEL_COLOR);
        JLabel amountLbl = new JLabel("Amount:");
        amountLbl.setFont(LABEL_FONT);
        amountPanel.add(amountLbl);
        amountPanel.add(amountField);
        centerPanel.add(amountPanel);
        centerPanel.add(Box.createVerticalStrut(20));

        // Result Section
        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBackground(new Color(249, 250, 251)); // Very light gray box
        resultPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        resultPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rateDetailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        resultPanel.add(resultLabel);
        resultPanel.add(Box.createVerticalStrut(5));
        resultPanel.add(rateDetailLabel);
        resultPanel.add(Box.createVerticalStrut(5));
        resultPanel.add(errorLabel);

        centerPanel.add(resultPanel);
        centerPanel.add(Box.createVerticalStrut(15));

        // Refresh Section
        JPanel refreshPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        refreshPanel.setBackground(PANEL_COLOR);
        refreshPanel.add(autoRefreshCheckBox);
        refreshPanel.add(Box.createHorizontalStrut(10));
        refreshPanel.add(lastUpdatedLabel);

        refreshPanel.add(Box.createHorizontalStrut(10));
        refreshPanel.add(offlineStatusLabel);

        centerPanel.add(refreshPanel);

        card.add(centerPanel, BorderLayout.CENTER);

        // 3. Footer (Back Button)
        JPanel footer = new JPanel();
        footer.setBackground(PANEL_COLOR);
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backBtn.setFocusPainted(false);
        footer.add(backBtn);
        card.add(footer, BorderLayout.SOUTH);

        add(card);
    }

    /**
     * Creates the form layout for From/To inputs (Amount removed from here).
     */
    private JPanel createCurrencySelectionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 1: From
        addLabel(panel, "From:", 0, 0, gbc);
        addComponent(panel, fromBox, 1, 0, gbc, true);
        addComponent(panel, favouriteFromBtn, 2, 0, gbc, false);

        // Row 2: To
        addLabel(panel, "To:", 0, 1, gbc);
        addComponent(panel, toBox, 1, 1, gbc, true);
        addComponent(panel, favouriteToBtn, 2, 1, gbc, false);

        return panel;
    }

    /**
     * Configures all action listeners.
     */
    private void setupListeners() {
        // Convert Action
        convertBtn.addActionListener(evt -> handleConvertAction());

        // Compare Multiple Action
        compareMultipleBtn.addActionListener(e -> handleMultiCompareAction());

        // Favourites
        favouriteFromBtn.addActionListener(e -> handleFavouriteAction(fromBox.getSelectedItem()));
        favouriteToBtn.addActionListener(e -> handleFavouriteAction(toBox.getSelectedItem()));

        // Back Navigation
        backBtn.addActionListener(e -> {
            viewManagerModel.setActiveView("home");
            viewManagerModel.firePropertyChange();
        });

        // Auto Refresh
        autoRefreshCheckBox.addActionListener(e -> handleAutoRefresh());

        // Update Dropdowns on Show
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                updateCurrencyDropdown();

                if (offlineViewController != null) {
                    offlineViewController.loadOfflineRates();
                }

            }
        });
    }

    private void handleConvertAction() {
        String amountText = amountField.getText();
        Object fromObj = fromBox.getSelectedItem();
        Object toObj = toBox.getSelectedItem();

        if (fromObj == null || toObj == null) {
            JOptionPane.showMessageDialog(this, "Please select both currencies.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String from = fromObj.toString();
        String to = toObj.toString();

        // Update State
        ConvertState currentState = viewModel.getState();
        currentState.setAmount(amountText);
        currentState.setFromCurrency(from);
        currentState.setToCurrency(to);
        viewModel.setState(currentState);

        if (convertController != null) {
            convertController.execute(amountText, from, to);

            // Log to Recent History
            if (recentCurrencyController != null && homeViewModel != null && homeViewModel.getState() != null) {
                String userId = homeViewModel.getState().getUsername();
                if (userId != null && !userId.isEmpty()) {
                    recentCurrencyController.execute(userId, from, to);
                }
            }
        }
    }

    private void handleFavouriteAction(Object selected) {
        if (favouriteCurrencyController == null || homeViewModel == null || homeViewModel.getState() == null) return;

        String userId = homeViewModel.getState().getUsername();
        if (userId == null || userId.isEmpty() || selected == null) return;

        favouriteCurrencyController.execute(userId, selected.toString(), true);
    }

    private void handleMultiCompareAction() {
        if (compareCurrenciesController == null) {
            JOptionPane.showMessageDialog(this, "Multi-compare feature not ready.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Object fromSelected = fromBox.getSelectedItem();
        if (fromSelected == null) return;

        openMultiCompareDialog(fromSelected.toString());
    }

    private void handleAutoRefresh() {
        if (autoRefreshCheckBox.isSelected()) {
            int intervalMillis = 60 * 60 * 1000; // 1 Hour
            autoRefreshTimer = new javax.swing.Timer(intervalMillis, ev -> convertBtn.doClick());
            autoRefreshTimer.start();
        } else {
            if (autoRefreshTimer != null) {
                autoRefreshTimer.stop();
                autoRefreshTimer = null;
            }
        }
    }

    // --- Helper UI Methods ---

    private void openMultiCompareDialog(String baseCurrency) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(new JLabel("Compare " + baseCurrency + " against up to 5 currencies:"), BorderLayout.NORTH);

        JPanel checkBoxPanel = new JPanel();
        checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.Y_AXIS));
        List<JCheckBox> boxes = new ArrayList<>();

        for (String code : baseCurrencies) {
            if (code.equals(baseCurrency)) continue;
            JCheckBox cb = new JCheckBox(code);
            boxes.add(cb);
            checkBoxPanel.add(cb);
        }

        JScrollPane scrollPane = new JScrollPane(checkBoxPanel);
        scrollPane.setPreferredSize(new Dimension(280, 200));
        panel.add(scrollPane, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(this, panel, "Compare Currencies", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            List<String> selected = new ArrayList<>();
            for (JCheckBox cb : boxes) {
                if (cb.isSelected()) selected.add(cb.getText());
            }

            if (selected.isEmpty() || selected.size() > 5) {
                JOptionPane.showMessageDialog(this, "Select between 1 and 5 currencies.", "Selection Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            compareCurrenciesController.execute(baseCurrency, selected);
        }
    }

    private void showComparisonChart(String baseCurrency, List<String> targets, List<Double> rates) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < targets.size(); i++) {
            dataset.addValue(rates.get(i), "Rate", targets.get(i));
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Relative Strength vs " + baseCurrency,
                "Target Currency",
                "Rate",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        // --- Keep bar width reasonable whether there is 1 or 5 bars ---
        CategoryPlot plot = chart.getCategoryPlot();

        // Control bar width
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setMaximumBarWidth(0.15); // 0.0–1.0, fraction of available space per category

        // Add margins around categories so a single bar does not fill the chart
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLowerMargin(0.25);   // left padding
        domainAxis.setUpperMargin(0.25);   // right padding
        domainAxis.setCategoryMargin(0.20); // spacing between bars when there are multiple

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 400)); // consistent visual size

        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Comparison Result",
                Dialog.ModalityType.MODELESS
        );
        dialog.setContentPane(chartPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void restoreState() {
        ConvertState initialState = viewModel.getState();
        if (initialState.getFromCurrency() != null) fromBox.setSelectedItem(initialState.getFromCurrency());
        if (initialState.getToCurrency() != null) toBox.setSelectedItem(initialState.getToCurrency());
        if (initialState.getAmount() != null) amountField.setText(initialState.getAmount());
    }

    // --- PropertyChangeListener ---
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Object source = evt.getSource();

        // -------------------------------
        // 1) ONLINE ConvertViewModel update
        // -------------------------------
        if (source == viewModel) {
            ConvertState state = viewModel.getState();

            if (state.getError() != null) {
                errorLabel.setText("Error: " + state.getError());
                resultLabel.setText("Conversion Failed");
                rateDetailLabel.setText("");
            } else {
                errorLabel.setText("");

                if (state.getConvertedAmountResult() != null
                        && !state.getConvertedAmountResult().equals("0.00")) {

                    resultLabel.setText(
                            state.getAmount() + " " + state.getFromCurrency()
                                    + " = " + state.getConvertedAmountResult()
                                    + " " + state.getToCurrency()
                    );

                    rateDetailLabel.setText(state.getRateDetails());

                    // ONLINE success -> replace timestamps
                    lastUpdatedLabel.setText(
                            "Last update: " + LocalDateTime.now().format(LAST_UPDATED_FMT)
                    );

                    // ONLINE success clears offline message
                    offlineStatusLabel.setText(" ");
                }
            }

            // MULTI-COMPARE
            if (state.getCompareTargets() != null && !state.getCompareTargets().isEmpty()) {
                List<String> targets = new ArrayList<>(state.getCompareTargets());
                List<Double> rates = new ArrayList<>(state.getCompareRates());

                // Clear state to avoid re-trigger
                state.setCompareTargets(new ArrayList<>());
                viewModel.setState(state);

                showComparisonChart(state.getFromCurrency(), targets, rates);
            }

            return;   // IMPORTANT: stop here
        }

        // -------------------------------
        // 2) OFFLINE OfflineViewModel update
        // -------------------------------
        if (offlineViewModel != null && source == offlineViewModel) {

            // 2a. Show pretty status string
            offlineStatusLabel.setText(offlineViewModel.getStatusMessage());

            // 2b. Show timestamp in Last Update area
            if (offlineViewModel.getTimestamp() != null) {
                lastUpdatedLabel.setText(
                        "Last update: " + offlineViewModel.getTimestamp()
                                .atZone(java.time.ZoneId.systemDefault())
                                .format(LAST_UPDATED_FMT)
                );
            }
        }
    }


    // --- Dependency Setters ---
    public void setConvertController(ConvertController c) { this.convertController = c; }
    public void setFavouriteCurrencyController(FavouriteCurrencyController c) { this.favouriteCurrencyController = c; }
    public void setRecentCurrencyController(RecentCurrencyController c) { this.recentCurrencyController = c; }
    public void setCompareCurrenciesController(CompareCurrenciesController c) { this.compareCurrenciesController = c; }

    public void setRecentCurrencyDAO(RecentCurrencyDataAccessInterface dao) { this.recentDAO = dao; updateCurrencyDropdown(); }
    public void setRecentCurrencyViewModel(RecentCurrencyViewModel vm) { this.recentCurrencyViewModel = vm; vm.addPropertyChangeListener(e -> updateCurrencyDropdown()); }
    public void setFavouriteCurrencyViewModel(FavouriteCurrencyViewModel vm) { this.favouriteCurrencyViewModel = vm; vm.addPropertyChangeListener(e -> updateCurrencyDropdown()); }

    // --- Offline Viewing dependency setters ---
    public void setOfflineViewModel(OfflineViewModel vm) {
        this.offlineViewModel = vm;
        if (vm != null) {
            vm.addPropertyChangeListener(this);
        }
    }

    public void setOfflineViewController(OfflineViewController controller) {
        this.offlineViewController = controller;
    }

    private void updateCurrencyDropdown() {
        java.util.List<String> ordered = null;

        // Save current selections so we can restore them later
        Object currentFrom = fromBox.getSelectedItem();
        Object currentTo = toBox.getSelectedItem();

        // 1. get recent/frequent ordering from DAO
        if (recentDAO != null && homeViewModel != null && homeViewModel.getState() != null) {
            String userId = homeViewModel.getState().getUsername();
            if (userId != null && !userId.isEmpty()) ordered = recentDAO.getOrderedCurrenciesForUser(userId);
        }
        if ((ordered == null || ordered.isEmpty()) && baseCurrencies != null) ordered = baseCurrencies;

        if (ordered == null) return;

        fromBox.removeAllItems();
        toBox.removeAllItems();
        for (String code : ordered) {
            fromBox.addItem(code);
            toBox.addItem(code);
        }

        if (currentFrom != null) fromBox.setSelectedItem(currentFrom);
        if (currentTo != null) toBox.setSelectedItem(currentTo);
    }

    // --- Helper UI Generators ---
    private void addLabel(JPanel panel, String text, int x, int y, GridBagConstraints gbc) {
        gbc.gridx = x; gbc.gridy = y; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        panel.add(label, gbc);
    }
    private void addComponent(JPanel panel, JComponent comp, int x, int y, GridBagConstraints gbc, boolean grow) {
        gbc.gridx = x; gbc.gridy = y; gbc.weightx = grow ? 1.0 : 0; gbc.fill = grow ? GridBagConstraints.HORIZONTAL : GridBagConstraints.NONE;
        panel.add(comp, gbc);
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    @Override public void actionPerformed(ActionEvent e) {}
    public String getViewName() { return viewName; }
}