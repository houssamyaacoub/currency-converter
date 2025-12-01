package interface_adapter.compare_currencies;

import interface_adapter.convert_currency.ConvertState;
import interface_adapter.convert_currency.ConvertViewModel;
import use_case.compare_currencies.CompareCurrenciesOutputBoundary;
import use_case.compare_currencies.CompareCurrenciesOutputData;

public class CompareCurrenciesPresenter implements CompareCurrenciesOutputBoundary {

    private final ConvertViewModel convertViewModel;

    public CompareCurrenciesPresenter(ConvertViewModel convertViewModel) {
        this.convertViewModel = convertViewModel;
    }

    @Override
    public void present(CompareCurrenciesOutputData data) {
        // Grab current state
        ConvertState state = convertViewModel.getState();

        // Make sure the state's base currency matches the base used in comparison
        state.setFromCurrency(data.getBaseCurrencyName());

        // Fill in compare data
        state.setCompareTargets(data.getTargetCurrencyNames());
        state.setCompareRates(data.getRates());

        // Push state + notify view
        convertViewModel.setState(state);
        convertViewModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        ConvertState state = convertViewModel.getState();
        state.setError(errorMessage);
        // Clear compare data if something goes wrong
        state.setCompareTargets(java.util.Collections.emptyList());
        state.setCompareRates(java.util.Collections.emptyList());
        convertViewModel.setState(state);
        convertViewModel.firePropertyChange();
    }
}
