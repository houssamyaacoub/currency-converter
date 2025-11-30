package view;

import interface_adapter.historic_trends.TrendsState;
import interface_adapter.historic_trends.TrendsViewModel;
import interface_adapter.historic_trends.TrendsController;
import use_case.historic_trends.TrendsOutputData;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;

// NEW: imports for home / favourite / recent
import interface_adapter.logged_in.HomeViewModel;
import interface_adapter.favourite_currency.FavouriteCurrencyController;
import interface_adapter.favourite_currency.FavouriteCurrencyViewModel;
import interface_adapter.recent_currency.RecentCurrencyController;
import interface_adapter.recent_currency.RecentCurrencyViewModel;
import use_case.recent_currency.RecentCurrencyDataAccessInterface;

public class TrendsView extends JPanel implements ActionListener, PropertyChangeListener {
    public final String viewName = "trends";
    private final TrendsViewModel trendsViewModel;
    private TrendsController trendsController;

    private final JPanel chartContainer;
    private final JPanel buttonContainer;
    private final JPanel currencyContainer;

    private final JButton backBtn;
    private final JButton graphBtn;

    // NEW: full currency list passed from AppBuilder (same as ConvertView)
    private final java.util.List<String> baseCurrencies;

    private final String[] timePeriods = {"1 week", "1 month", "6 months", "1 year"};

    // NEW: store the combo boxes and list as fields (not local variables)
    private final JComboBox<String> fromBox;
    private final JList<String> toList;
    private final JComboBox<String> timePeriodBox;

    // NEW: star buttons to allow adding favourites from this view
    private final JButton favouriteFromBtn;
    private final JButton favouriteToBtn;

    // NEW: references needed to reuse favourites / recent logic
    private final HomeViewModel homeViewModel;

    private FavouriteCurrencyController favouriteCurrencyController;
    private FavouriteCurrencyViewModel favouriteCurrencyViewModel;

    private RecentCurrencyController recentCurrencyController;
    private RecentCurrencyViewModel recentCurrencyViewModel;
    private RecentCurrencyDataAccessInterface recentDAO;

