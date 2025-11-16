package ui;

import javax.swing.*;

public class ResultScreen extends JPanel {

    private final MainFrame parent;

    public ResultScreen(MainFrame parent) {
        this.parent = parent;
        setLayout(null);

        JLabel enterLabel = new JLabel("Enter Amount:");
        enterLabel.setBounds(100, 40, 120, 30);
        add(enterLabel);

        JTextField amountField = new JTextField();
        amountField.setBounds(250, 40, 150, 30);
        add(amountField);

        JLabel resultLabel = new JLabel("CAD =             USD");
        // this is just example and we need to make this change every time
        // depending on the amount and the currency
        resultLabel.setOpaque(true);
        resultLabel.setBounds(200, 150, 250, 40);
        add(resultLabel);

        JButton backBtn = new JButton("BACK");
        backBtn.setBounds(150, 300, 100, 40);
        add(backBtn);

        backBtn.addActionListener(e -> parent.showScreen(MainFrame.SCREEN_CONVERTER));
    }
}
