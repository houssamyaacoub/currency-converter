package interface_adapter.favourite_currency;

import use_case.favourite_currency.FavouriteCurrencyOutputBoundary;
import use_case.favourite_currency.FavouriteCurrencyOutputData;

/**
 * Presenter for the Favourite Currency use case (Use Case 5).
 *
 * Converts the output data into the FavouriteCurrencyState
 * and notifies the ViewModel to update the UI.
 */
public class FavouriteCurrencyPresenter implements FavouriteCurrencyOutputBoundary {

    private final FavouriteCurrencyViewModel viewModel;

    public FavouriteCurrencyPresenter(FavouriteCurrencyViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(FavouriteCurrencyOutputData outputData) {
        FavouriteCurrencyState state = viewModel.getState();

        state.setFavouriteCurrencies(outputData.getFavouriteCurrencies());
        state.setErrorMessage("");

        viewModel.setState(state);
        viewModel.firePropertyChange();   // ← matches teammate's code
    }

    @Override
    public void prepareFailView(String errorMessage) {
        FavouriteCurrencyState state = viewModel.getState();

        state.setErrorMessage(errorMessage);

        viewModel.setState(state);
        viewModel.firePropertyChange();   // ← matches teammate's code
    }
}
