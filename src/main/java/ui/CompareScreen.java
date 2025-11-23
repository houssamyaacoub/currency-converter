package ui;

import interface_adapter.convert_currency.ConvertMultipleController;
import interface_adapter.convert_currency.ConvertMultipleState;
import interface_adapter.convert_currency.ConvertMultipleViewModel;
import use_case.convert_multiple.ConvertMultipleOutputData;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class CompareScreen extends JPanel implements PropertyChangeListener {

    private final MainFrame parent;
    private final ConvertMultipleViewModel viewModel;
    private final ConvertMultipleController controller;

    private final JComboBox<String> baseBox;
    private final JList<String> targetList;
    private final JTextField amountField;
    private final JLabel errorLabel;
    private final ComparisonChartPanel chartPanel;

    public CompareScreen(MainFrame parent,
                         String[] currencyNames,
                         ConvertMultipleViewModel viewModel,
                         ConvertMultipleController controller) {
        this.parent = parent;
        this.viewModel = viewModel;
        this.controller = controller;

        this.viewModel.addPropertyChangeListener(this);

        setLayout(null);
        setBackground(new java.awt.Color(210, 230, 255));

        JLabel title = new JLabel("Compare Multiple Currencies");
        title.setBounds(80, 20, 360, 30);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(title);

        JLabel baseLabel = new JLabel("Base Currency:");
        baseLabel.setBounds(40, 70, 120, 25);
        add(baseLabel);

        baseBox = new JComboBox<>(currencyNames);
        baseBox.setBounds(160, 70, 240, 30);
        add(baseBox);

        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setBounds(40, 110, 120, 25);
        add(amountLabel);

        amountField = new JTextField("1.00");
        amountField.setBounds(160, 110, 100, 30);
        add(amountField);

        JLabel targetLabel = new JLabel("Select up to 5 targets:");
        targetLabel.setBounds(40, 150, 200, 25);
        add(targetLabel);

        targetList = new JList<>(currencyNames);
        targetList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollPane = new JScrollPane(targetList);
        scrollPane.setBounds(40, 180, 200, 160);
        add(scrollPane);

        JButton compareButton = new JButton("Compare");
        compareButton.setBounds(270, 180, 150, 40);
        add(compareButton);

        JButton backButton = new JButton("Back");
        backButton.setBounds(270, 230, 150, 40);
        add(backButton);

        errorLabel = new JLabel("");
        errorLabel.setBounds(40, 350, 440, 25);
        errorLabel.setForeground(Color.RED);
        add(errorLabel);

        chartPanel = new ComparisonChartPanel();
        chartPanel.setBounds(260, 130, 220, 210);
        add(chartPanel);

        compareButton.addActionListener(e -> onCompare());
        backButton.addActionListener(e -> parent.showScreen(MainFrame.SCREEN_CONVERTER));
    }

    private void onCompare() {
        String amountStr = amountField.getText();
        String baseName = (String) baseBox.getSelectedItem();
        List<String> selectedTargets = targetList.getSelectedValuesList();
        if (selectedTargets.size() == 0) {
            errorLabel.setText("Please select at least one target currency.");
            return;
        }
        if (selectedTargets.size() > 5) {
            errorLabel.setText("Please select at most 5 target currencies.");
            return;
        }
        errorLabel.setText("");
        controller.execute(amountStr, baseName, new ArrayList<>(selectedTargets));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        ConvertMultipleState state = viewModel.getState();
        if (state.getError() != null) {
            errorLabel.setText(state.getError());
            chartPanel.setData(new ArrayList<>());
        } else {
            errorLabel.setText("");
            chartPanel.setData(state.getConversions());
        }
        chartPanel.repaint();
    }

    private static class ComparisonChartPanel extends JPanel {

        private java.util.List<ConvertMultipleOutputData.ConversionResult> data = new ArrayList<>();

        public void setData(java.util.List<ConvertMultipleOutputData.ConversionResult> data) {
            this.data = data;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data == null || data.isEmpty()) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g;
            int width = getWidth();
            int height = getHeight();
            double max = 0.0;
            for (ConvertMultipleOutputData.ConversionResult r : data) {
                if (r.getConvertedAmount() > max) {
                    max = r.getConvertedAmount();
                }
            }
            if (max <= 0) {
                return;
            }
            int n = data.size();
            int barWidth = Math.max(10, width / (n * 2));
            int x = (width - n * barWidth - (n - 1) * barWidth) / 2;
            int baseline = height - 30;
            g2.drawLine(20, baseline, width - 10, baseline);
            for (int i = 0; i < n; i++) {
                ConvertMultipleOutputData.ConversionResult r = data.get(i);
                double ratio = r.getConvertedAmount() / max;
                int barHeight = (int) (ratio * (height - 60));
                int y = baseline - barHeight;
                g2.setColor(new Color(100, 150, 240));
                g2.fillRect(x, y, barWidth, barHeight);
                g2.setColor(Color.BLACK);
                String label = r.getTargetCurrencySymbol();
                if (label == null || label.isEmpty()) {
                    label = r.getTargetCurrencyName();
                }
                FontMetrics fm = g2.getFontMetrics();
                int lw = fm.stringWidth(label);
                g2.drawString(label, x + (barWidth - lw) / 2, baseline + 15);
                x += barWidth * 2;
            }
        }
    }
}
