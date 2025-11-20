
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
                    .addHomeView()
                    .addSignupView()
                    .addLoginView()
                    .addConvertView()
                    .addTrendsView()
                    .addSignupUseCase()
                    .addLoginUseCase()
                    .addLogoutUseCase()
                    .addChangePasswordUseCase()
                    .addTrendsUseCase()
                    .addConvertUseCase()
                    .build(); // Builds and configures the JFrame

            application.pack();
            application.setLocationRelativeTo(null);
            application.setVisible(true);
    }
}