package interface_adapter.favourite_currency;

import use_case.favourite_currency.FavouriteCurrencyOutputBoundary;
import use_case.favourite_currency.FavouriteCurrencyOutputData;

/**
 * Presenter for the Favourite Currency use case (Use Case 5).
 * Converts the output data into the FavouriteCurrencyState
 * and notifies the ViewModel to update the UI.
 */
public class FavouriteCurrencyPresenter implements FavouriteCurrencyOutputBoundary {

    private final FavouriteCurrencyViewModel viewModel;

    /**
     * Creates a new FavouriteCurrencyPresenter.
     *
     * @param viewModel the ViewModel that will be updated when the use case
     *                  succeeds or fails.
     */

    public FavouriteCurrencyPresenter(FavouriteCurrencyViewModel viewModel) {
        this.viewModel = viewModel;
    }

    /**
     * Updates the ViewModel with the new list of favourite currencies and
     * clears any previous error message.
     *
     * @param outputData the output data produced by the use case interactor.
     */

    @Override
    public void prepareSuccessView(FavouriteCurrencyOutputData outputData) {
        FavouriteCurrencyState state = viewModel.getState();

        state.setFavouriteCurrencies(outputData.getFavouriteCurrencies());
        state.setErrorMessage("");

        viewModel.setState(state);
        // Notify listeners that the state has changed (matches teammate's presenter code).
        viewModel.firePropertyChange();
    }

    /**
     * Updates the ViewModel to reflect a failure in the use case execution.
     *
     * @param errorMessage a human-readable error message that can be shown
     *                     in the UI.
     */

    @Override
    public void prepareFailView(String errorMessage) {
        FavouriteCurrencyState state = viewModel.getState();

        state.setErrorMessage(errorMessage);

        viewModel.setState(state);
        // Notify listeners that the state has changed (matches teammate's presenter code).
        viewModel.firePropertyChange();
    }
}
