package app;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        AppBuilder appBuilder = new AppBuilder();
        JFrame application = appBuilder
                .addLoginView()
                .addSignupView()
                .addHomeView()
                .addTrendsView()
                .addConvertView()
                .addCompareView()
                .addSignupUseCase()
                .addLoginUseCase()
                .addLogoutUseCase()
                .addChangePasswordUseCase()
                .addTrendsUseCase()
                .addConvertUseCase()
                .addConvertMultipleUseCase()
                .build();


        application.pack();
        application.setLocationRelativeTo(null);
        application.setVisible(true);
    }
}
