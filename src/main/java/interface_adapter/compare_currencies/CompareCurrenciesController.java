package interface_adapter.compare_currencies;

import java.util.List;

import use_case.compare_currencies.CompareCurrenciesInputBoundary;
import use_case.compare_currencies.CompareCurrenciesInputData;

/**
 * Controller for the Compare Currencies use case.
 *
 * <p>Its job is to take raw input from the UI, wrap it in an input data object,
 * and pass it to the interactor (via the input boundary).
 * This keeps the UI decoupled from the business logic.
 */
public class CompareCurrenciesController {

    /** The input boundary (the interactor will eventually implement this). */
    private final CompareCurrenciesInputBoundary useCase;

    /**
     * Constructs the controller.
     *
     * @param useCase the input boundary for Compare Currencies
     */
    public CompareCurrenciesController(CompareCurrenciesInputBoundary useCase) {
        this.useCase = useCase;
    }

    /**
     * Called by the UI (ConvertView) when the user wants to compare currencies.
     *
     * <p>It wraps the base currency and selected target list into an InputData
     * object and passes it to the interactor through the input boundary.
     *
     * @param baseCurrencyName    the name/code of the base currency
     * @param targetCurrencyNames list of selected target currencies
     */
    public void execute(String baseCurrencyName, List<String> targetCurrencyNames) {
        // Wrap raw UI input into a single object the interactor understands
        final CompareCurrenciesInputData inputData =
                new CompareCurrenciesInputData(baseCurrencyName, targetCurrencyNames);

        // Pass it into the use case — controller never does business logic
        useCase.execute(inputData);
    }
}
