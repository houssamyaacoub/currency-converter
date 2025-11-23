package ui;

import data_access.CurrencyListDAO;
import data_access.ExchangeRateHostDAO;
import entity.Currency;
import interface_adapter.convert_currency.ConvertMultipleController;
import interface_adapter.convert_currency.ConvertMultiplePresenter;
import interface_adapter.convert_currency.ConvertMultipleViewModel;
import use_case.convert_multiple.ConvertMultipleInputBoundary;
import use_case.convert_multiple.ConvertMultipleInteractor;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    public static final String SCREEN_CONVERTER = "converter";
    public static final String SCREEN_HISTORY = "history";
    public static final String SCREEN_RESULT = "result";
    public static final String SCREEN_COMPARE = "compare";

    public MainFrame() {
        super("Currency Tool");

        CurrencyListDAO currencyListDAO = new CurrencyListDAO();
        ExchangeRateHostDAO rateDAO = new ExchangeRateHostDAO(currencyListDAO);

        List<Currency> currencies = currencyListDAO.getAllCurrencies();
        String[] currencyNames = currencies.stream().map(Currency::getName).toArray(String[]::new);

        ConvertMultipleViewModel multipleViewModel = new ConvertMultipleViewModel();
        ConvertMultiplePresenter multiplePresenter = new ConvertMultiplePresenter(multipleViewModel);
        ConvertMultipleInputBoundary multipleInteractor =
                new ConvertMultipleInteractor(rateDAO, multiplePresenter, currencyListDAO);
        ConvertMultipleController multipleController =
                new ConvertMultipleController(multipleInteractor);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(new ConverterScreen(this, currencyNames), SCREEN_CONVERTER);
        mainPanel.add(new HistoricalTrendsScreen(this), SCREEN_HISTORY);
        mainPanel.add(new ResultScreen(this), SCREEN_RESULT);
        mainPanel.add(new CompareScreen(this, currencyNames, multipleViewModel, multipleController), SCREEN_COMPARE);

        add(mainPanel);

        setSize(640, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        showScreen(SCREEN_CONVERTER);
    }

    public void showScreen(String name) {
        cardLayout.show(mainPanel, name);
    }
}
