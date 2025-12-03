package interface_adapter.compare_currencies;

import interface_adapter.convert_currency.ConvertState;
import interface_adapter.convert_currency.ConvertViewModel;
import use_case.compare_currencies.CompareCurrenciesOutputBoundary;
import use_case.compare_currencies.CompareCurrenciesOutputData;

/**
 * Presenter for the Compare Currencies use case.
 *
 * <p>The presenter receives formatted output data from the interactor
 * and updates the ViewModel, which triggers the UI refresh.
 *
 * <p>No UI components are touched directly — only the ViewModel changes.
 */
public class CompareCurrenciesPresenter implements CompareCurrenciesOutputBoundary {

    /** The ViewModel that ConvertView listens to. */
    private final ConvertViewModel convertViewModel;

    /**
     * Constructs the presenter.
     *
     * @param convertViewModel the ViewModel used by the ConvertView
     */
    public CompareCurrenciesPresenter(ConvertViewModel convertViewModel) {
        this.convertViewModel = convertViewModel;
    }

    /**
     * Called when comparison succeeds.
     *
     * @param data output data containing base currency, target list, and rate list
     */
    @Override
    public void present(CompareCurrenciesOutputData data) {
        // Grab current state object from the ViewModel
        final ConvertState state = convertViewModel.getState();

        // Make sure the ViewModel's base currency matches what the comparison used
        state.setFromCurrency(data.getBaseCurrencyName());

        // Insert comparison results into the state
        state.setCompareTargets(data.getTargetCurrencyNames());
        state.setCompareRates(data.getRates());

        // Push changes back into the ViewModel + notify UI
        convertViewModel.setState(state);
        convertViewModel.firePropertyChange();
    }

    /**
     * Called when something goes wrong (invalid selection, exception, etc.).
     *
     * @param errorMessage user-friendly message describing the problem
     */
    @Override
    public void prepareFailView(String errorMessage) {
        // Get current state
        final ConvertState state = convertViewModel.getState();

        // Insert the error message
        state.setError(errorMessage);

        // Clear compare data so UI doesn't show stale results
        state.setCompareTargets(java.util.Collections.emptyList());
        state.setCompareRates(java.util.Collections.emptyList());

        // Push updates + notify UI
        convertViewModel.setState(state);
        convertViewModel.firePropertyChange();
    }
}
