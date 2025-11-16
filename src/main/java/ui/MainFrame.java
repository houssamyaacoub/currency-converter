package ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    public static final String SCREEN_CONVERTER = "converter";
    public static final String SCREEN_HISTORY = "history";
    public static final String SCREEN_RESULT = "result";

    public MainFrame() {
        super("Currency Tool");

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(new ConverterScreen(this), SCREEN_CONVERTER);
        mainPanel.add(new HistoricalTrendsScreen(this), SCREEN_HISTORY);
        mainPanel.add(new ResultScreen(this), SCREEN_RESULT);

        add(mainPanel);

        setSize(520, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        showScreen(SCREEN_CONVERTER);
    }

    public void showScreen(String name) {
        cardLayout.show(mainPanel, name);
    }
}
