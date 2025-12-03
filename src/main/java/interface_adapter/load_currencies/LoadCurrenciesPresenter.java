package interface_adapter.load_currencies;

import interface_adapter.convert_currency.ConvertState;
import interface_adapter.convert_currency.ConvertViewModel;
import use_case.load_currencies.LoadCurrenciesOutputBoundary;
import use_case.load_currencies.LoadCurrenciesOutputData;

import java.util.List;

/**
 * Presenter for the Load Currencies Use Case.
 * This component acts as an Interface Adapter. It receives the list of available currencies
 * from the Interactor, transforms it into a format suitable for the View (String array),
 * and updates the relevant ViewModels.
 */
public class LoadCurrenciesPresenter implements LoadCurrenciesOutputBoundary {

    private final ConvertViewModel convertViewModel;
    private final LoadCurrenciesViewModel statusViewModel;

    /**
     * Constructs a new LoadCurrenciesPresenter.
     *
     * @param convertViewModel The ViewModel for the Conversion screen (receives the currency data).
     * @param statusViewModel  The ViewModel for tracking loading status (receives success/error flags).
     */
    public LoadCurrenciesPresenter(ConvertViewModel convertViewModel,
                                   LoadCurrenciesViewModel statusViewModel) {
        this.convertViewModel = convertViewModel;
        this.statusViewModel = statusViewModel;
    }

    /**
     * Processes the successful retrieval of currency data.
     * Transforms the list of currency codes into a String array, updates the
     * {@link ConvertViewModel} state, and notifies listeners that the list is loaded.
     *
     * @param outputData The output data containing the list of currency codes.
     */
    @Override
    public void presentSuccessView(LoadCurrenciesOutputData outputData) {
        // 1. Transform: Convert List<String> to String[] for Swing JComboBox compatibility
        List<String> currencyNames = outputData.getCurrencyNames();
        String[] codesArray = currencyNames.toArray(new String[0]);

        // 2. Update ConvertViewModel (Display Data)
        ConvertState convertState = convertViewModel.getState();
        convertState.setCurrencyCodes(codesArray);
        convertState.setError(null); // Logic Correction: Clear any previous errors on success

        convertViewModel.setState(convertState);
        convertViewModel.firePropertyChange("currencyListLoaded");

        // 3. Update StatusViewModel (Operation Status)
        LoadCurrenciesState statusState = statusViewModel.getState();
        statusState.setLoading(false);
        statusState.setLoadError(null);

        statusViewModel.setState(statusState);

        // Redundancy Removed: Deleted duplicate call to convertViewModel.setState(convertState)
    }

    /**
     * Processes a failure during the data loading process.
     * Updates the {@link LoadCurrenciesViewModel} with the error message and clears
     * the currency list in the {@link ConvertViewModel} to prevent invalid state.
     *
     * @param errorMessage The explanation of why the load failed.
     */
    @Override
    public void prepareFailView(String errorMessage) {
        // 1. Update StatusViewModel (Signal Failure)
        LoadCurrenciesState statusState = statusViewModel.getState();
        statusState.setLoading(false);
        statusState.setLoadError(errorMessage);
        statusViewModel.setState(statusState);

        // 2. Update ConvertViewModel (Clear Data and Set Error)
        ConvertState convertState = convertViewModel.getState();
        convertState.setCurrencyCodes(new String[0]); // Use empty array instead of null
        convertState.setError("Failed to load currencies.");
        convertViewModel.setState(convertState);
        convertViewModel.firePropertyChange(); // Notify view to display error
    }
}