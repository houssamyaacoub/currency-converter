package view;

import interface_adapter.historic_trends.TrendsState;
import interface_adapter.historic_trends.TrendsViewModel;
import interface_adapter.historic_trends.TrendsController; // To go back

import interface_adapter.logged_in.HomeState;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
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
import java.time.LocalDate;

public class TrendsView extends JPanel implements ActionListener, PropertyChangeListener {
    public final String viewName = "trends";
    private final TrendsViewModel trendsViewModel;
    private TrendsController trendsController; // Use this to go BACK to home

    private final JPanel chartContainer;
    private final JButton backBtn;

    public TrendsView(TrendsViewModel trendsViewModel) {
        this.trendsViewModel = trendsViewModel;
        this.trendsViewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());

        JLabel title = new JLabel("Historical Exchange Rates");
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        add(title, BorderLayout.NORTH);

        // 1. Create a container for the chart
        chartContainer = new JPanel();
        chartContainer.setLayout(new BorderLayout());

        // 2. Add dummy chart immediately so we can see it
        ChartPanel chartPanel = makeChartPanel("USD", "CAD");
        chartContainer.add(chartPanel, BorderLayout.CENTER);

        add(chartContainer, BorderLayout.CENTER);

        // 3. Buttons
        JPanel buttons = new JPanel();
        backBtn = new JButton("Back to Converter");
        buttons.add(backBtn);
        add(buttons, BorderLayout.SOUTH);

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
    }

    /**
     * Helper to create the Chart with SAMPLE DATA
     */
    private ChartPanel makeChartPanel(String base, String target) {
        // 1. Create the dataset (Sample Data)
        TimeSeries series = new TimeSeries("Exchange Rate");

        // Adding dummy data points (Last 7 days)
        // Note: In real code, this comes from trendsViewModel.getState().getDates/Rates()
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
                base + " vs " + target, // Title
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

        return new ChartPanel(chart);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // When real data comes in via the State, we would:
        // 1. Clear chartContainer
        // 2. Create new ChartPanel with state data
        // 3. chartContainer.add(newChartPanel)
        // 4. revalidate()
    }

    public void setTrendsController(TrendsController controller) {
        this.trendsController = controller;
    }

    public String getViewName() {
        return this.viewName;
    }
}