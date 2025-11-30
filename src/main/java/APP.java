
import app.AppBuilder;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class APP {
    public static void main(String[] args) {

        // Ensure the Swing GUI is started safely on the Event Dispatch Thread (EDT)

            // Instantiate the builder
            AppBuilder builder = new AppBuilder();

            // Execute the builder chain
            JFrame application = builder
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