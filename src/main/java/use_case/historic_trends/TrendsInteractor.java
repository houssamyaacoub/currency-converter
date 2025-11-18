package use_case.historic_trends;

import use_case.convert.ConvertDataAccessInterface;


public class TrendsInteractor implements TrendsInputBoundary{

    //private final ConvertDataAccessInterface convertDataAccessObject;
    private final TrendsOutputBoundary trendsPresenter;
    public TrendsInteractor(TrendsOutputBoundary trendsPresenter) {
        //this.convertDataAccessObject = convertDataAccessInterface;
        this.trendsPresenter = trendsPresenter;
    }

    @Override
    public void execute(TrendsInputData trendsInputData) {
        // 1. Get the data
        String base = trendsInputData.getBaseCurrency();
        String target = trendsInputData.getTargetCurrency();

        // 2. (Future Step) This is where we ask the DAO for historical data.

        // 3. Prepare Output
        TrendsOutputData outputData = new TrendsOutputData(base, target, false);

        // 4. Tell Presenter to switch views
        trendsPresenter.prepareSuccessView(outputData);
        System.out.println("Now in Presenter");
    }

    public void switchToHomeView() {
        trendsPresenter.prepareHomeView();
    }
}
