package ui;

import javax.swing.*;

public class ConverterScreen extends JPanel {

    private final MainFrame parent;

    public ConverterScreen(MainFrame parent, String[] currencyNames) {
        this.parent = parent;
        setLayout(null);
        setBackground(new java.awt.Color(150, 150, 245));

        JLabel title = new JLabel("Currency Converter");
        title.setBounds(170, 20, 275, 25);
        add(title);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setForeground(new java.awt.Color(30, 30, 30));
        title.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 22));

        JLabel fromLabel = new JLabel("From:");
        fromLabel.setBounds(40, 70, 50, 30);
        add(fromLabel);

        JComboBox<String> fromBox = new JComboBox<>(currencyNames);
        fromBox.setBounds(90, 70, 210, 40);
        fromBox.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 14));
        add(fromBox);

        JButton favFrom = new JButton("❤️");
        favFrom.setBounds(310, 70, 50, 40);
        favFrom.setBackground(new java.awt.Color(255, 220, 220));
        add(favFrom);

        JLabel toLabel = new JLabel("To:");
        toLabel.setBounds(280, 70, 30, 30);
        add(toLabel);

        JComboBox<String> toBox = new JComboBox<>(currencyNames);
        toBox.setBounds(320, 70, 210, 40);
        toBox.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 14));
        add(toBox);

        JButton favTo = new JButton("❤️");
        favTo.setBounds(540, 70, 50, 40);
        favTo.setBackground(new java.awt.Color(255, 220, 220));
        add(favTo);

        JButton historicalBtn = new JButton("Historical Trends");
        historicalBtn.setBounds(80, 200, 160, 50);
        historicalBtn.setBackground(new java.awt.Color(200, 230, 255));
        historicalBtn.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 15));
        add(historicalBtn);

        JButton convertBtn = new JButton("Convert");
        convertBtn.setBounds(260, 200, 160, 50);
        convertBtn.setBackground(new java.awt.Color(180, 255, 200));
        convertBtn.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 15));
        add(convertBtn);

        JButton compareBtn = new JButton("Compare Multiple");
        compareBtn.setBounds(440, 200, 180, 50);
        compareBtn.setBackground(new java.awt.Color(255, 240, 200));
        compareBtn.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 14));
        add(compareBtn);

        historicalBtn.addActionListener(e -> parent.showScreen(MainFrame.SCREEN_HISTORY));
        convertBtn.addActionListener(e -> parent.showScreen(MainFrame.SCREEN_RESULT));
        compareBtn.addActionListener(e -> parent.showScreen(MainFrame.SCREEN_COMPARE));
    }
}
