package interface_adapter.historic_trends;

import use_case.historic_trends.TrendsInputBoundary;
import use_case.historic_trends.TrendsInputData;

import java.util.List;

public class TrendsController {

    private final TrendsInputBoundary interactor;

    public TrendsController(TrendsInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(String baseCurrency, List<String> targetCurrencies, String timePeriod) {
        TrendsInputData inputData = new TrendsInputData(baseCurrency, targetCurrencies, timePeriod);
        interactor.execute(inputData);
    }

    public void switchToHome() {
        interactor.switchToHomeView();
    }
}
