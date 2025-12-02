package interface_adapter.recent_currency;

import use_case.recent_currency.RecentCurrencyOutputBoundary;
import use_case.recent_currency.RecentCurrencyOutputData;

/**
 * Presenter for the Recent / Frequently Used Currencies use case (Use Case 8).
 * Converts the output data into the RecentCurrencyState
 * and triggers the ViewModel to notify UI listeners.
 */
public class RecentCurrencyPresenter implements RecentCurrencyOutputBoundary {

    private final RecentCurrencyViewModel viewModel;

    /**
     * Creates a new RecentCurrencyPresenter.
     *
     * @param viewModel the ViewModel that will be updated when the use case
     *                  succeeds or fails.
     */

    public RecentCurrencyPresenter(RecentCurrencyViewModel viewModel) {
        this.viewModel = viewModel;
    }
    /**
     * Populates the state with the lists returned by the interactor and clears
     * any previous error message. Then fires a property change so that the UI
     * can refresh its dropdowns or other components.
     *
     * @param outputData the output data produced by the recent currency use case.
     */

    @Override
    public void prepareSuccessView(RecentCurrencyOutputData outputData) {
        RecentCurrencyState state = viewModel.getState();

        state.setFavouriteCurrencies(outputData.getFavouriteCurrencies());
        state.setTopFrequentCurrencies(outputData.getTopFrequentCurrencies());
        state.setOrderedCurrencyList(outputData.getOrderedCurrencyList());
        state.setErrorMessage("");

        viewModel.setState(state);
        // ✔ correct method
        viewModel.firePropertyChange();
    }

    /**
     * Updates the state with an error message when the use case fails and
     * notifies the UI that the state has changed.
     *
     * @param errorMessage a human-readable error message that can be shown
     *                     in the UI.
     */

    @Override
    public void prepareFailView(String errorMessage) {
        RecentCurrencyState state = viewModel.getState();

        state.setErrorMessage(errorMessage);

        viewModel.setState(state);
        // ✔ correct method
        viewModel.firePropertyChange();
    }
}
