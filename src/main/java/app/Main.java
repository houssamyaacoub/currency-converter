package app;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        AppBuilder appBuilder = new AppBuilder();
        JFrame application = appBuilder
                .addLoginView()
                .addSignupView()
                .addHomeView()
                .addConvertView()
                .addTrendsView()
                .addSignupUseCase()
                .addLoadCurrenciesUseCase()
                .addLoginUseCase()
                .addLogoutUseCase()
                .addChangePasswordUseCase()
                .addTrendsUseCase()
                .addFavouriteCurrencyUseCase()
                .addRecentCurrencyUseCase()
                .addConvertUseCase()
                .build();

        application.pack();
        application.setLocationRelativeTo(null);
        application.setVisible(true);
    }
}
