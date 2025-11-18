package interface_adapter.historic_trends;

import use_case.historic_trends.TrendsInputBoundary;
import use_case.historic_trends.TrendsInputData;

public class TrendsController {

    final TrendsInputBoundary trendsUseCaseInteractor;

    public TrendsController(TrendsInputBoundary trendsUseCaseInteractor) {
        this.trendsUseCaseInteractor = trendsUseCaseInteractor;
    }

    public void execute(String baseCurrency, String targetCurrency) {
        // 1. Wrap the strings into an InputData object
        TrendsInputData inputData = new TrendsInputData(baseCurrency, targetCurrency);

        // 2. Execute the Use Case
        trendsUseCaseInteractor.execute(inputData);
    }

    // Back Button
    public void switchToHome() {
        trendsUseCaseInteractor.switchToHomeView();
    }
}