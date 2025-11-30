package interface_adapter.compare_currencies;

import use_case.compare_currencies.CompareCurrenciesInputBoundary;
import use_case.compare_currencies.CompareCurrenciesInputData;

import java.util.List;

public class CompareCurrenciesController {

    private final CompareCurrenciesInputBoundary useCase;

    public CompareCurrenciesController(CompareCurrenciesInputBoundary useCase) {
        this.useCase = useCase;
    }

    // Simple wrapper: UI passes base + list of targets
    public void execute(String baseCurrencyName, List<String> targetCurrencyNames) {
        CompareCurrenciesInputData inputData =
                new CompareCurrenciesInputData(baseCurrencyName, targetCurrencyNames);
        useCase.execute(inputData);
    }
}
