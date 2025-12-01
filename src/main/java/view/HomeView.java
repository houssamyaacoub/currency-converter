package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.convert_currency.ConvertController;
import interface_adapter.historic_trends.TrendsController;
import interface_adapter.logged_in.ChangePasswordController;
import interface_adapter.logged_in.HomeState;
import interface_adapter.logged_in.HomeViewModel;
import interface_adapter.logout.LogoutController;
import interface_adapter.travel_budget.TravelBudgetController;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The unified view for the authenticated hub.
 */
public class HomeView extends JPanel implements ActionListener, PropertyChangeListener {

    public final String viewName = "home";

    // --- UI Constants (SCALED UP FOR LARGER WINDOW) ---
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 32); // Was 24
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 20); // Was 16
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14); // Was 12
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 18); // New constant

    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color PRIMARY_BTN_COLOR = new Color(59, 130, 246);
    private static final Color SECONDARY_BTN_COLOR = new Color(107, 114, 128);
    private static final Color DANGER_BTN_COLOR = new Color(220, 38, 38);
    private static final Color TEXT_COLOR = new Color(31, 41, 55);

    // --- Architecture ---
    private final HomeViewModel homeViewModel;
    private final ViewManagerModel viewManagerModel;

    // Controllers
    private TrendsController trendsController;
    private ConvertController convertController;
    private ChangePasswordController changePasswordController;
    private LogoutController logoutController;
    private TravelBudgetController travelBudgetController;


    // --- UI Components ---
    private JLabel usernameDisplay;
    private JLabel passwordErrorField;
    private JTextField passwordInputField;

    private JButton convertBtn;
    private JButton historicalBtn;
    private JButton logOutBtn;
    private JButton changePasswordBtn;
    private JButton travelBudgetBtn;

    public HomeView(HomeViewModel homeViewModel, ViewManagerModel viewManagerModel) {
        this.homeViewModel = homeViewModel;
        this.viewManagerModel = viewManagerModel;
        this.homeViewModel.addPropertyChangeListener(this);

        initializeUI();
        setupListeners();
    }

    private void initializeUI() {
        setLayout(new GridBagLayout());
        setBackground(BACKGROUND_COLOR);

        // --- The Main Card Panel ---
        JPanel mainCard = new JPanel();
        mainCard.setLayout(new BoxLayout(mainCard, BoxLayout.Y_AXIS));
        mainCard.setBackground(CARD_COLOR);

        // Padding to fill space
        mainCard.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
                new EmptyBorder(50, 60, 50, 60)
        ));

        // 1. Header Section
        JPanel headerPanel = createHeaderPanel();
        mainCard.add(headerPanel);
        mainCard.add(Box.createVerticalStrut(35)); // More spacing
        mainCard.add(createSeparator());
        mainCard.add(Box.createVerticalStrut(35));

        // 2. Navigation Section
        JLabel navLabel = new JLabel("DASHBOARD");
        navLabel.setFont(LABEL_FONT);
        navLabel.setForeground(Color.GRAY);
        navLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainCard.add(navLabel);
        mainCard.add(Box.createVerticalStrut(15));

        JPanel navPanel = createNavigationPanel();
        mainCard.add(navPanel);
        mainCard.add(Box.createVerticalStrut(40)); // More spacing

        // 3. Account Settings Section
        JLabel accountLabel = new JLabel("Change password here:");
        accountLabel.setFont(LABEL_FONT);
        accountLabel.setForeground(Color.GRAY);
        accountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainCard.add(accountLabel);
        mainCard.add(Box.createVerticalStrut(15));

        JPanel accountPanel = createAccountPanel();
        mainCard.add(accountPanel);

        add(mainCard);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_COLOR);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // UPDATED: Matches Window Title
        JLabel title = new JLabel("Currency Converter");
        title.setFont(TITLE_FONT);
        title.setForeground(PRIMARY_BTN_COLOR);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        userPanel.setBackground(CARD_COLOR);
        userPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel welcome = new JLabel("Welcome back, ");
        welcome.setFont(SUBTITLE_FONT);
        welcome.setForeground(TEXT_COLOR);

        usernameDisplay = new JLabel("");
        usernameDisplay.setFont(new Font("Segoe UI", Font.BOLD, 20)); // Bigger username
        usernameDisplay.setForeground(TEXT_COLOR);

        userPanel.add(welcome);
        userPanel.add(usernameDisplay);

        panel.add(title);
        panel.add(Box.createVerticalStrut(10));
        panel.add(userPanel);
        return panel;
    }

    private JPanel createNavigationPanel() {
        // Increased gap between buttons (30px)
        JPanel panel = new JPanel(new GridLayout(1, 3, 30, 0));
        panel.setBackground(CARD_COLOR);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Increased preferred size for buttons
        panel.setMaximumSize(new Dimension(800, 80));

        convertBtn = createStyledButton("Convert Currency", PRIMARY_BTN_COLOR, Color.WHITE);
        historicalBtn = createStyledButton("Historical Trends", PRIMARY_BTN_COLOR, Color.WHITE);
        travelBudgetBtn = createStyledButton("Travel Budget", PRIMARY_BTN_COLOR, Color.WHITE);

        panel.add(convertBtn);
        panel.add(historicalBtn);
        panel.add(travelBudgetBtn);
        return panel;
    }

    private JPanel createAccountPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CARD_COLOR);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0); // More vertical spacing
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Row 1: Password Input
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0;
        passwordInputField = new JTextField(15);
        passwordInputField.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // Bigger input text
        passwordInputField.putClientProperty("JTextField.placeholderText", "Enter new password");
        passwordInputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10))); // Bigger input padding
        panel.add(passwordInputField, gbc);

        // Row 2: Action Buttons
        gbc.gridy = 1;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnPanel.setBackground(CARD_COLOR);

        changePasswordBtn = createStyledButton("Change Password", SECONDARY_BTN_COLOR, Color.WHITE);
        changePasswordBtn.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Slightly smaller than main nav

        logOutBtn = createStyledButton("Log Out", DANGER_BTN_COLOR, Color.WHITE);
        logOutBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btnPanel.add(changePasswordBtn);
        btnPanel.add(Box.createHorizontalStrut(20));
        btnPanel.add(logOutBtn);

        panel.add(btnPanel, gbc);

        // Row 3: Error Message
        gbc.gridy = 2;
        passwordErrorField = new JLabel();
        passwordErrorField.setForeground(DANGER_BTN_COLOR);
        passwordErrorField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(passwordErrorField, gbc);

        return panel;
    }

    private void setupListeners() {
        convertBtn.addActionListener(e -> {
            viewManagerModel.setActiveView("convert");
            viewManagerModel.firePropertyChanged();
        });

        historicalBtn.addActionListener(e -> {
            viewManagerModel.setActiveView("trends");
            viewManagerModel.firePropertyChanged();
        });

        logOutBtn.addActionListener(e -> {
            if (logoutController != null) logoutController.execute();
        });

        travelBudgetBtn.addActionListener(e -> {               // NEW
            viewManagerModel.setActiveView("travel_budget");   // use the viewName of TravelBudgetView
            viewManagerModel.firePropertyChanged();
        });

        changePasswordBtn.addActionListener(e -> {
            if (changePasswordController != null) {
                final HomeState currentState = homeViewModel.getState();
                changePasswordController.execute(
                        currentState.getUsername(),
                        passwordInputField.getText()
                );
            }
        });

        passwordInputField.getDocument().addDocumentListener(new DocumentListener() {
            private void documentListenerHelper() {
                final HomeState currentState = homeViewModel.getState();
                currentState.setPassword(passwordInputField.getText());
                currentState.setPasswordError(null);
                homeViewModel.setState(currentState);
            }
            @Override public void insertUpdate(DocumentEvent e) { documentListenerHelper(); }
            @Override public void removeUpdate(DocumentEvent e) { documentListenerHelper(); }
            @Override public void changedUpdate(DocumentEvent e) { documentListenerHelper(); }
        });
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(BUTTON_FONT);

        btn.setOpaque(true);
        btn.setBorderPainted(false);

        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(15, 30, 15, 30));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JSeparator createSeparator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(Color.LIGHT_GRAY);
        sep.setMaximumSize(new Dimension(Short.MAX_VALUE, 1));
        return sep;
    }

    // --- Architecture Methods ---

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final HomeState state = (HomeState) homeViewModel.getState();
        if (state != null) {
            usernameDisplay.setText(state.getUsername());

            if (state.getPasswordError() != null) {
                passwordErrorField.setText(state.getPasswordError());
            } else {
                passwordErrorField.setText("");
                if ("passwordUpdateSuccess".equals(evt.getPropertyName())) {
                    JOptionPane.showMessageDialog(this, "Password updated successfully!");
                    passwordInputField.setText("");
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {}

    public String getViewName() { return viewName; }

    public void setLogoutController(LogoutController controller) { this.logoutController = controller; }
    public void setChangePasswordController(ChangePasswordController controller) { this.changePasswordController = controller; }
    public void setTrendsController(TrendsController controller) { this.trendsController = controller; }
    public void setConvertController(ConvertController controller) { this.convertController = controller; }
    public void setTravelBudgetController(TravelBudgetController controller) { this.travelBudgetController = controller; }

}