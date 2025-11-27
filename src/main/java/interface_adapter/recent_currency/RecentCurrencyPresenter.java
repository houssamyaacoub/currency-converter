package interface_adapter.recent_currency;

import use_case.recent_currency.RecentCurrencyOutputBoundary;
import use_case.recent_currency.RecentCurrencyOutputData;

/**
 * Presenter for the Recent / Frequently Used Currencies use case (Use Case 8).
 *
 * Converts the output data into the RecentCurrencyState
 * and triggers the ViewModel to notify UI listeners.
 */
public class RecentCurrencyPresenter implements RecentCurrencyOutputBoundary {

    private final RecentCurrencyViewModel viewModel;

    public RecentCurrencyPresenter(RecentCurrencyViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(RecentCurrencyOutputData outputData) {
        RecentCurrencyState state = viewModel.getState();

        state.setFavouriteCurrencies(outputData.getFavouriteCurrencies());
        state.setTopFrequentCurrencies(outputData.getTopFrequentCurrencies());
        state.setOrderedCurrencyList(outputData.getOrderedCurrencyList());
        state.setErrorMessage("");

        viewModel.setState(state);
        viewModel.firePropertyChange();   // ✔ correct method
    }

    @Override
    public void prepareFailView(String errorMessage) {
        RecentCurrencyState state = viewModel.getState();

        state.setErrorMessage(errorMessage);

        viewModel.setState(state);
        viewModel.firePropertyChange();   // ✔ correct method
    }
}
