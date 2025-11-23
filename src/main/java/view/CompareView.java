package view;

import interface_adapter.ViewManagerModel;
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

public class CompareView extends JPanel implements PropertyChangeListener {

    public final String viewName = "compare";

    private final ViewManagerModel viewManagerModel;
    private final ConvertMultipleViewModel viewModel;

    private ConvertMultipleController controller;

    private final JComboBox<String> baseBox;
    private final JList<String> targetList;
    private final JTextField amountField;
    private final JLabel errorLabel;
    private final ComparisonChartPanel chartPanel;
    private final JButton backBtn;
    private final JButton compareBtn;

    public CompareView(ViewManagerModel viewManagerModel, ConvertMultipleViewModel viewModel) {
        this.viewManagerModel = viewManagerModel;
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        String[] currencies = {"Turkish Lira", "Lebanese Pound", "United States Dollar"};

        JLabel title = new JLabel("Compare Multiple Currencies");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        add(new JLabel("Base:"), gbc);
        gbc.gridx = 1;
        baseBox = new JComboBox<>(currencies);
        add(baseBox, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1;
        amountField = new JTextField("1.00", 10);
        add(amountField, gbc);

        gbc.gridy = 3; gbc.gridx = 0;
        add(new JLabel("Targets (max 5):"), gbc);
        gbc.gridx = 1; gbc.gridheight = 3;
        targetList = new JList<>(currencies);
        targetList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollPane = new JScrollPane(targetList);
        scrollPane.setPreferredSize(new Dimension(160, 120));
        add(scrollPane, gbc);
        gbc.gridheight = 1;

        gbc.gridx = 2; gbc.gridy = 1; gbc.gridheight = 4;
        chartPanel = new ComparisonChartPanel();
        chartPanel.setPreferredSize(new Dimension(260, 180));
        add(chartPanel, gbc);
        gbc.gridheight = 1;

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 1;
        compareBtn = new JButton("Compare");
        add(compareBtn, gbc);

        gbc.gridx = 1;
        backBtn = new JButton("Back");
        add(backBtn, gbc);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 3;
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(errorLabel, gbc);

        compareBtn.addActionListener(e -> onCompare());
        backBtn.addActionListener(e -> {
            viewManagerModel.setActiveView("convert");
            try {
                viewManagerModel.firePropertyChange();
            } catch (Exception ignored) {}
            try {
                viewManagerModel.firePropertyChanged();
            } catch (Exception ignored) {}
        });
    }

    private void onCompare() {
        String amountStr = amountField.getText();
        String baseName = (String) baseBox.getSelectedItem();
        List<String> targets = targetList.getSelectedValuesList();
        if (targets.size() == 0) {
            errorLabel.setText("Please select at least one target currency.");
            return;
        }
        if (targets.size() > 5) {
            errorLabel.setText("Please select at most 5 target currencies.");
            return;
        }
        errorLabel.setText("");
        if (controller != null) {
            controller.execute(amountStr, baseName, new ArrayList<>(targets));
        }
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

    public String getViewName() {
        return viewName;
    }

    public void setConvertMultipleController(ConvertMultipleController controller) {
        this.controller = controller;
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
