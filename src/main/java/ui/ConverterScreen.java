package ui;

import javax.swing.*;

public class ConverterScreen extends JPanel {

    private final MainFrame parent;

    public ConverterScreen(MainFrame parent) {
        this.parent = parent;
        setLayout(null);

        JLabel title = new JLabel("Currency Converter");
        title.setBounds(200, 20, 200, 25);
        add(title);

        JLabel fromLabel = new JLabel("From:");
        fromLabel.setBounds(40, 70, 50, 30);
        add(fromLabel);

        JComboBox<String> fromBox = new JComboBox<>(new String[]{"CAD", "USD", "EUR", "JPY"});
        fromBox.setBounds(90, 70, 120, 30);
        add(fromBox);
        // we have to put more currencies but this four is just example that i putted in


        JButton favFrom = new JButton("❤️");
        favFrom.setBounds(215, 70, 50, 30);
        add(favFrom);
        // Im not sure how we are going to make this part happen but putted on the ui

        JLabel toLabel = new JLabel("To:");
        toLabel.setBounds(280, 70, 30, 30);
        add(toLabel);

        JComboBox<String> toBox = new JComboBox<>(new String[]{"CAD", "USD", "EUR", "JPY"});
        toBox.setBounds(310, 70, 120, 30);
        add(toBox);

        JButton favTo = new JButton("❤️");
        favTo.setBounds(435, 70, 50, 30);
        add(favTo);

        JButton historicalBtn = new JButton("Historical Trends");
        historicalBtn.setBounds(130, 200, 130, 40);
        add(historicalBtn);

        JButton convertBtn = new JButton("Convert");
        convertBtn.setBounds(280, 200, 130, 40);
        add(convertBtn);

        historicalBtn.addActionListener(e -> parent.showScreen(MainFrame.SCREEN_HISTORY));
        convertBtn.addActionListener(e -> parent.showScreen(MainFrame.SCREEN_RESULT));
    }
}
