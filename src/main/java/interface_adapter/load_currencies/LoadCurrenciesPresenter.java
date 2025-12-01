package interface_adapter.load_currencies;

import interface_adapter.convert_currency.ConvertState;
import interface_adapter.convert_currency.ConvertViewModel;
import interface_adapter.historic_trends.TrendsState;
import interface_adapter.historic_trends.TrendsViewModel;
import use_case.load_currencies.LoadCurrenciesOutputBoundary;
import use_case.load_currencies.LoadCurrenciesOutputData;
import java.util.List;

/**
 * Presenter transforms the Interactor's output list into a String array
 * and pushes it into the ConvertViewModel state.
 */
public class LoadCurrenciesPresenter implements LoadCurrenciesOutputBoundary {

    private final ConvertViewModel convertViewModel;
    private final LoadCurrenciesViewModel statusViewModel;
    private final TrendsViewModel trendsViewModel;


    public LoadCurrenciesPresenter(ConvertViewModel convertViewModel,
                                   TrendsViewModel trendsViewModel, // Inject it
                                   LoadCurrenciesViewModel statusViewModel) {
        this.convertViewModel = convertViewModel;
        this.trendsViewModel = trendsViewModel;
        this.statusViewModel = statusViewModel;
    }
    @Override
    public void presentSuccessView(LoadCurrenciesOutputData outputData) {

        List<String> currencyNames = outputData.getCurrencyCodes();
        String[] codesArray = currencyNames.toArray(new String[0]);

        ConvertState convertState = convertViewModel.getState();
        convertState.setCurrencyCodes(codesArray);
        convertViewModel.setState(convertState);
        convertViewModel.firePropertyChange("currencyListLoaded");

        TrendsState trendsState = trendsViewModel.getState();
        trendsState.setCurrencyCodes(codesArray);
        trendsViewModel.setState(trendsState);
        trendsViewModel.firePropertyChange("currencyListLoaded");

        LoadCurrenciesState statusState = statusViewModel.getState();
        statusState.setLoading(false);
        statusState.setLoadError(null);

        convertViewModel.setState(convertState); // Triggers ConvertView to populate boxes
        statusViewModel.setState(statusState); // Notifies status components
    }

    @Override
    public void prepareFailView(String errorMessage) {
        LoadCurrenciesState statusState = statusViewModel.getState();
        statusState.setLoading(false);
        statusState.setLoadError(errorMessage);
        statusViewModel.setState(statusState);

        ConvertState convertState = convertViewModel.getState();
        convertState.setCurrencyCodes(new String[]{});
        convertState.setError("Failed to load currencies.");
        convertViewModel.setState(convertState);
    }
}