package view;

import interface_adapter.historic_trends.TrendsController;
import interface_adapter.historic_trends.TrendsState;
import interface_adapter.historic_trends.TrendsViewModel;
import interface_adapter.logged_in.HomeViewModel;
import interface_adapter.favourite_currency.FavouriteCurrencyController;
import interface_adapter.favourite_currency.FavouriteCurrencyViewModel;
import interface_adapter.recent_currency.RecentCurrencyController;
import interface_adapter.recent_currency.RecentCurrencyViewModel;
import interface_adapter.load_currencies.LoadCurrenciesController;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import use_case.recent_currency.RecentCurrencyDataAccessInterface;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
 * TrendsView
 * <p>
 * Represents the User Interface for the Historical Exchange Rates feature.
 * This class observes the {@link TrendsViewModel} for state changes and delegates
 * user actions to the {@link TrendsController}.
 */
public class TrendsView extends JPanel implements ActionListener, PropertyChangeListener {

    public final String viewName = "trends";

    // --- UI Constants ---
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color PANEL_COLOR = Color.WHITE;
    private static final Color PRIMARY_BTN_COLOR = new Color(59, 130, 246);
    private static final Color TEXT_COLOR = new Color(31, 41, 55);

    // --- Architecture Components ---
    private final TrendsViewModel trendsViewModel;
    private final HomeViewModel homeViewModel;
    private TrendsController trendsController;

    private FavouriteCurrencyController favouriteCurrencyController;
    private FavouriteCurrencyViewModel favouriteCurrencyViewModel;

    private RecentCurrencyController recentCurrencyController;
    private RecentCurrencyViewModel recentCurrencyViewModel;

    // --- UI Components ---
    private final JPanel chartContainer;
    private final JComboBox<String> fromBox;
    private final JComboBox<String> toBox;
    private final JComboBox<String> timePeriodBox;

    private final JButton graphBtn;
    private final JButton backBtn;
    private final JButton favouriteFromBtn;
    private final JButton favouriteToBtn;

    // --- Data ---
    private final String[] timePeriods = {"1 week", "1 month", "6 months", "1 year"};

    /**
     * Constructs the TrendsView.
     */
    private LoadCurrenciesController loadCurrenciesController;

