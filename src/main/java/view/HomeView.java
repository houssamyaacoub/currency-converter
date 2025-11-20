package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.convert_currency.ConvertController;
import interface_adapter.historic_trends.TrendsController;
import interface_adapter.logged_in.ChangePasswordController;
import interface_adapter.logged_in.HomeState;
import interface_adapter.logged_in.HomeViewModel;
import interface_adapter.logout.LogoutController;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The unified view for the authenticated hub. Combines navigation (Convert/Trends)
 * and user management (Logout/Change Password) into a single GridBagLayout panel.
 */
public class HomeView extends JPanel implements ActionListener, PropertyChangeListener {

    public final String viewName = "home";
    private final HomeViewModel homeViewModel;
    private final ViewManagerModel viewManagerModel;

    // Controllers (Injected via setters)
    private TrendsController trendsController;
    private ConvertController convertController;
    private ChangePasswordController changePasswordController;
    private LogoutController logoutController;

    // UI Components
    private final JLabel welcomeLabel;
    private final JLabel usernameDisplay;
    private final JLabel passwordErrorField;
    private final JButton convertBtn;
    private final JButton historicalBtn;
    private final JButton logOutBtn;
    private final JButton changePasswordBtn;
    private final JTextField passwordInputField;

    // Assumed helper class (must be defined elsewhere or nested)
    private static class LabelTextPanel extends JPanel {
        LabelTextPanel(JLabel label, JTextField textField) {
            this.add(label);
            this.add(textField);
        }
    }

    public HomeView(HomeViewModel homeViewModel, ViewManagerModel viewManagerModel) {
        this.homeViewModel = homeViewModel;
        this.viewManagerModel = viewManagerModel;
        this.homeViewModel.addPropertyChangeListener(this);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        // Initialize components
        usernameDisplay = new JLabel();
        passwordInputField = new JTextField(15);
        passwordErrorField = new JLabel();
        passwordErrorField.setForeground(Color.RED);
        convertBtn = new JButton("Convert Currency");
        historicalBtn = new JButton("Historical Trends");
        logOutBtn = new JButton("Log Out");
        changePasswordBtn = new JButton("Change Password");

        // --- 1. Title and Welcome ---
        JLabel currencyTitle = new JLabel("Currency Converter Hub");
        currencyTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(currencyTitle, gbc);

        welcomeLabel = new JLabel("Welcome, ");
        welcomeLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));

        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        userInfoPanel.add(welcomeLabel);
        userInfoPanel.add(usernameDisplay);

        gbc.gridy = 1; add(userInfoPanel, gbc); // Display "Welcome, [Username]"

        // --- 2. Main Navigation Buttons ---

        gbc.gridy = 2; gbc.gridwidth = 1; gbc.gridx = 0;
        add(convertBtn, gbc);
        gbc.gridx = 1;
        add(historicalBtn, gbc);

        // --- 3. Change Password Section ---

        JPanel passwordPanel = new LabelTextPanel(new JLabel("New Password:"), passwordInputField);

        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        add(passwordPanel, gbc);

        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 2;
        add(passwordErrorField, gbc);
        // --- 4. User Actions Buttons ---

        JPanel userActionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        userActionsPanel.add(changePasswordBtn);
        userActionsPanel.add(logOutBtn);

        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        add(userActionsPanel, gbc);


        // A. Simple Navigation (Convert Button)
        convertBtn.addActionListener(e -> {
            viewManagerModel.setActiveView("convert");
            viewManagerModel.firePropertyChanged();
        });



        // B. Historical Trends (Triggers Controller/Use Case)
        historicalBtn.addActionListener(e -> {
            viewManagerModel.setActiveView("trends");
            viewManagerModel.firePropertyChanged();
        });

        // C. Logout Trigger
        logOutBtn.addActionListener(e -> {
            if (logoutController != null) {
                logoutController.execute();
            }
        });

        // D. Change Password Trigger
        changePasswordBtn.addActionListener(e -> {
            if (changePasswordController != null) {
                final HomeState currentState = homeViewModel.getState();

                // Execute Change Password Use Case
                changePasswordController.execute(
                        currentState.getUsername(),
                        passwordInputField.getText()
                );
            }
        });

        // E. Password Input Listener (Updates ViewModel State)
        passwordInputField.getDocument().addDocumentListener(new DocumentListener() {
            private void documentListenerHelper() {
                final HomeState currentState = homeViewModel.getState();
                currentState.setPassword(passwordInputField.getText());
                // Clear any previous error message on new input
                currentState.setPasswordError(null);
                homeViewModel.setState(currentState);
            }
            @Override public void insertUpdate(DocumentEvent e) { documentListenerHelper(); }
            @Override public void removeUpdate(DocumentEvent e) { documentListenerHelper(); }
            @Override public void changedUpdate(DocumentEvent e) { documentListenerHelper(); }
        });
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final HomeState state = (HomeState) homeViewModel.getState();

        if (state != null) {
            // 1. Update Username Display
            usernameDisplay.setText(state.getUsername());

            // 2. Handle Password Change Error/Success
            if (state.getPasswordError() != null) {
                passwordErrorField.setText("Error: " + state.getPasswordError());
            } else {
                passwordErrorField.setText("");
                // Optional: If password was successfully changed, clear the input field
                if (evt.getPropertyName().equals("passwordUpdateSuccess")) {
                    JOptionPane.showMessageDialog(this, "Password updated successfully!");
                    passwordInputField.setText("");
                }
            }

            // 3. Handle general errors (e.g., from Logout or Trends initial load)
            if (state.getPasswordError() != null) {
                JOptionPane.showMessageDialog(this, state.getPasswordError());
            }
        }
    }

    public String getViewName() {
        return viewName;
    }

    // --- SETTERS REQUIRED FOR APPBUILDER WIRING ---
    public void setLogoutController(LogoutController controller) {
        this.logoutController = controller;
    }

    public void setChangePasswordController(ChangePasswordController controller) {
        this.changePasswordController = controller;
    }

    public void setTrendsController(TrendsController controller) {
        this.trendsController = controller;
    }

    public void setConvertController(ConvertController controller) {
        this.convertController = controller;
    }
}