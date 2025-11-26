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

public class TrendsView extends JPanel implements ActionListener, PropertyChangeListener {
    public final String viewName = "trends";
    private final TrendsViewModel trendsViewModel;
    private TrendsController trendsController;

    private final JPanel chartContainer;
    private final JPanel buttonContainer;
    private final JPanel currencyContainer;

    private final JButton backBtn;
    private final JButton graphBtn;
    String[] currencies = {"Turkish Lira", "Lebanese Pound", "United States Dollar"};
    String[] timePeriods = {"1 week", "1 month", "6 months", "1 year"};

    public TrendsView(TrendsViewModel trendsViewModel) {
        this.trendsViewModel = trendsViewModel;
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
        gbc.gridwidth = 4;
        gbc.insets = new Insets(4, 4, 8, 4);
        currencyContainer.add(title, gbc);

        JComboBox<String> fromBox = new JComboBox<>(currencies);
        JList<String> toList = new JList<>(currencies);
        toList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        toList.setVisibleRowCount(3);
        JScrollPane toScroll = new JScrollPane(toList);
        JComboBox<String> timePeriodBox = new JComboBox<>(timePeriods);

        gbc.insets = new Insets(2, 4, 2, 4);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        currencyContainer.add(new JLabel("From"), gbc);
        gbc.gridx = 1;
        currencyContainer.add(fromBox, gbc);

        gbc.gridx = 2;
        currencyContainer.add(new JLabel("To (select 1–5)"), gbc);
        gbc.gridx = 3;
        currencyContainer.add(toScroll, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        currencyContainer.add(timePeriodBox, gbc);

        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridx = 1;
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
                    }
                }
        );
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

    public String getViewName() {
        return this.viewName;
    }
}