    // NEW: constructor now also receives HomeViewModel and baseCurrencies
    public TrendsView(TrendsViewModel trendsViewModel,
                      HomeViewModel homeViewModel,
                      java.util.List<String> baseCurrencies) {
        this.trendsViewModel = trendsViewModel;
        this.homeViewModel = homeViewModel;
        this.baseCurrencies = baseCurrencies; // store full list for fallback
        this.trendsViewModel.addPropertyChangeListener(this);
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (Exception e) {
            System.out.println("L&F not found");
        }

        setLayout(new BorderLayout());

        currencyContainer = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel title = new JLabel("Historical Exchange Rates");
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3; // 5 columns now (extra for star buttons)
        gbc.insets = new Insets(4, 4, 8, 4);
        currencyContainer.add(title, gbc);

        // NEW: create fields instead of local variables so we can update them later
        fromBox = new JComboBox<>();
        toList = new JList<>();
        toList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        toList.setVisibleRowCount(5); // show up to 5 rows before scroll
        JScrollPane toScroll = new JScrollPane(toList);
        timePeriodBox = new JComboBox<>(timePeriods);

        // NEW: favourite buttons (same style as in ConvertView)
        favouriteFromBtn = new JButton("★");
        favouriteFromBtn.setMargin(new Insets(2, 6, 2, 6));
        favouriteFromBtn.setToolTipText("Add FROM currency to favourites");

        favouriteToBtn = new JButton("★");
        favouriteToBtn.setMargin(new Insets(2, 6, 2, 6));
        favouriteToBtn.setToolTipText("Add selected TO currencies to favourites");

        gbc.insets = new Insets(2, 4, 2, 4);

        // Row 1: "From" + combo box + star button
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;

        gbc.gridx = 0;
        currencyContainer.add(new JLabel("From"), gbc);

        gbc.gridx = 1;
        currencyContainer.add(fromBox, gbc);

        gbc.gridx = 2;
        currencyContainer.add(favouriteFromBtn, gbc);

        // Row 2: "To" label + scrollable list + star button
        gbc.gridy = 2;

        gbc.gridx = 0;
        currencyContainer.add(new JLabel("To (select 1–5)"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;   // allow the list to grow
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        currencyContainer.add(toScroll, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        currencyContainer.add(favouriteToBtn, gbc);

        // Row 3: "Time period" + combo box + Graph button
        gbc.gridy = 3;

        gbc.gridx = 0;
        currencyContainer.add(new JLabel("Time period"), gbc);

        gbc.gridx = 1;
        currencyContainer.add(timePeriodBox, gbc);

        gbc.gridx = 2;
        graphBtn = new JButton("Graph");
        graphBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        graphBtn.setBackground(new Color(200, 100, 100));
        currencyContainer.add(graphBtn, gbc);

        add(currencyContainer, BorderLayout.NORTH);

        chartContainer = new JPanel(new BorderLayout());
        chartContainer.add(new JLabel("No Data Available", SwingConstants.CENTER), BorderLayout.CENTER);
        add(chartContainer, BorderLayout.CENTER);

        buttonContainer = new JPanel();
        backBtn = new JButton("Back to Hub");
        buttonContainer.add(backBtn);
        add(buttonContainer, BorderLayout.SOUTH);

        // Populate the dropdowns according to favourites / recent if possible
        updateCurrencyDropdown();

        backBtn.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        if (trendsController != null) {
                            trendsController.switchToHome();
                        }
                    }
                }
        );

        graphBtn.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        String from = (String) fromBox.getSelectedItem();
                        String period = (String) timePeriodBox.getSelectedItem();
                        List<String> selectedTargets = toList.getSelectedValuesList();

                        if (from == null || period == null || selectedTargets.isEmpty()) {
                            System.out.println("Select base, at least one target, and period");
                            return;
                        }

                        java.util.ArrayList<String> filtered = new java.util.ArrayList<>();
                        for (String t : selectedTargets) {
                            if (!from.equals(t)) {
                                filtered.add(t);
                            }
                        }
                        if (filtered.isEmpty()) {
                            System.out.println("Targets cannot all be the same as base");
                            return;
                        }

                        if (trendsController != null) {
                            trendsController.execute(from, filtered, period);
                        }

                        // Also record recent usage for the selected pairs
                        if (recentCurrencyController != null && homeViewModel != null
                                && homeViewModel.getState() != null) {

                            String userId = homeViewModel.getState().getUsername();
                            if (userId != null && !userId.isEmpty()) {
                                for (String target : filtered) {
                                    // Record each (from, target) pair as recently used
                                    recentCurrencyController.execute(userId, from, target);
                                }
                            }
                        }
                    }
                }
        );


        // Listener: favourite FROM currency
        favouriteFromBtn.addActionListener(e -> {
            // Guard clauses to avoid NullPointerExceptions
            if (favouriteCurrencyController == null || homeViewModel == null || homeViewModel.getState() == null) {
                return;
            }
            String userId = homeViewModel.getState().getUsername();
            Object selected = fromBox.getSelectedItem();
            if (userId == null || userId.isEmpty() || selected == null) {
                return;
            }
            String currencyCode = selected.toString();
            // Mark the base currency as favourite for this user
            favouriteCurrencyController.execute(userId, currencyCode, true);
        });

        // Listener: favourite TO currencies (can be multiple)
        favouriteToBtn.addActionListener(e -> {
            if (favouriteCurrencyController == null || homeViewModel == null || homeViewModel.getState() == null) {
                return;
            }
            String userId = homeViewModel.getState().getUsername();
            if (userId == null || userId.isEmpty()) {
                return;
            }
            List<String> selectedTargets = toList.getSelectedValuesList();
            if (selectedTargets == null || selectedTargets.isEmpty()) {
                return;
            }
            for (String currencyCode : selectedTargets) {
                // Mark each selected target as favourite for this user
                favouriteCurrencyController.execute(userId, currencyCode, true);
            }
        });

        // Ensure currencies are re-ordered whenever this view becomes visible.
        // At this point the user is already logged in and HomeViewModel
        // has the correct username, so favourites and recent usage can be applied.
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                updateCurrencyDropdown();
            }
        });

    }

    // Central place to update "from" and "to" according to favourites + recent
    private void updateCurrencyDropdown() {
        java.util.List<String> ordered = null;

        // Try to get ordered list from Recent DAO using current user id
        if (recentDAO != null && homeViewModel != null && homeViewModel.getState() != null) {
            String userId = homeViewModel.getState().getUsername();
            if (userId != null && !userId.isEmpty()) {
                // DAO is responsible for ordering favourites first, then recent, then others
                ordered = recentDAO.getOrderedCurrenciesForUser(userId);
            }
        }

        // Fallback: if no DAO or no user data, use the full baseCurrencies list
        if ((ordered == null || ordered.isEmpty()) && baseCurrencies != null && !baseCurrencies.isEmpty()) {
            ordered = new java.util.ArrayList<>(baseCurrencies);
        }

        if (ordered == null || ordered.isEmpty()) {
            return;
        }

        // Update "from" combo box
        fromBox.removeAllItems();
        for (String code : ordered) {
            fromBox.addItem(code);
        }

        // Update "to" list with the same ordering
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String code : ordered) {
            listModel.addElement(code);
        }
        toList.setModel(listModel);
    }

    private ChartPanel makeChartPanel(TrendsState state) {
        TimeSeriesCollection dataset = new TimeSeriesCollection();

        if (state.getSeriesList() != null) {
            for (TrendsOutputData.SeriesData seriesData : state.getSeriesList()) {
                TimeSeries series = new TimeSeries(seriesData.getTargetCurrency());
                java.util.ArrayList<LocalDate> dates = seriesData.getDates();
                java.util.ArrayList<Double> values = seriesData.getPercents();

                for (int i = 0; i < dates.size(); i++) {
                    LocalDate d = dates.get(i);
                    Double v = values.get(i);
                    Day day = new Day(d.getDayOfMonth(), d.getMonthValue(), d.getYear());
                    series.addOrUpdate(day, v);
                }

                dataset.addSeries(series);
            }
        }

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                state.getBaseCurrency() + " vs targets (% change)",
                "Date",
                "Percent change (%)",
                dataset,
                true,
                true,
                false
        );

        XYPlot plot = (XYPlot) chart.getPlot();
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("dd-MMM"));

        return new ChartPanel(chart);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        TrendsState state = trendsViewModel.getState();

        chartContainer.removeAll();

        if (state.getSeriesList() != null && !state.getSeriesList().isEmpty()) {
            ChartPanel newChart = makeChartPanel(state);
            chartContainer.add(newChart, BorderLayout.CENTER);
        } else {
            chartContainer.add(new JLabel("No Data Available", SwingConstants.CENTER), BorderLayout.CENTER);
        }

        chartContainer.revalidate();
        chartContainer.repaint();
    }

    public void setTrendsController(TrendsController controller) {
        this.trendsController = controller;
    }

    // Inject favourite currency use case into this view
    public void setFavouriteCurrencyController(FavouriteCurrencyController controller) {
        this.favouriteCurrencyController = controller;
    }

    public void setFavouriteCurrencyViewModel(FavouriteCurrencyViewModel vm) {
        this.favouriteCurrencyViewModel = vm;
        if (this.favouriteCurrencyViewModel != null) {
            // When favourites change, refresh the dropdown ordering
            this.favouriteCurrencyViewModel.addPropertyChangeListener(evt -> updateCurrencyDropdown());
        }
    }

    // Inject recent currency use case into this view
    public void setRecentCurrencyController(RecentCurrencyController controller) {
        this.recentCurrencyController = controller;
    }

    public void setRecentCurrencyViewModel(RecentCurrencyViewModel vm) {
        this.recentCurrencyViewModel = vm;
        if (this.recentCurrencyViewModel != null) {
            // When recent data changes, refresh the dropdown ordering
            this.recentCurrencyViewModel.addPropertyChangeListener(evt -> updateCurrencyDropdown());
        }
    }

    public void setRecentCurrencyDAO(RecentCurrencyDataAccessInterface dao) {
        this.recentDAO = dao;
        // Refresh immediately when DAO is first injected
        updateCurrencyDropdown();
    }

    public String getViewName() {
        return this.viewName;
    }
}
