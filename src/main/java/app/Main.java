package app;

import javax.swing.*;

/**
 * Entry point of the Currency Converter application.
 * This class lives in the outermost layer (frameworks & drivers) and is
 * responsible only for bootstrapping the application and showing the main
 * Swing window.
 */

public class Main {

    /**
     * Bootstraps the Swing application by constructing an {@link AppBuilder},
     * wiring all views and use cases, and showing the main window.
     *
     * @param args standard command line arguments (unused)
     */
    public static void main(String[] args) {
        AppBuilder appBuilder = new AppBuilder();
        JFrame application = appBuilder
                .addLoginView()
                .addSignupView()
                .addHomeView()
                .addTrendsView()
                .addConvertView()
                .addTravelBudgetView()
                .addSignupUseCase()
                .addLoginUseCase()
                .addLogoutUseCase()
                .addChangePasswordUseCase()
                .addTrendsUseCase()
                .addFavouriteCurrencyUseCase()
                .addRecentCurrencyUseCase()
                .addTravelBudgetUseCase()
                .addConvertUseCase()
                .addOfflineViewingUseCase()
                .build();


        application.pack();
        application.setLocationRelativeTo(null);
        application.setVisible(true);
    }
}
