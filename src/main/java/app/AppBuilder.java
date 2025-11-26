package app;

import data_access.CurrencyListDAO;
import data_access.ExchangeRateHostDAO;
import data_access.FileUserDataAccessObject;
import entity.UserFactory;
import interface_adapter.ViewManagerModel;
import interface_adapter.convert_currency.ConvertController;
import interface_adapter.convert_currency.ConvertPresenter;
import interface_adapter.convert_currency.ConvertViewModel;
import interface_adapter.historic_trends.TrendsController;
import interface_adapter.historic_trends.TrendsPresenter;
import interface_adapter.historic_trends.TrendsViewModel;
import interface_adapter.logged_in.ChangePasswordController;
import interface_adapter.logged_in.ChangePasswordPresenter;
import interface_adapter.logged_in.HomeViewModel;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginPresenter;
import interface_adapter.login.LoginViewModel;
import interface_adapter.logout.LogoutController;
import interface_adapter.logout.LogoutPresenter;
import interface_adapter.signup.SignupController;
import interface_adapter.signup.SignupPresenter;
import interface_adapter.signup.SignupViewModel;
import use_case.convert.ExchangeRateDataAccessInterface;
import use_case.convert.CurrencyRepository;
import use_case.change_password.ChangePasswordInputBoundary;
import use_case.change_password.ChangePasswordInteractor;
import use_case.change_password.ChangePasswordOutputBoundary;
import use_case.convert.*;
import use_case.historic_trends.TrendsInputBoundary;
import use_case.historic_trends.TrendsInteractor;
import use_case.historic_trends.TrendsOutputBoundary;
import use_case.login.LoginInputBoundary;
import use_case.login.LoginInteractor;
import use_case.login.LoginOutputBoundary;
import use_case.logout.LogoutInputBoundary;
import use_case.logout.LogoutInteractor;
import use_case.logout.LogoutOutputBoundary;
import use_case.signup.SignupInputBoundary;
import use_case.signup.SignupInteractor;
import use_case.signup.SignupOutputBoundary;
import view.*;

import javax.swing.*;
import java.awt.*;

public class AppBuilder {
    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    final UserFactory userFactory = new UserFactory();
    final ViewManagerModel viewManagerModel = new ViewManagerModel();
    final ViewManager viewManager;
    // set which data access implementation to use, can be any
    // of the classes from the data_access package

    private final CurrencyListDAO currencyRepository = new CurrencyListDAO();
    final FileUserDataAccessObject userDataAccessObject =
            new FileUserDataAccessObject("users.csv", userFactory, currencyRepository);
    // DAO version using a shared external database

    private SignupView signupView;
    private SignupViewModel signupViewModel;
    private LoginViewModel loginViewModel;
    private HomeViewModel homeViewModel;
    private TrendsViewModel trendsViewModel;
    private ConvertViewModel convertViewModel;
    private HomeView homeView;
    private LoginView loginView;
    private TrendsView trendsView;
    private ConvertView convertView;
    private final ExchangeRateDataAccessInterface dataAccess = new ExchangeRateHostDAO(currencyRepository);
    // Shared DAO for favourites so all use cases see the same in-memory data.
    //private data_access.FavouriteCurrencyFileDataAccessObject favouriteDAO;




    public AppBuilder() {
        cardPanel.setLayout(cardLayout);
        this.viewManager = new ViewManager(cardPanel, cardLayout, viewManagerModel);
    }

    public AppBuilder addSignupView() {
        signupViewModel = new SignupViewModel();
        signupView = new SignupView(signupViewModel);
        cardPanel.add(signupView, signupView.getViewName());
        return this;
    }

    public AppBuilder addLoginView() {
        loginViewModel = new LoginViewModel();
        loginView = new LoginView(loginViewModel);
        cardPanel.add(loginView, loginView.getViewName());
        return this;
    }

    public AppBuilder addHomeView() {
        homeViewModel = new HomeViewModel();
        homeView = new HomeView(homeViewModel, viewManagerModel);
        cardPanel.add(homeView, homeView.getViewName());
        return this;
    }

    public AppBuilder addTrendsView() {
        trendsViewModel = new TrendsViewModel();
        trendsView = new TrendsView(trendsViewModel);
        cardPanel.add(trendsView, trendsView.getViewName());
        return this;
    }

    public AppBuilder addConvertView() {
        convertViewModel = new ConvertViewModel();

        java.util.List<String> baseCurrencies = new java.util.ArrayList<>();
        for (entity.Currency c : ((CurrencyListDAO) currencyRepository).getAllCurrencies()) {
            baseCurrencies.add(c.getName());
        }


        convertView = new ConvertView(viewManagerModel, convertViewModel, baseCurrencies, homeViewModel);


        cardPanel.add(convertView, convertView.getViewName());
        return this;
    }


    public AppBuilder addSignupUseCase() {
        final SignupOutputBoundary signupOutputBoundary = new SignupPresenter(viewManagerModel,
                signupViewModel, loginViewModel);
        final SignupInputBoundary userSignupInteractor = new SignupInteractor(
                userDataAccessObject, signupOutputBoundary, userFactory);

        SignupController controller = new SignupController(userSignupInteractor);
        signupView.setSignupController(controller);
        return this;
    }

