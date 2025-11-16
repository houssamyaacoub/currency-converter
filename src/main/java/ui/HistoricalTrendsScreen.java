package ui;

import javax.swing.*;
import java.awt.*;

public class HistoricalTrendsScreen extends JPanel {

    private final MainFrame parent;

    public HistoricalTrendsScreen(MainFrame parent) {
        this.parent = parent;
        setLayout(null);

        JLabel title = new JLabel("Currency Converter");
        title.setBounds(200, 10, 200, 25);
        add(title);

        JLabel timeLabel = new JLabel("Time Range :");
        timeLabel.setBounds(100, 60, 200, 25);
        add(timeLabel);

        JComboBox<String> timeRange = new JComboBox<>(new String[]{
                "1 Week", "1 Month", "3 Months", "6 Months", "1 Year"
        });
        timeRange.setBounds(190, 58, 200, 35);
        add(timeRange);
        // this is the time range i thought of

        JPanel chartPanel = new JPanel();
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        chartPanel.setBounds(100, 120, 320, 250);
        add(chartPanel);
        // this is where we have to put the graph.

        JButton backBtn = new JButton("BACK");
        backBtn.setBounds(200, 390, 100, 40);
        add(backBtn);

        backBtn.addActionListener(e -> parent.showScreen(MainFrame.SCREEN_CONVERTER));
    }
}
