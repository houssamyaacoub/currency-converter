package interface_adapter.historic_trends;

import use_case.historic_trends.TrendsInputBoundary;
import use_case.historic_trends.TrendsInputData;

public class TrendsController {

    final TrendsInputBoundary trendsUseCaseInteractor;

    public TrendsController(TrendsInputBoundary trendsUseCaseInteractor) {
        this.trendsUseCaseInteractor = trendsUseCaseInteractor;
    }

    public void execute(String baseCurrency, String targetCurrency, String timePeriod) {
        // 1. Wrap the strings into an InputData object
        TrendsInputData inputData = new TrendsInputData(baseCurrency, targetCurrency, timePeriod);

        // 2. Execute the Use Case
        trendsUseCaseInteractor.execute(inputData);
    }

    public void executeInitialLoad() {
        trendsUseCaseInteractor.executeInitialLoad();
    }


    // Back Button
    public void switchToHome() {
        trendsUseCaseInteractor.switchToHomeView();
    }
}