    public AppBuilder addLoginUseCase() {
        final LoginOutputBoundary loginOutputBoundary = new LoginPresenter(viewManagerModel,
                homeViewModel, loginViewModel);
        final LoginInputBoundary loginInteractor = new LoginInteractor(
                userDataAccessObject, loginOutputBoundary);

        LoginController loginController = new LoginController(loginInteractor);
        loginView.setLoginController(loginController);
        return this;
    }

    public AppBuilder addChangePasswordUseCase() {
        final ChangePasswordOutputBoundary changePasswordOutputBoundary = new ChangePasswordPresenter(viewManagerModel,
                homeViewModel);

        final ChangePasswordInputBoundary changePasswordInteractor =
                new ChangePasswordInteractor(userDataAccessObject, changePasswordOutputBoundary, userFactory);

        ChangePasswordController changePasswordController = new ChangePasswordController(changePasswordInteractor);
        homeView.setChangePasswordController(changePasswordController);
        return this;
    }

    /**
     * Adds the Logout Use Case to the application.
     * @return this builder
     */
    public AppBuilder addLogoutUseCase() {
        final LogoutOutputBoundary logoutOutputBoundary = new LogoutPresenter(viewManagerModel,
                homeViewModel, loginViewModel);

        final LogoutInputBoundary logoutInteractor =
                new LogoutInteractor(userDataAccessObject, logoutOutputBoundary);

        final LogoutController logoutController = new LogoutController(logoutInteractor);
        homeView.setLogoutController(logoutController);
        return this;
    }

    public AppBuilder addTrendsUseCase() {
        final TrendsOutputBoundary trendsPresenter = new TrendsPresenter(viewManagerModel, trendsViewModel);
        final TrendsInputBoundary trendsInteractor = new TrendsInteractor(trendsPresenter);
        final TrendsController trendsController = new TrendsController(trendsInteractor);
        homeView.setTrendsController(trendsController);
        trendsView.setTrendsController(trendsController);// Should change later (back button)
        return this;
    }


    public AppBuilder addConvertUseCase() {
        final ConvertOutputBoundary convertPresenter = new ConvertPresenter(convertViewModel);
        final ConvertInputBoundary convertInteractor = new ConvertCurrencyInteractor(dataAccess, convertPresenter, currencyRepository);
        final ConvertController convertController = new ConvertController(convertInteractor);
        homeView.setConvertController(convertController);
        convertView.setConvertController(convertController);// Should change later (back button)
        return this;
    }

    public AppBuilder addFavouriteCurrencyUseCase() {

        // ViewModel and Presenter
        final interface_adapter.favourite_currency.FavouriteCurrencyViewModel favouriteVM =
                new interface_adapter.favourite_currency.FavouriteCurrencyViewModel();
        final interface_adapter.favourite_currency.FavouriteCurrencyPresenter favouritePresenter =
                new interface_adapter.favourite_currency.FavouriteCurrencyPresenter(favouriteVM);

        // Use the FileUserDataAccessObject directly as the Favourite DAO.
        final use_case.favourite_currency.FavouriteCurrencyDataAccessInterface favouriteDAO =
                (use_case.favourite_currency.FavouriteCurrencyDataAccessInterface) userDataAccessObject;

        // Interactor
        final use_case.favourite_currency.FavouriteCurrencyInputBoundary favouriteInteractor =
                new use_case.favourite_currency.FavouriteCurrencyInteractor(
                        favouriteDAO,
                        favouritePresenter
                );

        // Controller
        final interface_adapter.favourite_currency.FavouriteCurrencyController favouriteController =
                new interface_adapter.favourite_currency.FavouriteCurrencyController(favouriteInteractor);

        // Inject into Convert view
        convertView.setFavouriteCurrencyController(favouriteController);
        convertView.setFavouriteCurrencyViewModel(favouriteVM);

        return this;
    }



    public AppBuilder addRecentCurrencyUseCase() {

        // Use FileUserDataAccessObject directly as the Recent DAO
        final use_case.recent_currency.RecentCurrencyDataAccessInterface recentDAO =
                (use_case.recent_currency.RecentCurrencyDataAccessInterface) userDataAccessObject;

        // 3. VM + Presenter
        final interface_adapter.recent_currency.RecentCurrencyViewModel recentVM =
                new interface_adapter.recent_currency.RecentCurrencyViewModel();
        final interface_adapter.recent_currency.RecentCurrencyPresenter recentPresenter =
                new interface_adapter.recent_currency.RecentCurrencyPresenter(recentVM);

        // 4. Interactor
        final use_case.recent_currency.RecentCurrencyInputBoundary recentInteractor =
                new use_case.recent_currency.RecentCurrencyInteractor(
                        recentDAO,
                        recentPresenter
                );

        // 5. Controller
        final interface_adapter.recent_currency.RecentCurrencyController recentController =
                new interface_adapter.recent_currency.RecentCurrencyController(recentInteractor);

        // 6. Inject into ConvertView
        convertView.setRecentCurrencyDAO(recentDAO);
        convertView.setRecentCurrencyViewModel(recentVM);
        convertView.setRecentCurrencyController(recentController);

        return this;
    }





    public JFrame build() {
        final JFrame application = new JFrame("Currency Converter");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        application.add(cardPanel);

        viewManagerModel.setState(signupViewModel.getViewName());
        viewManagerModel.firePropertyChange();

        return application;
    }


}