    public TrendsView(TrendsViewModel trendsViewModel, HomeViewModel homeViewModel) {

        this.trendsViewModel = trendsViewModel;
        this.homeViewModel = homeViewModel;
        this.trendsViewModel.addPropertyChangeListener(this);

        // Initialize Components
        fromBox = new JComboBox<>();
        toBox = new JComboBox<>();
        timePeriodBox = new JComboBox<>(timePeriods);

        graphBtn = createStyledButton("Graph", PRIMARY_BTN_COLOR, Color.WHITE);
        backBtn = new JButton("Back to Hub");

        favouriteFromBtn = new JButton("★");
        favouriteFromBtn.setMargin(new Insets(2, 6, 2, 6));
        favouriteFromBtn.setToolTipText("Add FROM currency to favourites");

        favouriteToBtn = new JButton("★");
        favouriteToBtn.setMargin(new Insets(2, 6, 2, 6));
        favouriteToBtn.setToolTipText("Add TO currency to favourites");

        chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBackground(PANEL_COLOR);
        chartContainer.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235)));

        initializeUI();
        setupListeners();
        updateCurrencyDropdown();
    }

    /**
     * Sets up the visual layout.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // 1. Header
        JLabel title = new JLabel("Historical Exchange Rates");
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(TEXT_COLOR);
        title.setBorder(new EmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // 2. Center Panel (Controls + Chart)
        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setBackground(BACKGROUND_COLOR);
        centerPanel.setBorder(new EmptyBorder(0, 20, 0, 20));

        centerPanel.add(createControlPanel(), BorderLayout.NORTH);

        // Generate dummy data for initial view
        ArrayList<LocalDate> dummyDates = new ArrayList<>();
        ArrayList<Double> dummyRates = new ArrayList<>();
        LocalDate today = LocalDate.now();
        double dummyRate = 1.35;

        // Generate 30 days of random data
        for (int i = 30; i >= 0; i--) {
            dummyDates.add(today.minusDays(i));
            dummyRate += (Math.random() - 0.5) * 0.05; // Random fluctuation
            dummyRates.add(dummyRate);
        }

        TrendsState dummyState = new TrendsState();
        dummyState.setBaseCurrency("Example Base");

        java.util.ArrayList<TrendsState.SeriesData> series = new java.util.ArrayList<>();
        series.add(new TrendsState.SeriesData("Example Target", dummyDates, dummyRates));

        dummyState.setSeriesList(series);

        ChartPanel initialChart = makeChartPanel(dummyState);
        chartContainer.add(initialChart, BorderLayout.CENTER);
        // --------------------------------------------------------

        centerPanel.add(chartContainer, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // 3. Bottom Navigation
        JPanel buttonContainer = new JPanel();
        buttonContainer.setBackground(BACKGROUND_COLOR);
        buttonContainer.setBorder(new EmptyBorder(15, 0, 15, 0));

        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backBtn.setFocusPainted(false);
        buttonContainer.add(backBtn);

        add(buttonContainer, BorderLayout.SOUTH);
    }

    /**
     * Creates the control panel for user inputs.
     */
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        addLabel(panel, "From Currency:", 0, 0, gbc);
        addComponent(panel, fromBox, 1, 0, gbc, true);
        addComponent(panel, favouriteFromBtn, 2, 0, gbc, false);

        addLabel(panel, "To Currency:", 0, 1, gbc);
        addComponent(panel, toBox, 1, 1, gbc, true);
        addComponent(panel, favouriteToBtn, 2, 1, gbc, false);

        addLabel(panel, "Time Period:", 0, 2, gbc);
        addComponent(panel, timePeriodBox, 1, 2, gbc, true);

        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(graphBtn, gbc);

        return panel;
    }

    /**
     * Configures all action listeners.
     */
    private void setupListeners() {

        // Back Button
        backBtn.addActionListener(evt -> {
            if (trendsController != null) trendsController.switchToHome();
        });

        // Graph Button
        graphBtn.addActionListener(evt -> {
            String from = String.valueOf(fromBox.getSelectedItem());
            String to = String.valueOf(toBox.getSelectedItem());
            String period = String.valueOf(timePeriodBox.getSelectedItem());

            if (from != null && to != null) {
                if (from.equals(to)) {
                    JOptionPane.showMessageDialog(this, "Please select two different currencies.", "Input Error", JOptionPane.WARNING_MESSAGE);
                } else {
                    trendsController.execute(from, to, period);
                }
            }
        });

        favouriteFromBtn.addActionListener(e -> handleFavouriteAction(fromBox.getSelectedItem()));
        favouriteToBtn.addActionListener(e -> handleFavouriteAction(toBox.getSelectedItem()));

        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                if (fromBox.getItemCount() == 0 && loadCurrenciesController != null) {
                    loadCurrenciesController.execute();
                } else {
                    // Ensure dropdown is consistent if we switch back and forth
                    updateCurrencyDropdown();
                }
            }
        });
    }

    private void handleFavouriteAction(Object selectedItem) {
        if (favouriteCurrencyController == null || homeViewModel == null || homeViewModel.getState() == null) return;

        String userId = homeViewModel.getState().getUsername();
        if (userId == null || userId.isEmpty() || selectedItem == null) return;

        favouriteCurrencyController.execute(userId, selectedItem.toString(), true);
    }

    /**
     * Creates a chart panel based on the data in the TrendsState.
     * @param state The state containing the list of series data.
     * @return A ChartPanel ready to be displayed.
     */
    private ChartPanel makeChartPanel(TrendsState state) {
        TimeSeriesCollection dataset = new TimeSeriesCollection();

        // Check if there is data to plot
        if (state.getSeriesList() != null) {
            // Iterate over the SeriesData objects stored in the state
            for (TrendsState.SeriesData seriesData : state.getSeriesList()) {
                TimeSeries series = new TimeSeries(seriesData.getTargetCurrency());

                java.util.ArrayList<LocalDate> dates = seriesData.getDates();
                java.util.ArrayList<Double> values = seriesData.getPercents();

                // Populate the series
                for (int i = 0; i < dates.size(); i++) {
                    LocalDate d = dates.get(i);
                    Double v = values.get(i);
                    Day day = new Day(d.getDayOfMonth(), d.getMonthValue(), d.getYear());
                    series.addOrUpdate(day, v);
                }
                dataset.addSeries(series);
            }
        }

        // Create the chart
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                state.getBaseCurrency() + " vs targets (% change)", // Title
                "Date",               // X-Axis Label
                "Percent change (%)", // Y-Axis Label
                dataset,              // Dataset
                true,                 // Show Legend
                true,                 // Show Tooltips
                false                 // No URLs
        );

        // Styling (Optional)
        XYPlot plot = (XYPlot) chart.getPlot();
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("dd-MMM"));
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        return new ChartPanel(chart);
    }

    /**
     * Updates dropdowns based on ViewModel data using Iterator Pattern.
     */
    private void updateCurrencyDropdown() {
        // CLEAN ARCHITECTURE: Read from ViewModel
        String[] codes = trendsViewModel.getState().getCurrencyCodes();

        // Save current selections
        Object currentFrom = fromBox.getSelectedItem();
        Object currentTo = toBox.getSelectedItem();

        if (codes == null || codes.length == 0) return;

        fromBox.removeAllItems();
        toBox.removeAllItems();

        // ITERATOR PATTERN implementation
        java.util.List<String> codeList = java.util.Arrays.asList(codes);
        Iterator<String> iterator = codeList.iterator();

        while (iterator.hasNext()) {
            String code = iterator.next();
            fromBox.addItem(code);
            toBox.addItem(code);
        }

        // Restore selections if possible
        if (currentFrom != null) fromBox.setSelectedItem(currentFrom);
        if (currentTo != null) toBox.setSelectedItem(currentTo);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("currencyListLoaded".equals(evt.getPropertyName())) {
            updateCurrencyDropdown();
        } else if ("state".equals(evt.getPropertyName())) { // Trends updated
            TrendsState state = trendsViewModel.getState();
            chartContainer.removeAll();

            if (state.getSeriesList() != null && !state.getSeriesList().isEmpty()) {
                ChartPanel newChart = makeChartPanel(state);
                chartContainer.add(newChart, BorderLayout.CENTER);
            } else if (state.getError() != null) {
                chartContainer.add(new JLabel("Error: " + state.getError(), SwingConstants.CENTER), BorderLayout.CENTER);
            } else {
                chartContainer.add(new JLabel("No Data Available", SwingConstants.CENTER), BorderLayout.CENTER);
            }
            chartContainer.revalidate();
            chartContainer.repaint();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {}

    // --- Dependency Injection ---
    public void setLoadCurrenciesController(LoadCurrenciesController controller) {this.loadCurrenciesController = controller;}
    public void setTrendsController(TrendsController controller) { this.trendsController = controller; }
    public void setFavouriteCurrencyController(FavouriteCurrencyController controller) { this.favouriteCurrencyController = controller; }
    public void setFavouriteCurrencyViewModel(FavouriteCurrencyViewModel vm) {
        this.favouriteCurrencyViewModel = vm;
        if (this.favouriteCurrencyViewModel != null) this.favouriteCurrencyViewModel.addPropertyChangeListener(evt -> updateCurrencyDropdown());
    }
    public void setRecentCurrencyController(RecentCurrencyController controller) { this.recentCurrencyController = controller; }
    public void setRecentCurrencyViewModel(RecentCurrencyViewModel vm) {
        this.recentCurrencyViewModel = vm;
        if (this.recentCurrencyViewModel != null) this.recentCurrencyViewModel.addPropertyChangeListener(evt -> updateCurrencyDropdown());
    }

    public String getViewName() { return this.viewName; }

    // --- Helper Methods ---
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
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

}