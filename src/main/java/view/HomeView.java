package view;

import interface_adapter.logged_in.HomeState;
import interface_adapter.logged_in.HomeViewModel;
import interface_adapter.logged_in.HomeController; // You'll need to create this controller
import interface_adapter.logout.LogoutController;
import interface_adapter.logged_in.ConvertController;
import interface_adapter.logged_in.ChangePasswordController;
// import interface_adapter.historical.HistoricalController; // And this one

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class HomeView extends JPanel implements ActionListener, PropertyChangeListener {

    public final String viewName = "home";
    private final HomeViewModel homeViewModel;

    // Controllers
    private ConvertController convertController;
    private LogoutController logoutController;
    private ChangePasswordController changePasswordController = null;
    // private HistoricalController historicalController;

    // UI Components
    private final JComboBox<String> fromBox;
    private final JComboBox<String> toBox;
    private final JTextField amountField; // ADDED: User needs to type amount
    private final JButton convertBtn;
    private final JButton historicalBtn;

    public HomeView(HomeViewModel homeViewModel) {
        this.homeViewModel = homeViewModel;
        this.homeViewModel.addPropertyChangeListener(this);

        setLayout(null); // Keeping your layout choice

        JLabel title = new JLabel("Currencty Converter");
        title.setBounds(200, 20, 200, 25);
        add(title);

        // --- AMOUNT INPUT (New) ---
        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setBounds(40, 110, 60, 30);
        add(amountLabel);

        amountField = new JTextField();
        amountField.setBounds(90, 110, 120, 30);
        add(amountField);
        // --------------------------

        JLabel fromLabel = new JLabel("From:");
        fromLabel.setBounds(40, 70, 50, 30);
        add(fromLabel);

        String[] currencies = {"CAD", "USD", "EUR", "JPY"}; // Ideally this comes from ViewModel
        fromBox = new JComboBox<>(currencies);
        fromBox.setBounds(90, 70, 120, 30);
        add(fromBox);

        JButton favFrom = new JButton("❤️");
        favFrom.setBounds(215, 70, 50, 30);
        add(favFrom);

        JLabel toLabel = new JLabel("To:");
        toLabel.setBounds(280, 70, 30, 30);
        add(toLabel);

        toBox = new JComboBox<>(currencies);
        toBox.setBounds(310, 70, 120, 30);
        add(toBox);

        JButton favTo = new JButton("❤️");
        favTo.setBounds(435, 70, 50, 30);
        add(favTo);

        historicalBtn = new JButton("Historical Trends");
        historicalBtn.setBounds(130, 200, 130, 40);
        add(historicalBtn);

        convertBtn = new JButton("Convert");
        convertBtn.setBounds(280, 200, 130, 40);
        add(convertBtn);

        // --- LISTENERS ---

        // 1. Home Button Click
        convertBtn.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        if (evt.getSource().equals(convertBtn)) {
                            HomeState currentState = homeViewModel.getState();
                            convertController.execute(
                                    currentState.getAmount(),
                                    currentState.getFromCurrency(),
                                    currentState.getToCurrency()
                            );
                        }
                    }
                }
        );

        // 2. Dropdown Listeners (Update State immediately)
        fromBox.addActionListener(e -> {
            HomeState currentState = homeViewModel.getState();
            currentState.setFromCurrency((String) fromBox.getSelectedItem());
            homeViewModel.setState(currentState);
        });

        toBox.addActionListener(e -> {
            HomeState currentState = homeViewModel.getState();
            currentState.setToCurrency((String) toBox.getSelectedItem());
            homeViewModel.setState(currentState);
        });

        // 3. Historical Button
        historicalBtn.addActionListener(e -> {
            // historicalController.execute(); // This will trigger the screen switch via Presenter
            System.out.println("Switch to History View requested");
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Basic click feedback
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        HomeState state = (HomeState) evt.getNewValue();
        // Update the view if the state changes externally
        // e.g. if error, show popup
        if (state.getError() != null) {
            JOptionPane.showMessageDialog(this, state.getError());
        }
    }

    // Setters for Controllers (Injected by AppBuilder)
    public void setConvertController(ConvertController controller) {
        this.convertController = controller;
    }
    public void setLogoutController(LogoutController logoutController) {
        this.logoutController = logoutController;
    }
    public void setChangePasswordController(ChangePasswordController changePasswordController) {
        this.changePasswordController = changePasswordController;
    }

    public String getViewName() {
        return viewName;
    }

}