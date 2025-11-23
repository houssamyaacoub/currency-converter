package view;

import interface_adapter.historic_trends.TrendsState;
import interface_adapter.historic_trends.TrendsViewModel;
import interface_adapter.historic_trends.TrendsController; // To go back

import interface_adapter.logged_in.HomeState;
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
import java.util.ArrayList;

public class TrendsView extends JPanel implements ActionListener, PropertyChangeListener {
    public final String viewName = "trends";
    private final TrendsViewModel trendsViewModel;
    private TrendsController trendsController; // Use this to go BACK to home

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
        } catch(Exception e){
            System.out.println("L&F not found");
        }

        setLayout(new BorderLayout());

        JLabel title = new JLabel("Historical Exchange Rates");
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        add(title, BorderLayout.NORTH);

        //Create currency container
        currencyContainer = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JComboBox<String> fromBox = new JComboBox<>(currencies);
        JComboBox<String> toBox = new JComboBox<>(currencies);
        JComboBox<String> timePeriodBox = new JComboBox<>(timePeriods);

        gbc.gridx = 0; gbc.gridy = 0;
        currencyContainer.add(new JLabel("From"), gbc);
        gbc.gridx = 1;
        currencyContainer.add(fromBox, gbc);
        gbc.gridx = 2;
        currencyContainer.add(new JLabel("To"), gbc);
        gbc.gridx = 3;
        currencyContainer.add(toBox, gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 3;
        currencyContainer.add(timePeriodBox, gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        graphBtn = new JButton("Graph");
        graphBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        graphBtn.setBackground(new Color(200, 100, 100));
        graphBtn.addActionListener(this);
        currencyContainer.add(graphBtn, gbc);
        add(currencyContainer, BorderLayout.NORTH);


        // 1. Create a container for the chart
        chartContainer = new JPanel(new BorderLayout());

        // 2. Add dummy chart immediately so we can see it
        TimeSeries series = new TimeSeries("Exchange Rate");

        // Adding dummy data points (I'll refactor this later to clean this up)
        Day current = new Day();
        double dummyRate = 1.35;
        for (int i = 0; i < 30; i++) {
            series.add(current, dummyRate);
            current = (Day) current.next(); // move to next day
            dummyRate += (Math.random() - 0.5) * 0.05; // Random
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(series);

        // 2. Create the chart
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "base" + " vs " + "target", // Title
                "Date",                 // X-Axis Label
                "Rate",                 // Y-Axis Label
                dataset,                // Data
                true,                   // Show Legend
                true,
                false
        );

        // 3. Style the plot (Optional)
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(new Color(240, 240, 240));

        chartContainer.add(new ChartPanel(chart), BorderLayout.CENTER);

        add(chartContainer, BorderLayout.CENTER);

        // 3. Buttons
        buttonContainer = new JPanel();
        backBtn = new JButton("Back to Hub");
        buttonContainer.add(backBtn);
        add(buttonContainer, BorderLayout.SOUTH);



        // Listener to go back
        backBtn.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        // HomeController can handle switching.
                        System.out.println("Go Back clicked");
                        trendsController.switchToHome();

                    }
                }
        );
        graphBtn.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        String from = (String) fromBox.getSelectedItem();
                        String to = (String) toBox.getSelectedItem();
                        String period = (String) timePeriodBox.getSelectedItem();

                        assert from != null;
                        if(!from.equals(to)) {
                            trendsController.execute(from, to, period);
                        }else{
                            System.out.println("Can't have same currencies!");
                        }
                    }
                }
        );


    }


    private ChartPanel makeChartPanel(String base, String target,
                                      ArrayList<LocalDate> dates, ArrayList<Double> rates) {

        TimeSeries series = new TimeSeries(base + "/" + target);

        // Populates with api data
        if (dates != null && rates != null) {
            for (int i = 0; i < dates.size(); i++) {
                LocalDate d = dates.get(i);
                Double r = rates.get(i);

                // JFreeChart Day object
                Day day = new Day(d.getDayOfMonth(), d.getMonthValue(), d.getYear());
                series.addOrUpdate(day, r);
            }
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(series);

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Exchange Rate: " + base + " to " + target,
                "Date",
                "Rate",
                dataset,
                true, true, false
        );
        XYPlot plot = (XYPlot) chart.getPlot();
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("MMM-dd"));

        return new ChartPanel(chart);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("state")) {
            TrendsState state = (TrendsState) evt.getNewValue();

            // 1. Clear the old chart
            chartContainer.removeAll();

            // 2. Check if we have data to plot
            if (state.getDates() != null && !state.getDates().isEmpty()) {
                // 3. Create NEW chart with NEW data
                ChartPanel newChart = makeChartPanel(
                        state.getBaseCurrency(),
                        state.getTargetCurrency(),
                        state.getDates(),
                        state.getRates()
                );
                chartContainer.add(newChart, BorderLayout.CENTER);
            } else {
                chartContainer.add(new JLabel("No Data Available"), BorderLayout.CENTER);
            }

            // 4. Refresh the UI
            chartContainer.revalidate();
            chartContainer.repaint();
        }
    }

    public void setTrendsController(TrendsController controller) {
        this.trendsController = controller;
    }

    public String getViewName() {
        return this.viewName;
    }
}